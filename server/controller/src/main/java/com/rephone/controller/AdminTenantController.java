package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminTenantItem;
import com.rephone.service.AdminTenantService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端租户管理（列表/创建/改名启停）。 */
@RestController
@RequestMapping("/api/admin")
public class AdminTenantController {

    private final AdminTenantService tenantService;

    public AdminTenantController(AdminTenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/tenants")
    public R<Page<AdminTenantItem>> tenants(@RequestParam(required = false) String keyword,
                                            @RequestParam(defaultValue = "1") long pageNum,
                                            @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(tenantService.page(keyword, pageNum, pageSize));
    }

    @PostMapping("/tenant")
    public R<Map<String, Long>> create(@RequestBody(required = false) Map<String, String> body) {
        Long id = tenantService.create(
                body == null ? null : body.get("name"),
                body == null ? null : trimToNull(body.get("adminUsername")),
                body == null ? null : trimToNull(body.get("adminPassword")),
                body == null ? null : trimToNull(body.get("adminNickname")));
        return R.ok(Map.of("tenantId", id));
    }

    @PutMapping("/tenant/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        String name = body == null ? null : (String) body.get("name");
        Integer status = body == null ? null : (Integer) body.get("status");
        tenantService.update(id, name, status);
        return R.ok();
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
