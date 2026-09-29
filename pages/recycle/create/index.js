import { createRecycleOrder } from '../../../services/recycle/order';
import { fen2yuan } from '../../../common/recycle-status';
import { areaData } from '../../../config/index';

const provinces = areaData.map((p) => ({ label: p.label, value: p.label }));

Page({
  data: {
    quote: null,
    quoteText: '',
    submitting: false,
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
    const quote = wx.getStorageSync('recycle.quoteResult');
    if (!quote || !quote.priceFen) {
      wx.showToast({ title: '请先完成估价', icon: 'none' });
      setTimeout(() => wx.redirectTo({ url: '/pages/recycle/estimate/index' }), 1000);
      return;
    }
    this.setData({ quote, quoteText: fen2yuan(quote.priceFen) });
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
      patch['regionColumns.city'] = cities.map((c) => ({ label: c.label, value: c.label }));
      patch['regionColumns.district'] = ((cities[0] && cities[0].children) || [])
        .map((d) => ({ label: d.label, value: d.label }));
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
      this.setData({
        'regionColumns.city': cities.map((c) => ({ label: c.label, value: c.label })),
        'regionColumns.district': ((cities[0] && cities[0].children) || [])
          .map((d) => ({ label: d.label, value: d.label })),
      });
      return;
    }
    if (column === 1) {
      const cityLabel = (this.data.regionColumns.city[index] || {}).label;
      if (!cityLabel) return;
      this._selCity = cityLabel;
      const province = areaData.find((x) => x.label === this._selProvince);
      const city = ((province && province.children) || []).find((c) => c.label === cityLabel);
      this.setData({
        'regionColumns.district': ((city && city.children) || [])
          .map((d) => ({ label: d.label, value: d.label })),
      });
    }
  },

  onRegionConfirm(e) {
    const value = (e.detail.value || []).filter(Boolean);
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
    this.setData({ submitting: true });
    try {
      const { orderNo } = await createRecycleOrder({
        modelId: this.data.quote.modelId,
        storage: this.data.quote.storage,
        condition: this.data.quote.condition,
        issues: this.data.quote.issueKeys || [],
        quoteFen: this.data.quote.priceFen,
        pickupType: form.pickupType,
        pickupName: form.name,
        pickupPhone: form.phone,
        pickupAddress: `${form.region} ${form.detail}`,
        remark: form.remark,
      });
      wx.removeStorageSync('recycle.quoteResult');
      wx.redirectTo({ url: `/pages/recycle/order/detail/index?orderNo=${orderNo}` });
    } catch (e) {
      wx.showToast({ title: e.message || '下单失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
