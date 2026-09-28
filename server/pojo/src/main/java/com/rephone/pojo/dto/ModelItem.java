package com.rephone.pojo.dto;

import java.util.List;

/** 机型列表项（含可选内存）。 */
public record ModelItem(Long id, String name, Integer releaseYear, List<String> storages) {
}
