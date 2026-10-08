package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.CommentMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.CommentItem;
import com.rephone.pojo.entity.Comment;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
import com.rephone.pojo.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 订单评价：维修/回收/出售三类订单完成后，下单用户可评一次（星级+文字+图片）。
 * 可评价条件 = 订单存在 + 归属当前用户 + 状态已完成；防重复靠唯一键 + 前置校验。
 */
@Service
public class CommentService {

    private static final ObjectMapper JSON = new ObjectMapper();

    private static final Set<Integer> TYPES = Set.of(Comment.TYPE_RECYCLE, Comment.TYPE_SALE, Comment.TYPE_REPAIR);
    private static final int MAX_IMAGES = 9;

    /** 各类型订单「已完成」状态值：回收 60 / 出售 40 / 维修 50 */
    private static final Map<Integer, Integer> DONE_STATUS = Map.of(
            Comment.TYPE_RECYCLE, 60, Comment.TYPE_SALE, 40, Comment.TYPE_REPAIR, 50);

    private final CommentMapper commentMapper;
    private final RecycleOrderMapper recycleOrderMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final UserMapper userMapper;

    public CommentService(CommentMapper commentMapper, RecycleOrderMapper recycleOrderMapper,
                          SaleOrderMapper saleOrderMapper, RepairOrderMapper repairOrderMapper,
                          UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.recycleOrderMapper = recycleOrderMapper;
        this.saleOrderMapper = saleOrderMapper;
        this.repairOrderMapper = repairOrderMapper;
        this.userMapper = userMapper;
    }

    // ===== 用户端 =====

