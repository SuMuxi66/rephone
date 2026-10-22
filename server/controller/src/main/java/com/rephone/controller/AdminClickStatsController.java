package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.ClickSummary;
import com.rephone.pojo.dto.ClickTopResult;
import com.rephone.pojo.dto.ClickTrendItem;
import com.rephone.service.ClickStatsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 商家端点击漏斗统计（权限点 stats:click，跨租户/租户内自动过滤）。 */
@RestController
@RequestMapping("/api/admin/stats/click")
public class AdminClickStatsController {

    private final ClickStatsService clickStatsService;

    public AdminClickStatsController(ClickStatsService clickStatsService) {
        this.clickStatsService = clickStatsService;
    }

    @GetMapping("/summary")
    public R<ClickSummary> summary() {
        return R.ok(clickStatsService.summary());
    }

    @GetMapping("/trend")
    public R<List<ClickTrendItem>> trend(@RequestParam(defaultValue = "14") int days) {
        return R.ok(clickStatsService.trend(days));
    }

    @GetMapping("/top")
    public R<ClickTopResult> top(@RequestParam(defaultValue = "30") int days,
                                 @RequestParam(defaultValue = "10") int limit) {
        return R.ok(clickStatsService.top(days, limit));
    }
}
