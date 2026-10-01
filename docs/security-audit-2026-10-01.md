# 安全测试报告（2026-10-01）

## 1. 范围与方法

对**本地真实运行实例**（Docker `rephone-server` + MySQL）做黑盒渗透 + 关键路径白盒复核。
不改动生产数据：越权用例只用脚本自建的两个身份与其自有资源。

- 黑盒：首轮 25 项探针 + 修复后复测 5 项（脚本见 `E:/code-start/.shots/pentest*.js`，不入库）
- 白盒：逐 service 核对归属校验（IDOR）、鉴权过滤器、JWT 实现、租户插件、异常处理

## 2. 结论

| 编号 | 严重度 | 问题 | 状态 |
|---|---|---|---|
| SEC-1 | **高** | `ADMIN_TOKEN` 默认 `local-admin-token`，开箱即用的管理端凭证 | ✅ 已修 |
| SEC-2 | **高** | 未设 `prod` profile → MockGuard 不生效 → mock 登录=全站认证绕过，且无任何提示 | ✅ 已修 |
| SEC-3 | 中 | 管理端登录无限流，可在线暴力破解 | ✅ 已修 |

**认证与越权面本身是干净的**：25 项探针中 23 项一次通过，且越权类 13 项**全部**返回 `40302`。

## 3. 发现详情

### SEC-1（高）默认管理令牌

`docker-compose.yml` 里 `ADMIN_TOKEN: ${ADMIN_TOKEN:-local-admin-token}` —— 一个**开箱可用**的凭证。

```
GET /api/admin/recycle/orders  Authorization: Bearer local-admin-token  -> HTTP 200 code=0 记录数=3
GET /api/admin/users?pageSize=3                                          -> HTTP 200 code=0 记录数=3
GET /api/admin/addresses?pageSize=3                                      -> HTTP 200 code=0 记录数=1
```

影响：读到全部订单与用户地址（PII），且该凭证同样可**写**（改订单状态、触发打款到零钱）。
部署时忘记覆盖 `.env` 就等于把后台公开。

**修复**：compose 默认值改为空（空则只禁用机器令牌，管理员仍可账号密码登录）；
`MockGuardConfig` 把 `local-admin-token` / `test-admin-token` / `admin` / `changeme` / `secret`
列为弱令牌，prod 下直接拒绝启动，非 prod 下高声告警。本地 `.env` 已生成 48 位随机值。

### SEC-2（高）生产守卫失效

`MockGuardConfig` 只在 `acceptsProfiles("prod")` 时检查，而 compose 与启动命令**都没有设置该 profile**，
于是守卫形同虚设：

```
# 容器环境
WX_MOCK_LOGIN = true
SPRING_PROFILES_ACTIVE = (未设置)

# 未认证即可换取会话
POST /api/wx/login {"code":"anything","deviceId":"attacker-chosen-id"} -> code=0 token 长度 226
```

影响：整个 `/api/wx/*` 的认证被绕过，任意人可创建/读取会话。同一开关还让 `WXPAY_MOCK`（假打款）、
`COS_MOCK`、`EXPRESS_MOCK` 失去保护。

**修复**：非 prod 环境也检查，但改为**打印醒目告警横幅**（列出所有生效的不安全开关及影响），
prod 仍然拒绝启动。已在容器启动日志确认横幅出现。

### SEC-3（中）管理端登录无限制

连续 8 次错误密码，410ms 内全部放行，无锁定、无限流 → 可暴力破解管理员口令。

**修复**：新增 `LoginAttemptGuard`，同一账号滑动窗口 15 分钟内失败 5 次即锁定，
成功登录清零；返回 `42901` 并给出剩余分钟数。进程内计数，单实例足够；
多实例部署阈值会被放大成 `实例数 × 5`，要严格需换 Redis 计数（已在类注释写明）。

## 4. 通过项（25 项探针明细）

| 类别 | 用例 | 结果 |
|---|---|---|
| 认证边界 | 无 token / 非 JWT / `alg=none` 无签名 / 错误密钥签名 / 篡改 payload 保留原签名 | 全部 401 |
| 横向越权 | 改、删、设默认**他人地址**；读、取消、确认打款、填运单号**他人回收单**；读**他人回收单轨迹** | 全部 `40302` |
| 数据隔离 | 地址列表、订单列表不含他人数据 | 通过 |
| 管理端 | 无凭证、普通用户 JWT 访问 `/api/admin/*` | 401 |
| 路径穿越 | COS 签名 `ext=../../../evil`；`/img/../../application.yml` | key 未穿越；HTTP 400 |
| 回调验签 | 快递100 回调伪造 `sign` | 返回 `fail` |
| 输入健壮性 | 分页 `pageSize=100000`；超长 `orderNo`(5000 字符)；筛选参数 SQL 注入串 | 钳制到 100；`40403`；正常返回 |
| 信息泄漏 | 未处理异常返回体 | 统一 `500 服务繁忙`，无堆栈 |

白盒复核补充：`RecycleOrderService` / `SaleOrderService` / `RepairOrderService` /
`AfterSaleService` / `UserAddressService` 的**所有用户侧读写路径**均有 `requireOwned` 或
`eq(userId, 当前用户)`；管理侧走 `requireOrder`（由 `AdminTokenFilter` 把关）。

`JwtTokenService` 强制密钥 ≥32 字节、用 `verifyWith(key)` 校验签名（不受 `alg=none` confusion 影响）；
`AdminTokenFilter` 用 `MessageDigest.isEqual` 常量时间比较机器令牌；
`TenantLineInnerInterceptor` 自动追加 `tenant_id`。

## 5. 未覆盖 / 遗留风险

1. **依赖漏洞未扫**：未跑 `mvn dependency-check` / `npm audit`，第三方组件 CVE 未评估。
2. **未做压力/并发安全测试**：如并发下单导致的库存超卖、并发状态流转（CAS 已实现但未压测）。
3. **`/api/wx/login` 本身无限流**：mock 模式下可无限创建用户（生产模式下 code 由微信签发、一次性，风险低）。
4. **限流为进程内实现**：多实例部署时阈值被放大。
5. **小程序端未测**：`wx.requestSubscribeMessage` 授权、token 本地存储等未做专项评估。
6. **前一个 agent（mimosa）报的 7 个高危未复核**：均为 `miniprogram_npm` 构建产物的“命令注入”
   与协议强制的 MD5/SHA1（快递100 签名、微信支付要求），判断为误报，但本轮未逐条验证。

## 6. 复测结果（修复后，真实实例）

```
PASS  [C3] 历史默认 ADMIN_TOKEN=local-admin-token   期望=401 拒绝   实际=HTTP 401 code=40100
PASS  [C8] .env 中的强 ADMIN_TOKEN 仍可访问管理端      期望=200       实际=HTTP 200 code=0 记录数=2
PASS  [C4] 管理端登录连续错误密码                     期望=第6次起限流 实际=第6次触发：请 14 分钟后再试
PASS  [C9] 限流不影响小程序登录                       期望=code=0    实际=code=0
PASS  [C10] 回归：快递公司字典                         期望=code=0    实际=code=0
```

后端全量单测 40 通过（新增 `LoginAttemptGuardTest` 3 项）。
