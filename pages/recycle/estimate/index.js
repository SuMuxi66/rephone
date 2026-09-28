import { fetchBrands, fetchModels, calculateQuote } from '../../../services/recycle/quote';

// 成色/故障选项前端固定展示，后端按 quote_rule 校验（P5 后台可改为接口下发）
const CONDITION_OPTIONS = [
  { key: 'COND_99', label: '99新', desc: '无明显使用痕迹' },
  { key: 'COND_95', label: '95新', desc: '轻微使用痕迹' },
  { key: 'COND_90', label: '9成新', desc: '明显划痕或磕碰' },
  { key: 'COND_80', label: '8成新及以下', desc: '较多磨损或功能问题' },
];

const ISSUE_OPTIONS = [
  { key: 'SCREEN', label: '屏幕划痕或磕碰' },
  { key: 'SHELL', label: '外壳明显磨损' },
  { key: 'BATTERY', label: '电池健康低于80%' },
  { key: 'REPAIRED', label: '曾维修或拆机' },
  { key: 'FACEID', label: '面容/指纹失效' },
  { key: 'NOBOOT', label: '无法开机' },
];

const EMPTY_FORM = {
  brandId: null,
  brandName: '',
  modelId: null,
  modelName: '',
  storage: '',
  condition: '',
  issues: [],
};

Page({
  data: {
    loading: true,
    submitting: false,
    loadError: '',
    brands: [],
    models: [],
    form: EMPTY_FORM,
    conditions: CONDITION_OPTIONS,
    issues: ISSUE_OPTIONS,
    pickerVisible: { brand: false, model: false, storage: false },
    pickerValue: { brand: [], model: [], storage: [] },
    pickerColumns: { brand: [], model: [], storage: [] },
  },

  onLoad() {
    this.init();
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
  },

  async init() {
    try {
      const brands = await fetchBrands();
      this.setData({
        brands,
        loading: false,
        'pickerColumns.brand': brands.map((b) => ({ label: b.name, value: b.id })),
      });
    } catch (e) {
      this.setData({ loading: false, loadError: e.message || '加载失败' });
    }
  },

  onRetry() {
    this.setData({ loading: true, loadError: '' });
    this.init();
  },

  showPicker(e) {
    const { type } = e.currentTarget.dataset;
    if (type === 'model' && !this.data.form.brandId) {
      wx.showToast({ title: '请先选择品牌', icon: 'none' });
      return;
    }
    if (type === 'storage' && !this.data.form.modelId) {
      wx.showToast({ title: '请先选择机型', icon: 'none' });
      return;
    }
    this.setData({ [`pickerVisible.${type}`]: true });
  },

  hidePicker(e) {
    const { type } = e.currentTarget.dataset;
    this.setData({ [`pickerVisible.${type}`]: false });
  },

  async onPickerChange(e) {
    const { type } = e.currentTarget.dataset;
    const raw = e.detail.value;
    const value = Array.isArray(raw) ? raw[0] : raw;
    this.setData({ [`pickerVisible.${type}`]: false });

    if (type === 'brand') {
      const brand = this.data.brands.find((b) => b.id === value);
      if (!brand) return;
      this.setData({
        'form.brandId': brand.id,
        'form.brandName': brand.name,
        'form.modelId': null,
        'form.modelName': '',
        'form.storage': '',
        'pickerValue.brand': [brand.id],
        'pickerColumns.model': [],
        'pickerColumns.storage': [],
      });
      try {
        const models = await fetchModels(brand.id);
        this.setData({
          models,
          'pickerColumns.model': models.map((m) => ({ label: m.name, value: m.id })),
        });
      } catch (err) {
        wx.showToast({ title: err.message || '机型加载失败', icon: 'none' });
      }
      return;
    }

    if (type === 'model') {
      const model = this.data.models.find((m) => m.id === value);
      if (!model) return;
      this.setData({
        'form.modelId': model.id,
        'form.modelName': model.name,
        'form.storage': '',
        'pickerValue.model': [model.id],
        'pickerColumns.storage': (model.storages || []).map((s) => ({ label: s, value: s })),
      });
      return;
    }

    if (type === 'storage') {
      this.setData({ 'form.storage': value, 'pickerValue.storage': [value] });
    }
  },

  onConditionChange(e) {
    this.setData({ 'form.condition': e.detail.value });
  },

  onIssuesChange(e) {
    this.setData({ 'form.issues': e.detail.value || [] });
  },

  async onSubmit() {
    const { form, submitting } = this.data;
    if (submitting || !form.brandId || !form.modelId || !form.storage || !form.condition) {
      wx.showToast({ title: '请先完成品牌、机型、内存与成色的选择', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      const result = await calculateQuote({
        modelId: form.modelId,
        storage: form.storage,
        condition: form.condition,
        issues: form.issues,
      });
      wx.setStorageSync('recycle.quoteResult', result);
      wx.navigateTo({ url: '/pages/recycle/result/index' });
    } catch (e) {
      wx.showToast({ title: e.message || '估价失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
