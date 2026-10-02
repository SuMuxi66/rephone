import { createRepairOrder, fetchCosUploadSign } from '../../../services/repair/repair';
import { fen2yuan } from '../../../common/recycle-status';
import { areaData } from '../../../config/index';
import addressPrefill from '../../../common/address-prefill';

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
  behaviors: [addressPrefill],

  data: {
    selection: null,
    itemsText: '',
    totalText: '0',
    submitting: false,
    // 服务方式：10 上门维修（默认） 20 寄修
    serviceType: 10,
    serviceOptions: [
      { label: '上门维修', value: 10, desc: '工程师按约定时间上门' },
      { label: '寄修', value: 20, desc: '自行寄出，修好回寄' },
    ],
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
      // 无选择数据（提交后返回/重编译进本页）：tab 页只能 switchTab，redirectTo 会静默失败卡白屏
      this.setData({ noSelection: true });
      wx.showToast({ title: '请先选择维修项目', icon: 'none' });
      setTimeout(() => wx.switchTab({ url: '/pages/repair/index' }), 1200);
      return;
    }
    const totalFen = selection.items.reduce((sum, it) => sum + (it.priceFen || 0), 0);
    this.setData({
      selection,
      itemsText: selection.items.map((it) => it.name).join('、'),
      totalText: fen2yuan(totalFen),
    });
    this.loadAddressBook();
  },

  goRepairTab() {
    wx.switchTab({ url: '/pages/repair/index' });
  },

  /** 服务方式切换：寄修无需预约时间 */
  onServiceChange(e) {
    this.setData({ serviceType: Number(e.currentTarget.dataset.value) });
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
    const { form, errors, submitting, selection, photos, serviceType } = this.data;
    if (submitting) return;
    if (!form.name || !form.phone || !form.region || !form.detail) {
      wx.showToast({ title: '请完整填写联系人、地区与详细地址', icon: 'none' });
      return;
    }
    const mailIn = Number(serviceType) === 20;
    if (!mailIn && !form.timeText) {
      wx.showToast({ title: '请选择预约时间', icon: 'none' });
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
        serviceType,
        contactName: form.name,
        contactPhone: form.phone,
        address: `${form.region} ${form.detail}`,
        appointTime: mailIn ? '' : form.timeText,
        remark: form.remark,
        images,
      });
      await this.saveAddressIfNew();
      wx.removeStorageSync('repair.selection');
      wx.redirectTo({ url: `/pages/repair/order/detail/index?orderNo=${orderNo}` });
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },
});
