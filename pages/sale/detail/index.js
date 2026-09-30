import { fetchSaleGoodsDetail, fetchGoodsInspection } from '../../../services/sale/order';
import { fen2yuan } from '../../../common/recycle-status';

/** 卡片上最多展示的「需说明」检查项条数 */
const MAX_PREVIEW_ITEMS = 3;

Page({
  data: {
    id: '',
    goods: null,
    priceText: '',
    originText: '',
    tagList: [],
    loading: true,
    // 质检报告
    hasReport: false,
    report: null,
    previewItems: [],
    normalCount: 0,
    abnormalCount: 0,
  },

  onLoad(options) {
    this.setData({ id: options.id || '' });
  },

  onShow() {
    this.fetchDetail();
  },

  async fetchDetail() {
    try {
      // 质检报告与商品并行拉取；报告接口失败不阻断详情
      const [goods, report] = await Promise.all([
        fetchSaleGoodsDetail(this.data.id),
        fetchGoodsInspection(this.data.id).catch(() => null),
      ]);
      const items = (report && report.items) || [];
      const abnormal = items.filter((it) => it.result !== '正常');
      this.setData({
        goods,
        loading: false,
        priceText: fen2yuan(goods.priceFen),
        originText: goods.originalPriceFen ? fen2yuan(goods.originalPriceFen) : '',
        tagList: (goods.tags || '')
          .split(',')
          .map((s) => s.trim())
          .filter(Boolean),
        hasReport: !!report,
        report,
        previewItems: abnormal.slice(0, MAX_PREVIEW_ITEMS),
        normalCount: (report && report.normalCount) || 0,
        abnormalCount: (report && report.abnormalCount) || 0,
      });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  previewImage() {
    const { goods } = this.data;
    if (goods && goods.image) {
      wx.previewImage({ current: goods.image, urls: [goods.image] });
    }
  },

  goInspection() {
    if (!this.data.hasReport) {
      wx.showToast({ title: '该机型质检报告整理中', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/sale/inspection/index?id=${this.data.id}` });
  },

  goBuy() {
    const { goods } = this.data;
    if (!goods) return;
    if (!goods.stock || goods.stock <= 0) {
      wx.showToast({ title: '该机型暂无库存', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/sale/confirm/index?id=${goods.id}` });
  },
});
