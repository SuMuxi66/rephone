package com.rephone.controller;

import com.rephone.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 存活探针。 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public R<String> health() {
        return R.ok("pong");
    }
}
