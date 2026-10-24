import { get } from './request';

/** COS 直传签名：GET /api/wx/cos/upload-sign（dir 白名单：recycle/repair/comment/avatar） */
export const fetchCosUploadSign = (ext, dir) =>
  get('/api/wx/cos/upload-sign', dir ? { ext, dir } : { ext });

/**
 * 裸 body PUT 直传 COS（XML API 只收裸 body，wx.uploadFile 的 multipart 不适用），成功返回对象 key。
 * mock 模式（未配 COS 密钥）直接抛错，由调用方提示，不落脏数据。
 */
export const uploadToCos = (tempFilePath, ext, dir) =>
  fetchCosUploadSign(ext, dir).then((sign) => {
    if (sign.mock) {
      throw new Error('对象存储未配置，暂不支持上传');
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
            success: (r) => (r.statusCode === 200
              ? resolve(sign.key)
              : reject(new Error('图片上传失败(' + r.statusCode + ')'))),
            fail: (err) => reject(new Error(err.errMsg || '图片上传失败')),
          });
        },
        fail: () => reject(new Error('读取图片失败')),
      });
    });
  });
