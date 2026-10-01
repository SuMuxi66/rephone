import { fetchHome } from '../../services/home/home';
import { fetchSaleGoods } from '../../services/sale/order';
import { fen2yuan } from '../../common/recycle-status';
import { resolveImageUrl } from '../../common/image-url';

/** 平台承诺：一行纯文字，不做对仗标语（说人话） */
const TRUSTS = ['官方质检', '顺丰包邮', '验收后付款'];

/** Banner 轮播：三大业务场景 */
const BANNERS = [
  {
    key: 'sell',
    theme: 'brand',
    title: '旧机高价卖',
    sub: '填个型号就知道值多少钱',
    cta: '免费估价',
    tap: 'goEstimate',
  },
  {
    key: 'repair',
    theme: 'accent',
    title: '手机维修',
    sub: '先报价再动手，修不好不收费',
    cta: '立即报修',
    tap: 'goRepair',
  },
  {
    key: 'buy',
    theme: 'deep',
    title: '严选二手机',
    sub: '每台都有质检报告，7 天可退',
    cta: '去逛好机',
    tap: 'goSale',
  },
];

/** 业务入口：第 1 项走横向大卡，其余走两张小卡（打破三等等分） */
const ENTRIES = [
  {
    key: 'sell',
    icon: 'wallet',
    name: '卖旧机',
    sub: '30 秒出价，不合适再商量',
    btn: '去估价',
    tap: 'goEstimate',
  },
  {
    key: 'repair',
    icon: 'tools',
    name: '修手机',
    sub: '修不好不收费',
    tap: 'goRepair',
  },
  {
    key: 'buy',
    icon: 'cart',
    name: '买二手机',
    sub: '一机一报告，7 天可退',
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
    trustLine: TRUSTS.join(' · '),
    banners: BANNERS,
    entries: ENTRIES,
    entriesMore: ENTRIES.slice(1),
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
