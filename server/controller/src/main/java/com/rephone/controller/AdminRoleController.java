package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminRoleItem;
import com.rephone.service.AdminRoleService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理端角色管理（列表/创建/编辑/删除）。 */
@RestController
@RequestMapping("/api/admin")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    public AdminRoleController(AdminRoleService adminRoleService) {
        this.adminRoleService = adminRoleService;
    }

    @GetMapping("/roles")
    public R<List<AdminRoleItem>> roles() {
        return R.ok(adminRoleService.listAll());
    }

    @PostMapping("/role")
    public R<Map<String, Long>> create(@RequestBody(required = false) Map<String, Object> body) {
        Long id = adminRoleService.createRole(
                body == null ? null : str(body.get("roleCode")),
                body == null ? null : str(body.get("roleName")),
                body == null ? null : strings(body.get("permissions")),
                body == null ? null : str(body.get("remark")));
        return R.ok(Map.of("roleId", id));
    }

    @PutMapping("/role/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        adminRoleService.updateRole(id,
                body == null ? null : str(body.get("roleName")),
                body == null ? null : strings(body.get("permissions")),
                body == null ? null : intOf(body.get("status")),
                body == null ? null : str(body.get("remark")));
        return R.ok();
    }

    @DeleteMapping("/role/{id}")
    public R<Void> delete(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        return R.ok();
    }

    private static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Integer intOf(Object value) {
        return value == null ? null : Integer.valueOf(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private static List<String> strings(Object value) {
        return value instanceof List ? (List<String>) value : null;
    }
}
