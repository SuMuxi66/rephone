package com.rephone.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** COS 配置。密钥只从环境变量注入，严禁入库入码。 */
@ConfigurationProperties(prefix = "rephone.cos")
public class CosProperties {

    private String secretId;
    private String secretKey;
    private String bucket;
    private String region;
    private boolean mock = false;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }
}
