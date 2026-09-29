package com.rephone.pojo.dto;

import java.util.List;

/** 机型列表项（含可选内存与图片 URL，image 为空时前端回退品牌 logo）。 */
public record ModelItem(Long id, String name, String image, Integer releaseYear, List<String> storages) {
}
