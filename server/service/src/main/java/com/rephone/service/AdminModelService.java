package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.BrandMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.QuoteRuleMapper;
import com.rephone.pojo.dto.AdminBrandItem;
import com.rephone.pojo.dto.AdminModelCreateRequest;
import com.rephone.pojo.dto.AdminModelItem;
import com.rephone.pojo.entity.Brand;
import com.rephone.pojo.entity.PhoneModel;
import com.rephone.pojo.entity.QuoteRule;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 管理端机型管理：品牌、机型、各内存基准价（quote_rule rule_type=10，单位元）。
 * 不提供删除（机型被订单快照/维修价目引用），只允许新增与编辑。
 */
@Service
public class AdminModelService {

    private static final BigDecimal MAX_BASE_PRICE = new BigDecimal("200000");

    private final BrandMapper brandMapper;
    private final PhoneModelMapper modelMapper;
    private final QuoteRuleMapper ruleMapper;

    public AdminModelService(BrandMapper brandMapper, PhoneModelMapper modelMapper, QuoteRuleMapper ruleMapper) {
        this.brandMapper = brandMapper;
        this.modelMapper = modelMapper;
        this.ruleMapper = ruleMapper;
    }

    public List<AdminBrandItem> brands() {
        List<Brand> brands = brandMapper.selectList(new LambdaQueryWrapper<Brand>().orderByAsc(Brand::getSort));
        Map<Long, Long> counts = modelMapper.selectList(new LambdaQueryWrapper<PhoneModel>()).stream()
                .collect(Collectors.groupingBy(PhoneModel::getBrandId, Collectors.counting()));
        return brands.stream()
                .map(b -> new AdminBrandItem(b.getId(), b.getName(), counts.getOrDefault(b.getId(), 0L)))
                .toList();
    }

    @Transactional
    public Long createBrand(String name) {
        requireLen(name, 64, "品牌名");
        Long exists = brandMapper.selectCount(new LambdaQueryWrapper<Brand>().eq(Brand::getName, name.trim()));
        if (exists != null && exists > 0) {
            throw new BizException(40050, "品牌已存在");
        }
        Brand brand = new Brand();
        brand.setTenantId(0L);
        brand.setName(name.trim());
        brand.setSort((int) (brandMapper.selectCount(null) + 1));
        brandMapper.insert(brand);
        return brand.getId();
    }

    public List<AdminModelItem> models(Long brandId) {
        if (brandId == null) {
            throw new BizException(40030, "brandId 不能为空");
        }
        List<PhoneModel> models = modelMapper.selectList(new LambdaQueryWrapper<PhoneModel>()
                .eq(PhoneModel::getBrandId, brandId)
                .orderByAsc(PhoneModel::getSort));
        Map<Long, List<QuoteRule>> pricesByModel = models.isEmpty() ? Map.of()
                : ruleMapper.selectList(new LambdaQueryWrapper<QuoteRule>()
                        .eq(QuoteRule::getRuleType, QuoteRule.TYPE_BASE_PRICE)
                        .in(QuoteRule::getModelId, models.stream().map(PhoneModel::getId).toList()))
                .stream()
                .collect(Collectors.groupingBy(QuoteRule::getModelId));
        return models.stream().map(m -> new AdminModelItem(m.getId(), m.getName(), m.getReleaseYear(),
                pricesByModel.getOrDefault(m.getId(), List.of()).stream()
                        .sorted(Comparator.comparing(QuoteRule::getSort))
                        .map(r -> new AdminModelItem.AdminModelPrice(r.getOptionKey(), r.getNumericValue()))
                        .toList())).toList();
    }

