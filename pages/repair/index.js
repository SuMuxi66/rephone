import { fetchBrands, fetchModels, fetchRepairItems } from '../../services/repair/repair';
import { fen2yuan } from '../../common/recycle-status';

Page({
  data: {
    loading: true,
    loadError: '',
    brands: [],
    models: [],
    form: {
      brandId: null,
      brandName: '',
      modelId: null,
      modelName: '',
    },
    pickerVisible: { brand: false, model: false },
    pickerValue: { brand: [], model: [] },
    pickerColumns: { brand: [], model: [] },
    groups: [],
    selectedIds: [],
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
    this.setData({ [`pickerVisible.${type}`]: true });
  },

  hidePicker(e) {
    const { type } = e.currentTarget.dataset;
    this.setData({ [`pickerVisible.${type}`]: false });
  },

  async onPickerChange(e) {
    const { type } = e.currentTarget.dataset;
    const value = Array.isArray(e.detail.value) ? e.detail.value[0] : e.detail.value;
    this.setData({ [`pickerVisible.${type}`]: false });

    if (type === 'brand') {
      const brand = this.data.brands.find((b) => b.id === value);
      if (!brand) return;
      this.setData({
        'form.brandId': brand.id,
        'form.brandName': brand.name,
        'form.modelId': null,
        'form.modelName': '',
        groups: [],
        selectedIds: [],
        'pickerValue.brand': [brand.id],
        'pickerColumns.model': [],
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
        'pickerValue.model': [model.id],
        groups: [],
        selectedIds: [],
        loading: true,
      });
      try {
        const groups = await fetchRepairItems(model.id);
        this.setData({
          loading: false,
          groups: (groups || []).map((g) => ({
            groupName: g.groupName,
            items: (g.items || []).map((it) => ({ ...it, priceText: fen2yuan(it.priceFen) })),
          })),
        });
      } catch (err) {
        this.setData({ loading: false });
        wx.showToast({ title: err.message || '维修项目加载失败', icon: 'none' });
      }
    }
  },

  onItemsChange(e) {
    this.setData({ selectedIds: e.detail.value || [] });
  },

  goQuote() {
    const { form, groups, selectedIds } = this.data;
    const items = [];
    (groups || []).forEach((g) => {
      (g.items || []).forEach((it) => {
        if (selectedIds.indexOf(it.itemId) !== -1) {
          items.push({ itemId: it.itemId, name: it.name, priceFen: it.priceFen });
        }
      });
    });
    if (!items.length) {
      wx.showToast({ title: '请先勾选要维修的项目', icon: 'none' });
      return;
    }
    wx.setStorageSync('repair.selection', {
      modelId: form.modelId,
      brandName: form.brandName,
      modelName: form.modelName,
      items,
    });
    wx.navigateTo({ url: '/pages/repair/quote/index' });
  },
});
