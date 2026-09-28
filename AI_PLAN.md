# RePhone 二手机回收/维修/出售平台 · AI 执行计划

## 0. 项目背景

- 现有工程：`E:\code-start\RePhone`
- 基础模板：Tencent TDesign 零售模板，**原生微信小程序**，纯前端 mock，无后端。
- 目标：改造成"二手机回收 / 维修 / 出售"平台。
- 第一阶段：**只做回收端微信小程序最小闭环**。
- 后端：**自建 Spring Boot 后端**。
- 原则：出售端尽量复用模板；回收端全新开发；维修端和管理后台后置。

---

## 1. 技术栈与硬约束

| 层 | 技术 |
|---|---|
| 小程序 | 原生微信小程序 + TDesign + dayjs |
| 后端 | Spring Boot 3 + MyBatis-Plus + MySQL 8 + Redis |
| 后端代码位置 | **与小程序同仓**：`E:\code-start\RePhone\server\`（Maven 多模块，已确认；微信开发者工具通过 `packOptions.ignore` 排除该目录） |
| 鉴权 | JWT + 微信 `wx.login` |
| 多租户 | 共享库 + `tenant_id` 字段隔离 |
| 快递 | 快递100 企业版 API |
| 支付 | 出售端微信支付 JSAPI；回收端"商家转账到零钱"后置 |
| 通知 | 微信订阅消息 |
| 部署 | Docker + Nginx + HTTPS |

**硬约束：**
1. 所有业务表必须带 `tenant_id`。
2. 密钥、appid、mchid、快递100 key 不得写进小程序代码，只能放后端配置或环境变量。
3. 小程序不得直接调用快递100、微信支付、订阅消息服务端接口，必须走后端。
4. 金额后端统一用"元" `decimal(10,2)`；返回小程序时转"分"整数，适配 TDesign `price` 组件。
5. 不删除模板 `model/`，接后端后作为数据适配层。
6. 每阶段必须可运行、可验证，禁止一次性大改全部代码。
7. 每阶段完成后必须输出：变更文件、运行方式、验证步骤、遗留问题。

---

## 2. 给 AI 的启动指令

将下面这段直接发给 AI：

```text
你是本项目的全栈开发代理。请读取项目根目录 AI_PLAN.md，按阶段执行。

规则：
1. 先只读分析当前项目，确认技术形态、目录结构、现有 mock 数据。
2. 不要跳阶段，不要一次性生成全部代码。
3. 每阶段开始前，先输出该阶段的任务清单和预计变更文件。
4. 每阶段完成后，输出：
   - 变更文件列表
   - 如何启动/运行
   - 如何验证（接口、页面、命令）
   - 遗留问题和下一阶段建议
