import updateManager from './common/updateManager';
import { ensureLogin } from './services/request';
import { reportEvents, TRACK } from './services/track';

App({
  globalData: {
    userInfo: null,
  },

  onLaunch() {
    this.silentLogin();
    // 启动埋点（谁在几点打开了小程序）：未登录时请求层会自动登录后重放
    reportEvents([{ eventType: TRACK.APP_OPEN, page: 'app-launch' }]);
  },

  onShow() {
    updateManager();
  },

  /** 静默登录：失败不阻塞，模板页面继续以 mock 数据运行 */
  async silentLogin() {
    try {
      const data = await ensureLogin();
      this.globalData.userInfo = data;
    } catch (e) {
      console.warn('[app] 静默登录失败（后端未启动或网络不可用），页面继续以 mock 数据运行:', e && e.message);
    }
  },
});
