package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.DashboardSummary;
import com.rephone.pojo.dto.DashboardTopItem;
import com.rephone.pojo.dto.DashboardTrendItem;
import com.rephone.service.DashboardService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端数据看板（跨租户）。 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public R<DashboardSummary> summary() {
        return R.ok(dashboardService.summary());
    }

    @GetMapping("/trend")
    public R<List<DashboardTrendItem>> trend(@RequestParam(defaultValue = "14") int days) {
        return R.ok(dashboardService.trend(days));
    }

    @GetMapping("/top")
    public R<Map<String, List<DashboardTopItem>>> top(@RequestParam(defaultValue = "5") int limit) {
        return R.ok(Map.of(
                "recycleModels", dashboardService.topRecycleModels(limit),
                "goods", dashboardService.topGoods(limit)));
    }
}
