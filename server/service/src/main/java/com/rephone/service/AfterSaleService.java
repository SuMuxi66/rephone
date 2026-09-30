package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.AfterSaleMapper;
import com.rephone.pojo.entity.AfterSale;
import com.rephone.pojo.entity.SaleOrder;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 售后单（仅退款 MVP）：用户申请 → 管理端审核。
 * 同意即 mock 退款到账并联动出售订单置为已退款；拒绝/撤销为终态。
 */
@Service
public class AfterSaleService {

    private static final Map<Integer, Set<Integer>> NEXT = Map.of(
            AfterSale.STATUS_WAIT_REVIEW, Set.of(AfterSale.STATUS_REFUNDED,
                    AfterSale.STATUS_REJECTED, AfterSale.STATUS_CANCELED));

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AfterSaleMapper saleMapper;
    private final SaleOrderService saleOrderService;

    public AfterSaleService(AfterSaleMapper saleMapper, SaleOrderService saleOrderService) {
        this.saleMapper = saleMapper;
        this.saleOrderService = saleOrderService;
    }

    /** 用户申请售后：订单须归属本人且处于已付款/已发货。 */
    @Transactional
    public String apply(String orderNo, String reason) {
        requireLen(reason, 255, "申请原因");
        SaleOrder order = saleOrderService.getOwned(orderNo);
        if (order.getStatus() != SaleOrder.STATUS_PAID && order.getStatus() != SaleOrder.STATUS_SHIPPED) {
            throw new BizException(40071, "当前订单状态不支持售后（仅已付款/已发货）");
        }
        Long dup = saleMapper.selectCount(new LambdaQueryWrapper<AfterSale>()
                .eq(AfterSale::getOrderNo, orderNo)
                .eq(AfterSale::getStatus, AfterSale.STATUS_WAIT_REVIEW));
        if (dup != null && dup > 0) {
            throw new BizException(40072, "该订单已有待审核的售后申请");
        }
        AfterSale afterSale = new AfterSale();
        afterSale.setAsNo(nextAsNo());
        afterSale.setOrderNo(orderNo);
        afterSale.setUserId(UserContextHolder.require().userId());
        afterSale.setType(10);
        afterSale.setReason(reason.trim());
        afterSale.setStatus(AfterSale.STATUS_WAIT_REVIEW);
        saleMapper.insert(afterSale);
        return afterSale.getAsNo();
    }

    @Transactional
    public void cancel(String asNo, String reason) {
        requireLen(reason, 255, "撤销原因");
        AfterSale afterSale = requireOwned(asNo);
        transition(afterSale, AfterSale.STATUS_CANCELED, "用户撤销：" + reason.trim());
    }

    // ===== 管理端 =====

    public Page<AfterSale> adminPage(Integer status, String keyword, long pageNum, long pageSize) {
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<AfterSale>()
                .orderByDesc(AfterSale::getId);
        if (status != null && status > 0) {
            wrapper.eq(AfterSale::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(AfterSale::getAsNo, kw).or().like(AfterSale::getOrderNo, kw));
        }
        return saleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /** 同意售后：mock 退款到账 + 出售订单联动置为已退款。 */
    @Transactional
    public void agree(String asNo, String adminRemark) {
        requireLen(adminRemark, 255, "审核说明");
        AfterSale afterSale = requireOrder(asNo);
        SaleOrder order = saleOrderService.requireOrder(afterSale.getOrderNo());
        transition(afterSale, AfterSale.STATUS_REFUNDED, "同意退款：" + adminRemark.trim());
        afterSale.setRefundFen(order.getTotalFen());
        saleMapper.updateById(afterSale);
        saleOrderService.refund(order.getOrderNo(), order.getTotalFen(),
                "售后" + afterSale.getAsNo() + "：" + afterSale.getReason(), 20, 0L);
    }

    @Transactional
    public void reject(String asNo, String adminRemark) {
        requireLen(adminRemark, 255, "审核说明");
        AfterSale afterSale = requireOrder(asNo);
        transition(afterSale, AfterSale.STATUS_REJECTED, "拒绝退款：" + adminRemark.trim());
        afterSale.setAdminRemark(adminRemark.trim());
        saleMapper.updateById(afterSale);
    }

    public AfterSale adminDetail(String asNo) {
        return requireOrder(asNo);
    }

    public Page<AfterSale> listMine(Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<AfterSale>()
                .eq(AfterSale::getUserId, UserContextHolder.require().userId())
                .orderByDesc(AfterSale::getId);
        if (status != null && status > 0) {
            wrapper.eq(AfterSale::getStatus, status);
        }
        return saleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    private void transition(AfterSale afterSale, int toStatus, String remark) {
        int from = afterSale.getStatus();
        Set<Integer> allowed = NEXT.getOrDefault(from, Set.of());
        if (!allowed.contains(toStatus)) {
            throw new BizException(40036, "售后单状态不允许该操作（当前：" + desc(from) + "）");
        }
        afterSale.setStatus(toStatus);
        saleMapper.updateById(afterSale);
    }

    private AfterSale requireOrder(String asNo) {
        AfterSale afterSale = saleMapper.selectOne(new LambdaQueryWrapper<AfterSale>()
                .eq(AfterSale::getAsNo, asNo)
                .last("limit 1"));
        if (afterSale == null) {
            throw new BizException(40410, "售后单不存在");
        }
        return afterSale;
    }

    private AfterSale requireOwned(String asNo) {
        AfterSale afterSale = requireOrder(asNo);
        if (!afterSale.getUserId().equals(UserContextHolder.require().userId())) {
            throw new BizException(40302, "无权操作该售后单");
        }
        return afterSale;
    }

    private String desc(int status) {
        return switch (status) {
            case AfterSale.STATUS_WAIT_REVIEW -> "待审核";
            case AfterSale.STATUS_REFUNDED -> "已退款";
            case AfterSale.STATUS_REJECTED -> "已拒绝";
            case AfterSale.STATUS_CANCELED -> "已撤销";
            default -> "未知";
        };
    }

    private static String nextAsNo() {
        return "A" + LocalDateTime.now().format(TS) + String.format("%08d", RANDOM.nextInt(100000000));
    }

    private static void requireLen(String value, int max, String label) {
        if (!StringUtils.hasText(value) || value.trim().isEmpty() || value.length() > max) {
            throw new BizException(40039, label + "必填且不超过 " + max + " 字");
        }
    }
}
