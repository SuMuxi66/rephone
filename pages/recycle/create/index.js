import { createRecycleOrder } from '../../../services/recycle/order';
import { fetchSubscribeTemplates, requestOrderStatusSubscribe } from '../../../services/subscribe';
import { fen2yuan } from '../../../common/recycle-status';
import { areaData } from '../../../config/index';
import addressPrefill from '../../../common/address-prefill';

const provinces = areaData.map((p) => ({ label: p.label, value: p.label }));

Page({
  behaviors: [addressPrefill],

  data: {
    quote: null,
    quoteText: '',
    submitting: false,
    /** 订单状态变更的订阅模板 ID，onLoad 预取；为空表示后端未配置，直接跳过订阅弹窗 */
    subscribeTemplateId: '',
    form: {
      pickupType: 10,
      name: '',
      phone: '',
      detail: '',
      region: '',
      remark: '',
    },
    errors: {
      phone: '',
    },
    regionPickerVisible: false,
    regionValue: [],
    regionColumns: { province: provinces, city: [], district: [] },
  },

  onLoad() {
    this.loadSubscribeTemplate();
    const quote = wx.getStorageSync('recycle.quoteResult');
    if (!quote || !quote.priceFen) {
      // 无估价数据：tab 页只能 switchTab，redirectTo 会静默失败卡白屏
      this.setData({ noQuote: true });
      wx.showToast({ title: '请先完成估价', icon: 'none' });
      setTimeout(() => wx.switchTab({ url: '/pages/recycle/estimate/index' }), 1200);
      return;
    }
    this.setData({ quote, quoteText: fen2yuan(quote.priceFen) });
    this.loadAddressBook();
  },

  goEstimateTab() {
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },

  /** 预取模板 ID：必须在下单点击之前拿到，才能保持在手势上下文里调用订阅 */
  loadSubscribeTemplate() {
    fetchSubscribeTemplates()
      .then((cfg) => this.setData({ subscribeTemplateId: (cfg && cfg.orderStatus) || '' }))
      .catch(() => {
        // 拉不到配置就静默跳过订阅，不能影响下单主流程
      });
  },

  /** 请求订阅订单状态变更。用户拒绝、未配模板都不阻塞下单。 */
  requestSubscribe() {
    const { subscribeTemplateId } = this.data;
    if (!subscribeTemplateId) return;
    requestOrderStatusSubscribe(subscribeTemplateId).then((res) => {
      if (res && res.error) {
        console.warn('[subscribe] 未完成订阅:', res.error);
      }
    });
  },

  onPickupType(e) {
    this.setData({ 'form.pickupType': Number(e.currentTarget.dataset.type) });
  },

  onInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({ [`form.${field}`]: e.detail.value });
    if (field === 'phone' && this.data.errors.phone) {
      this.setData({ 'errors.phone': '' });
    }
  },

  onPhoneBlur(e) {
    const phone = (e.detail.value || '').trim();
    if (phone && !/^1\d{10}$/.test(phone)) {
      this.setData({ 'errors.phone': '手机号格式不正确' });
    } else {
      this.setData({ 'errors.phone': '' });
    }
  },

  showRegion() {
    // 首次打开时初始化市/区列，避免用户直接确认拿到空值
    const patch = { regionPickerVisible: true };
    if (!this.data.regionColumns.city.length) {
      this._selProvince = areaData[0].label;
      this._selCity = '';
      const cities = areaData[0].children || [];
      const districts = (cities[0] && cities[0].children) || [];
      const firstCity = (cities[0] && cities[0].label) || '';
      const firstDistrict = (districts[0] && districts[0].label) || '';
      patch['regionColumns.city'] = cities.map((c) => ({ label: c.label, value: c.label }));
      patch['regionColumns.district'] = districts
        .map((d) => ({ label: d.label, value: d.label }));
      // TDesign picker 未触达的列 confirm 可能缺值：种子化默认选中，onRegionConfirm 兜底用
      patch.regionValue = [this._selProvince, firstCity, firstDistrict];
      this._selRegionLabels = [this._selProvince, firstCity, firstDistrict];
    }
    this.setData(patch);
  },

  hideRegion() {
    this.setData({ regionPickerVisible: false });
  },

  /** 省或市列滚动时联动下级列（私有字段记录当前选中省/市，供下级联动取 children） */
  onRegionPick(e) {
    const { column, index } = e.detail;
    if (column === 0) {
      const province = provinces[index];
      if (!province) return;
      this._selProvince = province.label;
      this._selCity = '';
      const p = areaData.find((x) => x.label === province.label);
      const cities = (p && p.children) || [];
      const districts = (cities[0] && cities[0].children) || [];
      this.setData({
        'regionColumns.city': cities.map((c) => ({ label: c.label, value: c.label })),
        'regionColumns.district': districts
          .map((d) => ({ label: d.label, value: d.label })),
      });
      this._selRegionLabels = [
        this._selProvince,
        (cities[0] && cities[0].label) || '',
        (districts[0] && districts[0].label) || '',
      ];
      return;
    }
    if (column === 1) {
      const cityLabel = (this.data.regionColumns.city[index] || {}).label;
      if (!cityLabel) return;
      this._selCity = cityLabel;
      const province = areaData.find((x) => x.label === this._selProvince);
      const city = ((province && province.children) || []).find((c) => c.label === cityLabel);
      const districts = (city && city.children) || [];
      this.setData({
        'regionColumns.district': districts
          .map((d) => ({ label: d.label, value: d.label })),
      });
      this._selRegionLabels = [
        this._selProvince,
        this._selCity,
        (districts[0] && districts[0].label) || '',
      ];
      return;
    }
    if (column === 2) {
      const districtLabel = (this.data.regionColumns.district[index] || {}).label;
      if (districtLabel) {
        this._selRegionLabels = [
          this._selRegionLabels[0],
          this._selRegionLabels[1],
          districtLabel,
        ];
      }
    }
  },

  onRegionConfirm(e) {
    const picked = (e.detail.value || []).filter(Boolean);
    const tracked = (this._selRegionLabels || []).filter(Boolean);
    // picker 组件未触达列可能返回缺值，回退到页面侧跟踪的完整选中
    const value = picked.length >= 3 ? picked : tracked;
    if (value.length < 3) {
      wx.showToast({ title: '请选择完整的省市区', icon: 'none' });
      return;
    }
    this.setData({
      regionPickerVisible: false,
      'form.region': value.join(' '),
    });
  },

  async onSubmit() {
    const { form, errors, submitting } = this.data;
    if (submitting) return;
    if (!form.name || !form.phone || !form.region || !form.detail) {
      wx.showToast({ title: '请完整填写取件联系人与地址', icon: 'none' });
      return;
    }
    if (!/^1\d{10}$/.test(form.phone) || errors.phone) {
      this.setData({ 'errors.phone': /^1\d{10}$/.test(form.phone) ? '' : '手机号格式不正确' });
      wx.showToast({ title: '请修正手机号后提交', icon: 'none' });
      return;
    }
    // 订阅必须在手势上下文里同步发起：这里一旦先进网络 await，微信可能拒绝调用
    this.requestSubscribe();

    this.setData({ submitting: true });
    try {
      const { orderNo, pickupFailReason } = await createRecycleOrder({
        modelId: this.data.quote.modelId,
        storage: this.data.quote.storage,
        condition: this.data.quote.condition,
        screenCondition: this.data.quote.screen || '',
        issues: this.data.quote.issueKeys || [],
        quoteFen: this.data.quote.priceFen,
        pickupType: form.pickupType,
        pickupName: form.name,
        pickupPhone: form.phone,
        pickupAddress: `${form.region} ${form.detail}`,
        remark: form.remark,
      });
      await this.saveAddressIfNew();
      wx.removeStorageSync('recycle.quoteResult');
      this.setData({ submitting: false });
      if (pickupFailReason) {
        this.showPickupFallback(orderNo, pickupFailReason);
        return;
      }
      wx.redirectTo({ url: `/pages/recycle/order/detail/index?orderNo=${orderNo}` });
    } catch (e) {
      wx.showToast({ title: e.message || '下单失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },

  /** 上门取件没约上：必须明确告知并给出去处，不能让用户干等一个不会来的快递员 */
  showPickupFallback(orderNo, reason) {
    wx.showModal({
      title: '上门取件未能预约',
      content: reason,
      confirmText: '去自助寄出',
      cancelText: '稍后处理',
      success: (res) => {
        const url = res.confirm
          ? `/pages/recycle/order/detail/index?orderNo=${orderNo}`
          : '/pages/recycle/order/list/index';
        wx.redirectTo({ url });
      },
    });
  },
});
