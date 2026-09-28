package com.rephone.express.model;

/** 上门取件下单请求（P4 接快递100 时落库/转发）。 */
public record ExpressPickupRequest(
        String orderNo,
        String receiverName,
        String receiverPhone,
        String receiverAddress,
        String pickupTime) {
}
