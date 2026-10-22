package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.mapper.UserAiProfileMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.UserProfileCard;
import com.rephone.pojo.dto.UserProfileDetail;
import com.rephone.pojo.dto.UserViewItem;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
import com.rephone.pojo.entity.User;
import com.rephone.pojo.entity.UserAiProfile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户行为画像：基于 click_event（启动/商品浏览）+ 三线订单，聚合每个用户的
 * 活跃度、品牌偏好、价格档分布与消费力。读时聚合，量级可控（MVP）。
 */
@Service
public class AdminUserProfileService {

    /** 浏览商品价格档（元）边界：1000 / 2000 / 3500 */
    private static final long[] BAND_BOUNDS_FEN = {100000L, 200000L, 350000L};
    private static final String[] BAND_LABELS = {"1千以下", "1千-2千", "2千-3.5千", "3.5千以上"};
    private static final int DETAIL_LIMIT = 50;

    private final ClickEventMapper clickEventMapper;
    private final UserMapper userMapper;
    private final UserAiProfileMapper userAiProfileMapper;
    private final GoodsMapper goodsMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final RecycleOrderMapper recycleOrderMapper;
    private final RepairOrderMapper repairOrderMapper;

    public AdminUserProfileService(ClickEventMapper clickEventMapper, UserMapper userMapper,
                                   UserAiProfileMapper userAiProfileMapper, GoodsMapper goodsMapper,
                                   SaleOrderMapper saleOrderMapper,
                                   RecycleOrderMapper recycleOrderMapper, RepairOrderMapper repairOrderMapper) {
        this.clickEventMapper = clickEventMapper;
        this.userMapper = userMapper;
        this.userAiProfileMapper = userAiProfileMapper;
        this.goodsMapper = goodsMapper;
        this.saleOrderMapper = saleOrderMapper;
        this.recycleOrderMapper = recycleOrderMapper;
        this.repairOrderMapper = repairOrderMapper;
    }

    /** 有行为数据的用户画像卡片，最近打开倒序，支持昵称/手机号过滤。 */
    public Page<UserProfileCard> page(long pageNum, long pageSize, String keyword) {
        Map<Long, UserAgg> aggs = aggregateByUser();
        List<Long> userIds = new ArrayList<>(aggs.keySet());
        Map<Long, User> users = userIds.isEmpty() ? Map.of()
                : userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        String kw = keyword == null ? "" : keyword.trim();
        List<UserProfileCard> cards = new ArrayList<>();
        for (Map.Entry<Long, UserAgg> entry : aggs.entrySet()) {
            User user = users.get(entry.getKey());
            if (user == null) {
                continue;
            }
            if (StringUtils.hasText(kw)
                    && !(safe(user.getNickname()).contains(kw) || safe(user.getPhone()).contains(kw))) {
                continue;
            }
            UserAgg agg = entry.getValue();
            cards.add(new UserProfileCard(user.getId(), user.getNickname(), user.getAvatarUrl(), user.getPhone(),
                    agg.opens, agg.views, agg.lastOpen == null ? "" : agg.lastOpen.toString(),
                    agg.topBrand, agg.topPriceBand, agg.avgPriceFen, ""));
        }
        cards.sort(Comparator.comparing((UserProfileCard c) -> c.lastOpenTime() == null ? "" : c.lastOpenTime())
                .reversed());
        long total = cards.size();
        int from = (int) Math.max(0, (pageNum - 1) * pageSize);
        int to = (int) Math.min(total, from + pageSize);
        List<UserProfileCard> records = from >= to ? List.of() : cards.subList(from, to);
        // 只为当前页用户取 AI 摘要，避免全量 join
        Map<Long, String> aiSummaries = records.isEmpty() ? Map.of()
                : userAiProfileMapper.selectList(new QueryWrapper<UserAiProfile>()
                                .in("user_id", records.stream().map(UserProfileCard::userId).toList()))
                        .stream()
                        .collect(Collectors.toMap(UserAiProfile::getUserId,
                                p -> preview(p.getAnalysis()), (a, b) -> a));
        records = records.stream()
                .map(c -> new UserProfileCard(c.userId(), c.nickname(), c.avatarUrl(), c.phone(),
                        c.openCount(), c.viewCount(), c.lastOpenTime(), c.topBrand(), c.topPriceBand(),
                        c.avgPriceFen(), aiSummaries.getOrDefault(c.userId(), "")))
                .toList();
        Page<UserProfileCard> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(records);
        return page;
    }

