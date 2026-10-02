package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.AfterSaleMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.pojo.dto.FinanceFlowItem;
import com.rephone.pojo.dto.FinanceSummary;
import com.rephone.pojo.entity.AfterSale;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 财务对账：纯查询聚合，不建账本表——对账单与订单流水天然一致。
 * 口径（金额均为分）：
 * - 回收打款 = recycle_order.status=50 的 final_fen（支出）
 * - 出售收款 = sale_order.status in (20,30,40) 的 total_fen（收入）
 * - 出售退款 = after_sale.status=30 的 refund_fen（退款）
 * - 维修收款 = repair_order.status=50 的 total_fen（收入）
 * - 净额 = 出售收款 + 维修收款 − 出售退款 − 回收打款
 * 流水三表各自按时间范围查询后内存合并倒序分页（早期数据量可接受；量大后改 SQL union）。
 */
@Service
public class FinanceService {

    public static final int BIZ_RECYCLE = 10;
    public static final int BIZ_SALE = 20;
    public static final int BIZ_REPAIR = 30;

    private final RecycleOrderMapper recycleOrderMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final AfterSaleMapper afterSaleMapper;
    private final RepairOrderMapper repairOrderMapper;

    public FinanceService(RecycleOrderMapper recycleOrderMapper, SaleOrderMapper saleOrderMapper,
                          AfterSaleMapper afterSaleMapper, RepairOrderMapper repairOrderMapper) {
        this.recycleOrderMapper = recycleOrderMapper;
        this.saleOrderMapper = saleOrderMapper;
        this.afterSaleMapper = afterSaleMapper;
        this.repairOrderMapper = repairOrderMapper;
    }

    /** 汇总。from/to 为空时默认近 30 天（含今天）。 */
    public FinanceSummary summary(LocalDate from, LocalDate to) {
        Range range = rangeOf(from, to);
        long[] recycle = sumRange(range, "final_fen", List.of(RecycleOrder.STATUS_PAID), "recycle_order");
        long[] sale = sumRange(range, "total_fen",
                List.of(SaleOrder.STATUS_PAID, SaleOrder.STATUS_SHIPPED, SaleOrder.STATUS_DONE), "sale_order");
        long[] refund = sumRange(range, "refund_fen", List.of(AfterSale.STATUS_REFUNDED), "after_sale");
        long[] repair = sumRange(range, "total_fen", List.of(RepairOrder.STATUS_DONE), "repair_order");
        long net = sale[0] + repair[0] - refund[0] - recycle[0];
        return new FinanceSummary(recycle[0], recycle[1], sale[0], sale[1], refund[0], refund[1],
                repair[0], repair[1], net, range.from().toString(), range.to().toString());
    }

