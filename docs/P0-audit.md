# P0 审计报告 · RePhone 项目现状与改造基线

> 生成：P0 阶段（2026-09-28）· 分支 `feature/recycle-mvp` · 基线提交 `3c8e400`
> 方法：只读侦察（未修改业务代码）。本报告是 P1-P7 各阶段的现状输入。

---

## 1. 技术形态确认（对照 AI_PLAN 硬约束）

| 检查项 | 结论 | 证据 |
|---|---|---|
| 技术形态 | 原生微信小程序（非 uni-app/Taro） | `app.js`/`app.json`/`app.wxss`，每页 js/json/wxml/wxss 四件套 |
| 模板来源 | Tencent `@tencent/tdesign-miniprogram-starter-retail` v0.2.0（MIT） | `package.json:2`，`project.config.json` projectname |
| UI 库 | `tdesign-miniprogram@1.9.5` + `dayjs` | `package.json` dependencies；`miniprogram_npm/` 已构建 |
| 后端 | **无**。纯前端 mock | `config/index.js:3` `useMock: true`；全局无 `wx.request(`/`wx.cloud.` 调用 |
| 登录 | **无**。无 `wx.login`/`getUserProfile`/手机号授权 | 全局 grep 0 命中；个人中心默认 mock 用户 |
| 支付 | **仅演示**。`wx.requestPayment` 整段注释，直接假成功 | `pages/order/order-confirm/pay.js:100` |
| 云函数 | 无 `cloudfunctions/` 目录 | 目录清单 |
| 请求封装 | 无统一层；`services/` 下 26 个 `fetchXxx()`，mock 分支 → `model/` → `delay()` | `services/_utils/delay.js` |
| 状态管理 | 无；页面 data/setData + URL query + `wx.setStorageSync` | `pages/cart/index.js:288`、`pages/order/order-confirm/index.js:85` |
| 密钥 | 无真实密钥；仅 `project.config.json:55` 演示 appid `wx4a895c2ba165483b` | 全局 grep secret/token/mchid 仅 3 处均为空值 |

工具链：git 2.53.0 ✅ · Java 25.0.3 LTS ✅ · Node 24.14.1 ✅ · **Maven 缺失**（P1 用 Maven Wrapper `mvnw.cmd` 解决）。

---

## 2. 页面 / 组件 / services 清单

### 2.1 页面（29 页 = 主包 4 + 分包 25，自定义 tabBar）

- 主包 tabBar：`pages/home/home`（首页）、`pages/category/index`（分类）、`pages/cart/index`（购物车）、`pages/usercenter/index`（我的）
- `pages/user/` 分包：`person-info`、`name-edit`、`address/list`、`address/edit`
- `pages/goods/` 分包：`list`、`details`、`search`、`result`、`comments`、`comments/create`
- `pages/order/` 分包：`order-confirm`(+`pay.js`)、`receipt`、`pay-result`、`order-list`、`order-detail`、`apply-service`、`after-service-list`、`after-service-detail`、`fill-tracking-no`、`delivery-detail`、`invoice`；状态机常量 `pages/order/config.js`
- `pages/coupon/` 分包：`coupon-list`、`coupon-detail`、`coupon-activity-goods`
- `pages/promotion/` 分包：`promotion-detail`

### 2.2 全局组件 `components/`（9 个）

`webp-image`(t-image，几乎全站)、`price`（分→元展示）、`goods-card`、`goods-list`、`load-more`、`loading-content`、`filter`、`filter-popup`、`swipeout`、`promotion/ui-coupon-card`。

### 2.3 关键页面级组件

- cart：`cart-bar`、`cart-group`、`specs-popup`、`cart-empty`、`goods-card`
- goods/details：`buy-bar`、`goods-specs-popup`、`promotion-popup`
- order：`order-card`、`order-goods-card`、`specs-goods-card`、`order-button-bar`（按钮由数据 `buttonVOs` 驱动）、`after-service-button-bar`、`reason-sheet`、`selectCoupons`、`customer-service`、`noGoods`
- user：`t-location`（微信地址导入）、`ui-address-item`
- usercenter：`user-center-card`、`order-group`、`ui-select-picker`

### 2.4 services 层（未来接口清单）

