package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.GoodsInspectionItemMapper;
import com.rephone.mapper.GoodsInspectionMapper;
import com.rephone.pojo.entity.Goods;
import com.rephone.pojo.entity.GoodsInspection;
import com.rephone.pojo.entity.GoodsInspectionItem;
import com.rephone.service.dto.GoodsInspectionSaveRequest;
import com.rephone.service.dto.GoodsInspectionView;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 商品质检报告（出售端）：管理端整体覆盖式保存，用户端只读。
 * 无报告时读取返回 {@code null}，由前端决定降级展示。
 */
@Service
public class GoodsInspectionService {

    /** 视为「无异常」的结论文案，其余一律计入异常项。 */
    private static final String NORMAL = "正常";

    private static final String DEFAULT_CATEGORY = "其他";
    private static final int MAX_ITEMS = 60;
    private static final int MAX_IMAGES = 9;
    private static final int ERR = 40074;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter[] TIME_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
    };

    private final GoodsInspectionMapper reportMapper;
    private final GoodsInspectionItemMapper itemMapper;
    private final GoodsService goodsService;

    public GoodsInspectionService(GoodsInspectionMapper reportMapper,
                                  GoodsInspectionItemMapper itemMapper,
                                  GoodsService goodsService) {
        this.reportMapper = reportMapper;
        this.itemMapper = itemMapper;
        this.goodsService = goodsService;
    }

    // ===== 用户端 =====

    /** 上架商品的质检报告；无报告返回 null。 */
    public GoodsInspectionView getForUser(Long goodsId) {
        return buildView(goodsService.getOnSale(goodsId));
    }

    // ===== 管理端 =====

    /** 任意存在商品的质检报告（含下架）；无报告返回 null。 */
    public GoodsInspectionView getForAdmin(Long goodsId) {
        return buildView(goodsService.requireGoods(goodsId));
    }

    /** 整体覆盖保存；items 为空视为清空报告。 */
    @Transactional
    public void save(Long goodsId, GoodsInspectionSaveRequest req) {
        goodsService.requireGoods(goodsId);
        List<GoodsInspectionSaveRequest.Item> items = normalizeItems(req);

        GoodsInspection report = findByGoods(goodsId);
        if (items.isEmpty()) {
            clear(goodsId, report);
            return;
        }

        boolean creating = report == null;
        if (creating) {
            report = new GoodsInspection();
            report.setTenantId(0L);
            report.setGoodsId(goodsId);
            report.setReportNo(nextReportNo());
        }
        report.setInspector(trimToNull(req.inspector()));
        LocalDateTime at = parseTime(req.inspectedAt());
        if (at == null) {
            at = report.getInspectedAt() == null ? LocalDateTime.now() : report.getInspectedAt();
        }
        report.setInspectedAt(at);
        report.setBatteryHealth(validateBattery(req.batteryHealth()));
        report.setSummary(limited(req.summary(), 512, "质检结论"));
        report.setImages(joinImages(req.images()));
        if (creating) {
            reportMapper.insert(report);
        } else {
            reportMapper.updateById(report);
        }

        // 检查项整体覆盖：先删后插，保证顺序与内容一致
        itemMapper.delete(new LambdaQueryWrapper<GoodsInspectionItem>()
                .eq(GoodsInspectionItem::getGoodsId, goodsId));
        int sort = 0;
        for (GoodsInspectionSaveRequest.Item it : items) {
            GoodsInspectionItem row = new GoodsInspectionItem();
            row.setTenantId(0L);
            row.setGoodsId(goodsId);
            row.setCategory(it.category());
            row.setItemName(it.name());
            row.setItemResult(it.result());
            row.setItemNote(it.note());
            row.setSortNo(sort++);
            itemMapper.insert(row);
        }
    }

    // ===== 内部 =====

    private void clear(Long goodsId, GoodsInspection report) {
        if (report != null) {
            reportMapper.deleteById(report.getId());
        }
        itemMapper.delete(new LambdaQueryWrapper<GoodsInspectionItem>()
                .eq(GoodsInspectionItem::getGoodsId, goodsId));
    }

    private GoodsInspection findByGoods(Long goodsId) {
        return reportMapper.selectOne(new LambdaQueryWrapper<GoodsInspection>()
                .eq(GoodsInspection::getGoodsId, goodsId)
                .last("limit 1"));
    }

    private GoodsInspectionView buildView(Goods goods) {
        GoodsInspection report = findByGoods(goods.getId());
        if (report == null) {
            return null;
        }
        List<GoodsInspectionItem> rows = itemMapper.selectList(new LambdaQueryWrapper<GoodsInspectionItem>()
                .eq(GoodsInspectionItem::getGoodsId, goods.getId())
                .orderByAsc(GoodsInspectionItem::getSortNo)
                .orderByAsc(GoodsInspectionItem::getId));
        List<GoodsInspectionView.Item> items = new ArrayList<>();
        int abnormal = 0;
        for (GoodsInspectionItem row : rows) {
            if (row == null) {
                continue;
            }
            if (!NORMAL.equals(row.getItemResult())) {
                abnormal++;
            }
            items.add(new GoodsInspectionView.Item(row.getCategory(), row.getItemName(),
                    row.getItemResult(), row.getItemNote()));
        }
        return new GoodsInspectionView(
                goods.getConditionLevel(),
                report.getReportNo(),
                report.getInspector(),
                report.getInspectedAt(),
                report.getBatteryHealth(),
                report.getSummary(),
                splitImages(report.getImages()),
                items,
                items.size() - abnormal,
                abnormal);
    }

    private List<GoodsInspectionSaveRequest.Item> normalizeItems(GoodsInspectionSaveRequest req) {
        List<GoodsInspectionSaveRequest.Item> out = new ArrayList<>();
        if (req == null || req.items() == null) {
            return out;
        }
        if (req.items().size() > MAX_ITEMS) {
            throw new BizException(ERR, "检查项不能超过 " + MAX_ITEMS + " 条");
        }
        for (GoodsInspectionSaveRequest.Item raw : req.items()) {
            if (raw == null) {
                continue;
            }
            String name = limited(raw.name(), 32, "检查项名");
            String result = limited(raw.result(), 32, "检查结论");
            if (name == null || result == null) {
                continue;
            }
            String category = limited(raw.category(), 16, "检查分组");
            String note = limited(raw.note(), 128, "检查说明");
            out.add(new GoodsInspectionSaveRequest.Item(
                    category == null ? DEFAULT_CATEGORY : category, name, result, note));
        }
        return out;
    }

    private Integer validateBattery(Integer value) {
        if (value == null) {
            return null;
        }
        if (value < 0 || value > 100) {
            throw new BizException(ERR, "电池健康度须为 0-100");
        }
        return value;
    }

    private String joinImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        Set<String> set = new LinkedHashSet<>();
        for (String img : images) {
            String value = limited(img, 512, "报告图片");
            if (value == null) {
                continue;
            }
            set.add(value);
            if (set.size() >= MAX_IMAGES) {
                break;
            }
        }
        return set.isEmpty() ? null : String.join(",", set);
    }

    private List<String> splitImages(String joined) {
        List<String> out = new ArrayList<>();
        if (!StringUtils.hasText(joined)) {
            return out;
        }
        for (String part : joined.split(",")) {
            if (StringUtils.hasText(part)) {
                out.add(part.trim());
            }
        }
        return out;
    }

    private LocalDateTime parseTime(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String value = raw.trim();
        for (DateTimeFormatter formatter : TIME_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (RuntimeException ignored) {
                // 尝试下一种格式
            }
        }
        try {
            return LocalDate.parse(value).atStartOfDay();
        } catch (RuntimeException ignored) {
            throw new BizException(ERR, "质检时间格式不正确");
        }
    }

    /** 校验长度并 trim；空白返回 null。 */
    private String limited(String value, int max, String label) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        if (trimmed.length() > max) {
            throw new BizException(ERR, label + "不能超过 " + max + " 字");
        }
        return trimmed;
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String nextReportNo() {
        return "Q" + LocalDateTime.now().format(TS) + String.format("%08d", RANDOM.nextInt(100000000));
    }
}
