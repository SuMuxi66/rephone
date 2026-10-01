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
 * 首页数据：回收行情来自机型库实时数据——只展示有基准价的机型（零元/无价机型不上首页），
 * 按最高基准价全库降序取前 8（高价值热门），机型管理里的新增/改价即时反映到首页。
 */
@Service
public class HomeService {

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

        // 全库基准价：rule_type=10 按 model_id 取最贵内存档
        Map<Long, BigDecimal> maxPriceByModel = ruleMapper.selectList(new LambdaQueryWrapper<QuoteRule>()
                        .eq(QuoteRule::getRuleType, QuoteRule.TYPE_BASE_PRICE))
                .stream()
                .collect(Collectors.toMap(QuoteRule::getModelId, QuoteRule::getNumericValue,
                        (a, b) -> a.max(b)));
        if (maxPriceByModel.isEmpty()) {
            return List.of();
        }

        Map<Long, Brand> brandById = brands.stream()
                .collect(Collectors.toMap(Brand::getId, b -> b, (a, b) -> a));

        // 有价的机型按价格降序取前 N（高价值热门）；同价次序稳定（再按 id 升序）
        List<PhoneModel> priced = modelMapper.selectList(new LambdaQueryWrapper<PhoneModel>()
                        .in(PhoneModel::getId, maxPriceByModel.keySet()))
                .stream()
                .filter(m -> brandById.containsKey(m.getBrandId()))
                .sorted((a, b) -> {
                    int byPrice = maxPriceByModel.get(b.getId())
                            .compareTo(maxPriceByModel.get(a.getId()));
                    return byPrice != 0 ? byPrice : Long.compare(a.getId(), b.getId());
                })
                .limit(TOTAL_LIMIT)
                .toList();

        List<HomeModelItem> result = new ArrayList<>();
        for (PhoneModel model : priced) {
            Brand brand = brandById.get(model.getBrandId());
            long maxFen = maxPriceByModel.get(model.getId())
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();
            result.add(new HomeModelItem(model.getBrandId(),
                    brand == null ? "" : brand.getName(),
                    model.getName(),
                    model.getImage() == null ? (brand == null ? null : brand.getLogo()) : model.getImage(),
                    maxFen));
        }
        return result;
    }
}
