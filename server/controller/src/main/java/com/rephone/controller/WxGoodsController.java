package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.entity.Goods;
import com.rephone.service.GoodsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 出售商品（用户端，需登录）。P6 小程序商城接线用。 */
@RestController
@RequestMapping("/api/wx/goods")
public class WxGoodsController {

    private final GoodsService goodsService;

    public WxGoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping
    public R<List<Goods>> list() {
        return R.ok(goodsService.listOnSale());
    }

    @GetMapping("/{id}")
    public R<Goods> detail(@PathVariable Long id) {
        return R.ok(goodsService.getOnSale(id));
    }
}
