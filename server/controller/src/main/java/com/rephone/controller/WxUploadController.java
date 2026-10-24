package com.rephone.controller;

import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import com.rephone.service.CosSignService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户端图片上传的本地存储兜底（未配 COS 密钥时由前端 uploadToCos 自动切换调用）：
 * 写运行目录 data/img/{dir}/（与管理端上传同一根目录），返回 /img/** 访问路径（WebResourceConfig 静态回显）。
 * 配置 COS 后前端走直传，本端点不再被调用，但保持可用以便排查。
 */
@RestController
@RequestMapping("/api/wx/upload")
public class WxUploadController {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_SIZE = 2 * 1024 * 1024L;

    private final Path root = Paths.get("data", "img");

    @PostMapping("/image")
    public R<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestParam(defaultValue = "avatar") String dir) {
        if (file == null || file.isEmpty()) {
            throw new BizException(40065, "请选择图片文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BizException(40065, "图片不能超过 2MB");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BizException(40065, "仅支持 jpg/png/webp 图片");
        }
        String safeDir = CosSignService.allowedDir(dir) ? dir : "avatar";
        String filename = System.currentTimeMillis() + "_"
                + Integer.toUnsignedString(original.hashCode()) + "." + ext;
        try {
            Path target = root.resolve(safeDir);
            Files.createDirectories(target);
            file.transferTo(target.resolve(filename).toAbsolutePath());
        } catch (IOException e) {
            throw new BizException(40065, "图片保存失败");
        }
        return R.ok(Map.of("url", "/img/" + safeDir + "/" + filename));
    }
}
