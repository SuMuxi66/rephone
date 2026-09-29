package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminStatusRequest;
import com.rephone.pojo.dto.RepairOrderDetail;
import com.rephone.pojo.dto.RepairOrderItem;
import com.rephone.service.RepairOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 维修工单（管理端，AdminToken 鉴权）。P5 后台复用。 */
@RestController
@RequestMapping("/api/admin/repair")
public class AdminRepairController {

    private final RepairOrderService repairService;

    public AdminRepairController(RepairOrderService repairService) {
        this.repairService = repairService;
    }

    @GetMapping("/orders")
    public R<Page<RepairOrderItem>> list(@RequestParam(required = false) Integer status,
                                         @RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(repairService.adminList(status, pageNum, pageSize));
    }

    @GetMapping("/order/{orderNo}")
    public R<RepairOrderDetail> detail(@PathVariable String orderNo) {
        return R.ok(repairService.adminDetail(orderNo));
    }

    /** 状态推进：10 待确认 → 20 已预约 → 30 维修中 → 40 待验收 → 50 已完成（按服务端状态机校验）。 */
    @PutMapping("/order/{orderNo}/status")
    public R<Void> transition(@PathVariable String orderNo, @RequestBody AdminStatusRequest request) {
        repairService.adminTransition(orderNo, request.toStatus(), request.remark());
        return R.ok();
    }
}
