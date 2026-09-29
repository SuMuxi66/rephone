# RePhone 平台开发路线图（2026-09-29 基于实际进度更新）

> 配套详细实施计划：`docs/superpowers/plans/2026-09-29-p5-admin-console.md`（P5 逐任务代码级计划）。
> P6/P7 到达启动时，按 superpowers:writing-plans 技能各出一份同格式详设。

## 一、当前进度快照（分支 feature/recycle-mvp）

| 阶段 | 内容 | 状态 | 证据 |
|---|---|---|---|
| P0 | 审计 + git 分支 | ✅ 已提交 | 3c8e400, b03c077, docs/P0-audit.md |
| P1 | 后端骨架 + wx.login + JWT + 多租户 | ✅ 已提交 | 96b3368；mvnw test；真实 MySQL 验证 |
| P2 | 品牌/机型/估价三表 + 估价 API + 估价页 + 入口改造 | ✅ 已提交 | 5bf0d9a；报价 566000 分精确命中 |
| P3 | 回收订单闭环（订单/质检表、下单/查询/填单/取消/确认） | ✅ 代码完成并双端验证，**待提交** | mvnw test 8/8；真实环境 10→20→30→40→50 全通过 |
| P4 | 快递100+回调 / COS 签名 / 订阅消息 / 企业付款(mock) | ✅ 同上，与 P3 同批 | 同上；揽收回调推进、COS mock 签名、打款 50 |
| P5 | 极简管理后台 | 📋 详设就绪（见配套计划） | — |
| P6 | 出售端接后端 + 微信支付 JSAPI | ⬜ 待详设 | — |
| P7 | 维修端 + 完整管理端（租户/RBAC/财务） | ⬜ 待详设 | — |

**当前阻塞（2 项，等用户动作）：**
1. **P3/P4 提交被 Mimosa 钩子拦截**：5 处"弱加密算法"均为第三方协议强制（COS=SHA1、微信支付v1=MD5、快递100=MD5，代码已注释依据）。已决定由用户在 Mimosa 面板加白名单，之后执行：
   `git commit`（变更已全部 staged，提交信息已拟好，见 P5 计划 Task 0）
2. **开发者工具编译缓存**：估价页渲染异常需"清缓存后编译"验证（P2 遗留，P3 新页面同理）。

## 二、阶段准入与验收标准

### P5 极简管理后台（下一交付物）
- 准入：P3/P4 完成提交（后台 4 个 admin 接口已就绪：GET orders / PUT status / POST inspection / POST payout）
- 产出：`admin/` Vite+Vue3+Element Plus 应用，登录（ADMIN_TOKEN）→ 订单列表（筛选/分页）→ 详情抽屉（按状态出按钮：开始质检/提交质检/触发打款/标记完成）→ 质检表单（元转分 + COS 照片上传）
- 验收：运营用浏览器完成 待寄出→…→已完成 全流程推进，与小程序端状态实时一致

### P6 出售端复用 + 微信支付
- 准入：P5 上线运营；微信支付商户号开通（JSAPI）
- 产出：`product/sale_order/payment_record` 三表；模板出售端 7 页接后端（按 mock.md 适配层方案，`useMock` 按 envVersion 切换）；下单 → JSAPI 统一下单 → `wx.requestPayment` → 支付回调验签改状态
- 验收：真实/沙箱支付一分钱跑通，回调幂等，出售订单与回收订单互不影响

### P7 维修端 + 完整管理端
- 准入：P6 支付链路稳定；维修业务规则明确（工单类型/配件库/报价流程）
- 产出：`repair_order/repair_item/parts_inventory`；维修工单状态机（待接单→检测中→待报价→维修中→待取件→已完成）；管理端升级：租户管理、RBAC（替换 ADMIN_TOKEN 简易鉴权）、财务对账、数据看板
- 验收：创建子租户、分配权限、工单流转、对账单与订单流水一致

## 三、真实化凭据清单（配置到位即切真实模式，代码零改动）

| 用途 | 环境变量 | 当前状态 |
|---|---|---|
| 微信小程序 | WX_APPID / WX_SECRET | 演示 appid，需正式小程序主体（二手数码类目资质） |
| 订阅消息 | WX_SUBSCRIBE_TEMPLATE_ID | 待小程序后台申请"订单状态变更"模板 |
| JWT | JWT_SECRET | 生产必填（≥32 字节），缺失且非 mock 时拒绝启动 |
| 快递100 | EXPRESS_KEY / EXPRESS_CUSTOMER / EXPRESS_CALLBACK_URL | 待企业版开通；EXPRESS_MOCK=false 切真实 |
| 腾讯云 COS | COS_SECRET_ID / COS_SECRET_KEY / COS_BUCKET / COS_REGION | 待开桶；COS_MOCK=false 切真实 |
| 微信支付/打款 | WXPAY_MCH_ID / WXPAY_MCH_KEY / WXPAY_CERT_PATH | 待商户开通+证书；WXPAY_MOCK=false 切真实 |
| 管理端 | ADMIN_TOKEN | 生产必改强随机 |
| 数据库 | MYSQL_URL/USERNAME/PASSWORD、REDIS_HOST/PORT | docker compose 已就绪 |

## 四、横切事项（不随阶段自动完成，需排期）

1. **合规**：小程序隐私保护指引声明（手机号/位置）；二手回收类目资质；用户协议。
2. **部署**：Docker 化后端镜像 + Nginx(HTTPS) + 域名备案 + 小程序服务器域名配置（P5 上线前）。
3. **数据**：机型库扩充流程（管理端维护界面，P5 顺带）；估价规则调价。
4. **质量**：接口测试随阶段同步（现有 H2 集成测试模式延续）；上线前 Mimosa 全量扫描清零误报。

## 五、执行顺序（依赖驱动）

```
[用户] Mimosa 白名单 → 提交 P3/P4（Task 0）
   └→ P5 管理后台（详设就绪，可直接执行）
        └→ P6 出售端+支付（启动时出详设；商户开通为长周期项，建议提前申请）
             └→ P7 维修端+总管理端（启动时出详设）
横切：合规声明与部署配置与 P5/P6 并行推进
```
