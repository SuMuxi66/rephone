# 首页改版：专业二手机交易平台（2026-10-01）

## 目标

pages/home 从"维修铺风格"升级为"专业二手机交易平台"首页；同步清理测试数据、
统一配色体系（design-tokens v4）。

## 需求映射

| 需求 | 落点 |
|---|---|
| 信任条（官方质检·一机一报告·180天质保·顺丰包邮·验收后付款） | home 搜索栏下横向 trust-bar |
| Banner 轮播×3（卖旧机/修手机/买二手机） | swiper，渐变卡+文案+CTA，替换原 masthead |
| 三大入口横排大卡 | 卖旧机/修手机/买二手机：图标+主副标题+按钮 |
| 清理冒烟商品→真实机型 | goods 表：删 191232，种 5 款真机（成色/内存/标签/划线价/图） |
| 删零元机型（iPhone 3G/Mate 8 等） | HomeService 只返回有基准价的机型，按价降序取前 8（高价值热门） |
| 商品卡：成色标签/质检标签/划线价/真实手机图 | home 严选卡 + sale 货架卡；图 = AI 白底产品图入 data/img/goods/ |
| 配色：深空灰/科技蓝主色+橙强调+白/浅灰底 | design-tokens v4（唯一来源，全局生效） |
| 底部信任栏简化 | 一行：官方质检 · 顺丰包邮 · 验收后付款 + 客服 |

## 技术要点

- 图片：compose 增挂 `./data:/app/data` 卷（上传与预置图重建不丢）；
  前端新增 `common/image-url.js` 把 `/img/...` 拼成 apiBaseUrl 全 URL（当前缺失）
- 商品种子：db/data-goods-demo.sql，INSERT...WHERE NOT EXISTS 幂等，加入 schema-locations
- 回收行情：HomeService 按 rule_type=10 基准价过滤 0/无价并全库降序取前 8

## 非目标

- 不改回收/维修/下单流程逻辑；不接真实微信支付；不引入新组件库

## 验收

- API：/api/wx/home 无 ¥0 行情；/api/wx/goods 返回成色/标签/划线价/图片路径；/img/** 200
- wechatide 编译 + 首页截图 + console 无报错
- 明确声明：真机未验
