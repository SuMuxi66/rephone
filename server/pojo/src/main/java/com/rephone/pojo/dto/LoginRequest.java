package com.rephone.pojo.dto;

/** 微信登录请求。code 来自小程序 wx.login()。 */
public record LoginRequest(String code) {
}
