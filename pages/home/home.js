import { fetchHome } from '../../services/home/home';

Page({
  data: {
    pageLoading: true,
    hotModels: [],
    flowSteps: [
      { step: 1, name: '在线估价', desc: '选机型答成色' },
      { step: 2, name: '顺丰邮寄', desc: '免费包邮上门取件' },
      { step: 3, name: '专业质检', desc: '48小时内出报告' },
      { step: 4, name: '确认打款', desc: '满意收款闪电到账' },
    ],
  },

  onShow() {
    this.getTabBar().init();
  },

  onLoad() {
    this.loadHomePage();
  },

  onPullDownRefresh() {
    this.loadHomePage();
  },

  loadHomePage() {
    wx.stopPullDownRefresh();
    fetchHome().then(({ hotModels }) => {
      this.setData({ hotModels, pageLoading: false });
    }).catch(() => {
      this.setData({ pageLoading: false });
    });
  },

  goEstimate() {
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },

  /** 热门机型 → 预选品牌后进入估价 */
  goEstimateWithBrand(e) {
    const { brandId } = e.currentTarget.dataset;
    if (brandId) {
      wx.setStorageSync('recycle.prefillBrandId', brandId);
    }
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },
});
