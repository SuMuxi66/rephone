import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '../router';

export const TOKEN_KEY = 'rephone.admin.token';
export const PROFILE_KEY = 'rephone.admin.profile';

export const getToken = () => localStorage.getItem(TOKEN_KEY) || '';
export const getProfile = () => {
  try {
    return JSON.parse(localStorage.getItem(PROFILE_KEY)) || {};
  } catch (e) {
    return {};
  }
};
export const saveSession = (token, profile) => {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(PROFILE_KEY, JSON.stringify(profile || {}));
};
export const clearSession = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(PROFILE_KEY);
};

const http = axios.create({ baseURL: '/api', timeout: 15000 });

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (resp) => {
    const body = resp.data;
    if (body && typeof body.code === 'number') {
      if (body.code === 0) {
        return body.data;
      }
      if (body.code === 40100) {
        clearSession();
        ElMessage.error(body.message || '登录已过期');
        router.push('/login');
        return Promise.reject(new Error(body.message));
      }
      ElMessage.error(body.message || '操作失败');
      return Promise.reject(new Error(body.message || '操作失败'));
    }
    return body;
  },
  (err) => {
    if (err.response && err.response.status === 401) {
      clearSession();
      ElMessage.error('登录已过期，请重新登录');
      router.push('/login');
    } else {
      ElMessage.error(err.message || '网络请求失败');
    }
    return Promise.reject(err);
  },
);

export default http;
