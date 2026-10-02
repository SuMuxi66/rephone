package com.rephone.controller.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 请求体大小硬上限（默认 1MB），收口安全补测 SEC-5（JSON 体积无上限 + Jackson 解析放大理论面）：
 * - 带 Content-Length 且超限：直接 413（41300），请求不进入任何过滤器与业务逻辑；
 * - chunked 无长度声明：包装为限长流，读取超限抛 IOException，
 *   由 Jackson 包装为 HttpMessageNotReadableException → 归一 40002「请求体格式错误」；
 * - multipart 跳过：文件上传体积由 Spring multipart 自身上限约束，与 JSON 上限互不干扰。
 */
public class RequestBodySizeFilter extends OncePerRequestFilter {

    public static final long DEFAULT_MAX_BODY_BYTES = 1024L * 1024L;

    private static final Logger log = LoggerFactory.getLogger(RequestBodySizeFilter.class);

    private final long maxBytes;

    public RequestBodySizeFilter() {
        this(DEFAULT_MAX_BODY_BYTES);
    }

    public RequestBodySizeFilter(long maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            chain.doFilter(request, response);
            return;
        }
        if (request.getContentLengthLong() > maxBytes) {
            log.warn("[body-limit] 拒绝超限请求体 uri={} contentLength={} max={}",
                    request.getRequestURI(), request.getContentLengthLong(), maxBytes);
            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":41300,\"message\":\"请求体过大\",\"data\":\"\"}");
            return;
        }
        chain.doFilter(new SizeCappedRequest(request), response);
    }

    /** 限长请求体：read() 本身声明 IOException，超限直接抛，Jackson 会包装成 HttpMessageNotReadableException。 */
    private final class SizeCappedRequest extends HttpServletRequestWrapper {

        private long total;

        SizeCappedRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ServletInputStream origin = super.getInputStream();
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    int b = origin.read();
                    count(b >= 0 ? 1 : 0);
                    return b;
                }

                @Override
                public int read(byte[] buf, int off, int len) throws IOException {
                    int n = origin.read(buf, off, len);
                    count(n);
                    return n;
                }

                @Override
                public boolean isFinished() {
                    return origin.isFinished();
                }

                @Override
                public boolean isReady() {
                    return origin.isReady();
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    origin.setReadListener(listener);
                }
            };
        }

        private void count(int justRead) throws IOException {
            if (justRead > 0) {
                total += justRead;
                if (total > maxBytes) {
                    throw new IOException("request body exceeds " + maxBytes + " bytes");
                }
            }
        }
    }
}
