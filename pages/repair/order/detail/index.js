import { fetchRepairOrderDetail, cancelRepairOrder, fillRepairExpress, fetchRepairTrace, fetchRepairReturnTrace, confirmRepairReceipt } from '../../../../services/repair/repair';
import { fetchExpressCompanies } from '../../../../services/recycle/order';
import { STATUS_DESC, statusDesc, timelineFor, SERVICE_TYPE_DESC } from '../../../../common/repair-status';
import { fen2yuan } from '../../../../common/recycle-status';

const TRACE_LIMIT = 8;

Page({
  data: {
    order: null,
    totalText: '0',
    itemsText: '',
    timeline: null,
    serviceText: '',
    mailIn: false,
    // 寄出填单弹层
    expressSheetVisible: false,
    companies: [],
    companyIndex: 0,
    expressNoInput: '',
    submittingExpress: false,
    // 轨迹弹层
    traceSheetVisible: false,
    traceTitle: '',
    traceLoading: false,
    traceNodes: [],
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
      const mailIn = Number(order.serviceType) === 20;
      this.setData({
        order: {
          ...order,
          statusDesc: statusDesc(order.status, order.serviceType),
        },
        totalText: fen2yuan(order.totalFen),
        itemsText: (order.items || []).map((it) => it.name).join('、'),
        timeline: timelineFor(order.status, order.serviceType),
        serviceText: SERVICE_TYPE_DESC[order.serviceType] || '上门维修',
        mailIn,
      });
    } catch (e) {
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  previewImage(e) {
    const { index } = e.currentTarget.dataset;
    const urls = this.data.order.images || [];
    wx.previewImage({ current: urls[index], urls });
  },

  onCancel() {
    wx.showModal({
      title: '取消预约',
      content: '确定取消本次维修预约吗？',
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

  // ===== 寄修：填写寄出运单号 =====

  async openExpressSheet() {
    this.setData({ expressSheetVisible: true, companyIndex: 0, expressNoInput: '' });
    if (!this.data.companies.length) {
      try {
        const companies = await fetchExpressCompanies();
        this.setData({ companies: companies || [] });
      } catch (e) {
        wx.showToast({ title: e.message || '快递公司加载失败', icon: 'none' });
      }
    }
  },

  hideExpressSheet() {
    this.setData({ expressSheetVisible: false });
  },

  onCompanyChange(e) {
    this.setData({ companyIndex: Number(e.detail.value) || 0 });
  },

  onExpressNoInput(e) {
    this.setData({ expressNoInput: e.detail.value });
  },

  async onSubmitExpress() {
    const { companies, companyIndex, expressNoInput, submittingExpress } = this.data;
    if (submittingExpress) return;
    const company = companies[companyIndex];
    const expressNo = (expressNoInput || '').trim();
    if (!company) {
      wx.showToast({ title: '请选择快递公司', icon: 'none' });
      return;
    }
    if (expressNo.length < 6) {
      wx.showToast({ title: '请输入正确的运单号', icon: 'none' });
      return;
    }
    this.setData({ submittingExpress: true });
    try {
      await fillRepairExpress(this._orderNo, {
        expressCompany: company.name,
        expressCom: company.com,
        expressNo,
      });
      wx.showToast({ title: '已提交', icon: 'success' });
      this.setData({ expressSheetVisible: false });
      this.fetchDetail();
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' });
    } finally {
      this.setData({ submittingExpress: false });
    }
  },

  // ===== 寄修：轨迹 =====

  async openTraceSheet(direction) {
    const returnDirection = direction === 'return';
    this.setData({
      traceSheetVisible: true,
      traceLoading: true,
      traceNodes: [],
      traceTitle: returnDirection ? '回寄物流' : '寄出物流',
    });
    try {
      const trace = returnDirection
        ? await fetchRepairReturnTrace(this._orderNo)
        : await fetchRepairTrace(this._orderNo);
      this.setData({
        traceNodes: (trace.items || []).slice(0, TRACE_LIMIT),
      });
    } catch (e) {
      this.setData({ traceNodes: [] });
      wx.showToast({ title: e.message || '轨迹查询失败', icon: 'none' });
    } finally {
      this.setData({ traceLoading: false });
    }
  },

  onOpenSendTrace() {
    this.openTraceSheet('out');
  },

  onOpenReturnTrace() {
    this.openTraceSheet('return');
  },

  hideTraceSheet() {
    this.setData({ traceSheetVisible: false });
  },

  // ===== 寄修：确认收货 =====

  onConfirmReceipt() {
    wx.showModal({
      title: '确认收货',
      content: '请确认已收到回寄设备且维修合格，确认后维修单完成。',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await confirmRepairReceipt(this._orderNo);
          wx.showToast({ title: '已完成', icon: 'success' });
          this.fetchDetail();
        } catch (e) {
          wx.showToast({ title: e.message || '操作失败', icon: 'none' });
        }
      },
    });
  },
});
