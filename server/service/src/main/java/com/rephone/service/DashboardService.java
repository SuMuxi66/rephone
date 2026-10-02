package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rephone.mapper.AfterSaleMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.pojo.dto.DashboardSummary;
import com.rephone.pojo.dto.FinanceSummary;
import com.rephone.pojo.dto.DashboardTopItem;
import com.rephone.pojo.dto.DashboardTrendItem;
import com.rephone.pojo.entity.AfterSale;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 数据看板：跨租户聚合。单量窗口计数走 COUNT；趋势按天分组在 Java 侧完成
 * （避免 H2/MySQL 日期函数差异；查询窗口限定 30 天，早期量级可接受，量大后改 SQL 聚合）。
 */
@Service
public class DashboardService {

    private final RecycleOrderMapper recycleOrderMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final AfterSaleMapper afterSaleMapper;
    private final FinanceService financeService;

    public DashboardService(RecycleOrderMapper recycleOrderMapper, SaleOrderMapper saleOrderMapper,
                            RepairOrderMapper repairOrderMapper, AfterSaleMapper afterSaleMapper,
                            FinanceService financeService) {
        this.recycleOrderMapper = recycleOrderMapper;
        this.saleOrderMapper = saleOrderMapper;
        this.repairOrderMapper = repairOrderMapper;
        this.afterSaleMapper = afterSaleMapper;
        this.financeService = financeService;
    }

    public DashboardSummary summary() {
        LocalDate today = LocalDate.now();
        long todayOrders = countSince(today.atStartOfDay())
                + countSaleSince(today.atStartOfDay()) + countRepairSince(today.atStartOfDay());
        long yesterdayOrders = countSince(today.minusDays(1).atStartOfDay())
                + countSaleSince(today.minusDays(1).atStartOfDay())
                + countRepairSince(today.minusDays(1).atStartOfDay())
                - countSince(today.atStartOfDay())
                - countSaleSince(today.atStartOfDay())
                - countRepairSince(today.atStartOfDay());
        long last7Orders = countSince(today.minusDays(6).atStartOfDay())
                + countSaleSince(today.minusDays(6).atStartOfDay())
                + countRepairSince(today.minusDays(6).atStartOfDay());
        long last30Orders = countSince(today.minusDays(29).atStartOfDay())
                + countSaleSince(today.minusDays(29).atStartOfDay())
                + countRepairSince(today.minusDays(29).atStartOfDay());

        FinanceSummary finance = financeService.summary(today.minusDays(29), today);

        return new DashboardSummary(todayOrders, Math.max(0, yesterdayOrders), last7Orders, last30Orders,
                recycleOrderMapper.selectCount(null),
                saleOrderMapper.selectCount(null),
                repairOrderMapper.selectCount(null),
                recycleOrderMapper.selectCount(new QueryWrapper<RecycleOrder>().in("status",
                        List.of(RecycleOrder.STATUS_WAIT_SEND, RecycleOrder.STATUS_SHIPPING,
                                RecycleOrder.STATUS_INSPECTING))),
                repairOrderMapper.selectCount(new QueryWrapper<RepairOrder>().in("status",
                        List.of(RepairOrder.STATUS_WAIT_CONFIRM, RepairOrder.STATUS_APPOINTED,
                                RepairOrder.STATUS_REPAIRING, RepairOrder.STATUS_WAIT_ACCEPT))),
                saleOrderMapper.selectCount(new QueryWrapper<SaleOrder>().eq("status", SaleOrder.STATUS_PAID)),
                afterSaleMapper.selectCount(new QueryWrapper<AfterSale>().eq("status",
                        AfterSale.STATUS_WAIT_REVIEW)),
                finance.netFen(), finance.recyclePayFen(), finance.saleIncomeFen(), finance.repairIncomeFen());
    }

    /** 近 days 天（含今天）三线单量趋势。 */
    public List<DashboardTrendItem> trend(int days) {
        int window = Math.min(Math.max(days, 1), 30);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(window - 1L).atStartOfDay();
        Map<String, long[]> byDate = new LinkedHashMap<>();
        for (int i = window - 1; i >= 0; i--) {
            byDate.put(today.minusDays(i).toString(), new long[3]);
        }
        for (RecycleOrder o : recycleOrderMapper.selectList(sinceWrapper(start))) {
            bump(byDate, dateOf(o.getCreateTime()), 0);
        }
        for (SaleOrder o : saleOrderMapper.selectList(sinceWrapper(start))) {
            bump(byDate, dateOf(o.getCreateTime()), 1);
        }
        for (RepairOrder o : repairOrderMapper.selectList(sinceWrapper(start))) {
            bump(byDate, dateOf(o.getCreateTime()), 2);
        }
        List<DashboardTrendItem> items = new ArrayList<>();
        byDate.forEach((date, counts) -> items.add(
                new DashboardTrendItem(date, counts[0], counts[1], counts[2])));
        return items;
    }

    /** 热门回收机型 Top N（按订单数）。 */
    public List<DashboardTopItem> topRecycleModels(int limit) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("model_name AS name", "brand_name AS subName", "COUNT(*) AS cnt")
                .ge("create_time", LocalDate.now().minusDays(29).atStartOfDay())
                .groupBy("brand_name", "model_name")
                .orderByDesc("cnt")
                .last("LIMIT " + Math.min(Math.max(limit, 1), 20));
        List<DashboardTopItem> items = new ArrayList<>();
        for (Map<String, Object> row : recycleOrderMapper.selectMaps(wrapper)) {
            items.add(new DashboardTopItem(String.valueOf(row.get("name")),
                    String.valueOf(row.get("subName")), num(row.get("cnt"))));
        }
        return items;
    }

    /** 热销商品 Top N（按件数）。 */
    public List<DashboardTopItem> topGoods(int limit) {
        QueryWrapper<SaleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("goods_name AS name", "'' AS subName", "SUM(quantity) AS cnt")
                .ge("create_time", LocalDate.now().minusDays(29).atStartOfDay())
                .groupBy("goods_name")
                .orderByDesc("cnt")
                .last("LIMIT " + Math.min(Math.max(limit, 1), 20));
        List<DashboardTopItem> items = new ArrayList<>();
        for (Map<String, Object> row : saleOrderMapper.selectMaps(wrapper)) {
            items.add(new DashboardTopItem(String.valueOf(row.get("name")),
                    String.valueOf(row.get("subName")), num(row.get("cnt"))));
        }
        return items;
    }

    // ===== 内部 =====

    private long countSince(LocalDateTime start) {
        return recycleOrderMapper.selectCount(new QueryWrapper<RecycleOrder>().ge("create_time", start));
    }

    private long countSaleSince(LocalDateTime start) {
        return saleOrderMapper.selectCount(new QueryWrapper<SaleOrder>().ge("create_time", start));
    }

    private long countRepairSince(LocalDateTime start) {
        return repairOrderMapper.selectCount(new QueryWrapper<RepairOrder>().ge("create_time", start));
    }

    private static <T> QueryWrapper<T> sinceWrapper(LocalDateTime start) {
        QueryWrapper<T> wrapper = new QueryWrapper<T>();
        wrapper.ge("create_time", start);
        return wrapper;
    }

    private static String dateOf(LocalDateTime time) {
        return time == null ? "" : time.toLocalDate().toString();
    }

    private static void bump(Map<String, long[]> byDate, String date, int index) {
        long[] counts = byDate.get(date);
        if (counts != null) {
            counts[index]++;
        }
    }

    private static long num(Object value) {
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }
}
