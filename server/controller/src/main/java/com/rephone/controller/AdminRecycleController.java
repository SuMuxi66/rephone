package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminStatusRequest;
import com.rephone.pojo.dto.InspectionSubmitRequest;
import com.rephone.pojo.dto.RecycleOrderItem;
import com.rephone.service.RecycleOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端回收订单（ADMIN_TOKEN 鉴权）。P5 提供 Vue 界面，本层先供闭环联调。 */
@RestController
@RequestMapping("/api/admin/recycle")
public class AdminRecycleController {

    private final RecycleOrderService orderService;

    public AdminRecycleController(RecycleOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public R<Page<RecycleOrderItem>> list(@RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(orderService.adminList(status, pageNum, pageSize));
    }

    @PutMapping("/order/{orderNo}/status")
    public R<Void> changeStatus(@PathVariable String orderNo, @RequestBody AdminStatusRequest request) {
        if (request == null || request.toStatus() == null) {
            return R.fail(40001, "toStatus 不能为空");
        }
        orderService.adminTransition(orderNo, request.toStatus(), request.remark());
        return R.ok();
    }

    @PostMapping("/order/{orderNo}/inspection")
    public R<Void> submitInspection(@PathVariable String orderNo,
                                    @RequestBody InspectionSubmitRequest request) {
        orderService.adminSubmitInspection(orderNo, request);
        return R.ok();
    }

    @PostMapping("/order/{orderNo}/payout")
    public R<Void> payout(@PathVariable String orderNo) {
        orderService.adminPayout(orderNo);
        return R.ok();
    }
}
