package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.pojo.dto.ClickModelTopItem;
import com.rephone.pojo.dto.ClickSummary;
import com.rephone.pojo.dto.ClickTopItem;
import com.rephone.pojo.dto.ClickTopResult;
import com.rephone.pojo.dto.ClickTrendItem;
import com.rephone.pojo.dto.ClickWindowStat;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import com.rephone.pojo.entity.PhoneModel;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 点击漏斗统计：小程序启动 / 商品曝光→浏览→出售转化 / 机型点击→回收与维修转化。
 * 聚合风格与 {@link DashboardService} 一致：计数走 COUNT、趋势按天在 Java 侧分桶
 * （避免 H2/MySQL 日期函数差异）、榜单走 GROUP BY selectMaps。
 */
@Service
public class ClickStatsService {

    private final ClickEventMapper clickEventMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final RecycleOrderMapper recycleOrderMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final GoodsMapper goodsMapper;
    private final PhoneModelMapper phoneModelMapper;

    public ClickStatsService(ClickEventMapper clickEventMapper, SaleOrderMapper saleOrderMapper,
                             RecycleOrderMapper recycleOrderMapper, RepairOrderMapper repairOrderMapper,
                             GoodsMapper goodsMapper, PhoneModelMapper phoneModelMapper) {
        this.clickEventMapper = clickEventMapper;
        this.saleOrderMapper = saleOrderMapper;
        this.recycleOrderMapper = recycleOrderMapper;
        this.repairOrderMapper = repairOrderMapper;
        this.goodsMapper = goodsMapper;
        this.phoneModelMapper = phoneModelMapper;
    }

    public ClickSummary summary() {
        LocalDate today = LocalDate.now();
        return new ClickSummary(
                windowStat(today.atStartOfDay()),
                windowStat(today.minusDays(6).atStartOfDay()),
                windowStat(today.minusDays(29).atStartOfDay()));
    }

    /** 近 days 天（含今天）启动/曝光/浏览趋势，窗口上限 30 天。 */
    public List<ClickTrendItem> trend(int days) {
        int window = Math.min(Math.max(days, 1), 30);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(window - 1L).atStartOfDay();
        Map<String, long[]> byDate = new LinkedHashMap<>();
        for (int i = window - 1; i >= 0; i--) {
            byDate.put(today.minusDays(i).toString(), new long[3]);
        }
        QueryWrapper<ClickEvent> wrapper = new QueryWrapper<ClickEvent>()
                .select("create_time", "event_type")
                .ge("create_time", start)
                .in("event_type", List.of(ClickEvent.TYPE_APP_OPEN, ClickEvent.TYPE_GOODS_EXPOSE,
                        ClickEvent.TYPE_GOODS_VIEW));
        for (Map<String, Object> row : clickEventMapper.selectMaps(wrapper)) {
            LocalDateTime t = toDateTime(row.get("create_time"));
            if (t == null) {
                continue;
            }
            long[] counts = byDate.get(t.toLocalDate().toString());
            if (counts == null) {
                continue;
            }
            int type = num(row.get("event_type")).intValue();
            if (type == ClickEvent.TYPE_APP_OPEN) {
                counts[0]++;
            } else if (type == ClickEvent.TYPE_GOODS_EXPOSE) {
                counts[1]++;
            } else if (type == ClickEvent.TYPE_GOODS_VIEW) {
                counts[2]++;
            }
        }
        List<ClickTrendItem> items = new ArrayList<>();
        byDate.forEach((date, counts) -> items.add(
                new ClickTrendItem(date, counts[0], counts[1], counts[2])));
        return items;
    }

    public ClickTopResult top(int days, int limit) {
        int cap = Math.min(Math.max(limit, 1), 20);
        LocalDateTime start = LocalDate.now().minusDays(Math.min(Math.max(days, 1), 30) - 1L).atStartOfDay();
        return new ClickTopResult(topGoods(start, cap), topModels(start, cap));
    }

    // ===== 内部 =====

    private ClickWindowStat windowStat(LocalDateTime start) {
        long appOpens = countEventType(ClickEvent.TYPE_APP_OPEN, start);
        long exposes = countEventType(ClickEvent.TYPE_GOODS_EXPOSE, start);
        long clicks = countEventType(ClickEvent.TYPE_GOODS_VIEW, start);
        return new ClickWindowStat(appOpens, exposes, clicks, ratio(clicks, exposes),
                saleOrderMapper.selectCount(new QueryWrapper<SaleOrder>().ge("create_time", start)),
                recycleOrderMapper.selectCount(new QueryWrapper<RecycleOrder>().ge("create_time", start)),
                repairOrderMapper.selectCount(new QueryWrapper<RepairOrder>().ge("create_time", start)));
    }

    private long countEventType(int eventType, LocalDateTime start) {
        return clickEventMapper.selectCount(new QueryWrapper<ClickEvent>()
                .eq("event_type", eventType)
                .ge("create_time", start));
    }

