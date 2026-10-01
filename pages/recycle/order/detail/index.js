import {
  fetchRecycleOrderDetail,
  fillExpressNo,
  cancelRecycleOrder,
  confirmPayout,
  fetchExpressCompanies,
} from '../../../../services/recycle/order';
import { STATUS_DESC, fen2yuan } from '../../../../common/recycle-status';

/** 正向流程节点（取消态不展示时间线） */
const FLOW = [10, 20, 30, 40, 50, 60];

const NODE_DESC = {
  10: '等待您寄出机器',
  20: '机器运输中',
  30: '工程师质检中',
  40: '确认最终回收价',
  50: '回收款已打款',
  60: '交易完成',
};

Page({
  data: {
    orderNo: '',
    order: null,
    quoteText: '',
    finalText: '',
    loading: true,
    timeline: null,
    expressPopupVisible: false,
    expressForm: { com: 'shunfeng', company: '顺丰速运', no: '' },
    expressCompanies: [],
    expressCompanyOptions: [],
    expressPickerVisible: false,
    expressPickerValue: [0],
    /** 当前所选快递公司查询轨迹时是否必须带收寄件人电话（顺丰速运/顺丰快运/中通快递） */
    expressNeedPhone: true,
    submitting: false,
  },

  onLoad(options) {
    this.setData({ orderNo: options.orderNo || '' });
    this.loadExpressCompanies();
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
    this.fetchDetail();
  },

  async fetchDetail() {
    try {
      const order = await fetchRecycleOrderDetail(this.data.orderNo);
      this.setData({
        order,
        loading: false,
        quoteText: fen2yuan(order.quoteFen),
        finalText: fen2yuan(order.finalFen),
        timeline: this.buildTimeline(order.status),
        'order.inspections': (order.inspections || []).map((it) => ({
          ...it,
          finalPriceText: fen2yuan(it.finalFen),
        })),
      });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  /** 快递公司字典由后端下发，前端不再写死，保证编码与轨迹查询用的是同一份 */
  async loadExpressCompanies() {
    try {
      const companies = (await fetchExpressCompanies()) || [];
      if (!companies.length) return;
      let index = companies.findIndex((c) => c.com === this.data.expressForm.com);
      if (index < 0) index = 0;
      const current = companies[index];
      this.setData({
        expressCompanies: companies,
        expressCompanyOptions: companies.map((c) => ({ label: c.name, value: c.com })),
        expressPickerValue: [index],
        'expressForm.com': current.com,
        'expressForm.company': current.name,
        expressNeedPhone: !!current.needPhone,
      });
    } catch (e) {
      // 字典拉不到就沿用默认顺丰，提交时后端仍会校验编码
    }
  },

  /** 状态时间线：取消态返回 null（页面显示提示行） */
  buildTimeline(status) {
    if (status === 80) {
      return null;
    }
    const index = FLOW.indexOf(status);
    const current = index < 0 ? 0 : index;
    return {
      current,
      nodes: FLOW.map((s) => ({ label: STATUS_DESC[s], desc: NODE_DESC[s] || '' })),
    };
  },

  showExpressPopup() {
    this.setData({ expressPopupVisible: true });
  },

  hideExpressPopup() {
    this.setData({ expressPopupVisible: false });
  },

  showExpressPicker() {
    this.setData({ expressPickerVisible: true });
  },

  hideExpressPicker() {
    this.setData({ expressPickerVisible: false });
  },

  onExpressCompanyConfirm(e) {
    const detail = e.detail || {};
    const com = (detail.value && detail.value[0]) || '';
    const company = this.data.expressCompanies.find((c) => c.com === com);
    this.setData({
      expressPickerVisible: false,
      'expressForm.com': com,
      'expressForm.company': (detail.label && detail.label[0]) || (company ? company.name : ''),
      expressNeedPhone: company ? !!company.needPhone : false,
    });
  },

  onExpressInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({ [`expressForm.${field}`]: e.detail.value });
  },

  async submitExpress() {
    const { orderNo, expressForm, submitting } = this.data;
    if (submitting) return;
    if (!expressForm.com) {
      wx.showToast({ title: '请选择快递公司', icon: 'none' });
      return;
    }
    if (!expressForm.no) {
      wx.showToast({ title: '请填写快递单号', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      await fillExpressNo(orderNo, {
        expressCom: expressForm.com,
        expressCompany: expressForm.company,
        expressNo: expressForm.no,
      });
      this.setData({ expressPopupVisible: false });
      wx.showToast({ title: '运单号已登记', icon: 'success' });
      this.fetchDetail();
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },

  goTrace() {
    wx.navigateTo({ url: `/pages/recycle/order/trace/index?orderNo=${this.data.orderNo}` });
  },

  onCancel() {
    wx.showModal({
      title: '取消订单',
      content: '确定取消该回收订单吗？',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await cancelRecycleOrder(this.data.orderNo, '用户主动取消');
          wx.showToast({ title: '已取消', icon: 'success' });
          this.fetchDetail();
        } catch (e) {
          wx.showToast({ title: e.message || '取消失败', icon: 'none' });
        }
      },
    });
  },

  onConfirmPayout() {
    wx.showModal({
      title: '确认打款',
      content: `质检最终价 ¥${this.data.finalText}，确认接受并收款？`,
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await confirmPayout(this.data.orderNo);
          wx.showToast({ title: '打款成功', icon: 'success' });
          this.fetchDetail();
        } catch (e) {
          wx.showToast({ title: e.message || '操作失败', icon: 'none' });
        }
      },
    });
  },
});
