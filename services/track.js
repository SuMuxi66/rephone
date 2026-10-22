import { post } from './request';

/** 事件类型，与后端 ClickEvent 常量一致 */
export const TRACK = {
  APP_OPEN: 1,
  GOODS_EXPOSE: 2,
  GOODS_VIEW: 3,
  MODEL_CLICK: 4,
};

/**
 * 上报行为事件（启动/商品曝光/商品浏览/机型点击）。
 * 埋点是旁路数据：所有错误静默吞掉，绝不影响业务主流程；
 * 未登录时请求层会自动登录后重放一次。
 */
export function reportEvents(events) {
  if (!Array.isArray(events) || !events.length) return;
  post('/api/wx/track/events', { events }).catch(() => {});
}
