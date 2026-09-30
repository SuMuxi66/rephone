package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.entity.SaleOrder;
import com.rephone.service.AfterSaleService;
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

/** 出售订单（用户端，需登录）。支付为 mock，真实微信支付 P6 联调替换。 */
@RestController
@RequestMapping("/api/wx/sale")
public class WxSaleOrderController {

    private final SaleOrderService orderService;
    private final AfterSaleService afterSaleService;

    public WxSaleOrderController(SaleOrderService orderService, AfterSaleService afterSaleService) {
        this.orderService = orderService;
        this.afterSaleService = afterSaleService;
    }

    @PostMapping("/order")
    public R<Map<String, String>> create(@RequestBody(required = false) Map<String, Object> body) {
        String orderNo = orderService.create(
                longOf(body, "goodsId"),
                intOf(body, "quantity"),
                strOf(body, "receiverName"),
                strOf(body, "receiverPhone"),
                strOf(body, "receiverAddr"),
                strOf(body, "remark"));
        return R.ok(Map.of("orderNo", orderNo));
    }

    @PostMapping("/order/{orderNo}/pay")
    public R<Map<String, String>> pay(@PathVariable String orderNo) {
        return R.ok(Map.of("payNo", orderService.pay(orderNo)));
    }

    @GetMapping("/orders")
    public R<Page<SaleOrder>> list(@RequestParam(required = false) Integer status,
                                   @RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(orderService.listMine(status, pageNum, pageSize));
    }

    @GetMapping("/order/{orderNo}")
    public R<SaleOrder> detail(@PathVariable String orderNo) {
        return R.ok(orderService.detailMine(orderNo));
    }

    @PutMapping("/order/{orderNo}/cancel")
    public R<Void> cancel(@PathVariable String orderNo, @RequestBody(required = false) Map<String, String> body) {
        orderService.cancel(orderNo, body == null ? null : body.get("reason"));
        return R.ok();
    }

    @PutMapping("/order/{orderNo}/confirm")
    public R<Void> confirm(@PathVariable String orderNo) {
        orderService.confirm(orderNo);
        return R.ok();
    }

    /** 申请售后（仅退款）：订单须已付款/已发货。 */
    @PostMapping("/order/{orderNo}/after-sale")
    public R<Map<String, String>> applyAfterSale(@PathVariable String orderNo,
                                                 @RequestBody(required = false) Map<String, String> body) {
        return R.ok(Map.of("asNo", afterSaleService.apply(orderNo, body == null ? null : body.get("reason"))));
    }

    private static String strOf(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static Long longOf(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : Long.valueOf(String.valueOf(v));
    }

    private static Integer intOf(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : Integer.valueOf(String.valueOf(v));
    }
}
