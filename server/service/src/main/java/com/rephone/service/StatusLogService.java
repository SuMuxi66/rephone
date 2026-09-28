package com.rephone.service;

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
}
