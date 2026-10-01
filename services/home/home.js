import { get } from '../request';
import { fen2yuan } from '../../common/recycle-status';

/**
 * 首页热门机型（回收行情）：真实机型库数据（按最高基准价降序的高价值热门机型）。
 * 直接走后端接口，不受模板全局 useMock 开关影响（机型数据只有真实库一份）。
 */
export function fetchHome() {
  return get('/api/wx/home').then((data) => ({
    hotModels: (data || []).map((m) => ({
      brandId: m.brandId,
      brandName: m.brandName,
      modelName: m.modelName,
      image: m.image || '',
      priceText: '¥' + fen2yuan(m.maxPriceFen),
    })),
  }));
}
