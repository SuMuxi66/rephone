package com.rephone.pojo.dto;

import java.util.List;

/** 管理端提交维修报价请求。amountFen 为报价金额（分），存储转 DECIMAL(10,2) 元。 */
public record RepairQuoteRequest(Long amountFen, String description, List<String> images) {
}
