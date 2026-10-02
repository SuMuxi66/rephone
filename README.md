# RePhone · 手机维修 / 二手机回收 / 二手出售平台

RePhone 是一个三端同仓的全栈项目：**微信小程序（用户端）+ Spring Boot（后端）+ Vue 3（管理后台）**，
覆盖手机维修（主业务）、二手机回收、二手机出售三条业务线。

## 三端架构

| 端 | 目录 | 技术栈 |
|---|---|---|
| 微信小程序 | 仓库根（`app.json` / `pages/` / `components/`） | 原生小程序 + tdesign-miniprogram 1.9.5 + dayjs |
| 后端 | [`server/`](server/) | Spring Boot 3.5 + Java 21 + MyBatis-Plus + MySQL 8 + Redis（Maven 多模块） |
| 管理后台 | [`admin/`](admin/) | Vite + Vue 3 + Element Plus + axios |

## 功能概览

- **维修**：故障报价 → 下单（到店 / 寄修，寄修含双向运单流转与物流轨迹）→ 工单列表/详情
- **回收**：在线估价 → 下单 → 填运单号 / 预约快递 → 质检报告 → 确认打款（微信商家转账）
- **出售**：商品列表 / 详情 / 下单（复用 TDesign 零售模板改造，接后端）
- **管理后台**：订单管理（回收/维修/出售）、质检录入、打款、RBAC 权限、租户管理、财务对账、数据看板
- **通知与物流**：快递100 下单与轨迹回调（本地快照缓存，TTL 30 分钟）、微信订阅消息推送
- **多租户**：所有业务表带 `tenant_id`，后端租户插件自动隔离

## 目录结构

```
RePhone
├── app.js / app.json          # 小程序入口与全局配置
├── pages/                     # 小程序页面（home / sale / repair / recycle / usercenter）
├── packages/retail-template/  # 零售模板分包（商品/订单/售后等）
├── components/ custom-tab-bar/ style/   # 公共组件、自定义 tabBar、设计令牌
├── common/                    # 状态码映射、地址预填 Behavior 等公共逻辑
├── services/                  # 小程序请求层
├── model/                     # 模板 mock 契约参考层（保留，勿删）
├── server/                    # Spring Boot 后端（Maven 多模块，自带 mvnw）
│   ├── common / pojo / mapper / service / controller / wechat / express
│   ├── start/src/main/resources/db/   # 建表脚本（幂等）+ patch-*.sql
│   ├── docker-compose.yml     # MySQL + Redis + server 一键编排
│   └── .env.example           # 密钥配置模板
├── admin/                     # Vue 3 管理后台
└── docs/                      # 审计报告、计划与设计文档
```

## 快速开始

### 1. 后端（Docker 一键起）

```bash
cd server
cp .env.example .env        # 填入 MySQL 密码；真实微信/快递100 密钥可选，缺省走 mock
docker compose up -d --build
```

- 服务端口 `8080`，MySQL `3306`，Redis `6379`
- 未配置真实凭据时自动使用 mock 模式（登录 / 快递100 / 支付 / 订阅消息），可完整跑通业务闭环
- 本地跑测试：`cd server && ./mvnw test`（Windows 用 `mvnw.cmd test`）

### 2. 微信小程序

1. `npm install`
2. 微信开发者工具导入仓库根目录，执行「构建 npm」
3. [config/index.js](config/index.js) 中切换 `env`，开发态 `apiBaseUrl` 默认 `http://localhost:8080`

### 3. 管理后台

```bash
cd admin
npm install
npm run dev        # http://localhost:5173，/api 代理到 localhost:8080
```

## 密钥与安全

- 所有密钥（appid/secret/mchid/快递100 key/ADMIN_TOKEN 等）**只**放 `server/.env`（已被 gitignore），
  新增配置同步补到 `server/.env.example` 的空值行
- 小程序端不含任何密钥，三方服务（支付、快递100、订阅消息、COS）一律由后端代理
- 金额后端统一以「分」(BIGINT) 存储与传输，前端展示层负责分/元转换

## 协作规范

- 详见 [AGENT.md](AGENT.md)（强制阅读）：提交粒度、状态码/设计令牌唯一来源、验证要求等
- 开发计划：[AI_PLAN.md](AI_PLAN.md)（P0–P7 分阶段）、`docs/superpowers/plans/`
- 提交信息遵循 Conventional Commits + 中文正文，scope 常用：`sale` `recycle` `repair` `admin` `order` `auth`

## License

[MIT](LICENSE)
