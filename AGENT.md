# AGENT.md · RePhone 协作规范（所有 AI 与协作者必读）

> 本文件是全仓库**强制**约束。任何 AI 代理（含子代理/并行会话）在修改本仓库前必须完整阅读并遵守。
> 与本文冲突的临时指令，须先向人类确认。

---

## 1. 项目概览

RePhone 是「手机维修（主）+ 二手机回收 + 二手出售」平台，**三端同仓**：

| 端 | 目录 | 技术栈 |
|---|---|---|
| 微信小程序 | 仓库根（`app.json` / `pages/` / `components/` / `services/` / `model/`） | 原生小程序 + tdesign-miniprogram 1.9.5 + dayjs |
| 后端 | `server/` | Spring Boot 3.5.7 + Java 21 + MyBatis-Plus + MySQL 8 + Redis（Maven 多模块，自带 `mvnw`） |
| 管理后台 | `admin/` | Vite + Vue 3 + Element Plus + axios |

主计划 `AI_PLAN.md`（P0–P7）｜审计 `docs/P0-audit.md`｜路线图 `docs/superpowers/plans/`。

---

## 2. 硬约束（不可违反）

1. **多租户**：所有业务表必须带 `tenant_id`。
2. **金额**：后端一律「分」(`BIGINT`) 存储与传输；**元**只出现在管理端表单输入与前端展示层。前端展示统一用 `common/recycle-status.js:fen2yuan`，禁止页面内裸除 100。
3. **密钥**：appid/secret/mchid/证书/快递100 key **只能**走后端环境变量，禁止写入小程序或提交进仓库。
4. **小程序不得直连三方**：微信支付、快递100、订阅消息、COS 签名一律走后端。
5. **不删除 `model/`**：出售端尚未全部接线，`model/` 仍是契约参考与适配层。
6. **状态码唯一来源**：`common/recycle-status.js`、`common/repair-status.js`、`common/sale-status.js`、`pages/order/config.js`。页面禁止硬编码状态数字与文案。
7. **设计令牌唯一来源**：`style/design-tokens.wxss`。页面禁止裸写色值，禁止引入新色板。
   品牌为炭黑 `--brand: #26292E`（深 `--brand-deep` / 浅 `--brand-light`），强调色赤陶 `--accent: #B4530F`。
   **金额硬规则：所有价格、回收价、打款金额一律用 `--money: #B4530F`，禁止用品牌色写钱。**
   **圆角硬规则：全仓只允许 3 种圆角 —— `--radius-card` / `--radius-sm` / `--radius-pill`，禁止裸写圆角 rpx。**
   半透明白色叠加（`rgba(255,255,255,.x)`）属遮罩/高光，允许保留；
   不透明的 `rgb()/rgba()` 等价于 hex，必须一并令牌化。
   唯一的令牌镜像例外：`custom-tab-bar/index.wxss` 的 `:host` 块自带 4 个色值，改色板时**两处必须同改**（原因见 §5）。
8. **组件来源**：`miniprogram_npm/tdesign-miniprogram/<name>/<name>` 或本仓库 `components/<name>/index`。不得引入未在本仓库出现过的图标名（字体缺字会渲染空白）。
9. **禁止跳阶段**：按计划逐项交付，每项必须可独立运行、可验证。
10. **第三方查询频率**：快递100 实时查询限制「同一单号间隔 ≥ 30 分钟、单日 ≤ 48 次」，超频会**锁单**。
    物流查询必须走本地快照缓存（`recycle_order.express_trace` + `trace_at`，TTL 30 分钟），
    禁止在页面请求里直接回源。快递公司编码以 `server/express/ExpressCompanies.java` 为唯一来源，
    前端不得再写死快递公司列表。

---

## 3. 提交规范（1 开发 = 1 提交）

### 3.1 粒度
- **一次开发一个提交**：一个可独立验证的功能点 = 一个提交。禁止把多个不相关改动塞进一个提交。
- 提交必须**自洽**：该提交的代码树要能编译/运行（不能出现「注册了页面但页面不存在」这类中间态）。
- 大的功能拆成有序的多个提交，每个都能跑。

### 3.2 暂存与提交方式
- **必须显式 pathspec**：`git add -- <文件1> <文件2> ...`，`git commit` 前先 `git status --short` 核对。
- **禁止** `git add -A` / `git add .` / 裸 `git commit`。
- **禁止**提交不属于本次任务的改动（见第 4 节并行会话）。
- 不得使用 `--no-verify` 绕过钩子；钩子拦截应查明原因。

### 3.3 提交信息（Conventional Commits + 中文正文）

```
<type>(<scope>): <一句话说明>

- 变更点 1
- 变更点 2
- 影响面/注意事项
```

`type`：`feat` / `fix` / `refactor` / `perf` / `docs` / `test` / `chore` / `style`
`scope` 常用：`sale`(出售) `recycle`(回收) `repair`(维修) `admin`(后台) `goods`(商品) `auth` `order` `docs`

