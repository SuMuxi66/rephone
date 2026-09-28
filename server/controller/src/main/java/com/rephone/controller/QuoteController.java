package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.BrandItem;
import com.rephone.pojo.dto.ModelItem;
import com.rephone.pojo.dto.QuoteCalculateRequest;
import com.rephone.pojo.dto.QuoteResult;
import com.rephone.service.QuoteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 估价相关接口（需登录）。 */
@RestController
@RequestMapping("/api/wx")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/brands")
    public R<List<BrandItem>> brands() {
        return R.ok(quoteService.listBrands());
    }

    @GetMapping("/models")
    public R<List<ModelItem>> models(@RequestParam Long brandId) {
        return R.ok(quoteService.listModels(brandId));
    }

    @PostMapping("/quote/calculate")
    public R<QuoteResult> calculate(@RequestBody QuoteCalculateRequest request) {
        return R.ok(quoteService.calculate(request));
    }
}
