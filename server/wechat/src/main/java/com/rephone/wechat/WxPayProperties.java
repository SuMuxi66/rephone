package com.rephone.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 微信商户/支付配置（企业付款到零钱）。密钥与证书路径只从环境变量注入。 */
@ConfigurationProperties(prefix = "rephone.wxpay")
public class WxPayProperties {

    /** 商户号。 */
    private String mchId;

    /** 商户 API 密钥（v1 XML 签名用）。 */
    private String mchKey;

    /** apiclient_cert.p12 证书绝对路径。 */
    private String certPath;

    /** true 时跳过真实请求，直接返回打款成功（开发联调）。 */
    private boolean mock = false;

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }
}
