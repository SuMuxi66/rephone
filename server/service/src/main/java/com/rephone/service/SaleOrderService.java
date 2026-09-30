package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.context.UserContext;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.pojo.entity.Goods;
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
 * 出售订单闭环：下单（价格服务端算+CAS 扣库存）→ mock 支付 → 发货 → 确认收货；
 * 取消回补库存；退款（管理端直退或售后审核驱动）置 90。
 * 状态流转全部 CAS + 写 order_status_log（order_type=20 出售单）。
 */
@Service
public class SaleOrderService {

    /** order_status_log.order_type：20=出售单 */
    public static final int ORDER_TYPE_SALE = 20;

    private static final Map<Integer, Set<Integer>> NEXT = Map.of(
            SaleOrder.STATUS_WAIT_PAY, Set.of(SaleOrder.STATUS_PAID, SaleOrder.STATUS_CANCELED),
            SaleOrder.STATUS_PAID, Set.of(SaleOrder.STATUS_SHIPPED, SaleOrder.STATUS_REFUNDED),
            SaleOrder.STATUS_SHIPPED, Set.of(SaleOrder.STATUS_DONE, SaleOrder.STATUS_REFUNDED));

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final SaleOrderMapper orderMapper;
    private final GoodsService goodsService;
    private final StatusLogService statusLogService;

    public SaleOrderService(SaleOrderMapper orderMapper, GoodsService goodsService,
                            StatusLogService statusLogService) {
        this.orderMapper = orderMapper;
        this.goodsService = goodsService;
        this.statusLogService = statusLogService;
    }

    @Transactional
    public String create(Long goodsId, Integer quantity, String receiverName, String receiverPhone,
                         String receiverAddr, String remark) {
        if (goodsId == null) {
            throw new BizException(40069, "goodsId 不能为空");
        }
        int qty = quantity == null ? 1 : quantity;
        if (qty < 1 || qty > 99) {
            throw new BizException(40069, "数量须为 1-99");
        }
        requireLen(receiverName, 32, "收件人");
        if (!StringUtils.hasText(receiverPhone) || !receiverPhone.matches("1\\d{10}")) {
            throw new BizException(40062, "手机号格式不正确");
        }
        requireLen(receiverAddr, 255, "收货地址");
        if (!StringUtils.hasText(receiverAddr)) {
            throw new BizException(40064, "收货地址不能为空");
        }
        requireLen(remark, 255, "备注");

        Goods goods = goodsService.getOnSale(goodsId);
        long totalFen = goods.getPriceFen() * qty;

        UserContext ctx = UserContextHolder.require();
        SaleOrder order = new SaleOrder();
        order.setOrderNo(nextOrderNo());
        order.setUserId(ctx.userId());
        order.setOpenid(ctx.openid());
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getName());
        order.setGoodsImage(goods.getImage());
        order.setPriceFen(goods.getPriceFen());
        order.setQuantity(qty);
        order.setTotalFen(totalFen);
        order.setStatus(SaleOrder.STATUS_WAIT_PAY);
        order.setReceiverName(receiverName.trim());
        order.setReceiverPhone(receiverPhone.trim());
        order.setReceiverAddr(receiverAddr.trim());
        order.setRemark(remark);
        orderMapper.insert(order);
        goodsService.deductStock(goods.getId(), qty);

