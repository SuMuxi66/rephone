import { fetchPerson } from '../../../../../services/usercenter/fetchPerson';
import { updateProfile } from '../../../../../services/usercenter/fetchUsercenter';
import { phoneEncryption } from '../../../utils/util';
import { clearToken, ensureLogin } from '../../../../../services/request';
import Toast from 'tdesign-miniprogram/toast/index';

Page({
  data: {
    personInfo: {
      avatarUrl: '',
      nickName: '',
      gender: 0,
      phoneNumber: '',
    },
    showUnbindConfirm: false,
    phoneSheetVisible: false,
    phoneInput: '',
    pickerOptions: [
      {
        name: '男',
        code: '1',
      },
      {
        name: '女',
        code: '2',
      },
    ],
    typeVisible: false,
    genderMap: ['', '男', '女'],
  },
  onShow() {
    this.init();
  },
  init() {
    this.fetchData();
  },
  fetchData() {
    fetchPerson().then((personInfo) => {
      this.setData({
        personInfo,
        'personInfo.phoneNumber': phoneEncryption(personInfo.phoneNumber),
      });
    });
  },
  onClickCell({ currentTarget }) {
    const { dataset } = currentTarget;
    const { nickName } = this.data.personInfo;

    switch (dataset.type) {
      case 'gender':
        this.setData({
          typeVisible: true,
        });
        break;
      case 'name':
        wx.navigateTo({
          url: `/packages/retail-template/pages/user/name-edit/index?name=${nickName}`,
        });
        break;
      case 'avatarUrl':
        this.toModifyAvatar();
        break;
      case 'phoneNumber':
        this.setData({
          phoneSheetVisible: true,
          phoneInput: this.data.personInfo.phoneNumber || '',
        });
        break;
      default: {
        break;
      }
    }
  },
  onPhoneInput(e) {
    this.setData({ phoneInput: e.detail.value });
  },
  hidePhoneSheet() {
    this.setData({ phoneSheetVisible: false });
  },
  onPhoneSave() {
    const phone = (this.data.phoneInput || '').trim();
    if (phone && !/^1\d{10}$/.test(phone)) {
      Toast({ context: this, selector: '#t-toast', message: '手机号格式不正确', theme: 'error' });
      return;
    }
    updateProfile({ phoneNumber: phone })
      .then(() => {
        this.setData({ phoneSheetVisible: false });
        Toast({ context: this, selector: '#t-toast', message: phone ? '绑定成功' : '已解绑', theme: 'success' });
        this.fetchData();
      })
      .catch((err) => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: err.message || '保存失败',
          theme: 'error',
          duration: 1000,
        });
      });
  },
  /** 切换账号：清本地登录态后重新走登录流程（真实微信登录下仍为当前微信身份） */
  openUnbindConfirm() {
    this.setData({ showUnbindConfirm: true });
  },
  onUnbindClose() {
    this.setData({ showUnbindConfirm: false });
  },
  onUnbindConfirm() {
    this.setData({ showUnbindConfirm: false });
    clearToken();
    ensureLogin()
      .then(() => {
        Toast({ context: this, selector: '#t-toast', message: '已重新登录', theme: 'success' });
        this.fetchData();
      })
      .catch((err) => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: err.message || '重新登录失败',
          theme: 'error',
          duration: 1000,
        });
      });
  },
  onClose() {
    this.setData({
      typeVisible: false,
    });
  },
  onConfirm(e) {
    const { value } = e.detail;
    const raw = value && typeof value === 'object' ? value.code : value;
    const gender = Number(raw);
    this.setData({
      typeVisible: false,
      'personInfo.gender': gender,
    });
    updateProfile({ gender: Number.isNaN(gender) ? null : gender })
      .then(() => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '设置成功',
          theme: 'success',
        });
      })
      .catch((err) => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: err.message || '设置失败',
          icon: '',
          duration: 1000,
        });
      });
  },
  async toModifyAvatar() {
    try {
      const tempFilePath = await new Promise((resolve, reject) => {
        wx.chooseImage({
          count: 1,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera'],
          success: (res) => {
            const { path, size } = res.tempFiles[0];
            if (size <= 10485760) {
              resolve(path);
            } else {
              reject({ errMsg: '图片大小超出限制，请重新上传' });
            }
          },
          fail: (err) => reject(err),
        });
      });
      const tempUrlArr = tempFilePath.split('/');
      const tempFileName = tempUrlArr[tempUrlArr.length - 1];
      Toast({
        context: this,
        selector: '#t-toast',
        message: `已选择图片-${tempFileName}`,
        theme: 'success',
      });
    } catch (error) {
      if (error.errMsg === 'chooseImage:fail cancel') return;
      Toast({
        context: this,
        selector: '#t-toast',
        message: error.errMsg || error.msg || '修改头像出错了',
        theme: 'error',
      });
    }
  },
});
