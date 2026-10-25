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

## 落地情况（2026-10-25 最终）

- 开发库：1572 行机型，954 行（**60.7%**）已有参数；Excel 参数表 304 款 100% 覆盖。
- data-model-specs.sql 分段来源可溯：Excel 参数表(311) / 子代理·Apple(50)+三星(66) / GSMArena 镜像(270) / ZOL 参数页(271)。
- ZOL 爬取战果：主跑 802 款命中 299，三轮补救再 +11，合计 271 入库（其余为重复/越界拒收）。
- 未覆盖 ~618 行 = zol 搜索索引未收录（2013-2017 老款如麦芒/畅享/青春版变体、2026 新品如 Galaxy Z Flip7/iQOO 15T、变体 SKU）+ 管理后台后续人工补录。
- ZOL 搜索怪癖（已固化进 tools/fetch-zol-specs-retry.py）：无空格关键词常返回空页、型号内部空格致标题失配、「中文厂牌+英文厂牌+空格+型号」才命中部分老款、title_norm 需无条件剥尾括号、渐进缩短关键词+标题精确匹配是唯一可靠姿势。
- 管理端 DTO 未动；quote 前端不受影响；表字段供后续 admin UI 使用。

## 验收标准

- Excel 全部 304 款在库内可按名称命中且参数非空（组合行冲突除外，另行报告）。
- `P2QuoteFlowTest` 通过（H2 加载 data-model-specs.sql 无错）。
- 收集数据来源可溯（每个分段注释来源与日期），无法核实的一律 NULL，禁止臆造。

## 风险

- 机型名称跨源写法差异（前缀/空格/变体合并行）→ 归一化匹配 + 冲突跳过并报告。
- 子代理检索质量参差 → 限定「查不到就 NULL」，字段级校验（数值字段非数字即拒收）。
- 荣耀等品牌含大量非手机条目 → 名称黑名单排除。
