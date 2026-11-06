# RePhone v1.0.2 全量测试报告

> **修复轮次更新（2026-10-15）**：本报告 §6 问题清单已有处置结果——
> **P1 已修复**（`4b98db9 fix(sale)`：出售支付切 `PaymentService` 统一入口，未配置商户凭据时 fail-closed 抛 50012；mock 闭环需显式 `SPRING_PROFILES_ACTIVE=dev` + `WXPAY_MOCK=true`，已在本地 dev 环境端到端验证：支付单 `P20261015142040871270` 入库 status=1、订单 10→20、状态日志正确，`P6SaleOrderTest` 断言同步更新，109/109 全绿）；
> **P3 已修复**（`1237da0 fix(recycle)` + `608a1b1 fix(admin)`：回收列表/质检时间与管理端三处列表列统一格式化，小程序端截图目检通过）；
> **P4 部分修复**（`0cab625 chore`：chalk 已声明并安装 4.1.2、`getSystemInfoSync` 已迁移 2 处——复测 console 弃用告警消失；但 `npm run check` 确认**设计性不可用**：脚本依赖 shell 管道 `grep`（Windows 无）且仓库无 `.eslintrc` 配置，是否补配置属项目决策，脚本未改动）；
> **P2 已定位+有出码通道**：esbuild 服务进程在 Windows 上自复制到 `%TEMP%\esbuild-<随机>`、退出时删除该目录被拒（Access is denied）。经对照实验（Defender 排除项生效后仍失败 / 清空残留进程与目录后仍失败 / esbuild 升 0.25.12 仍失败 / TEMP 换盘符仍失败 / `fltmc` 无第三方杀软驱动），排除杀软与 ACL，**判定为本机沙箱驱动拦截删除**（TEMP ACL 中可见 `CodexSandboxUsers` 等沙箱账户）。**代码与工具链无恙**：Docker Linux 构建一次通过（4.26s，dist 完整产出 33 个资源）。本地出码命令：`docker run --rm -v "%cd%:/app" -v rephone_admin_nm:/app/node_modules -w /app node:20-alpine sh -c "npm ci --no-audit --no-fund && npm run build"`；根治需排查本机删除拦截驱动（可先试禁用 UCPD 驱动并重启，`sc config UCPD start= disabled`，社区有同症状案例但未确证）。
> 修复计划见 `docs/superpowers/plans/2026-10-15-v1.0.2-qa-fixes.md`。

| 项 | 值 |
|---|---|
| 测试时间 | 2026-10-14 16:00 – 17:10（本地时区 UTC+8） |
| 分支 / 提交 | `feature/recycle-mvp` @ `2345ebc`（与 `origin/main` 同步） |
| 工作区状态 | 测试开始时 `nothing to commit, working tree clean`；测试结束后仅新增 `docs/testing/`（本报告与截图），**未修改任何业务代码** |
| 测试人 | ZCode（QA 代理），按任务约束执行：不修码、不提交、不推送、不触真实资金 |

---

## 1. 环境检查结果（阶段一）

| 检查项 | 结果 |
|---|---|
| `git status` / 分支 | ✅ 干净；分支 `feature/recycle-mvp`，HEAD `2345ebc`（feat(admin): 账号页支持商家角色） |
| AGENT.md / README.md / README-production.md | ✅ 已完整阅读；AGENT.md §7「验证要求」、§8「环境注意事项」作为本轮测试依据 |
| 根 `package.json`（小程序） | 版本 **1.0.2**；`npm test` 为占位命令（`echo Error: no test specified && exit 1`），已按提示改为检查真实可用脚本；可用脚本：`check`（eslintCheck）、`lint`（**带 --fix，本轮禁用**）、`prune-npm` |
| `miniprogram_npm/`（不入库构建产物） | 递归 303 个 .js、83 个组件目录 → 裁剪后的完整构建，无需重建 |
| 根 `node_modules/` | 仅 4 项（dayjs / tdesign-miniprogram / tslib + lockfile），devDependencies 未安装 → `npm run check` 不可运行（见问题 4） |
| `project.config.json` | appid `wx180a891a1c015501`（用户提供的测试号），`urlCheck:false`，`libVersion 3.13.0`（IDE 实际加载 3.17.3） |
| `server/`（Maven 多模块） | 8 模块（common/pojo/mapper/wechat/express/service/controller/start）；测试类 21 个 + 配置/控制器子包 |
| `server/.env`（仅核对键名与布尔开关，**未读取、未输出任何密钥值**） | 键：MYSQL_ROOT_PASSWORD、EXPRESS_KEY、EXPRESS_CUSTOMER、EXPRESS_MOCK、ADMIN_TOKEN、WX_APPID、WX_SECRET、WX_MOCK_LOGIN、JWT_SECRET、ADMIN_BOOTSTRAP_PASSWORD；`WX_APPID` 与 project.config.json 一致；**无 WXPAY_*/COS_* 配置** |
| Docker 本地栈 | `rephone-server`（Up 25h，0.0.0.0:8080）、`rephone-mysql`（healthy）、`rephone-redis` 运行中；容器实际开关（`docker exec env` 核实）：`SPRING_PROFILES_ACTIVE=prod`、`WX_MOCK_LOGIN=false`、`EXPRESS_MOCK=false`、`WXPAY_MOCK=false`、`COS_MOCK=false` |
| 后端健康检查 | `GET /api/health` → `{"code":0,"message":"success","data":"pong"}` |
| 微信开发者工具 | wechatide 门禁通过（`versionRelation=equal`，`loginExpired=false`），fullMode 开窗，基础库 3.17.3 |
| 本机工具链 | Java 25.0.3、Node 24.14.1、Docker 29；Maven 走 `server/mvnw.cmd` |

