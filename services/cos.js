import { get, getToken } from './request';
import { config } from '../config/index';

/** COS 直传签名：GET /api/wx/cos/upload-sign（dir 白名单：recycle/repair/comment/avatar） */
export const fetchCosUploadSign = (ext, dir) =>
  get('/api/wx/cos/upload-sign', dir ? { ext, dir } : { ext });

/**
 * 图片上传统一入口：配了 COS 密钥 → 裸 body PUT 直传（XML API 只收裸 body，wx.uploadFile
 * 的 multipart 不适用），返回 COS 对象 key；未配（mock 签名）→ 走后端本地存储兜底
 * /api/wx/upload/image，返回 /img/** 访问路径（resolveImageUrl 拼基址回显）。
 * 两种返回都可直接存 avatarUrl/图片字段，展示层用 resolveImageUrl 统一处理。
 */
export const uploadToCos = (tempFilePath, ext, dir) =>
  fetchCosUploadSign(ext, dir).then((sign) => {
    if (!sign.mock) {
      return new Promise((resolve, reject) => {
        wx.getFileSystemManager().readFile({
          filePath: tempFilePath,
          success: (res) => {
            wx.request({
              url: sign.host + '/' + sign.key,
              method: 'PUT',
              header: { Authorization: sign.authorization, 'content-type': 'image/jpeg' },
              data: res.data,
              success: (r) => (r.statusCode === 200
                ? resolve(sign.key)
                : reject(new Error('图片上传失败(' + r.statusCode + ')'))),
              fail: (err) => reject(new Error(err.errMsg || '图片上传失败')),
            });
          },
          fail: () => reject(new Error('读取图片失败')),
        });
      });
    }
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: config.apiBaseUrl + '/api/wx/upload/image?dir=' + (dir || 'avatar'),
        filePath: tempFilePath,
        name: 'file',
        header: { Authorization: 'Bearer ' + getToken() },
        success: (r) => {
          let body = {};
          try {
            body = JSON.parse(r.data) || {};
          } catch (e) {
            body = {};
          }
          if (r.statusCode === 200 && body.code === 0 && body.data && body.data.url) {
            resolve(body.data.url);
          } else {
            reject(new Error((body && body.message) || '图片上传失败(' + r.statusCode + ')'));
          }
        },
        fail: (err) => reject(new Error(err.errMsg || '图片上传失败')),
      });
    });
  });
