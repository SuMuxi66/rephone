package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.wechat.WxProperties;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 订阅消息配置（用户端，需登录）。 */
@RestController
@RequestMapping("/api/wx/subscribe")
public class WxSubscribeController {

    private final WxProperties wxProperties;

    public WxSubscribeController(WxProperties wxProperties) {
        this.wxProperties = wxProperties;
    }

    /**
     * 小程序 requestSubscribeMessage 需要的模板 ID。
     * 未配置时返回空串，前端据此跳过订阅弹窗（不能把模板 ID 写死在小程序里）。
     */
    @GetMapping("/templates")
    public R<Map<String, String>> templates() {
        String id = wxProperties.getSubscribeTemplateId();
        return R.ok(Map.of("orderStatus", StringUtils.hasText(id) ? id : ""));
    }
}
