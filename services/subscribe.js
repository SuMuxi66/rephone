import { get } from './request';

/** 订阅消息模板 ID：GET /api/wx/subscribe/templates。未配置时 orderStatus 为空串。 */
export const fetchSubscribeTemplates = () => get('/api/wx/subscribe/templates');

/**
 * 请求订阅「订单状态变更」。
 *
 * 两个硬约束：
 * 1. 微信要求带用户点击的手势上下文 —— 所以模板 ID 必须提前预取，调用方在自己的点击回调里
 *    **同步**调用本函数；先 await 一个网络请求再调，可能直接 fail。
 * 2. 用户拒绝、模板未配置、接口不可用，都不应阻塞主流程 —— 统一 resolve，不 reject。
 */
export function requestOrderStatusSubscribe(templateId) {
  if (!templateId) {
    return Promise.resolve({ skipped: true });
  }
  return new Promise((resolve) => {
    wx.requestSubscribeMessage({
      tmplIds: [templateId],
      success: resolve,
      fail: (err) => resolve({ error: (err && err.errMsg) || 'requestSubscribeMessage failed' }),
    });
  });
}
