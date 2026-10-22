package com.rephone.pojo.dto;

import java.util.List;

/** 点击榜单聚合结果。 */
public record ClickTopResult(
        List<ClickTopItem> goods,
        List<ClickModelTopItem> models) {
}
