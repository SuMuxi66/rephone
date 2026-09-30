import { fetchBrands } from '../../services/recycle/quote';
import { fetchModels as fetchRepairModels } from '../../services/repair/repair';
import { fetchModels as fetchQuoteModels } from '../../services/recycle/quote';

/** 机型库按业务取不同接口：维修=全量机型库，回收=受估价基准价过滤的机型 */
const MODEL_FETCHERS = {
  repair: fetchRepairModels,
  recycle: fetchQuoteModels,
};

/** 选择结果 / 当前选择的 storage 键（页面返回没有返回值，用 storage 传递） */
const RESULT_KEY = 'modelPicker.result';
const CURRENT_KEY = 'modelPicker.current';

Page({
  data: {
    pageLoading: true,
    modelsLoading: false,
    loadError: '',
    brands: [],
    models: [],
    list: [],
    activeBrandId: null,
    keyword: '',
    selectedModelId: null,
  },

  onLoad(options) {
    this.biz = options && options.biz === 'recycle' ? 'recycle' : 'repair';
    const current = wx.getStorageSync(CURRENT_KEY) || {};
    wx.removeStorageSync(CURRENT_KEY);
    this.current = current;
    this.setData({ selectedModelId: current.modelId || null });
    this.init();
  },

  async init() {
    try {
      const brands = await fetchBrands();
      this.setData({ brands, pageLoading: false });
      if (!brands || !brands.length) {
        this.setData({ loadError: '机型库暂无品牌数据' });
        return;
      }
      const active = brands.find((b) => b.id === this.current.brandId) || brands[0];
      this.selectBrand(active.id);
    } catch (e) {
      this.setData({ pageLoading: false, loadError: e.message || '机型库加载失败' });
    }
  },

  onRetry() {
    this.setData({ pageLoading: true, loadError: '' });
    this.init();
  },

  onBrandTap(e) {
    const id = e.currentTarget.dataset.id;
    if (id === this.data.activeBrandId) return;
    this.setData({ keyword: '', list: [] });
    this.selectBrand(id);
  },

  async selectBrand(brandId) {
    this.setData({ activeBrandId: brandId, models: [], list: [], modelsLoading: true });
    const fetcher = MODEL_FETCHERS[this.biz] || fetchRepairModels;
    try {
      const models = await fetcher(brandId);
      this.setData({ models: models || [], modelsLoading: false });
      this.applyFilter();
    } catch (e) {
      this.setData({ modelsLoading: false });
      wx.showToast({ title: e.message || '机型加载失败', icon: 'none' });
    }
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
    this.applyFilter();
  },

  applyFilter() {
    const kw = String(this.data.keyword || '').trim().toLowerCase();
    const list = (this.data.models || []).filter(
      (m) => !kw || String(m.name || '').toLowerCase().includes(kw),
    );
    this.setData({ list });
  },

  /** 选中机型 → 写入 storage 并返回上一页 */
  onPick(e) {
    const id = e.currentTarget.dataset.id;
    const model = (this.data.models || []).find((m) => m.id === id);
    if (!model) return;
    const brand = (this.data.brands || []).find((b) => b.id === this.data.activeBrandId) || {};
    wx.setStorageSync(RESULT_KEY, {
      brandId: brand.id,
      brandName: brand.name,
      modelId: model.id,
      modelName: model.name,
    });
    wx.navigateBack();
  },
});
