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
      priceText: (quote.priceFen / 100).toFixed(2),
    });
  },

  onReQuote() {
    wx.navigateBack();
  },

  onOrder() {
    wx.showToast({ title: '回收下单功能将在下一阶段上线', icon: 'none' });
  },
});
