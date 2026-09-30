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
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 快递100 实现。mock 模式（EXPRESS_MOCK=true，默认）不发起真实请求；
 * 真实模式要求 key/customer 配置，签名按快递100规范 MD5(param+key+customer)。
 */
@Service
public class ExpressServiceImpl implements ExpressService {

    private static final String ORDER_URL = "https://api.kuaidi100.com/applyApi/api/order";
    private static final String CANCEL_URL = "https://api.kuaidi100.com/applyApi/api/cancel";
    private static final String QUERY_URL = "https://api.kuaidi100.com/applyApi/api/synquery";

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
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("kuaidicom", "shunfeng");
            param.put("sendMan", Map.of("name", request.receiverName(), "tel", request.receiverPhone(),
                    "address", request.receiverAddress()));
            param.put("callback", props.getCallbackUrl());
            param.put("orderid", request.orderNo());
            String paramJson = objectMapper.writeValueAsString(param);
            Map<String, String> form = signedForm(paramJson);
            Map<?, ?> resp = restClient.post().uri(ORDER_URL).body(form).retrieve().body(Map.class);
            Map<?, ?> data = resp == null ? null : (resp.get("data") instanceof Map<?, ?> d ? d : null);
            Object taskId = data == null ? null : data.get("taskId");
            Object billCode = data == null ? null : data.get("billCode");
            if (taskId == null) {
                throw new BizException(50020, "快递100 下单失败: " + resp);
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
        try {
            String paramJson = objectMapper.writeValueAsString(Map.of("taskId", taskNo));
            restClient.post().uri(CANCEL_URL).body(signedForm(paramJson)).retrieve().body(Map.class);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(50022, "快递100 取消异常: " + e.getMessage());
        }
    }

    @Override
    public ExpressTrace queryTrace(String expressNo) {
        if (props.isMock()) {
            return new ExpressTrace(expressNo, "顺丰速运", List.of(
                    new ExpressTrace.Node(nowText(), "mock 轨迹：包裹运输中"),
                    new ExpressTrace.Node(nowText(), "mock 轨迹：快递已揽收")));
        }
        requireConfigured();
        try {
            String paramJson = objectMapper.writeValueAsString(Map.of("num", expressNo));
            Map<?, ?> resp = restClient.post().uri(QUERY_URL).body(signedForm(paramJson)).retrieve().body(Map.class);
            Object data = resp == null ? null : resp.get("data");
            if (!(data instanceof List<?> nodes)) {
                throw new BizException(50023, "快递100 轨迹查询失败: " + resp);
            }
            List<ExpressTrace.Node> list = nodes.stream()
                    .map(o -> (Map<?, ?>) o)
                    .map(m -> new ExpressTrace.Node(String.valueOf(m.get("time")), String.valueOf(m.get("context"))))
                    .toList();
            return new ExpressTrace(expressNo, "顺丰速运", list);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(50024, "快递100 轨迹异常: " + e.getMessage());
        }
    }

    private void requireConfigured() {
        if (!StringUtils.hasText(props.getKey()) || !StringUtils.hasText(props.getCustomer())) {
            throw new BizException(50025, "快递100 未配置（需 EXPRESS_KEY / EXPRESS_CUSTOMER 环境变量）");
        }
        HttpGuard.requirePublicHttps(ORDER_URL);
    }

    private Map<String, String> signedForm(String paramJson) {
        Map<String, String> form = new HashMap<>();
        form.put("param", paramJson);
        form.put("key", props.getKey());
        form.put("customer", props.getCustomer());
        form.put("sign", md5Hex(paramJson + props.getKey() + props.getCustomer()).toUpperCase());
        return form;
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

    private static String nowText() {
        return java.time.LocalDateTime.now().toString();
    }
}
