# 小程序维修主导改版（方案A双服务首页）Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把小程序从"回收落地页"改版为"维修主导、回收第二"的双服务首页，tab 换序为 首页/维修/回收/我的，并给维修页空态补服务承诺区。

**Architecture:** 纯前端静态改版，零后端改动。首页 hero 由回收改为维修主卡，回收降级为白底次卡；热门维修为前端静态常量（点击 switchTab）；热门机型数据流（`fetchHome()`，f28a83c 已接真库）原样保留。设计依据 `docs/superpowers/specs/2026-09-29-repair-first-home-design.md`。

**Tech Stack:** 微信原生小程序 + TDesign 1.9.5（tdesign-miniprogram npm 组件）、WXSS 设计令牌（`style/design-tokens.wxss`）。

## Global Constraints

- **并行工作区**：另一会话正在本仓库改后端。提交必须显式 pathspec：`git commit <文件列表> -m "..."`；禁止裸 `git commit`、禁止 `git add -A`。
- **Mimosa 提交门**：被拦时先等几分钟重试一次（ledger 登记机制）；仍拦才用 stash 窗口期（把涉事文件移到 `E:\code-start\_rephone_npm_stash\` → 提交 → **立即移回**）。不改 `.mimosa/` 下任何文件。
- **设计令牌**：`--brand: #FF5F15`、`--brand-light`、`--bg-card`、`--bg-page`、`--radius-card`、`--space-page`、`--text-1/2/3`（定义在 `style/design-tokens.wxss`）。wxml 属性里不能用 CSS 变量，t-icon 的 `color` 属性硬编码 `#FF5F15`。
- **图标白名单**（仅用已在本仓库出现过的图标，避免渲染空白）：默认字体 `tools`、`check-circle-filled`、`service`；wr 字体 `search`、`deliver`、`wallet`、`navigation`。
- **tabBar 双文件同步**：`app.json` 的 `tabBar.list` 顺序与 `custom-tab-bar/data.js` 的数组顺序必须一致（custom-tab-bar/index.js 按 route findIndex 高亮，无索引硬编码，换序安全）。
- **tab 页底部留白**：`pages/repair/index.wxss` 的 `page { padding-bottom: calc(env(safe-area-inset-bottom) + 160rpx) }` 是自定义 tabBar 浮层的避让规则，保留勿动。
- **文案**：tab 名"估价"改"回收"（pagePath 不变）；全局导航标题改"RePhone 手机维修·回收"。
- 本改版无逻辑变更，无单测可写；每个任务的验证 = 结构校验命令 + 最终开发者工具人工走查（Task 4）。e2e/security/H2 均为 API 层，无后端改动不需重跑（并行会话后端 WIP 期间更不要跑，避免误报）。

---

### Task 1: Tab 层换序与改名

**Files:**
- Modify: `app.json`（tabBar.list、window.navigationBarTitleText）
- Modify: `custom-tab-bar/data.js`（数组重排）

**Interfaces:**
- Produces: tab 顺序 `首页→维修→回收→我的`；`custom-tab-bar/data.js` 导出数组第 2 项为 `{ icon:'tools', text:'维修', url:'pages/repair/index', prefix:'' }`（Task 2 的 switchTab 目标 `/pages/repair/index` 依赖此页为 tab 页）。

- [ ] **Step 1: 修改 app.json 的 tabBar.list 与标题**

把 `app.json` 中 `"tabBar"` 的 `"list"` 数组整体替换为（text"估价"→"回收"，路径不变，仅顺序调整）：

```json
    "list": [
      {
        "pagePath": "pages/home/home",
        "text": "首页"
      },
      {
        "pagePath": "pages/repair/index",
        "text": "维修"
      },
      {
        "pagePath": "pages/recycle/estimate/index",
        "text": "回收"
      },
      {
        "pagePath": "pages/usercenter/index",
        "text": "我的"
      }
    ]
```

同文件 `"window"` 中：

```json
    "navigationBarTitleText": "RePhone 手机维修·回收",
```

（原值 `"RePhone 二手机回收"`）

- [ ] **Step 2: 重写 custom-tab-bar/data.js**

文件整体替换为：

