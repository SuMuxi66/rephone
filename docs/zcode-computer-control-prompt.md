# 给 zcode 的操控提示词：RePhone 运行时验证与收尾

> 用法：把本文件**全文**作为提示词发给 zcode，或发一句「读取 `docs/zcode-computer-control-prompt.md` 并严格执行」。
> 本文件里的环境事实**都是实测过的**，直接采信；不要重新侦察，也不要凭常识假设（微信开发者工具**不在**默认安装路径）。

---

## 0. 任务

在这台机器上**真的把小程序的模拟器和后台跑起来**，把「只能靠人眼确认」的部分变成可复现的证据：

1. 先修掉第 2 节的阻塞项 —— 不修，你看到的一切报错都是假的；
2. 用微信开发者工具编译并逐页验证第 4 节清单；
3. 顺手完成第 6 节队列里的两个小任务；
4. 按第 8 节格式交付。

**总原则：能出网就出网验证，能断言就不要靠眼睛，能截图就不要口头描述。**
mock 通过 ≠ 真的能用。

---

## 1. 环境事实（已实测，直接用）

| 项 | 值 |
|---|---|
| 仓库 | `E:\code-start\RePhone` |
| 当前分支 | `feature/recycle-mvp` |
| 小程序 appid | `wx4a895c2ba165483b` |
| 库版本 | `libVersion 3.17.3`（`project.private.config.json`）、`3.13.0`（`project.config.json`） |
| **微信开发者工具** | `E:\weixinkaifagongju\微信开发者工具.exe` ← **注意不在 Program Files** |
| **开发者工具 CLI** | `E:\weixinkaifagongju\cli.bat`（已确认存在，是官方 `resources\app.asar.unpacked\js\common\cli\index.js` 的包装） |
| 开发者工具当前状态 | **已在运行**（29 个进程） |
| 后端 | `http://localhost:8080`（Docker 容器 `rephone-server`） |
| MySQL | `localhost:3306`（容器 `rephone-mysql`，healthy） |
| Redis | `localhost:6379` |
| 小程序 develop 的 apiBaseUrl | `http://localhost:8080`（`config/index.js` 的 `ENV_MAP.develop`） |
| 域名校验 | `project.private.config.json` 里 `urlCheck: false`，**不需要**配白名单 |
| `miniprogram_npm` | **已构建**（`tdesign-miniprogram` 84 项 + `dayjs` + `tslib`），**不需要**重新构建 npm |
| 后端登录 | `WX_MOCK_LOGIN=true`，模拟器的 `wx.login` code 会被直接接受 |
| 工作树 | 干净（HEAD = `58175da`） |

其它工具：`docker 29.4.2`、`node v24.14.1`、`java 25.0.3`、`git 2.53.0`。
无头 Chrome：`C:\Program Files\Google\Chrome\Application\chrome.exe`。

---

## 2. 三个阻塞项：**均已修复**（2026-10-01 运行时验证完成）

> **本节已过时，保留仅作历史记录。** 三个阻塞项都已经处理完，运行时验证已经跑通，
> 结论见文末「附录：运行时验证结果」。新会话**不要**重复修这三项。

### 修复情况

| 项 | 状态 | 修复方式 |
|---|---|---|
| B1 缺列 → 回收单详情 500 | ✅ 已修 | 执行了 `patch-express-trace.sql`；验证：无归属单 500→403(40302)、归属单返回 `code:0` |
| B2 容器跑在 mock 模式 | ✅ 已修 | `6ca59ab` compose 透传 `EXPRESS_*`；容器内已有真实 key，`EXPRESS_MOCK=false` |
| B3 容器代码旧于 HEAD | ✅ 已修 | 重建容器（含当时全部 HEAD 代码）；`pickupFailReason` 在真实出网下已验证正确回传 |

### 以下为原始记录（历史）

### B1（最严重）`recycle_order` 少了 3 个列 → 回收单详情现在就是 500

