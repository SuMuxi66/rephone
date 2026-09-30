/** 出售订单状态机（与后端 SaleOrder 常量保持一致） */
export const STATUS = {
  WAIT_PAY: 10,
  PAID: 20,
  SHIPPED: 30,
  DONE: 40,
  CANCELED: 80,
  REFUNDED: 90,
};

export const STATUS_DESC = {
  10: '待付款',
  20: '待发货',
  30: '待收货',
  40: '已完成',
  80: '已取消',
  90: '已退款',
};

export const statusDesc = (status) => STATUS_DESC[status] || '未知';

/**
 * 状态 → TDesign tag 主题色映射。
 * 说明：TDesign tag 无 info 主题，"待付款"归入 warning（待处理）。
 */
export const STATUS_THEME = {
  10: 'warning',
  20: 'primary',
  30: 'primary',
  40: 'success',
  80: 'default',
  90: 'danger',
};

export const statusTheme = (status) => STATUS_THEME[status] || 'default';
