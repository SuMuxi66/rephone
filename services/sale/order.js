import { get, post, put } from '../request';

/** 在售商品列表：GET /api/wx/goods（支持 keyword/brand/conditionLevel/sort） */
export const fetchSaleGoods = (params) => get('/api/wx/goods', params);

/** 货架筛选条选项（在售商品的品牌与成色去重）：GET /api/wx/goods/filters */
export const fetchSaleGoodsFilters = () => get('/api/wx/goods/filters');

/** 商品详情：GET /api/wx/goods/{id} */
export const fetchSaleGoodsDetail = (id) => get(`/api/wx/goods/${id}`);

/** 商品质检报告：GET /api/wx/goods/{id}/inspection（该商品尚无报告时返回 null） */
export const fetchGoodsInspection = (id) => get(`/api/wx/goods/${id}/inspection`);

/** 创建出售订单（我买到的）：POST /api/wx/sale/order */
export const createSaleOrder = (payload) => post('/api/wx/sale/order', payload);

/** 支付：POST /api/wx/sale/order/{orderNo}/pay（后端当前为 mock 支付，接真实微信支付后替换） */
export const paySaleOrder = (orderNo) => post(`/api/wx/sale/order/${orderNo}/pay`);

/** 我的购买订单分页：GET /api/wx/sale/orders */
export const fetchSaleOrders = (params) => get('/api/wx/sale/orders', params);

/** 购买订单详情：GET /api/wx/sale/order/{orderNo} */
export const fetchSaleOrderDetail = (orderNo) => get(`/api/wx/sale/order/${orderNo}`);

/** 取消订单（仅待付款）：PUT /api/wx/sale/order/{orderNo}/cancel */
export const cancelSaleOrder = (orderNo, reason) =>
  put(`/api/wx/sale/order/${orderNo}/cancel`, { reason });

/** 确认收货（仅已发货）：PUT /api/wx/sale/order/{orderNo}/confirm */
export const confirmSaleOrder = (orderNo) => put(`/api/wx/sale/order/${orderNo}/confirm`);

/** 申请售后（仅退款，已付款/已发货可申请）：POST /api/wx/sale/order/{orderNo}/after-sale */
export const applySaleAfterSale = (orderNo, reason) =>
  post(`/api/wx/sale/order/${orderNo}/after-sale`, { reason });

/** 我的售后单：GET /api/wx/after-sale/list */
export const fetchMyAfterSales = (params) => get('/api/wx/after-sale/list', params);

/** 撤销售后：PUT /api/wx/after-sale/{asNo}/cancel */
export const cancelMyAfterSale = (asNo, reason) =>
  put(`/api/wx/after-sale/${asNo}/cancel`, { reason });
