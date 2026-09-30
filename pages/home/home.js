import { config } from '../../config/index';
import { fetchHome } from '../../services/home/home';
import { fetchSaleGoods } from '../../services/sale/order';
import { fen2yuan } from '../../common/recycle-status';

/** 快捷故障入口（静态常量；v1 不携带机型/故障预选参数） */
const FAULTS = [
  { name: '换屏幕', hint: '外屏 · 内屏 · 总成' },
  { name: '换电池', hint: '容量衰减 · 鼓包' },
  { name: '进水处理', hint: '清洗 · 烘干 · 除锈' },
  { name: '不开机', hint: '主板 · 供电 · 系统' },
];

/** 首页严选位展示件数：1 件主推 + 2 件紧凑行 */
const FEATURED_MORE = 2;
/** 回收行情展示条数 */
const QUOTE_ROWS = 4;

const toYuan = (fen) => fen2yuan(fen);

Page({
  data: {
    pageLoading: true,
    faults: FAULTS,
    hotModels: [],
    featured: null,
    featuredMore: [],
    hooks: { repair: '先报价', recycle: '在线估价', buy: '官方质检' },
    servicePhone: config.servicePhone,
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
    const list = (goods || []).slice(0, 1 + FEATURED_MORE).map((g) => ({
      ...g,
      priceText: toYuan(g.priceFen),
      originText: g.originalPriceFen ? toYuan(g.originalPriceFen) : '',
      specText: [g.conditionLevel, g.storage].filter(Boolean).join(' · '),
    }));

    // 钩子数字优先用真实数据，缺失时回落到中性文案
    const maxRecycleFen = (home.hotModels || []).reduce(
      (max, m) => Math.max(max, Number(m.maxPriceFen) || 0),
      0,
    );
    const minBuyFen = list.reduce((min, g) => (min === 0 ? g.priceFen : Math.min(min, g.priceFen)), 0);

    this.setData({
      pageLoading: false,
      hotModels,
      featured: list[0] || null,
      featuredMore: list.slice(1),
      hooks: {
        repair: '先报价',
        recycle: maxRecycleFen > 0 ? '最高 ¥' + Math.round(maxRecycleFen / 100) : '在线估价',
        buy: minBuyFen > 0 ? '¥' + Math.round(minBuyFen / 100) + ' 起' : '官方质检',
      },
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
