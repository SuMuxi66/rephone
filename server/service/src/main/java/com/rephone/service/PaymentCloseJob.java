package com.rephone.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 过期未支付支付单自动关闭任务（默认每 5 分钟扫描一次，TTL 见 rephone.pay.pending-ttl-minutes）。 */
@Component
public class PaymentCloseJob {

    private static final Logger log = LoggerFactory.getLogger(PaymentCloseJob.class);

    private final PaymentService paymentService;

    public PaymentCloseJob(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Scheduled(cron = "${rephone.pay.close-cron:0 */5 * * * *}")
    public void closeExpired() {
        int closed = paymentService.closeExpired();
        if (closed > 0) {
            log.info("[pay-close] 过期未支付支付单已关闭 count={}", closed);
        }
    }
}
