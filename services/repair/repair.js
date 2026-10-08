import { get, post, request } from '../request';

export { fetchBrands } from '../recycle/quote';
export { fetchCosUploadSign } from '../recycle/order';

/** 全量机型（维修用，不受估价基准价过滤）：GET /api/wx/repair/models?brandId= */
export const fetchModels = (brandId) => get('/api/wx/repair/models', { brandId });

/** 维修项目分组字典（含该机型实时价）：GET /api/wx/repair/items?modelId= */
export const fetchRepairItems = (modelId) => get('/api/wx/repair/items', { modelId });

/** 创建维修预约单（只传项目 ID，金额服务端计价）：POST /api/wx/repair/order */
export const createRepairOrder = (payload) => post('/api/wx/repair/order', payload);

/** 维修单列表：GET /api/wx/repair/orders */
export const fetchRepairOrders = (params) => get('/api/wx/repair/orders', params);

/** 维修单详情：GET /api/wx/repair/order/{orderNo} */
export const fetchRepairOrderDetail = (orderNo) => get(`/api/wx/repair/order/${orderNo}`);

/** 取消预约（上门仅待确认；寄修含待寄出）：PUT /api/wx/repair/order/{orderNo}/cancel */
export const cancelRepairOrder = (orderNo, reason) =>
  request({ url: `/api/wx/repair/order/${orderNo}/cancel`, method: 'PUT', data: { reason } });

/** 寄修：填写寄出运单号（待寄出态），状态 20→25：PUT /api/wx/repair/order/{orderNo}/express */
export const fillRepairExpress = (orderNo, payload) =>
  request({ url: `/api/wx/repair/order/${orderNo}/express`, method: 'PUT', data: payload });

/** 寄修：寄出运单轨迹（30 分钟快照缓存）：GET /api/wx/repair/order/{orderNo}/trace */
export const fetchRepairTrace = (orderNo) => get(`/api/wx/repair/order/${orderNo}/trace`);

/** 寄修：回寄运单轨迹：GET /api/wx/repair/order/{orderNo}/return-trace */
export const fetchRepairReturnTrace = (orderNo) => get(`/api/wx/repair/order/${orderNo}/return-trace`);

/** 寄修：确认收货（回寄中 45→50）：PUT /api/wx/repair/order/{orderNo}/confirm */
export const confirmRepairReceipt = (orderNo) =>
  request({ url: `/api/wx/repair/order/${orderNo}/confirm`, method: 'PUT' });

// ===== 报价模式：检测 → 报价 → 用户确认 → 支付 → 维修 =====

/** 查看最近一次报价（未报价返回 null）：GET /api/wx/repair/order/{orderNo}/quote */
export const fetchRepairQuote = (orderNo) =>
  get('/api/wx/repair/order/' + orderNo + '/quote');

/** 接受报价（36→38 待支付）：PUT /api/wx/repair/order/{orderNo}/confirm-price */
export const confirmRepairPrice = (orderNo) =>
  request({ url: `/api/wx/repair/order/${orderNo}/confirm-price`, method: 'PUT' });

/** 拒绝报价（36→20 退回待处理，可重新检测报价）：PUT /api/wx/repair/order/{orderNo}/reject-price */
export const rejectRepairPrice = (orderNo) =>
  request({ url: `/api/wx/repair/order/${orderNo}/reject-price`, method: 'PUT' });

/** 按已接受报价发起支付（38→30 维修中）：PUT /api/wx/repair/order/{orderNo}/pay */
export const payRepairOrder = (orderNo) =>
  request({ url: `/api/wx/repair/order/${orderNo}/pay`, method: 'PUT' });

/**
 * 发起维修支付：mock 模式后端直接闭环（返回 mock=true）；真实模式拿签名参数调起微信收银台。
 * 返回支付响应；用户取消/失败时抛错（errMsg）。
 */
export const requestRepairPayment = async (orderNo) => {
  const res = await payRepairOrder(orderNo);
  if (res && res.mock) {
    return res;
  }
  await new Promise((resolve, reject) => {
    wx.requestPayment({
      appId: res.appId,
      timeStamp: res.timeStamp,
      nonceStr: res.nonceStr,
      package: res.package,
      signType: res.signType || 'MD5',
      paySign: res.paySign,
      success: resolve,
      fail: (e) => reject(new Error((e && e.errMsg) || '支付未完成')),
    });
  });
  return res;
};
