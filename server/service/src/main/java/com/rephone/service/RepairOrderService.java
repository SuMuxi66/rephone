package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.context.UserContext;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.express.ExpressCompanies;
import com.rephone.express.ExpressService;
import com.rephone.express.model.ExpressCompany;
import com.rephone.express.model.ExpressTrace;
import com.rephone.mapper.BrandMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.RepairItemMapper;
import com.rephone.mapper.RepairItemPriceMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.pojo.dto.ExpressFillRequest;
import com.rephone.pojo.dto.ExpressTraceResult;
import com.rephone.pojo.dto.RepairGroupView;
import com.rephone.pojo.dto.RepairItemCell;
import com.rephone.pojo.dto.RepairOrderCreateRequest;
import com.rephone.pojo.dto.RepairOrderDetail;
import com.rephone.pojo.dto.RepairOrderItem;
import com.rephone.pojo.entity.Brand;
import com.rephone.pojo.entity.PhoneModel;
import com.rephone.pojo.entity.RepairItem;
import com.rephone.pojo.entity.RepairItemPrice;
import com.rephone.pojo.entity.RepairOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 维修工单服务：项目字典/价格（机型价回退 model_id=0 基准价）、下单（服务端实时计价，
 * 客户端只传项目 ID，金额不可篡改）、用户取消、寄修运单流转与管理端状态机。
 * 上门(10)：10 待确认 → 20 已预约 → 30 维修中 → 40 待验收 → 50 已完成；
 * 寄修(20)：10 待确认 → 20 待寄出 → 25 已寄出 → 30 维修中 → 40 待回寄 → 45 回寄中 → 50 已完成。
 */
@Service
public class RepairOrderService {

    /** order_status_log.order_type：30=维修工单 */
    public static final int ORDER_TYPE_REPAIR = 30;

    /** 服务方式：10 上门维修（默认） */
    public static final int SERVICE_ONSITE = 10;
    /** 服务方式：20 寄修 */
    public static final int SERVICE_MAIL_IN = 20;

    private static final Map<Integer, Set<Integer>> ADMIN_TRANSITIONS_ONSITE = Map.of(
            RepairOrder.STATUS_WAIT_CONFIRM, Set.of(RepairOrder.STATUS_APPOINTED),
            RepairOrder.STATUS_APPOINTED, Set.of(RepairOrder.STATUS_REPAIRING),
            RepairOrder.STATUS_REPAIRING, Set.of(RepairOrder.STATUS_WAIT_ACCEPT),
            RepairOrder.STATUS_WAIT_ACCEPT, Set.of(RepairOrder.STATUS_DONE));

    /** 寄修：20→25 由用户填单触发、40→45 由商家填回寄单触发，均不走普通推进。 */
    private static final Map<Integer, Set<Integer>> ADMIN_TRANSITIONS_MAIL_IN = Map.of(
            RepairOrder.STATUS_WAIT_CONFIRM, Set.of(RepairOrder.STATUS_APPOINTED),
            RepairOrder.STATUS_SHIPPED, Set.of(RepairOrder.STATUS_REPAIRING),
            RepairOrder.STATUS_REPAIRING, Set.of(RepairOrder.STATUS_WAIT_ACCEPT),
            RepairOrder.STATUS_RETURNING, Set.of(RepairOrder.STATUS_DONE));

    /** 轨迹快照有效期：快递100 同一单号 30 分钟才能查一次，超频会锁单。 */
    private static final Duration TRACE_TTL = Duration.ofMinutes(30);

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final DateTimeFormatter TIME_TEXT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RepairItemMapper itemMapper;
    private final RepairItemPriceMapper priceMapper;
    private final RepairOrderMapper orderMapper;
    private final PhoneModelMapper modelMapper;
    private final BrandMapper brandMapper;
    private final StatusLogService statusLogService;
    private final ExpressService expressService;

    public RepairOrderService(RepairItemMapper itemMapper, RepairItemPriceMapper priceMapper,
                              RepairOrderMapper orderMapper, PhoneModelMapper modelMapper,
                              BrandMapper brandMapper, StatusLogService statusLogService,
                              ExpressService expressService) {
        this.itemMapper = itemMapper;
        this.priceMapper = priceMapper;
        this.orderMapper = orderMapper;
        this.modelMapper = modelMapper;
        this.brandMapper = brandMapper;
        this.statusLogService = statusLogService;
        this.expressService = expressService;
    }

