import { get, post } from '../request';

/** 品牌列表：GET /api/wx/brands */
export const fetchBrands = () => get('/api/wx/brands');

/** 品牌下机型列表（含可选内存）：GET /api/wx/models?brandId= */
export const fetchModels = (brandId) => get('/api/wx/models', { brandId });

/** 估价：POST /api/wx/quote/calculate，返回 priceFen（分） */
export const calculateQuote = (payload) => post('/api/wx/quote/calculate', payload);
