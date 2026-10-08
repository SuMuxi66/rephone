package com.rephone.service;

import com.rephone.common.exception.BizException;
import java.util.List;
import org.springframework.util.StringUtils;

/**
 * 图片 COS key 统一校验规则（维修/回收/评价共用一份，禁止各处自写口径）。
 * key 形如 comment/2026-10-08/xxx.jpg：非空、≤256 字符、白名单字符、拒绝 ".." 目录穿越。
 */
public final class ImageKeys {

    public static final int MAX_IMAGES = 9;

    private ImageKeys() {
    }

    public static boolean isValidKey(String key) {
        return StringUtils.hasText(key) && !key.contains("..")
                && key.matches("[A-Za-z0-9/_.\\-]{1,256}");
    }

    /** 纯 key 列表统一校验（数量与路径非法抛 40039）。 */
    public static void check(List<String> keys) {
        if (keys == null) {
            return;
        }
        if (keys.size() > MAX_IMAGES) {
            throw new BizException(40039, "图片数量或路径非法（最多 " + MAX_IMAGES + " 张）");
        }
        for (String key : keys) {
            if (!isValidKey(key)) {
                throw new BizException(40039, "图片数量或路径非法（最多 " + MAX_IMAGES + " 张）");
            }
        }
    }
}
