/* eslint-disable no-param-reassign */
import Toast from 'tdesign-miniprogram/toast/index';
import {
  fetchAddressList,
  createAddress,
  deleteAddress,
} from '../../../../../../services/address';
import { resolveAddress, rejectAddress } from '../../../../services/address/list';
import { handleChooseAddressFail } from '../../../../utils/getPermission';

Page({
  data: {
    addressList: [],
    isOrderSure: false,
  },

  /** 选择模式（订单确认页选地址时进入；RePhone 下单流程用下单页内地址簿，不经过这里） */
  selectMode: false,
  /** 是否已经选择地址，不置为true的话页面离开时会触发取消选择行为 */
  hasSelect: false,
  /** 编辑返回后需要勾选的地址 id */
  checkedId: '',

  onLoad(query) {
    const { selectMode = '', isOrderSure = '', id = '' } = query;
    this.setData({
      isOrderSure: !!isOrderSure,
    });
    this.selectMode = !!selectMode;
    this.checkedId = id;
  },

  onShow() {
    this.getAddressList();
  },

  onUnload() {
    if (this.selectMode && !this.hasSelect) {
      rejectAddress();
    }
  },

  /** 后端地址 → 模板条目形状（t-address-item 渲染依赖） */
  toDisplayItem(a) {
    return {
      id: a.id,
      addressId: a.id,
      name: a.name,
      phoneNumber: a.phone,
      region: a.region,
      detail: a.detail,
      address: `${a.region} ${a.detail}`,
      tag: a.isDefault ? '默认' : '',
      isDefault: a.isDefault ? 1 : 0,
    };
  },

  getAddressList() {
    fetchAddressList()
      .then((list) => {
        const addressList = (list || []).map((a) => this.toDisplayItem(a));
        addressList.forEach((address) => {
          if (String(address.id) === String(this.checkedId)) {
            address.checked = true;
          }
        });
        this.setData({ addressList });
      })
      .catch(() => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '地址加载失败',
          icon: '',
          duration: 1000,
        });
      });
  },

  getWXAddressHandle() {
    wx.chooseAddress({
      success: (res) => {
        if (res.errMsg.indexOf('ok') === -1) {
          Toast({
            context: this,
            selector: '#t-toast',
            message: res.errMsg,
            icon: '',
            duration: 1000,
          });
          return;
        }
        createAddress({
          name: res.userName,
          phone: res.telNumber,
          region: `${res.provinceName} ${res.cityName} ${res.countryName}`,
          detail: res.detailInfo,
          isDefault: 0,
        })
          .then(() => {
            Toast({
              context: this,
              selector: '#t-toast',
              message: '添加成功',
              icon: '',
              duration: 1000,
            });
            this.getAddressList();
          })
          .catch((e) => {
            Toast({
              context: this,
              selector: '#t-toast',
              message: e.message || '添加失败',
              icon: '',
              duration: 1000,
            });
          });
      },
      fail: (err) => {
        const handled = handleChooseAddressFail(err, {
          onNoPermission: () => {
            Toast({
              context: this,
              selector: '#t-toast',
              message: '当前小程序未开通微信地址权限，请手动填写',
              icon: '',
              duration: 2000,
            });
          },
        });
        if (!handled) {
          console.warn('chooseAddress fail', err);
        }
      },
    });
  },

  deleteAddressHandle(e) {
    const { id } = e.currentTarget.dataset;
    wx.showModal({
      title: '删除地址',
      content: '确定删除该收货地址吗？',
      confirmColor: '#FA550F',
      success: (res) => {
        if (!res.confirm) return;
        deleteAddress(id)
          .then(() => {
            Toast({
              context: this,
              selector: '#t-toast',
              message: '删除成功',
              theme: 'success',
              duration: 1000,
            });
            this.getAddressList();
          })
          .catch(() => {
            Toast({
              context: this,
              selector: '#t-toast',
              message: '删除失败',
              icon: '',
              duration: 1000,
            });
          });
      },
    });
  },

  editAddressHandle({ detail }) {
    const { id } = detail || {};
    wx.navigateTo({ url: `/pages/user/address/edit/index?id=${id}` });
  },

  selectHandle({ detail }) {
    if (this.selectMode) {
      this.hasSelect = true;
      resolveAddress(detail);
      wx.navigateBack({ delta: 1 });
    } else {
      this.editAddressHandle({ detail });
    }
  },

  createHandle() {
    wx.navigateTo({ url: '/packages/retail-template/pages/user/address/edit/index' });
  },
});
