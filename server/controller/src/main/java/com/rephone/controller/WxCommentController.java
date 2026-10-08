package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.CommentItem;
import com.rephone.service.CommentService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 订单评价（用户端，需登录）。订单完成后可评价一次：星级 + 文字 + 图片。 */
@RestController
@RequestMapping("/api/wx/comment")
public class WxCommentController {

    private final CommentService commentService;

    public WxCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public R<Map<String, Long>> create(@RequestBody(required = false) Map<String, Object> body) {
        Long id = commentService.create(
                intVal(body, "orderType"), str(body, "orderNo"), intVal(body, "score"),
                str(body, "content"), strList(body, "images"));
        return R.ok(Map.of("commentId", id));
    }

    @GetMapping("/order/{orderNo}")
    public R<CommentItem> getByOrder(@PathVariable String orderNo,
                                     @RequestParam(required = false) Integer orderType) {
        return R.ok(commentService.getByOrder(orderType, orderNo));
    }

    private static String str(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static Integer intVal(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        if (v instanceof Number n) {
            return n.intValue();
        }
        try {
            return v == null ? null : Integer.valueOf(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> strList(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        if (!(v instanceof List<?> list)) {
            return List.of();
        }
        return list.stream().map(String::valueOf).toList();
    }
}
