import { fetchRepairItems } from '../../services/repair/repair';
import { fen2yuan } from '../../common/recycle-status';

/** 机型库页面返回结果 / 进入时回传当前选择的 storage 键（页面返回无返回值） */
const RESULT_KEY = 'modelPicker.result';
const CURRENT_KEY = 'modelPicker.current';

Page({
  data: {
    form: {
      brandId: null,
      brandName: '',
      modelId: null,
      modelName: '',
    },
    groups: [],
    selectedIds: [],
    itemsLoading: false,
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
    this.applyPickerResult();
  },

  /** 打开机型库页面（品牌 + 机型一次选完，替代原来的文字选择器） */
  openModelPicker() {
    wx.setStorageSync(CURRENT_KEY, {
      brandId: this.data.form.brandId,
      modelId: this.data.form.modelId,
    });
    wx.navigateTo({ url: '/pages/model-picker/index?biz=repair' });
  },

  /** 机型库返回：品牌与机型一起带回；机型有变化才重新拉维修项目 */
  applyPickerResult() {
    const result = wx.getStorageSync(RESULT_KEY);
    if (!result) return;
    wx.removeStorageSync(RESULT_KEY);
    const changed = result.modelId !== this.data.form.modelId;
    this.setData({
      'form.brandId': result.brandId,
      'form.brandName': result.brandName,
      'form.modelId': result.modelId,
      'form.modelName': result.modelName,
      groups: [],
      selectedIds: [],
    });
    if (changed) {
      this.loadRepairItems(result.modelId);
    }
  },

  async loadRepairItems(modelId) {
    this.setData({ itemsLoading: true });
    try {
      const groups = await fetchRepairItems(modelId);
      this.setData({
        itemsLoading: false,
        groups: (groups || []).map((g) => ({
          groupName: g.groupName,
          items: (g.items || []).map((it) => ({ ...it, priceText: fen2yuan(it.priceFen) })),
        })),
      });
    } catch (e) {
      this.setData({ itemsLoading: false });
      wx.showToast({ title: e.message || '维修项目加载失败', icon: 'none' });
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