    @Transactional
    public Long createModel(AdminModelCreateRequest req) {
        if (req == null || req.brandId() == null) {
            throw new BizException(40030, "brandId 不能为空");
        }
        requireLen(req.name(), 128, "机型名");
        if (brandMapper.selectById(req.brandId()) == null) {
            throw new BizException(40405, "品牌不存在");
        }
        Long exists = modelMapper.selectCount(new LambdaQueryWrapper<PhoneModel>()
                .eq(PhoneModel::getBrandId, req.brandId())
                .eq(PhoneModel::getName, req.name().trim()));
        if (exists != null && exists > 0) {
            throw new BizException(40051, "该品牌下机型已存在");
        }
        List<AdminModelItem.AdminModelPrice> prices = req.prices();
        if (prices == null || prices.isEmpty()) {
            throw new BizException(40052, "请至少配置一档内存基准价");
        }
        prices.forEach(p -> validatePrice(p.storage(), p.priceYuan()));

        PhoneModel model = new PhoneModel();
        model.setTenantId(0L);
        model.setBrandId(req.brandId());
        model.setName(req.name().trim());
        model.setReleaseYear(req.releaseYear());
        Long count = modelMapper.selectCount(new LambdaQueryWrapper<PhoneModel>()
                .eq(PhoneModel::getBrandId, req.brandId()));
        model.setSort(count == null ? 1 : count.intValue() + 1);
        modelMapper.insert(model);
        upsertPrices(model.getId(), prices);
        return model.getId();
    }

    @Transactional
    public void updateModel(Long id, String name, Integer releaseYear) {
        PhoneModel model = requireModel(id);
        if (StringUtils.hasText(name)) {
            requireLen(name, 128, "机型名");
            Long exists = modelMapper.selectCount(new LambdaQueryWrapper<PhoneModel>()
                    .eq(PhoneModel::getBrandId, model.getBrandId())
                    .eq(PhoneModel::getName, name.trim())
                    .ne(PhoneModel::getId, id));
            if (exists != null && exists > 0) {
                throw new BizException(40051, "该品牌下机型已存在");
            }
            model.setName(name.trim());
        }
        if (releaseYear != null) {
            model.setReleaseYear(releaseYear);
        }
        modelMapper.updateById(model);
    }

    /** 单档内存基准价 upsert：存在则改价，不存在则新增该内存档。 */
    @Transactional
    public void updatePrice(Long modelId, String storage, BigDecimal priceYuan) {
        requireModel(modelId);
        validatePrice(storage, priceYuan);
        upsertPrices(modelId, List.of(new AdminModelItem.AdminModelPrice(storage, priceYuan)));
    }

    private void upsertPrices(Long modelId, List<AdminModelItem.AdminModelPrice> prices) {
        Map<String, QuoteRule> existing = ruleMapper.selectList(new LambdaQueryWrapper<QuoteRule>()
                        .eq(QuoteRule::getRuleType, QuoteRule.TYPE_BASE_PRICE)
                        .eq(QuoteRule::getModelId, modelId))
                .stream()
                .collect(Collectors.toMap(QuoteRule::getOptionKey, Function.identity(), (a, b) -> a));
        int sort = existing.size();
        for (AdminModelItem.AdminModelPrice p : prices) {
            QuoteRule rule = existing.get(p.storage());
            if (rule == null) {
                rule = new QuoteRule();
                rule.setTenantId(0L);
                rule.setRuleType(QuoteRule.TYPE_BASE_PRICE);
                rule.setModelId(modelId);
                rule.setOptionKey(p.storage());
                rule.setOptionLabel(p.storage());
                rule.setNumericValue(p.priceYuan());
                rule.setSort(++sort);
                ruleMapper.insert(rule);
            } else {
                rule.setNumericValue(p.priceYuan());
                ruleMapper.updateById(rule);
            }
        }
    }

    private void validatePrice(String storage, BigDecimal priceYuan) {
        requireLen(storage, 32, "内存规格");
        if (priceYuan == null || priceYuan.signum() <= 0 || priceYuan.compareTo(MAX_BASE_PRICE) > 0) {
            throw new BizException(40053, "基准价须为正且不超过 200000 元");
        }
    }

    private PhoneModel requireModel(Long id) {
        PhoneModel model = id == null ? null : modelMapper.selectById(id);
        if (model == null) {
            throw new BizException(40402, "机型不存在");
        }
        return model;
    }

    private void requireLen(String value, int max, String label) {
        if (!StringUtils.hasText(value) || value.trim().isEmpty()) {
            throw new BizException(40059, label + "不能为空");
        }
        if (value.length() > max) {
            throw new BizException(40039, label + "长度不能超过 " + max + " 字");
        }
    }
}
