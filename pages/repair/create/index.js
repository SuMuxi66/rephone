import { createRepairOrder, fetchCosUploadSign } from '../../../services/repair/repair';
import { fen2yuan } from '../../../common/recycle-status';
import { areaData } from '../../../config/index';

const provinces = areaData.map((p) => ({ label: p.label, value: p.label }));
const DAY_OPTIONS = [
  { label: '今天', value: '今天' },
  { label: '明天', value: '明天' },
];
const SLOT_OPTIONS = [
  { label: '上午（9:00-12:00）', value: '上午' },
  { label: '下午（12:00-18:00）', value: '下午' },
];

/** COS 直传：mock 签名无法真实上传，联调环境用本地路径占位 */
function uploadToCos(tempFilePath, ext) {
  return fetchCosUploadSign(ext).then((sign) => {
    if (sign.mock) {
      return tempFilePath;
    }
    return new Promise((resolve, reject) => {
      wx.getFileSystemManager().readFile({
        filePath: tempFilePath,
        success: (res) => {
          wx.request({
            url: `${sign.host}/${sign.key}`,
            method: 'PUT',
            header: { Authorization: sign.authorization, 'content-type': 'image/jpeg' },
            data: res.data,
            success: () => resolve(sign.key),
            fail: (err) => reject(new Error(err.errMsg || '图片上传失败')),
          });
        },
        fail: () => reject(new Error('读取图片失败')),
      });
    });
  });
}

Page({
  data: {
    selection: null,
    itemsText: '',
    totalText: '0',
    submitting: false,
    form: {
      name: '',
      phone: '',
      region: '',
      detail: '',
      timeText: '',
      remark: '',
    },
    errors: { phone: '' },
    photos: [],
    regionPickerVisible: false,
    regionValue: [],
    regionColumns: { province: provinces, city: [], district: [] },
    timePickerVisible: false,
    timeValue: ['今天', '上午'],
    timeColumns: { day: DAY_OPTIONS, slot: SLOT_OPTIONS },
  },

  onLoad() {
    const selection = wx.getStorageSync('repair.selection');
    if (!selection || !selection.items || !selection.items.length) {
      wx.showToast({ title: '请先选择维修项目', icon: 'none' });
      setTimeout(() => wx.redirectTo({ url: '/pages/repair/index' }), 1000);
      return;
    }
    const totalFen = selection.items.reduce((sum, it) => sum + (it.priceFen || 0), 0);
    this.setData({
      selection,
      itemsText: selection.items.map((it) => it.name).join('、'),
      totalText: fen2yuan(totalFen),
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

  /** 省市区级联（与回收下单页同源 areaData） */
  showRegion() {
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

  showTime() {
    this.setData({ timePickerVisible: true });
  },

  hideTime() {
    this.setData({ timePickerVisible: false });
  },

  onTimeConfirm(e) {
    const value = e.detail.value || [];
    const day = (DAY_OPTIONS[value[0]] || DAY_OPTIONS[0]).value;
    const slot = (SLOT_OPTIONS[value[1]] || SLOT_OPTIONS[0]).value;
    this.setData({
      timePickerVisible: false,
      timeValue: [value[0], value[1]],
      'form.timeText': `${day} ${slot}`,
    });
  },

  choosePhoto() {
    const left = 3 - this.data.photos.length;
    wx.chooseMedia({
      count: left,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: (res) => {
        const files = (res.tempFiles || []).map((f) => f.tempFilePath);
        this.setData({ photos: this.data.photos.concat(files) });
      },
    });
  },

  previewPhoto(e) {
    const index = e.currentTarget.dataset.index;
    wx.previewImage({ current: this.data.photos[index], urls: this.data.photos });
  },

  removePhoto(e) {
    const index = e.currentTarget.dataset.index;
    const photos = this.data.photos.slice();
    photos.splice(index, 1);
    this.setData({ photos });
  },

  async onSubmit() {
    const { form, errors, submitting, selection, photos } = this.data;
    if (submitting) return;
    if (!form.name || !form.phone || !form.region || !form.detail) {
      wx.showToast({ title: '请完整填写联系人、地区与详细地址', icon: 'none' });
      return;
    }
    if (!/^1\d{10}$/.test(form.phone) || errors.phone) {
      this.setData({ 'errors.phone': /^1\d{10}$/.test(form.phone) ? '' : '手机号格式不正确' });
      wx.showToast({ title: '请修正手机号后提交', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      // 照片先传 COS（mock 环境保留本地路径占位）
      const images = [];
      for (const path of photos) {
        const ext = (path.match(/\.(\w+)$/) || [])[1] || 'jpg';
        images.push(await uploadToCos(path, ext));
      }
      const { orderNo } = await createRepairOrder({
        modelId: selection.modelId,
        itemIds: selection.items.map((it) => it.itemId),
        serviceType: 10,
        contactName: form.name,
        contactPhone: form.phone,
        address: `${form.region} ${form.detail}`,
        appointTime: form.timeText,
        remark: form.remark,
        images,
      });
      wx.removeStorageSync('repair.selection');
      wx.redirectTo({ url: `/pages/repair/order/detail/index?orderNo=${orderNo}` });
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
