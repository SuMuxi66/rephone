package com.rephone.pojo.dto;

/** 管理端用户列表行。openid 仅展示尾 6 位（敏感信息脱敏）。 */
public record AdminUserItem(Long id, String username, String nickname, String phone,
                            String role, Integer status, String openidMasked, String createTime) {
}
