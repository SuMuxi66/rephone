import { config } from '../config/index';

/**
 * 后端图片路径 → 可访问完整 URL。
 * 后端静态资源返回相对路径（如 /img/goods/x.png，见 WebResourceConfig），
 * 小程序 image 组件会把它当包内路径导致裂图，必须拼上 apiBaseUrl。
 */
export const resolveImageUrl = (path) => {
  if (!path) return '';
  if (path.indexOf('http://') === 0 || path.indexOf('https://') === 0) return path;
  return config.apiBaseUrl + path;
};
