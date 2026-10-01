import { fen2yuan } from '../../../common/recycle-status';

Page({
  data: {
    selection: null,
    items: [],
    totalText: '0',
  },

  onLoad() {
    const selection = wx.getStorageSync('repair.selection');
    if (!selection || !selection.items || !selection.items.length) {
      // 无选择数据：tab 页只能 switchTab，redirectTo 会静默失败卡白屏
      this.setData({ noSelection: true });
      wx.showToast({ title: '请先选择维修项目', icon: 'none' });
      setTimeout(() => wx.switchTab({ url: '/pages/repair/index' }), 1200);
      return;
    }
    const totalFen = selection.items.reduce((sum, it) => sum + (it.priceFen || 0), 0);
    this.setData({
      selection,
      items: selection.items.map((it) => ({ ...it, priceText: fen2yuan(it.priceFen) })),
      totalText: fen2yuan(totalFen),
    });
  },

  goRepairTab() {
    wx.switchTab({ url: '/pages/repair/index' });
  },

  goBack() {
    wx.navigateBack();
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/repair/create/index' });
  },
});
