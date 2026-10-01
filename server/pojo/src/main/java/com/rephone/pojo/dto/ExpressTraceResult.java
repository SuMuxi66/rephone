package com.rephone.pojo.dto;

import java.util.List;

/**
 * 物流轨迹查询结果。
 *
 * @param com         快递100 公司编码
 * @param companyName 快递公司名称
 * @param expressNo   运单号
 * @param state       快递单状态值（0在途 1揽收 3签收 5派件…）
 * @param stateName   状态中文名
 * @param items       轨迹节点，最新在前
 * @param queriedAt   本次快照时间（缓存命中时为上次回源时间）
 * @param cached      true 表示命中本地快照，未消耗快递100 查询单量
 */
public record ExpressTraceResult(String com, String companyName, String expressNo,
                                 String state, String stateName,
                                 List<Item> items, String queriedAt, boolean cached) {

    public record Item(String time, String context, String status, String statusCode) {
    }
}
