import { updateProfile } from '../../../services/usercenter/fetchUsercenter';

Page({
  data: {
    nameValue: '',
    submitting: false,
  },
  onLoad(options) {
    this.setData({
      nameValue: options.name || '',
    });
  },
  onSubmit() {
    const name = (this.data.nameValue || '').trim();
    if (!name) {
      wx.showToast({ title: '昵称不能为空', icon: 'none' });
      return;
    }
    if (this.data.submitting) return;
    this.setData({ submitting: true });
    updateProfile({ nickname: name })
      .then(() => {
        wx.showToast({ title: '保存成功', icon: 'success' });
        setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
      })
      .catch((e) => {
        wx.showToast({ title: e.message || '保存失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ submitting: false });
      });
  },
  clearContent() {
    this.setData({
      nameValue: '',
    });
  },
});
