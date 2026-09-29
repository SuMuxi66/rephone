package com.rephone.pojo.dto;

import java.util.List;

/** 维修项目分组（屏幕问题/电池问题…）。 */
public record RepairGroupView(String groupName, List<RepairItemCell> items) {
}
