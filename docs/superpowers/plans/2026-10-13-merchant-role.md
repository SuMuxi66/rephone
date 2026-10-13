# 商家角色端（MERCHANT RBAC）实施计划（2026-10-13）

## 目标

补齐 PRD §4「商家端」：维修商家以 `MERCHANT` 角色登录管理后台，只能看到并操作**本租户**的维修工单
（接单/状态推进/提交报价/回寄运单），其余管理功能一律 403。

## 方案选型

不新做独立端。现有 RBAC 已具备全部机制，只差一个角色种子与两处租户校验：

- `AdminTokenFilter` 对**任何角色** tid>0 的账号都会写 `TenantContextHolder` → 业务表查询自动限定本租户（无需改过滤器）
- `AdminPermissions` 静态映射：`/api/admin/repair` → `repair:manage`
- 管理后台菜单/路由已按 `/me` 下发的权限点裁剪（`visibleMenu` / router meta）

## 改动清单

### 后端（提交 1：feat(admin): MERCHANT 商家角色）

| 文件 | 改动 |
|---|---|
| `server/start/src/main/resources/db/schema-role.sql` | 种子加 `('MERCHANT','维修商家','["repair:manage"]',1,1,'商家端：仅维修工单，数据限本租户')` |
| `server/service/AdminRoleService.java` | 加 `MERCHANT_ROLE` 常量；`RESERVED_CODES` 加 `MERCHANT` |
| `server/service/AdminUserService.java` | `createAdmin`/`changeRole` 中租户必填校验从仅 TENANT_ADMIN 扩展为 TENANT_ADMIN+MERCHANT；错误文案改通用（40080/40079/40081 不变码） |

### 测试（同提交 1 内）

| 文件 | 改动 |
|---|---|
| `server/start/src/test/java/com/rephone/MerchantRoleTest.java`（新增） | ① 商家账号不带租户创建被拒（40080）② 商家登录 `/me` 权限仅 repair:manage ③ 能列本租户维修单 ④ recycle/users 接口 403（40300） |

### 管理后台（提交 2：feat(admin): 账号页支持商家角色与租户绑定）

| 文件 | 改动 |
|---|---|
| `admin/src/api/permissions.js` | `ROLE_LABELS` 加 `MERCHANT: '维修商家'` |
| `admin/src/views/Accounts.vue` | 创建账号的租户字段显隐与提交条件：TENANT_ADMIN → `TENANT_ADMIN`/`MERCHANT`；文案「所属租户」 |

## 非目标

- 不做回收人员端（PRD §5，后续同模式加 RECYCLER 角色）
- 不做商家独立小程序端/独立域名
- 不改维修工单业务流程与状态机

## 验收标准

1. `cd server && ./mvnw.cmd test` 全量通过（含新增 MerchantRoleTest）
2. admin Vue SFC 静态校验 0 失败
3. 商家账号：可见=仅维修单管理菜单；本租户维修单可查可推进；跨模块接口 40300
4. 提交遵循 AGENT.md（显式 pathspec、Conventional Commits）

## 风险

- H2 各测试类 `schema-locations` 各自带 `schema-role.sql`，新种子对存量测试是新增行，无冲突（INSERT IGNORE）
- `createAdmin` 校验收紧仅影响新角色；存量角色行为不变