        statusLogService.record(ORDER_TYPE_SALE, order.getOrderNo(), 0,
                SaleOrder.STATUS_WAIT_PAY, 10, ctx.userId(), "用户提交订单");
        return order.getOrderNo();
    }

    /** mock 支付：10 → 20。真实微信支付在 P6 支付联调时替换本方法内部实现。 */
    @Transactional
    public String pay(String orderNo) {
        SaleOrder order = getOwned(orderNo);
        if (order.getStatus() != SaleOrder.STATUS_WAIT_PAY) {
            throw new BizException(40036, "订单状态不允许支付（当前：" + desc(order.getStatus()) + "）");
        }
        String payNo = "MOCKPAY-" + order.getOrderNo();
        casStatus(order, SaleOrder.STATUS_PAID, 10, order.getUserId(),
                "支付成功" + fen(order.getTotalFen()) + "元");
        order.setPayNo(payNo);
        orderMapper.updateById(order);
        return payNo;
    }

    @Transactional
    public void cancel(String orderNo, String reason) {
        requireLen(reason, 255, "取消原因");
        SaleOrder order = getOwned(orderNo);
        if (order.getStatus() != SaleOrder.STATUS_WAIT_PAY) {
            throw new BizException(40036, "仅待付款订单可取消（当前：" + desc(order.getStatus()) + "）");
        }
        casStatus(order, SaleOrder.STATUS_CANCELED, 10, order.getUserId(),
                StringUtils.hasText(reason) ? reason : "用户取消订单");
        goodsService.restoreStock(order.getGoodsId(), order.getQuantity());
    }

    @Transactional
    public void confirm(String orderNo) {
        SaleOrder order = getOwned(orderNo);
        casStatus(order, SaleOrder.STATUS_DONE, 10, order.getUserId(), "用户确认收货");
    }

    // ===== 管理端 =====

    public Page<SaleOrder> adminPage(Integer status, String keyword, long pageNum, long pageSize) {
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<SaleOrder>()
                .orderByDesc(SaleOrder::getId);
        if (status != null && status > 0) {
            wrapper.eq(SaleOrder::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(SaleOrder::getOrderNo, kw)
                    .or().like(SaleOrder::getGoodsName, kw)
                    .or().like(SaleOrder::getReceiverName, kw)
                    .or().like(SaleOrder::getReceiverPhone, kw));
        }
        return orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    public SaleOrder adminDetail(String orderNo) {
        return requireOrder(orderNo);
    }

    @Transactional
    public void ship(String orderNo, String expressCompany, String expressNo, String remark) {
        requireLen(expressCompany, 32, "快递公司");
        requireLen(expressNo, 32, "快递单号");
        SaleOrder order = requireOrder(orderNo);
        if (order.getStatus() != SaleOrder.STATUS_PAID) {
            throw new BizException(40036, "仅已付款订单可发货（当前：" + desc(order.getStatus()) + "）");
        }
        casStatus(order, SaleOrder.STATUS_SHIPPED, 20, null,
                "发货 " + expressCompany + " " + expressNo);
        order.setExpressCompany(expressCompany.trim());
        order.setExpressNo(expressNo.trim());
        if (StringUtils.hasText(remark)) {
            order.setAdminRemark(remark.trim());
        }
        orderMapper.updateById(order);
    }

    /** 管理端整单退款（mock 到账）：20/30 → 90。 */
    @Transactional
    public void refund(String orderNo, Long refundFen, String reason, int operatorType, Long operatorId) {
        SaleOrder order = requireOrder(orderNo);
        Set<Integer> allowed = NEXT.getOrDefault(order.getStatus(), Set.of());
        if (!allowed.contains(SaleOrder.STATUS_REFUNDED)) {
            throw new BizException(40036, "订单状态不允许退款（当前：" + desc(order.getStatus()) + "）");
        }
        requireLen(reason, 255, "退款原因");
        long fen = refundFen == null ? order.getTotalFen() : refundFen;
        if (fen <= 0 || fen > order.getTotalFen()) {
            throw new BizException(40070, "退款金额无效");
        }
        int from = order.getStatus();
        long updated = orderMapper.update(null, new LambdaUpdateWrapper<SaleOrder>()
                .eq(SaleOrder::getId, order.getId())
                .eq(SaleOrder::getStatus, from)
                .set(SaleOrder::getStatus, SaleOrder.STATUS_REFUNDED)
                .set(SaleOrder::getRefundFen, fen)
                .set(SaleOrder::getRefundReason, reason.trim()));
        if (updated != 1) {
            throw new BizException(40024, "订单状态已变更，请刷新后重试");
        }
        order.setStatus(SaleOrder.STATUS_REFUNDED);
        statusLogService.record(ORDER_TYPE_SALE, orderNo, from, SaleOrder.STATUS_REFUNDED,
                operatorType, operatorId, "退款" + fen(fen) + "元：" + reason.trim());
    }

    // ===== 共用 =====

    @Transactional
    public void adminTransition(String orderNo, Integer toStatus, String remark) {
        if (toStatus == null) {
            throw new BizException(40036, "目标状态不能为空");
        }
        SaleOrder order = requireOrder(orderNo);
        Set<Integer> allowed = NEXT.getOrDefault(order.getStatus(), Set.of());
        if (!allowed.contains(toStatus)) {
            throw new BizException(40036, "订单状态不允许该操作（当前：" + desc(order.getStatus()) + "）");
        }
        casStatus(order, toStatus, 20, null, StringUtils.hasText(remark) ? remark : "后台更新状态");
        if (toStatus == SaleOrder.STATUS_CANCELED) {
            goodsService.restoreStock(order.getGoodsId(), order.getQuantity());
        }
    }

    public Page<SaleOrder> listMine(Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<SaleOrder>()
                .eq(SaleOrder::getUserId, UserContextHolder.require().userId())
                .orderByDesc(SaleOrder::getId);
        if (status != null && status > 0) {
            wrapper.eq(SaleOrder::getStatus, status);
        }
        return orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    public SaleOrder detailMine(String orderNo) {
        return getOwned(orderNo);
    }

    public SaleOrder requireOrder(String orderNo) {
        SaleOrder order = orderMapper.selectOne(new LambdaQueryWrapper<SaleOrder>()
                .eq(SaleOrder::getOrderNo, orderNo)
                .last("limit 1"));
        if (order == null) {
            throw new BizException(40409, "出售订单不存在");
        }
        return order;
    }

    /** 归属校验后取单（跨服务复用：售后申请）。 */
    public SaleOrder getOwned(String orderNo) {
        SaleOrder order = requireOrder(orderNo);
        if (!order.getUserId().equals(UserContextHolder.require().userId())) {
            throw new BizException(40302, "无权操作该订单");
        }
        return order;
    }

    /** CAS 状态流转 + 日志。 */
    private void casStatus(SaleOrder order, int toStatus, int operatorType, Long operatorId, String remark) {
        int from = order.getStatus();
        Set<Integer> allowed = NEXT.getOrDefault(from, Set.of());
        if (!allowed.contains(toStatus)) {
            throw new BizException(40024, "订单状态不允许该操作（当前：" + desc(from) + "）");
        }
        long updated = orderMapper.update(null, new LambdaUpdateWrapper<SaleOrder>()
                .eq(SaleOrder::getId, order.getId())
                .eq(SaleOrder::getStatus, from)
                .set(SaleOrder::getStatus, toStatus));
        if (updated != 1) {
            throw new BizException(40024, "订单状态已变更，请刷新后重试");
        }
        order.setStatus(toStatus);
        statusLogService.record(ORDER_TYPE_SALE, order.getOrderNo(), from, toStatus,
                operatorType, operatorId, remark);
    }

    private String desc(int status) {
        return switch (status) {
            case SaleOrder.STATUS_WAIT_PAY -> "待付款";
            case SaleOrder.STATUS_PAID -> "已付款待发货";
            case SaleOrder.STATUS_SHIPPED -> "已发货";
            case SaleOrder.STATUS_DONE -> "已完成";
            case SaleOrder.STATUS_CANCELED -> "已取消";
            case SaleOrder.STATUS_REFUNDED -> "已退款";
            default -> "未知";
        };
    }

    private static String fen(Long fen) {
        return fen == null ? "0" : java.math.BigDecimal.valueOf(fen)
                .divide(java.math.BigDecimal.valueOf(100)).toPlainString();
    }

    private static String nextOrderNo() {
        return "S" + LocalDateTime.now().format(TS) + String.format("%08d", RANDOM.nextInt(100000000));
    }

    private static void requireLen(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new BizException(40039, label + "长度不能超过 " + max + " 字");
        }
    }
}
