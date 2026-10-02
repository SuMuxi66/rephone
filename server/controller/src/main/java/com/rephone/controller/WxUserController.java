package com.rephone.controller;

import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.UserProfileResponse;
import com.rephone.pojo.dto.UserProfileUpdateRequest;
import com.rephone.pojo.entity.User;
import com.rephone.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户资料（需 token）。租户过滤由拦截器自动追加。 */
@RestController
@RequestMapping("/api/wx/user")
public class WxUserController {

    private final UserService userService;

    public WxUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public R<UserProfileResponse> profile() {
        Long userId = UserContextHolder.require().userId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException(40401, "用户不存在");
        }
        return R.ok(new UserProfileResponse(user.getId(), user.getNickname(), user.getAvatarUrl(),
                user.getGender(), user.getPhone(), user.getTenantId()));
    }

    @PutMapping("/profile")
    public R<UserProfileResponse> updateProfile(@RequestBody(required = false) UserProfileUpdateRequest request) {
        if (request == null) {
            throw new BizException(40001, "参数不能为空");
        }
        Long userId = UserContextHolder.require().userId();
        User user = userService.updateProfile(userId, request.nickname(), request.gender(),
                request.avatarUrl(), request.phoneNumber());
        return R.ok(new UserProfileResponse(user.getId(), user.getNickname(), user.getAvatarUrl(),
                user.getGender(), user.getPhone(), user.getTenantId()));
    }
}