---

## 2. 实际执行的命令及退出码（阶段二）

| 命令 | 退出码 | 结果摘要 |
|---|---|---|
| `git status` / `git branch --show-current` / `git log --oneline -5` | 0 | 干净工作区，分支与提交如上 |
| `node <QA脚本 check-miniprogram.mjs>`（自写只读校验，存 `%TEMP%\rephone-qa\`） | 0 | 主包 22 页 + 分包 4 页四件套齐全；233 个 JSON 全部可解析；169 处组件引用全部可解析；12 处 npm 导入全部可解析；tabBar 与 `custom-tab-bar/data.js` 顺序一致；WXML 标签配对通过。报出的 4 条 problem 经人工甄别**均为检查器误报**（`.zcode/`、`config/` 属 Node 工具脚本误扫；`</wxs>` 为自闭合判定缺陷） |
| `server: mvnw.cmd test` | 0 | **Tests run: 109, Failures: 0, Errors: 0, Skipped: 0；BUILD SUCCESS，Total 26.785s**（覆盖支付回调金额/签名负向用例、维修报价/支付/寄修、回收估价/质检/拒绝终价、RBAC/租户隔离、商家角色、MockGuard、登录防爆破、输入加固、财务看板等） |
| `admin: npm run build` | **1（3 次复现）** | 1695 个模块转译全部成功后，esbuild 清理临时目录阶段报 `Access is denied`，构建中断（原始日志见 §5） |
| `node <QA脚本 check-admin-vue.mjs>`（AGENT.md §7.2 认可的降级路径） | 0 | `@vue/compiler-sfc` parse + compileScript + compileTemplate：**16/16 个 .vue 全部通过**；router 15 个懒加载引用全部可解析 |
| `wechatide -c ZCode check_wechatide_status --skill-version 0.3.11` | 0 | 门禁通过 |
| `wechatide open_project_window / simulator_refresh / get_simulator_console / automation_* / simulator_screenshot` | 0 | 见 §3 |
| `docker exec rephone-server env`（只取 5 个布尔/枚举开关） | 0 | 见 §1 |
| `node <QA脚本 admin-login.mjs>`（从 server/.env 读密码换 JWT，凭据不进对话上下文） | 0 | 管理端登录成功（role=ADMIN） |

**结论说明**：根目录 `npm test`、`admin` 无 test/lint 脚本、后端无独立 lint——项目实际可执行的自动化测试面就是「Maven 测试 + Vite 构建 + 小程序 IDE 编译」，以上已全部覆盖。

---

## 3. 微信开发者工具编译与页面自动化（阶段三）

### 3.1 整包编译

- `simulator_refresh` 触发整包编译后，首页正常加载（pageStack 返回 `pages/home/home`）。
- 全程 console 抓取（`grep -iE 'error|exception|fail|deny'`）：**0 条错误**，仅 1 条 `wx.getSystemInfoSync is deprecated` 警告（见问题 5）。
- 判定：**编译通过，无白屏**。

### 3.2 页面覆盖与交互（21 张截图存于 `docs/testing/screenshots/`）

| # | 链路 / 页面 | 操作方式 | 结果 | 截图 |
|---|---|---|---|---|
| 1 | 首页 / 自定义 tabBar 五项 | IDE 编译后落地页 | ✅ 渲染完整，tab 高亮正确 | shot-home.png |
| 2 | 出售 Tab（pages/sale/index） | switchTab | ✅ 品牌/成色筛选、排序、两列网格 4 件在售 | shot-sale-index.png |
| 3 | 维修 Tab（pages/repair/index） | switchTab | ✅ 品牌/机型选择、服务承诺 | shot-repair-index.png |
| 4 | 回收 Tab（在线估价） | switchTab | ✅ 三步进度、12 品牌宫格（后端下发） | shot-recycle-estimate.png |
| 5 | 我的（pages/usercenter/index） | switchTab | ✅ **真实登录成功**（机友314214，code2Session 走通），订单计数正常 | shot-usercenter.png |
| 6 | **回收 E2E**：估价（品牌→机型→内存→成色→屏幕）→ 后端报价 ¥6800.00 → 下单 → 订单详情 | UI 点选 + callMethod 提交 | ✅ 建单 **R2026101416272005924114**；680000 分→6800.00 元换算正确 | shot-recycle-result.png / shot-recycle-create.png / shot-recycle-order-detail.png |
| 7 | **维修 E2E**：机型库选型（50 款）→ 勾选「换外屏+更换电池」→ 报价确认 ¥1198.00（699+499）→ 下单 → 详情 | UI + callMethod | ✅ 建单 **F2026101416341992478048**；**address-prefill 地址预填实测生效** | shot-repair-quote.png / shot-repair-order-detail.png |
| 8 | **出售 E2E**：商品列表→详情（¥2899.00）→确认单→下单→详情（待付款） | UI + callMethod | ✅ 建单 **S2026101416371865466783**；收货信息自动预填 | shot-sale-detail.png / shot-sale-confirm.png / shot-sale-order-detail.png |
| 9 | 出售「去支付」 | callMethod onPay | ⚠️ **未弹支付面板，订单直接 待付款→待发货**（见问题 1，本地无资金损失但属高危缺陷） | shot-sale-pay-error.png |
| 10 | 订单列表 ×3 + 分包地址簿页 | redirectTo | ✅ 三列表新单均在首条、状态筛选齐全；地址簿手机号脱敏（138\*\*\*\*8000），无白屏 | shot-recycle-order-list.png / shot-repair-order-list.png / shot-sale-order-list.png / shot-address-list.png |
| 11 | **跨端质检闭环**：小程序填运单号（顺丰 SF9999999999，状态→运输中）→ 管理端「推进到质检中」（确认框）→「提交质检结果」（结论+最终价 6500）→ 小程序同步显示「待确认 + 质检最终价 ¥6500.00」 | 两端联动 | ✅ 全链路通过；管理端质检表单含「最终价≤估价两倍」校验提示；**「触发打款」按钮按约束未点击** | shot-recycle-inspection.png |
| 12 | **拒绝最终价**：小程序「拒绝并退回」（mock wx.showModal 确认，已 restore） | callMethod + wx api mock | ✅ 状态 40 待确认 → **30 质检中**（退回重新质检） | shot-recycle-reject.png |

### 3.3 管理后台页面（Playwright 驱动 IAB，会话经 localStorage 注入，密码不进上下文）

| 页面 | 验证点 | 结果 |
|---|---|---|
| /dashboard 数据看板 | 指标卡、订单总量（16/9/12）、待处理、近 30 天资金、14 天趋势图、热门榜单；**今日单量 3 与本轮建的 3 张测试单吻合** | ✅（admin-dashboard.png） |
| /recycle 回收单管理 | 16 条、8 个状态 tab、测试单首行；详情抽屉字段完整；质检操作链路见 §3.2#11 | ✅ |
| /repair 维修单管理 | 12 条、8 个状态 tab、测试单 F2026…「待确认 1198.00」首行 | ✅ |
| /sale 售出管理 | 9 条、测试单「待发货」首行 | ✅ |
| /finance 财务对账 | 流水 12 条：出售收款 10262 / 维修收款 6191 / 回收打款 0 / 出售退款 9396 / 净额 7057；本轮出售收款 2899 已入账 | ✅ |
| /goods 商品管理 | 6 条、质检报告/编辑/上下架操作入口齐全 | ✅ |
| 菜单 | 13 个菜单项全部渲染（售后/机型/租户/角色/账号/地址/评价未逐一操作，见 §6） | ✅ |

> 测试过程备注：IAB 中 Element Plus 表格的定位器点击持续超时（快照可见、hit-test 正常、无遮罩、无 JS 对话框），改用「页面内 `element.click()` / CUA 坐标」完成交互；该现象属自动化工具通道问题，**不判定为产品缺陷**。

---

## 4. 测试状态总表

| 测试项 | 状态 | 依据 |
|---|---|---|
| 后端全量集成测试（109 个） | ✅ 通过 | mvnw test BUILD SUCCESS |
| 小程序静态完整性（路由/组件/npm/JSON/WXML） | ✅ 通过 | QA 脚本 + 误报甄别 |
| 小程序整包编译 | ✅ 通过 | IDE 编译 + console 零错误 + 截图 |
| 小程序三线 E2E（下单/详情/列表） | ✅ 通过 | 3 张真实订单 + 截图 |
| 回收质检→终价→拒绝退回（跨端） | ✅ 通过 | 状态机 10→20→30→40→30 |
| 出售「确认收货/售后」后续流 | ⛔ 未测 | 需先真实/模拟发货，本轮范围外 |
| 维修「管理端报价→用户确认→支付」 | ◐ 部分 | 前半（下单待确认）✅；报价/确认/支付未做页面操作（后端 RepairPaymentFlowTest 9 个用例 ✅ 覆盖） |
| 回收「确认打款→已完成」 | ⛔ 主动拦截 | 约束禁止真实打款；未配置商户证书也不可能出账 |
| 真实微信支付 / 快递100 真实下单 | ⛔ 拦截 | 约束 3/6；本地未配置 WXPAY/EXPRESS 出网 |
| admin `vite build` | ❌ 环境阻断 | 3 次复现失败（见 §5），源码静态校验 16/16 通过 |
| 上传体验版 / 预览二维码 | ⛔ 未执行 | 避免触碰发布链路（约束 4） |

---

## 5. 失败原始日志与初步归因

### 5.1 admin 构建（退出码 1，3 次复现，第 3 次使用全新 TMP 目录仍失败）

```
> rephone-admin@1.0.2 build
> vite build

