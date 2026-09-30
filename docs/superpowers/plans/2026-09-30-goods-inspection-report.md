# 商品质检报告（转转式）实施计划

日期：2026-09-30 ｜ 分支：`feature/recycle-mvp` ｜ 状态：**执行中**
前置：`7cf4604`（出售端机型属性增强 1-4 项）｜ 规范：`AGENT.md`

## 1. 背景与目标

出售商品详情页目前只有「大图 / 价格 / 库存 / 名称 / 商品说明」，缺少二手交易最关键的**信任信息**：
成色分级、损伤明细、质检报告。用户明确要求对标转转——**详情页有质检报告小卡片，点击打开完整报告**。

**目标**：建立「商品 ↔ 质检报告（报告头 + N 条检查项）」数据模型，打通
「管理端录入报告 → 用户端详情页卡片 → 完整报告页」全链路。

## 2. 现状

| 层 | 已有 | 缺口 |
|---|---|---|
| 表 | `goods`（含 brand/storage/condition_level/tags）、`inspection`（**回收订单**质检，1:N 订单） | 无「出售商品」质检报告 |
| 用户端 | `GET /api/wx/goods`（筛选/排序）、`GET /api/wx/goods/{id}` | 无报告接口 |
| 管理端 | `/api/admin/goods` CRUD | 无报告录入 |
| 小程序 | `pages/sale/detail` 仅展示基础字段 | 无成色/损伤/报告 |

> 注意：`inspection` 表挂在**回收订单号**上，语义与出售商品质检不同，**不复用**，另建表。

## 3. 非目标

- 报告图片的人工标注/画框（只存 COS 图片 URL 列表）。
- 质检项模板的版本化管理（当前由管理端自由增删项）。
- 质检与回收流程的自动衔接（回收机器自动生成出售质检报告）。
- 真实微信支付。

## 4. 数据模型

### 4.1 `goods_inspection`（报告头，与商品 1:1）

| 列 | 类型 | 说明 |
|---|---|---|
| `id` | BIGINT AI | 主键 |
| `tenant_id` | BIGINT NOT NULL DEFAULT 0 | 租户 |
| `goods_id` | BIGINT NOT NULL | 商品 ID（UNIQUE） |
| `report_no` | VARCHAR(32) NOT NULL | 报告编号（`Q` 前缀，业务生成） |
| `inspector` | VARCHAR(32) | 质检工程师 |
| `inspected_at` | DATETIME | 质检时间 |
| `battery_health` | INT | 电池健康度 0-100 |
| `summary` | VARCHAR(512) | 质检综合结论 |
| `images` | VARCHAR(1024) | 报告图片 URL，英文逗号分隔 |
| `create_time` / `update_time` | DATETIME | 审计 |

### 4.2 `goods_inspection_item`（检查项，与商品 1:N）

| 列 | 类型 | 说明 |
|---|---|---|
| `id` / `tenant_id` / `goods_id` | | 归属 |
| `category` | VARCHAR(16) NOT NULL | 分组：外观 / 屏幕 / 功能 / 拆修 |
| `item_name` | VARCHAR(32) NOT NULL | 检查项名（如 屏幕显示） |
| `item_result` | VARCHAR(32) NOT NULL | 结论（正常 / 轻微划痕 / 已更换 / 异常 …） |
| `item_note` | VARCHAR(128) | 补充说明（损伤位置描述） |
| `sort_no` | INT NOT NULL DEFAULT 0 | 展示排序 |

**成色唯一来源**：整体成色沿用 `goods.condition_level`，报告表不再冗余该列，
避免两处不一致；报告视图 DTO 里带上它供报告页展示。

### 4.3 迁移
两张表都是**新建表**，`CREATE TABLE IF NOT EXISTS` 对存量库同样生效，
**无需** patch 脚本（与「加列」场景不同）。

## 5. 接口

```
POST   /api/admin/goods/{id}/inspection   保存报告（整体覆盖；items 为空则清空报告）
GET    /api/admin/goods/{id}/inspection   管理端读取（含检查项）
GET    /api/wx/goods/{id}/inspection      用户端读取（含检查项 + 商品成色）
```

返回 DTO `GoodsInspectionView`：
```
{ conditionLevel, reportNo, inspector, inspectedAt, batteryHealth, summary,
  images: [url...], items: [{category,name,result,note}],
  normalCount, abnormalCount }
```
无报告时 `data` 为 `null`（前端据此展示「质检报告整理中」态）。

## 6. 界面

### 6.1 小程序商品详情（`pages/sale/detail`）
1. **机型参数**卡片：品牌 / 内存 / 成色 / 标签。
2. **质检报告小卡片**（转转式）：
   - 左侧绿色盾牌图标 + 「RePhone 官方质检」
   - 成色大字 + 电池健康度 + 报告编号
   - 「全部 N 项检测 · 其中 M 项需说明」摘要
   - 最多 3 条非「正常」检查项（部位 + 结论）
   - 右侧「查看完整报告 ›」，点击进入报告页
   - 无报告时降级为灰态提示，不影响下单
3. **损伤说明**：非正常项汇总。

### 6.2 完整报告页（新增 `pages/sale/inspection/index`）
顶部报告编号/质检时间/工程师 → 成色与电池健康度 → 按 `category` 分组的检查项列表
（正常项绿标，异常项橙标 + 说明）→ 综合结论 → 报告图片预览。

### 6.3 管理端（`admin/src/views/Goods.vue`）
商品表格新增「质检报告」操作 → 弹窗：报告头字段 + 动态检查项表格（增删行、分组、结论、说明）。

## 7. 提交拆分（1 开发 1 提交）

| # | 提交 | 内容 |
|---|---|---|
| 0 | `docs: 新增商品质检报告（转转式）实施计划` | 本文件 |
| 1 | `feat(goods): 新增商品质检报告表与实体` | schema 两表 + 两个实体 + 两个 Mapper |
| 2 | `feat(goods): 质检报告保存/读取服务与端上接口` | View/SaveRequest DTO、Service、admin 与 wx 接口 |
| 3 | `test(goods): P6 补充质检报告用例` | 保存→用户端读取→覆盖→清空→越权 |
| 4 | `feat(admin): 商品质检报告录入与查看` | api/admin.js + Goods.vue |
| 5 | `feat(sale): 详情页质检报告卡片与完整报告页` | 详情页改造 + 新报告页 + app.json |

## 8. 验收

1. `cd server && ./mvnw.cmd -o test` 全绿（含新增用例）。
2. `cd admin && npm run build` 通过。
3. 管理端为商品录入报告（含正常项与损伤项）→ 小程序详情页出现质检卡片 →
   点击进入报告页可见分组检查项与综合结论。
4. 未录入报告的商品详情页显示降级态，且不影响下单。

## 9. 风险

| 风险 | 对策 |
|---|---|
| 报告为空时详情页空白 | 卡片做降级态；详情其他内容不依赖报告 |
| 检查项无限增长拖慢详情 | 详情卡片只取前 3 条非正常项；完整报告页才全量加载 |
| 与回收质检 `inspection` 混用 | 表名 `goods_inspection`，注释写明区别 |
| 并行会话改后端 | 严格显式 pathspec，只碰 goods/inspection 相关文件 |

## 10. 进度

- [ ] 1 表与实体
- [ ] 2 服务与接口
- [ ] 3 测试
- [ ] 4 后台录入
- [ ] 5 小程序卡片与报告页
