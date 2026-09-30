import { fetchSaleGoodsDetail, fetchGoodsInspection } from '../../../services/sale/order';

Page({
  data: {
    id: '',
    goods: null,
    report: null,
    groups: [],
    images: [],
    inspectedAtText: '',
    loading: true,
  },

  onLoad(options) {
    this.setData({ id: options.id || '' });
  },

  onShow() {
    this.fetch();
  },

  async fetch() {
    try {
      const [goods, report] = await Promise.all([
        fetchSaleGoodsDetail(this.data.id),
        fetchGoodsInspection(this.data.id),
      ]);
      if (!report) {
        this.setData({ goods, report: null, loading: false });
        return;
      }
      // 按 category 分组，保持后端 sortNo 顺序
      const order = [];
      const map = {};
      (report.items || []).forEach((it) => {
        const key = it.category || '其他';
        if (!map[key]) {
          map[key] = [];
          order.push(key);
        }
        map[key].push({ ...it, normal: it.result === '正常' });
      });
      this.setData({
        goods,
        report,
        loading: false,
        groups: order.map((name) => ({ name, items: map[name] })),
        images: report.images || [],
        inspectedAtText: report.inspectedAt
          ? String(report.inspectedAt).replace('T', ' ').slice(0, 16)
          : '—',
      });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  previewImage(e) {
    const { images } = this.data;
    if (!images.length) return;
    const index = Number(e.currentTarget.dataset.index) || 0;
    wx.previewImage({ current: images[index], urls: images });
  },

  goBuy() {
    const { goods } = this.data;
    if (!goods) return;
    wx.navigateTo({ url: `/pages/sale/confirm/index?id=${goods.id}` });
  },
});
