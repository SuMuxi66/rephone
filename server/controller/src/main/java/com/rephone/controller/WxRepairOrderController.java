package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.RepairGroupView;
import com.rephone.pojo.dto.RepairOrderCreateRequest;
import com.rephone.pojo.dto.RepairOrderDetail;
import com.rephone.pojo.dto.RepairOrderItem;
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

/** 维修工单（用户端，需登录）。MVP 仅上门维修，修好验收后付款。 */
@RestController
@RequestMapping("/api/wx/repair")
public class WxRepairOrderController {

    private final RepairOrderService repairService;

    public WxRepairOrderController(RepairOrderService repairService) {
        this.repairService = repairService;
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
}