    public Long create(Integer orderType, String orderNo, Integer score, String content, List<String> images) {
        validate(orderType, orderNo, score, content, images);
        Long userId = UserContextHolder.require().userId();
        Long tenantId = requireCompletedOwnedOrder(orderType, orderNo, userId);

        Long exists = commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getOrderType, orderType)
                .eq(Comment::getOrderNo, orderNo));
        if (exists != null && exists > 0) {
            throw new BizException(40079, "该订单已评价，无需重复提交");
        }

        Comment comment = new Comment();
        comment.setTenantId(tenantId);
        comment.setOrderType(orderType);
        comment.setOrderNo(orderNo);
        comment.setUserId(userId);
        comment.setScore(score);
        String text = content == null ? "" : content.trim();
        comment.setContent(text.isEmpty() ? null : text);
        List<String> imgs = normalizeImages(images);
        if (!imgs.isEmpty()) {
            comment.setImagesJson(writeImages(imgs));
        }
        commentMapper.insert(comment);
        return comment.getId();
    }

    /** 按单查评价；未评价返回 null（前端以此区分「立即评价/已评价」）。 */
    public CommentItem getByOrder(Integer orderType, String orderNo) {
        validate(orderType, orderNo, 5, null, null);
        Comment comment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getOrderType, orderType)
                .eq(Comment::getOrderNo, orderNo)
                .last("limit 1"));
        return comment == null ? null : toItem(comment);
    }

    // ===== 管理端 =====

    public Page<CommentItem> adminPage(Integer orderType, String orderNo, Integer score,
                                       long pageNum, long pageSize) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getId);
        if (orderType != null && orderType > 0) {
            wrapper.eq(Comment::getOrderType, orderType);
        }
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Comment::getOrderNo, orderNo.trim());
        }
        if (score != null && score > 0) {
            wrapper.eq(Comment::getScore, score);
        }
        Page<Comment> page = commentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Map<Long, User> users = page.getRecords().isEmpty() ? Map.of()
                : userMapper.selectList(new LambdaQueryWrapper<User>()
                        .in(User::getId, page.getRecords().stream().map(Comment::getUserId).toList()))
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        Page<CommentItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream()
                .map(c -> new CommentItem(c.getId(), c.getOrderType(), c.getOrderNo(), c.getUserId(),
                        users.containsKey(c.getUserId()) ? users.get(c.getUserId()).getNickname() : "",
                        c.getScore(), c.getContent(), parseImages(c.getImagesJson()),
                        c.getCreateTime() == null ? "" : c.getCreateTime().toString()))
                .toList());
        return result;
    }

    // ===== 内部 =====

    private void validate(Integer orderType, String orderNo, Integer score, String content, List<String> images) {
        if (orderType == null || !TYPES.contains(orderType)) {
            throw new BizException(40071, "订单类型无效");
        }
        if (!StringUtils.hasText(orderNo) || orderNo.length() > 32) {
            throw new BizException(40072, "订单号不能为空且不超过 32 字");
        }
        if (score != null && (score < 1 || score > 5)) {
            throw new BizException(40073, "评分需为 1-5 星");
        }
        if (content != null && content.trim().length() > 500) {
            throw new BizException(40074, "评价内容不能超过 500 字");
        }
        if (images != null && images.size() > MAX_IMAGES) {
            throw new BizException(40075, "评价图片最多 " + MAX_IMAGES + " 张");
        }
    }

    /** 校验订单存在、归属当前用户、已完成；返回订单租户（评价落同租户）。 */
    private Long requireCompletedOwnedOrder(Integer orderType, String orderNo, Long userId) {
        Long tenantId;
        Long ownerId;
        Integer status;
        if (orderType == Comment.TYPE_RECYCLE) {
            RecycleOrder order = recycleOrderMapper.selectOne(new LambdaQueryWrapper<RecycleOrder>()
                    .eq(RecycleOrder::getOrderNo, orderNo).last("limit 1"));
            if (order == null) {
                throw new BizException(40407, "订单不存在");
            }
            tenantId = order.getTenantId();
            ownerId = order.getUserId();
            status = order.getStatus();
        } else if (orderType == Comment.TYPE_SALE) {
            SaleOrder order = saleOrderMapper.selectOne(new LambdaQueryWrapper<SaleOrder>()
                    .eq(SaleOrder::getOrderNo, orderNo).last("limit 1"));
            if (order == null) {
                throw new BizException(40407, "订单不存在");
            }
            tenantId = order.getTenantId();
            ownerId = order.getUserId();
            status = order.getStatus();
        } else {
            RepairOrder order = repairOrderMapper.selectOne(new LambdaQueryWrapper<RepairOrder>()
                    .eq(RepairOrder::getOrderNo, orderNo).last("limit 1"));
            if (order == null) {
                throw new BizException(40407, "订单不存在");
            }
            tenantId = order.getTenantId();
            ownerId = order.getUserId();
            status = order.getStatus();
        }
        if (!ownerId.equals(userId)) {
            throw new BizException(40302, "只能评价自己的订单");
        }
        if (!DONE_STATUS.get(orderType).equals(status)) {
            throw new BizException(40076, "订单完成后才能评价");
        }
        return tenantId;
    }

    /** COS key 白名单字符，防把任意字符串当图片地址存储。 */
    private static List<String> normalizeImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .peek(key -> {
                    if (!key.matches("[A-Za-z0-9/_.\\-]{1,256}")) {
                        throw new BizException(40077, "图片标识不合法");
                    }
                })
                .toList();
    }

    private static String writeImages(List<String> images) {
        try {
            return JSON.writeValueAsString(images);
        } catch (Exception e) {
            throw new IllegalStateException("评价图片序列化失败", e);
        }
    }

    private static List<String> parseImages(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return JSON.readValue(json, JSON.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return List.of();
        }
    }

    private CommentItem toItem(Comment c) {
        User user = userMapper.selectById(c.getUserId());
        return new CommentItem(c.getId(), c.getOrderType(), c.getOrderNo(), c.getUserId(),
                user == null ? "" : user.getNickname(), c.getScore(), c.getContent(),
                parseImages(c.getImagesJson()), c.getCreateTime() == null ? "" : c.getCreateTime().toString());
    }
}
