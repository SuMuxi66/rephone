# 出售端机型属性增强（转转式筛选）实施计划

日期：2026-09-30 ｜ 分支：`feature/recycle-mvp` ｜ 状态：**执行中**
配套规范：`AGENT.md` ｜ 前置：`bd572cf`（购买 tab + 货架/详情/下单）

## 1. 背景与目标

出售端已具备「货架 → 详情 → 下单」闭环，但商品只有 `name/image/价格/库存/描述` 五个可展示字段，
无法像转转那样按 **品牌 / 内存 / 成色** 筛选，货架信息密度不足。

**目标**：给 `goods` 增加机型属性字段，打通「管理端录入 → 后端筛选 → 小程序货架筛选/详情展示」全链路。

## 2. 现状（`bd572cf` 时点）

| 层 | 已有 | 缺口 |
|---|---|---|
| 表 | `goods(tenant_id,name,image,price_fen,original_price_fen,stock,status,desc_text)` | 无品牌/内存/成色/标签 |
| 用户端 | `GET /api/wx/goods`（返回**全量**在售 `List<Goods>`）、`GET /api/wx/goods/{id}` | 无任何筛选/排序入参 |
| 管理端 | `GET/POST/PUT /api/admin/goods`、`/{id}/status` | 无属性字段 |
| 后台 UI | `admin/src/views/Goods.vue` 表格 + 新增/编辑弹窗 | 无属性录入项 |
| 小程序 | `pages/sale/index`（**前端本地**搜索/排序）、`pages/sale/detail` | 依赖客户端过滤，无品牌/成色维度 |

## 3. 非目标（本次不做）

- 商品多图 / 视频、SKU 多规格、优惠券、评价。
- 货架**后端分页**（`/api/wx/goods` 仍返回全量数组，前端分批渲染；分页留待库存上量后单独提交，
  届时需同步修改 `P6SaleOrderTest` 对 `data` 为数组的断言）。
- 「回收 → 质检 → 自动生成在售库存」的自动化打通（业务规则未定，单独立项）。
- 真实微信支付。

## 4. 数据模型

`goods` 新增 4 列（全部可空，兼容存量数据）：

| 列 | 类型 | 说明 | 示例 |
|---|---|---|---|
| `brand` | VARCHAR(32) | 品牌 | `Apple` |
| `storage` | VARCHAR(16) | 内存/容量 | `256G` |
| `condition_level` | VARCHAR(16) | 成色 | `95新` |
| `tags` | VARCHAR(255) | 标签，英文逗号分隔 | `官方自营,已验机` |

Java 实体对应 `brand / storage / conditionLevel / tags`（MyBatis-Plus 下划线转驼峰）。

### 迁移策略（重要）

建表脚本用 `CREATE TABLE IF NOT EXISTS`，**不会给已存在的表加列**。

- **新库 / H2 测试库**：`schema-sale.sql` 里直接带新列，无需额外操作。
- **存量 MySQL 库**：手动执行 `server/start/src/main/resources/db/patch-goods-attrs.sql`（纯 `ALTER TABLE`），
  该文件**不**写进 `spring.sql.init.schema-locations`（MySQL 8 不支持 `ADD COLUMN IF NOT EXISTS`，H2 语义也不同，
  强行共用会破坏测试）。执行前请先 `SHOW COLUMNS FROM goods;` 确认缺失列。

## 5. 任务与提交拆分（1 开发 1 提交）

| # | 提交信息 | 主要文件 | 验收 |
|---|---|---|---|
| 1 | `feat(goods): 商品表与实体扩展品牌/内存/成色/标签字段` | `db/schema-sale.sql`、`db/patch-goods-attrs.sql`(新)、`pojo/entity/Goods.java` | `mvnw test` 编译通过；H2 建表含新列 |
| 2 | `feat(goods): 商品新增/编辑接口支持品牌/内存/成色/标签` | `GoodsService.java`、`AdminGoodsController.java` | 管理端可写入属性并回读 |
| 3 | `feat(goods): 在售列表支持关键词/品牌/成色筛选与排序，并新增筛选项接口` | `GoodsService.java`、`WxGoodsController.java` | 不同入参返回不同结果集；`/filters` 返回在售品牌/成色去重 |
| 4 | `fix(goods): 修正货架筛选项接口空值处理，补充 P6 属性与筛选断言` | `GoodsService.java`、`P6SaleOrderTest.java` | `mvnw test` 全绿 |
| 5 | `feat(admin): 商品管理支持品牌/内存/成色/标签录入与展示` | `admin/src/views/Goods.vue`（+`api/admin.js` 如需） | `npm run build` 通过；表单可保存/回填 |
| 6 | `feat(sale): 货架支持品牌/成色筛选，详情展示机型属性` | `pages/sale/index.*`、`pages/sale/detail/index.*` | 货架 chips 生效；详情显示 4 项属性 |

### 接口约定（第 2/3 项）

```
POST /api/admin/goods          body: {name,image,priceYuan,originalPriceYuan,stock,descText,brand,storage,conditionLevel,tags}
PUT  /api/admin/goods/{id}     body: 同上（仅传入字段生效）

GET  /api/wx/goods             query: keyword, brand, conditionLevel, sort=default|priceAsc|priceDesc|newest
                               → 仍返回 List<Goods>（不分页）
GET  /api/wx/goods/filters     → { "brands": ["Apple", ...], "conditions": ["95新", ...] }（仅统计在售且有货）
GET  /api/wx/goods/{id}        不变
```

筛选语义：`keyword` 模糊匹配 `name`/`brand`；`brand`/`conditionLevel` 精确匹配；空值表示不过滤。
`sort` 默认 `default`（id 倒序，即最新）。

## 6. 风险与对策

| 风险 | 对策 |
|---|---|
| 存量 MySQL 缺列导致运行期 SQL 报错 | 交付 `patch-goods-attrs.sql` + 在交付说明中显式提醒执行 |
| 并行会话正在改 `server/**/*.java` | 严格显式 pathspec 提交；只碰 goods 相关文件；发现他人改动不提交 |
| 测试断言与接口契约漂移 | 第 4 项专门补测试；改契约必须同提交内改测试 |
| 前端仍保留本地过滤造成双份逻辑 | 第 6 项改为调用后端筛选，删除本地过滤分支 |

## 7. 整体验收

1. `cd server && ./mvnw -q test` 全绿（H2，含新增断言）。
2. `cd admin && npm run build` 通过。
3. 联调：管理端录入「Apple / 256G / 95新 / 官方自营,已验机」的商品 → 小程序「购买」页可按品牌与成色筛选到它 → 详情页显示 4 项属性 → 下单成功。
4. 每个提交自洽（可编译、可运行）。

## 8. 进度

- [x] 1 表与实体扩展（`0b586ae`）
- [x] 2 管理端接口写入属性（`9d4f008`）
- [x] 3 用户端筛选与筛选项接口（`2e010ca`）
- [x] 4 筛选项空值修复 + P6 断言（`10650ca`）
- [ ] 5 后台录入 UI
- [ ] 6 小程序货架/详情

### 验证记录

- `cd server && ./mvnw.cmd -o test` → **28 个用例全部通过**（P1 1 / P2 7 / P3 4 / P5 5+3 / P6 5 / P7 3），BUILD SUCCESS。
- 第 3 项首次提交的 `onSaleFilterOptions` 在空值场景抛 NPE，已被第 4 项的 P6 用例捕获并修复——这正是「改契约必须同提交补测试」的价值。
- 尚未验证：管理端 `npm run build`（第 5 项）、小程序开发者工具编译（第 6 项）。
