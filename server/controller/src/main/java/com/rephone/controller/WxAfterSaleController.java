package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.entity.AfterSale;
import com.rephone.service.AfterSaleService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 售后单（用户端，需登录）。 */
@RestController
@RequestMapping("/api/wx/after-sale")
public class WxAfterSaleController {

    private final AfterSaleService afterSaleService;

    public WxAfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @GetMapping("/list")
    public R<Page<AfterSale>> list(@RequestParam(required = false) Integer status,
                                   @RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(afterSaleService.listMine(status, pageNum, pageSize));
    }

    @PutMapping("/{asNo}/cancel")
    public R<Void> cancel(@PathVariable String asNo, @RequestBody(required = false) Map<String, String> body) {
        afterSaleService.cancel(asNo, body == null ? null : body.get("reason"));
        return R.ok();
    }
}
