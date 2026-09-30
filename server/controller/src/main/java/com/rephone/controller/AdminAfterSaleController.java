package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.entity.AfterSale;
import com.rephone.service.AfterSaleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端售后管理（仅退款审核）。 */
@RestController
@RequestMapping("/api/admin/after-sales")
public class AdminAfterSaleController {

    private final AfterSaleService afterSaleService;

    public AdminAfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @GetMapping
    public R<Page<AfterSale>> list(@RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(afterSaleService.adminPage(status, keyword, pageNum, pageSize));
    }

    @GetMapping("/{asNo}")
    public R<AfterSale> detail(@PathVariable String asNo) {
        return R.ok(afterSaleService.adminDetail(asNo));
    }

    /** 同意退款：mock 退款到账并联动出售订单置为已退款。 */
    @PutMapping("/{asNo}/agree")
    public R<Void> agree(@PathVariable String asNo, @RequestBody(required = false) java.util.Map<String, String> body) {
        afterSaleService.agree(asNo, body == null ? null : body.get("adminRemark"));
        return R.ok();
    }

    @PutMapping("/{asNo}/reject")
    public R<Void> reject(@PathVariable String asNo, @RequestBody(required = false) java.util.Map<String, String> body) {
        afterSaleService.reject(asNo, body == null ? null : body.get("adminRemark"));
        return R.ok();
    }
}