vite v5.4.21 building for production...
transforming...
✓ 1695 modules transformed.
rendering chunks...
x Build failed in 8.93s
error during build:
[vite:esbuild-transpile] remove C:\Users\ADMINI~1\AppData\Local\Temp\esbuild-4f3dc879...: Access is denied.
    at E:\code-start\RePhone\admin\node_modules\esbuild\lib\main.js:737:38
    ...
```

**归因：环境问题，非代码缺陷。** 每次都在「转译成功之后的临时目录清理」阶段失败、且每次临时目录名不同（排除残留文件锁定）；1695 个模块转译成功说明源码本身无编译错误。与 AGENT.md §8 记载的历史现象一致（本机安全软件实时扫描锁住 esbuild 刚解出的服务目录）。影响：`admin/dist` 无法产出（当前 dist 为空目录；dist 未纳入 git，无版本库损失）。建议：为 `%TEMP%` 与 esbuild 服务进程加杀软白名单后重试；或升级 vite/esbuild 观察是否规避。

### 5.2 后端测试期间的 ERROR 日志（**预期输出，非失败**）

```
ERROR ... GlobalExceptionHandler : 未处理异常
ERROR ... WxPayCallbackController : [wxpay][callback] 金额不符 payNo=P-RP-T2 expect=25000 actual=100
ERROR ... WxPayCallbackController : [wxpay][callback] 签名验证失败 out_trade_no=P-RP-T4
```

以上出自 RepairPaymentFlowTest / InputHardeningTest 的**负向用例**（验证异常处理与回调防伪），对应测试全部通过。

---

## 6. 问题清单（按严重程度排列）

| 级别 | 问题 | 位置 / 证据 | 影响 |
|---|---|---|---|
| **P1（条件性资金风险）** | 出售订单支付为**硬编码模拟闭环**：`pay()` 无条件生成 `MOCKPAY-*` 流水号并 CAS 到已支付，**无任何 mock 开关或配置判断**；`MockGuardConfig` 管不到它。本轮实测点击「去支付」即 待付款→待发货（本地无真实资金，但生产配置商户号后该接口仍会 0 元放单） | `server/service/src/main/java/com/rephone/service/SaleOrderService.java:100-113`（注释自述「真实微信支付联调时替换其内部实现」）；对照安全实现 `PaymentService.start()` → `WxPayClient`（未配置会抛 50012）。后端 P6SaleOrderTest 依赖 `MOCKPAY-` 格式，测试固化了该行为 | 若出售线上线收款 = 用户可 0 元下单（届时应升 P0）；当前开发阶段为设计中的临时路径 |
| **P2（环境阻断）** | admin `vite build` 本机稳定失败（esbuild 清理临时目录 Access is denied） | §5.1 原始日志，3 次复现 | 无法产出管理端部署物；CI/他人环境需另行验证 |
| **P3（UI 一致性）** | 回收单创建时间显示 ISO `T` 格式（`2026-10-14T16:27:20`）；维修/出售列表均已格式化（`2026-10-14 16:34`） | 小程序 `pages/recycle/order/list`（shot-recycle-order-list.png）+ 管理端 `admin/src/views/RecycleOrders.vue` 列表 | 观感瑕疵，两端同源（后端原样返回 `LocalDateTime.toString()`） |
| **P4（依赖健康）** | 根 `devDependencies` 未安装（本机 node_modules 仅 4 项），`npm run check` 不可运行；且 `config/eslintCheck.js` 依赖的 `chalk` 未在 package.json 声明 | §1 环境检查 | 静态检查工具链不可用（与构建无关，不影响小程序运行） |
| **P4（API 弃用）** | 小程序启动告警 `wx.getSystemInfoSync is deprecated` | IDE console（§3.1） | 未来基础库移除风险，建议迁移 `wx.getWindowInfo/getAppBaseInfo` 等 |
| **观察项** | 管理端回收单「触发打款」按钮在待确认状态即可用（本轮按约束未点击，打款全链路未验证） | shot 证据 §3.2#11 | 需在受控环境（WXPAY mock/沙箱）单独验证 |
| **观察项** | 本地运行容器为 `prod` profile + 全 mock false，但无支付/快递/COS 配置；小程序真实登录（code2Session）正常 | §1 | 与 README-production「未配置保持 mock 闭环」的设计相符，但叠加问题 1 后，出售线支付行为与预期不符 |

---

## 7. 尚未测试的项目与所需环境

1. **真实支付/打款/退款全链路**：需要微信支付商户号（含证书）+ 公网 https 回调，或显式 `WXPAY_MOCK=true` 的受控 dev 环境；回收打款另需企业付款到零钱签约。
2. **快递100 真实轨迹**：需要 `EXPRESS_MOCK=false` + 有效 key + 公网回调（当前容器即此配置，但本轮未触发真实下单以防消耗配额/锁单——AGENT.md §2.10 频率限制）。
3. **回收「确认打款→已完成」与维修报价确认/支付的页面操作**：依赖上述受控支付环境。
4. **出售发货/收货/售后**、售后管理、租户/角色/账号/地址/评价管理页的**操作级**测试（本轮仅做渲染级轮巡）。
5. **admin 生产构建**：需先解决 §5.1 环境问题。
6. **iOS 真机 / 正式 AppID / 上传提审**：不在本轮范围。
7. `npm run check`（eslint 静态检查）：需 `npm install` 安装 devDependencies 后方可运行。

---

## 8. 建议修复顺序

1. **P1 出售模拟支付**（SaleOrderService.pay）：切换到 `PaymentService.start()` 统一支付入口（或至少加「未配置真实支付即拒绝/开关守卫」），同步改造 P6SaleOrderTest 的期望；在合并/上线出售收款前必须完成。
2. **P2 admin 构建环境**：杀软白名单或 esbuild 升级验证，恢复 `npm run build` 产出能力。
3. **P3 回收时间格式化**：小程序回收列表与管理端 RecycleOrders 列表统一走既有的时间格式化（对齐维修/出售列表）。
4. **P4 依赖健康**：`npm install` 补齐 devDependencies；`chalk` 补进 package.json（或改用无依赖实现）。
5. **P4 getSystemInfoSync 迁移**。
6. 在受控 mock 支付环境补测：回收打款、维修报价确认→支付、出售发货/售后。

---

## 9. 可复核性说明

- 所有 QA 临时脚本位于 `%TEMP%\rephone-qa\`（check-miniprogram.mjs / check-admin-vue.mjs / admin-login.mjs 等），均为只读或仅访问本地服务。
- 截图 21 张：`docs/testing/screenshots/`（文件名与 §3.2 对应）。
- 三张测试订单留在本地 Docker MySQL（开发库）：回收 `R2026101416272005924114`（终态：质检中/已拒绝终价）、维修 `F2026101416341992478048`（待确认）、出售 `S2026101416371865466783`（待发货——由问题 1 的模拟支付产生）。
- 运行中的本地服务：Docker 栈（未动）、admin dev server（`npm run dev`，后台任务，端口 5173）、微信开发者工具项目窗口（已开）。如需回收请自行关闭。
- 本轮**未修改任何业务代码、未提交、未推送**；工作区新增仅 `docs/testing/`。
