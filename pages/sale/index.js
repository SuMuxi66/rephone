import { fetchSaleGoods, fetchSaleGoodsFilters } from '../../services/sale/order';
import { fen2yuan } from '../../common/recycle-status';
import { resolveImageUrl } from '../../common/image-url';

const SORTS = [
  { key: 'default', label: '综合' },
  { key: 'priceAsc', label: '价格低到高' },
  { key: 'priceDesc', label: '价格高到低' },
];

/** 首屏渲染条数：后端返回的是已筛选的全量在售库存，前端只做分批渲染 */
const PAGE_SIZE = 10;
/** 关键词输入防抖（毫秒），避免逐字符打后端 */
const SEARCH_DEBOUNCE = 350;

/** 后端 Goods 实体 → 货架卡片展示字段（金额：分 → 元） */
function decorate(g) {
  return {
    ...g,
    image: resolveImageUrl(g.image),
    priceText: fen2yuan(g.priceFen),
    originText: g.originalPriceFen ? fen2yuan(g.originalPriceFen) : '',
    stockText: g.stock > 0 ? `库存 ${g.stock} 件` : '暂时无货',
    tagTop: (g.tags || '').split(',').filter(Boolean).slice(0, 1),
  };
}

Page({
  data: {
    sorts: SORTS,
    activeSort: 'default',
    keyword: '',
    // 筛选条选项（来自后端 /filters）
    brands: [],
    conditions: [],
    activeBrand: '',
    activeCondition: '',
    filterActive: false,
    // 列表
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

  onUnload() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
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
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    this.searchTimer = setTimeout(() => this.reload(), SEARCH_DEBOUNCE);
  },

  switchSort(e) {
    const key = e.currentTarget.dataset.key;
    if (key === this.data.activeSort) return;
    this.setData({ activeSort: key });
    this.reload();
  },

  /** 再次点击已选中的筛选项 = 取消该筛选 */
  switchBrand(e) {
    const brand = e.currentTarget.dataset.brand || '';
    this.setData({ activeBrand: brand === this.data.activeBrand ? '' : brand });
    this.reload();
  },

  switchCondition(e) {
    const value = e.currentTarget.dataset.condition || '';
    this.setData({ activeCondition: value === this.data.activeCondition ? '' : value });
    this.reload();
  },

  resetFilters() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    this.setData({
      keyword: '',
      activeBrand: '',
      activeCondition: '',
      activeSort: 'default',
    });
    this.reload();
  },

  /** 筛选与排序全部交给后端，前端不再本地过滤 */
  buildQuery() {
    const { keyword, activeBrand, activeCondition, activeSort } = this.data;
    const query = { sort: activeSort || 'default' };
    const kw = (keyword || '').trim();
    if (kw) query.keyword = kw;
    if (activeBrand) query.brand = activeBrand;
    if (activeCondition) query.conditionLevel = activeCondition;
    return query;
  },

  async reload() {
    this.setData({ loadStatus: 1, goods: [], visible: [], pageNum: 1, finished: false });
    try {
      // 先取选项：库存变化后若当前选中项已不存在，重置它，避免「高亮消失但仍在过滤」
      const filters = await fetchSaleGoodsFilters().catch(() => null);
      if (filters) {
        const brands = filters.brands || [];
        const conditions = filters.conditions || [];
        const patch = { brands, conditions };
        if (this.data.activeBrand && !brands.includes(this.data.activeBrand)) {
          patch.activeBrand = '';
        }
        if (this.data.activeCondition && !conditions.includes(this.data.activeCondition)) {
          patch.activeCondition = '';
        }
        this.setData(patch);
      }

      const list = await fetchSaleGoods(this.buildQuery());
      const goods = (list || []).map(decorate);
      const { keyword, activeBrand, activeCondition, activeSort } = this.data;
      this.setData({
        goods,
        total: goods.length,
        loadStatus: 0,
        filterActive: !!((keyword || '').trim() || activeBrand || activeCondition || activeSort !== 'default'),
      });
      this.renderPage(1);
    } catch (e) {
      this.setData({ goods: [], visible: [], total: 0, finished: true, loadStatus: 3 });
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },

  renderPage(pageNum) {
    const { goods } = this.data;
    const visible = goods.slice(0, pageNum * PAGE_SIZE);
    this.setData({
      visible,
      pageNum,
      total: goods.length,
      finished: visible.length >= goods.length,
    });
  },

  loadMore() {
    this.renderPage(this.data.pageNum + 1);
  },

  onRetry() {
    this.reload();
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/sale/detail/index?id=${id}` });
  },
});
