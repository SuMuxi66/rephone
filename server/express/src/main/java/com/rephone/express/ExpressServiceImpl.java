package com.rephone.express;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.exception.BizException;
import com.rephone.common.web.HttpGuard;
import com.rephone.express.model.ExpressPickupRequest;
import com.rephone.express.model.ExpressPickupResult;
import com.rephone.express.model.ExpressTrace;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 快递100 实现。
 *
 * <p>实时查询走 {@code poll.kuaidi100.com/poll/query.do}，签名 = MD5(param + key + customer) 转 32 位大写。
 * 寄件/上门取件（applyApi）属于另一个签约产品，账号未开通时会被快递100 拒绝。
 * mock 模式（EXPRESS_MOCK=true，默认）不发起真实请求。
 */
@Service
public class ExpressServiceImpl implements ExpressService {

    /** 上门取件下单（寄件服务产品，需单独开通）。 */
    private static final String ORDER_URL = "https://api.kuaidi100.com/applyApi/api/order";
    /** 取消取件。 */
    private static final String CANCEL_URL = "https://api.kuaidi100.com/applyApi/api/cancel";
    /** 实时快递查询。 */
    private static final String QUERY_URL = "https://poll.kuaidi100.com/poll/query.do";

    /** 寄件默认运力。开通寄件服务后按签约运力调整。 */
    private static final String PICKUP_COMPANY = "shunfeng";

    /** 单次轨迹最多保留的节点数，防止超长 JSON 撑爆快照列。 */
    private static final int MAX_TRACE_NODES = 40;

    /** 物流状态值 → 中文名，见实时查询文档 1.6。 */
    private static final Map<String, String> STATE_NAMES = Map.ofEntries(
            Map.entry("0", "在途"), Map.entry("1", "已揽收"), Map.entry("2", "疑难"),
            Map.entry("3", "已签收"), Map.entry("4", "退签"), Map.entry("5", "派件中"),
            Map.entry("6", "退回中"), Map.entry("7", "转投"), Map.entry("8", "清关"),
            Map.entry("10", "待清关"), Map.entry("11", "清关中"), Map.entry("12", "已清关"),
            Map.entry("13", "清关异常"), Map.entry("14", "拒签"));

    /** 快递100 查询错误码 → 用户可读文案，见实时查询文档 1.9。 */
    private static final Map<String, String> QUERY_ERRORS = Map.ofEntries(
            Map.entry("400", "快递公司编码有误或账号不可用"),
            Map.entry("401", "暂不支持该快递公司"),
            Map.entry("408", "该快递公司需要填写收寄件人手机号"),
            Map.entry("500", "暂未查到物流信息，请稍后再试"),
            Map.entry("501", "快递100 服务异常，请稍后再试"),
            Map.entry("502", "快递100 繁忙，请稍后再试"),
            Map.entry("503", "快递100 签名校验失败，请检查密钥配置"),
            Map.entry("504", "查询过于频繁，请 30 分钟后再试"),
            Map.entry("601", "快递100 账号单量不足，请联系运营充值"));

    private final ExpressProperties props;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ExpressServiceImpl(ExpressProperties props) {
        this.props = props;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(8).toMillis());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Override
    public ExpressPickupResult createPickup(ExpressPickupRequest request) {
        if (props.isMock()) {
            String taskId = "TASK-MOCK-" + System.currentTimeMillis();
            return new ExpressPickupResult(taskId, "MOCKSF" + System.currentTimeMillis());
        }
        requireConfigured();
        HttpGuard.requirePublicHttps(ORDER_URL);
        try {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("kuaidicom", PICKUP_COMPANY);
            param.put("sendMan", Map.of("name", request.receiverName(), "tel", request.receiverPhone(),
                    "address", request.receiverAddress()));
            param.put("callback", props.getCallbackUrl());
            param.put("orderid", request.orderNo());
            String paramJson = objectMapper.writeValueAsString(param);
            Map<?, ?> resp = postForm(ORDER_URL, signedForm(paramJson));
            Map<?, ?> data = resp == null ? null : (resp.get("data") instanceof Map<?, ?> d ? d : null);
            Object taskId = data == null ? null : data.get("taskId");
            Object billCode = data == null ? null : data.get("billCode");
            if (taskId == null) {
                throw new BizException(50020, "快递100 下单失败（寄件服务可能未开通）: " + resp);
            }
            return new ExpressPickupResult(String.valueOf(taskId),
                    billCode == null ? null : String.valueOf(billCode));
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(50021, "快递100 下单异常: " + e.getMessage());
        }
    }

