package com.rephone.express.kuaidi100;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.web.HttpGuard;
import com.rephone.express.ExpressProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * 快递100 HTTP 客户端：只负责签名、表单提交、响应信封解析与错误分类，不掺任何业务语义。
 * 业务层（{@code ExpressServiceImpl}）只组装 param、映射结果，不碰签名与 RestClient。
 *
 * <p>签名规则（官方实时查询文档 1.3）：{@code MD5(param + key + customer)} 转 32 位大写，
 * 其中 param 是「实际提交的那个 JSON 字符串」本身——所以签名与提交必须用同一份字符串。
 *
 * <p>重试只作用于幂等查询：快递100 的下单/取消是写操作，重试可能重复下单，一律不重试。
 */
@Component
public class Kuaidi100Client {

    private static final Logger log = LoggerFactory.getLogger(Kuaidi100Client.class);

    /** 幂等查询最多尝试次数（含首次）。 */
    private static final int MAX_ATTEMPTS = 3;

    /** 第 n 次失败后的退避时长，超出数组长度则沿用最后一个。 */
    private static final long[] BACKOFF_MS = {200L, 600L};

    /** 快递100 服务器侧瞬时故障，值得重试（见文档 1.9）。 */
    private static final Set<String> RETRYABLE_CODES = Set.of("501", "502", "504");

    /** 权限/余额/产品未开通：重试无用，必须人工介入。 */
    private static final Set<String> MANUAL_CODES = Set.of("601");

    /**
     * 快递100 网关在没有 User-Agent 时会返回 HTML 拦截页而不是 JSON，
     * 必须显式带上，否则解析会失败（实测踩过）。
     */
    private static final String USER_AGENT = "RePhone/1.0";

    /** 错误分类，决定调用方是重试、改参数还是转人工。 */
    public enum ErrorKind {
        /** 网络超时或快递100 侧瞬时故障，可重试。 */
        RETRYABLE,
        /** 参数、签名或编码问题，重试无用，必须修正配置或代码。 */
        PARAM,
        /** 权限不足、单量耗尽、产品未开通，必须人工介入。 */
        MANUAL
    }

    /** 快递100 调用失败。调用方按 {@link #kind()} 决定降级策略，不要只 catch Exception。 */
    public static class Kuaidi100Exception extends RuntimeException {

        private final ErrorKind kind;
        private final String returnCode;

        public Kuaidi100Exception(ErrorKind kind, String returnCode, String message, Throwable cause) {
            super(message, cause);
            this.kind = kind;
            this.returnCode = returnCode == null ? "" : returnCode;
        }

        public ErrorKind kind() {
            return kind;
        }

        public String returnCode() {
            return returnCode;
        }
    }

    private final ExpressProperties props;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Kuaidi100Client(ExpressProperties props) {
        this.props = props;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(8).toMillis());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    /**
     * 幂等查询：瞬时故障自动重试（有次数与退避上限）。
     *
     * <p>返回快递100 的原始信封（含 {@code result/returnCode/message/data}），
     * 业务层自己决定怎么解读——客户端不替业务判断「查无结果」是不是错误。
     */
    public Map<String, Object> query(String url, Map<String, Object> param) {
        return call(url, param, true);
    }

    /**
     * 非幂等写操作（下单/取消）：<b>绝不重试</b>。
     * 重试可能重复下单，快递100 的 {@code orderid} 幂等只是兜底，不能当重试依据。
     */
    public Map<String, Object> postOnce(String url, Map<String, Object> param) {
        return call(url, param, false);
    }

