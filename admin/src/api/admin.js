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
export const fetchModels = (brandId) => http.get('/admin/models', { params: { brandId } });
export const createModel = (payload) => http.post('/admin/model', payload);
export const updateModel = (id, payload) => http.put(`/admin/model/${id}`, payload);
export const updateModelPrice = (id, storage, priceYuan) =>
  http.put(`/admin/model/${id}/price`, { storage, priceYuan });

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
