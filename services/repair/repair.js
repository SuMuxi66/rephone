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

/** 取消预约（仅待确认）：PUT /api/wx/repair/order/{orderNo}/cancel */
export const cancelRepairOrder = (orderNo, reason) =>
  request({ url: `/api/wx/repair/order/${orderNo}/cancel`, method: 'PUT', data: { reason } });