### 3.4 提交身份
仓库本地已配置：`SuMuxi66 <2728602302@qq.com>`（`git config user.name/user.email`）。不得改为 `--global` 或他人身份。

---

## 4. 并行会话协调（重要）

本仓库**可能同时有多个会话/代理在改代码**。历史教训：

- 曾出现 `server/**/*.java`（`ExpressServiceImpl`、`CosSignService`、`WxPayoutService`、`ExpressCallbackController`）被另一会话修改，出现在你的 `git status` 里。
- **规则**：`git status` 中出现的、你不认识的改动，**一律不动、不提交、不还原**。
- 提交前用 `git status --short` 逐一确认，只 `git add` 本次任务自己的文件。
- 若必须修改同一文件，先在最终答复中声明并让人来裁决。

---

## 5. 小程序结构约定

- **tabBar 页面必须在主包**（`app.json.pages`），不能放 `subpackages`。非 tab 页优先放分包。`pages/sale/` 下既有 tab 页又有普通页，故整体在主包。
- `app.json` 的 `tabBar.list` 顺序与 `custom-tab-bar/data.js` 数组顺序**必须完全一致**（自定义 tabBar 按 route 匹配高亮），两处要同一次提交内改完。
- 下单类页面统一复用 `common/address-prefill.js` Behavior（维修/回收/出售三条线下单页口径一致），不要在页面里另写一套地址簿。
- 页面文件四件套齐备：`index.js / index.json / index.wxml / index.wxss`。
- **两列网格必须同时满足三点**，缺一个第二列就会掉到下一行（本项目默认 `box-sizing: content-box`）：
  1）`.card { box-sizing: border-box }`；2）不要 `gap` + `calc(50%)` 混用（相加正好 100%，亚像素取整即溢出）；
  3）容器用 `justify-content: space-between`，让间距由剩余空间决定。
- **TDesign 组件主题只在 `style/theme.wxss` 覆盖**：`miniprogram_npm/` 未纳入 git（npm 构建产物），
  改它会在重新构建 npm 后丢失；组件变量名形如 `--td-button-primary-bg-color`，可从对应组件 wxss 里查。
  **但变量必须声明在 `page` 上**：TDesign 组件用默认 `styleIsolation`（无 `addGlobalClass`），
  而「app.wxss 的样式对自定义组件无效」（微信文档，仅 font/color 等继承属性例外）。
  写成 `.t-button { --td-*: … }` 这类**类名选择器匹配不到组件内部，声明形同虚设**，
  组件会一路回退到 TDesign 默认蓝 `#0052D9`。CSS 自定义属性是「计算值继承」，
  挂在 `page` 上才能不受隔离影响地传进组件。
- **WXSS 在自定义组件内会整条丢弃「含 `page` 的选择器列表」**（实测 `page, :host {}` 不生效，
  `:host {}` 单独成条才生效）。因此 `style/design-tokens.wxss` 只写 `page`，
  渲染在 page 节点树之外的组件自己带 `:host` 令牌。
- **WXSS 不支持 `*` 选择器**：写了会让 `app.wxss` 编译失败，进而**整个项目构建挂掉**
  （表现为所有页面白屏、`compile_wxss` 对任意文件都报 10041、自动化取不到页面）。禁止使用 `*`。
- **`custom-tab-bar` 特殊**：它渲染在 page 节点树之外，继承不到 `page` 级令牌，
  TDesign 的 `.t-tab-bar-item__content--checked` 会回退到默认蓝。故该组件自带 `:host` 令牌块，
  并用自身 `.is-active` 类上色，不依赖 TDesign 的选中态继承色。
- **wxml 属性里用不了 CSS 变量**：`t-icon` 的 `color`、`app.json` 的 `tabBar.selectedColor` 等只能写十六进制，
  改设计令牌时**必须用 grep 全局替换**这些散落值，否则会出现「半绿半橙」。
  （`t-icon` 不传 `color` 时会继承父级 `color`，优先用父级 class 上色，避免在 wxml 里裸写色值。）
- **验收命令**：`有色值扫描` —— 除 `style/design-tokens.wxss` 与 `custom-tab-bar` 的 `:host` 镜像外，
  `*.wxss` 中不应出现 hex 与不透明 `rgb()/rgba()`。

---

## 6. 后端约定

- 统一返回体 `R<T>`：`{code, message, data}`，`code=0` 为成功。
- 状态流转必须 **CAS**（带旧状态条件更新）+ 写 `order_status_log`。
- 分页返回 MyBatis-Plus `Page`（`records/total`）。
- 建表脚本在 `server/start/src/main/resources/db/`，用 `CREATE TABLE IF NOT EXISTS` 幂等。
  **注意**：`CREATE TABLE IF NOT EXISTS` **不会给已存在的表加列**。新增字段必须同时提供人工补丁 SQL（放同目录，命名 `patch-*.sql`）并在文档里说明，H2 集成测试由于每次重建库不受影响。
