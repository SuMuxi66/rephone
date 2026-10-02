import http from './http';

// 金额：接口一律分(long)，界面一律元(2 位小数)
export const fen2yuan = (fen) => (fen == null ? '--' : (fen / 100).toFixed(2));
export const yuan2fen = (yuan) => Math.round(Number(yuan) * 100);

export const login = (username, password) => http.post('/admin/auth/login', { username, password });

export const fetchMe = () => http.get('/admin/auth/me');

// ===== 回收单 =====
export const fetchRecycleOrders = (params) => http.get('/admin/recycle/orders', { params });
export const fetchRecycleOrderDetail = (orderNo) => http.get(`/admin/recycle/order/${orderNo}`);
export const changeRecycleStatus = (orderNo, toStatus, remark) =>
  http.put(`/admin/recycle/order/${orderNo}/status`, { toStatus, remark });
export const submitInspection = (orderNo, payload) =>
  http.post(`/admin/recycle/order/${orderNo}/inspection`, payload);
export const payoutRecycle = (orderNo) => http.post(`/admin/recycle/order/${orderNo}/payout`);

// ===== 维修单 =====
export const fetchRepairOrders = (params) => http.get('/admin/repair/orders', { params });
export const fetchRepairOrderDetail = (orderNo) => http.get(`/admin/repair/order/${orderNo}`);
export const changeRepairStatus = (orderNo, toStatus, remark) =>
  http.put(`/admin/repair/order/${orderNo}/status`, { toStatus, remark });

// ===== 账号管理 =====
export const fetchUsers = (params) => http.get('/admin/users', { params });
export const setUserStatus = (id, status) => http.put(`/admin/user/${id}/status`, { status });
export const createAdminAccount = (payload) => http.post('/admin/account', payload);
export const resetAdminPassword = (id, password) => http.put(`/admin/account/${id}/password`, { password });

// ===== 用户地址管理 =====
export const fetchAddresses = (params) => http.get('/admin/addresses', { params });
export const deleteAddress = (id) => http.delete(`/admin/addresses/${id}`);

// ===== 机型管理 =====
export const fetchBrands = () => http.get('/admin/brands');
export const createBrand = (name) => http.post('/admin/brand', { name });
export const fetchModels = (brandId, params) => http.get('/admin/models', { params: { brandId, ...params } });
export const createModel = (payload) => http.post('/admin/model', payload);
export const updateModel = (id, payload) => http.put(`/admin/model/${id}`, payload);
export const updateModelPrice = (id, storage, priceYuan) =>
  http.put(`/admin/model/${id}/price`, { storage, priceYuan });

// ===== 商品/售出/售后 =====
export const fetchGoods = (params) => http.get('/admin/goods', { params });
export const createGoods = (payload) => http.post('/admin/goods', payload);
export const updateGoods = (id, payload) => http.put(`/admin/goods/${id}`, payload);
export const setGoodsStatus = (id, status) => http.put(`/admin/goods/${id}/status`, { status });

/**
 * 上传图片：POST /api/admin/upload（multipart）。
 * 后端已限制 2MB 与 jpg/png/webp，category 决定落到 data/img/<category>/。
 * 不手动设置 Content-Type，交给 axios 带 boundary。
 */
export const uploadImage = (formData) => http.post('/admin/upload', formData);

export const fetchGoodsInspection = (id) => http.get(`/admin/goods/${id}/inspection`);
export const saveGoodsInspection = (id, payload) => http.post(`/admin/goods/${id}/inspection`, payload);

export const fetchSaleOrders = (params) => http.get('/admin/sale/orders', { params });
export const fetchSaleOrderDetail = (orderNo) => http.get(`/admin/sale/order/${orderNo}`);
export const shipSaleOrder = (orderNo, payload) => http.put(`/admin/sale/order/${orderNo}/ship`, payload);
export const refundSaleOrder = (orderNo, reason) =>
  http.post(`/admin/sale/order/${orderNo}/refund`, { reason });

export const fetchAfterSales = (params) => http.get('/admin/after-sales', { params });
export const fetchAfterSaleDetail = (asNo) => http.get(`/admin/after-sales/${asNo}`);
export const agreeAfterSale = (asNo, adminRemark) =>
  http.put(`/admin/after-sales/${asNo}/agree`, { adminRemark });
export const rejectAfterSale = (asNo, adminRemark) =>
  http.put(`/admin/after-sales/${asNo}/reject`, { adminRemark });

export const SALE_STATUS = {
  10: { label: '待付款', type: 'info' },
  20: { label: '待发货', type: 'primary' },
  30: { label: '已发货', type: 'warning' },
  40: { label: '已完成', type: 'success' },
  80: { label: '已取消', type: 'info' },
  90: { label: '已退款', type: 'danger' },
};

export const AFTER_SALE_STATUS = {
  10: { label: '待审核', type: 'warning' },
  30: { label: '已退款', type: 'success' },
  40: { label: '已拒绝', type: 'danger' },
  80: { label: '已撤销', type: 'info' },
};

// ===== P7：租户 / 角色 / 财务对账 / 数据看板 =====
export const fetchTenants = (params) => http.get('/admin/tenants', { params });
export const createTenant = (payload) => http.post('/admin/tenant', payload);
export const updateTenant = (id, payload) => http.put(`/admin/tenant/${id}`, payload);

export const fetchRoles = () => http.get('/admin/roles');
export const createRole = (payload) => http.post('/admin/role', payload);
export const updateRole = (id, payload) => http.put(`/admin/role/${id}`, payload);
export const deleteRole = (id) => http.delete(`/admin/role/${id}`);
export const changeAccountRole = (id, roleCode) => http.put(`/admin/account/${id}/role`, { roleCode });

export const fetchFinanceSummary = (params) => http.get('/admin/finance/summary', { params });
export const fetchFinanceFlows = (params) => http.get('/admin/finance/flows', { params });

export const fetchDashboardSummary = () => http.get('/admin/dashboard/summary');
export const fetchDashboardTrend = (days) => http.get('/admin/dashboard/trend', { params: { days } });
export const fetchDashboardTop = (limit) => http.get('/admin/dashboard/top', { params: { limit } });

export const FINANCE_BIZ = {
  10: { label: '回收打款', type: 'danger' },
  20: { label: '出售收款', type: 'success' },
  30: { label: '维修收款', type: 'primary' },
};

export const FLOW_DIRECTION = {
  income: { label: '收入', type: 'success' },
  payout: { label: '支出', type: 'danger' },
  refund: { label: '退款', type: 'warning' },
};

// ===== 状态字典 =====
export const RECYCLE_STATUS = {
  10: { label: '待寄出', type: 'warning' },
  20: { label: '运输中', type: 'primary' },
  30: { label: '质检中', type: 'warning' },
  40: { label: '待确认', type: 'warning' },
  50: { label: '已打款', type: 'success' },
  60: { label: '已完成', type: 'success' },
  80: { label: '已取消', type: 'info' },
};

export const REPAIR_STATUS = {
  10: { label: '待确认', type: 'info' },
  20: { label: '已预约', type: 'primary' },
  30: { label: '维修中', type: 'warning' },
  40: { label: '待验收', type: 'warning' },
  50: { label: '已完成', type: 'success' },
  80: { label: '已取消', type: 'info' },
};
