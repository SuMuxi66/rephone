import { get, post } from './request';

/** 创建评价：POST /api/wx/comment（订单完成后可评一次） */
export const createComment = (payload) => post('/api/wx/comment', payload);

/** 按单查评价：GET /api/wx/comment/order/{orderNo}?orderType=（未评价时 data 为 null） */
export const fetchOrderComment = (orderNo, orderType) =>
  get('/api/wx/comment/order/' + orderNo, { orderType });
