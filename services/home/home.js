import { config } from '../../config/index';

/** 回收首页数据（mock；P6 接 /api/wx/home 时按 mock.md 适配层方案替换） */
function mockFetchHome() {
  const { delay } = require('../_utils/delay');
  return delay().then(() => ({
    hotModels: [
      { brandId: 1, brandName: 'Apple', modelName: 'iPhone 15 Pro Max', priceText: '¥8200' },
      { brandId: 1, brandName: 'Apple', modelName: 'iPhone 15', priceText: '¥6700' },
      { brandId: 2, brandName: '华为', modelName: 'Mate 60 Pro', priceText: '¥7400' },
      { brandId: 2, brandName: '华为', modelName: 'P60', priceText: '¥5700' },
      { brandId: 3, brandName: '小米', modelName: '小米 14 Pro', priceText: '¥6600' },
      { brandId: 3, brandName: '小米', modelName: 'Redmi K70', priceText: '¥4000' },
      { brandId: 4, brandName: 'OPPO', modelName: 'Find X7', priceText: '¥5900' },
      { brandId: 5, brandName: 'vivo', modelName: 'X100', priceText: '¥5600' },
    ],
  }));
}

/** 获取首页数据 */
export function fetchHome() {
  if (config.useMock) {
    return mockFetchHome();
  }
  return new Promise((resolve) => {
    resolve('real api');
  });
}
