import { statusDesc, statusTheme } from '../../common/recycle-status';

Component({
  properties: {
    status: {
      type: null,
      value: 0,
      observer(status) {
        this.update(status);
      },
    },
  },

  data: {
    label: '',
    theme: 'default',
  },

  lifetimes: {
    attached() {
      this.update(this.data.status);
    },
  },

  methods: {
    update(status) {
      this.setData({
        label: statusDesc(status),
        theme: statusTheme(status),
      });
    },
  },
});
