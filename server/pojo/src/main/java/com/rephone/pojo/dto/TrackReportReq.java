package com.rephone.pojo.dto;

import java.util.List;

/** 小程序埋点上报请求体。 */
public record TrackReportReq(List<TrackEventReq> events) {

    /** 单条事件：targetIds 供批量曝光使用，targetId 供单目标事件使用。 */
    public record TrackEventReq(
            Integer eventType,
            Integer targetType,
            Long targetId,
            List<Long> targetIds,
            String page,
            String deviceId) {
    }
}
