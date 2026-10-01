import { get, post, request } from '../request';

/** 创建回收订单：POST /api/wx/recycle/order */
export const createRecycleOrder = (payload) => post('/api/wx/recycle/order', payload);

/** 回收订单列表：GET /api/wx/recycle/orders */
export const fetchRecycleOrders = (params) => get('/api/wx/recycle/orders', params);

/** 回收订单详情：GET /api/wx/recycle/order/{orderNo} */
export const fetchRecycleOrderDetail = (orderNo) => get(`/api/wx/recycle/order/${orderNo}`);

/** 填写运单号：PUT /api/wx/recycle/order/{orderNo}/express */
export const fillExpressNo = (orderNo, data) =>
  request({ url: `/api/wx/recycle/order/${orderNo}/express`, method: 'PUT', data });

/** 快递公司字典：GET /api/wx/express/companies */
export const fetchExpressCompanies = () => get('/api/wx/express/companies');

/** 物流轨迹：GET /api/wx/recycle/order/{orderNo}/trace（后端 30 分钟内返回本地快照） */
export const fetchOrderTrace = (orderNo) => get(`/api/wx/recycle/order/${orderNo}/trace`);

/** 取消订单：PUT /api/wx/recycle/order/{orderNo}/cancel */
export const cancelRecycleOrder = (orderNo, reason) =>
  request({ url: `/api/wx/recycle/order/${orderNo}/cancel`, method: 'PUT', data: { reason } });

/** 确认打款：PUT /api/wx/recycle/order/{orderNo}/confirm */
export const confirmPayout = (orderNo) =>
  request({ url: `/api/wx/recycle/order/${orderNo}/confirm`, method: 'PUT' });

/** COS 直传签名：GET /api/wx/cos/upload-sign */
export const fetchCosUploadSign = (ext) => get('/api/wx/cos/upload-sign', { ext });
