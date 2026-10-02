package com.rephone.pojo.dto;

/**
 * 用户资料更新请求（仅传入字段生效）。
 * phoneNumber：传空串表示解绑手机号；传值时须为 11 位大陆手机号。
 */
public record UserProfileUpdateRequest(String nickname, Integer gender, String avatarUrl, String phoneNumber) {
}
