import { get } from '../request';

/**
 * 首页热门机型：真实机型库数据（每品牌前 2 款 + 最贵内存档基准价）。
 * 直接走后端接口，不受模板全局 useMock 开关影响（机型数据只有真实库一份）。
 */
export function fetchHome() {
  return get('/api/wx/home').then((data) => ({
    hotModels: (data || []).map((m) => ({
      brandId: m.brandId,
      brandName: m.brandName,
      modelName: m.modelName,
      priceText: '¥' + (m.maxPriceFen / 100).toFixed(0),
    })),
  }));
}
