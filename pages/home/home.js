import { fetchHome } from '../../services/home/home';
import { fetchSaleGoods } from '../../services/sale/order';
import { fen2yuan } from '../../common/recycle-status';
import { resolveImageUrl } from '../../common/image-url';

/** 信任条：平台承诺 */
const TRUSTS = ['官方质检', '一机一报告', '180天质保', '顺丰包邮', '验收后付款'];

/** Banner 轮播：三大业务场景（商用平台文案） */
const BANNERS = [
  {
    key: 'sell',
    theme: 'accent',
    title: '旧机高价卖',
    sub: '在线估价 30 秒到价 · 顺丰包邮 · 质检后打款',
    cta: '免费估价',
    tap: 'goEstimate',
  },
  {
    key: 'repair',
    theme: 'deep',
    title: '手机维修',
    sub: '上门快修 · 先报价后维修 · 修好验收才付款',
    cta: '立即报修',
    tap: 'goRepair',
  },
  {
    key: 'buy',
    theme: 'brand',
    title: '严选二手机',
    sub: '官方质检 · 一机一报告 · 180 天质保 · 7 天退换',
    cta: '去逛好机',
    tap: 'goSale',
  },
];

/** 三大业务入口：横排大卡 */
const ENTRIES = [
  {
    key: 'sell',
    theme: 'accent',
    icon: 'wallet',
    name: '卖旧机',
    sub: '30秒估价·质检打款',
    btn: '去估价',
    tap: 'goEstimate',
  },
  {
    key: 'repair',
    theme: 'deep',
    icon: 'tools',
    name: '修手机',
    sub: '上门快修·先修后付',
    btn: '去报修',
    tap: 'goRepair',
  },
  {
    key: 'buy',
    theme: 'brand',
    icon: 'cart',
    name: '买二手机',
    sub: '官方质检·7天退换',
    btn: '去选购',
    tap: 'goSale',
  },
];

/** 严选位展示件数：1 件主推 + 2 件紧凑行 */
const FEATURED_MORE = 2;
/** 回收行情展示条数 */
const QUOTE_ROWS = 4;

Page({
  data: {
    pageLoading: true,
    trusts: TRUSTS,
    banners: BANNERS,
    entries: ENTRIES,
    hotModels: [],
    featured: null,
    featuredMore: [],
    servicePhone: '',
  },

  onShow() {
    this.getTabBar().init();
  },

  onLoad() {
    this.loadHomePage();
  },

  onPullDownRefresh() {
    this.loadHomePage();
  },

  /** 两个数据源并行拉取，任一失败都不阻断另一块（首页不允许整页空白） */
  async loadHomePage() {
    const [home, goods] = await Promise.all([
      fetchHome().catch(() => ({ hotModels: [] })),
      fetchSaleGoods({ sort: 'default' }).catch(() => []),
    ]);

    const hotModels = (home.hotModels || []).slice(0, QUOTE_ROWS);
    const list = (goods || []).map((g) => ({
      ...g,
      image: resolveImageUrl(g.image),
      priceText: fen2yuan(g.priceFen),
      originText: g.originalPriceFen ? fen2yuan(g.originalPriceFen) : '',
      specText: [g.conditionLevel, g.storage].filter(Boolean).join(' · '),
      tagTop: (g.tags || '').split(',').filter(Boolean).slice(0, 1),
    }));

    this.setData({
      pageLoading: false,
      hotModels,
      featured: list[0] || null,
      featuredMore: list.slice(1, 1 + FEATURED_MORE),
    });
    wx.stopPullDownRefresh();
  },

  goRepair() {
    wx.switchTab({ url: '/pages/repair/index' });
  },

  goEstimate() {
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },

  goSale() {
    wx.switchTab({ url: '/pages/sale/index' });
  },

  goGoodsDetail(e) {
    const { id } = e.currentTarget.dataset;
    if (!id) return;
    wx.navigateTo({ url: '/pages/sale/detail/index?id=' + id });
  },

  /** 行情行 → 回收估价页并预选品牌 */
  goEstimateWithBrand(e) {
    const { brandId } = e.currentTarget.dataset;
    if (brandId) {
      wx.setStorageSync('recycle.prefillBrandId', brandId);
    }
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },
});
