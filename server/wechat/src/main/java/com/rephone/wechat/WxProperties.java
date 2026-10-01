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

    /** 订阅消息模板 ID（订单状态通知）。WX_SUBSCRIBE_TEMPLATE_ID。 */
    private String subscribeTemplateId;

    /**
     * 订阅消息模板字段名：订单状态。形如 thing1。
     *
     * <p>微信在申请模板时**随机分配**字段编号，不随模板内容固定，所以绝不能写死。
     * 故意用三个独立属性而不是 Map —— Map 的 key 在宽松绑定下大小写可能被改写，
     * 而字段名直接决定推送成败（写错会返回 47003）。
     */
    private String subscribeFieldStatus;

    /** 订阅消息模板字段名：订单号。形如 character_string2。 */
    private String subscribeFieldOrderNo;

    /** 订阅消息模板字段名：时间（模板含 time 类字段时才配）。形如 time3。 */
    private String subscribeFieldTime;

    /** 订阅消息点击后跳转的小程序页面路径。 */
    private String subscribePage;

    public String getSubscribeTemplateId() {
        return subscribeTemplateId;
    }

    public void setSubscribeTemplateId(String subscribeTemplateId) {
        this.subscribeTemplateId = subscribeTemplateId;
    }

    public String getSubscribeFieldStatus() {
        return subscribeFieldStatus;
    }

    public void setSubscribeFieldStatus(String subscribeFieldStatus) {
        this.subscribeFieldStatus = subscribeFieldStatus;
    }

    public String getSubscribeFieldOrderNo() {
        return subscribeFieldOrderNo;
    }

    public void setSubscribeFieldOrderNo(String subscribeFieldOrderNo) {
        this.subscribeFieldOrderNo = subscribeFieldOrderNo;
    }

    public String getSubscribeFieldTime() {
        return subscribeFieldTime;
    }

    public void setSubscribeFieldTime(String subscribeFieldTime) {
        this.subscribeFieldTime = subscribeFieldTime;
    }

    public String getSubscribePage() {
        return subscribePage;
    }

    public void setSubscribePage(String subscribePage) {
        this.subscribePage = subscribePage;
    }

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
