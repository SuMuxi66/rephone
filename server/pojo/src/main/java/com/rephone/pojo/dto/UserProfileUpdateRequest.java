package com.rephone.pojo.dto;

/** 用户资料更新请求（仅传入字段生效）。 */
public record UserProfileUpdateRequest(String nickname, Integer gender, String avatarUrl) {
}
