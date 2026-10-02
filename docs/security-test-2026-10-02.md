# 安全补测报告（2026-10-02）—— 对 10-01 审计的增量复测

> 定位：`docs/security-audit-2026-10-01.md` 的**增量补测**。10-01 审计之后平台发生了三批大变化
> （真实模式切换 WX_MOCK_LOGIN=false + 真实 code2session、P7 管理端全套新端点、寄修双向运单），
> 且原审计未覆盖**业务金额篡改**与 **mass assignment**。本轮只打增量和缺口，不重复原 25 项。
> 全部探针在本地真实实例（docker rephone-server + MySQL）执行，测试夹具单已取消，无脏数据残留。

## 1. 结论速览

| 维度 | 结论 |
|---|---|
| SQL 注入 | **干净**。静态面零原生 SQL，黑盒 14 项探针全部无效 |
| 暴力破解 | **干净**。P7 登录改造后 LoginAttemptGuard 仍在生效（第 6 次锁定） |
| 新管理端面鉴权 | **干净**。匿名/垃圾/用户 token 全 401，租户管理员越权全被 40300/隔离挡住 |
| 金额篡改 | **干净**。服务端实时重算报价，任何篡改（+1分/负数/1分钱）一律 40020 拒绝 |
| mass assignment | **干净**。走私字段（id/tenantId/status/finalFen/userId）全部被 DTO 绑定忽略 |
| 观测面/泄露 | **干净**。actuator/swagger/druid 不可达，错误体零堆栈，无 CORS 回显 |
| 新发现 | 2 个低危健壮性问题（见 §6），无高危/中危 |

## 2. 方法与凭据

- 黑盒探针脚本：`C:/Users/Administrator/.zcode/tmp/sec-{a,b,c,e,f}.ps1`（仓库外一次性工具，不入库）
- 三种视角凭证：机器令牌（.env ADMIN_TOKEN）、超管账号 JWT（admin 登录签发）、
  租户2管理员 JWT（**用本地 JWT_SECRET 铸造 sub=544/tid=2/role=admin，走真实 AdminTokenFilter
  的查库-授权-租户校验路径**，等效真实登录）
- 数据完整性锚点：探针前后 `phone_model=1458 / brand=12` 不变，证明无注入副作用落库

## 3. S1 SQL 注入（14 项）

**静态审查（白盒）**：全 server 无 mapper XML、无 `@Select/@Update/@Delete/@Insert` 注解 SQL、
无 jdbcTemplate；唯一 8 处 QueryWrapper `.last()` 均为常量 `limit 1` 或整数钳制的
`LIMIT`（`DashboardService.java:113/129` 的 `Math.min(Math.max(limit,1),20)`）。注入面仅剩运行时验证。

| # | 探针 | 结果 |
|---|---|---|
| 1 | `tenants?keyword=' OR '1'='1`（基线 keyword=E2E 共1条） | total=0，字面量匹配 ✅ |
| 2 | `tenants?keyword=' UNION SELECT id,username,password_hash...FROM user--` | total=0 ✅ |
| 3 | `tenants?keyword=x' AND SLEEP(3)-- -` | 30ms（无延时注入）✅ |
| 4 | `models?keyword=' UNION SELECT username,password_hash FROM user--` | 同上路径（带 brandId 必填参数）✅ |
| 5 | `dashboard/trend?days=' OR '1'='1`（int 型参数） | 500 兜底，无数据返回 ✅ |
| 6 | `dashboard/top?limit=3;DROP TABLE phone_model--` | 500 兜底；**探针后 phone_model 仍 1458 行** ✅ |
| 7 | `finance/summary?from=2026-01-01' OR...`（LocalDate 型） | 500 兜底 ✅ |
| 8 | `wx orders?status=10 OR 1=1`（Integer 型） | 500 兜底 ✅ |
| 9 | `wx order/R20261002' OR '1'='1`（路径参数注入） | 40403 订单不存在（业务错误，非 500）✅ |
| 10 | `admin recycle/order/' UNION SELECT 1,2,3-- -` | 40403 ✅ |
| 11 | 登录接口 `username=' OR '1'='1' --` | 40101 用户名或密码错误，不发 token ✅ |

## 4. S2 暴力破解 / 登录防护（P7 改造后复测）

| # | 用例 | 结果 |
|---|---|---|
| 1 | 未知账号登录 | 40101「用户名或密码错误」——**与错密码同文案同码，无账号枚举面** ✅ |
| 2 | admin 连续 5 次错密码 | 前 5 次 40101 正常放行（每次约 70ms） |
| 3 | 第 6 次错密码 | **42901「登录失败次数过多，请 14 分钟后再试」——锁定生效** ✅ |
| 4 | 锁定期间提交**正确**密码 | 仍 42901（锁覆盖有效凭证，防撞库窗口）✅ |
| 5 | 锁定期间机器令牌访问 API | 200 正常（break-glass 独立凭证，设计如此）✅ |
| 6 | 重启容器清进程内计数后 | admin 正常登录恢复 ✅ |

原 SEC-3 修复（15 分钟滑窗 5 次失败锁定）在 P7 登录准入改造后**回归通过**。

