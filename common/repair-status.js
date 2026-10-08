// 维修工单状态：10 待确认 15 待检测 20 已预约/待寄出 25 已寄出 30 维修中 35 待报价 36 待确认报价 38 待支付 40 待验收/待回寄 45 回寄中 50 已完成 80 已取消
// 25/45 仅寄修（serviceType=20）使用；15/35/36/38 为报价模式（检测→报价→确认→支付→维修）
export const STATUS_DESC = {
  10: '待确认',
  15: '待检测',
  20: '已预约',
  25: '已寄出',
  30: '维修中',
  35: '待报价',
  36: '待确认报价',
  38: '待支付',
  40: '待验收',
  45: '回寄中',
  50: '已完成',
  80: '已取消',
};

/** 寄修视角下的状态文案（20/40 与上门含义不同） */
export const MAIL_IN_STATUS_DESC = {
  20: '待寄出',
  40: '待回寄',
};

export const STATUS_THEME = {
  10: 'default',
  15: 'warning',
  20: 'primary',
  25: 'primary',
  30: 'warning',
  35: 'warning',
  36: 'warning',
  38: 'danger',
  40: 'warning',
  45: 'warning',
  50: 'success',
  80: 'default',
};

export const statusDesc = (status, serviceType) => {
  if (Number(serviceType) === 20 && MAIL_IN_STATUS_DESC[status]) {
    return MAIL_IN_STATUS_DESC[status];
  }
  return STATUS_DESC[status] || '未知';
};

export const statusTheme = (status) => STATUS_THEME[status] || 'default';

/** 服务方式文案 */
export const SERVICE_TYPE_DESC = {
  10: '上门维修',
  20: '寄修',
};

/** 报价模式专属状态（详情页据此切报价时间线） */
export const QUOTE_STATUSES = [15, 35, 36, 38];

/** 详情时间线主流程：按服务方式分流（取消态不显示时间线） */
export const ONSITE_FLOW = [10, 20, 30, 40, 50];
export const MAIL_IN_FLOW = [10, 20, 25, 30, 40, 45, 50];
/** 报价模式时间线：接单检测 → 报价 → 确认 → 支付后并入维修中 */
export const ONSITE_QUOTE_FLOW = [10, 15, 35, 36, 38, 30, 40, 50];
export const MAIL_IN_QUOTE_FLOW = [10, 20, 25, 15, 35, 36, 38, 30, 40, 45, 50];

export const flowFor = (serviceType, quoteMode) => {
  const mailIn = Number(serviceType) === 20;
  if (quoteMode) {
    return mailIn ? MAIL_IN_QUOTE_FLOW : ONSITE_QUOTE_FLOW;
  }
  return mailIn ? MAIL_IN_FLOW : ONSITE_FLOW;
};

export const NODE_DESC = {
  10: '已提交预约，等待客服确认',
  15: '工程师检测中',
  '20_ONSITE': '预约已确认，工程师将按约定时间上门',
  '20_MAILIN': '预约已确认，请将设备寄出并填写运单号',
  25: '设备已寄出，等待商家收件',
  35: '检测完成，等待商家提交报价',
  36: '报价已提交，请确认价格',
  38: '报价已接受，请完成支付',
  30: '工程师检修中',
  '40_ONSITE': '维修完成，请验收设备',
  '40_MAILIN': '维修完成，商家将回寄设备',
  45: '设备回寄中，请注意查收',
  50: '完成，享受质保服务',
};

/** 按服务方式取时间线节点序列（label+desc）；quoteMode=有报价记录或处于报价专属状态 */
export const timelineFor = (status, serviceType, quoteMode) => {
  if (status === 80) {
    return null;
  }
  const flow = flowFor(serviceType, quoteMode);
  const current = flow.indexOf(Number(status));
  const mailIn = Number(serviceType) === 20;
  return {
    current,
    nodes: flow.map((s) => ({
      label: statusDesc(s, serviceType),
      desc:
        s === 20 ? (mailIn ? NODE_DESC['20_MAILIN'] : NODE_DESC['20_ONSITE'])
        : s === 40 ? (mailIn ? NODE_DESC['40_MAILIN'] : NODE_DESC['40_ONSITE'])
        : NODE_DESC[s] || '',
    })),
  };
};
