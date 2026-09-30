package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.entity.Goods;
import com.rephone.service.GoodsInspectionService;
import com.rephone.service.GoodsService;
import com.rephone.service.dto.GoodsInspectionView;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 出售商品（用户端，需登录）。P6 小程序商城接线用。 */
@RestController
@RequestMapping("/api/wx/goods")
public class WxGoodsController {

    private final GoodsService goodsService;
    private final GoodsInspectionService inspectionService;

    public WxGoodsController(GoodsService goodsService, GoodsInspectionService inspectionService) {
        this.goodsService = goodsService;
        this.inspectionService = inspectionService;
    }

    /** 在售库存：keyword 模糊匹配名称/品牌，brand 与 conditionLevel 精确匹配，sort=default|priceAsc|priceDesc */
    @GetMapping
    public R<List<Goods>> list(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String brand,
                               @RequestParam(required = false) String conditionLevel,
                               @RequestParam(required = false) String sort) {
        return R.ok(goodsService.listOnSale(keyword, brand, conditionLevel, sort));
    }

    /** 货架筛选条选项：在售商品的品牌与成色去重列表 */
    @GetMapping("/filters")
    public R<Map<String, List<String>>> filters() {
        return R.ok(goodsService.onSaleFilterOptions());
    }

    /** 商品质检报告；该商品尚无报告时 data 为 null，前端据此展示降级态 */
    @GetMapping("/{id}/inspection")
    public R<GoodsInspectionView> inspection(@PathVariable Long id) {
        return R.ok(inspectionService.getForUser(id));
    }

    @GetMapping("/{id}")
    public R<Goods> detail(@PathVariable Long id) {
        return R.ok(goodsService.getOnSale(id));
    }
}