    /** 流水（可按 biz 过滤），按创建时间倒序内存分页。 */
    public Page<FinanceFlowItem> flows(LocalDate from, LocalDate to, Integer biz,
                                       long pageNum, long pageSize) {
        Range range = rangeOf(from, to);
        boolean wantRecycle = biz == null || biz == BIZ_RECYCLE;
        boolean wantSale = biz == null || biz == BIZ_SALE;
        boolean wantRepair = biz == null || biz == BIZ_REPAIR;
        if (!wantRecycle && !wantSale && !wantRepair) {
            throw new BizException(40090, "biz 仅支持 10 回收 / 20 出售 / 30 维修");
        }
        List<FinanceFlowItem> items = new ArrayList<>();
        if (wantRecycle) {
            for (RecycleOrder o : recycleOrderMapper.selectList(
                    conditionWrapper(range, List.of(RecycleOrder.STATUS_PAID), RecycleOrder.class, true))) {
                items.add(new FinanceFlowItem(o.getOrderNo(), BIZ_RECYCLE, orZero(o.getFinalFen()),
                        "payout", o.getStatus(), time(o.getCreateTime())));
            }
        }
        if (wantSale) {
            for (SaleOrder o : saleOrderMapper.selectList(conditionWrapper(range,
                    List.of(SaleOrder.STATUS_PAID, SaleOrder.STATUS_SHIPPED, SaleOrder.STATUS_DONE),
                    SaleOrder.class, true))) {
                items.add(new FinanceFlowItem(o.getOrderNo(), BIZ_SALE, orZero(o.getTotalFen()),
                        "income", o.getStatus(), time(o.getCreateTime())));
            }
            for (AfterSale o : afterSaleMapper.selectList(
                    conditionWrapper(range, List.of(AfterSale.STATUS_REFUNDED), AfterSale.class, true))) {
                items.add(new FinanceFlowItem(o.getAsNo(), BIZ_SALE, orZero(o.getRefundFen()),
                        "refund", o.getStatus(), time(o.getCreateTime())));
            }
        }
        if (wantRepair) {
            for (RepairOrder o : repairOrderMapper.selectList(
                    conditionWrapper(range, List.of(RepairOrder.STATUS_DONE), RepairOrder.class, true))) {
                items.add(new FinanceFlowItem(o.getOrderNo(), BIZ_REPAIR, orZero(o.getTotalFen()),
                        "income", o.getStatus(), time(o.getCreateTime())));
            }
        }
        items.sort(Comparator.comparing(FinanceFlowItem::createTime).reversed());

        Page<FinanceFlowItem> page = new Page<>(pageNum, pageSize, items.size());
        int fromIdx = (int) Math.max(0, (pageNum - 1) * pageSize);
        int toIdx = (int) Math.min(items.size(), fromIdx + pageSize);
        page.setRecords(fromIdx >= items.size() ? List.of() : items.subList(fromIdx, toIdx));
        return page;
    }

    // ===== 内部 =====

    private record Range(LocalDate from, LocalDate to) {
    }

    private static Range rangeOf(LocalDate from, LocalDate to) {
        LocalDate end = to == null ? LocalDate.now() : to;
        LocalDate start = from == null ? end.minusDays(29) : from;
        if (start.isAfter(end)) {
            throw new BizException(40091, "开始日期不能晚于结束日期");
        }
        return new Range(start, end);
    }

    /** [SUM(amountColumn), COUNT(*)]：仅统计资金终态与时间范围。聚合查询不能带 ORDER BY 非聚合列（H2 严格拒绝）。 */
    private long[] sumRange(Range range, String amountColumn, List<Integer> statuses, String table) {
        String select = "IFNULL(SUM(" + amountColumn + "),0) AS total, COUNT(*) AS cnt";
        List<Map<String, Object>> rows = switch (table) {
            case "recycle_order" -> recycleOrderMapper.selectMaps(
                    conditionWrapper(range, statuses, RecycleOrder.class, false).select(select));
            case "sale_order" -> saleOrderMapper.selectMaps(
                    conditionWrapper(range, statuses, SaleOrder.class, false).select(select));
            case "after_sale" -> afterSaleMapper.selectMaps(
                    conditionWrapper(range, statuses, AfterSale.class, false).select(select));
            case "repair_order" -> repairOrderMapper.selectMaps(
                    conditionWrapper(range, statuses, RepairOrder.class, false).select(select));
            default -> throw new IllegalArgumentException("未知表 " + table);
        };
        Map<String, Object> row = rows.isEmpty() ? Map.of("total", 0L, "cnt", 0L) : rows.get(0);
        return new long[]{num(row.get("total")), num(row.get("cnt"))};
    }

    /**
     * 状态集合 + create_time 半开区间；实体类型显式传入避免链式调用推断失败。
     * ordered=false 用于聚合查询（ORDER BY 非聚合列在 H2 下直接报错）。
     */
    private static <T> QueryWrapper<T> conditionWrapper(Range range, List<Integer> statuses,
                                                        Class<T> type, boolean ordered) {
        QueryWrapper<T> wrapper = new QueryWrapper<T>(type);
        wrapper.in("status", statuses)
                .ge("create_time", range.from().atStartOfDay())
                .lt("create_time", range.to().plusDays(1).atStartOfDay());
        if (ordered) {
            wrapper.orderByDesc("create_time");
        }
        return wrapper;
    }

    private static long num(Object value) {
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }

    private static long orZero(Long value) {
        return value == null ? 0L : value;
    }

    private static String time(LocalDateTime time) {
        return time == null ? "" : time.toString();
    }
}
