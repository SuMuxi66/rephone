# P7 完整管理端四件：租户管理 + RBAC + 财务对账 + 数据看板（2026-10-02）

> 微信支付（外部位）用户明确先不做；本计划只覆盖管理端能力升级。

## 目标

1. **RBAC**：管理员按角色持权限点访问 /api/admin/**；机器凭证 ADMIN_TOKEN 保持 break-glass 全权；
   /me 下发权限，admin 前端菜单/路由按权限显隐。
2. **租户管理**：租户 CRUD + 启停；可建租户附带其管理员账号；租户管理员登录后数据自动限定本租户
   （复用现有 TenantLineInnerInterceptor，AdminTokenFilter 账号分支按 JWT tid 写租户上下文）。
3. **财务对账**：三线订单资金流按日期范围汇总 + 流水明细（纯查询聚合，不建账本表，与订单天然一致）。
4. **数据看板**：汇总卡 + 近 14 天三线单量趋势 + 热门回收机型/热销商品 Top，admin 新默认首页。

## 现状约束（摸底结论）

- `tenant` 表已存在（schema.sql）；`user.role` VARCHAR(16) 现为 USER/ADMIN——**复用该列存角色编码，零迁移**。
- 管理端请求租户上下文为空 → 拦截器不加租户条件 → 平台管理员天然全租户；给租户管理员写上下文即得隔离。
- ADMIN_TOKEN 双凭证、登录限流、BCrypt、防自锁（管理员禁禁用）均已存在，全部保留。

## 设计

### 角色与权限点

角色表 `role`（新表，启动自动建；种子 INSERT IGNORE）：

| role_code | 名称 | 内置 | 权限 |
|---|---|---|---|
| ADMIN | 平台超管 | 是 | *（代码固定，不存表） |
| TENANT_ADMIN | 租户管理员 | 是 | recycle/repair/sale/goods/address/dashboard |
| OPERATOR | 运营 | 是 | recycle/repair/sale/goods/address/dashboard |
| FINANCE | 财务 | 是 | finance/dashboard |
| VIEWER | 只读 | 是 | dashboard |

权限点（静态枚举，10 个）：`recycle:manage` `repair:manage` `sale:manage` `goods:manage`
`address:manage` `account:manage` `tenant:manage` `role:manage` `finance:read` `dashboard:read`。

- 登录条件从 `role=='ADMIN'` 放宽为「role ∈ role 表中 status=1 的管理角色集合」。
- JWT claim 保持 `role=admin`（统称），实际权限每请求按 user.role → role 表解析。
- 新增 `AdminPermissionInterceptor`：路径前缀→权限点静态映射；token 凭证放行；
  账号凭证查库校验（顺带实现「禁用账号 token 立即失效」）；无权限 40300。
- 预留自建角色：角色管理页可增改（权限矩阵勾选）、可删（内置不可删，ADMIN 不可改）。

### 租户

- `GET /api/admin/tenants`、`POST /api/admin/tenant`、`PUT /api/admin/tenant/{id}`（改名/启停；租户 0 不可停用）。
- 创建租户可选附带管理员账号（username/password/nickname → user.tenant_id=新租户, role=TENANT_ADMIN）。
- AdminTokenFilter 账号凭证分支：JWT tid>0 → `TenantContextHolder.set(tid)`；tid=0（平台超管）→ 保持 null 全租户。
- 账号管理：创建管理员支持选角色；新增 `PUT /api/admin/account/{id}/role`。

### 财务对账（口径）

| 款项 | 来源 | 口径 |
|---|---|---|
| 回收打款 | recycle_order | status=50，sum(final_fen) |
| 出售收款 | sale_order | status in (20,30,40)，sum(total_fen) |
| 出售退款 | after_sale | 同意态，sum(refund_fen) |
| 维修收款 | repair_order | status=50，sum(total_fen) |

`GET /api/admin/finance/summary?from&to`（默认近 30 天）+ `GET /api/admin/finance/flows?from&to&type&pageNum`
（三表各自范围查询后内存合并倒序分页；流水=每笔订单一行）。

### 数据看板

- `GET /api/admin/dashboard/summary`：今日/昨日/近7日/近30日单量与营收、各线待处理数。
- `GET /api/admin/dashboard/trend?days=14`：按天三线单量（Java 分组聚合，避免 H2/MySQL 函数差异）。
- `GET /api/admin/dashboard/top`：热门回收机型 Top5（订单数）、热销商品 Top5（件数）。
- 前端不引入图表库：汇总卡 + CSS 条形趋势 + Top 榜单；默认首页改 `/dashboard`。

## 提交拆分（每个自洽可验证）

1. `docs(plan)`: 本计划
2. `feat(server)`: 角色表/种子 + RBAC 权限点 + 拦截器 + 登录/me 扩展 + H2 测试
3. `feat(server)`: 租户管理接口 + 租户上下文隔离 + 账号角色接口 + H2 测试
4. `feat(server)`: 财务对账接口
5. `feat(server)`: 数据看板接口
6. `feat(admin)`: 菜单/路由权限化 + 租户管理页 + 角色管理页 + 账号页角色化
7. `feat(admin)`: 财务对账页 + 数据看板页 + 默认首页

## 非目标

- 微信支付/打款真实化（用户指定外置缓做）；多租户 wx 端注册流转（wx 登录仍固定 tid=0）；
  权限点细到按钮级；对账差异化核账（银行流水比对）。

## 验收标准

- H2：mvnw test 全绿；新增角色/租户/权限测试（无权限 40300、超管放行、token 放行、租户管理员只见本租户）。
- 真实库：容器重建后 role 表自动建、种子就位；curl 验证：建租户+租户管理员 → 租户管理员登录 →
  列表只见本租户数据、访问 finance 40300；平台超管对账/看板数字与订单表 SQL 手工核对一致。
- admin：npm 构建风险规避（用 @vue/compiler-sfc 静态校验，不跑 vite build）；页面可用性浏览器实测。

## 风险

- AdminPermissionInterceptor 每请求查 user+role（各一次主键/小表查询）——本地量级无碍，上量后再谈缓存。
- trend 走 Java 聚合（30 天窗口查询）——早期数据量可接受，标注后续 SQL 优化点。
- 路径→权限映射为静态表，新增 admin 接口必须同步登记（写进 AGENT.md 备忘）。
