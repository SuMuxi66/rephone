# RePhone 生产部署手册（README-production）

> 适用版本：v1.0.0 及以上（v1.0.1 起，含维修报价/支付、评价系统、回收图片与拒绝最终价）。上线前请逐项过完文末 Checklist。

## 1. 环境要求

| 组件 | 版本 | 说明 |
|---|---|---|
| Docker + Docker Compose | 24+ / v2 | MySQL/Redis/后端一键编排（`server/docker-compose.yml`） |
| MySQL | 8.0 | utf8mb4；数据卷持久化 |
| Redis | 7 | 会话/缓存 |
| 小程序 | 微信开发者工具最新稳定版 | `config/index.js` 指向后端域名（https） |
| 管理后台 | Node 18+（构建） | `admin/` Vite 构建后由 Nginx 托管 |
| 域名与证书 | 1 个 https 域名（后端 API+回调） | 微信支付回调/订阅消息要求公网 https |

## 2. 启动步骤

```bash
git clone <repo> && cd RePhone/server
cp .env.example .env          # 逐项填写（见 §4），.env 不入库
docker compose up -d --build  # MySQL/Redis/后端；表结构启动时自动幂等创建
docker compose logs -f server # 等待启动完成（/api/health 返回 pong）
```

管理后台：

```bash
cd admin && npm ci && npm run build   # 产物 admin/dist（不入库），托管到 Nginx（/ 指向 dist，/api 反代到后端 8080）
                                      # 或直接使用 GitHub Release 提供的 rephone-admin-dist-<版本>.zip
```

小程序：开发者工具导入仓库根目录，`config/index.js` 的 API 地址切到生产域名，上传提审。

## 3. 数据库初始化

- **新库**：无需手工执行 SQL。应用启动时 `spring.sql.init` 幂等创建全部表
  （`db/schema*.sql`：基础表 → 估价 → 回收 → 维修 → 出售 → 支付 → 角色，再落数据种子）。
- **存量库升级**：`CREATE TABLE IF NOT EXISTS` 不会给已存在的表加列，必须按顺序手工执行全部补丁。
  **执行任何补丁前先备份**（`mysqldump` 全量，并确认可恢复）；已执行过的补丁重复执行会报重复列，忽略即可：
  1. `db/patch-express-trace.sql`（recycle_order 轨迹快照列）
  2. `db/patch-repair-express.sql`（repair_order 双向运单列）
  3. `db/patch-repair-quote.sql`（repair_order.pay_no + repair_quote 表）
  4. `db/patch-recycle-images.sql`（recycle_order.images_json）
  5. `db/patch-goods-attrs.sql`（goods 品牌/容量/成色/标签列 + 检索索引；缺列会导致商品列表/筛选与出售下单报错）
- **初始化与补丁的区别**：`server/sql/001_init.sql`（compose 首次初始化挂载）只含 tenant/user/order_status_log
  三张基础表，其余表由应用启动补齐，二者幂等兼容；`db/patch-*.sql` 仅服务于「表已存在」的存量库，
  新库一律不要手工执行（重复执行会报重复列/重复索引）。
- **回滚方案**：表结构变更全部为「加表/加列」，回滚 = `ALTER TABLE ... DROP COLUMN` / `DROP TABLE`
  （补丁文件头部有精确的加列清单，逆向执行即可）；数据回滚依赖 MySQL 定时备份
  （上线前必须配置 `mysqldump` 每日全量 + binlog）。应用回滚 = `docker compose` 切回上一镜像 tag。

## 4. 环境变量（server/.env）

