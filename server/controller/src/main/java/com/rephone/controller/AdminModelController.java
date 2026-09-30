package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminBrandItem;
import com.rephone.pojo.dto.AdminModelCreateRequest;
import com.rephone.pojo.dto.AdminModelItem;
import com.rephone.service.AdminModelService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端机型管理：品牌/机型/内存基准价（估价引擎数据源）。 */
@RestController
@RequestMapping("/api/admin")
public class AdminModelController {

    private final AdminModelService modelService;

    public AdminModelController(AdminModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/brands")
    public R<List<AdminBrandItem>> brands() {
        return R.ok(modelService.brands());
    }

    @PostMapping("/brand")
    public R<Map<String, Long>> createBrand(@RequestBody Map<String, String> body) {
        return R.ok(Map.of("brandId", modelService.createBrand(body == null ? null : body.get("name"))));
    }

    @GetMapping("/models")
    public R<Page<AdminModelItem>> models(@RequestParam Long brandId,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(modelService.adminPage(brandId, keyword, pageNum, pageSize));
    }

    @PostMapping("/model")
    public R<Map<String, Long>> createModel(@RequestBody AdminModelCreateRequest request) {
        return R.ok(Map.of("modelId", modelService.createModel(request)));
    }

    @PutMapping("/model/{id}")
    public R<Void> updateModel(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = body == null ? null : (String) body.get("name");
        String image = body == null ? null : (String) body.get("image");
        Integer releaseYear = body == null || body.get("releaseYear") == null
                ? null : Integer.valueOf(String.valueOf(body.get("releaseYear")));
        modelService.updateModel(id, name, releaseYear, image);
        return R.ok();
    }

    @PutMapping("/model/{id}/price")
    public R<Void> updatePrice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String storage = body == null ? null : (String) body.get("storage");
        BigDecimal priceYuan = body == null || body.get("priceYuan") == null
                ? null : new BigDecimal(String.valueOf(body.get("priceYuan")));
        modelService.updatePrice(id, storage, priceYuan);
        return R.ok();
    }
}
