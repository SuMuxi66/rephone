import { fetchOrderTrace } from '../../../../services/recycle/order';

Page({
  data: {
    orderNo: '',
    trace: null,
    items: [],
    loading: true,
    error: '',
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo || '' });
    this.loadTrace();
  },

  async loadTrace() {
    if (!this.data.orderNo) {
      this.setData({ loading: false, error: '缺少订单号' });
      return;
    }
    this.setData({ loading: true, error: '' });
    try {
      const trace = await fetchOrderTrace(this.data.orderNo);
      this.setData({
        trace,
        items: (trace.items || []).map((it, i) => ({ ...it, key: `${i}-${it.time || ''}` })),
        loading: false,
      });
    } catch (e) {
      this.setData({ loading: false, error: e.message || '物流查询失败' });
    }
  },

  onCopy() {
    const no = this.data.trace && this.data.trace.expressNo;
    if (!no) return;
    wx.setClipboardData({ data: no });
  },

  onRetry() {
    this.loadTrace();
  },
});
