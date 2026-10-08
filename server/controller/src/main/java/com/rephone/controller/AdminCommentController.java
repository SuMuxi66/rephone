package com.rephone.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.result.R;
import com.rephone.pojo.dto.CommentItem;
import com.rephone.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 评价管理（管理端）。分页查看全部评价，支持类型/单号/星级筛选。 */
@RestController
@RequestMapping("/api/admin/comment")
public class AdminCommentController {

    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/list")
    public R<Page<CommentItem>> list(@RequestParam(required = false) Integer orderType,
                                     @RequestParam(required = false) String orderNo,
                                     @RequestParam(required = false) Integer score,
                                     @RequestParam(defaultValue = "1") long pageNum,
                                     @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(commentService.adminPage(orderType, orderNo, score, pageNum, pageSize));
    }
}