    @Override
    public void cancelPickup(String taskNo) {
        if (props.isMock()) {
            return;
        }
        requireConfigured();
        HttpGuard.requirePublicHttps(CANCEL_URL);
        try {
            String paramJson = objectMapper.writeValueAsString(Map.of("taskId", taskNo));
            postForm(CANCEL_URL, signedForm(paramJson));
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(50022, "快递100 取消异常: " + e.getMessage());
        }
    }

    @Override
    public ExpressTrace queryTrace(String com, String expressNo, String phone) {
        if (props.isMock()) {
            return mockTrace(com, expressNo);
        }
        requireConfigured();
        HttpGuard.requirePublicHttps(QUERY_URL);

        Map<String, Object> param = new LinkedHashMap<>();
        param.put("com", com);
        param.put("num", expressNo);
        if (StringUtils.hasText(phone)) {
            // 顺丰速运/顺丰快运/中通快递 必填收寄件人电话，缺失会返回 408
            param.put("phone", phone.trim());
        }
        // resultv2=4：返回行政区域解析 + 高级物流状态名/状态值
        param.put("resultv2", "4");
        param.put("order", "desc");

        Map<?, ?> resp;
        try {
            resp = postForm(QUERY_URL, signedForm(objectMapper.writeValueAsString(param)));
        } catch (Exception e) {
            throw new BizException(50024, "快递100 轨迹查询异常: " + e.getMessage());
        }
        if (resp == null) {
            throw new BizException(50024, "快递100 未返回数据");
        }
        Object data = resp.get("data");
        if (!(data instanceof List<?> raw) || raw.isEmpty()) {
            throw new BizException(50024, queryError(resp));
        }
        List<ExpressTrace.Node> nodes = new ArrayList<>();
        for (Object item : raw) {
            if (nodes.size() >= MAX_TRACE_NODES) {
                break;
            }
            if (item instanceof Map<?, ?> m) {
                nodes.add(new ExpressTrace.Node(text(m.get("ftime"), m.get("time")),
                        text(m.get("context")), text(m.get("status")), text(m.get("statusCode"))));
            }
        }
        if (nodes.isEmpty()) {
            throw new BizException(50024, "暂未查到物流信息，请稍后再试");
        }
        String state = text(resp.get("state"));
        return new ExpressTrace(com, ExpressCompanies.nameOf(com), expressNo,
                state, STATE_NAMES.getOrDefault(state, ""), nodes);
    }

    // ===== 内部 =====

    private Map<?, ?> postForm(String url, MultiValueMap<String, String> form) {
        return restClient.post().uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);
    }

    private void requireConfigured() {
        if (!StringUtils.hasText(props.getKey()) || !StringUtils.hasText(props.getCustomer())) {
            throw new BizException(50025, "快递100 未配置（需 EXPRESS_KEY / EXPRESS_CUSTOMER 环境变量）");
        }
    }

    /** 快递100 签名：MD5(param + key + customer)，32 位大写。 */
    private MultiValueMap<String, String> signedForm(String paramJson) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("param", paramJson);
        form.add("customer", props.getCustomer());
        form.add("sign", md5Hex(paramJson + props.getKey() + props.getCustomer()).toUpperCase());
        return form;
    }

    /** 把快递100 的 returnCode 翻成可读文案；没见过的错误码保留原始 message。 */
    private static String queryError(Map<?, ?> resp) {
        String code = text(resp.get("returnCode"));
        String friendly = QUERY_ERRORS.get(code);
        if (friendly != null) {
            return friendly;
        }
        String message = text(resp.get("message"));
        return StringUtils.hasText(message) ? message : "物流查询失败";
    }

    private static ExpressTrace mockTrace(String com, String expressNo) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new ExpressTrace(com, ExpressCompanies.nameOf(com), expressNo, "1", STATE_NAMES.get("1"),
                List.of(new ExpressTrace.Node(now, "mock 轨迹：快件已由快递员揽收", "已揽收", "103"),
                        new ExpressTrace.Node(now, "mock 轨迹：包裹运输中", "在途", "1002")));
    }

    /** 取第一个非空值，快递100 同一语义字段有多个别名。 */
    private static String text(Object... candidates) {
        for (Object c : candidates) {
            if (c != null) {
                String s = String.valueOf(c).trim();
                if (!s.isEmpty() && !"null".equals(s)) {
                    return s;
                }
            }
        }
        return "";
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
