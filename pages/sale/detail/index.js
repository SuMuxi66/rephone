import { fetchSaleGoodsDetail } from '../../../services/sale/order';
import { fen2yuan } from '../../../common/recycle-status';

Page({
  data: {
    id: '',
    goods: null,
    priceText: '',
    originText: '',
    loading: true,
  },

  onLoad(options) {
    this.setData({ id: options.id || '' });
  },

  onShow() {
    this.fetchDetail();
  },

  async fetchDetail() {
    try {
      const goods = await fetchSaleGoodsDetail(this.data.id);
      this.setData({
        goods,
        loading: false,
        priceText: fen2yuan(goods.priceFen),
        originText: goods.originalPriceFen ? fen2yuan(goods.originalPriceFen) : '',
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
