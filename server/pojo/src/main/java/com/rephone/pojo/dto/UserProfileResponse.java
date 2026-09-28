package com.rephone.pojo.dto;

/** 用户资料响应。 */
public record UserProfileResponse(Long userId, String nickname, String avatarUrl, Integer gender,
                                 String phone, Long tenantId) {
}