## 5. S3 新管理端面鉴权 + 租户隔离（P7 新增端点）

| # | 用例 | 结果 |
|---|---|---|
| 1 | finance/dashboard/tenants/roles 匿名访问 | 401 ✅ |
| 2 | 垃圾 Bearer 串 | 401 ✅ |
| 3 | **小程序用户 JWT**（无 role=admin claim）访问 /api/admin/* | 401 ✅ |
| 4 | 机器令牌（对照组） | 200 ✅ |
| 5 | 租户2管理员 → /tenants /roles /finance/* | **40300 无权限**（平台级功能对租户管理员封死）✅ |
| 6 | 租户2管理员 → 本租户业务列表 | 200 但 total=0；dashboard 全 0（DB 中租户0有14单，一条未泄）✅ |
| 7 | 租户2管理员读**租户0订单号**详情 | 40403 订单不存在（租户上下文过滤生效，无存在性泄露）✅ |

## 6. S4 金额篡改 + S5 mass assignment（10-01 审计未覆盖的缺口）

| # | 用例 | 结果 |
|---|---|---|
| 1 | 服务端重算报价（iPhone 15 Pro Max/128GB/COND_95/SCR_OK） | priceFen=578000，与真实订单一致（对照成功）✅ |
| 2 | 下单 quoteFen=578**001**（+1分） | **40020 报价已更新，请重新估价** ✅ |
| 3 | 下单 quoteFen=**-1** | 40020 ✅ |
| 4 | 下单 quoteFen=**1**（一分钱套现尝试） | 40020 ✅ |
| 5 | 出售单 quantity=0 / -1 / 100 | 40069「数量须为 1-99」✅ |
| 6 | 出售单 quantity=2 + 走私 `totalFen:1` | DB 实测 total_fen=**579800**=289900×2（服务端算，走私字段忽略）✅ |
| 7 | 回收单走私 `id:999999, tenantId:9, status:50, finalFen:1, userId:541, expressCompany:'hax'` | DB 实测：user_id=543 / tenant_id=0 / status=10 / final_fen=NULL / express_company=NULL，**全部忽略** ✅ |

回收金额防线是**结构性**的：`RecycleOrderService.create` 以 `quoteService.calculate` 实时结果为准，
客户端报价仅作一致性回传（不一致即 40020）；质检最终价另有 `>0 且 ≤估价×2` 双重钳制（40029）。
测试单已全部取消清理。

## 7. S6 观测面 / 信息泄露 / 传输面

| # | 用例 | 结果 |
|---|---|---|
| 1 | /actuator、/actuator/env、/v3/api-docs、/swagger-ui、/druid、/console | 全部统一 JSON 兜底，**无任何管理面暴露** ✅ |
| 2 | 畸形 JSON / 错误 JSON 类型 | 500 兜底，**零堆栈泄露** ✅ |
| 3 | 400KB 超大 body 打登录 | 104ms 内 40101 正常处理，无挂起 ✅ |
| 4 | OPTIONS 预检 + 恶意 Origin | 无 `Access-Control-Allow-Origin` 回显（浏览器侧天然阻断）✅ |
| 5 | GET / 根路径 | 无目录列表/HTML 泄露 ✅ |
| 6 | 路径参数 `<script>` 注入 | HTTP 400（容器层拒绝），无反射 ✅ |

## 8. 新发现（均为低危，不阻断上线）

**SEC-4（低）参数异常统一返回 500「服务繁忙」**：`GlobalExceptionHandler` 只有 `BizException`
+ 兜底 `Exception` 两级，Spring 的参数缺失/类型不匹配/JSON 解析错误全部落兜底。无信息泄露，
但客户端无法区分「我请求错了」和「服务坏了」，监控侧也会把攻击噪音算进 500 错误率。
建议：补 `MissingServletRequestParameterException` / `MethodArgumentTypeMismatchException` /
`HttpMessageNotReadableException` 三个 handler 映射 400/400xx。

**SEC-5（低，10-01 遗留的持续项）JSON 请求体无显式大小上限**：400KB 实测可进业务逻辑；
叠加 OSV 已核实的 Jackson 二次方解析问题（GHSA-cxp5-3px4-pw24），理论 DoS 面存在。
建议：自定义 Filter 限制 body 上限（如 1MB），与 10-01 报告第 7 节建议合并处理。

**复核确认 P1（今日功能测试已报，本轮二次验证）**：停用租户的管理员登录接口仍发放 token
（登录准入缺口），但**数据面二次确认无泄露**——该 token 访问一切 API 均 40101/40300。
维持原定级与修复建议（AdminAuthController.login 事务内校验租户状态）。

## 9. 覆盖边界（未测项）

- 并发安全（超卖/CAS 竞态压测）——10-01 报告遗留 #2，仍未做
- 多实例部署下限流阈值放大——10-01 遗留 #4，仍未做（本地单实例）
- 微信支付回调验签——支付链路未实装（P6 支付外置缓做，商户号未到位）
- 快递100 回调验签——10-01 已测（伪造 sign 返回 fail），本轮未重复
- 生产域名/HTTPS/合法域名——属部署清单（docs 上线计划 bba0f5e），非代码层
