package com.rephone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源（机型图片等）：
 * - /img/** 优先读运行目录 data/img/（管理端上传，重启不丢、不打进 jar）；
 * - 回退 classpath:/img/（随包分发的预置图）。
 */
@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:./data/img/", "classpath:/img/");
    }
}
