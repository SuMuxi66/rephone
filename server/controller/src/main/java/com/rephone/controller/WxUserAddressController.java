package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.entity.UserAddress;
import com.rephone.service.UserAddressService;
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

/** 用户收货地址簿（用户端，需登录）。 */
@RestController
@RequestMapping("/api/wx/address")
public class WxUserAddressController {

    private final UserAddressService addressService;

    public WxUserAddressController(UserAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public R<List<UserAddress>> list() {
        return R.ok(addressService.listMine());
    }

    @PostMapping
    public R<Map<String, Long>> create(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(Map.of("addressId", addressService.create(
                str(body, "name"), str(body, "phone"), str(body, "region"),
                str(body, "detail"), bool(body, "isDefault"))));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        addressService.update(id, str(body, "name"), str(body, "phone"),
                str(body, "region"), str(body, "detail"));
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return R.ok();
    }

    @PutMapping("/{id}/default")
    public R<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return R.ok();
    }

    private static String str(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static boolean bool(Map<String, Object> body, String key) {
        return Boolean.TRUE.equals(body == null ? null : body.get(key));
    }
}
