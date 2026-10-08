import {
  fetchRepairOrderDetail,
  confirmRepairPrice,
  rejectRepairPrice,
  requestRepairPayment,
} from '../../../services/repair/repair';
import { fen2yuan } from '../../../common/recycle-status';

/** 报价确认页：展示维修问题与报价（金额/说明/图片），用户接受或拒绝；接受后可在此支付。 */
Page({
  data: {
    orderNo: '',
    order: null,
    quote: null,
    amountText: '0',
    itemsText: '',
    loading: true,
    acting: false,
  },

  onLoad(options) {
    this._orderNo = options.orderNo || '';
    this.fetchAll();
  },

  onShow() {
    if (!this.data.loading) {
      this.fetchAll();
    }
  },

  async fetchAll() {
    if (!this._orderNo) return;
    try {
      const order = await fetchRepairOrderDetail(this._orderNo);
      const quote = order.quote || null;
      this.setData({
        order,
        quote,
        amountText: fen2yuan(quote ? quote.amountFen : 0),
        itemsText: (order.items || []).map((it) => it.name).join('、'),
        loading: false,
      });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  previewImage(e) {
    const urls = (this.data.quote && this.data.quote.images) || [];
    wx.previewImage({ current: urls[e.currentTarget.dataset.index], urls });
  },

  onAccept() {
    const { amountText, acting } = this.data;
    if (acting) return;
    wx.showModal({
      title: '接受报价',
      content: '确认接受 ¥' + amountText + ' 的维修报价？接受后需完成支付，维修随即开始。',
      success: async (res) => {
        if (!res.confirm) return;
        this.setData({ acting: true });
        try {
          await confirmRepairPrice(this._orderNo);
          wx.showToast({ title: '已接受报价', icon: 'success' });
          this.fetchAll();
        } catch (e) {
          wx.showToast({ title: e.message || '操作失败', icon: 'none' });
        } finally {
          this.setData({ acting: false });
        }
      },
    });
  },

  onReject() {
    const { acting } = this.data;
    if (acting) return;
    wx.showModal({
      title: '拒绝报价',
      content: '拒绝后将退回待处理，商家可重新检测报价，你也可以随时取消订单。',
      success: async (res) => {
        if (!res.confirm) return;
        this.setData({ acting: true });
        try {
          await rejectRepairPrice(this._orderNo);
          wx.showToast({ title: '已拒绝报价', icon: 'success' });
          setTimeout(() => wx.navigateBack(), 800);
        } catch (e) {
          wx.showToast({ title: e.message || '操作失败', icon: 'none' });
          this.setData({ acting: false });
        }
      },
    });
  },

  async onPay() {
    const { acting } = this.data;
    if (acting) return;
    this.setData({ acting: true });
    try {
      // mock 模式直接闭环；真实模式调起微信收银台，用户支付成功后 resolve
      await requestRepairPayment(this._orderNo);
      wx.showToast({ title: '支付成功', icon: 'success' });
      this.fetchAll();
    } catch (e) {
      wx.showToast({ title: e.message || '支付失败', icon: 'none' });
    } finally {
      this.setData({ acting: false });
    }
  },

  backToDetail() {
    wx.navigateBack();
  },
});