**现象（已复现）**：

```
GET http://localhost:8080/api/wx/recycle/order/R2026093019494015825194
→ {"code":500,"message":"服务繁忙，请稍后重试"}
```

**原因**：`recycle_order` 表有 57 行数据，但**没有** `express_com` / `express_trace` / `trace_at` 三列；
MyBatis-Plus 会把实体所有字段拼进 SELECT → `Unknown column 'express_com' in 'field list'`。

**为什么列表接口看起来是好的**：`GET /api/wx/recycle/orders` 返回 `total: 0`，
MyBatis-Plus 分页在 count 为 0 时会**跳过**取数 SQL，所以侥幸没炸。**别被它骗了。**

**修法**（二选一，执行前先 `docker exec rephone-mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" ...'` 确认现状）：

```bash
# 方式一：在容器里直接执行补丁
docker exec -i rephone-mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" rephone' < server/start/src/main/resources/db/patch-express-trace.sql
```

**验证**：重放上面那条 GET，必须变成 `code:403`（无权查看，因为该单不属于当前用户）而**不是** 500。
`403` 才是对的 —— 它证明 SQL 通过了、权限校验生效了。

### B2 运行中的后端跑在 **mock 模式**

容器里 `printenv` 显示只设了 `MYSQL_*` 和 `WX_MOCK_LOGIN`，**没有 `EXPRESS_*`**。
→ `EXPRESS_MOCK` 取默认值 `true`，`EXPRESS_KEY/CUSTOMER` 为空。

**含义**：物流轨迹会返回 mock 数据（"mock 轨迹：快件已由快递员揽收"），上门取件会**假装预约成功**。
如果要验真实快递100，得先把 `server/.env` 里的 `EXPRESS_*` 注进容器（`docker-compose.yml` 已经改了，但要看它有没有读 `server/.env`）。

**不要**为了这个去改代码逻辑；先判断当前任务是「验 UI」还是「验真实出网」，分开做。

### B3 容器里的代码比 HEAD 旧

容器创建于 `2026-10-01T05:53:01Z`（= 13:53 CST），而 HEAD `58175da` 是 `13:59:27`。
至少这三笔**不在**容器里：

- `b50c244` 快递100 抽独立客户端（响应解析 bug 修复）
- `e7c703d` 上门取件失败回传原因（`pickupBooked` / `pickupFailReason`）
- `58175da` 前端上门取件降级弹窗

**你要验 `pickupFailReason` 就必须先重建后端容器**，否则字段永远是 `undefined`。
重建前先 `git status` 确认工作树，重建命令参考 `server/docker-compose.yml`。

---

## 3. 怎么操控微信开发者工具

### 3.0 首选中的首选：`wechatide` 官方 CLI（已实测跑通，强烈建议）

本机已安装 `wechatide-skill`（`~/.dsh/skills/wechatide-skill`），配官方 `wechatide` CLI。
**下面的 3.1 / 3.2 都是它不可用时的兜底**，别再手搓 `cli.bat` 或自己装 automator。

前置（只做一次，之后复用）：
```bash
wechatide -c DSH check_wechatide_status --skill-version 0.3.11   # versionRelation 需为 equal
```
首次会返回 `pending + taskId`（需在开发者工具里点授权），按 skill 的异步规则 10 秒轮询一次、最多 10 次：
```bash
wechatide -c DSH polling_task_result --task-id <taskId>
```

实测跑通的工作流（**注意：每次交互前先确认当前页**）：