5. 不修改与当前阶段无关的文件。
6. 不删除出售端可复用模块，只隐藏入口或保留。
7. 所有业务表带 tenant_id。
8. 密钥不得进前端代码。
9. 如果信息不足，先提问，不要猜测。
10. 每完成一个阶段，等待我确认后再进入下一阶段。
```

---

## 3. 总体阶段

| 阶段 | 目标 | 产出 |
|---|---|---|
| P0 | 项目确认与分支准备 | 只读报告、改造清单、git 分支 |
| P1 | 后端骨架 + 登录 + 多租户 | Spring Boot 工程、用户表、JWT、wx.login |
| P2 | 基础数据 + 估价接口 | 品牌/型号/估价规则表、估价 API、小程序估价页 |
| P3 | 回收订单闭环 | 回收订单表、下单/列表/详情 API、小程序回收订单页 |
| P4 | 快递100 + 订阅消息 | 上门取件、运单回填、状态通知 |
| P5 | 极简管理后台 | 订单列表、改状态、录质检结果 |
| P6 | 出售端复用 + 支付 | 商品/购物车/订单接后端、微信支付 |
| P7 | 维修端 + 完整管理端 | 维修工单、配件库存、多租户权限 |

---

## 4. 分阶段任务卡

### P0：项目确认与分支准备

**任务：**
- 读取现有工程，确认：
  - 原生小程序
  - TDesign 版本
  - `useMock: true`
  - 无后端、无登录、无支付
  - 页面清单、组件清单、services 清单
- 输出改造映射：
  - 出售端可复用
  - 回收端需新增
  - 需隐藏入口
- 创建 git 分支：`feature/recycle-mvp`

**验收：**
- 输出 `docs/P0-audit.md`
- 不改任何业务代码

---

### P1：后端骨架 + 登录 + 多租户

**后端任务：**
- 创建 Spring Boot 工程，模块：
  - common、pojo、mapper、service、controller、wechat、express、start
- 配置 MySQL、Redis、MyBatis-Plus、JWT
- 实现多租户插件 `TenantLineInnerInterceptor`
- 建表：
  - `user`
  - `tenant`
  - `order_status_log`
- 实现接口：
  - `POST /api/wx/login`：入参 `code`，返回 `token`
  - `GET /api/wx/user/profile`
- 实现微信 `code2Session`

**小程序任务：**
- `app.js` 加 `wx.login` 和 token 存储
- 新建 `services/request.js` 统一请求层
- `config/index.js` 的 `useMock` 改为环境开关

**验收：**
- 小程序能拿到 token
- 后端 `user` 表有记录
- 携带 token 能访问 `/api/wx/user/profile`

---

### P2：基础数据 + 估价接口

**后端任务：**
- 建表：
  - `brand`
  - `phone_model`
  - `quote_rule`
- 实现接口：
  - `GET /api/wx/brands`
  - `GET /api/wx/models?brandId=`
  - `POST /api/wx/quote/calculate`
- 导入初始品牌/型号/估价规则

**小程序任务：**
- 新增页面：
  - `pages/recycle/estimate/index`
  - `pages/recycle/result/index`
- 复用 TDesign：`t-cell`、`t-picker`、`t-radio-group`、`t-stepper`
- 流程：品牌 → 型号 → 内存 → 成色 → 功能问题 → 预估价
- 接真实估价接口

**验收：**
- 选择机型后能返回预估价
- 金额单位正确（分/元转换）

---

### P3：回收订单闭环

**后端任务：**
- 建表：
  - `recycle_order`
  - `inspection`
- 实现接口：
  - `POST /api/wx/recycle/order`
  - `GET /api/wx/recycle/orders?status=`
  - `GET /api/wx/recycle/order/{orderNo}`
  - `PUT /api/wx/recycle/order/{orderNo}/express`
- 状态机：
  - 10 待寄出
  - 20 运输中
  - 30 质检中
  - 40 待确认
  - 50 已打款
  - 60 已完成
  - 80 已取消

**小程序任务：**
- 新增：
  - `pages/recycle/create/index`
  - `pages/recycle/order/list/index`
  - `pages/recycle/order/detail/index`
- 复用 `pages/order/order-list` 和 `order-detail` 骨架
- 复用 `fill-tracking-no` 填运单号
- 新建 `common/recycle-status.js`

**验收：**
- 用户能下单
- 能在列表和详情看到订单
- 能填运单号
- 状态变更写 `order_status_log`

---

### P4：快递100 + 订阅消息

**后端任务：**
- 封装 `ExpressService`
- 实现：
  - 快递100 上门取件下单
  - 取消
  - 物流查询
- 配置 `key`、`secret` 到环境变量
- 实现订阅消息发送

**小程序任务：**
- 下单时请求 `wx.requestSubscribeMessage`
- 订单状态变更后由后端推送

**验收：**
- 沙箱环境能预约快递
- 状态变更能收到订阅消息

---

### P5：极简管理后台

**任务：**
- 新建 Vue3 + Element Plus 管理后台
- 页面：
  - 回收订单列表
  - 订单详情
  - 改状态
  - 录质检结果
  - 上传质检照片
- 接口：
  - `GET /api/admin/recycle/orders`
  - `PUT /api/admin/recycle/order/{orderNo}/status`
  - `POST /api/admin/recycle/order/{orderNo}/inspection`

**验收：**
- 运营能改状态：待寄出 → 运输中 → 质检中 → 待确认
- 能录入最终价

---

### P6：出售端复用 + 支付

**任务：**
- 把模板出售端页面接新后端：
  - `goods/list`
  - `goods/details`
  - `cart`
  - `order-confirm`
  - `pay-result`
  - `order-list`
  - `order-detail`
- 建表：
  - `product`
  - `sale_order`
  - `payment_record`
- 接微信支付 JSAPI
- 保留 `model/` 作为适配层

**验收：**
- 出售端能下单
- 能发起微信支付
- 支付回调能更新订单状态

---

### P7：维修端 + 完整管理端

**任务：**
- 建表：
  - `repair_order`
  - `repair_item`
  - `parts_inventory`
- 维修工单状态机：
  - 待接单 → 检测中 → 待报价 → 维修中 → 待取件 → 已完成
- 总管理端：
  - 租户管理
  - RBAC 权限
  - 财务对账
  - 数据看板

**验收：**
- 能创建子租户
- 能分配权限
- 维修工单能流转

---

## 5. 关键 API 契约

统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

核心接口：

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/wx/login` | 微信登录 |
| GET | `/api/wx/brands` | 品牌列表 |
| GET | `/api/wx/models?brandId=` | 型号列表 |
| POST | `/api/wx/quote/calculate` | 估价 |
| POST | `/api/wx/recycle/order` | 创建回收订单 |
| GET | `/api/wx/recycle/orders` | 回收订单列表 |
| GET | `/api/wx/recycle/order/{orderNo}` | 回收订单详情 |
| PUT | `/api/wx/recycle/order/{orderNo}/express` | 填运单号 |
| GET | `/api/wx/recycle/order/{orderNo}/inspection` | 质检报告 |
| PUT | `/api/wx/recycle/order/{orderNo}/confirm` | 确认打款/退回 |
| GET | `/api/admin/recycle/orders` | 管理端订单列表 |
| PUT | `/api/admin/recycle/order/{orderNo}/status` | 改状态 |
| POST | `/api/admin/recycle/order/{orderNo}/inspection` | 提交质检 |