    public UserProfileDetail detail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(40404, "用户不存在");
        }
        List<ClickEvent> allEvents = clickEventMapper.selectList(new QueryWrapper<ClickEvent>()
                .eq("user_id", userId)
                .and(w -> w.eq("event_type", ClickEvent.TYPE_APP_OPEN)
                        .or().eq("event_type", ClickEvent.TYPE_GOODS_VIEW))
                .orderByDesc("create_time")
                .last("LIMIT 200"));

        List<String> openTimes = new ArrayList<>();
        List<ClickEvent> viewEvents = new ArrayList<>();
        for (ClickEvent e : allEvents) {
            if (e.getEventType() == ClickEvent.TYPE_APP_OPEN && openTimes.size() < DETAIL_LIMIT) {
                openTimes.add(e.getCreateTime() == null ? "" : e.getCreateTime().toString());
            } else if (e.getEventType() == ClickEvent.TYPE_GOODS_VIEW && viewEvents.size() < DETAIL_LIMIT) {
                viewEvents.add(e);
            }
        }
        Map<Long, Goods> goodsMap = viewEvents.isEmpty() ? Map.of()
                : goodsMapper.selectBatchIds(viewEvents.stream().map(ClickEvent::getTargetId).distinct().toList())
                        .stream().collect(Collectors.toMap(Goods::getId, Function.identity(), (a, b) -> a));
        List<UserViewItem> views = viewEvents.stream()
                .map(e -> new UserViewItem(e.getTargetId(),
                        goodsName(goodsMap.get(e.getTargetId()), e.getTargetId()),
                        e.getBrand(), e.getPriceFen(),
                        e.getCreateTime() == null ? "" : e.getCreateTime().toString()))
                .toList();

        List<ClickEvent> allViews = clickEventMapper.selectList(new QueryWrapper<ClickEvent>()
                .eq("user_id", userId)
                .eq("event_type", ClickEvent.TYPE_GOODS_VIEW));
        Map<String, Long> brandDist = new LinkedHashMap<>();
        Map<String, Long> bandDist = new LinkedHashMap<>();
        for (String label : BAND_LABELS) {
            bandDist.put(label, 0L);
        }
        long priceSum = 0;
        long priceCount = 0;
        for (ClickEvent e : allViews) {
            if (StringUtils.hasText(e.getBrand())) {
                brandDist.merge(e.getBrand(), 1L, Long::sum);
            }
            if (e.getPriceFen() != null) {
                bandDist.merge(priceBand(e.getPriceFen()), 1L, Long::sum);
                priceSum += e.getPriceFen();
                priceCount++;
            }
        }
        sortMapByValueDesc(brandDist);

        UserAiProfile aiProfile = userAiProfileMapper.selectOne(new QueryWrapper<UserAiProfile>()
                .eq("user_id", userId)
                .last("limit 1"));

        return new UserProfileDetail(user.getId(), user.getNickname(), user.getAvatarUrl(), user.getPhone(),
                openTimes, views, brandDist, bandDist,
                saleOrderMapper.selectCount(new QueryWrapper<SaleOrder>().eq("user_id", userId)),
                recycleOrderMapper.selectCount(new QueryWrapper<RecycleOrder>().eq("user_id", userId)),
                repairOrderMapper.selectCount(new QueryWrapper<RepairOrder>().eq("user_id", userId)),
                aiProfile == null ? "" : aiProfile.getAnalysis(),
                aiProfile == null ? "" : aiProfile.getModel(),
                aiProfile == null || aiProfile.getUpdateTime() == null ? "" : aiProfile.getUpdateTime().toString());
    }

    // ===== 内部 =====

    /** 全量事件按用户聚合（启动数/浏览数/最近打开/偏好品牌/价格档），MVP 读时聚合。 */
    private Map<Long, UserAgg> aggregateByUser() {
        List<ClickEvent> events = clickEventMapper.selectList(new QueryWrapper<ClickEvent>()
                .in("event_type", List.of(ClickEvent.TYPE_APP_OPEN, ClickEvent.TYPE_GOODS_VIEW)));
        Map<Long, UserAgg> aggs = new LinkedHashMap<>();
        for (ClickEvent e : events) {
            if (e.getUserId() == null || e.getUserId() <= 0) {
                continue;
            }
            UserAgg agg = aggs.computeIfAbsent(e.getUserId(), k -> new UserAgg());
            if (e.getEventType() == ClickEvent.TYPE_APP_OPEN) {
                agg.opens++;
                if (e.getCreateTime() != null
                        && (agg.lastOpen == null || e.getCreateTime().isAfter(agg.lastOpen))) {
                    agg.lastOpen = e.getCreateTime();
                }
            } else {
                agg.views++;
                if (StringUtils.hasText(e.getBrand())) {
                    agg.brandCount.merge(e.getBrand(), 1L, Long::sum);
                }
                if (e.getPriceFen() != null) {
                    agg.bandCount.merge(priceBand(e.getPriceFen()), 1L, Long::sum);
                    agg.priceSum += e.getPriceFen();
                    agg.priceCount++;
                }
            }
        }
        for (UserAgg agg : aggs.values()) {
            agg.topBrand = topKey(agg.brandCount);
            agg.topPriceBand = topKey(agg.bandCount);
            agg.avgPriceFen = agg.priceCount > 0 ? agg.priceSum / agg.priceCount : null;
        }
        return aggs;
    }

    private static String priceBand(Long priceFen) {
        for (int i = 0; i < BAND_BOUNDS_FEN.length; i++) {
            if (priceFen < BAND_BOUNDS_FEN[i]) {
                return BAND_LABELS[i];
            }
        }
        return BAND_LABELS[BAND_LABELS.length - 1];
    }

    private static String topKey(Map<String, Long> counts) {
        String top = null;
        long max = 0;
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                top = entry.getKey();
            }
        }
        return top;
    }

    private static void sortMapByValueDesc(Map<String, Long> map) {
        List<Map.Entry<String, Long>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Map.Entry.<String, Long>comparingByValue().reversed());
        map.clear();
        for (Map.Entry<String, Long> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    private static String goodsName(Goods goods, Long goodsId) {
        return goods == null ? "已删除商品#" + goodsId : goods.getName();
    }

    private static String safe(String text) {
        return text == null ? "" : text;
    }

    /** 卡片上的 AI 摘要：取分析正文前 80 字。 */
    private static String preview(String analysis) {
        if (!StringUtils.hasText(analysis)) {
            return "";
        }
        String compact = analysis.trim();
        return compact.length() > 80 ? compact.substring(0, 80) : compact;
    }

    /** 单用户聚合可变体。 */
    private static final class UserAgg {
        private long opens;
        private long views;
        private LocalDateTime lastOpen;
        private final Map<String, Long> brandCount = new HashMap<>();
        private final Map<String, Long> bandCount = new HashMap<>();
        private long priceSum;
        private long priceCount;
        private String topBrand;
        private String topPriceBand;
        private Long avgPriceFen;
    }
}
