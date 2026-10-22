# 用户行为画像 + 点击漏斗统计（2026-10-22）

## 目标

1. **点击漏斗统计**：商家端新增「点击统计」页，覆盖小程序启动、商品曝光→点击→下单转化、机型点击→回收/维修转化。
2. **用户行为画像**：长期记录「谁在几点打开了小程序」「浏览了哪些商品」，聚合出偏好（品牌/价格档）与消费力，AI（OpenAI 兼容接口）生成分析，商家端以用户卡片呈现（正面概览/背面 AI 分析/点开看明细）。

## 口径

### 事件（`click_event` 表，长期留存，不去重，PV 口径）

| event_type | 含义 | 小程序挂点 |
|---|---|---|
| 1 启动 | 冷启动一次记一条 | `app.js` onLaunch |
| 2 商品曝光 | 出售列表每次加载，返回的商品 id 集合记一条（分页各记各） | `pages/sale/index.js` reload 成功后 |
| 3 商品浏览 | 商品详情页打开一次记一条（=点击） | `pages/sale/detail/index.js` onLoad |
| 4 机型点击 | 选中机型记一条，page 带 biz 区分回收/维修 | `pages/model-picker/index.js` onPick |

商品事件由**后端按 goods_id 快照 brand/price_fen** 入事件行（防商品删除/改价影响长期分析）。

### 派生指标

- CTR = 商品浏览 / 商品曝光（曝光=接口返回即曝光，近似口径）
- 转化 = 窗口期新增单量：出售按 `sale_order.goods_id`、回收按 `recycle_order.model_id`、维修按 `repair_order.model_id`，不排除取消单
- 画像：打开次数/最近打开；偏好 = 品牌分布 + 价格档分布（<1000 / 1000–2000 / 2000–3500 / ≥3500 元）；消费力 = 浏览价格档 + 三线订单数

## 范围

- 后端：事件表 + 上报接口（`/api/wx/track/events`）+ 漏斗统计接口（`/api/admin/stats/click/*`）+ 画像接口（`/api/admin/user-profile/*`）+ AI 分析服务（OpenAI 兼容，`server/.env` 配置 `AI_BASE_URL/AI_API_KEY/AI_MODEL/AI_MOCK`）
- 小程序：`services/track.js` + 四处挂点（上报失败静默吞掉）
- admin：`ClickStats.vue`（点击统计）、`UserProfiles.vue`（用户卡片栅格 + CSS 翻转背面 AI + 抽屉明细）+ 权限点 `stats:click`、`profile:read`

## 非目标

- 真实视口曝光、行为去重、日粒度 rollup、事件清理任务
- 游客（无 token）行为统计；机型浏览进画像
- CloudBase AI 网关接入（走通用 OpenAI 兼容配置即可）

## 任务/提交拆分

- [x] 1. `docs(plan)`: 本计划文档
- [x] 2. `feat(stats)`: click_event 表 + schema/patch + 权限点（stats:click、profile:read）+ 上报接口 + 测试（ClickTrackTest 4/4）
- [x] 3. `feat(stats)`: 点击漏斗统计接口 summary/trend/top + 测试（ClickStatsTest 5/5）
- [x] 4. `feat(track)`: 小程序 services/track.js + 四处埋点（babel 静态校验过，未做 IDE 运行时验证）
- [x] 5. `feat(admin)`: 点击统计页 ClickStats.vue + 路由菜单权限（compiler-sfc 校验过）
- [x] 6. `feat(profile)`: 用户画像聚合接口 page/detail + 测试（UserProfileTest 5/5）
- [x] 7. `feat(admin)`: 用户画像卡片页 UserProfiles.vue + 路由菜单权限（compiler-sfc 校验过）
- [x] 8. `feat(ai)`: AiAnalysisService + 生成接口 + user_ai_profile 表 + 测试（AiAnalysisTest 4/4；全量回归 127/127）

注：全部代码与验证已完成；git 提交因 Mimosa 门禁对存量弱加密误报的强制拦截而暂缓，
待插件临时停用后按 1→8 顺序补提交（application.yml 的 AI 配置与 schema 登记随提交 8 落地）。

## 验收标准

- `cd server && ./mvnw test` 全量通过（新测试类：独立命名 H2 库 + 自种数据 + 钉 mock）
- admin 改动过 `@vue/compiler-sfc` 静态校验（不跑 vite build，规避 esbuild 破坏风险）
- 小程序改动过 `@babel/parser` / `JSON.parse` / WXML 标签配对静态检查
- 每个提交自洽可独立运行；显式 pathspec 提交

## 风险

- 曝光/点击为 PV 口径近似值，分析时注意口径
- 事件量增长后读时聚合变慢 → 后续 rollup 日表
- AI key 未配置时生成按钮友好报错，不影响其他功能
- 行为画像涉及终端用户数据，仅商家后台呈现，不对外
