# RePhone 全功能可用性测试报告（2026-10-02）

> 测试方式：按本报告逐项执行，每项只记录**实际执行命令与实际输出**的结论，未执行项如实标注。
> 遵循 verification-before-completion 原则：先取证、后下结论。

## 0. 环境探测

| 项 | 结果 | 证据 |
|---|---|---|
| Docker 容器 | rephone-server Up 42min / rephone-mysql Up 2h (healthy) / rephone-redis Up 26h | `docker ps` |
| 后端存活 | HTTP 可达，未带 token 请求返回 401（鉴权生效） | `curl http://localhost:8080/api/wx/brands` |
| 运行模式 | `WX_MOCK_LOGIN=false`、`EXPRESS_MOCK=false`（**真实模式**） | `docker exec rephone-server env`（仅查非密钥开关） |
| wechatide CLI | 可用，已授权（auth success），登录态有效（loginExpired=false） | `check_wechatide_status` |
| 开发者工具服务端口 | **未开启**（127.0.0.1:9420 拒绝连接）→ 小程序自动化阻塞 | `Invoke-WebRequest 127.0.0.1:9420/v2/ping` |

## 1. T1 后端单测/集成测试 —— ✅ 通过

- 命令：`cd server && ./mvnw.cmd test`
- 结果：**Tests run: 62, Failures: 0, Errors: 0, Skipped: 0**，`BUILD SUCCESS`（19.5s）
- Reactor 9 个模块全部 SUCCESS（含 H2 内存库集成测试：回收/维修寄修/订阅消息等流程）
- 测试期警告（非失败项）：mock 配置守卫提示——测试环境用 mock 凭据属预期

## 2. T2 后端运行时 API 冒烟 —— ⚠️ 部分通过（1 项阻塞转 T4）

| 步骤 | 结果 | 证据 |
|---|---|---|
| 伪造 code 登录 | 按预期被拒：业务码 `50003`（真实模式 code2Session 校验失败） | `POST /api/wx/login {"code":"test-code-123"}` |
| 管理端未授权访问 | 按预期被拒：HTTP **401** | `GET /api/admin/recycle/orders`（无 token） |
| 带 token 业务链路（品牌→型号→估价→下单→列表→详情） | **阻塞**：运行中容器为真实模式，脚本拿不到真实登录态 | — |
| 真实登录全链路 | **转 T4**：由小程序自动化（开发者工具真 appid wx.login）取得真实 token 后回补执行 | — |

> 结论：鉴权与错误路径行为正确；正向业务链路需真实登录态，见 T4。

## 3. T3 管理后台静态校验 —— ✅ 通过

- 方式：`@vue/compiler-sfc` 的 parse + compileScript + compileTemplate（**刻意不跑 `vite build`**，
  避免 AGENT.md §8 记录的 dist 破坏性风险）
- 结果：**15 个 .vue 文件全部编译通过，0 失败**

## 4. T4 小程序编译 + 页面自动化 —— ⛔ 阻塞（等待人工操作）

- 已完成：wechatide CLI 授权成功、登录态有效、微信开发者工具进程在运行
- 阻塞点：**开发者工具「服务端口」未开启**（9420 拒绝连接），
  `open_project_window` 报 `MCP_TOOL_ERROR: terminated`，后续调用均 `MCP_INIT_ERROR: aborted`
- 解除条件（人工）：微信开发者工具 → 设置 → 安全设置 → 开启服务端口
- 解除后执行：
  1. `open_project_window` 打开项目
  2. 编译 → 逐 tab 页导航（首页/购买/维修/回收/我的）→ console 错误采集 → 截图
  3. 读取登录态 token → 回补 T2 正向业务链路（品牌/型号/估价/下单/详情）

## 5. 顺带发现（非本次测试范围，建议处理）

- 运行中的容器仍以 `0.0.0.0` 暴露 3306/6379——compose 已改为回环绑定（提交 4ff5ef7），
  但**容器是改前启动的**，需 `docker compose up -d` 重建后生效

## 6. 总结

| 维度 | 状态 |
|---|---|
| 后端代码质量（62 项测试） | ✅ |
| 后端运行时安全（未授权拒绝） | ✅ |
| 管理后台编译正确性 | ✅ |
| 小程序端到端 | ⛔ 等服务端口开启 |
| 后端正向业务链路（真实登录态） | ⏳ 随 T4 回补 |
