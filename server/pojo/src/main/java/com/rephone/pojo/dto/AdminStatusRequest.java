package com.rephone.pojo.dto;

/** 管理端修改订单状态请求。 */
public record AdminStatusRequest(Integer toStatus, String remark) {
}