```bash
# 编译并打开某页（--query 传页面参数）
wechatide -c DSH simulator_open_page --project "E:\code-start\RePhone" \
  --page pages/recycle/order/detail/index --query "orderNo=RTEST-FILL-001"

# 确认当前页（否则会在错误的页面上操作，报 is not a function）
wechatide -c DSH automation_runtime_info --project "..." --action currentPage

# 截图（optimize 默认 true → 写的是 JPEG，文件名要用 .jpg）
wechatide -c DSH simulator_screenshot --project "..." --path "E:\code-start\.shots\x.jpg" --wait 2

# 读 console / network（command 必须是 grep）
wechatide -c DSH get_simulator_console --project "..." --command "grep -i error"
wechatide -c DSH get_simulator_network --project "..." --command "grep -n ."

# 读写页面 data / 调用页面方法（TDesign 自定义组件用 selector 选不中，用这个）
wechatide -c DSH automation_page_action --project "..." --action getData --data-path "form"
wechatide -c DSH automation_page_action --project "..." --action callMethod --method "showExpressPicker"

# 普通 view 元素可以按 selector 点
wechatide -c DSH automation_element_action --project "..." --selector ".cell" --action tap

# 页面滚动
wechatide -c DSH automation_viewport_action --project "..." --action pageScrollTo --scroll-top 6000
```

**两个实测踩过的坑**：
1. `t-button` / `t-cell` 这类 TDesign 自定义组件 `querySelectorAll` **选不中**（返回空数组），
   要用 `automation_page_action --action callMethod` 直接调页面方法，或改用普通 `view` 的选择器。
2. 用 `Select-String '"success"'` 过滤输出会**把 `"success": false` 也匹配上**（子串重叠），
   必须看完整 JSON，或用 `"success": true` 精确匹配。踩过一次，误判成「通过」。

### 3.1 兜底：官方 CLI 把项目跑起来

**前置条件（常见卡点）**：IDE 里必须打开
**设置 → 安全设置 → 服务端口**。不开，所有 CLI 命令都会报 `Please enable service port`。

```bat
:: 先确认登录态（未登录会返回 false，需要你在 IDE 里扫码）
"E:\weixinkaifagongju\cli.bat" islogin

:: 打开项目（已经在 IDE 里打开过就跳过）
"E:\weixinkaifagongju\cli.bat" open --project "E:\code-start\RePhone"

:: 关闭项目 / 退出 IDE（收尾时用，别把用户正在用的窗口关掉）
"E:\weixinkaifagongju\cli.bat" close --project "E:\code-start\RePhone"
```

**注意**：`cli.bat` 内部会自己 `chcp 65001`，输出是 UTF-8；在 PowerShell 里可能乱码，
用 `cmd /c` 调用或把输出重定向到文件再读，别因为乱码就以为命令失败了。

**不要**执行 `upload`（会上传体验版），**不要**执行 `--auto-preview` 除非你确实要生成预览二维码。

### 3.2 最有用：`miniprogram-automator`（能断言 + 能截图）

这是官方自动化 SDK，能拿到页面实例、查元素、触发事件、截图 —— 这才是「操控」的正确姿势。

**装到仓库外面**，别污染 `package.json`：

```bash
mkdir -p E:/code-start/.agent-tools && cd E:/code-start/.agent-tools
npm init -y
npm i miniprogram-automator
```

脚本模板（存 `E:/code-start/.agent-tools/verify.js`）：

```js
const automator = require('miniprogram-automator');

(async () => {
  const mp = await automator.launch({
    cliPath: 'E:/weixinkaifagongju/cli.bat',
    projectPath: 'E:/code-start/RePhone',
  });

  // 逐页验证：路径 + 断言
  const checks = [
    { path: '/pages/recycle/order/detail/index?orderNo=<真实单号>', sel: '.status-text' },
    { path: '/pages/recycle/order/trace/index?orderNo=<真实单号>',  sel: '.head-state' },
    { path: '/pages/model-picker/index?biz=repair',                  sel: '.brand-list' },
  ];

  for (const c of checks) {
    const page = await mp.reLaunch(c.path);
    await page.waitFor(1000);
    const el = await page.$(c.sel);
    const text = el ? (await el.text()).trim() : '(元素不存在)';
    console.log(JSON.stringify({ path: c.path, sel: c.sel, text }));

    // 页面级报错也要抓：这是判断「编译通过但白屏」的关键
    const data = await page.data();
    if (data && data.loading === true) console.log('  ⚠️ loading 卡住');
    await page.screenshot({ path: `E:/code-start/.shots/${c.path.replace(/[^a-z0-9]/gi, '_')}.png` });
  }

  await mp.close();
})().catch((e) => { console.error('FAILED', e); process.exit(1); });
```

