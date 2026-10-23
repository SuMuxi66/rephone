import { fen2yuan } from '../../../common/recycle-status';

Page({
  data: {
    quote: null,
    priceText: '',
  },

  onLoad() {
    const quote = wx.getStorageSync('recycle.quoteResult');
    if (!quote || !quote.priceFen) {
      wx.showToast({ title: '报价已失效，请重新估价', icon: 'none' });
      setTimeout(() => wx.navigateBack({ fail: () => wx.switchTab({ url: '/pages/home/home' }) }), 1200);
      return;
    }
    this.setData({
      quote,
      priceText: fen2yuan(quote.priceFen),
      // t-cell note 只收字符串：功能问题标签数组在此拼接，避免组件类型告警
      issueLabelsText: (quote.issueLabels || []).join('、'),
    });
  },

  onReQuote() {
    wx.navigateBack();
  },

  onOrder() {
    wx.navigateTo({ url: '/pages/recycle/create/index' });
  },
});
