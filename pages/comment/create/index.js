import { createComment, fetchOrderComment } from '../../../services/comment';
import { fetchCosUploadSign } from '../../../services/recycle/order';

const MAX_PHOTOS = 9;

const TYPE_LABELS = {
  10: '回收订单',
  20: '出售订单',
  30: '维修订单',
};

Page({
  data: {
    orderType: 0,
    orderNo: '',
    typeLabel: '',
    /** 已评价（含刚提交成功）：只读展示评价内容 */
    comment: null,
    // 表单态
    score: 5,
    content: '',
    photos: [],
    submitting: false,
  },

  onLoad(options) {
    const orderType = Number(options.orderType) || 0;
    const orderNo = options.orderNo || '';
    this.setData({ orderType, orderNo, typeLabel: TYPE_LABELS[orderType] || '订单' });
    this.loadExisting();
  },

  /** 已评过直接进只读态；拉取失败不阻塞填写，提交时后端兜底 */
  async loadExisting() {
    try {
      const comment = await fetchOrderComment(this.data.orderNo, this.data.orderType);
      if (comment) {
        this.setData({ comment });
      }
    } catch (e) {
      // 忽略，按未评价处理
    }
  },

  onScoreChange(e) {
    this.setData({ score: e.detail.value });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  choosePhoto() {
    const left = MAX_PHOTOS - this.data.photos.length;
    if (left <= 0) {
      wx.showToast({ title: '最多 9 张图片', icon: 'none' });
      return;
    }
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
    const photos = this.data.photos.slice();
    photos.splice(e.currentTarget.dataset.index, 1);
    this.setData({ photos });
  },

  async onSubmit() {
    const { orderType, orderNo, score, content, photos, submitting } = this.data;
    if (submitting) return;
    if (!orderType || !orderNo) {
      wx.showToast({ title: '订单信息缺失，无法评价', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      // 图片先传 COS（mock 环境保留本地路径占位）
      const images = [];
      for (const path of photos) {
        const ext = (path.match(/\.(\w+)$/) || [])[1] || 'jpg';
        images.push(await this.uploadToCos(path, ext));
      }
      await createComment({ orderType, orderNo, score, content, images });
      // 需求验收：提交成功后显示评价内容
      const comment = await fetchOrderComment(orderNo, orderType);
      this.setData({ comment });
      wx.showToast({ title: '评价成功', icon: 'success' });
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },

  goBack() {
    wx.navigateBack();
  },

  /** COS 直传（与维修下单页同款），目录固定 comment */
  uploadToCos(tempFilePath, ext) {
    return fetchCosUploadSign(ext, 'comment').then((sign) => {
      if (sign.mock) {
        return tempFilePath;
      }
      return new Promise((resolve, reject) => {
        wx.getFileSystemManager().readFile({
          filePath: tempFilePath,
          success: (res) => {
            wx.request({
              url: sign.host + '/' + sign.key,
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
  },
});