```bash
node E:/code-start/.agent-tools/verify.js
```

**先自己读一遍 `miniprogram-automator` 的 README 确认 API 名称**（`reLaunch` / `$` / `data` / `screenshot` 在不同版本上有差异），
别照抄上面的模板就当成能跑。**跑通之后，截图就是最硬的证据。**

截图统一放 `E:/code-start/.shots/`（**仓库外**，别提交）。

### 3.3 兜底：纯 GUI

前两条都不通时，用「系统截图 + 人工看」：
截全屏（`Add-Type` 或 `nircmd` 之类的现成工具），逐页点击，把截图给用户看。
这一档的证据强度最低，交付时必须**明确说明是人工目视**。

### 3.4 「编译通过」的判定标准

不要只看 IDE 里有没有红字，要同时满足：

1. CLI/IDE 编译无 error（warning 可接受，但要抄下来）；
2. 目标页面**没有白屏**：`.status-text` / `.head-state` 这类首屏元素能取到文本；
3. 模拟器 Console 里**没有** `Failed to load` / `Component is not found` / `Cannot read property`；
4. 网络面板里 `http://localhost:8080/api/...` 返回 `code:0`（或预期的业务错误码，且**不是 500**）。

---

## 4. 必验页面清单

这些页面本轮刚改过、**只在静态层面检查过，从未在运行时跑过**：

| # | 路径 | 前置 | 期望 |
|---|---|---|---|
| 1 | `/pages/recycle/order/detail/index?orderNo=<真实单号>` | **先修 B1**，单号可从 DB 取 | 状态卡显示状态文案；若 `pickupType=20` 且无 `expressTaskNo`，显示琥珀色兜底提示；运单号行**可点**且带箭头 |
| 2 | 同上 → 点运单号 | 该单有 `expressNo` | 跳转 `/pages/recycle/order/trace/index` |
| 3 | `/pages/recycle/order/trace/index?orderNo=<有运单号且有轨迹的单>` | 无 | 绿色头部卡显示「快递公司 · 单号」和状态名；时间轴最新节点加粗；「复制单号」可用；不白屏 |
| 4 | 同上，但订单**没有**运单号 | 无 | 显示「该订单还没有运单号」，**不是**空白页、**不是** 500 |
| 5 | `/pages/recycle/create/index` → 选「上门取件」→ 提交 | **先重建容器（B3）** | 若后端回 `pickupFailReason`，弹出「上门取件未能预约」对话框，按钮是「去自助寄出 / 稍后处理」 |
| 6 | 同上选「自行寄出」→ 提交 | 无 | 直接进订单详情，**不弹**降级对话框 |
| 7 | 订单详情 → 「填写运单号」 | 无 | 快递公司是**选择器**（不是文本框）；选项 ≥22 家；选「顺丰速运」后出现琥珀提示（需手机号） |
| 8 | 型号：`/pages/model-picker/index?biz=repair` | 无 | 左侧品牌栏有绿色指示条；右侧两列机型图；顶部搜索可过滤；点机型返回并回填 |
| 9 | 维修 tab：`/pages/repair/index` | 无 | 点「品牌」或「机型」都进机型库；选完自动出维修项目与价格 |
| 10 | 首页 `/pages/home/home` | 无 | **能竖向滚动到底**（这修过一版）；「哪里坏了」4 块两列不乱；价格用琥珀色 |