```js
export default [
  {
    icon: 'home',
    text: '首页',
    url: 'pages/home/home',
    prefix: 'wr',
  },
  {
    icon: 'tools',
    text: '维修',
    url: 'pages/repair/index',
    prefix: '',
  },
  {
    icon: 'wallet',
    text: '回收',
    url: 'pages/recycle/estimate/index',
    prefix: 'wr',
  },
  {
    icon: 'person',
    text: '我的',
    url: 'pages/usercenter/index',
    prefix: 'wr',
  },
];
```

- [ ] **Step 3: 验证 JSON 合法性与双文件顺序一致**

```cmd
node -e "JSON.parse(require('fs').readFileSync('app.json','utf8'));console.log('app.json OK')"
findstr /c:"pagePath" app.json
findstr /c:"url:" custom-tab-bar\data.js
```

Expected: 第一条输出 `app.json OK`；后两条的顺序必须一致：`pages/home/home` → `pages/repair/index` → `pages/recycle/estimate/index` → `pages/usercenter/index`。

- [ ] **Step 4: Commit（显式 pathspec）**

```cmd
git commit app.json custom-tab-bar/data.js -m "feat: tab 换序为首页/维修/回收/我的，估价改名回收"
```

若被 Mimosa 拦截：等 3 分钟重试一次；仍拦则按 Global Constraints 的 stash 窗口期流程。

---

### Task 2: 首页改版为维修主导双服务分区

**Files:**
- Modify: `pages/home/home.js`（整文件重写）
- Modify: `pages/home/home.wxml`（整文件重写）
- Modify: `pages/home/home.wxss`（文件末尾追加样式块）

**Interfaces:**
- Consumes: `fetchHome()`（`services/home/home.js`，返回 `{ hotModels }`，本任务不改）；storage key `recycle.prefillBrandId`（估价页读取，保留写入）。
- Produces: `goRepair()`（switchTab 到 `/pages/repair/index`）；data 项 `hotRepairs`（字符串数组）。

- [ ] **Step 1: 重写 pages/home/home.js**

```js
import { fetchHome } from '../../services/home/home';

const HOT_REPAIRS = ['换屏', '换电池', '进水', '不开机', '摄像头', '其他故障'];

Page({
  data: {
    pageLoading: true,
    hotRepairs: HOT_REPAIRS,
    hotModels: [],
  },

  onShow() {
    this.getTabBar().init();
  },

  onLoad() {
    this.loadHomePage();
  },

  onPullDownRefresh() {
    this.loadHomePage();
  },

  loadHomePage() {
    wx.stopPullDownRefresh();
    fetchHome().then(({ hotModels }) => {
      this.setData({ hotModels, pageLoading: false });
    }).catch(() => {
      this.setData({ pageLoading: false });
    });
  },

  goRepair() {
    wx.switchTab({ url: '/pages/repair/index' });
  },

  goEstimate() {
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },

  /** 热门机型 → 预选品牌后进入估价 */
  goEstimateWithBrand(e) {
    const { brandId } = e.currentTarget.dataset;
    if (brandId) {
      wx.setStorageSync('recycle.prefillBrandId', brandId);
    }
    wx.switchTab({ url: '/pages/recycle/estimate/index' });
  },
});
```

（相对原文件的差异：删除 `flowSteps` data；新增 `hotRepairs` 与 `goRepair`；`goEstimate`/`goEstimateWithBrand`/`loadHomePage` 原样保留。）

- [ ] **Step 2: 重写 pages/home/home.wxml**

