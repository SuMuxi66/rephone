import { calculateQuote } from '../../../services/recycle/quote';
import { ensureAgreements } from '../../../common/agreement-manager';

/** 机型库选择结果 / 当前选中（与维修页共用同一机型库二级页） */
const RESULT_KEY = 'modelPicker.result';
const CURRENT_KEY = 'modelPicker.current';

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
    submitting: false,
    storages: [],
    form: EMPTY_FORM,
    conditions: CONDITION_OPTIONS,
    screens: SCREEN_OPTIONS,
    issueGroups: ISSUE_GROUPS,
    /** 功能问题选中映射（WXML 无法对数组做 includes，渲染态用 map） */
    issueSelected: {},
    /** 步骤指示：0 选机型中，1 描述成色中，2 可获取报价 */
    stepCurrent: 0,
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
    this.applyPickerResult();
  },

  /** 打开机型库页面（品牌 + 机型一次选完）；首页热门品牌预选经 prefillBrandId 透传为当前品牌 */
  openModelPicker() {
    wx.setStorageSync(CURRENT_KEY, {
      brandId: this.data.form.brandId || wx.getStorageSync('recycle.prefillBrandId') || null,
      modelId: this.data.form.modelId || null,
    });
    wx.removeStorageSync('recycle.prefillBrandId');
    wx.navigateTo({ url: '/pages/model-picker/index?biz=recycle' });
  },

  /** 机型库返回：品牌与机型一起带回（storages 随机型带回）；机型有变才清内存并滚动到下一步 */
  applyPickerResult() {
    const result = wx.getStorageSync(RESULT_KEY);
    if (!result || !result.modelId) return;
    wx.removeStorageSync(RESULT_KEY);
    const changed = result.modelId !== this.data.form.modelId;
    if (!changed) return;
    this.setData({
      'form.brandId': result.brandId,
      'form.brandName': result.brandName,
      'form.modelId': result.modelId,
      'form.modelName': result.modelName,
      storages: result.storages || [],
      'form.storage': '',
    });
    this.updateStep();
    this.scrollToSection('#sec-storage');
  },

  onStorageTap(e) {
    const value = e.currentTarget.dataset.val;
    if (!value || value === this.data.form.storage) return;
    this.setData({ 'form.storage': value });
    this.updateStep();
    this.scrollToSection('#sec-condition');
  },

  onConditionTap(e) {
    const key = e.currentTarget.dataset.key;
    if (!key || key === this.data.form.condition) return;
    const option = CONDITION_OPTIONS.find((item) => item.key === key);
    this.setData({ 'form.condition': key, 'form.conditionLabel': option ? option.label : '' });
    this.updateStep();
    this.scrollToSection('#sec-screen');
  },

  onScreenTap(e) {
    const key = e.currentTarget.dataset.key;
    if (!key || key === this.data.form.screen) return;
    const option = SCREEN_OPTIONS.find((item) => item.key === key);
    this.setData({ 'form.screen': key, 'form.screenLabel': option ? option.label : '' });
    this.updateStep();
    this.scrollToSection('#sec-issues');
  },

  onIssueToggle(e) {
    const key = e.currentTarget.dataset.key;
    if (!key) return;
    const issues = (this.data.form.issues || []).slice();
    const index = issues.indexOf(key);
    if (index >= 0) {
      issues.splice(index, 1);
    } else {
      issues.push(key);
    }
    this.setData({ 'form.issues': issues, issueSelected: this.buildIssueSelected(issues) });
  },

  buildIssueSelected(issues) {
    const map = {};
    (issues || []).forEach((key) => {
      map[key] = true;
    });
    return map;
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

  /** 新区块渐显后滚到可见处；低版本基础库不支持 selector 时静默跳过 */
  scrollToSection(selector) {
    if (!wx.pageScrollTo) return;
    wx.pageScrollTo({ selector, offsetTop: -16, duration: 300, fail: () => {} });
  },

  async onSubmit() {
    const { form, submitting } = this.data;
    if (submitting || !form.brandId || !form.modelId || !form.storage || !form.condition || !form.screen) {
      wx.showToast({ title: '请先完成机型、成色与屏幕状态的选择', icon: 'none' });
      return;
    }
    // 协议门禁：获取预估价前先确认《回收估价用户须知》（已签过则直接放行）
    if (!(await ensureAgreements(this, 'estimate'))) {
      wx.showToast({ title: '需同意《回收估价用户须知》后继续', icon: 'none' });
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
