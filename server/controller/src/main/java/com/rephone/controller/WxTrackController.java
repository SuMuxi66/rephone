package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.TrackReportReq;
import com.rephone.service.TrackService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 小程序行为埋点上报（需登录；游客不上报）。失败由小程序端静默吞掉，不影响业务。 */
@RestController
@RequestMapping("/api/wx/track")
public class WxTrackController {

    private final TrackService trackService;

    public WxTrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/events")
    public R<Map<String, Integer>> report(@RequestBody(required = false) TrackReportReq req) {
        int accepted = trackService.report(req == null ? null : req.events());
        return R.ok(Map.of("accepted", accepted));
    }
}
