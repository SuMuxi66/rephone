package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.InspectionMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.pojo.dto.InspectionSubmitRequest;
import com.rephone.pojo.dto.ExpressFillRequest;
import com.rephone.pojo.dto.RecycleOrderCreateRequest;
import com.rephone.pojo.dto.RecycleOrderDetail;
import com.rephone.pojo.dto.RecycleOrderItem;
import com.rephone.pojo.dto.QuoteCalculateRequest;
import com.rephone.pojo.dto.QuoteResult;
import com.rephone.pojo.entity.Inspection;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.express.ExpressService;
import com.rephone.express.model.ExpressPickupRequest;
import com.rephone.express.model.ExpressPickupResult;
import com.rephone.wechat.WxSubscribeService;
import com.rephone.wechat.WxPayoutService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 回收订单服务：下单（估价复核）、查询、状态机流转（全部写 order_status_log）、
 * 状态变更触发订阅消息推送。
 */
@Service
public class RecycleOrderService {

    private static final Map<Integer, String> STATUS_DESC = Map.of(
            RecycleOrder.STATUS_WAIT_SEND, "待寄出",
            RecycleOrder.STATUS_SHIPPING, "运输中",
            RecycleOrder.STATUS_INSPECTING, "质检中",
            RecycleOrder.STATUS_WAIT_CONFIRM, "待确认",
            RecycleOrder.STATUS_PAID, "已打款",
            RecycleOrder.STATUS_DONE, "已完成",
            RecycleOrder.STATUS_CANCELED, "已取消");

    /** 合法状态迁移。 */
    private static final Map<Integer, List<Integer>> TRANSITIONS = Map.of(
            RecycleOrder.STATUS_WAIT_SEND, List.of(RecycleOrder.STATUS_SHIPPING, RecycleOrder.STATUS_CANCELED),
            RecycleOrder.STATUS_SHIPPING, List.of(RecycleOrder.STATUS_INSPECTING),
            RecycleOrder.STATUS_INSPECTING, List.of(RecycleOrder.STATUS_WAIT_CONFIRM),
            RecycleOrder.STATUS_WAIT_CONFIRM, List.of(RecycleOrder.STATUS_PAID, RecycleOrder.STATUS_CANCELED),
            RecycleOrder.STATUS_PAID, List.of(RecycleOrder.STATUS_DONE));

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ObjectMapper JSON = new ObjectMapper();

    private final RecycleOrderMapper orderMapper;
    private final InspectionMapper inspectionMapper;
    private final QuoteService quoteService;
    private final StatusLogService statusLogService;
    private final WxSubscribeService subscribeService;
    private final WxPayoutService payoutService;
    private final ExpressService expressService;

    public RecycleOrderService(RecycleOrderMapper orderMapper, InspectionMapper inspectionMapper,
                               QuoteService quoteService, StatusLogService statusLogService,
                               WxSubscribeService subscribeService, WxPayoutService payoutService,
                               ExpressService expressService) {
        this.orderMapper = orderMapper;
        this.inspectionMapper = inspectionMapper;
        this.quoteService = quoteService;
        this.statusLogService = statusLogService;
        this.subscribeService = subscribeService;
        this.payoutService = payoutService;
        this.expressService = expressService;
    }

    @Transactional
    public String create(RecycleOrderCreateRequest req) {
        validateCreate(req);
        // 服务端复核估价：以 quote_rule 实时计算为准，客户端报价不一致则拒绝（防止篡改价格下单）
        QuoteResult serverQuote = quoteService.calculate(new QuoteCalculateRequest(
                req.modelId(), req.storage(), req.condition(), req.screenCondition(), req.issues()));
        if (!serverQuote.priceFen().equals(req.quoteFen())) {
            throw new BizException(40020, "报价已更新，请重新估价");
        }

        RecycleOrder order = new RecycleOrder();
        order.setOrderNo(nextOrderNo());
        order.setUserId(UserContextHolder.require().userId());
        order.setOpenid(UserContextHolder.require().openid());
        order.setModelId(req.modelId());
        order.setBrandName(serverQuote.brandName());
        order.setModelName(serverQuote.modelName());
        order.setStorage(req.storage());
        order.setConditionKey(req.condition());
        order.setConditionLabel(serverQuote.conditionLabel());
        order.setIssuesJson(toJson(req.issues()));
        order.setQuoteFen(serverQuote.priceFen());
        order.setStatus(RecycleOrder.STATUS_WAIT_SEND);
        order.setPickupType(req.pickupType());
        order.setPickupName(req.pickupName());
        order.setPickupPhone(req.pickupPhone());
        order.setPickupAddress(req.pickupAddress());
        order.setRemark(req.remark());
        orderMapper.insert(order);

        // 上门取件单：向快递100 预约取件并回写任务号/运单号（运单号可能由后续回调补齐）
        if (order.getPickupType() != null && order.getPickupType() == 20) {
            try {
                ExpressPickupResult pickup = expressService.createPickup(new ExpressPickupRequest(
                        order.getOrderNo(), order.getPickupName(), order.getPickupPhone(),
                        order.getPickupAddress(), null));
                order.setExpressTaskNo(pickup.taskId());
                if (pickup.expressNo() != null) {
                    order.setExpressNo(pickup.expressNo());
                }
                orderMapper.updateById(order);
            } catch (Exception e) {
                // 取件预约失败不阻塞下单，用户/后台可改约
            }
        }

        statusLogService.record(10, order.getOrderNo(), 0, RecycleOrder.STATUS_WAIT_SEND, 10,
                order.getUserId(), "用户创建回收订单");
        return order.getOrderNo();
    }

