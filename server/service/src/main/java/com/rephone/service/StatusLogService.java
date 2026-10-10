package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.context.TenantContextHolder;
import com.rephone.mapper.OrderStatusLogMapper;
import com.rephone.pojo.entity.OrderStatusLog;
import org.springframework.stereotype.Service;

/** 订单状态流转日志。P3 起由订单服务调用。 */
@Service
public class StatusLogService {

    private final OrderStatusLogMapper logMapper;

    public StatusLogService(OrderStatusLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    /**
     * 记录一次状态变更。租户取当前上下文，匿名链路（系统任务）记 0。
     */
    public void record(int orderType, String orderNo, int fromStatus, int toStatus,
                       int operatorType, Long operatorId, String remark) {
        OrderStatusLog entry = new OrderStatusLog();
        entry.setTenantId(TenantContextHolder.getOrDefault());
        entry.setOrderType(orderType);
        entry.setOrderNo(orderNo);
        entry.setFromStatus(fromStatus);
        entry.setToStatus(toStatus);
        entry.setOperatorType(operatorType);
        entry.setOperatorId(operatorId);
        entry.setRemark(remark);
        logMapper.insert(entry);
    }

    /** 统计某订单指定状态迁移（按操作者类型）的次数，用于拒绝报价等防滥用计数。 */
    public long countTransitions(String orderNo, int fromStatus, int toStatus, int operatorType) {
        Long count = logMapper.selectCount(new LambdaQueryWrapper<OrderStatusLog>()
                .eq(OrderStatusLog::getOrderNo, orderNo)
                .eq(OrderStatusLog::getFromStatus, fromStatus)
                .eq(OrderStatusLog::getToStatus, toStatus)
                .eq(OrderStatusLog::getOperatorType, operatorType));
        return count == null ? 0 : count;
    }
}
