package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.UserProfileCard;
import com.rephone.pojo.dto.UserProfileDetail;
import com.rephone.service.AdminUserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 商家端用户行为画像（权限点 profile:read，租户内自动过滤/平台超管全量）。 */
@RestController
@RequestMapping("/api/admin/user-profile")
public class AdminUserProfileController {

    private final AdminUserProfileService userProfileService;

    public AdminUserProfileController(AdminUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/page")
    public R<Page<UserProfileCard>> page(@RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "20") long pageSize,
                                         @RequestParam(required = false) String keyword) {
        return R.ok(userProfileService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{userId}/detail")
    public R<UserProfileDetail> detail(@PathVariable Long userId) {
        return R.ok(userProfileService.detail(userId));
    }
}
