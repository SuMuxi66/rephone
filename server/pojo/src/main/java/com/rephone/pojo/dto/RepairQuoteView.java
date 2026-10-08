package com.rephone.pojo.dto;

import java.util.List;

/** 维修报价展示（金额一律分；status 1待确认 2已接受 3已拒绝）。 */
public record RepairQuoteView(Long quoteId, Long amountFen, String description, List<String> images,
                              Integer status, String createTime) {
}
