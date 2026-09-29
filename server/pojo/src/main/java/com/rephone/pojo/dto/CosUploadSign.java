package com.rephone.pojo.dto;

/** COS 直传签名响应。mock=true 时为演示数据，不能真正上传。 */
public record CosUploadSign(boolean mock, String host, String key,
                            String authorization, long signStart, long signEnd) {
}