| 域 | 函数 | 契约（model） |
|---|---|---|
| 商品 | `fetchGood(ID)`、`fetchGoodsList(pageIndex,pageSize)`、`fetchGoodsList(params)`⚠️同名、`getCategoryList()`、`getSearchHistory/getSearchPopular` | `model/good.js`、`model/goods.js`、`model/search.js`、`model/category.js` |
| 购物车 | `fetchCartGroupData()` | `model/cart.js` |
| 订单 | `fetchSettleDetail(params)`、`dispatchCommitPay(params)`、`fetchOrders/fetchOrdersCount`、`fetchOrderDetail/fetchBusinessTime` | `model/order/orderConfirm.js`、`orderList.js`、`orderDetail.js` |
| 售后 | `fetchRightsPreview`、`fetchApplyReasonList`、`dispatchApplyService`、`dispatchConfirmReceived`；页面级 mock：`getRightsList/getRightsDetail/cancelRights/create/update/getDeliverCompanyList` | `model/order/applyService.js`、`pages/order/after-service-list/api.js` 等 |
| 地址 | `fetchDeliveryAddress(id/List)`；Promise 通道 `services/address/list.js`、`edit.js` | `model/address.js` |
| 用户 | `fetchPerson`、`fetchUserCenter` | `model/usercenter.js` |
| 其他 | 优惠券 `fetchCouponList/Detail`、评价 `fetchComments(Count)/getCommentDetail/getGoods`、活动 `fetchActivity(List)/fetchPromotion`、首页 `fetchHome` | 对应 `model/` |

---

## 3. mock 数据契约摘要（P1-P3 建表依据）

- **SPU/SKU**（`model/good.js`）：`spuId, title, primaryImage, images[], available, minSalePrice/minLinePrice/maxSalePrice/maxLinePrice`（**单位:分**）`, spuStockQuantity, soldNum, isPutOnSale, categoryIds[], specList[], skuList[{skuId, specInfo[], priceInfo[{priceType,price}], stockInfo{stockQuantity}, weight}]`
- **购物车**（`model/cart.js`）：`storeGoods[{storeId, storeName, promotionGoodsList[{promotionId, goodsPromotionList[{uid, spuId, skuId, isSelected, quantity, stockQuantity, price, originPrice, specInfo[]}]}]}], invalidGoodItems[]`
- **订单**（`model/order/orderDetail.js`）：`orderNo, parentOrderNo, uid, storeId, orderStatus(5/10/40/50/80), totalAmount, paymentAmount, freightFee, discountAmount, remark, createTime, orderItemVOs[], logisticsVO{}, paymentVO{}, buttonVOs[], autoCancelTime` —— `buttonVOs` 驱动按钮的模式可直接复用到回收订单
- **售后单**（`after-service-list/api.js`）：`rightsNo, orderNo, rightsType(10/20), rightsStatus(10→20→30→50/60), refundAmount, rightsItem[], rightsRefund{}, logisticsVO{sender*}` —— 回收单状态机参考
- **地址**（`model/address.js`）：`uid, addressId, phone, name, 省/市/区 Name+Code, detailAddress, isDefault, addressTag, latitude, longitude`
- **用户**（`model/usercenter.js`）：`userInfo{avatarUrl, nickName, phoneNumber, gender}`、`customerServiceInfo{servicePhone:'4006336868'}`（演示值）
- **优惠券/类目/评价**：`model/coupon.js`、`model/category.js`（3 级树）、`model/comments.js`

---

## 4. 改造映射

### 4.1 出售端可复用（P6 接后端，改动小）
`components/{goods-card,goods-list,price,filter,filter-popup,load-more,webp-image}`；`pages/goods/{list,search,result,comments}`、`goods/details`+`goods-specs-popup`+`buy-bar`；`cart` 全套；`order-confirm`、`pay-result`、`order-list/order-detail`；`user/address/*`；`usercenter` 骨架；`utils/util.js:priceFormat`（分→元）；`config/index.js:areaData` 省市区。

### 4.2 回收端需新增（P2-P4）
- 估价：`pages/recycle/estimate|result`（品牌→型号→内存→成色→问题→报价）
- 下单：`pages/recycle/create`（邮寄/上门，地址复用 `user/address`）
- 订单：`pages/recycle/order/{list,detail}`——复用 `order-list/order-detail` 骨架 + `buttonVOs` 驱动模式；状态机 `10 待寄出→20 运输中→30 质检中→40 待确认→50 已打款→60 已完成 / 80 已取消`
- 质检报告：`pages/recycle/inspection`
- 运单填写：复用 `pages/order/fill-tracking-no`（快递公司 mock 列表换后端接口）

