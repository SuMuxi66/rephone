package com.rephone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.express.ExpressProperties;
import com.rephone.service.RecycleOrderService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 快递100 物流推送回调（/api/callback/kuaidi100，不在 JWT 过滤范围）。
 * 验签规则：sign = MD5(param + key)；mock 模式跳过验签。
 * 揽收/运输节点 → 订单进入 20 运输中；签收节点仅记录，质检由后台推进。
 */
@RestController
public class ExpressCallbackController {

    private static final Logger log = LoggerFactory.getLogger(ExpressCallbackController.class);

    private final ExpressProperties expressProperties;
    private final RecycleOrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExpressCallbackController(ExpressProperties expressProperties, RecycleOrderService orderService) {
        this.expressProperties = expressProperties;
        this.orderService = orderService;
    }

    @PostMapping("/api/callback/kuaidi100")
    public String callback(@RequestParam Map<String, String> form) {
        String param = form.getOrDefault("param", "");
        String sign = form.getOrDefault("sign", "");
        if (!expressProperties.isMock()) {
            if (!StringUtils.hasText(param) || !StringUtils.hasText(sign)
                    || !md5Hex(param + expressProperties.getKey()).equalsIgnoreCase(sign)) {
                log.warn("[kuaidi100-callback] 验签失败");
                return "fail";
            }
        }
        try {
            JsonNode node = objectMapper.readTree(param);
            String expressNo = firstText(node, "kuaidinum", "billcode", "num");
            String status = firstText(node, "status", "state");
            String message = firstText(node, "message");
            JsonNode last = node.path("last_result");
            if (last.isArray() && !last.isEmpty()) {
                JsonNode l = last.get(last.size() - 1);
                if (!StringUtils.hasText(status)) {
                    status = l.path("status").asText("");
                }
                if (!StringUtils.hasText(message)) {
                    message = l.path("message").asText("");
                }
            }
            if (StringUtils.hasText(expressNo) && (message.contains("揽收") || message.contains("运输"))) {
                orderService.onExpressShipping(expressNo, firstText(node, "taskId"), message);
            }
            log.info("[kuaidi100-callback] no={} status={} msg={}", expressNo, status, message);
            return "success";
        } catch (Exception e) {
            log.warn("[kuaidi100-callback] 处理失败: {}", e.getMessage());
            return "fail";
        }
    }

    private static String firstText(JsonNode node, String... fields) {
        for (String f : fields) {
            String v = node.path(f).asText("");
            if (StringUtils.hasText(v)) {
                return v;
            }
        }
        return "";
    }

    // MD5 为快递100 推送验签协议强制要求（非安全算法选型）。
    private static String md5Hex(String data) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
