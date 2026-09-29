import { statusDesc as recycleDesc, statusTheme as recycleTheme } from '../../common/recycle-status';
import { statusDesc as repairDesc, statusTheme as repairTheme } from '../../common/repair-status';

Component({
  properties: {
    status: {
      type: null,
      value: 0,
      observer() {
        this.update();
      },
    },
    // 业务线：recycle=回收单（默认），repair=维修单（状态字典不同）
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
      const desc = this.data.biz === 'repair' ? repairDesc : recycleDesc;
      const theme = this.data.biz === 'repair' ? repairTheme : recycleTheme;
      this.setData({
        label: desc(this.data.status),
        theme: theme(this.data.status),
      });
    },
  },
});
