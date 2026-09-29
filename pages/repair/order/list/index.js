import { fetchRepairOrders } from '../../../../services/repair/repair';
import { STATUS_DESC, statusDesc } from '../../../../common/repair-status';
import { fen2yuan } from '../../../../common/recycle-status';

const FILTERS = [
  { label: '全部', status: 0 },
  { label: '待确认', status: 10 },
  { label: '已预约', status: 20 },
  { label: '维修中', status: 30 },
  { label: '待验收', status: 40 },
  { label: '已完成', status: 50 },
];

Page({
  data: {
    filters: FILTERS,
    activeStatus: 0,
    orders: [],
    pageNum: 1,
    finished: false,
    loadStatus: 0,
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
    this.reload();
  },

  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    if (!this.data.finished && this.data.loadStatus === 0) {
      this.loadMore();
    }
  },

  switchFilter(e) {
    const status = Number(e.currentTarget.dataset.status);
    if (status === this.data.activeStatus) return;
    this.setData({ activeStatus: status });
    this.reload();
  },

  async reload() {
    this.setData({ pageNum: 1, finished: false, orders: [], loadStatus: 1 });
    await this.fetchPage(1);
  },

  onRetry() {
    this.reload();
  },

  async loadMore() {
    this.setData({ loadStatus: 1 });
    await this.fetchPage(this.data.pageNum + 1);
  },

  async fetchPage(pageNum) {
    try {
      const params = { pageNum, pageSize: 10 };
      if (this.data.activeStatus > 0) {
        params.status = this.data.activeStatus;
      }
      const page = await fetchRepairOrders(params);
      const items = (page.records || []).map((o) => ({
        ...o,
        priceText: fen2yuan(o.totalFen),
        statusDesc: statusDesc(o.status) || STATUS_DESC[o.status] || '未知',
      }));
      const orders = this.data.orders.concat(items);
      this.setData({
        orders,
        pageNum,
        finished: orders.length >= (page.total || 0),
        loadStatus: 0,
      });
    } catch (e) {
      this.setData({ loadStatus: 3 });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  goDetail(e) {
    const { orderNo } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/repair/order/detail/index?orderNo=${orderNo}` });
  },

  goRepair() {
    wx.switchTab({ url: '/pages/repair/index' });
  },
});
