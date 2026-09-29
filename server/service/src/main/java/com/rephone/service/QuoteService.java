package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.BrandMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.QuoteRuleMapper;
import com.rephone.pojo.dto.BrandItem;
import com.rephone.pojo.dto.ModelItem;
import com.rephone.pojo.dto.QuoteCalculateRequest;
import com.rephone.pojo.dto.QuoteResult;
import com.rephone.pojo.entity.Brand;
import com.rephone.pojo.entity.PhoneModel;
import com.rephone.pojo.entity.QuoteRule;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 估价服务：基准价(元) × 成色系数 × 屏幕系数 − 故障扣减(元)，下限 0；
 * 返回小程序的金额统一换算为分（LONG，HALF_UP），见 AI_PLAN 硬约束 4。
 */
@Service
public class QuoteService {

    private final BrandMapper brandMapper;
    private final PhoneModelMapper modelMapper;
    private final QuoteRuleMapper ruleMapper;

    public QuoteService(BrandMapper brandMapper, PhoneModelMapper modelMapper, QuoteRuleMapper ruleMapper) {
        this.brandMapper = brandMapper;
        this.modelMapper = modelMapper;
        this.ruleMapper = ruleMapper;
    }

    public List<BrandItem> listBrands() {
        return brandMapper.selectList(new LambdaQueryWrapper<Brand>().orderByAsc(Brand::getSort))
                .stream()
                .map(b -> new BrandItem(b.getId(), b.getName(), b.getLogo()))
                .toList();
    }

    public List<ModelItem> listModels(Long brandId) {
        if (brandId == null) {
            throw new BizException(40010, "brandId 不能为空");
        }
        List<PhoneModel> models = modelMapper.selectList(new LambdaQueryWrapper<PhoneModel>()
                .eq(PhoneModel::getBrandId, brandId)
                .orderByAsc(PhoneModel::getSort));
        return models.stream().map(m -> new ModelItem(m.getId(), m.getName(), m.getImage(),
                m.getReleaseYear(), storageOptions(m.getId()))).toList();
    }

    public QuoteResult calculate(QuoteCalculateRequest req) {
        if (req == null || req.modelId() == null) {
            throw new BizException(40011, "modelId 不能为空");
        }
        if (!StringUtils.hasText(req.storage())) {
            throw new BizException(40012, "storage 不能为空");
        }
        if (!StringUtils.hasText(req.condition())) {
            throw new BizException(40013, "condition 不能为空");
        }
        PhoneModel model = modelMapper.selectById(req.modelId());
        if (model == null) {
            throw new BizException(40402, "机型不存在");
        }

        QuoteRule baseRule = ruleMapper.selectOne(new LambdaQueryWrapper<QuoteRule>()
                .eq(QuoteRule::getRuleType, QuoteRule.TYPE_BASE_PRICE)
                .eq(QuoteRule::getModelId, req.modelId())
                .eq(QuoteRule::getOptionKey, req.storage())
                .last("limit 1"));
        if (baseRule == null) {
            throw new BizException(40014, "该机型不支持所选内存");
        }

        QuoteRule condRule = ruleMapper.selectOne(new LambdaQueryWrapper<QuoteRule>()
                .eq(QuoteRule::getRuleType, QuoteRule.TYPE_CONDITION_FACTOR)
                .eq(QuoteRule::getOptionKey, req.condition())
                .last("limit 1"));
        if (condRule == null) {
            throw new BizException(40015, "成色选项无效");
        }

        // 屏幕状态为可选维度：未传视为无瑕疵（系数 1.0），与整机成色相乘
        BigDecimal screenFactor = BigDecimal.ONE;
        String screenLabel = null;
        if (StringUtils.hasText(req.screenCondition())) {
            QuoteRule screenRule = ruleMapper.selectOne(new LambdaQueryWrapper<QuoteRule>()
                    .eq(QuoteRule::getRuleType, QuoteRule.TYPE_SCREEN_FACTOR)
                    .eq(QuoteRule::getOptionKey, req.screenCondition())
                    .last("limit 1"));
            if (screenRule == null) {
                throw new BizException(40017, "屏幕状态选项无效");
            }
            screenFactor = screenRule.getNumericValue();
            screenLabel = screenRule.getOptionLabel();
        }

        BigDecimal deduction = BigDecimal.ZERO;
        List<String> issueLabels = List.of();
        if (!CollectionUtils.isEmpty(req.issues())) {
            Set<String> keys = new LinkedHashSet<>(req.issues());
            List<QuoteRule> issueRules = ruleMapper.selectList(new LambdaQueryWrapper<QuoteRule>()
                    .eq(QuoteRule::getRuleType, QuoteRule.TYPE_ISSUE_DEDUCTION)
                    .in(QuoteRule::getOptionKey, keys));
            if (issueRules.size() != keys.size()) {
                throw new BizException(40016, "存在无效的故障选项");
            }
            deduction = issueRules.stream()
                    .map(QuoteRule::getNumericValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            issueLabels = issueRules.stream().map(QuoteRule::getOptionLabel).toList();
        }

        BigDecimal price = baseRule.getNumericValue()
                .multiply(condRule.getNumericValue())
                .multiply(screenFactor)
                .subtract(deduction)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        long priceFen = price.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        Brand brand = brandMapper.selectById(model.getBrandId());
        return new QuoteResult(model.getId(), model.getName(),
                brand == null ? "" : brand.getName(),
                req.storage(), req.condition(), condRule.getOptionLabel(),
                screenLabel, issueLabels, priceFen);
    }

    private List<String> storageOptions(Long modelId) {
        return ruleMapper.selectList(new LambdaQueryWrapper<QuoteRule>()
                        .eq(QuoteRule::getRuleType, QuoteRule.TYPE_BASE_PRICE)
                        .eq(QuoteRule::getModelId, modelId)
                        .orderByAsc(QuoteRule::getSort))
                .stream()
                .map(QuoteRule::getOptionKey)
                .collect(Collectors.toList());
    }
}