**特别注意第 7、8 条**：TDesign 的 `t-picker` 曾把按钮文案渲染成字面量 `true`
（`cancelBtn`/`confirmBtn` 默认值是布尔 `true`）。已修，但**要确认真的显示「取消 / 确定」**。

---

## 5. 怎么验管理后台（可选，优先级低于第 4 节）

`admin/` 是独立的 Vue3 + Element Plus 工程，前端调 `/api` 需要代理到 8080。

```bash
cd E:/code-start/RePhone/admin
npm run dev     # 起在 localhost:5173，用无头 Chrome 或直接看
```

**⚠️ 千万不要跑 `npm run build`**：本机 esbuild 清理临时文件会 `Access is denied` 导致构建失败，
而 **vite 会先清空 `admin/dist`** —— 已跟踪的产物会被删掉，然后你还得 `git checkout HEAD -- admin/dist` 恢复。
只验证用 `npm run dev`，或者用 `@vue/compiler-sfc` 做静态校验。

登录：账号 `admin`，密码来自 `ADMIN_BOOTSTRAP_PASSWORD`（容器环境变量里可能没设，没设就没有种子账号）。

---

## 6. 后续任务队列（验完第 4 节再做）

### 6.1 订阅消息收尾（**已存在的代码比你想的多**）

**已经做好的**（别重写）：
- `WxAccessTokenService`：stable_token + 内存缓存 + 到期前 5 分钟刷新 + `synchronized`；
- `WxSubscribeService.notifyOrderStatus()`：调 `cgi-bin/message/subscribe/send`；
- 状态流转时已经会调用它（`RecycleOrderService` 的 `transition()`）；
- 模板 ID 走 `WX_SUBSCRIBE_TEMPLATE_ID` 配置，**没硬编码**。

**缺的只有两件**：
1. **前端 `wx.requestSubscribeMessage`** —— 全仓库只有 `AI_PLAN.md` 提过，代码里没有。
   合适的时机：下单成功后 / 进入订单详情时，请求「订单状态变更」模板。
2. **模板字段名对齐** —— `WxSubscribeService` 里字段名写死为 `thing1` / `character_string2`，
   而**微信是申请模板时随机分配字段编号的**。用户申请到的模板如果编号不同，推送会失败并报 **47003**，
   而现在的 `catch (Exception)` 只记 warn，**永远不会显式报出来**。
   → 拿到真实模板后，把字段编号改成配置项，并在推送前校验返回体的 `errcode`。

**真实推送我（上一个 agent）没验过** —— 需要真实模板 ID + 真机授权，你也没法伪造。做到能出网就出网。

### 6.2 管理后台：回收单质检的图片上传

`admin/src/views/RecycleOrders.vue` 的质检表单里有 `images: []` 字段，但**界面上没有上传控件**。
后端 `POST /api/admin/upload` 已经存在（`AdminUploadController`），接上即可。
参考 `Goods.vue` 里质检报告弹窗的写法保持风格一致。

---

## 7. 纪律（不可违反）

1. **`AGENT.md` 是法律**，先读它。里面的硬约束、提交规范、小程序约定全部适用。
2. **1 开发 = 1 提交**，`git add -- <显式路径>`，**禁止** `git add -A` / `git add .` / 裸 `git commit`。
3. **这机器上有多个 agent 并行改同一个仓库**（你、mimosa、以及别的会话）。
   - 提交前**必须** `git status --short` 核对，只 add 你自己改的文件；
   - 会看到别人的文件**中途消失又出现**（`git stash` 窗口），**不要慌、不要动、不要"修复"**，等它恢复后重跑；
   - 出现这种情况就重跑一次构建，别把自己绕进去。
4. **密钥**：真实值只写 `server/.env`（已 gitignore，Spring Boot 会通过 `spring.config.import` 读它），
   同时往 `server/.env.example` 补空值行。**绝不**写进代码、`application.yml`、文档或提交。
