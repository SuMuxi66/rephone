package com.rephone.pojo.dto;

import java.util.List;

/** 管理端提交质检结果请求。提交后订单进入 40 待确认。 */
public record InspectionSubmitRequest(String result, Long finalFen, List<String> images) {
}
