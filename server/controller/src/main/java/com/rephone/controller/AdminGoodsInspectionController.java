package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.service.GoodsInspectionService;
import com.rephone.service.dto.GoodsInspectionSaveRequest;
import com.rephone.service.dto.GoodsInspectionView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理端商品质检报告：录入与读取（整体覆盖；items 为空即清空报告）。 */
@RestController
@RequestMapping("/api/admin/goods")
public class AdminGoodsInspectionController {

    private final GoodsInspectionService inspectionService;

    public AdminGoodsInspectionController(GoodsInspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @GetMapping("/{id}/inspection")
    public R<GoodsInspectionView> get(@PathVariable Long id) {
        return R.ok(inspectionService.getForAdmin(id));
    }

    @PostMapping("/{id}/inspection")
    public R<Void> save(@PathVariable Long id,
                        @RequestBody(required = false) GoodsInspectionSaveRequest body) {
        inspectionService.save(id, body);
        return R.ok();
    }
}
