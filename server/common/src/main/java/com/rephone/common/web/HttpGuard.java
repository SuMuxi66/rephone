package com.rephone.common.web;

import java.net.URI;
import java.util.Set;

/**
 * 服务端出站 HTTP 防护：仅允许 https，禁止 localhost/环回/私有/保留地址。
 * 所有后端向第三方（微信、快递100）发请求前必须先经过本类校验。
 */
public final class HttpGuard {

    private static final Set<String> BLOCKED_HOSTS =
            Set.of("localhost", "127.0.0.1", "0.0.0.0", "::1", "[::1]", "metadata.google.internal");

    private HttpGuard() {
    }

    public static void requirePublicHttps(String url) {
        URI uri = URI.create(url);
        String scheme = uri.getScheme() == null ? "" : uri.getScheme();
        if (!"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("出站请求仅允许 https 协议");
        }
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
        if (host.isEmpty()) {
            throw new IllegalArgumentException("出站请求缺少合法 host");
        }
        if (BLOCKED_HOSTS.contains(host) || host.endsWith(".local") || host.endsWith(".internal")
                || isPrivateIpv4(host)) {
            throw new IllegalArgumentException("出站请求禁止指向私有/保留地址: " + host);
        }
    }

    private static boolean isPrivateIpv4(String host) {
        String[] parts = host.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        int[] o = new int[4];
        for (int i = 0; i < 4; i++) {
            try {
                o[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                return false;
            }
            if (o[i] < 0 || o[i] > 255) {
                return false;
            }
        }
        return o[0] == 10                                   // 10/8
                || (o[0] == 172 && o[1] >= 16 && o[1] <= 31)  // 172.16/12
                || (o[0] == 192 && o[1] == 168)               // 192.168/16
                || (o[0] == 169 && o[1] == 254)               // 169.254/16 链路本地
                || (o[0] == 100 && o[1] >= 64 && o[1] <= 127); // 100.64/10 CGN
    }
}
