import { get, put } from '../request';

/** 当前用户资料：GET /api/wx/user/profile */
export const fetchUserProfile = () => get('/api/wx/user/profile');

/** 更新用户资料（仅传入字段生效）：PUT /api/wx/user/profile */
export const updateProfile = (payload) => put('/api/wx/user/profile', payload);

/** 回收单总数：GET /api/wx/recycle/orders（取分页 total） */
export const fetchRecycleOrderTotal = () =>
  get('/api/wx/recycle/orders', { pageNum: 1, pageSize: 1 }).then((page) => (page && page.total) || 0);

/** 维修单总数：GET /api/wx/repair/orders（取分页 total） */
export const fetchRepairOrderTotal = () =>
  get('/api/wx/repair/orders', { pageNum: 1, pageSize: 1 }).then((page) => (page && page.total) || 0);
