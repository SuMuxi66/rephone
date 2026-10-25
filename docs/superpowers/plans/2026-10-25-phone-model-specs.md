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
7. [ ] 后续路径（待用户选择）：a) 自写 zol 爬虫脚本抓 2022-2026 国行参数（覆盖最全，需反爬处理）；b) 逐轮手动分批收集入库；c) 子代理换用可联网会话重跑。

## 落地情况（2026-10-25）

- 开发库：1572 行机型，691 行（44%）已有参数；Excel 参数表 304 款 100% 覆盖。
- data-model-specs.sql 分段来源可溯：Excel 参数表 / 子代理·模型知识 / GSMArena 镜像。
- 管理端 DTO 未动；quote 前端不受影响；表字段供后续 admin UI 使用。

## 验收标准

- Excel 全部 304 款在库内可按名称命中且参数非空（组合行冲突除外，另行报告）。
- `P2QuoteFlowTest` 通过（H2 加载 data-model-specs.sql 无错）。
- 收集数据来源可溯（每个分段注释来源与日期），无法核实的一律 NULL，禁止臆造。

## 风险

- 机型名称跨源写法差异（前缀/空格/变体合并行）→ 归一化匹配 + 冲突跳过并报告。
- 子代理检索质量参差 → 限定「查不到就 NULL」，字段级校验（数值字段非数字即拒收）。
- 荣耀等品牌含大量非手机条目 → 名称黑名单排除。
