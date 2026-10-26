# 计划：机型参数字段 + 详细参数灌库（2026-10-25）

## 目标

1. `phone_model` 表增加 12 个参数字段（对应《2020-2026 主力机型参数表》的 14 列：品牌/型号已有，发布时间拆为 release_year(已有)+release_month）。
2. 用户提供的参数表（304 款）参数全部入库。
3. 多子代理并行收集库内其余机型的参数，分批入库，直至覆盖全部「手机」类机型。

## 范围

- `schema-quote.sql`：`phone_model` 建表语句加列（新库/H2 直接生效）。
- `patch-phone-model-spec.sql`：已有 MySQL 库手动执行一次的 ALTER 补丁（仓库既有惯例）。
- `data-model-specs.sql`：参数数据（guarded UPDATE：仅当 `chip IS NULL` 时填充，幂等且不覆盖后台人工修改），挂入 `application.yml` sql-init，并加进 P2QuoteFlowTest 的 schema-locations 做 H2 运行时验证。
- `PhoneModel` 实体补字段（管理端 AdminModelItem DTO 不动，接口形状不变）。
- 子代理收集结果以同格式分段追加进 `data-model-specs.sql`，逐批提交。

新字段：`series`、`release_month`、`chip`、`ram`、`storage`、`battery_mah`、`charge_w`、`wireless_w`(0=不支持)、`screen_size`、`resolution`、`refresh_rate`、`rear_camera`。

## 非目标

- 管理后台参数录入/展示 UI（后续单独做）。
- 机型图片。
- 非手机类条目（荣耀笔记本/平板/MagicBook/MagicPad、nubia 豆包助手预览版）不收集参数。

## 任务拆分

1. [x] 表结构 + 补丁 + 实体 + 测试配置（提交 4460156）
2. [x] Excel 304 款参数生成并接入（311 行 guarded UPDATE，1 条合并行冲突跳过）
3. [x] P2QuoteFlowTest H2 实测（7/7 通过）
4. [x] 开发库（docker MySQL）应用补丁与数据（691/1572 行有参数）
5. [x] 修复历史 bug：原「红米」分区 83 款因从未创建该品牌而静默失效，已并入 Redmi（连带发现 2026-10-24 补充的 REDMI 15 / Redmi K70 电竞版同样未真正入库）
6. [~] 子代理收集（进行中）：
   - 试点 4 代理：侦察 + Apple-1 + 三星-1/2。**关键约束：子代理环境无 WebSearch 工具**，仅 WebFetch（apple.com.cn 可用，zol/GSMArena 403/404），其余靠模型知识（来源已如实标注）。
   - 已入库：Apple 50 + 三星 66（模型知识）+ GSMArena 镜像 foykes/gsm-arena-dataset 批量导入 270（老机型，正则抽取+越界拒绝）。
   - 未覆盖：约 880 行（主要是 2022-2026 国产机型与低端长尾）。
7. [~] 后续路径（用户选定 a：自写爬虫）：
   - 探测结论：detail.zol.com.cn 详情页可直连；搜索/目录页反爬锁定；百度百科 403；百度/360/搜狗不稳定。
   - 可用链路：search.zol.com.cn/s/all.php?keyword= 搜索（标题归一化精确匹配）→ 详情页取 /<catalog>/<id>/param.shtml → 解析参数表。
   - tools/fetch-zol-specs.py：全量 ~865 款后台爬取，限速 1.0-1.6s/请求，JSONL 断点续跑，小样 15 款 11 成功。
   - 产出经 tools/ingest-model-specs.py 入库（来源标注 ZOL 参数页）。

## 落地情况（2026-10-26 第二次更新）

- 开发库：1572 行机型，1109 行（**70.5%**）已有参数。
- 第四批 f6bd863：ZOL 品牌专区目录直连——mobile.zol.com.cn/manu_<id>.shtml 专区页是全量产品目录
  （product-title 标记，9 专区 1992 款），本地归一化+品牌推断精确匹配 173 款，参数抓取 169/169 全成（搜索命中率仅 40%）。
- 剩余 463 行 = 69 非手机类目（笔记本/平板/MagicBook，无参数需求）+ 394 手机缺口：
  Redmi 82 + realme 29（zol 子品牌无专区页）、荣耀 118/华为 67/vivo 63/OPPO 41 中大部分为
  变体后缀（活力版/青春版/畅玩）与 zol 未收录老款——统一走管理端「只看缺参数」人工补录。
- 完整工具链：fetch-zol-specs.py（搜索）/ fetch-zol-specs-retry.py（变体补救）/
  fetch-zol-catalog-specs.py（目录直连，命中率最高）/ ingest-model-specs.py（入库）。

## 验收标准

- Excel 全部 304 款在库内可按名称命中且参数非空（组合行冲突除外，另行报告）。
- `P2QuoteFlowTest` 通过（H2 加载 data-model-specs.sql 无错）。
- 收集数据来源可溯（每个分段注释来源与日期），无法核实的一律 NULL，禁止臆造。

## 风险

- 机型名称跨源写法差异（前缀/空格/变体合并行）→ 归一化匹配 + 冲突跳过并报告。
- 子代理检索质量参差 → 限定「查不到就 NULL」，字段级校验（数值字段非数字即拒收）。
- 荣耀等品牌含大量非手机条目 → 名称黑名单排除。
