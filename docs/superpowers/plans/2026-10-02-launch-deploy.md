# 上线部署计划落实清单（2026-10-02）

## 目标

为 RePhone 三端（小程序 / Spring Boot 后端 / 管理后台）梳理上线部署路径，
把**当前就能在仓库内完成**的准备工作逐项落实，需要外部资源（域名/密钥/审核）的项列明等待条件。

## 非目标

- 不申请域名、备案、证书、微信/快递100/支付商户凭据（需人工办理，本文档只列步骤）
- 不改动业务代码与页面逻辑
- 不在本次接入真实支付/打款（P6 范围，mock 模式已可跑通闭环）

---

## A. 本次可直接落实（仓库内改动）

| # | 任务 | 产出 | 状态 |
|---|---|---|---|
| A1 | 编写本部署清单文档 | `docs/superpowers/plans/2026-10-02-launch-deploy.md` | ✅ |
| A2 | 生产端口安全：compose 中 MySQL 3306 / Redis 6379 目前映射到宿主机 `0.0.0.0`，公网可直连数据库与缓存 | `server/docker-compose.yml` 端口改绑 `127.0.0.1`（本地宿主机工具仍可用，公网不可达） | 待办 |
| A3 | `.env.example` 补齐 compose 引用但模板缺失的变量：`WX_MOCK_LOGIN` / `JWT_SECRET` / `WX_APPID` / `WX_SECRET` / `ADMIN_BOOTSTRAP_PASSWORD` | `server/.env.example` | 待办 |

### 验收标准

- A2：`server/docker-compose.yml` 中 mysql/redis 的 `ports` 均为 `127.0.0.1:` 前缀；server 服务端口映射不变
- A3：`server/.env.example` 覆盖 compose `environment:` 段引用的全部 `${VAR}` 变量（grep 交叉核对）
- 提交遵循 AGENT.md：显式 pathspec、Conventional Commits + 中文正文、提交前 `git status --short` 核对

## B. 需要人工办理 / 外部资源（列明等待条件）

| # | 事项 | 负责人动作 | 阻塞点 |
|---|---|---|---|
| B1 | 云服务器（2C4G+，装 Docker） | 购买并交付 SSH 信息 | 预算/选型 |
| B2 | 域名 + ICP 备案（周期约 1–2 周，**最先启动**） | 注册域名 → 云厂商提交备案 | 备案审核 |
| B3 | HTTPS 证书 | 云厂商免费证书或 Let's Encrypt，Nginx 反代 8080 | 依赖 B2 |
| B4 | 生产 `server/.env` 真实凭据 | `WX_MOCK_LOGIN=false` + 真实 `WX_APPID/SECRET`、强随机 `JWT_SECRET`、必填强随机 `ADMIN_TOKEN`/`ADMIN_BOOTSTRAP_PASSWORD`、`EXPRESS_MOCK=false` + 快递100 key/customer/公网回调、订阅消息模板 | 各平台注册审批 |
| B5 | 小程序类目资质确认 | 微信后台确认维修/二手回收/二手出售类目可过审 | 资质材料 |
| B6 | `config/index.js` 体验版/正式版 `apiBaseUrl` | 替换两处 TODO 域名 | 依赖 B2/B3 |
| B7 | 客服电话 TODO | 替换 `config/index.js` 的 `servicePhone` | 用户提供 |
| B8 | MySQL 定时备份 | 服务器上配 cron `mysqldump` | 依赖 B1 |
| B9 | 微信支付商户号 + API 证书 | 商户平台开通（商家转账到零钱单独签约） | 资质审核 |

## C. 上线流程（B 项就绪后执行）

1. 服务器 `cd server && docker compose up -d --build`，用真实凭据验证登录/下单/回调
2. 开发者工具「上传」→ 后台设为体验版（`trial` 环境自动生效）→ 真机全流程自测
3. 提交审核（类目 B5 已确认）→ 发布（`release` 环境自动生效）
4. 支付/打款真实化（B9），先行灰度验证一单

## 风险与注意

- **compose 改动兼容性**：改绑 `127.0.0.1` 后，若有人从**局域网其他机器**连开发库会失败；本机不受影响（AGENT.md §8 本机即开发机）
- 不把任何真实密钥写进 `.env.example`、文档或代码（AGENT.md 硬约束 3）
- `server/.env` 已被 gitignore，本次不读取、不提交其真实值
