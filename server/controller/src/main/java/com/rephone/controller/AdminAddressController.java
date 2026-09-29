package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.AdminAddressItem;
import com.rephone.service.UserAddressService;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端用户地址查看与删除（删除用于隐私合规场景）。 */
@RestController
@RequestMapping("/api/admin/addresses")
public class AdminAddressController {

    private final UserAddressService addressService;

    public AdminAddressController(UserAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public R<Page<AdminAddressItem>> list(@RequestParam(required = false) Long userId,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(addressService.adminPage(userId, keyword, pageNum, pageSize));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        addressService.adminDelete(id);
        return R.ok();
    }
}
