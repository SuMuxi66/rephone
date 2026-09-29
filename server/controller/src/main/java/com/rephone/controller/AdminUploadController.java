package com.rephone.controller;

import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
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

/** 管理端图片上传：存运行目录 data/img/{category}/，返回 /img/** 访问地址。 */
@RestController
@RequestMapping("/api/admin/upload")
public class AdminUploadController {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_SIZE = 2 * 1024 * 1024L;

    private final Path root = Paths.get("data", "img");

    @PostMapping
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(defaultValue = "models") String category) {
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
        String safeCategory = category.matches("[a-z]{2,16}") ? category : "models";
        String filename = System.currentTimeMillis() + "_"
                + Integer.toUnsignedString(original.hashCode()) + "." + ext;
        try {
            Path dir = root.resolve(safeCategory);
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename).toAbsolutePath());
        } catch (IOException e) {
            throw new BizException(40065, "图片保存失败");
        }
        return R.ok(Map.of("url", "/img/" + safeCategory + "/" + filename));
    }
}