    public Page<RecycleOrderItem> list(Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getUserId, UserContextHolder.require().userId())
                .orderByDesc(RecycleOrder::getId);
        if (status != null && status > 0) {
            wrapper.eq(RecycleOrder::getStatus, status);
        }
        Page<RecycleOrder> page = orderMapper.selectPage(new Page<>(clampPage(pageNum), clampPage(pageSize)), wrapper);
        Page<RecycleOrderItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toItem).toList());
        return result;
    }

    public RecycleOrder detail(String orderNo) {
        RecycleOrder order = getByOrderNo(orderNo);
        if (!order.getUserId().equals(UserContextHolder.require().userId())) {
            throw new BizException(40302, "无权查看该订单");
        }
        return order;
    }

    /** 详情 DTO：订单字段 + 质检记录列表（用户端，校验归属）。 */
    public RecycleOrderDetail detailWithInspections(String orderNo) {
        return assemble(detail(orderNo));
    }

    /** 管理端详情：不做归属校验（管理端可见全部租户订单）。 */
    public RecycleOrderDetail adminDetailWithInspections(String orderNo) {
        return assemble(getByOrderNo(orderNo));
    }

    private RecycleOrderDetail assemble(RecycleOrder o) {
        return new RecycleOrderDetail(o.getOrderNo(), o.getBrandName(), o.getModelName(), o.getStorage(),
                o.getConditionLabel(), fromJsonList(o.getIssuesJson()), o.getQuoteFen(), o.getFinalFen(),
                o.getStatus(), desc(o.getStatus()), o.getPickupType(), o.getPickupName(), o.getPickupPhone(),
                o.getPickupAddress(), o.getExpressCompany(), o.getExpressNo(), o.getRemark(), o.getAdminRemark(),
                o.getCreateTime() == null ? "" : o.getCreateTime().toString(), inspections(o.getOrderNo()));
    }

    public List<RecycleOrderDetail.InspectionItem> inspections(String orderNo) {
        return inspectionMapper.selectList(new LambdaQueryWrapper<Inspection>()
                        .eq(Inspection::getOrderNo, orderNo)
                        .orderByDesc(Inspection::getId))
                .stream()
                .map(i -> new RecycleOrderDetail.InspectionItem(i.getResult(), i.getFinalFen(),
                        fromJsonList(i.getImagesJson()), i.getCreateTime() == null ? "" : i.getCreateTime().toString()))
                .toList();
    }

    /** 用户填写/修改运单号：10 → 20 运输中。 */
    @Transactional
    public void fillExpress(String orderNo, ExpressFillRequest req) {
        if (req == null || !StringUtils.hasText(req.expressNo())) {
            throw new BizException(40021, "快递单号不能为空");
        }
        requireLen(req.expressCompany(), 32, "快递公司");
        requireLen(req.expressNo(), 32, "快递单号");
        RecycleOrder order = getOwned(orderNo);
        transition(order, RecycleOrder.STATUS_SHIPPING, 10, order.getUserId(), "用户填写运单号");
        order.setExpressCompany(req.expressCompany());
        order.setExpressNo(req.expressNo().trim());
        orderMapper.updateById(order);
    }

    /** 用户取消：仅待寄出可取消。 */
    @Transactional
    public void cancel(String orderNo, String reason) {
        requireLen(reason, 255, "取消原因");
        RecycleOrder order = getOwned(orderNo);
        transition(order, RecycleOrder.STATUS_CANCELED, 10, order.getUserId(),
                StringUtils.hasText(reason) ? reason : "用户取消订单");
    }

    /** 用户确认打款：40 → 50 → 打款。 */
    @Transactional
    public void confirmPayout(String orderNo) {
        RecycleOrder order = getOwned(orderNo);
        doPayout(order, 10, order.getUserId());
    }

    /**
     * 打款统一入口：先 CAS 占位 40→50（同一订单只有一个请求能进入打款，杜绝并发重复外呼），
     * 打款是事务内最后一步，其后不存在任何会回滚本事务的 DB 写；打款抛异常则整事务回滚回 40，可重试
     * （微信侧以订单号派生的 partner_trade_no 幂等，不会重复出款）。
     */
    private void doPayout(RecycleOrder order, int operatorType, Long operatorId) {
        if (order.getFinalFen() == null) {
            throw new BizException(40022, "订单尚未出质检最终价");
        }
        transition(order, RecycleOrder.STATUS_PAID, operatorType, operatorId,
                "打款" + fen(order.getFinalFen()) + "元");
        payoutService.payoutToChange(order.getOrderNo(), order.getOpenid(), order.getFinalFen());
    }

    // ===== 管理端 =====

    public Page<RecycleOrderItem> adminList(Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .orderByDesc(RecycleOrder::getId);
        if (status != null && status > 0) {
            wrapper.eq(RecycleOrder::getStatus, status);
        }
        Page<RecycleOrder> page = orderMapper.selectPage(new Page<>(clampPage(pageNum), clampPage(pageSize)), wrapper);
        Page<RecycleOrderItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toItem).toList());
        return result;
    }

    @Transactional
    public void adminTransition(String orderNo, int toStatus, String remark) {
        RecycleOrder order = getByOrderNo(orderNo);
        transition(order, toStatus, 20, null, remark);
    }

    /** 管理端提交质检：30 → 40 待确认，落最终价。最终价必须为正且不超过估价两倍（防资金异常）。 */
    @Transactional
    public void adminSubmitInspection(String orderNo, InspectionSubmitRequest req) {
        if (req == null || !StringUtils.hasText(req.result()) || req.finalFen() == null) {
            throw new BizException(40023, "质检结论与最终价不能为空");
        }
        requireLen(req.result(), 512, "质检结论");
        if (req.images() != null
                && (req.images().size() > 9 || req.images().stream().anyMatch(i -> i != null && i.length() > 255))) {
            throw new BizException(40039, "质检图片数量或路径超限");
        }
        RecycleOrder order = getByOrderNo(orderNo);
        if (req.finalFen() <= 0 || req.finalFen() > order.getQuoteFen() * 2) {
            throw new BizException(40029, "质检最终价超出合理范围（须为正且不超过估价两倍）");
        }
        transition(order, RecycleOrder.STATUS_WAIT_CONFIRM, 20, null,
                "质检完成，最终价" + fen(req.finalFen()) + "元");
        order.setFinalFen(req.finalFen());
        orderMapper.updateById(order);

        Inspection inspection = new Inspection();
        inspection.setOrderNo(orderNo);
        inspection.setResult(req.result());
        inspection.setFinalFen(req.finalFen());
        inspection.setImagesJson(toJson(req.images()));
        inspection.setOperatorId(UserContextHolder.get() == null ? null : UserContextHolder.get().userId());
        inspectionMapper.insert(inspection);
    }

    /** 管理端直接触发打款（40 → 50），与用户确认打款共用 CAS 占位逻辑。 */
    @Transactional
    public void adminPayout(String orderNo) {
        RecycleOrder order = getByOrderNo(orderNo);
        doPayout(order, 20, null);
    }

    /** 快递100 物流回调：揽收/运输 → 20 运输中。按运单号或取件任务号匹配订单。 */
    @Transactional
    public void onExpressShipping(String expressNo, String taskNo, String nodeDesc) {
        RecycleOrder order = orderMapper.selectOne(new LambdaQueryWrapper<RecycleOrder>()
                .and(w -> {
                    if (StringUtils.hasText(expressNo)) {
                        w.eq(RecycleOrder::getExpressNo, expressNo);
                    }
                    if (StringUtils.hasText(taskNo)) {
                        if (StringUtils.hasText(expressNo)) {
                            w.or();
                        }
                        w.eq(RecycleOrder::getExpressTaskNo, taskNo);
                    }
                })
                .last("limit 1"));
        if (order == null || order.getStatus() >= RecycleOrder.STATUS_SHIPPING) {
            return;
        }
        transition(order, RecycleOrder.STATUS_SHIPPING, 30, null,
                StringUtils.hasText(nodeDesc) ? nodeDesc : "快递揽收");
    }

    // ===== 内部 =====

    private void transition(RecycleOrder order, int toStatus, int operatorType, Long operatorId, String remark) {
        int from = order.getStatus();
        List<Integer> allowed = TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(toStatus)) {
            throw new BizException(40024, "订单状态不允许该操作（当前：" + desc(from) + "）");
        }
        // CAS 状态流转：UPDATE ... WHERE status=from，并发下只有一个请求能改成功，防状态互相覆盖/重复打款
        long updated = orderMapper.update(null, new LambdaUpdateWrapper<RecycleOrder>()
                .eq(RecycleOrder::getId, order.getId())
                .eq(RecycleOrder::getStatus, from)
                .set(RecycleOrder::getStatus, toStatus));
        if (updated != 1) {
            throw new BizException(40024, "订单状态已变更，请刷新后重试");
        }
        order.setStatus(toStatus);
        statusLogService.record(10, order.getOrderNo(), from, toStatus, operatorType, operatorId, remark);
        String notify = "您的回收订单 " + order.getOrderNo() + " " + desc(from) + " → " + desc(toStatus);
        subscribeService.notifyOrderStatus(order.getOpenid(), order.getOrderNo(), notify);
    }

    private RecycleOrder getOwned(String orderNo) {
        RecycleOrder order = getByOrderNo(orderNo);
        if (!order.getUserId().equals(UserContextHolder.require().userId())) {
            throw new BizException(40302, "无权操作该订单");
        }
        return order;
    }

    private RecycleOrder getByOrderNo(String orderNo) {
        RecycleOrder order = orderMapper.selectOne(new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getOrderNo, orderNo)
                .last("limit 1"));
        if (order == null) {
            throw new BizException(40403, "订单不存在");
        }
        return order;
    }

    private void validateCreate(RecycleOrderCreateRequest req) {
        if (req == null || req.modelId() == null || !StringUtils.hasText(req.storage())
                || !StringUtils.hasText(req.condition()) || req.quoteFen() == null) {
            throw new BizException(40025, "估价信息不完整");
        }
        if (req.pickupType() == null || (req.pickupType() != 10 && req.pickupType() != 20)) {
            throw new BizException(40026, "取件方式无效");
        }
        if (!StringUtils.hasText(req.pickupName()) || !StringUtils.hasText(req.pickupPhone())
                || !StringUtils.hasText(req.pickupAddress())) {
            throw new BizException(40027, "取件联系人/电话/地址不能为空");
        }
        // 长度与列宽对齐，超限返回业务错误而非 DB 异常
        requireLen(req.pickupName(), 32, "取件联系人");
        requireLen(req.pickupPhone(), 20, "取件电话");
        requireLen(req.pickupAddress(), 255, "取件地址");
        requireLen(req.remark(), 255, "备注");
    }

    /** 分页参数钳制：页码 ≥1，页大小 ≤100，防超大分页拖库。 */
    private static long clampPage(long value) {
        return Math.min(Math.max(value, 1), 100);
    }

    private static void requireLen(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new BizException(40039, label + "长度不能超过 " + max + " 字");
        }
    }

    /** 订单号：R + 时间戳 + 8 位随机，业务生成，非自增。 */
    private static String nextOrderNo() {
        return "R" + LocalDateTime.now().format(TS)
                + String.format("%08d", RANDOM.nextInt(100000000));
    }

    private RecycleOrderItem toItem(RecycleOrder o) {
        return new RecycleOrderItem(o.getOrderNo(), o.getBrandName(), o.getModelName(), o.getStorage(),
                o.getConditionLabel(), o.getQuoteFen(), o.getFinalFen(), o.getStatus(), desc(o.getStatus()),
                o.getCreateTime() == null ? "" : o.getCreateTime().toString());
    }

    private static String desc(int status) {
        return STATUS_DESC.getOrDefault(status, "未知(" + status + ")");
    }

    private static String fen(Long fen) {
        return fen == null ? "0" : java.math.BigDecimal.valueOf(fen)
                .divide(java.math.BigDecimal.valueOf(100)).toPlainString();
    }

    private static String toJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return JSON.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private static List<String> fromJsonList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return JSON.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
