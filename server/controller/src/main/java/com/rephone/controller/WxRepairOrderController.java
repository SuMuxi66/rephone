package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminStatusRequest;
import com.rephone.pojo.dto.ExpressFillRequest;
import com.rephone.pojo.dto.ExpressTraceResult;
import com.rephone.pojo.dto.RepairGroupView;
import com.rephone.pojo.dto.RepairOrderCreateRequest;
import com.rephone.pojo.dto.RepairOrderDetail;
import com.rephone.pojo.dto.RepairOrderItem;
import com.rephone.service.QuoteService;
import com.rephone.service.RepairOrderService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 维修工单（用户端，需登录）。上门修好验收付款；寄修含寄出/回寄运单与轨迹。 */
@RestController
@RequestMapping("/api/wx/repair")
public class WxRepairOrderController {

    private final RepairOrderService repairService;
    private final QuoteService quoteService;

    public WxRepairOrderController(RepairOrderService repairService, QuoteService quoteService) {
        this.repairService = repairService;
        this.quoteService = quoteService;
    }

    /** 全量机型（机型库为设备字典，维修价有全机型兜底价）。 */
    @GetMapping("/models")
    public R<?> allModels(@RequestParam Long brandId) {
        return R.ok(quoteService.listAllModels(brandId));
    }

    /** 维修项目分组字典（含该机型实时价格）。 */
    @GetMapping("/items")
    public R<List<RepairGroupView>> items(@RequestParam Long modelId) {
        return R.ok(repairService.items(modelId));
    }

    @PostMapping("/order")
    public R<Map<String, String>> create(@RequestBody RepairOrderCreateRequest request) {
        return R.ok(Map.of("orderNo", repairService.create(request)));
    }

    @GetMapping("/orders")
    public R<Page<RepairOrderItem>> list(@RequestParam(required = false) Integer status,
                                         @RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(repairService.list(status, pageNum, pageSize));
    }

    @GetMapping("/order/{orderNo}")
    public R<RepairOrderDetail> detail(@PathVariable String orderNo) {
        return R.ok(repairService.detail(orderNo));
    }

    @PutMapping("/order/{orderNo}/cancel")
    public R<Void> cancel(@PathVariable String orderNo,
                          @RequestBody(required = false) Map<String, String> body) {
        repairService.cancel(orderNo, body == null ? null : body.get("reason"));
        return R.ok();
    }

    /** 寄修：填写寄出运单号（仅寄修单、待寄出态），状态 20→25。 */
    @PutMapping("/order/{orderNo}/express")
    public R<Void> fillExpress(@PathVariable String orderNo,
                               @RequestBody(required = false) ExpressFillRequest request) {
        repairService.fillExpress(orderNo, request);
        return R.ok();
    }

    /** 寄修：寄出运单轨迹（30 分钟快照缓存）。 */
    @GetMapping("/order/{orderNo}/trace")
    public R<ExpressTraceResult> trace(@PathVariable String orderNo) {
        return R.ok(repairService.trace(orderNo, false));
    }

    /** 寄修：回寄运单轨迹。 */
    @GetMapping("/order/{orderNo}/return-trace")
    public R<ExpressTraceResult> returnTrace(@PathVariable String orderNo) {
        return R.ok(repairService.trace(orderNo, true));
    }

    /** 寄修：用户确认收货（回寄中 45→50）。 */
    @PutMapping("/order/{orderNo}/confirm")
    public R<Void> confirm(@PathVariable String orderNo) {
        repairService.confirmReceipt(orderNo);
        return R.ok();
    }
}
