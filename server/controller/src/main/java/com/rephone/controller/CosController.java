package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.CosUploadSign;
import com.rephone.service.CosSignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** COS 直传签名（用户端，需登录）。小程序拿签名后直传 COS，文件不经过后端。 */
@RestController
@RequestMapping("/api/wx/cos")
public class CosController {

    private final CosSignService cosSignService;

    public CosController(CosSignService cosSignService) {
        this.cosSignService = cosSignService;
    }

    @GetMapping("/upload-sign")
    public R<CosUploadSign> uploadSign(@RequestParam(defaultValue = "jpg") String ext) {
        return R.ok(cosSignService.signPutObject(ext));
    }
}
