package com.rephone.controller;

import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import com.rephone.common.security.JwtTokenService;
import com.rephone.pojo.dto.LoginRequest;
import com.rephone.pojo.dto.LoginResponse;
import com.rephone.service.UserService;
import com.rephone.service.dto.LoginResult;
import com.rephone.wechat.WxApiClient;
import com.rephone.wechat.model.WxSession;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 微信登录（匿名入口，无需 token）。 */
@RestController
@RequestMapping("/api/wx")
public class WxAuthController {

    private final WxApiClient wxApiClient;
    private final UserService userService;
    private final JwtTokenService tokenService;

    public WxAuthController(WxApiClient wxApiClient, UserService userService, JwtTokenService tokenService) {
        this.wxApiClient = wxApiClient;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody(required = false) LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.code())) {
            throw new BizException(40001, "code 不能为空");
        }
        WxSession session = wxApiClient.code2Session(request.code().trim(), request.deviceId());
        LoginResult result = userService.loginOrRegister(session);
        String token = tokenService.create(result.user().getId(), result.user().getOpenid(),
                result.user().getTenantId());
        return R.ok(new LoginResponse(token, result.user().getId(), result.user().getNickname(),
                result.user().getAvatarUrl(), result.newUser()));
    }
}
