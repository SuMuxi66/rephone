import { fetchRepairOrderDetail, cancelRepairOrder } from '../../../../services/repair/repair';
import { STATUS_DESC, FLOW, NODE_DESC } from '../../../../common/repair-status';
import { fen2yuan } from '../../../../common/recycle-status';

Page({
  data: {
    order: null,
    totalText: '0',
    itemsText: '',
    timeline: null,
  },

  onLoad(query) {
    this._orderNo = query.orderNo || '';
  },

  onShow() {
    this.fetchDetail();
  },

  async fetchDetail() {
    if (!this._orderNo) return;
    try {
      const order = await fetchRepairOrderDetail(this._orderNo);
      this.setData({
        order: {
          ...order,
          statusDesc: STATUS_DESC[order.status] || '未知',
        },
        totalText: fen2yuan(order.totalFen),
        itemsText: (order.items || []).map((it) => it.name).join('、'),
        timeline: this.buildTimeline(order.status),
      });
    } catch (e) {
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  /** 时间线：主流程 10→20→30→40→50；取消态返回 null 显示提示行 */
  buildTimeline(status) {
    if (status === 80) {
      return null;
    }
    const current = FLOW.indexOf(status);
    return {
      current,
      nodes: FLOW.map((s) => ({ label: STATUS_DESC[s], desc: NODE_DESC[s] })),
    };
  },

  previewImage(e) {
    const { index } = e.currentTarget.dataset;
    const urls = this.data.order.images || [];
    wx.previewImage({ current: urls[index], urls });
  },

  onCancel() {
    wx.showModal({
      title: '取消预约',
      content: '确定取消本次上门维修预约吗？',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await cancelRepairOrder(this._orderNo, '用户取消预约');
          wx.showToast({ title: '已取消', icon: 'success' });
          this.fetchDetail();
        } catch (e) {
          wx.showToast({ title: e.message || '取消失败', icon: 'none' });
        }
      },
    });
  },
});
