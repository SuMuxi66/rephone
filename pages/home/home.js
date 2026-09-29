import { fetchHome } from '../../services/home/home';

const HOT_REPAIRS = ['换屏', '换电池', '进水', '不开机', '摄像头', '其他故障'];

Page({
  data: {
    pageLoading: true,
    hotRepairs: HOT_REPAIRS,
    hotModels: [],
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

  goRepair() {
    wx.switchTab({ url: '/pages/repair/index' });
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
