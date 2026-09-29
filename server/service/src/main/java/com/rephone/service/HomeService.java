package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.mapper.BrandMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.QuoteRuleMapper;
import com.rephone.pojo.dto.HomeModelItem;
import com.rephone.pojo.entity.Brand;
import com.rephone.pojo.entity.PhoneModel;
import com.rephone.pojo.entity.QuoteRule;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 首页数据：热门机型来自机型库实时数据（每品牌前 2 款、按后台排序，
 * 价格取该机型最贵内存档基准价），机型管理里的新增/改价即时反映到首页。
 */
@Service
public class HomeService {

    private static final int MODELS_PER_BRAND = 2;
    private static final int TOTAL_LIMIT = 8;

    private final BrandMapper brandMapper;
    private final PhoneModelMapper modelMapper;
    private final QuoteRuleMapper ruleMapper;

    public HomeService(BrandMapper brandMapper, PhoneModelMapper modelMapper, QuoteRuleMapper ruleMapper) {
        this.brandMapper = brandMapper;
        this.modelMapper = modelMapper;
        this.ruleMapper = ruleMapper;
    }

    public List<HomeModelItem> hotModels() {
        List<Brand> brands = brandMapper.selectList(new LambdaQueryWrapper<Brand>().orderByAsc(Brand::getSort));
        if (brands.isEmpty()) {
            return List.of();
        }
        Map<Long, List<PhoneModel>> modelsByBrand = modelMapper.selectList(
                        new LambdaQueryWrapper<PhoneModel>().orderByAsc(PhoneModel::getSort))
                .stream()
                .collect(Collectors.groupingBy(PhoneModel::getBrandId));

        // 按品牌顺序、每品牌取前 2 款，保持 picked 顺序即展示顺序
        List<PhoneModel> picked = new ArrayList<>();
        for (Brand brand : brands) {
            modelsByBrand.getOrDefault(brand.getId(), List.of()).stream()
                    .limit(MODELS_PER_BRAND)
                    .forEach(picked::add);
            if (picked.size() >= TOTAL_LIMIT) {
                break;
            }
        }
        if (picked.isEmpty()) {
            return List.of();
        }

        Map<Long, BigDecimal> maxPriceByModel = ruleMapper.selectList(new LambdaQueryWrapper<QuoteRule>()
                        .eq(QuoteRule::getRuleType, QuoteRule.TYPE_BASE_PRICE)
                        .in(QuoteRule::getModelId, picked.stream().map(PhoneModel::getId).toList()))
                .stream()
                .collect(Collectors.toMap(QuoteRule::getModelId, QuoteRule::getNumericValue,
                        (a, b) -> a.max(b)));
        Map<Long, Brand> brandById = brands.stream()
                .collect(Collectors.toMap(Brand::getId, b -> b, (a, b) -> a));

        List<HomeModelItem> result = new ArrayList<>();
        for (PhoneModel model : picked) {
            BigDecimal maxYuan = maxPriceByModel.get(model.getId());
            Brand brand = brandById.get(model.getBrandId());
            result.add(new HomeModelItem(model.getBrandId(),
                    brand == null ? "" : brand.getName(),
                    model.getName(),
                    model.getImage() == null ? (brand == null ? null : brand.getLogo()) : model.getImage(),
                    maxYuan == null ? 0L
                            : maxYuan.multiply(BigDecimal.valueOf(100))
                            .setScale(0, RoundingMode.HALF_UP).longValueExact()));
        }
        return result;
    }
}
