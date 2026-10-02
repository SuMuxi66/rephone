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