### 4.3 维修端 / 管理端（P5/P7）
完全空白；仅能复用订单/售后页骨架。管理后台走独立 Vue3 + Element Plus。

### 4.4 需隐藏/删除入口（P1 顺手做，P3 前完成）
- `custom-tab-bar/data.js` 去掉购物车 tab（`app.json` tabBar 同步）
- `app.json` subpackages 移除 `coupon`、`promotion`
- `pages/usercenter/index.js:menuData` 去"优惠券/积分"；`order-group` 出售订单入口暂藏
- `services/home/home.js` 写死的 7 个营销 tab、`order/receipt`、`order/invoice`
- `order-button-bar` 的 `INVITE_GROUPON(11)` 按钮类型

---

## 5. 配置与密钥位置

| 项 | 位置 | 现值/状态 |
|---|---|---|
| appid | `project.config.json:55` | `wx4a895c2ba165483b`（模板演示，待替换） |
| mock 开关 / CDN | `config/index.js:3,7` | `useMock:true`；COS `we-retail-static-1300977798` |
| 云环境 ID / 商户号 / 订阅消息模板 | — | 未发现（P1/P6 新建） |
| 快递公司 | `pages/order/fill-tracking-no/api.js` | 9 家写死 mock |
| 客服电话 | `model/usercenter.js` | 4006336868（演示） |
| 权限声明 | `app.json` | `scope.userLocation` + `requiredPrivateInfos:["chooseAddress"]` |
| 省市区 | `config/index.js` `areaData` | 内联 444KB（主包体积隐患） |
| libVersion | `project.config.json`=3.13.0 vs `project.private.config.json`=3.17.3 | 不一致，建议统一 |

**P0 期间唯一的配置变更**：`project.config.json` `packOptions.ignore` 新增 `server/`、`docs/` 两个 folder（为 P1 同仓后端铺路，微信工具不打包）。

---

## 6. 风险清单（按改造成本排序）

1. **零后端**：26 个 service 全是 `resolve('real api')` 占位；登录/支付/订单写/打款四链路从零建。`model/` 是唯一接口契约，务必按 `mock.md` 适配层方案接后端。
2. **支付反向**：回收场景是"平台付给用户"（商家转账到零钱），与模板收款方向相反，P4+ 单独设计。
3. **金额单位"分"**：`utils/util.js:priceFormat` 硬性假设分；后端 decimal(10,2) 元 → 前端分转换必须集中在 `services/request.js` 适配层。
4. **同名导出陷阱**：`services/good/fetchGoods.js` 与 `fetchGoodsList.js` 都导出 `fetchGoodsList`（签名不同），改造时极易引错。
5. **Storage 跨页传参**：`order.goodsRequestList`、`invoiceData` 两个 key；明细 JSON 有 1MB 上限与状态不同步风险，重构为服务端结算单。
6. **硬编码**：`storeId '1'`（`details/index.js:244`）vs mock `'1000'` 不一致；`saasId/uid` mock；首页 tabList、快递公司、客服电话写死。
7. **无登录态/隐私协议**：收手机号+位置需小程序后台隐私接口声明；二手数码/旧货回收类目资质待确认。
8. **杂项**：`.DS_Store` 已入 .gitignore；libVersion 不一致；P0 时 Mimosa 对 `miniprogram_npm/tslib|dayjs` 构建产物误报"命令注入"（深扫 seal `sha256:c831d6f6…`，经查为微信 npm 包装器的动态 `require`，非注入；构建产物已 gitignore 不入库，初始提交按用户决策在产物临时移出窗口期完成）。

---

## 7. P1 输入清单（下一阶段开工包）

- 工程：`E:\code-start\RePhone\server\` Maven 多模块：`common、pojo、mapper、service、controller、wechat、express、start`（父 pom + `mvnw`，因本机无全局 Maven）
- 首批表：`tenant`、`user`、`order_status_log`（全部带 `tenant_id bigint NOT NULL DEFAULT 0`）
- 首批接口：`POST /api/wx/login`（code→token，code2Session）、`GET /api/wx/user/profile`；统一返回 `{code:0, message, data}`
- 小程序配套：`services/request.js` 统一请求层（含 token 注入、元→分适配钩子）、`app.js` 启动登录、`config/index.js` useMock 改环境开关
- 环境变量占位（不落盘真实值）：`WX_APPID`、`WX_SECRET`、`JWT_SECRET`、MySQL/Redis 连接
