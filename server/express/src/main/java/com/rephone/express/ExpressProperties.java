package com.rephone.express;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 快递100 配置。key/customer 只从环境变量注入。 */
@ConfigurationProperties(prefix = "rephone.express")
public class ExpressProperties {

    /** 快递100 授权 key。 */
    private String key;

    /** 快递100 授权 customer 编码。 */
    private String customer;

    /** 物流推送回调地址（须公网 https）。 */
    private String callbackUrl;

    /** true 时进入沙箱 mock 模式（默认 true），不发起真实请求。 */
    private boolean mock = true;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }
}
