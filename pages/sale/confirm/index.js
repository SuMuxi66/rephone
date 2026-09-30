import { createSaleOrder, fetchSaleGoodsDetail } from '../../../services/sale/order';
import { fen2yuan } from '../../../common/recycle-status';
import { areaData } from '../../../config/index';
import addressPrefill from '../../../common/address-prefill';

const provinces = areaData.map((p) => ({ label: p.label, value: p.label }));

Page({
  behaviors: [addressPrefill],

  data: {
    goods: null,
    priceText: '',
    quantity: 1,
    maxQuantity: 1,
    totalText: '0',
    submitting: false,
    form: {
      name: '',
      phone: '',
      region: '',
      detail: '',
      remark: '',
    },
    errors: { phone: '' },
    regionPickerVisible: false,
    regionValue: [],
    regionColumns: { province: provinces, city: [], district: [] },
  },

  onLoad(options) {
    this.goodsId = options.id;
    this.fetchGoods();
    this.loadAddressBook();
  },

  async fetchGoods() {
    try {
      const goods = await fetchSaleGoodsDetail(this.goodsId);
      const maxQuantity = Math.min(Math.max(goods.stock || 0, 1), 99);
      this.setData({
        goods,
        maxQuantity,
        priceText: fen2yuan(goods.priceFen),
        totalText: fen2yuan(goods.priceFen * this.data.quantity),
      });
    } catch (e) {
      wx.showToast({ title: e.message || '商品加载失败', icon: 'none' });
    }
  },

  onQuantityChange(e) {
    const quantity = e.detail.value;
    const { goods } = this.data;
    this.setData({
      quantity,
      totalText: fen2yuan((goods ? goods.priceFen : 0) * quantity),
    });
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

  /** 省市区级联（与维修/回收下单页同源 areaData） */
  showRegion() {
    const patch = { regionPickerVisible: true };
    if (!this.data.regionColumns.city.length) {
      this._selProvince = areaData[0].label;
      this._selCity = '';
      const cities = areaData[0].children || [];
      const districts = (cities[0] && cities[0].children) || [];
      const firstCity = (cities[0] && cities[0].label) || '';
      const firstDistrict = (districts[0] && districts[0].label) || '';
      patch['regionColumns.city'] = cities.map((c) => ({ label: c.label, value: c.label }));
      patch['regionColumns.district'] = districts.map((d) => ({ label: d.label, value: d.label }));
      patch.regionValue = [this._selProvince, firstCity, firstDistrict];
      this._selRegionLabels = [this._selProvince, firstCity, firstDistrict];
    }
    this.setData(patch);
  },

  hideRegion() {
    this.setData({ regionPickerVisible: false });
  },

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
        'regionColumns.district': districts.map((d) => ({ label: d.label, value: d.label })),
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
        'regionColumns.district': districts.map((d) => ({ label: d.label, value: d.label })),
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
    const { form, errors, submitting, quantity, goods } = this.data;
    if (submitting) return;
    if (!goods) {
      wx.showToast({ title: '商品信息未加载完成', icon: 'none' });
      return;
    }
    if (!form.name || !form.phone || !form.region || !form.detail) {
      wx.showToast({ title: '请完整填写收货人、地区与详细地址', icon: 'none' });
      return;
    }
    if (!/^1\d{10}$/.test(form.phone) || errors.phone) {
      this.setData({ 'errors.phone': /^1\d{10}$/.test(form.phone) ? '' : '手机号格式不正确' });
      wx.showToast({ title: '请修正手机号后提交', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      const { orderNo } = await createSaleOrder({
        goodsId: goods.id,
        quantity,
        receiverName: form.name,
        receiverPhone: form.phone,
        receiverAddr: `${form.region} ${form.detail}`,
        remark: form.remark,
      });
      await this.saveAddressIfNew();
      wx.redirectTo({ url: `/pages/sale/order/detail/index?orderNo=${orderNo}` });
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
