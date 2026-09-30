import { config } from '../config/index';

const TOKEN_KEY = 'rephone.token';

export const getToken = () => wx.getStorageSync(TOKEN_KEY) || '';
export const setToken = (token) => wx.setStorageSync(TOKEN_KEY, token);
export const clearToken = () => wx.removeStorageSync(TOKEN_KEY);

function wxLogin() {
  return new Promise((resolve, reject) => {
    wx.login({
      success: (res) => (res.code ? resolve(res.code) : reject(new Error('wx.login 未返回 code'))),
      fail: reject,
    });
  });
}

let loginPromise = null;

/**
 * 静默登录：wx.login → POST /api/wx/login → 存储 token。
 * 并发调用共享同一次登录。
 */
export function ensureLogin() {
  if (loginPromise) {
    return loginPromise;
  }
  loginPromise = (async () => {
    const code = await wxLogin();
    const data = await request({ url: '/api/wx/login', method: 'POST', data: { code }, skipAuth: true });
    if (!data || !data.token) {
      throw new Error('登录响应缺少 token');
    }
    setToken(data.token);
    return data;
  })().finally(() => {
    loginPromise = null;
  });
  return loginPromise;
}

function rawRequest(options) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${config.apiBaseUrl}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      timeout: 10000,
      header: Object.assign(
        { 'content-type': 'application/json' },
        options.skipAuth || !getToken() ? {} : { Authorization: `Bearer ${getToken()}` },
      ),
      success: resolve,
      fail: (err) => reject(new Error(err.errMsg || '网络请求失败')),
    });
  });
}

/**
 * 统一请求层：解包 {code, message, data}，code=0 时 resolve(data)。
 * 401 / 40100 → 清除 token、重新静默登录后重放一次。
 */
export async function request(options) {
  const res = await rawRequest(options);
  const unauthorized = res.statusCode === 401 || (res.data && res.data.code === 40100);
  if (unauthorized) {
    if (options.skipAuth || options.retried) {
      throw new Error('登录已过期');
    }
    clearToken();
    await ensureLogin();
    return request(Object.assign({}, options, { retried: true }));
  }
  if (res.statusCode < 200 || res.statusCode >= 300) {
    throw new Error(`请求失败(${res.statusCode})`);
  }
  const body = res.data || {};
  if (body.code !== 0) {
    throw Object.assign(new Error(body.message || '业务处理失败'), { code: body.code });
  }
  return body.data;
}

export const get = (url, data) => request({ url, data, method: 'GET' });
export const post = (url, data) => request({ url, data, method: 'POST' });
export const put = (url, data) => request({ url, data, method: 'PUT' });
export const del = (url, data) => request({ url, data, method: 'DELETE' });
