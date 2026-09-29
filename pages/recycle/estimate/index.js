import { fetchBrands, fetchModels, calculateQuote } from '../../../services/recycle/quote';

// 成色/故障选项前端固定展示，后端按 quote_rule 校验（P5 后台可改为接口下发）
const CONDITION_OPTIONS = [
  { key: 'COND_99', label: '99新', desc: '无明显使用痕迹' },
  { key: 'COND_95', label: '95新', desc: '轻微使用痕迹' },
  { key: 'COND_90', label: '9成新', desc: '明显划痕或磕碰' },
  { key: 'COND_80', label: '8成新及以下', desc: '较多磨损或功能问题' },
];

// 屏幕状态（独立于整机成色，报价=基准×成色×屏幕系数−故障扣减）
const SCREEN_OPTIONS = [
  { key: 'SCR_OK', label: '无划痕无瑕疵', desc: '屏幕完好，点亮无明显划痕' },
  { key: 'SCR_LIGHT', label: '轻微划痕', desc: '有细划痕，日常使用不显眼' },
  { key: 'SCR_HEAVY', label: '明显划痕或磕碰', desc: '多处划痕或外屏磕碰' },
  { key: 'SCR_BROKEN', label: '碎屏或显示异常', desc: '外屏破裂、花屏、亮线或色斑' },
];

// 功能问题分组（对齐转转/爱回收标准检测项，key 与后端 quote_rule 一致）
const ISSUE_GROUPS = [
  {
    title: '电池与充电',
    items: [
      { key: 'BATTERY', label: '电池健康低于80%' },
      { key: 'CHARGE', label: '充电异常' },
    ],
  },
  {
    title: '显示与拍照',
    items: [
      { key: 'DISPLAY', label: '花屏/亮线或色斑' },
      { key: 'CAMERA', label: '前后摄像头异常' },
      { key: 'FLASH', label: '闪光灯异常' },
    ],
  },
  {
    title: '声音与通话',
    items: [
      { key: 'SPEAKER', label: '扬声器或听筒异常' },
      { key: 'MIC', label: '麦克风或送话异常' },
      { key: 'SIGNAL', label: 'Wi-Fi/蓝牙或信号异常' },
    ],
  },
  {
    title: '按键与其他功能',
    items: [
      { key: 'BUTTON', label: '电源/音量键失灵' },
      { key: 'VIBRATE', label: '振动异常' },
      { key: 'FACEID', label: '面容/指纹失效' },
    ],
  },
  {
    title: '维修与进水',
    items: [
      { key: 'REPAIRED', label: '曾拆机或换件维修' },
      { key: 'MAINBOARD', label: '主板维修史' },
      { key: 'WATER', label: '进水或受潮' },
    ],
  },
  {
    title: '严重故障与账号',
    items: [
      { key: 'REBOOT', label: '反复重启或死机' },
      { key: 'NOBOOT', label: '无法开机' },
      { key: 'IDLOCK', label: 'ID锁/账号无法退出' },
    ],
  },
];

const EMPTY_FORM = {
  brandId: null,
  brandName: '',
  modelId: null,
  modelName: '',
  storage: '',
  condition: '',
  screen: '',
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
    screens: SCREEN_OPTIONS,
    issueGroups: ISSUE_GROUPS,
    pickerVisible: { brand: false, model: false, storage: false },
    pickerValue: { brand: [], model: [], storage: [] },
    pickerColumns: { brand: [], model: [], storage: [] },
    /** 步骤指示：0 选机型中，1 描述成色中，2 可获取报价 */
    stepCurrent: 0,
  },

  onLoad() {
    this.init();
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
    const prefill = wx.getStorageSync('recycle.prefillBrandId');
    if (prefill) {
      wx.removeStorageSync('recycle.prefillBrandId');
      this._prefillBrandId = prefill;
      this.applyPrefill();
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
      this.applyPrefill();
    } catch (e) {
      this.setData({ loading: false, loadError: e.message || '加载失败' });
    }
  },

  /** 首页热门机型入口带来的品牌预选 */
  applyPrefill() {
    const brandId = this._prefillBrandId;
    if (!brandId || !this.data.brands.length || this.data.form.brandId) {
      return;
    }
    const brand = this.data.brands.find((b) => b.id === brandId);
    if (brand) {
      this.selectBrand(brand);
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

  async selectBrand(brand) {
    this.setData({
      'form.brandId': brand.id,
      'form.brandName': brand.name,
      'form.modelId': null,
      'form.modelName': '',
      'form.storage': '',
      'pickerValue.brand': [brand.id],
      'pickerColumns.model': [],
      'pickerColumns.storage': [],
      stepCurrent: 0,
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
  },

  async onPickerChange(e) {
    const { type } = e.currentTarget.dataset;
    const value = Array.isArray(e.detail.value) ? e.detail.value[0] : e.detail.value;
    this.setData({ [`pickerVisible.${type}`]: false });

    if (type === 'brand') {
      const brand = this.data.brands.find((b) => b.id === value);
      if (brand) await this.selectBrand(brand);
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
    this.updateStep();
  },

  onConditionChange(e) {
    this.setData({ 'form.condition': e.detail.value });
    this.updateStep();
  },

  onScreenChange(e) {
    this.setData({ 'form.screen': e.detail.value });
    this.updateStep();
  },

  onIssuesChange(e) {
    this.setData({ 'form.issues': e.detail.value || [] });
  },

  /** 选机型(品牌+机型+内存)完成 → 第2步；成色与屏幕状态完成 → 第3步 */
  updateStep() {
    const { form } = this.data;
    let stepCurrent = 0;
    if (form.condition && form.screen) {
      stepCurrent = 2;
    } else if (form.brandId && form.modelId && form.storage) {
      stepCurrent = 1;
    }
    if (stepCurrent !== this.data.stepCurrent) {
      this.setData({ stepCurrent });
    }
  },

  async onSubmit() {
    const { form, submitting } = this.data;
    if (submitting || !form.brandId || !form.modelId || !form.storage || !form.condition || !form.screen) {
      wx.showToast({ title: '请先完成机型、成色与屏幕状态的选择', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      const result = await calculateQuote({
        modelId: form.modelId,
        storage: form.storage,
        condition: form.condition,
        screenCondition: form.screen,
        issues: form.issues,
      });
      // issueKeys/screen 供下单页回传后端复核（后端只认代码，不认中文标签）
      wx.setStorageSync(
        'recycle.quoteResult',
        Object.assign({}, result, { issueKeys: form.issues, screen: form.screen }),
      );
      wx.navigateTo({ url: '/pages/recycle/result/index' });
    } catch (e) {
      wx.showToast({ title: e.message || '估价失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
