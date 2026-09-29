package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.ExpressFillRequest;
import com.rephone.pojo.dto.RecycleOrderCreateRequest;
import com.rephone.pojo.dto.RecycleOrderDetail;
import com.rephone.pojo.dto.RecycleOrderItem;
import com.rephone.service.RecycleOrderService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 回收订单（用户端，需登录）。 */
@RestController
@RequestMapping("/api/wx/recycle")
public class WxRecycleOrderController {

    private final RecycleOrderService orderService;

    public WxRecycleOrderController(RecycleOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public R<Map<String, String>> create(@RequestBody RecycleOrderCreateRequest request) {
        return R.ok(Map.of("orderNo", orderService.create(request)));
    }

    @GetMapping("/orders")
    public R<Page<RecycleOrderItem>> list(@RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(orderService.list(status, pageNum, pageSize));
    }

    @GetMapping("/order/{orderNo}")
    public R<RecycleOrderDetail> detail(@PathVariable String orderNo) {
        return R.ok(orderService.detailWithInspections(orderNo));
    }

    @PutMapping("/order/{orderNo}/express")
    public R<Void> fillExpress(@PathVariable String orderNo, @RequestBody ExpressFillRequest request) {
        orderService.fillExpress(orderNo, request);
        return R.ok();
    }

    @PutMapping("/order/{orderNo}/cancel")
    public R<Void> cancel(@PathVariable String orderNo,
                          @RequestBody(required = false) Map<String, String> body) {
        orderService.cancel(orderNo, body == null ? null : body.get("reason"));
        return R.ok();
    }

    /** 确认打款：质检待确认（40）→ 触发企业付款 → 已打款（50）。 */
    @PutMapping("/order/{orderNo}/confirm")
    public R<Void> confirm(@PathVariable String orderNo) {
        orderService.confirmPayout(orderNo);
        return R.ok();
    }
}
