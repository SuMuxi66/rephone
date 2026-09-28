package com.rephone.wechat.model;

/** code2Session 换取的微信会话。sessionKey 仅用于后续手机号解密等能力，需要缓存时走 Redis。 */
public record WxSession(String openid, String sessionKey, String unionid) {
}
