package com.rephone.wechat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** jscode2session 原始响应。 */
public record WxCode2SessionResponse(
        @JsonProperty("openid") String openid,
        @JsonProperty("session_key") String sessionKey,
        @JsonProperty("unionid") String unionid,
        @JsonProperty("errcode") Integer errcode,
        @JsonProperty("errmsg") String errmsg) {
}
