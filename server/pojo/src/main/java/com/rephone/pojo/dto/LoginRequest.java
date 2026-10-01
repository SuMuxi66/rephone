package com.rephone.pojo.dto;

/**
 * 微信登录请求。code 来自小程序 wx.login()；
 * deviceId 为前端持久化的设备标识，仅 mock-login 用于稳定身份映射，真实登录忽略。
 */
public record LoginRequest(String code, String deviceId) {
}
