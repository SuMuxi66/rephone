package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.UserAiProfileMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.AiProfileInfo;
import com.rephone.pojo.dto.UserProfileDetail;
import com.rephone.pojo.entity.User;
import com.rephone.pojo.entity.UserAiProfile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户 AI 画像分析：聚合画像统计数据交给 OpenAI 兼容接口生成中文画像，结果落 user_ai_profile。
 * 配置只走服务端环境变量（.env）：AI_BASE_URL / AI_API_KEY / AI_MODEL / AI_MOCK；
 * 未配置时明确报错，绝不影响画像基础数据。
 */
@Service
public class AiAnalysisService {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final String SYSTEM_PROMPT =
            "你是二手手机平台的用户增长分析师。根据用户行为统计数据，输出 200-300 字中文用户画像，"
                    + "覆盖：活跃度、品牌与机型偏好、消费力评估、可执行的营销建议。只输出分析正文，不要寒暄。";

    private final AdminUserProfileService userProfileService;
    private final UserAiProfileMapper userAiProfileMapper;
    private final UserMapper userMapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${rephone.ai.base-url:}")
    private String baseUrl;

    @Value("${rephone.ai.api-key:}")
    private String apiKey;

    @Value("${rephone.ai.model:}")
    private String model;

    @Value("${rephone.ai.mock:false}")
    private boolean mock;

    public AiAnalysisService(AdminUserProfileService userProfileService, UserAiProfileMapper userAiProfileMapper,
                             UserMapper userMapper) {
        this.userProfileService = userProfileService;
        this.userAiProfileMapper = userAiProfileMapper;
        this.userMapper = userMapper;
    }

    /** 生成（或重新生成）用户画像分析并落库。 */
    public AiProfileInfo generate(Long userId) {
        UserProfileDetail detail = userProfileService.detail(userId);
        String analysis;
        String usedModel;
        if (mock) {
            analysis = mockAnalysis(detail);
            usedModel = "mock";
        } else {
            if (!StringUtils.hasText(baseUrl) || !StringUtils.hasText(apiKey) || !StringUtils.hasText(model)) {
                throw new BizException(40099, "AI 分析未配置：请在 server/.env 配置 AI_BASE_URL / AI_API_KEY / AI_MODEL");
            }
            analysis = callChatCompletions(buildPrompt(detail));
            usedModel = model;
        }
        UserAiProfile saved = upsert(userId, analysis, usedModel);
        return new AiProfileInfo(userId, saved.getAnalysis(), saved.getModel(),
                saved.getUpdateTime() == null ? "" : saved.getUpdateTime().toString());
    }

    // ===== 内部 =====

    private UserAiProfile upsert(Long userId, String analysis, String usedModel) {
        Long tenantId = 0L;
        User user = userMapper.selectById(userId);
        if (user != null && user.getTenantId() != null) {
            tenantId = user.getTenantId();
        }
        UserAiProfile existing = userAiProfileMapper.selectOne(new QueryWrapper<UserAiProfile>()
                .eq("user_id", userId)
                .last("limit 1"));
        UserAiProfile profile = existing == null ? new UserAiProfile() : existing;
        profile.setTenantId(tenantId);
        profile.setUserId(userId);
        profile.setAnalysis(analysis);
        profile.setModel(usedModel);
        if (existing == null) {
            userAiProfileMapper.insert(profile);
        } else {
            userAiProfileMapper.updateById(profile);
        }
        return profile;
    }

    /** 把画像统计压成紧凑 JSON 作为用户提示词。 */
    private String buildPrompt(UserProfileDetail detail) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("昵称", detail.nickname());
        stats.put("打开小程序次数", detail.openTimes().size());
        stats.put("最近打开时间", detail.openTimes().isEmpty() ? "" : detail.openTimes().get(0));
        stats.put("浏览商品次数", detail.views().size());
        stats.put("品牌分布", detail.brandDist());
        stats.put("价格档分布", detail.priceBandDist());
        stats.put("最近浏览商品", detail.views().stream().map(UserProfileDetailView::of).toList());
        stats.put("出售订单数", detail.saleOrders());
        stats.put("回收订单数", detail.recycleOrders());
        stats.put("维修订单数", detail.repairOrders());
        try {
            return JSON.writeValueAsString(stats);
        } catch (Exception e) {
            throw new IllegalStateException("画像统计序列化失败", e);
        }
    }

    /** OpenAI 兼容 /chat/completions 调用；非 2xx 抛业务异常并带响应片段。 */
    private String callChatCompletions(String prompt) {
        String url = (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "chat/completions";
        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.4,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", prompt)));
        HttpRequest request;
        String respBody;
        try {
            request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                    .build();
            HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());
            respBody = resp.body();
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new BizException(40160, "AI 接口返回 " + resp.statusCode() + "："
                        + snippet(respBody));
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(40160, "AI 接口调用失败：" + e.getMessage());
        }
        try {
            return JSON.readTree(respBody).path("choices").path(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new BizException(40160, "AI 接口响应解析失败：" + snippet(respBody));
        }
    }

    /** 未配置真实模型时的规则模板，口径与真实画像一致。 */
    private String mockAnalysis(UserProfileDetail detail) {
        StringBuilder sb = new StringBuilder();
        sb.append("【规则模板】该用户累计打开小程序 ").append(detail.openTimes().size())
                .append(" 次，浏览商品 ").append(detail.views().size()).append(" 次。");
        if (!detail.brandDist().isEmpty()) {
            sb.append("品牌偏好集中在 ");
            detail.brandDist().forEach((k, v) -> sb.append(k).append("(").append(v).append(") "));
        }
        if (!detail.priceBandDist().isEmpty()) {
            sb.append("；浏览价格档分布：");
            detail.priceBandDist().forEach((k, v) -> sb.append(k).append("=").append(v).append(" "));
        }
        sb.append("。订单情况：出售 ").append(detail.saleOrders())
                .append(" 单、回收 ").append(detail.recycleOrders())
                .append(" 单、维修 ").append(detail.repairOrders()).append(" 单。");
        sb.append("建议：按其浏览价格档推送同档新上架商品，并对高活跃用户发放回收换新券提升转化。");
        return sb.toString();
    }

    private static String snippet(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > 200 ? text.substring(0, 200) : text;
    }

    /** 视图行仅保留提示词需要的字段，避免把时间等冗余喂给模型。 */
    private record UserProfileDetailView(String goodsName, String brand, Long priceFen) {

        static UserProfileDetailView of(com.rephone.pojo.dto.UserViewItem v) {
            return new UserProfileDetailView(v.goodsName(), v.brand(), v.priceFen());
        }
    }
}
