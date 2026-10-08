package com.rephone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** 开启定时任务（支付单过期关闭等）。 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
