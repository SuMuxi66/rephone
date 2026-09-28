package com.rephone.pojo.dto;

/** 微信登录响应。 */
public record LoginResponse(String token, Long userId, String nickname, String avatarUrl, boolean newUser) {
}