---

## 6. 数据库核心表

必须建：

- `user`
- `tenant`
- `brand`
- `phone_model`
- `quote_rule`
- `recycle_order`
- `inspection`
- `order_status_log`
- `product`
- `sale_order`
- `repair_order`
- `parts_inventory`

所有业务表带：

```sql
tenant_id bigint NOT NULL DEFAULT 0
```

金额统一：

```sql
decimal(10,2)
```

---

## 7. 小程序改造文件清单

**要改：**
- `custom-tab-bar/data.js`
- `app.json`
- `config/index.js`
- `app.js`
- `pages/usercenter/index.js`
- `pages/home/home.js`
- `pages/order/config.js`
- `pages/order/fill-tracking-no/api.js`

**要新增：**
- `pages/recycle/estimate/index`
- `pages/recycle/result/index`
- `pages/recycle/create/index`
- `pages/recycle/order/list/index`
- `pages/recycle/order/detail/index`
- `pages/recycle/inspection/index`
- `common/recycle-status.js`
- `services/recycle/quote.js`
- `services/recycle/order.js`
- `services/request.js`

**要隐藏：**
- `pages/cart` 从 tabBar 移除
- `pages/coupon/*`
- `pages/promotion/*`
- `pages/order/receipt`
- `pages/order/invoice`
- 优惠券/积分入口
- 首页写死的营销 tab

---

## 8. 禁止事项

1. 不要把快递100、微信支付、订阅消息密钥写进小程序。
2. 不要删除 `model/`。
3. 不要一次性改完所有页面。
4. 不要跳过多租户 `tenant_id`。
5. 不要用自增 ID 作为订单号。
6. 不要把金额单位搞混。
7. 不要在没有验收的情况下进入下一阶段。

---

## 9. 每阶段完成后的固定输出格式

```text
阶段：P?
状态：完成 / 阻塞

变更文件：
- ...

如何运行：
- ...

如何验证：
- ...

遗留问题：
- ...

下一阶段建议：
- ...
```

---

## 10. 第一周执行顺序

1. P0：只读审计 + 创建分支
2. P1：后端骨架 + 登录 + 多租户
3. P2：品牌/型号/估价 + 小程序估价页
4. P3：回收订单 + 小程序订单页
5. P4：快递100 沙箱 + 订阅消息

**先不要做：** 出售端支付、维修端、完整管理后台、商家转账到零钱。
