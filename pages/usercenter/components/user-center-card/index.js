const AuthStepType = {
  ONE: 1,
  TWO: 2,
  THREE: 3,
};

Component({
  options: {
    multipleSlots: true,
  },
  properties: {
    currAuthStep: {
      type: Number,
      value: AuthStepType.ONE,
    },
    userInfo: {
      type: Object,
      value: {},
    },
    isNeedGetUserInfo: {
      type: Boolean,
      value: false,
    },
  },
  data: {
    // 原为 TDesign 模板的远程默认头像（tdesign.gtimg.com），依赖第三方 CDN；
    // 置空后由 t-avatar 渲染自带默认图标（纯 CSS/字体，无外网依赖）。
    defaultAvatarUrl: '',
    AuthStepType,
  },
  methods: {
    gotoUserEditPage() {
      this.triggerEvent('gotoUserEditPage');
    },
  },
});
