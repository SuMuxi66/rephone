package com.rephone.service.dto;

import com.rephone.pojo.entity.User;

/** 登录结果：用户 + 是否新注册。 */
public record LoginResult(User user, boolean newUser) {
}
