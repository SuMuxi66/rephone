import { recordSignatures } from '../../common/agreement-manager';

/**
 * 通用协议签署弹窗。
 * 页面侧不直接操作本组件，统一走 common/agreement-manager.js 的 ensureAgreements(page, scene)：
 * 文档内容与场景队列见 common/agreement-docs.js；组件自身保持通用，可任意传入 docs。
 *
 * 两种模式：
 * - read：逐篇全文阅读（可要求滑到底部）→ 勾选「我已阅读并同意」→ 确认，队列自动进入下一篇；
 * - confirm：清单勾选（全局首启），点标题阅读全文，全部勾选后才能点「同意并继续」，无关闭/拒绝出口。
 */
Component({
  options: { multipleSlots: false },

  data: {
    opened: false,
    mode: 'read',
    docs: [],
    /** confirm 模式的勾选态 {docKey: true} */
    checks: {},
    allChecked: false,
    /** 当前打开的单篇全文；confirm 清单模式下为 null */
    view: null,
    /** read 模式当前篇下标 */
    current: 0,
    agreed: false,
    /** 当前篇是否已滑动到底（requireRead 文档必须满足，否则确认按钮置灰） */
    reachedEnd: false,
    /** 阅读区滚动位置：翻篇时 0/0.5 交替触发回顶（scroll-top 同值不生效） */
    scrollTop: 0,
  },

  lifetimes: {
    detached() {
      // 页面在弹窗打开期间被销毁时兜底释放挂起的 Promise，避免调用方永久等待
      if (this._reject) {
        this._reject(new Error('agreement-modal destroyed'));
        this._resolve = null;
        this._reject = null;
      }
    },
  },

  methods: {
    /**
     * 打开签署流程。全部签完 resolve；拒绝/关闭/组件销毁 reject。
     * @param {Object} options { scene, mode: 'read'|'confirm', docs: [{key,title,version,content,requireRead}] }
     */
    open(options) {
      const { scene = 'custom', mode = 'read', docs = [], maskClosable = true } = options || {};
      if (!docs.length) {
        return Promise.resolve();
      }
      if (this._reject) {
        // 上一次流程未结束又被 open：先收掉旧 Promise，防止悬挂
        this._reject(new Error('agreement reopened'));
      }
      this._scene = scene;
      this._maskClosable = maskClosable;
      this._busy = false;
      let resolveFn = null;
      let rejectFn = null;
      const promise = new Promise((resolve, reject) => {
        resolveFn = resolve;
        rejectFn = reject;
      });
      this._resolve = resolveFn;
      this._reject = rejectFn;
      this.setData(
        {
          opened: true,
          mode,
          docs,
          checks: {},
          allChecked: false,
          current: 0,
          view: mode === 'read' ? docs[0] : null,
          agreed: false,
          reachedEnd: false,
        },
        () => this.checkOverflow(),
      );
      return promise;
    },

    noop() {},

    onMaskTap() {
      // 全局首启等 lock 场景点遮罩不关闭：不同意就不能用
      if (this._maskClosable) {
        this.finish(false);
      }
    },

    finish(signed) {
      if (!this.data.opened) return;
      this._busy = false;
      this.setData({ opened: false, view: null });
      const resolveFn = this._resolve;
      const rejectFn = this._reject;
      this._resolve = null;
      this._reject = null;
      if (signed && resolveFn) resolveFn();
      if (!signed && rejectFn) rejectFn(new Error('用户未同意协议'));
    },

    // ===== read 模式：逐篇签署 =====

    toggleAgreed() {
      this.setData({ agreed: !this.data.agreed });
    },

    onReachEnd() {
      if (!this.data.reachedEnd) {
        this.setData({ reachedEnd: true });
      }
    },

    /**
     * 渲染后量一次内容高度：不足一屏时 scrolltolower 永远不会触发，
     * 此时直接视为已读到底，否则短文档会被锁死在禁用按钮上。
     * 首帧可能还没完成布局（量到 0 高度），少量几次再放弃。
     */
    checkOverflow(attempt) {
      const run = attempt || 0;
      const query = this.createSelectorQuery();
      query.select('.doc-scroll').boundingClientRect();
      query.select('.doc-content').boundingClientRect();
      query['exec']((res) => {
        const box = res && res[0];
        const inner = res && res[1];
        if (!box || !inner || !box.height) {
          if (run < 2) {
            setTimeout(() => this.checkOverflow(run + 1), 120);
          }
          return;
        }
        if (inner.height <= box.height + 2) {
          this.onReachEnd();
        }
      });
    },

    // ===== confirm 模式：清单勾选 =====

    onToggleCheck(e) {
      const key = e.currentTarget.dataset.key;
      const checks = Object.assign({}, this.data.checks);
      if (checks[key]) {
        delete checks[key];
      } else {
        checks[key] = true;
      }
      this.setData({ checks, allChecked: this.data.docs.every((doc) => checks[doc.key]) });
    },

    onToggleAll() {
      const allChecked = !this.data.allChecked;
      const checks = {};
      if (allChecked) {
        this.data.docs.forEach((doc) => {
          checks[doc.key] = true;
        });
      }
      this.setData({ checks, allChecked });
    },

    openDetail(e) {
      const key = e.currentTarget.dataset.key;
      const doc = this.data.docs.find((item) => item.key === key);
      if (doc) {
        this.setData({ view: doc }, () => this.checkOverflow());
      }
    },

    closeDetail() {
      this.setData({ view: null });
    },

    // ===== 确认 / 拒绝 =====

    onConfirm() {
      // 重入防护：确认键是裸节点绑定，连点第二下会在记账后再次进入
      //（终篇/confirm 模式下 data 里的勾选态仍为真，会重复记账、重复上报）
      if (this._busy) return;
      const { mode, docs, current, view, agreed, reachedEnd, allChecked } = this.data;
      if (mode === 'confirm') {
        if (!allChecked) return;
        this._busy = true;
        recordSignatures(docs, this._scene);
        this.finish(true);
        return;
      }
      if (!view || !agreed) return;
      if (view.requireRead && !reachedEnd) return;
      this._busy = true;
      recordSignatures([view], this._scene);
      const next = current + 1;
      if (next < docs.length) {
        this.setData(
          {
            current: next,
            view: docs[next],
            agreed: false,
            reachedEnd: false,
            scrollTop: this.data.scrollTop === 0 ? 0.5 : 0,
          },
          () => {
            this._busy = false;
            this.checkOverflow();
          },
        );
      } else {
        this.finish(true);
      }
    },

    onRefuse() {
      this.finish(false);
    },
  },
});