5. **不要关掉用户正在用的窗口**。开发者工具已经开着（用户在用它），别随手 `cli.bat quit`。
6. **截图/临时文件放仓库外**（`E:/code-start/.shots/`、`E:/code-start/.agent-tools/`），别提交。
7. **不许声称验证过而没真跑**。跑不通就写「未验证 + 卡在哪一步 + 报错原文」。

---

## 8. 交付格式

每个阶段结束时输出：

```
### 变更文件
- path/to/file —— 一句话说明改了什么

### 验证方式（附证据）
- 命令 / 脚本：
- 输出片段：（贴真实输出，不要改写）
- 截图：E:/code-start/.shots/xxx.png（如有）

### 结论
- ✅ 通过 / ❌ 未通过 / ⚠️ 部分通过（说明哪部分没验）

### 遗留问题
- 现象 + 复现方式 + 已知原因 + 建议
```

**不要**用「应该没问题」「看起来正常」这种措辞。要么有输出，要么写「没验」。

---

## 附录：运行时验证结果（2026-10-01，wechatide CLI 实测）

模拟器登录用户：**userId=530 / openid=mock-900428ce32686c2a**（mock 登录）。
测试单：**RTEST-TRACE-001**（status=20，有运单号 + 轨迹快照）、**RTEST-FILL-001**（status=10，待寄出）。
截图存于仓库外 E:/code-start/.shots/wx-*.jpg。

| # | 验证点 | 结果 | 证据 |
|---|---|---|---|
| 1 | 回收单详情（有归属） | ✅ | code:0；机型/配置/时间轴/价格全部正确渲染 |
| 2 | 回收单详情（无归属） | ✅ | code:40302「无权查看该订单」，证明 SQL 与权限链路正常 |
| 3 | 物流轨迹页（有数据） | ✅ | 绿色头卡「派件中 + 本地快照」、公司·单号、4 节点时间轴、最新节点加粗 |
| 4 | 物流轨迹页（无运单号） | ✅ | 显示「该订单还没有运单号」+「重新查询」，非白屏非 500 |
| 5 | 填写运单号弹层 | ✅ | 快递公司来自后端字典（22 家）显示「顺丰速运」；顺丰出现琥珀提示「查询物流需提供收寄件人手机号，将使用本单登记的 13800000001」 |
| 6 | **快递公司选择器按钮文案** | ✅ | 显示「**取消 / 确定**」而不是字面量 true —— TDesign 默认值 bug 已修实锤 |
| 7 | 机型库页 | ✅ | 左侧品牌栏 Apple 高亮带绿色指示条、右侧两列机型网格、顶部搜索框 |
| 8 | 维修页 → 机型库跳转 | ✅ | 路由 /pages/model-picker/index?biz=repair |
| 9 | 机型库选型 → 回填 | ✅ | 返回维修页后 form = {brandId:1, brandName:Apple, modelId:25, modelName:iPhone 3G}，并自动加载维修项目与价格（199/399/149 元起） |
| 10 | 首页竖向滚动 | ✅ | 能滚到底：严选二手机 → 回收行情表 → 保障说明 → REPHONE 页脚 → TabBar |
| 11 | 全局 console | ✅ | grep -i error 返回空；仅一条 wx.getSystemInfoSync is deprecated 警告（TDesign 内部，非我方代码） |

### 测试数据清理

验证用的两张假单，需要时删掉（SQL 写成文件再喂给 stdio，避免命令行引号地狱）：

    DELETE FROM recycle_order WHERE order_no LIKE 'RTEST-%';

    docker exec -i rephone-mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" rephone' < cleanup.sql

### 仍未验证的部分（诚实声明）

- 订阅消息真实推送：需要小程序后台申请到真实模板 ID + 真机授权，无法伪造。
- admin 管理后台：本轮未跑（admin 构建在本机有破坏性风险，见第 5 节）。