    /** 维修项目分组字典（含该机型价格）。无价格的项目不下发。 */
    public List<RepairGroupView> items(Long modelId) {
        if (modelId == null) {
            throw new BizException(40030, "modelId 不能为空");
        }
        if (modelMapper.selectById(modelId) == null) {
            throw new BizException(40402, "机型不存在");
        }
        List<RepairItem> items = itemMapper.selectList(new LambdaQueryWrapper<RepairItem>()
                .eq(RepairItem::getEnabled, 1)
                .orderByAsc(RepairItem::getSort));
        Map<Long, BigDecimal> prices = priceMap(modelId,
                items.stream().map(RepairItem::getId).toList());
        Map<String, List<RepairItemCell>> grouped = new LinkedHashMap<>();
        for (RepairItem item : items) {
            BigDecimal price = prices.get(item.getId());
            if (price == null) {
                continue;
            }
            grouped.computeIfAbsent(item.getGroupName(), k -> new ArrayList<>())
                    .add(new RepairItemCell(item.getId(), item.getName(), toFen(price)));
        }
        return grouped.entrySet().stream()
                .map(e -> new RepairGroupView(e.getKey(), e.getValue()))
                .toList();
    }

    @Transactional
    public String create(RepairOrderCreateRequest req) {
        if (req == null || req.modelId() == null) {
            throw new BizException(40030, "modelId 不能为空");
        }
        if (CollectionUtils.isEmpty(req.itemIds())) {
            throw new BizException(40031, "请至少选择一个维修项目");
        }
        if (!StringUtils.hasText(req.contactName()) || !StringUtils.hasText(req.contactPhone())
                || !StringUtils.hasText(req.address())) {
            throw new BizException(40032, "联系人/电话/地址不能为空");
        }
        int serviceType = req.serviceType() == null ? SERVICE_ONSITE : req.serviceType();
        if (serviceType != SERVICE_ONSITE && serviceType != SERVICE_MAIL_IN) {
            throw new BizException(40038, "服务方式仅支持上门维修或寄修");
        }
        if (serviceType == SERVICE_ONSITE && !StringUtils.hasText(req.appointTime())) {
            throw new BizException(40037, "预约时间不能为空");
        }
        // 长度与列宽对齐，超限返回业务错误而非 DB 异常
        requireLen(req.contactName(), 32, "联系人");
        requireLen(req.contactPhone(), 20, "联系电话");
        requireLen(req.address(), 255, "上门地址");
        requireLen(req.appointTime(), 32, "预约时间");
        requireLen(req.remark(), 255, "故障描述");
        if (req.images() != null
                && (req.images().size() > 9 || req.images().stream().anyMatch(i -> i != null && i.length() > 255))) {
            throw new BizException(40039, "故障照片数量或路径超限");
        }
        PhoneModel model = modelMapper.selectById(req.modelId());
        if (model == null) {
            throw new BizException(40402, "机型不存在");
        }

        List<Long> itemIds = new LinkedHashSet<>(req.itemIds()).stream().toList();
        List<RepairItem> items = itemMapper.selectList(new LambdaQueryWrapper<RepairItem>()
                .in(RepairItem::getId, itemIds)
                .eq(RepairItem::getEnabled, 1));
        if (items.size() != itemIds.size()) {
            throw new BizException(40033, "存在无效的维修项目");
        }
        items.sort(Comparator.comparing(RepairItem::getSort));
        Map<Long, BigDecimal> prices = priceMap(req.modelId(), itemIds);

        // 项目快照 + 服务端实时计价（客户端不传金额，无篡改面）
        List<RepairOrderDetail.RepairLine> lines = new ArrayList<>();
        long totalFen = 0;
        for (RepairItem item : items) {
            BigDecimal price = prices.get(item.getId());
            if (price == null) {
                throw new BizException(40034, "该机型暂不支持维修项目：" + item.getName());
            }
            if (price.signum() < 0) {
                throw new BizException(40040, "维修项目价格配置异常");
            }
            long priceFen = toFen(price);
            lines.add(new RepairOrderDetail.RepairLine(item.getId(), item.getName(), priceFen));
            totalFen += priceFen;
        }

        Brand brand = brandMapper.selectById(model.getBrandId());
        UserContext ctx = UserContextHolder.require();

        RepairOrder order = new RepairOrder();
        order.setOrderNo(nextOrderNo());
        order.setUserId(ctx.userId());
        order.setOpenid(ctx.openid());
        order.setModelId(req.modelId());
        order.setBrandName(brand == null ? "" : brand.getName());
        order.setModelName(model.getName());
        order.setItemsJson(toJson(lines));
        order.setTotalFen(totalFen);
        order.setStatus(RepairOrder.STATUS_WAIT_CONFIRM);
        order.setServiceType(serviceType);
        order.setContactName(req.contactName().trim());
        order.setContactPhone(req.contactPhone().trim());
        order.setAddress(req.address().trim());
        order.setAppointTime(StringUtils.hasText(req.appointTime()) ? req.appointTime().trim() : "");
        order.setRemark(req.remark());
        order.setImagesJson(CollectionUtils.isEmpty(req.images()) ? null : toJson(req.images()));
        order.setWarrantyDays(180);
        orderMapper.insert(order);

        statusLogService.record(ORDER_TYPE_REPAIR, order.getOrderNo(), 0,
                RepairOrder.STATUS_WAIT_CONFIRM, 10, ctx.userId(), "用户提交维修预约");
        return order.getOrderNo();
    }

