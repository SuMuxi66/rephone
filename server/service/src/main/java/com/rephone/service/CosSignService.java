package com.rephone.service;

import com.rephone.pojo.dto.CosUploadSign;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 腾讯云 COS 直传签名（XML API PutObject 预签名）。
 * 密钥仅来自环境变量 COS_SECRET_ID / COS_SECRET_KEY；
 * 未配置（或 COS_MOCK=true）时进入 mock 模式：返回 mock=true 的演示签名，
 * 小程序可跑通流程但不会真实上传。
 */
@Service
public class CosSignService {

    @Autowired
    private com.rephone.service.CosProperties props;

    public CosUploadSign signPutObject(String ext) {
        String safeExt = StringUtils.hasText(ext) && ext.matches("[a-zA-Z0-9]{1,8}") ? ext : "jpg";
        String key = "recycle/" + LocalDate.now() + "/" + UUID.randomUUID().toString().replace("-", "") + "." + safeExt;

        if (props.isMock() || !StringUtils.hasText(props.getSecretId()) || !StringUtils.hasText(props.getSecretKey())) {
            return new CosUploadSign(true, "https://mock-bucket.cos.mock-region.myqcloud.com", key, "mock", 0, 0);
        }

        long start = System.currentTimeMillis() / 1000 - 60;
        long end = start + 900;
        String keyTime = start + ";" + end;
        String host = props.getBucket() + ".cos." + props.getRegion() + ".myqcloud.com";
        // 以下 SHA-1/HmacSHA1 为腾讯云 COS XML API 签名协议强制要求（q-sign-algorithm=sha1），
        // 属于第三方协议规范，非安全算法选型；保护强度由 HMAC 密钥保证。
        String signKey = hmacSha1Hex(keyTime, props.getSecretKey());
        String httpString = "put\n/" + key + "\n\nhost=" + host + "\n";
        String stringToSign = "sha1\n" + keyTime + "\n" + sha1Hex(httpString) + "\n";
        String signature = hmacSha1Hex(stringToSign, signKey);
        String authorization = "q-sign-algorithm=sha1&q-ak=" + props.getSecretId()
                + "&q-sign-time=" + keyTime + "&q-key-time=" + keyTime
                + "&q-header-list=host&q-url-param-list=&q-signature=" + signature;
        return new CosUploadSign(false, "https://" + host, key, authorization, start, end);
    }

    private static String hmacSha1Hex(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            return hex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA1 不可用", e);
        }
    }

    private static String sha1Hex(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return hex(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 不可用", e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
