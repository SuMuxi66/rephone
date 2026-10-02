package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.controller.support.AdminTokenFilter;
import com.rephone.pojo.dto.AdminUserItem;
import com.rephone.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端账号管理（用户列表/启停 + 管理员账号创建/重置密码）。 */
@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public R<Page<AdminUserItem>> users(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String role,
                                        @RequestParam(defaultValue = "1") long pageNum,
                                        @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(userService.page(keyword, role, pageNum, pageSize));
    }

    @PutMapping("/user/{id}/status")
    public R<Void> setStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.setStatus(id, body == null ? null : body.get("status"));
        return R.ok();
    }

    @PostMapping("/account")
    public R<Map<String, Long>> createAdmin(@RequestBody Map<String, String> body) {
        Long id = userService.createAdmin(
                body == null ? null : body.get("username"),
                body == null ? null : body.get("password"),
                body == null ? null : body.get("nickname"),
                body == null ? null : body.get("roleCode"),
                body == null ? null : parseLong(body.get("tenantId")));
        return R.ok(Map.of("userId", id));
    }

    @PutMapping("/account/{id}/role")
    public R<Void> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body,
                              HttpServletRequest request) {
        Long callerId = (Long) request.getAttribute(AdminTokenFilter.ATTR_USER_ID);
        userService.changeRole(callerId, id, body == null ? null : body.get("roleCode"));
        return R.ok();
    }

    @PutMapping("/account/{id}/password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body == null ? null : body.get("password"));
        return R.ok();
    }

    private static Long parseLong(String value) {
        try {
            return value == null || value.isBlank() ? null : Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
