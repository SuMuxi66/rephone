package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.context.UserContext;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.BrandMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.RepairItemMapper;
import com.rephone.mapper.RepairItemPriceMapper;
import com.rephone.mapper.RepairOrderMapper;
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
 * 客户端只传项目 ID，金额不可篡改）、用户取消与管理端状态机。
 * 状态：10 待确认 → 20 已预约 → 30 维修中 → 40 待验收 → 50 已完成；10 可取消为 80。
 */
@Service
public class RepairOrderService {

    /** order_status_log.order_type：30=维修工单 */
    public static final int ORDER_TYPE_REPAIR = 30;

    private static final Map<Integer, Set<Integer>> ADMIN_TRANSITIONS = Map.of(
            RepairOrder.STATUS_WAIT_CONFIRM, Set.of(RepairOrder.STATUS_APPOINTED),
            RepairOrder.STATUS_APPOINTED, Set.of(RepairOrder.STATUS_REPAIRING),
            RepairOrder.STATUS_REPAIRING, Set.of(RepairOrder.STATUS_WAIT_ACCEPT),
            RepairOrder.STATUS_WAIT_ACCEPT, Set.of(RepairOrder.STATUS_DONE));

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final DateTimeFormatter TIME_TEXT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RepairItemMapper itemMapper;
    private final RepairItemPriceMapper priceMapper;
    private final RepairOrderMapper orderMapper;
    private final PhoneModelMapper modelMapper;
    private final BrandMapper brandMapper;
    private final StatusLogService statusLogService;

    public RepairOrderService(RepairItemMapper itemMapper, RepairItemPriceMapper priceMapper,
                              RepairOrderMapper orderMapper, PhoneModelMapper modelMapper,
                              BrandMapper brandMapper, StatusLogService statusLogService) {
        this.itemMapper = itemMapper;
        this.priceMapper = priceMapper;
        this.orderMapper = orderMapper;
        this.modelMapper = modelMapper;
        this.brandMapper = brandMapper;
        this.statusLogService = statusLogService;
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
        if (!StringUtils.hasText(req.appointTime())) {
            throw new BizException(40037, "预约时间不能为空");
        }
        if (req.serviceType() != null && req.serviceType() != 10) {
            throw new BizException(40038, "寄修通道即将上线，当前仅支持上门维修");
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
        order.setServiceType(10);
        order.setContactName(req.contactName().trim());
        order.setContactPhone(req.contactPhone().trim());
        order.setAddress(req.address().trim());
        order.setAppointTime(req.appointTime().trim());
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
        if (order.getStatus() != RepairOrder.STATUS_WAIT_CONFIRM) {
            throw new BizException(40036, "订单状态不允许取消（当前：" + desc(order.getStatus()) + "）");
        }
        order.setStatus(RepairOrder.STATUS_CANCELED);
        orderMapper.updateById(order);
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, RepairOrder.STATUS_WAIT_CONFIRM,
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
        Set<Integer> allowed = ADMIN_TRANSITIONS.getOrDefault(order.getStatus(), Set.of());
        if (!allowed.contains(toStatus)) {
            throw new BizException(40036, "订单状态不允许该操作（当前：" + desc(order.getStatus())
                    + "，目标：" + desc(toStatus) + "）");
        }
        int fromStatus = order.getStatus();
        order.setStatus(toStatus);
        orderMapper.updateById(order);
        statusLogService.record(ORDER_TYPE_REPAIR, orderNo, fromStatus, toStatus, 20, 0L,
                StringUtils.hasText(remark) ? remark : "后台更新状态");
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
                text(o.getCreateTime()));
    }

    private String desc(int status) {
        return switch (status) {
            case RepairOrder.STATUS_WAIT_CONFIRM -> "待确认";
            case RepairOrder.STATUS_APPOINTED -> "已预约";
            case RepairOrder.STATUS_REPAIRING -> "维修中";
            case RepairOrder.STATUS_WAIT_ACCEPT -> "待验收";
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
