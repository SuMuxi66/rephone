# 小程序维修主导改版设计（方案 A：双服务首页）

日期：2026-09-29 ｜ 分支：`feature/recycle-mvp` ｜ 状态：已经用户确认

## 背景与目标

平台定位调整：**主打维修，回收第二**。当前小程序与该定位存在三处错位：

1. 首页是纯回收落地页（主横幅、三入口、热门机型、流程全部指向回收），维修零曝光；
2. Tab 顺序为 首页/估价(回收)/维修/我的，回收排在维修前；
3. 维修页是纯表单工具页，选机型前缺服务感。

目标：维修成为首页主推位与第二 tab；回收降为第二业务位但保留完整转化链路（热门机型 + 品牌预选估价）。

## 范围

仅小程序前端，预计改动 7 个文件（app.json、custom-tab-bar/data.js、home 三件、repair/index 两件），**零后端改动**。

不做（明确排除）：机型级维修故障预选（v2 再做）、寄修服务（后端未上线）、微信支付、估价页内部文案统一（v2）。

## 改动明细

### 1. Tab 层（app.json + custom-tab-bar/data.js）

- `app.json` `tabBar.list` 重排为：首页 / 维修 / 回收 / 我的；`pages/recycle/estimate/index` 的 text 由"估价"改为"**回收**"（pagePath 不动）。
- `custom-tab-bar/data.js` 同步重排与改名。**两处必须同一次提交内同步**，否则高亮/切换错乱。
- `app.json` `window.navigationBarTitleText`："RePhone 二手机回收" → "**RePhone 手机维修·回收**"。

### 2. 首页改版（pages/home/home.wxml / home.js / home.wxss）

新结构自上而下：

1. **维修主卡（hero）**：标题"手机维修 · 上门快修"；卖点副题"先报价后维修 · 修好验收付款 · 180天质保"；CTA"立即报修" → `wx.switchTab('/pages/repair/index')`。
2. **服务三保障行**（改造原"流程三入口"，样式复用 `entries`）：工程师上门 / 180天质保 / 先报价后修，静态内容，点击均跳维修 tab。
3. **热门维修横滑**：换屏 / 换电池 / 进水 / 不开机 / 摄像头 / 其他故障。前端常量 `hotRepairs`（home.js data），点击 → switchTab 维修页。v1 不带机型/故障预选参数。
4. **回收次卡**（原 hero 降级为小卡）：标题"旧机回收 · 高价秒估"；副题"顺丰包邮 / 上门取件 · 质检后快速打款"；CTA"免费估价" → 既有 `goEstimate`。
5. **热门机型横滑**：原样保留 `hotModels` 渲染与 `goEstimateWithBrand`（storage `recycle.prefillBrandId` 品牌预选）。
6. **移除"回收流程"四步区块**及 `flowSteps` data（估价页首屏已有完整引导，首页减负）。

数据流：唯一接口 `fetchHome()` 不变，仅继续供 `hotModels`；失败时现有 `catch` → `pageLoading=false` 渲染静态区块，行为保留。

### 3. 维修页轻增强（pages/repair/index.wxml / index.wxss）

- `form.modelId` 为空时的空态区（现为裸 `t-empty`）升级为**服务承诺区**：三个卖点（先报价后维修 / 品质配件 / 180天质保）+ 原引导文案"选择品牌和机型，查看专属维修价"。
- 表单主流程（R1 已验证：品牌/机型选择、故障勾选、去预约）**不动**。

### 4. 不改动清单

`pages/recycle/*`、`pages/repair/{quote,create,order/*}`、`services/*`、`server/*`、`admin/*`、`custom-tab-bar/` 其余逻辑、分包（user/goods/order）。

## 错误处理与兼容

- 无新接口依赖；首页加载失败仍可渲染静态区块（现有 pageLoading 逻辑保留）。
- 自定义 tabBar 依赖各 tab 页 `onShow` 调 `getTabBar().init()`，换序不影响该机制。
- TabBar 换序的唯一风险是 `app.json` 与 `custom-tab-bar/data.js` 不同步——列为实施检查项。

## 验证方式

微信开发者工具人工走查清单：

1. 四个 tab 切换，顺序为 首页/维修/回收/我的，高亮正确；
2. 首页维修主卡、三保障、热门维修 chips 均跳维修 tab；回收次卡与热门机型跳估价页；
3. 热门机型 → 品牌预选 → 估价页品牌已选中（`recycle.prefillBrandId` 链路）；
4. 维修页空态显示服务承诺区；选机型后表单流程正常；
5. 首页下拉刷新正常。

回归：e2e 41 项 / security 42 项 / H2 集成测试均为 API 层，本改动无后端影响，无需重跑（如有疑虑可在提交后跑一遍 e2e 脚本确认）。
