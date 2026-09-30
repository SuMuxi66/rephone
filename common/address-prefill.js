import { fetchAddressList, createAddress } from '../services/address';

/**
 * 下单页地址簿共用逻辑（维修/回收表单同构：form.{name,phone,region,detail}）：
 * 进页自动带入默认地址；半屏列表切换历史地址；订单提交成功后把未入库的
 * 新地址写回（联系人+电话+地区+详址四元组判重），首条自动设默认。
 */
export default Behavior({
  data: {
    addressBook: [],
    addressSheetVisible: false,
  },

  methods: {
    /** 拉取地址簿并自动带入默认（或第一条）地址；未登录/网络失败静默降级 */
    loadAddressBook() {
      return fetchAddressList()
        .then((list) => {
          const book = list || [];
          this.setData({ addressBook: book });
          if (book.length) {
            this.applyAddress(book.find((a) => a.isDefault) || book[0]);
          }
          return book;
        })
        .catch(() => []);
    },

    applyAddress(addr) {
      if (!addr) return;
      this.setData({
        'form.name': addr.name || '',
        'form.phone': addr.phone || '',
        'form.region': addr.region || '',
        'form.detail': addr.detail || '',
        'errors.phone': '',
      });
    },

    onAddressSheetVisibleChange(e) {
      this.setData({ addressSheetVisible: !!e.detail.visible });
    },

    onPickAddress(e) {
      const id = e.currentTarget.dataset.id;
      this.applyAddress(this.data.addressBook.find((a) => a.id === id));
      this.setData({ addressSheetVisible: false });
    },

    /** 订单提交成功后调用：新地址自动入库 */
    saveAddressIfNew() {
      const { form, addressBook } = this.data;
      const exists = addressBook.some(
        (a) =>
          a.name === form.name &&
          a.phone === form.phone &&
          a.region === form.region &&
          a.detail === form.detail,
      );
      if (exists || !form.region || !form.detail) {
        return Promise.resolve();
      }
      const isDefault = addressBook.length ? 0 : 1;
      return createAddress({
        name: form.name,
        phone: form.phone,
        region: form.region,
        detail: form.detail,
        isDefault,
      })
        .then(({ addressId }) => {
          this.setData({
            addressBook: addressBook.concat([
              {
                id: addressId,
                name: form.name,
                phone: form.phone,
                region: form.region,
                detail: form.detail,
                isDefault,
              },
            ]),
          });
        })
        .catch(() => {});
    },
  },
});
