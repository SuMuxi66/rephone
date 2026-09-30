package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.entity.SaleOrder;
import com.rephone.service.SaleOrderService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端售出管理（发货/退款/备注）。 */
@RestController
@RequestMapping("/api/admin/sale")
public class AdminSaleController {

    private final SaleOrderService orderService;

    public AdminSaleController(SaleOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public R<Page<SaleOrder>> list(@RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(orderService.adminPage(status, keyword, pageNum, pageSize));
    }

    @GetMapping("/order/{orderNo}")
    public R<SaleOrder> detail(@PathVariable String orderNo) {
        return R.ok(orderService.adminDetail(orderNo));
    }

    @PutMapping("/order/{orderNo}/ship")
    public R<Void> ship(@PathVariable String orderNo, @RequestBody(required = false) Map<String, String> body) {
        orderService.ship(orderNo,
                body == null ? null : body.get("expressCompany"),
                body == null ? null : body.get("expressNo"),
                body == null ? null : body.get("remark"));
        return R.ok();
    }

    @PostMapping("/order/{orderNo}/refund")
    public R<Void> refund(@PathVariable String orderNo, @RequestBody(required = false) Map<String, String> body) {
        orderService.refund(orderNo, null,
                body == null ? null : body.get("reason"), 20, 0L);
        return R.ok();
    }
}