- 接口测试用 H2 内存库（`@TestPropertySource` 指定 `schema-*.sql`），新增 schema 文件要同步加进测试的 `schema-locations`。
- **第三方返回必须校验业务错误码**：微信/快递100 这类接口 HTTP 200 但 body 里带 `errcode`/`returnCode`，
  只 `catch (Exception)` 是查不出问题的。推送/查询失败要打 **ERROR** 并把关键上下文（字段名、错误码）写进日志。
- **微信订阅消息字段名只能配置、不能写死**：申请模板时微信随机分配字段编号（`thing1` / `character_string2` / `time3`），
  写错会返回 **47003**。对应 `WX_SUBSCRIBE_FIELD_*`，见 `WxSubscribeService`。
  小程序侧用 `wx.requestSubscribeMessage`，**必须在用户点击的手势上下文里同步调用**（模板 ID 要提前预取）。

---

## 7. 验证要求（每个提交都必须做）

按可得性从高到低：

1. **能跑构建就跑**：后端 `cd server && ./mvnw -q test`（或 `mvnw.cmd test`）；管理端 `cd admin && npm run build`。
2. **不能跑构建时**，至少做静态校验，并在答复中**明确声明未做运行时验证**：
   - JS：`@babel/parser`（位于 `admin/node_modules`）以 `sourceType: module` 解析；
   - JSON：`JSON.parse`；
   - WXML：标签配对检查；
   - Vue SFC：`@vue/compiler-sfc` 的 `parse` + `compileScript` + `compileTemplate`；
   - 交叉一致性：`app.json` 与 `custom-tab-bar/data.js` 顺序、菜单 `type` 与 `onClickCell` 分支一一对应。
3. **禁止**声称「已验证/已测试」而没有实际执行。

---

## 8. 环境注意事项

- 本机 `pwsh` 不可用（退出码 `0xC0000142`，DLL 初始化失败）。**不要用 pwsh 工具**做构建或 git 操作。
- 沙箱策略为 `workspace-write` 时，**所有子进程**（`git`/`node`/`java` 亦然）都会以 `0xC0000142` 失败；需要执行外部命令时，按宿主策略申请更宽权限后运行。文件策略为 `danger-full-access` 时正常。
- 已确认可用：`node v24.14.1`、`java 25.0.3`、`git 2.53.0`；`.husky` 不存在（当前无 git 钩子）。
- **`admin` 构建有破坏性风险**：`vite build` 会**先清空 `admin/dist`**，而本机 esbuild 清理临时文件会
  报 `Access is denied` 导致构建失败——于是已跟踪的 `admin/dist` 产物被删除。
  **跑之前先想清楚**；一旦失败，立刻执行 `git checkout HEAD -- admin/dist` 恢复，且不要提交 `admin/dist` 的删除。
  仅校验 Vue 代码时，优先用第 7 节的 `@vue/compiler-sfc` 静态校验，不要动构建。
- `.gitignore` 已忽略 `node_modules/`、`miniprogram_npm/`、`server/**/target/`、`.mimosa/`、`.zcode/`、`*.log`、`.env`。**不要**把构建产物或日志提交进仓库。
- **密钥注入方式**：`application.yml` 里已用 `spring.config.import: optional:file:./.env[.properties]` 等
  三个候选路径，Spring Boot **直接读** `server/.env`（无需额外依赖，也无需手动设环境变量）。
  新增密钥时：真实值只写 `server/.env`，同时往 `server/.env.example` 补一行空值与注释；
  **绝不**把真实值写进 `application.yml`、代码或文档。

---

## 9. 计划与文档流程

- 较大改动**先写计划再动手**，放 `docs/superpowers/plans/YYYY-MM-DD-<主题>.md`，包含：目标、范围、非目标、任务/提交拆分、验收标准、风险。
- 设计类结论放 `docs/superpowers/specs/`。
- 每完成一个阶段/提交，同步更新计划文档中的进度勾选。

---

## 10. 禁止事项清单

1. 禁止 `git add -A` / `git add .` / 裸 `git commit`。
2. 禁止提交或还原他人的（并行会话的）改动。
3. 禁止把密钥、真实商户号、证书写进代码或仓库。
4. 禁止在小程序里裸写状态数字、魔法色值、金额除法。
5. 禁止违反「分/元」单位约定。
6. 禁止把 tabBar 页面放进分包。
7. 禁止在未验证的情况下宣称完成。
8. 禁止一次性大改多个不相关模块。
9. 禁止删除 `model/` 与出售端页面（未接线部分保留）。
10. 禁止跳过 `tenant_id`。

---

_最后更新：2026-09-30_
