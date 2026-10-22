package com.rephone.service;

import com.rephone.common.context.UserContext;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.pojo.dto.TrackReportReq;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 小程序行为埋点上报：启动 / 商品曝光 / 商品浏览 / 机型点击。
 * PV 口径不去重，长期留存；商品事件写入时快照 brand/price_fen。
 * 埋点属于旁路数据：只做结构性校验，绝不影响业务主流程。
 */
@Service
public class TrackService {

    private static final Set<Integer> EVENT_TYPES = Set.of(
            ClickEvent.TYPE_APP_OPEN, ClickEvent.TYPE_GOODS_EXPOSE,
            ClickEvent.TYPE_GOODS_VIEW, ClickEvent.TYPE_MODEL_CLICK);
    private static final int MAX_EVENTS = 100;
    private static final int MAX_TARGET_IDS = 50;
    private static final int MAX_ROWS_PER_REPORT = 500;
    private static final int MAX_TEXT_LEN = 64;

    private final ClickEventMapper clickEventMapper;
    private final GoodsMapper goodsMapper;

    public TrackService(ClickEventMapper clickEventMapper, GoodsMapper goodsMapper) {
        this.clickEventMapper = clickEventMapper;
        this.goodsMapper = goodsMapper;
    }

    /** 批量落库，返回写入行数。 */
    public int report(List<TrackReportReq.TrackEventReq> events) {
        if (events == null || events.isEmpty()) {
            throw new BizException(40098, "事件列表不能为空");
        }
        if (events.size() > MAX_EVENTS) {
            throw new BizException(40098, "单次最多上报 " + MAX_EVENTS + " 条事件");
        }
        UserContext ctx = UserContextHolder.require();
        long tenantId = ctx.tenantId();
        long userId = ctx.userId();

        List<ClickEvent> rows = new ArrayList<>();
        for (TrackReportReq.TrackEventReq e : events) {
            rows.addAll(buildRows(e, tenantId, userId));
            if (rows.size() > MAX_ROWS_PER_REPORT) {
                throw new BizException(40097, "单次上报数据量过大");
            }
        }
        for (ClickEvent row : rows) {
            clickEventMapper.insert(row);
        }
        return rows.size();
    }

    private List<ClickEvent> buildRows(TrackReportReq.TrackEventReq e, long tenantId, long userId) {
        if (e == null || e.eventType() == null || !EVENT_TYPES.contains(e.eventType())) {
            throw new BizException(40095, "事件类型无效");
        }
        String page = trimText(e.page());
        String deviceId = trimText(e.deviceId());
        List<ClickEvent> rows = new ArrayList<>();
        switch (e.eventType()) {
            case ClickEvent.TYPE_APP_OPEN -> rows.add(baseRow(e, tenantId, userId,
                    ClickEvent.TARGET_NONE, 0L, page, deviceId));
            case ClickEvent.TYPE_GOODS_EXPOSE -> {
                List<Long> ids = normalizeTargetIds(e.targetIds());
                Map<Long, Goods> goodsMap = loadGoods(ids);
                for (Long id : ids) {
                    rows.add(goodsRow(e, tenantId, userId, id, goodsMap.get(id), page, deviceId));
                }
            }
            case ClickEvent.TYPE_GOODS_VIEW -> {
                Long id = requireTargetId(e.targetId());
                Map<Long, Goods> goodsMap = loadGoods(List.of(id));
                rows.add(goodsRow(e, tenantId, userId, id, goodsMap.get(id), page, deviceId));
            }
            case ClickEvent.TYPE_MODEL_CLICK ->
                    rows.add(baseRow(e, tenantId, userId,
                            ClickEvent.TARGET_MODEL, requireTargetId(e.targetId()), page, deviceId));
            default -> throw new BizException(40095, "事件类型无效");
        }
        return rows;
    }

    private ClickEvent goodsRow(TrackReportReq.TrackEventReq e, long tenantId, long userId,
                                Long goodsId, Goods goods, String page, String deviceId) {
        ClickEvent row = baseRow(e, tenantId, userId, ClickEvent.TARGET_GOODS, goodsId, page, deviceId);
        if (goods != null) {
            row.setBrand(goods.getBrand());
            row.setPriceFen(goods.getPriceFen());
        }
        return row;
    }

    private ClickEvent baseRow(TrackReportReq.TrackEventReq e, long tenantId, long userId,
                               int targetType, Long targetId, String page, String deviceId) {
        ClickEvent row = new ClickEvent();
        row.setTenantId(tenantId);
        row.setUserId(userId);
        row.setEventType(e.eventType());
        row.setTargetType(targetType);
        row.setTargetId(targetId);
        row.setPage(page);
        row.setDeviceId(deviceId);
        return row;
    }

    private List<Long> normalizeTargetIds(List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            throw new BizException(40096, "曝光事件缺少目标ID列表");
        }
        if (targetIds.size() > MAX_TARGET_IDS) {
            throw new BizException(40097, "单次曝光目标最多 " + MAX_TARGET_IDS + " 个");
        }
        return targetIds.stream().distinct().filter(id -> id != null && id > 0).toList();
    }

    private Long requireTargetId(Long targetId) {
        if (targetId == null || targetId <= 0) {
            throw new BizException(40096, "目标ID无效");
        }
        return targetId;
    }

    /** 批量取商品做品牌/价格快照；商品已删除时快照为空但事件仍保留。 */
    private Map<Long, Goods> loadGoods(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return goodsMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Goods::getId, Function.identity(), (a, b) -> a));
    }

    private static String trimText(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        String t = text.trim();
        return t.length() > MAX_TEXT_LEN ? t.substring(0, MAX_TEXT_LEN) : t;
    }
}
