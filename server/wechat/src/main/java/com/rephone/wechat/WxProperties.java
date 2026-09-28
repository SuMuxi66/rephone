package com.rephone.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信开放能力配置。appid/secret 只能通过环境变量注入：
 * WX_APPID / WX_SECRET，严禁写入代码或配置文件。
 */
@ConfigurationProperties(prefix = "rephone.wx")
public class WxProperties {

    private String appid;

    private String secret;

    /**
     * 开发联调模式：未配置微信凭证时生成模拟会话。
     * 默认 false；仅在开发环境通过 WX_MOCK_LOGIN=true 打开。
     */
    private boolean mockLogin = false;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isMockLogin() {
        return mockLogin;
    }

    public void setMockLogin(boolean mockLogin) {
        this.mockLogin = mockLogin;
    }
}
