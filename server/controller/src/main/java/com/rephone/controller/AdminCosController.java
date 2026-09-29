package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.CosUploadSign;
import com.rephone.service.CosSignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** COS 直传签名（管理端）。质检照片等由管理后台直传 COS，不经过后端。 */
@RestController
@RequestMapping("/api/admin/cos")
public class AdminCosController {

    private final CosSignService cosSignService;

    public AdminCosController(CosSignService cosSignService) {
        this.cosSignService = cosSignService;
    }

    @GetMapping("/upload-sign")
    public R<CosUploadSign> uploadSign(@RequestParam(defaultValue = "jpg") String ext) {
        return R.ok(cosSignService.signPutObject(ext));
    }
}
