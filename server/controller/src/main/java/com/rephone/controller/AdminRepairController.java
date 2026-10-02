package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.express.ExpressCompanies;
import com.rephone.express.model.ExpressCompany;
import com.rephone.pojo.dto.AdminStatusRequest;
import com.rephone.pojo.dto.ExpressFillRequest;
import com.rephone.pojo.dto.ExpressTraceResult;
import com.rephone.pojo.dto.RepairOrderDetail;
import com.rephone.pojo.dto.RepairOrderItem;
import com.rephone.service.RepairOrderService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 维修工单（管理端）。上门/寄修双状态机推进与寄修回寄运单。 */
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

    /** 状态推进（按服务方式分支校验：上门 10→20→30→40→50；寄修 10→20、25→30、30→40、45→50）。 */
    @PutMapping("/order/{orderNo}/status")
    public R<Void> transition(@PathVariable String orderNo, @RequestBody AdminStatusRequest request) {
        repairService.adminTransition(orderNo, request.toStatus(), request.remark());
        return R.ok();
    }

    /** 寄修：商家填写回寄运单号（待回寄态），状态 40→45。 */
    @PutMapping("/order/{orderNo}/return-express")
    public R<Void> fillReturnExpress(@PathVariable String orderNo,
                                     @RequestBody(required = false) ExpressFillRequest request) {
        repairService.adminFillReturnExpress(orderNo, request);
        return R.ok();
    }

    /** 寄修：查询双向物流轨迹（direction=out 寄出 / return 回寄，30 分钟快照）。 */
    @GetMapping("/order/{orderNo}/trace")
    public R<ExpressTraceResult> trace(@PathVariable String orderNo,
                                       @RequestParam(defaultValue = "out") String direction) {
        return R.ok(repairService.trace(orderNo, "return".equalsIgnoreCase(direction)));
    }

    /** 快递公司字典（回寄填单选择器，与后端查询共用一份编码）。 */
    @GetMapping("/express-companies")
    public R<List<ExpressCompany>> expressCompanies() {
        return R.ok(ExpressCompanies.all());
    }
}