    private List<ClickTopItem> topGoods(LocalDateTime start, int limit) {
        Map<Long, Long> exposes = countEventsByTarget(ClickEvent.TYPE_GOODS_EXPOSE, start);
        Map<Long, Long> clicks = countEventsByTarget(ClickEvent.TYPE_GOODS_VIEW, start);
        Map<Long, Long> orders = new HashMap<>();
        QueryWrapper<SaleOrder> orderWrapper = new QueryWrapper<SaleOrder>()
                .select("goods_id AS tid", "COUNT(*) AS cnt")
                .ge("create_time", start)
                .groupBy("goods_id");
        for (Map<String, Object> row : saleOrderMapper.selectMaps(orderWrapper)) {
            orders.put(Long.parseLong(String.valueOf(row.get("tid"))), num(row.get("cnt")));
        }
        // 空数据时不得调 selectBatchIds（空集合生成非法 IN () SQL）
        List<Long> goodsIds = mergeKeys(exposes, clicks);
        Map<Long, Goods> goodsMap = goodsIds.isEmpty() ? Map.of()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                        .collect(java.util.stream.Collectors.toMap(Goods::getId, g -> g, (a, b) -> a));
        List<ClickTopItem> items = new ArrayList<>();
        for (Long id : goodsIds) {
            long click = clicks.getOrDefault(id, 0L);
            long expose = exposes.getOrDefault(id, 0L);
            Goods goods = goodsMap.get(id);
            items.add(new ClickTopItem(id,
                    goods == null ? "已删除商品#" + id : goods.getName(),
                    expose, click, ratio(click, expose), orders.getOrDefault(id, 0L)));
        }
        items.sort((a, b) -> Long.compare(b.clicks(), a.clicks()));
        return items.subList(0, Math.min(limit, items.size()));
    }

    private List<ClickModelTopItem> topModels(LocalDateTime start, int limit) {
        Map<Long, Long> clicks = countEventsByTarget(ClickEvent.TYPE_MODEL_CLICK, start);
        Map<Long, Long> recycleOrders = countOrdersByModel(recycleOrderMapper, start, "model_id", RecycleOrder.class);
        Map<Long, Long> repairOrders = countOrdersByModel(repairOrderMapper, start, "model_id", RepairOrder.class);
        List<Long> ids = new ArrayList<>(clicks.keySet());
        for (Long key : mergeKeys(recycleOrders, repairOrders)) {
            if (!clicks.containsKey(key)) {
                ids.add(key);
            }
        }
        // 空数据时不得调 selectBatchIds（空集合生成非法 IN () SQL）
        Map<Long, PhoneModel> modelMap = ids.isEmpty() ? Map.of()
                : phoneModelMapper.selectBatchIds(ids).stream()
                        .collect(java.util.stream.Collectors.toMap(PhoneModel::getId, m -> m, (a, b) -> a));
        List<ClickModelTopItem> items = new ArrayList<>();
        for (Long id : ids) {
            PhoneModel model = modelMap.get(id);
            items.add(new ClickModelTopItem(id,
                    model == null ? "已删除机型#" + id : model.getName(),
                    clicks.getOrDefault(id, 0L),
                    recycleOrders.getOrDefault(id, 0L),
                    repairOrders.getOrDefault(id, 0L)));
        }
        items.sort((a, b) -> Long.compare(b.clicks(), a.clicks()));
        return items.subList(0, Math.min(limit, items.size()));
    }

    /** 事件按目标聚合计数（曝光按行=按商品，浏览/机型按行）。 */
    private Map<Long, Long> countEventsByTarget(int eventType, LocalDateTime start) {
        QueryWrapper<ClickEvent> wrapper = new QueryWrapper<ClickEvent>()
                .select("target_id AS tid", "COUNT(*) AS cnt")
                .eq("event_type", eventType)
                .ge("create_time", start)
                .groupBy("target_id");
        Map<Long, Long> result = new HashMap<>();
        for (Map<String, Object> row : clickEventMapper.selectMaps(wrapper)) {
            result.put(Long.parseLong(String.valueOf(row.get("tid"))), num(row.get("cnt")));
        }
        return result;
    }

    private <T> Map<Long, Long> countOrdersByModel(
            BaseMapper<T> mapper, LocalDateTime start, String column, Class<T> type) {
        QueryWrapper<T> wrapper = new QueryWrapper<T>()
                .select(column + " AS tid", "COUNT(*) AS cnt")
                .ge("create_time", start)
                .groupBy(column);
        Map<Long, Long> result = new HashMap<>();
        for (Map<String, Object> row : mapper.selectMaps(wrapper)) {
            result.put(Long.parseLong(String.valueOf(row.get("tid"))), num(row.get("cnt")));
        }
        return result;
    }

    private static List<Long> mergeKeys(Map<Long, Long> first, Map<Long, Long> second) {
        List<Long> keys = new ArrayList<>(first.keySet());
        for (Long key : second.keySet()) {
            if (!first.containsKey(key)) {
                keys.add(key);
            }
        }
        return keys;
    }

    private static Double ratio(long part, long total) {
        return total > 0 ? Math.round(part * 10000.0 / total) / 10000.0 : null;
    }

    /** selectMaps 里 DATETIME 可能以 LocalDateTime 或 Timestamp 返回（H2/MySQL 差异）。 */
    private static LocalDateTime toDateTime(Object value) {
        if (value instanceof LocalDateTime t) {
            return t;
        }
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        }
        return null;
    }

    private static Long num(Object value) {
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }
}
