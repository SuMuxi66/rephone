# 计划：管理端机型参数维护（2026-10-26）

## 落地情况（已完成）

- 后端 4329474：spec 全量保存/回显/noSpec 过滤/校验，AdminModelSpecTest 5/5 + 回归 18/18。
- 前端 f77e2bb：参数列（缺失/完整/n/12）+「只看缺参数」+ 12 字段编辑对话框，SFC 静态编译过。
- 开发容器重建后真接口冒烟通过：admin 登录 → noSpec 命中 → 保存 → 回显（含 null 清空）→ 40054 拒绝 → 还原。
- 附带 f689d77：补提 ZOL 爬虫匹配修复（三星/苹果前缀、循环剥前缀、无条件剥尾括号）。
- admin UI 查看：cd admin && npm run dev（5173，admin/admin123456）。

## 目标

让运营在管理后台直接查看/补录/修正机型的 12 个参数字段（series/release_month/chip/ram/storage/battery_mah/charge_w/wireless_w/screen_size/resolution/refresh_rate/rear_camera），覆盖爬虫未命中的 ~618 行。

## 范围

- 后端：`AdminModelItem` 增加嵌套 `AdminModelSpec`；分页接口返回 spec 并支持 `noSpec=true` 过滤（只看缺参数，复用 noImage 模式）；新增 `PUT /api/admin/model/{id}/spec` 全量保存（显式 LambdaUpdateWrapper set，含 null 清空——规避 updateById 置 null 假成功陷阱）；校验镜像 DB 列长与数值范围。
- 前端（admin/src/views/Models.vue + api/admin.js）：表格加「参数」列（n/12 完整度）；头部「只看缺参数」勾选；操作列「参数」按钮打开 12 字段编辑对话框；新增机型走原流程，建完点「参数」补录。
- 测试：新类 `AdminModelSpecTest`（独立 H2 库 `mspec` + 静态 admin token + API 自种数据），覆盖保存/回显/校验拒绝/noSpec 过滤/清空语义。

## 非目标

- 小程序端参数展示（后续）。
- 批量导入 UI（数据仍走 data-model-specs.sql seed）。

## 验收标准

- `AdminModelSpecTest` 通过；`P2QuoteFlowTest` 回归通过。
- 前端 SFC 静态编译通过（@vue/compiler-sfc，不跑 vite build——admin/dist 清空风险）。
- 数据一致性：管理端改值后，seed 重放（chip IS NULL 才填）不覆盖人工修改。

## 风险

- record 组件顺序变更影响既有构造调用点（仅 AdminModelService 一处，同步改）。
- spec 保存与 seed guarded UPDATE 的互斥依赖 chip 字段语义：清空 chip 会使其重新可被 seed 填充（注释说明，可接受）。
