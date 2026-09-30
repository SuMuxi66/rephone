import {
  fetchUserProfile,
  fetchRecycleOrderTotal,
  fetchRepairOrderTotal,
  fetchSaleOrderTotal,
} from '../../services/usercenter/fetchUsercenter';
import Toast from 'tdesign-miniprogram/toast/index';
import { config } from '../../config/index';

const menuData = [
  [
    {
      title: '我的维修单',
      tit: '',
      url: '',
      type: 'repair-orders',
    },
    {
      title: '我卖出的',
      tit: '',
      url: '',
      type: 'recycle-orders',
    },
    {
      title: '我买到的',
      tit: '',
      url: '',
      type: 'sale-orders',
    },
    {
      title: '收货地址',
      tit: '',
      url: '',
      type: 'address',
    },
  ],
  [
    {
      title: '帮助中心',
      tit: '',
      url: '',
      type: 'help-center',
    },
    {
      title: '客服热线',
      tit: '',
      url: '',
      type: 'service',
      icon: 'service',
    },
  ],
];

const getDefaultData = () => ({
  showMakePhone: false,
  userInfo: {
    avatarUrl: '',
    nickName: '正在登录...',
    phoneNumber: '',
  },
  menuData,
  customerServiceInfo: {
    servicePhone: config.servicePhone,
    serviceTimeDuration: config.serviceTimeDuration,
  },
  currAuthStep: 1,
  showKefu: true,
  versionNo: '',
});

Page({
  data: getDefaultData(),

  onLoad() {
    this.getVersionInfo();
  },

  onShow() {
    this.getTabBar().init();
    this.init();
  },
  onPullDownRefresh() {
    this.init();
  },

  init() {
    this.fetUseriInfoHandle();
  },

  fetUseriInfoHandle() {
    // 单条统计失败不影响整页：接口缺失/未登录时降级为 0
    Promise.all([
      fetchUserProfile(),
      fetchRecycleOrderTotal().catch(() => 0),
      fetchRepairOrderTotal().catch(() => 0),
      fetchSaleOrderTotal().catch(() => 0),
    ])
      .then(([profile, recycleTotal, repairTotal, saleTotal]) => {
        const totals = {
          'recycle-orders': recycleTotal,
          'repair-orders': repairTotal,
          'sale-orders': saleTotal,
        };
        menuData[0].forEach((v) => {
          if (totals[v.type] !== undefined) {
            // eslint-disable-next-line no-param-reassign
            v.tit = totals[v.type];
          }
        });
        this.setData({
          userInfo: {
            avatarUrl: profile.avatarUrl || '',
            nickName: profile.nickname || 'RePhone 用户',
            phoneNumber: profile.phone || '',
          },
          menuData,
          currAuthStep: 2,
        });
        wx.stopPullDownRefresh();
      })
      .catch(() => {
        // 未登录/网络失败：显示默认卡片，不阻断其它入口
        this.setData({
          userInfo: { avatarUrl: '', nickName: '点击登录', phoneNumber: '' },
          currAuthStep: 2,
        });
        wx.stopPullDownRefresh();
      });
  },

  onClickCell({ currentTarget }) {
    const { type } = currentTarget.dataset;

    switch (type) {
      case 'recycle-orders': {
        wx.navigateTo({ url: '/pages/recycle/order/list/index' });
        break;
      }
      case 'repair-orders': {
        wx.navigateTo({ url: '/pages/repair/order/list/index' });
        break;
      }
      case 'sale-orders': {
        wx.navigateTo({ url: '/pages/sale/order/list/index' });
        break;
      }
      case 'address': {
        wx.navigateTo({ url: '/pages/user/address/list/index' });
        break;
      }
      case 'service': {
        this.openMakePhone();
        break;
      }
      case 'help-center': {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '你点击了帮助中心',
          icon: '',
          duration: 1000,
        });
        break;
      }
      default: {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '未知跳转',
          icon: '',
          duration: 1000,
        });
        break;
      }
    }
  },

  openMakePhone() {
    this.setData({ showMakePhone: true });
  },

  closeMakePhone() {
    this.setData({ showMakePhone: false });
  },

  call() {
    wx.makePhoneCall({
      phoneNumber: this.data.customerServiceInfo.servicePhone,
    });
  },

  gotoUserEditPage() {
    const { currAuthStep } = this.data;
    if (currAuthStep === 2) {
      wx.navigateTo({ url: '/pages/user/person-info/index' });
    } else {
      this.fetUseriInfoHandle();
    }
  },

  getVersionInfo() {
    const versionInfo = wx.getAccountInfoSync();
    const { version, envVersion = __wxConfig } = versionInfo.miniProgram;
    this.setData({
      versionNo: envVersion === 'release' ? version : envVersion,
    });
  },
});
