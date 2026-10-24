package com.rephone.controller;

import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.UserProfileResponse;
import com.rephone.pojo.dto.UserProfileUpdateRequest;
import com.rephone.pojo.entity.User;
import com.rephone.service.CosSignService;
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
    private final CosSignService cosSignService;

    public WxUserController(UserService userService, CosSignService cosSignService) {
        this.userService = userService;
        this.cosSignService = cosSignService;
    }

    @GetMapping("/profile")
    public R<UserProfileResponse> profile() {
        Long userId = UserContextHolder.require().userId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException(40401, "用户不存在");
        }
        return ok(user);
    }

    @PutMapping("/profile")
    public R<UserProfileResponse> updateProfile(@RequestBody(required = false) UserProfileUpdateRequest request) {
        if (request == null) {
            throw new BizException(40001, "参数不能为空");
        }
        Long userId = UserContextHolder.require().userId();
        User user = userService.updateProfile(userId, request.nickname(), request.gender(),
                request.avatarUrl(), request.phoneNumber());
        return ok(user);
    }

    /** avatarUrl 以 COS key 存储，返回前签 GET 预签名 URL 供前端直接回显（mock/空值原样返回）。 */
    private R<UserProfileResponse> ok(User user) {
        return R.ok(new UserProfileResponse(user.getId(), user.getNickname(),
                cosSignService.signGetObject(user.getAvatarUrl()),
                user.getGender(), user.getPhone(), user.getTenantId()));
    }
}
