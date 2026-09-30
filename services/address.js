import { get, post, put, del } from '../request';

/** 地址簿列表：GET /api/wx/address */
export const fetchAddressList = () => get('/api/wx/address');

/** 新增地址：POST /api/wx/address，返回 { addressId } */
export const createAddress = (payload) => post('/api/wx/address', payload);

/** 编辑地址：PUT /api/wx/address/{id} */
export const updateAddress = (id, payload) => put(`/api/wx/address/${id}`, payload);

/** 删除地址：DELETE /api/wx/address/{id} */
export const deleteAddress = (id) => del(`/api/wx/address/${id}`);

/** 设默认地址：PUT /api/wx/address/{id}/default */
export const setDefaultAddress = (id) => put(`/api/wx/address/${id}/default`);
