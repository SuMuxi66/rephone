import { statusDesc as recycleDesc, statusTheme as recycleTheme } from '../../common/recycle-status';
import { statusDesc as repairDesc, statusTheme as repairTheme } from '../../common/repair-status';
import { statusDesc as saleDesc, statusTheme as saleTheme } from '../../common/sale-status';

/** biz → 状态字典（不同业务线状态码含义不同，必须显式指定） */
const DICT = {
  recycle: { desc: recycleDesc, theme: recycleTheme },
  repair: { desc: repairDesc, theme: repairTheme },
  sale: { desc: saleDesc, theme: saleTheme },
};

Component({
  properties: {
    status: {
      type: null,
      value: 0,
      observer() {
        this.update();
      },
    },
    // 业务线：recycle=回收单（默认）/ repair=维修单 / sale=出售订单（我买到的）
    biz: {
      type: String,
      value: 'recycle',
      observer() {
        this.update();
      },
    },
  },

  data: {
    label: '',
    theme: 'default',
  },

  lifetimes: {
    attached() {
      this.update();
    },
  },

  methods: {
    update() {
      const dict = DICT[this.data.biz] || DICT.recycle;
      this.setData({
        label: dict.desc(this.data.status),
        theme: dict.theme(this.data.status),
      });
    },
  },
});
