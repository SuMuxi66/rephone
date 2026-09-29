package com.rephone.controller.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 管理端简易鉴权：校验 Authorization: Bearer &lt;ADMIN_TOKEN&gt;。
 * 适用于 P5 前的最小管理闭环；正式 RBAC 在 P7 落地。
 */
public class AdminTokenFilter extends OncePerRequestFilter {

    private final String adminToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminTokenFilter(String adminToken) {
        this.adminToken = adminToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!StringUtils.hasText(adminToken)) {
            writeError(response, 50030, "管理端未配置 ADMIN_TOKEN，拒绝访问");
            return;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.equals("Bearer " + adminToken)) {
            writeError(response, 40100, "管理端鉴权失败");
            return;
        }
        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("code", code, "message", message, "data", ""));
    }
}
