import { fetchSaleGoods } from '../../services/sale/order';
import { fen2yuan } from '../../common/recycle-status';

const SORTS = [
  { key: 'default', label: '综合' },
  { key: 'priceAsc', label: '价格低到高' },
  { key: 'priceDesc', label: '价格高到低' },
];

/** 首屏渲染条数（后端 /api/wx/goods 返回全量在售库存，前端做分批渲染） */
const PAGE_SIZE = 10;

/** 后端 Goods 实体 → 货架卡片展示字段（金额：分 → 元） */
function decorate(g) {
  return {
    ...g,
    priceText: fen2yuan(g.priceFen),
    originText: g.originalPriceFen ? fen2yuan(g.originalPriceFen) : '',
    stockText: g.stock > 0 ? `库存 ${g.stock} 件` : '暂时无货',
  };
}

Page({
  data: {
    sorts: SORTS,
    activeSort: 'default',
    keyword: '',
    goods: [],
    visible: [],
    total: 0,
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
    if (!this.data.finished) {
      this.loadMore();
    }
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
    this.applyFilter();
  },

  switchSort(e) {
    const key = e.currentTarget.dataset.key;
    if (key === this.data.activeSort) return;
    this.setData({ activeSort: key });
    this.applyFilter();
  },

  async reload() {
    this.setData({ loadStatus: 1 });
    try {
      const list = await fetchSaleGoods();
      this.all = (list || []).map(decorate);
      this.applyFilter();
    } catch (e) {
      this.all = [];
      this.setData({ goods: [], visible: [], total: 0, finished: true, loadStatus: 3 });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  /** 关键词 + 排序在本地完成（后端该接口暂不支持筛选参数） */
  applyFilter() {
    const { keyword, activeSort } = this.data;
    const kw = (keyword || '').trim().toLowerCase();
    let list = (this.all || []).filter((g) => {
      if (!kw) return true;
      return (
        String(g.name || '').toLowerCase().includes(kw) ||
        String(g.descText || '').toLowerCase().includes(kw)
      );
    });
    if (activeSort === 'priceAsc') {
      list = list.slice().sort((a, b) => a.priceFen - b.priceFen);
    } else if (activeSort === 'priceDesc') {
      list = list.slice().sort((a, b) => b.priceFen - a.priceFen);
    }
    const visible = list.slice(0, PAGE_SIZE);
    this.setData({
      goods: list,
      visible,
      total: list.length,
      pageNum: 1,
      finished: visible.length >= list.length,
      loadStatus: 0,
    });
  },

  loadMore() {
    const { goods, pageNum } = this.data;
    const next = pageNum + 1;
    const visible = goods.slice(0, next * PAGE_SIZE);
    this.setData({ visible, pageNum: next, finished: visible.length >= goods.length });
  },

  onRetry() {
    this.reload();
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/sale/detail/index?id=${id}` });
  },
});
