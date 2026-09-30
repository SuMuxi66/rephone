package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.GoodsMapper;
import com.rephone.pojo.entity.Goods;
import com.rephone.service.dto.GoodsAttrs;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 出售商品：管理端 CRUD（金额元↔分）+ 用户端上架列表。 */
@Service
public class GoodsService {

    private static final long MAX_PRICE_FEN = 20_000_000L;

    private final GoodsMapper goodsMapper;

    public GoodsService(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    // ===== 用户端 =====

    /**
     * 在售（上架且有货）商品列表。
     * 入参均可空：keyword 模糊匹配商品名/品牌，brand 与 conditionLevel 精确匹配，sort 见 {@link #applySort}。
     * 暂不分页（前端分批渲染），库存上量后再改为 Page（需同步调整 P6SaleOrderTest 断言）。
     */
    public List<Goods> listOnSale(String keyword, String brand, String conditionLevel, String sort) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, 1)
                .gt(Goods::getStock, 0);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Goods::getName, kw).or().like(Goods::getBrand, kw));
        }
        if (StringUtils.hasText(brand)) {
            wrapper.eq(Goods::getBrand, brand.trim());
        }
        if (StringUtils.hasText(conditionLevel)) {
            wrapper.eq(Goods::getConditionLevel, conditionLevel.trim());
        }
        applySort(wrapper, sort);
        return goodsMapper.selectList(wrapper);
    }

    /** sort：default/newest=最新优先；priceAsc/priceDesc=按售价。未知值回落为 default。 */
    private void applySort(LambdaQueryWrapper<Goods> wrapper, String sort) {
        String s = sort == null ? "" : sort.trim();
        switch (s) {
            case "priceAsc" -> wrapper.orderByAsc(Goods::getPriceFen);
            case "priceDesc" -> wrapper.orderByDesc(Goods::getPriceFen);
            default -> wrapper.orderByDesc(Goods::getId);
        }
    }

    /** 在售（有货）商品的品牌与成色去重选项，驱动小程序货架筛选条。 */
    public Map<String, List<String>> onSaleFilterOptions() {
        List<Goods> onSale = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, 1)
                .gt(Goods::getStock, 0));
        Set<String> brandSet = new TreeSet<>();
        Set<String> conditionSet = new TreeSet<>();
        for (Goods goods : onSale) {
            if (goods == null) {
                continue;
            }
            if (StringUtils.hasText(goods.getBrand())) {
                brandSet.add(goods.getBrand().trim());
            }
            if (StringUtils.hasText(goods.getConditionLevel())) {
                conditionSet.add(goods.getConditionLevel().trim());
            }
        }
        return Map.of("brands", new ArrayList<>(brandSet), "conditions", new ArrayList<>(conditionSet));
    }

    public Goods getOnSale(Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null || goods.getStatus() != 1) {
            throw new BizException(40407, "商品不存在或已下架");
        }
        return goods;
    }

    // ===== 管理端 =====

    public Page<Goods> adminPage(String keyword, Integer status, long pageNum, long pageSize) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>().orderByDesc(Goods::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Goods::getName, keyword.trim());
        }
        if (status != null && (status == 0 || status == 1)) {
            wrapper.eq(Goods::getStatus, status);
        }
        return goodsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    public Long create(String name, String image, BigDecimal priceYuan, BigDecimal originalYuan,
                       Integer stock, String descText, GoodsAttrs attrs) {
        validateName(name);
        long priceFen = validatePrice(priceYuan);
        Long originalFen = null;
        if (originalYuan != null) {
            originalFen = toFen(originalYuan, "原价");
        }
        Goods goods = new Goods();
        goods.setTenantId(0L);
        goods.setName(name.trim());
        goods.setImage(StringUtils.hasText(image) ? image.trim() : null);
        goods.setPriceFen(priceFen);
        goods.setOriginalPriceFen(originalFen);
        goods.setStock(stock == null ? 0 : Math.max(stock, 0));
        goods.setStatus(1);
        goods.setDescText(StringUtils.hasText(descText) ? descText.trim() : null);
        applyAttrs(goods, attrs);
        goodsMapper.insert(goods);
        return goods.getId();
    }

    public void update(Long id, String name, String image, BigDecimal priceYuan,
                       BigDecimal originalYuan, Integer stock, String descText, GoodsAttrs attrs) {
        Goods goods = requireGoods(id);
        if (StringUtils.hasText(name)) {
            validateName(name);
            goods.setName(name.trim());
        }
        if (image != null) {
            goods.setImage(image.isBlank() ? null : image.trim());
        }
        if (priceYuan != null) {
            goods.setPriceFen(toFen(priceYuan, "售价"));
        }
        if (originalYuan != null) {
            goods.setOriginalPriceFen(originalYuan.signum() <= 0 ? null : toFen(originalYuan, "原价"));
        }
        if (stock != null) {
            goods.setStock(Math.max(stock, 0));
        }
        if (descText != null) {
            goods.setDescText(descText.isBlank() ? null : descText.trim());
        }
        applyAttrs(goods, attrs);
        goodsMapper.updateById(goods);
    }

    public void setStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BizException(40054, "status 仅允许 0/1");
        }
        Goods goods = requireGoods(id);
        goods.setStatus(status);
        goodsMapper.updateById(goods);
    }

    public Goods requireGoods(Long id) {
        Goods goods = id == null ? null : goodsMapper.selectById(id);
        if (goods == null) {
            throw new BizException(40408, "商品不存在");
        }
        return goods;
    }

    /** CAS 扣库存：下单占用。 */
    public void deductStock(Long goodsId, int quantity) {
        int updated = goodsMapper.update(null, new LambdaUpdateWrapper<Goods>()
                .eq(Goods::getId, goodsId)
                .ge(Goods::getStock, quantity)
                .setSql("stock = stock - " + quantity));
        if (updated != 1) {
            throw new BizException(40066, "库存不足");
        }
    }

    /** 回补库存：取消订单。 */
    public void restoreStock(Long goodsId, int quantity) {
        goodsMapper.update(null, new LambdaUpdateWrapper<Goods>()
                .eq(Goods::getId, goodsId)
                .setSql("stock = stock + " + quantity));
    }

    /**
     * 机型属性写入。管理端表单整表提交，故采用「整体覆盖」语义：传 null 即清空该属性。
     */
    private void applyAttrs(Goods goods, GoodsAttrs attrs) {
        GoodsAttrs a = attrs == null ? GoodsAttrs.empty() : attrs;
        checkAttrLen(a.brand(), 32, "品牌");
        checkAttrLen(a.storage(), 16, "内存");
        checkAttrLen(a.conditionLevel(), 16, "成色");
        checkAttrLen(a.tags(), 255, "标签");
        goods.setBrand(a.brand());
        goods.setStorage(a.storage());
        goods.setConditionLevel(a.conditionLevel());
        goods.setTags(a.tags());
    }

    private void checkAttrLen(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new BizException(40073, label + "不能超过 " + max + " 字");
        }
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name) || name.trim().isEmpty() || name.length() > 128) {
            throw new BizException(40067, "商品名不能为空且不超过 128 字");
        }
    }

    private long validatePrice(BigDecimal priceYuan) {
        long fen = toFen(priceYuan, "售价");
        if (fen <= 0 || fen > MAX_PRICE_FEN) {
            throw new BizException(40068, "售价须为正且不超过 20 万元");
        }
        return fen;
    }

    private long toFen(BigDecimal yuan, String label) {
        if (yuan == null) {
            throw new BizException(40068, label + "无效");
        }
        return yuan.multiply(BigDecimal.valueOf(100))
                .setScale(0, java.math.RoundingMode.HALF_UP)
                .longValueExact();
    }
}
