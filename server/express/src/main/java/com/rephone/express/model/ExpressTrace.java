package com.rephone.express.model;

import java.util.List;

/**
 * 物流轨迹（快递100 实时查询结果）。
 *
 * @param com         快递100 公司编码
 * @param companyName 公司名称
 * @param expressNo   运单号
 * @param state       快递单当前状态值（0在途 1揽收 2疑难 3签收 4退签 5派件 6退回 7转投 8清关 14拒签…）
 * @param stateName   状态中文名，未知状态为空串
 * @param nodes       轨迹节点，时间倒序（最新在最前）
 */
public record ExpressTrace(String com, String companyName, String expressNo,
                           String state, String stateName, List<Node> nodes) {

    /**
     * @param time       格式化时间（ftime，缺失时回退 time）
     * @param context    轨迹内容
     * @param status     本节点物流状态名（resultv2 开启后返回）
     * @param statusCode 本节点高级物流状态值（resultv2=4/8 返回）
     */
    public record Node(String time, String context, String status, String statusCode) {
    }
}
