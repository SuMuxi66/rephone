/** 回收订单状态机（与后端 RecycleOrder 常量保持一致） */
export const STATUS = {
  WAIT_SEND: 10,
  SHIPPING: 20,
  INSPECTING: 30,
  WAIT_CONFIRM: 40,
  PAID: 50,
  DONE: 60,
  CANCELED: 80,
};

export const STATUS_DESC = {
  10: '待寄出',
  20: '运输中',
  30: '质检中',
  40: '待确认',
  50: '已打款',
  60: '已完成',
  80: '已取消',
};

export const statusDesc = (status) => STATUS_DESC[status] || '未知';

/**
 * 状态 → TDesign tag 主题色映射。
 * 说明：TDesign tag 无 info 主题，"运输中"归入 primary（进行中）。
 */
export const STATUS_THEME = {
  10: 'warning',
  20: 'primary',
  30: 'warning',
  40: 'warning',
  50: 'success',
  60: 'success',
  80: 'default',
};

export const statusTheme = (status) => STATUS_THEME[status] || 'default';

/** 分 -> 元字符串 */
export const fen2yuan = (fen) => (fen == null ? '' : (fen / 100).toFixed(2));
