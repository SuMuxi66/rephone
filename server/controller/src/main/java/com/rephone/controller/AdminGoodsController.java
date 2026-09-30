package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.entity.Goods;
import com.rephone.service.GoodsService;
import com.rephone.service.dto.GoodsAttrs;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端商品管理（上架/下架/编辑，金额元↔分）。 */
@RestController
@RequestMapping("/api/admin/goods")
public class AdminGoodsController {

    private final GoodsService goodsService;

    public AdminGoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping
    public R<Page<Goods>> list(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(defaultValue = "1") long pageNum,
                               @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(goodsService.adminPage(keyword, status, pageNum, pageSize));
    }

    @PostMapping
    public R<Map<String, Long>> create(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(Map.of("goodsId", goodsService.create(
                str(body, "name"), str(body, "image"),
                decimal(body, "priceYuan"), decimal(body, "originalPriceYuan"),
                intOf(body, "stock"), str(body, "descText"), attrs(body))));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        goodsService.update(id, str(body, "name"), str(body, "image"),
                decimal(body, "priceYuan"), decimal(body, "originalPriceYuan"),
                intOf(body, "stock"), str(body, "descText"), attrs(body));
        return R.ok();
    }

    @PutMapping("/{id}/status")
    public R<Void> setStatus(@PathVariable Long id, @RequestBody(required = false) Map<String, Integer> body) {
        goodsService.setStatus(id, body == null ? null : body.get("status"));
        return R.ok();
    }

    private static GoodsAttrs attrs(Map<String, Object> body) {
        return GoodsAttrs.of(str(body, "brand"), str(body, "storage"),
                str(body, "conditionLevel"), str(body, "tags"));
    }

    private static String str(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static Integer intOf(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : Integer.valueOf(String.valueOf(v));
    }

    private static BigDecimal decimal(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : new BigDecimal(String.valueOf(v));
    }
}
