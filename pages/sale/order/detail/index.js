import {
  fetchSaleOrderDetail,
  cancelSaleOrder,
  confirmSaleOrder,
  paySaleOrder,
  applySaleAfterSale,
} from '../../../../services/sale/order';
import { statusDesc } from '../../../../common/sale-status';
import { fen2yuan } from '../../../../common/recycle-status';
import { fetchOrderComment } from '../../../../services/comment';

/** 后端 createTime 为 ISO 字符串（2026-09-30T19:09:05） */
const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 16) : '');

Page({
  data: {
    orderNo: '',
    order: null,
    totalText: '',
    priceText: '',
    refundText: '',
    createTimeText: '',
    loading: true,
    afterSalePopupVisible: false,
    afterSaleForm: { reason: '' },
    submitting: false,
    /** 已完成订单的评价（null=未评价，详情页出现「立即评价」按钮） */
    comment: null,
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo || '' });
  },

  onShow() {
    this.fetchDetail();
  },

  async fetchDetail() {
    try {
      const order = await fetchSaleOrderDetail(this.data.orderNo);
      this.setData({
        order: { ...order, statusDesc: statusDesc(order.status) },
        loading: false,
        totalText: fen2yuan(order.totalFen),
        priceText: fen2yuan(order.priceFen),
        refundText: fen2yuan(order.refundFen),
        createTimeText: formatTime(order.createTime),
      });
      this.loadComment();
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  /** 已完成订单的评价状态：有则展示评价卡，无则出现「立即评价」按钮 */
  async loadComment() {
    try {
      const comment = await fetchOrderComment(this.data.orderNo, 20);
      this.setData({ comment });
    } catch (e) {
      // 评价查询失败不影响详情主流程
    }
  },

  goComment() {
    wx.navigateTo({ url: '/pages/comment/create/index?orderType=20&orderNo=' + this.data.orderNo });
  },

  copyOrderNo() {
    wx.setClipboardData({ data: this.data.orderNo });
  },

  onPay() {
    if (this.data.submitting) return;
    this.setData({ submitting: true });
    paySaleOrder(this.data.orderNo)
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' });
        this.fetchDetail();
      })
      .catch((e) => wx.showToast({ title: e.message || '支付失败', icon: 'none' }))
      .finally(() => this.setData({ submitting: false }));
  },

  onCancel() {
    wx.showModal({
      title: '取消订单',
      content: '确定取消该订单吗？',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await cancelSaleOrder(this.data.orderNo, '用户主动取消');
          wx.showToast({ title: '已取消', icon: 'success' });
          this.fetchDetail();
        } catch (e) {
          wx.showToast({ title: e.message || '取消失败', icon: 'none' });
        }
      },
    });
  },

  onConfirm() {
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await confirmSaleOrder(this.data.orderNo);
          wx.showToast({ title: '已确认收货', icon: 'success' });
          this.fetchDetail();
        } catch (e) {
          wx.showToast({ title: e.message || '操作失败', icon: 'none' });
        }
      },
    });
  },

  showAfterSalePopup() {
    this.setData({ afterSalePopupVisible: true, 'afterSaleForm.reason': '' });
  },

  hideAfterSalePopup() {
    this.setData({ afterSalePopupVisible: false });
  },

  onAfterSaleReasonInput(e) {
    this.setData({ 'afterSaleForm.reason': e.detail.value });
  },

  async submitAfterSale() {
    const { orderNo, afterSaleForm, submitting } = this.data;
    if (submitting) return;
    if (!afterSaleForm.reason) {
      wx.showToast({ title: '请填写申请原因', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      await applySaleAfterSale(orderNo, afterSaleForm.reason);
      this.setData({ afterSalePopupVisible: false });
      wx.showToast({ title: '已提交申请', icon: 'success' });
      this.fetchDetail();
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
