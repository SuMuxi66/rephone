// 维修工单状态：10 待确认 20 已预约 30 维修中 40 待验收 50 已完成 80 已取消
export const STATUS_DESC = {
  10: '待确认',
  20: '已预约',
  30: '维修中',
  40: '待验收',
  50: '已完成',
  80: '已取消',
};

export const STATUS_THEME = {
  10: 'default',
  20: 'primary',
  30: 'warning',
  40: 'warning',
  50: 'success',
  80: 'default',
};

export const statusDesc = (status) => STATUS_DESC[status] || '未知';
export const statusTheme = (status) => STATUS_THEME[status] || 'default';

/** 详情时间线主流程（取消态不显示时间线） */
export const FLOW = [10, 20, 30, 40, 50];

export const NODE_DESC = {
  10: '已提交预约，等待客服确认上门时间',
  20: '预约已确认，工程师将按约定时间上门',
  30: '工程师检修中',
  40: '维修完成，请验收设备',
  50: '验收完成，享受质保服务',
};
