const getPermission = ({ code, name }) => {
  return new Promise((resolve, reject) => {
    wx.getSetting({
      success: (res) => {
        if (res.authSetting[code] === false) {
          wx.showModal({
            title: `获取${name}失败`,
            content: `获取${name}失败，请在【右上角】-小程序【设置】项中，将【${name}】开启。`,
            confirmText: '去设置',
            confirmColor: '#FA550F',
            cancelColor: '取消',
            success(res) {
              if (res.confirm) {
                wx.openSetting({
                  success(settingRes) {
                    if (settingRes.authSetting[code] === true) {
                      resolve();
                    } else {
                      console.warn('用户未打开权限', name, code);
                      reject();
                    }
                  },
                });
              } else {
                reject();
              }
            },
            fail() {
              reject();
            },
          });
        } else {
          resolve();
        }
      },
      fail() {
        reject();
      },
    });
  });
};

/**
 * wx.chooseAddress fail 兜底：
 * - 用户主动取消：静默；
 * - 授权被拒：弹窗引导去设置开启；
 * - 接口权限未开通（-80424 / no permission / privacy，如演示 appid 未开通"获取用户收货地址"）：
 *   由调用方通过 onNoPermission 给出"请手动填写"类提示。
 * 返回 true 表示错误已在此处理，调用方无需再提示。
 */
const handleChooseAddressFail = (err, options) => {
  const opts = options || {};
  const msg = (err && err.errMsg) || '';
  if (msg.indexOf('cancel') !== -1) {
    return true;
  }
  if (msg.indexOf('auth deny') !== -1 || msg.indexOf('authorize') !== -1) {
    wx.showModal({
      title: '无法获取微信收货地址',
      content: '请在设置中开启通讯地址权限后重试',
      confirmText: '去设置',
      success(res) {
        if (res.confirm) {
          wx.openSetting();
        }
      },
    });
    return true;
  }
  if (msg.indexOf('-80424') !== -1 || msg.indexOf('permission') !== -1 || msg.indexOf('privacy') !== -1) {
    if (typeof opts.onNoPermission === 'function') {
      opts.onNoPermission();
    }
    return true;
  }
  return false;
};

module.exports = {
  getPermission,
  handleChooseAddressFail,
};
