# 回收估价页分步卡片化改造（2026-09-30）

## 目标

回收估价页（pages/recycle/estimate）由「cell+弹层 picker+长表单一次铺开」改为
**分步渐显的卡片式问答**：答完第 1 题才出现第 2 题，选项全部卡片化（转转式）。

## 范围

- 仅前端 `pages/recycle/estimate/index.{js,wxml,wxss,json}`，纯展示层重排
- 提交契约不变：表单字段 brandId/modelId/storage/condition/screen/issues、
  `POST /api/wx/quote/calculate` 载荷、`recycle.quoteResult` storage 结构均不动

## 交互设计

6 个问题区块按序渐显，区块头带序号/已完成值：

1. 选择品牌：logo 卡片栅格（3 列，无 logo 用首字占位）
2. 选择机型：图片卡片栅格（复用原弹层 model-grid 样式，改为内联展示）
3. 选择内存：胶囊 chip
4. 成色：4 张「标签+说明」大卡（2 列）
5. 屏幕状态：同上
6. 功能问题（多选）：分组 chip，可多选

- 每次选择后自动滚动到新出现的区块
- 重选上游（如改品牌）级联重置下游并收起下游区块（沿用现有 selectBrand 重置逻辑）
- 顶部 t-steps 三大步（选机型/描述成色/获取报价）与 stepCurrent 逻辑保留

## 非目标

- 不改 result/create 页面，不改后端
- 不做机型搜索（列表内），后续有需要另开任务

## 验收

- JS `node --check`（ESM）+ JSON `JSON.parse` + WXML 标签配对检查通过
- app.json 不涉及；组件引用 json 与 WXML 实际用量一致
- 明确声明：未做开发者工具运行时验证

## 风险

- wx.pageScrollTo 滚动定位在低版本基础库可能无效——用 createSelectorQuery
  计算 scrollTop，失败静默（不影响主流程）
- 样式全部走 design-tokens 令牌，禁止裸色值（AGENT.md §2.7）