```xml
<view class="page" wx:if="{{!pageLoading}}">
  <!-- 主推：维修 -->
  <view class="hero" bindtap="goRepair">
    <view class="hero-title">手机维修 · 上门快修</view>
    <view class="hero-sub">先报价后维修 · 修好验收付款 · 180天质保</view>
    <view class="hero-cta">立即报修</view>
  </view>

  <!-- 服务三保障 -->
  <view class="entries">
    <view class="entry" bindtap="goRepair">
      <t-icon name="tools" size="52rpx" color="#FF5F15" />
      <view class="entry-name">工程师上门</view>
    </view>
    <view class="entry" bindtap="goRepair">
      <t-icon name="check-circle-filled" size="52rpx" color="#FF5F15" />
      <view class="entry-name">180天质保</view>
    </view>
    <view class="entry" bindtap="goRepair">
      <t-icon prefix="wr" name="wallet" size="52rpx" color="#FF5F15" />
      <view class="entry-name">先报价后修</view>
    </view>
  </view>

  <!-- 热门维修 -->
  <view class="section-head">
    <text class="section-title">热门维修</text>
  </view>
  <scroll-view scroll-x class="models" enhanced show-scrollbar="{{false}}">
    <view wx:for="{{hotRepairs}}" wx:key="*this" class="repair-chip" bindtap="goRepair">
      {{item}}
    </view>
  </scroll-view>

  <!-- 次业务：回收 -->
  <view class="hero hero--secondary" bindtap="goEstimate">
    <view class="hero-title">旧机回收 · 高价秒估</view>
    <view class="hero-sub">顺丰包邮 / 上门取件 · 质检后快速打款</view>
    <view class="hero-cta">免费估价</view>
  </view>

  <!-- 热门机型 -->
  <view class="section-head">
    <text class="section-title">热门机型</text>
    <text class="section-more" bindtap="goEstimate">更多</text>
  </view>
  <scroll-view scroll-x class="models" enhanced show-scrollbar="{{false}}">
    <view
      wx:for="{{hotModels}}"
      wx:key="modelName"
      class="model-card"
      data-brand-id="{{item.brandId}}"
      bindtap="goEstimateWithBrand"
    >
      <view class="model-brand">{{item.brandName}}</view>
      <view class="model-name">{{item.modelName}}</view>
      <view class="model-price">最高 {{item.priceText}}</view>
    </view>
  </scroll-view>
</view>

<view wx:if="{{pageLoading}}" style="text-align: center; color: #b9b9b9; padding-top: 200rpx">
  <t-loading theme="circular" size="40rpx" text="加载中..." inherit-color />
</view>
```

（相对原文件：hero 主卡改为维修文案；原"流程三入口"改为服务三保障并全部指向 goRepair；新增热门维修 chips；原回收 hero 降级为 `hero--secondary`；热门机型区块原样保留；删除"回收流程"四步区块。）

- [ ] **Step 3: pages/home/home.wxss 末尾追加样式**

在文件末尾追加（`.hero`/`.entries` 等既有类复用，不改动；`.model-img`/`.model-ph` 系列原样保留）：

```css
/* ===== 维修主导改版（2026-09-29） ===== */
.hero--secondary {
  padding: 40rpx 48rpx;
  background: var(--bg-card);
  border: 2rpx solid var(--brand-light);
  color: var(--text-1);
}

.hero--secondary .hero-title {
  font-size: 36rpx;
  color: var(--text-1);
}

.hero--secondary .hero-sub {
  color: var(--text-3);
  opacity: 1;
}

.hero--secondary .hero-cta {
  background: var(--brand);
  color: #fff;
}

.repair-chip {
  display: inline-flex;
  align-items: center;
  padding: 16rpx 32rpx;
  margin-right: 20rpx;
  border-radius: 36rpx;
  background: var(--bg-card);
  color: var(--text-1);
  font-size: 28rpx;
  font-weight: 500;
}
```

- [ ] **Step 4: 结构校验**

```cmd
findstr /c:"flowSteps" pages\home\home.js pages\home\home.wxml
findstr /c:"goRepair" pages\home\home.js pages\home\home.wxml
findstr /c:"hotRepairs" pages\home\home.js pages\home\home.wxml
```

Expected: `flowSteps` 两文件均无输出；`goRepair` 与 `hotRepairs` 在 js（定义）与 wxml（引用）中均各至少 1 处。

- [ ] **Step 5: Commit（显式 pathspec）**

```cmd
git commit pages/home/home.js pages/home/home.wxml pages/home/home.wxss -m "feat: 首页改版为维修主导双服务分区"
```

---

### Task 3: 维修页空态升级为服务承诺区

**Files:**
- Modify: `pages/repair/index.wxml`（空态块替换）
- Modify: `pages/repair/index.wxss`（`.empty-tip` 改卡片 + 新增 promise 样式）
- Modify: `pages/repair/index.json`（移除 `t-empty`、新增 `t-icon`）

**Interfaces:**
- Consumes: 无（纯静态展示，不涉及 index.js 改动）；TDesign `t-icon` 组件（页面级注册，`app.json` 的 `usingComponents` 为空，必须在页面 json 注册）。