    @Transactional
    public void cancel(String orderNo, String reason) {
        requireLen(reason, 255, "取消原因");
        RepairOrder order = requireOwned(orderNo);
        int fromStatus = order.getStatus();
        boolean cancellable = fromStatus == RepairOrder.STATUS_WAIT_CONFIRM
                || (Integer.valueOf(SERVICE_MAIL_IN).equals(order.getServiceType())
                        && fromStatus == RepairOrder.STATUS_APPOINTED);
        if (!cancellable) {
            throw new BizException(40036, "订单状态不允许取消（当前：" + desc(fromStatus) + "）");
        }
        RepairOrder patch = new RepairOrder();
        patch.setStatus(RepairOrder.STATUS_CANCELED);
        int updated = orderMapper.update(patch, new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, order.getId())
                .eq(RepairOrder::getStatus, fromStatus));
        if (updated == 0) {
            throw new BizException(40045, "订单状态已变更，请刷新后重试");
        }
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, fromStatus,
                RepairOrder.STATUS_CANCELED, 10, order.getUserId(),
                StringUtils.hasText(reason) ? reason : "用户取消预约");
    }

    public Page<RepairOrderItem> list(Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getUserId, UserContextHolder.require().userId())
                .orderByDesc(RepairOrder::getId);
        if (status != null && status > 0) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        return toPage(orderMapper.selectPage(new Page<>(clampPage(pageNum), clampPage(pageSize)), wrapper));
    }

    public RepairOrderDetail detail(String orderNo) {
        RepairOrder order = requireOwned(orderNo);
        return toDetail(order);
    }

    public Page<RepairOrderItem> adminList(Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .orderByDesc(RepairOrder::getId);
        if (status != null && status > 0) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        return toPage(orderMapper.selectPage(new Page<>(clampPage(pageNum), clampPage(pageSize)), wrapper));
    }

    public RepairOrderDetail adminDetail(String orderNo) {
        return toDetail(requireOrder(orderNo));
    }

    @Transactional
    public void adminTransition(String orderNo, Integer toStatus, String remark) {
        if (toStatus == null) {
            throw new BizException(40036, "目标状态不能为空");
        }
        requireLen(remark, 255, "备注");
        RepairOrder order = requireOrder(orderNo);
        boolean mailIn = Integer.valueOf(SERVICE_MAIL_IN).equals(order.getServiceType());
        Set<Integer> allowed = (mailIn ? ADMIN_TRANSITIONS_MAIL_IN : ADMIN_TRANSITIONS_ONSITE)
                .getOrDefault(order.getStatus(), Set.of());
        if (!allowed.contains(toStatus)) {
            throw new BizException(40036, "订单状态不允许该操作（当前：" + desc(order.getStatus())
                    + "，目标：" + desc(toStatus) + "）");
        }
        int fromStatus = order.getStatus();
        RepairOrder patch = new RepairOrder();
        patch.setStatus(toStatus);
        int updated = orderMapper.update(patch, new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, order.getId())
                .eq(RepairOrder::getStatus, fromStatus));
        if (updated == 0) {
            throw new BizException(40045, "订单状态已变更，请刷新后重试");
        }
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, fromStatus, toStatus, 20, 0L,
                StringUtils.hasText(remark) ? remark : "后台更新状态");
    }

    // ===== 寄修运单流转 =====

    /** 用户填写寄出运单号：仅寄修单、状态 20；CAS 20→25。 */
    @Transactional
    public void fillExpress(String orderNo, ExpressFillRequest req) {
        ExpressCompany company = resolveCompany(req);
        String expressNo = req.expressNo().trim();
        RepairOrder order = requireOwned(orderNo);
        if (!Integer.valueOf(SERVICE_MAIL_IN).equals(order.getServiceType())) {
            throw new BizException(40048, "该订单不是寄修单，无需填写运单号");
        }
        if (order.getStatus() != RepairOrder.STATUS_APPOINTED) {
            throw new BizException(40049, "当前状态不可填写运单号（当前：" + desc(order.getStatus()) + "）");
        }
        RepairOrder patch = new RepairOrder();
        patch.setStatus(RepairOrder.STATUS_SHIPPED);
        patch.setExpressCom(company.com());
        patch.setExpressCompany(company.name());
        patch.setExpressNo(expressNo);
        int updated = orderMapper.update(patch, new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, order.getId())
                .eq(RepairOrder::getStatus, RepairOrder.STATUS_APPOINTED));
        if (updated == 0) {
            throw new BizException(40045, "订单状态已变更，请刷新后重试");
        }
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, RepairOrder.STATUS_APPOINTED,
                RepairOrder.STATUS_SHIPPED, 10, order.getUserId(), "用户寄出，运单号 " + expressNo);
    }

    /** 商家填写回寄运单号：仅寄修单、状态 40；CAS 40→45。 */
    @Transactional
    public void adminFillReturnExpress(String orderNo, ExpressFillRequest req) {
        ExpressCompany company = resolveCompany(req);
        String expressNo = req.expressNo().trim();
        RepairOrder order = requireOrder(orderNo);
        if (!Integer.valueOf(SERVICE_MAIL_IN).equals(order.getServiceType())) {
            throw new BizException(40048, "该订单不是寄修单，无需回寄");
        }
        if (order.getStatus() != RepairOrder.STATUS_WAIT_ACCEPT) {
            throw new BizException(40049, "当前状态不可填写回寄运单号（当前：" + desc(order.getStatus()) + "）");
        }
        RepairOrder patch = new RepairOrder();
        patch.setStatus(RepairOrder.STATUS_RETURNING);
        patch.setReturnExpressCom(company.com());
        patch.setReturnExpressCompany(company.name());
        patch.setReturnExpressNo(expressNo);
        int updated = orderMapper.update(patch, new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, order.getId())
                .eq(RepairOrder::getStatus, RepairOrder.STATUS_WAIT_ACCEPT));
        if (updated == 0) {
            throw new BizException(40045, "订单状态已变更，请刷新后重试");
        }
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, RepairOrder.STATUS_WAIT_ACCEPT,
                RepairOrder.STATUS_RETURNING, 20, 0L, "商家回寄，运单号 " + expressNo);
    }

    /** 用户确认收货：仅寄修单、状态 45；CAS 45→50。 */
    @Transactional
    public void confirmReceipt(String orderNo) {
        RepairOrder order = requireOwned(orderNo);
        if (!Integer.valueOf(SERVICE_MAIL_IN).equals(order.getServiceType())
                || order.getStatus() != RepairOrder.STATUS_RETURNING) {
            throw new BizException(40051, "当前状态无需确认收货（当前：" + desc(order.getStatus()) + "）");
        }
        RepairOrder patch = new RepairOrder();
        patch.setStatus(RepairOrder.STATUS_DONE);
        int updated = orderMapper.update(patch, new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, order.getId())
                .eq(RepairOrder::getStatus, RepairOrder.STATUS_RETURNING));
        if (updated == 0) {
            throw new BizException(40045, "订单状态已变更，请刷新后重试");
        }
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, RepairOrder.STATUS_RETURNING,
                RepairOrder.STATUS_DONE, 10, order.getUserId(), "用户确认收货，维修单完成");
    }

    /** 物流轨迹（用户/商家同用）：30 分钟内回本地快照，超时回源快递100 并落快照。 */
    public ExpressTraceResult trace(String orderNo, boolean returnDirection) {
        RepairOrder order = requireOrder(orderNo);
        String com = returnDirection ? order.getReturnExpressCom() : order.getExpressCom();
        String company = returnDirection ? order.getReturnExpressCompany() : order.getExpressCompany();
        String expressNo = returnDirection ? order.getReturnExpressNo() : order.getExpressNo();
        if (!StringUtils.hasText(expressNo)) {
            throw new BizException(40050, returnDirection ? "该订单还没有回寄运单号" : "该订单还没有寄出运单号");
        }
        ExpressTraceResult snapshot = freshSnapshot(
                returnDirection ? order.getReturnExpressTrace() : order.getExpressTrace(),
                returnDirection ? order.getReturnTraceAt() : order.getTraceAt());
        if (snapshot != null) {
            return snapshot;
        }
        ExpressCompany resolved = ExpressCompanies.byCom(com)
                .or(() -> ExpressCompanies.byName(company))
                .orElseThrow(() -> new BizException(40030, "快递公司编码无效，无法查询轨迹"));
        // 顺丰/中通必填收寄件人电话，这里用下单登记的联系电话
        ExpressTrace trace = expressService.queryTrace(resolved.com(), expressNo, order.getContactPhone());
        ExpressTraceResult result = new ExpressTraceResult(trace.com(), trace.companyName(), trace.expressNo(),
                trace.state(), trace.stateName(),
                trace.nodes().stream()
                        .map(n -> new ExpressTraceResult.Item(n.time(), n.context(), n.status(), n.statusCode()))
                        .toList(),
                LocalDateTime.now().toString(), false);
        RepairOrder patch = new RepairOrder();
        patch.setId(order.getId());
        String snapshotJson = toJson(result);
        if (returnDirection) {
            patch.setReturnExpressCom(trace.com());
            patch.setReturnExpressCompany(trace.companyName());
            patch.setReturnExpressTrace(snapshotJson);
            patch.setReturnTraceAt(LocalDateTime.now());
        } else {
            patch.setExpressCom(trace.com());
            patch.setExpressCompany(trace.companyName());
            patch.setExpressTrace(snapshotJson);
            patch.setTraceAt(LocalDateTime.now());
        }
        orderMapper.updateById(patch);
        return result;
    }

    /** 30 分钟内的轨迹快照；不存在、过期或解析失败返回 null（视为需要回源）。命中时 cached 置 true。 */
    private static ExpressTraceResult freshSnapshot(String json, LocalDateTime at) {
        if (!StringUtils.hasText(json) || at == null) {
            return null;
        }
        if (at.isBefore(LocalDateTime.now().minus(TRACE_TTL))) {
            return null;
        }
        try {
            ExpressTraceResult s = JSON.readValue(json, ExpressTraceResult.class);
            return new ExpressTraceResult(s.com(), s.companyName(), s.expressNo(), s.state(), s.stateName(),
                    s.items(), s.queriedAt(), true);
        } catch (Exception e) {
            return null;
        }
    }

    /** 快递公司解析：优先编码，其次展示名（与回收单同源字典）；都认不出直接拒绝。 */
    private static ExpressCompany resolveCompany(ExpressFillRequest req) {
        if (req == null || !StringUtils.hasText(req.expressNo())) {
            throw new BizException(40046, "快递单号不能为空");
        }
        requireLen(req.expressCompany(), 32, "快递公司");
        requireLen(req.expressNo(), 32, "快递单号");
        if (req.expressNo().trim().length() < 6) {
            throw new BizException(40046, "快递单号至少 6 位");
        }
        return ExpressCompanies.byCom(req.expressCom())
                .or(() -> ExpressCompanies.byName(req.expressCompany()))
                .orElseThrow(() -> new BizException(40047, "请从列表中重新选择快递公司"));
    }

    private RepairOrder requireOrder(String orderNo) {
        RepairOrder order = orderMapper.selectOne(new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getOrderNo, orderNo)
                .last("limit 1"));
        if (order == null) {
            throw new BizException(40404, "维修单不存在");
        }
        return order;
    }

    private RepairOrder requireOwned(String orderNo) {
        RepairOrder order = requireOrder(orderNo);
        if (!order.getUserId().equals(UserContextHolder.require().userId())) {
            throw new BizException(40302, "无权查看该维修单");
        }
        return order;
    }

    /** 机型价优先，缺省回退 model_id=0 基准价。 */
    private Map<Long, BigDecimal> priceMap(Long modelId, List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        List<RepairItemPrice> rows = priceMapper.selectList(new LambdaQueryWrapper<RepairItemPrice>()
                .in(RepairItemPrice::getItemId, itemIds)
                .in(RepairItemPrice::getModelId, List.of(modelId, 0L)));
        Map<Long, BigDecimal> result = new HashMap<>();
        for (RepairItemPrice row : rows) {
            BigDecimal existing = result.get(row.getItemId());
            if (existing == null || row.getModelId().equals(modelId)) {
                result.put(row.getItemId(), row.getPrice());
            }
        }
        return result;
    }

    private Page<RepairOrderItem> toPage(Page<RepairOrder> page) {
        Page<RepairOrderItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream()
                .map(o -> new RepairOrderItem(o.getOrderNo(), o.getBrandName(), o.getModelName(),
                        o.getTotalFen(), o.getStatus(), text(o.getCreateTime())))
                .toList());
        return result;
    }

    private RepairOrderDetail toDetail(RepairOrder o) {
        return new RepairOrderDetail(o.getOrderNo(), o.getBrandName(), o.getModelName(),
                readLines(o.getItemsJson()), o.getTotalFen(), o.getStatus(), o.getServiceType(),
                o.getContactName(), o.getContactPhone(), o.getAddress(), o.getAppointTime(),
                o.getRemark(), readStrings(o.getImagesJson()), o.getWarrantyDays(),
                text(o.getCreateTime()),
                o.getExpressCom(), o.getExpressCompany(), o.getExpressNo(),
                o.getReturnExpressCom(), o.getReturnExpressCompany(), o.getReturnExpressNo());
    }

    private String desc(int status) {
        return switch (status) {
            case RepairOrder.STATUS_WAIT_CONFIRM -> "待确认";
            case RepairOrder.STATUS_APPOINTED -> "已预约/待寄出";
            case RepairOrder.STATUS_SHIPPED -> "已寄出";
            case RepairOrder.STATUS_REPAIRING -> "维修中";
            case RepairOrder.STATUS_WAIT_ACCEPT -> "待验收/待回寄";
            case RepairOrder.STATUS_RETURNING -> "回寄中";
            case RepairOrder.STATUS_DONE -> "已完成";
            case RepairOrder.STATUS_CANCELED -> "已取消";
            default -> "未知";
        };
    }

    /** 分页参数钳制：页码 ≥1，页大小 ≤100。 */
    private static long clampPage(long value) {
        return Math.min(Math.max(value, 1), 100);
    }

    private static void requireLen(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new BizException(40039, label + "长度不能超过 " + max + " 字");
        }
    }

    private long toFen(BigDecimal yuan) {
        return yuan.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private String nextOrderNo() {
        return "F" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%08d", RANDOM.nextInt(100000000));
    }

    private String text(LocalDateTime time) {
        return time == null ? "" : time.format(TIME_TEXT);
    }

    private String toJson(Object value) {
        try {
            return JSON.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private List<RepairOrderDetail.RepairLine> readLines(String json) {
        try {
            if (!StringUtils.hasText(json)) {
                return List.of();
            }
            return JSON.readValue(json, new TypeReference<List<RepairOrderDetail.RepairLine>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<String> readStrings(String json) {
        try {
            if (!StringUtils.hasText(json)) {
                return List.of();
            }
            return JSON.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }
}