| 变量 | 必填 | 说明 |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | ✅ | 强随机；compose 里 MySQL 与后端共用 |
| `JWT_SECRET` | ✅（真实登录） | ≥32 字节强随机，不足启动失败 |
| `WX_MOCK_LOGIN` | ✅ | 生产必须 `false` |
| `WX_APPID` / `WX_SECRET` | ✅ | 小程序凭据 |
| `ADMIN_TOKEN` | 建议 | 管理端机器令牌（强随机）；留空则仅账号密码登录 |
| `ADMIN_BOOTSTRAP_PASSWORD` | ✅ | 管理员引导密码（首启创建 admin 账号用） |
| `EXPRESS_MOCK` | ✅ | 生产必须 `false`（mock 回调虽已强制验签，仍应关掉） |
| `EXPRESS_KEY` / `EXPRESS_CUSTOMER` / `EXPRESS_CALLBACK_URL` | 快递100 时 | 快递100 企业版；回调地址公网 https |
| `WXPAY_MOCK` | ✅ | 生产 `false` |
| `WXPAY_MCH_ID` / `WXPAY_MCH_KEY` | ✅（支付） | 微信商户号 / APIv2 密钥 |
| `WXPAY_NOTIFY_URL` | ✅（支付） | `https://<域名>/api/callback/wxpay` |
| `WXPAY_CERT_PATH` | 打款时 | 企业付款到零钱证书路径 |
| `PAY_PENDING_TTL_MINUTES` | 可选 | 待支付单关闭时长（默认 30 分钟） |
| `PAY_CLOSE_CRON` | 可选 | 关单扫描周期（默认每 5 分钟） |
| `COS_MOCK` | ✅ | 生产 `false` |
| `COS_SECRET_ID` / `COS_SECRET_KEY` / `COS_BUCKET` / `COS_REGION` | ✅（图片） | 腾讯云 COS；图片直传与预签名回显依赖它 |
| `WX_SUBSCRIBE_*` / `EXPRESS_MOCK` 以外的订阅项 | 可选 | 订阅消息模板字段；未配置则跳过订阅 |

**docker compose 注意**：以上变量已全部在 `server/docker-compose.yml` 透传进容器；
compose 会自动读取同目录 `.env` 做插值，改完 `.env` 后 `docker compose up -d` 生效。

**安全默认值（v1.0.1 起）**：compose 里
`SPRING_PROFILES_ACTIVE` 默认 `prod`，四个 mock 开关默认**全部为 false**。
因此**未做任何配置就 `docker compose up` 的部署是安全的**：一旦有人打开了任一 mock 开关，
`MockGuardConfig` 会在启动阶段抛出 `IllegalStateException` 并拒绝启动，
而不是打一行日志照常运行。本地要跑 mock 必须显式设 `SPRING_PROFILES_ACTIVE=dev` 加对应开关。

## 5. 微信配置

1. **小程序**：`WX_APPID/WX_SECRET`；后台「开发管理-服务器域名」加入后端 https 域名（request 合法域名）。
2. **微信支付（V2 JSAPI）**：商户平台开通 JSAPI 支付；`WXPAY_MCH_ID/WXPAY_MCH_KEY`；
   支付回调 `https://<域名>/api/callback/wxpay` 填入商户平台并保证公网可达。
   未配置时系统保持 mock 闭环（下单/详情可用，不产生真实收款）。
3. **企业付款到零钱**（回收打款）：商户平台签约并配置 `WXPAY_CERT_PATH` 证书。
4. **订阅消息**：申请「订单状态变更」模板，字段名照抄后台（thing1 等）填入 `WX_SUBSCRIBE_*`。

## 6. COS 配置

1. 腾讯云控制台创建存储桶（私有读写），记下 `COS_BUCKET/COS_REGION`。
2. 子账号授予该桶 `PutObject/GetObject` 权限，填入 `COS_SECRET_ID/COS_SECRET_KEY`，`COS_MOCK=false`。
3. 图片访问走后端签发的 30 分钟预签名 URL（回收/维修/评价图片），无需公有读。
4. 管理端本地图（商品/质检/报价附件）落在容器 `/app/data/img/**`（compose 已挂载 `./data`）。

## 7. 测试账号

| 端 | 账号 | 说明 |
|---|---|---|
| 小程序（开发） | 任意 | `WX_MOCK_LOGIN=true` 时任意 code 登录为演示用户；**生产关闭后走真实微信登录**，无预置业务账号 |
| 管理后台 | `admin` | 首次启动用 `ADMIN_BOOTSTRAP_PASSWORD` 完成 admin 引导（平台超管，`*` 全权） |
| 管理后台（机器凭证） | `ADMIN_TOKEN` | 仅自动化脚本用；生产建议留空禁用 |
| 角色 | TENANT_ADMIN/OPERATOR/FINANCE/VIEWER | 「角色管理」页按权限矩阵创建并绑定账号 |

## 8. 上线 Checklist（压缩版）

- [ ] `.env` 全部必填项就位，`*_MOCK` 全部 false
- [ ] `JWT_SECRET`/`ADMIN_TOKEN`/`MYSQL_ROOT_PASSWORD` 强随机
- [ ] MySQL/Redis 仅回环绑定（compose 已默认）+ 每日备份
- [ ] 支付回调/快递回调域名公网可达，微信商户平台已配置
- [ ] 存量库补丁按 §3 顺序执行完毕
- [ ] 冒烟：小程序登录 → 估价 → 下单（带图）→ 后台质检 → 打款 → 评价；维修报价 → 支付（真实 1 分钱流程）→ 取消/完成