    private Map<String, Object> call(String url, Map<String, Object> param, boolean idempotent) {
        requireConfigured();
        HttpGuard.requirePublicHttps(url);
        int maxAttempts = idempotent ? MAX_ATTEMPTS : 1;
        long started = System.currentTimeMillis();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Map<String, Object> resp;
            try {
                resp = post(url, param);
            } catch (Kuaidi100Exception e) {
                if (e.kind() == ErrorKind.RETRYABLE && attempt < maxAttempts) {
                    log.warn("[kuaidi100] 传输故障重试 url={} attempt={}/{} msg={}", url, attempt, maxAttempts, e.getMessage());
                    sleepBackoff(attempt);
                    continue;
                }
                log.warn("[kuaidi100] 调用失败 url={} elapsed={}ms attempt={} kind={} code={} msg={}",
                        url, System.currentTimeMillis() - started, attempt, e.kind(), e.returnCode(), e.getMessage());
                throw e;
            }
            String code = text(resp.get("returnCode"));
            if (idempotent && RETRYABLE_CODES.contains(code) && attempt < maxAttempts) {
                log.warn("[kuaidi100] 服务端瞬时故障重试 url={} attempt={}/{} code={}", url, attempt, maxAttempts, code);
                sleepBackoff(attempt);
                continue;
            }
            log.info("[kuaidi100] 调用完成 url={} elapsed={}ms attempt={} code={}",
                    url, System.currentTimeMillis() - started, attempt, StringUtils.hasText(code) ? code : "-");
            return resp;
        }
        // maxAttempts >= 1，循环只可能通过 return / throw 结束，这里仅为编译期可达性兜底
        throw new IllegalStateException("unreachable");
    }

    /**
     * 先把响应当字符串收下来再自己解析：
     * 快递100 的成功响应不带 application/json，Spring 的 Map 转换器会直接报
     * "no suitable HttpMessageConverter"；同时原始报文要留着排错。
     */
    private Map<String, Object> post(String url, Map<String, Object> param) {
        MultiValueMap<String, String> form;
        try {
            form = signedForm(objectMapper.writeValueAsString(param));
        } catch (JsonProcessingException e) {
            throw new Kuaidi100Exception(ErrorKind.PARAM, "", "参数序列化失败: " + e.getMessage(), e);
        }
        String body;
        try {
            body = restClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .body(form)
                    .retrieve()
                    .body(String.class);
        } catch (HttpStatusCodeException e) {
            int status = e.getStatusCode().value();
            // 4xx 是我们请求/地址/权限的问题，重试无用；5xx 才是快递100 侧瞬时故障
            throw new Kuaidi100Exception(status >= 500 ? ErrorKind.RETRYABLE : ErrorKind.PARAM,
                    "", "快递100 HTTP " + status, e);
        } catch (ResourceAccessException e) {
            throw new Kuaidi100Exception(ErrorKind.RETRYABLE, "", "快递100 连接异常: " + e.getMessage(), e);
        } catch (RestClientResponseException e) {
            throw new Kuaidi100Exception(ErrorKind.RETRYABLE, "", "快递100 响应异常: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Kuaidi100Exception(ErrorKind.RETRYABLE, "", "快递100 请求异常: " + e.getMessage(), e);
        }
        if (!StringUtils.hasText(body)) {
            throw new Kuaidi100Exception(ErrorKind.RETRYABLE, "", "快递100 返回空响应", null);
        }
        try {
            return objectMapper.readValue(body, new TypeReference<LinkedHashMap<String, Object>>() { });
        } catch (JsonProcessingException e) {
            throw new Kuaidi100Exception(ErrorKind.PARAM, "",
                    "快递100 返回非 JSON: " + snippet(body), e);
        }
    }

    /** 截断原始报文，避免把整页 HTML 或超长报错塞进日志/异常。 */
    private static String snippet(String body) {
        String s = body.replaceAll("\\s+", " ").trim();
        return s.length() <= 200 ? s : s.substring(0, 200) + "...";
    }

    private void requireConfigured() {
        if (!StringUtils.hasText(props.getKey()) || !StringUtils.hasText(props.getCustomer())) {
            throw new Kuaidi100Exception(ErrorKind.MANUAL, "",
                    "快递100 未配置（需 EXPRESS_KEY / EXPRESS_CUSTOMER）", null);
        }
    }

    /** 签名与提交必须用同一份 paramJson，任何重排都会导致 503。 */
    private MultiValueMap<String, String> signedForm(String paramJson) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("param", paramJson);
        form.add("customer", props.getCustomer());
        form.add("sign", md5Hex(paramJson + props.getKey() + props.getCustomer()).toUpperCase());
        return form;
    }

    /** 事件码分类；未收录的一律按参数问题处理（重试无用）。 */
    public static ErrorKind classify(String returnCode) {
        if (RETRYABLE_CODES.contains(returnCode)) {
            return ErrorKind.RETRYABLE;
        }
        if (MANUAL_CODES.contains(returnCode)) {
            return ErrorKind.MANUAL;
        }
        return ErrorKind.PARAM;
    }

    private static void sleepBackoff(int attempt) {
        long ms = BACKOFF_MS[Math.min(attempt - 1, BACKOFF_MS.length - 1)];
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Kuaidi100Exception(ErrorKind.RETRYABLE, "", "重试被中断", e);
        }
    }

    private static String text(Object value) {
        if (value == null) {
            return "";
        }
        String s = String.valueOf(value).trim();
        return "null".equals(s) ? "" : s;
    }

    /** MD5 为快递100 签名协议强制要求（非安全算法选型）。 */
    private static String md5Hex(String data) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(data.getBytes(StandardCharsets.UTF_8)); // mimosa-ignore
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
