package com.rephone.pojo.dto;

/**
 * 回收下单结果。
 *
 * <p>上门取件可能预约失败（地区不支持、运力满、寄件服务未开通…），失败不能静默：
 * 用户以为有人上门、订单一直卡在待寄出，运营也不知道。所以把失败原因显式回给前端做降级引导。
 *
 * @param orderNo          订单号
 * @param pickupBooked     上门取件是否预约成功；pickupType=10（自行寄出）恒为 false
 * @param pickupFailReason 预约失败时给用户的降级提示；成功或本就不需要预约时为空串
 */
public record RecycleOrderCreateResult(String orderNo, boolean pickupBooked, String pickupFailReason) {
}