- [ ] **Step 1: 替换 pages/repair/index.wxml 的空态块**

把：

```xml
    <view wx:else class="empty-tip">
      <t-empty description="选择品牌和机型，查看专属维修价" />
    </view>
```

替换为：

```xml
    <view wx:else class="empty-tip">
      <view class="promise">
        <view class="promise-item">
          <t-icon prefix="wr" name="wallet" size="44rpx" color="#FF5F15" />
          <text>先报价后维修</text>
        </view>
        <view class="promise-item">
          <t-icon name="tools" size="44rpx" color="#FF5F15" />
          <text>品质配件</text>
        </view>
        <view class="promise-item">
          <t-icon name="check-circle-filled" size="44rpx" color="#FF5F15" />
          <text>180天质保</text>
        </view>
      </view>
      <view class="empty-guide">选择品牌和机型，查看专属维修价</view>
    </view>
```

（t-empty 在本文件中仅此一处使用，替换后该组件不再被引用。）

- [ ] **Step 2: 更新 pages/repair/index.json 的 usingComponents**

删除行：

```json
    "t-empty": "tdesign-miniprogram/empty/empty",
```

在 `"t-loading": ...` 行后新增：

```json
    "t-icon": "tdesign-miniprogram/icon/icon",
```

改后 `"usingComponents"` 应为：t-cell、t-cell-group、t-button、t-loading、t-icon、t-checkbox、t-checkbox-group、t-picker、t-picker-item、section-card。

- [ ] **Step 3: pages/repair/index.wxss 替换空态样式**

把：

```css
.empty-tip {
  padding: 120rpx 40rpx;
}
```

替换为：

```css
.empty-tip {
  margin: 32rpx var(--space-page) 0;
  padding: 64rpx 40rpx;
  background: var(--bg-card);
  border-radius: var(--radius-card);
  text-align: center;
}

.promise {
  display: flex;
}

.promise-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  color: var(--brand);
  font-size: 24rpx;
}

.empty-guide {
  margin-top: 40rpx;
  font-size: 26rpx;
  color: var(--text-3);
}
```

（`page` 的 tabBar 避让 padding-bottom 规则保留不动。）

- [ ] **Step 4: 结构校验**

```cmd
findstr /s /c:"t-empty" pages\repair\index.wxml pages\repair\index.json
findstr /c:"t-icon" pages\repair\index.json
node -e "JSON.parse(require('fs').readFileSync('pages/repair/index.json','utf8'));console.log('json OK')"
```

Expected: 第一条无输出（t-empty 已清除）；第二条 1 处命中；第三条输出 `json OK`。

- [ ] **Step 5: Commit（显式 pathspec）**

```cmd
git commit pages/repair/index.wxml pages/repair/index.wxss pages/repair/index.json -m "feat: 维修页空态升级为服务承诺区"
```

---

### Task 4: 开发者工具走查与收尾

**Files:**
- 无代码改动；发现问题则回到对应 Task 修复后重走其校验与提交步骤。

- [ ] **Step 1: 微信开发者工具人工走查清单**

1. 四个 tab 依次点击：顺序为 首页/维修/回收/我的，每页高亮正确（验证 app.json 与 data.js 同步无误）；
2. 首页：维修 hero 卡、服务三保障、热门维修 chips 点击均跳维修 tab；
3. 首页：回收次卡与"更多"跳估价页；热门机型点击跳估价页且品牌已预选（`recycle.prefillBrandId` 链路）；
4. 首页下拉刷新正常；hotModels 正常展示（f28a83c 真库接口）；
5. 维修页：未选机型时显示服务承诺区（三个卖点图标正常着色）；选机型后表单流程不受影响；
6. 首页、维修页内容可完整滑到底（tabBar 浮层避让）。

- [ ] **Step 2:（可选）CUA 截图辅助核对**

可用 computer-use 技能操控微信开发者工具截图核对首页/维修页/回收页三屏。要点：开发者工具无 a11y 树、点击有偏移、`project.private.config.json` 已设 `urlCheck:false`。

- [ ] **Step 3: 无遗留改动确认**

```cmd
git status --short
```

Expected: 仅剩并行会话的后端 WIP 文件（AdminModel*/HomeService/QuoteService/schema-quote.sql/WebResourceConfig/AdminUploadController 等），无本计划相关文件未提交。
