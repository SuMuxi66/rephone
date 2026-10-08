package com.rephone.pojo.dto;

import java.util.List;

/** 评价展示行（images 为解析后的 COS key 列表；nickname 为冗余展示字段）。 */
public record CommentItem(Long id, Integer orderType, String orderNo, Long userId, String nickname,
                          Integer score, String content, List<String> images, String createTime) {
}
