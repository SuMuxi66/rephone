// 订单评价展示卡：我的评价（星级 + 文字 + 图片），由详情页传入 comment
Component({
  properties: {
    comment: { type: Object, value: null },
  },

  methods: {
    onPreview(e) {
      const index = e.currentTarget.dataset.index;
      const urls = (this.data.comment && this.data.comment.images) || [];
      wx.previewImage({ current: urls[index], urls });
    },
  },
});
