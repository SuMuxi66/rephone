# 2026-10-01 主包瘦身：模板栈并入单一分包 + 死代码清理

> **状态：已完成（2026-10-01）。** 实际落地为 5 个提交：eb5a8d6（packOptions）、
> be150e7（删 order-group）、f8da5db（删死目录）、544c63e（大迁移）、804c021（wxss @import 修复）。
> 与原方案的差异：services/{recycle,repair,sale,subscribe,usercenter,address,request,home} 与
> model 迁移决定均以依赖扫描脚本实测为准（留主包的被主包 live 页面/app.js 引用）；
> ui-select-picker 实际迁入分包（仅 person-info 使用）；提交 3 后补了 wxss @import 重写（原方案遗漏）。
> 运行时验证：主包 7 页 + 分包 3 页渲染通过；模拟器端到端下单→详情→填运单→轨迹通过；
> IDE 体验评分 性能100/体验79/最佳实践91。「主包大小」一项待 IDE 代码质量面板人工重扫确认。

## 目标

IDE 代码质量扫描（2026-10-01 14:45）三项未通过：

1. 主包大小 ≥ 1.5M（实测构成约 2.4M：miniprogram_npm 1.08M、config/areaData 465K、model 177K、模板分包页 430K、plugins 182K、死目录 99K…）
2. 主包存在仅被分包使用的 JS（services/{activity,comments,good,order}/…、utils/{util,addressParse,getPermission}、tdesign dialog）
3. 无依赖文件 / 无使用组件（pages/usercenter/components/order-group）

将主包降到 1.5M 以下，并消除 2、3 两类告警。

## 非目标

- 不动 miniprogram_npm（构建产物，gitignore；tdesign dialog 为整包复制产物，重新构建会还原，仅记录说明）
- 不动 config/index.js 的 areaData（回收/维修/出售三个下单页的省市区级联数据源，主包必需）
- 不动 live 业务代码逻辑（home/sale/recycle/repair/usercenter 页面、custom-tab-bar）
- 不做分包异步化（areaData 可作为后续优化项）

## 依据的实测引用关系（脚本扫描，见会话）

- `model/` 全部 18 个文件的引用者都是模板 services（无主包页面引用）
- IDE 标记的 18 个「主包未使用 JS」全部只被 pages/{goods,order,user} 分包页引用
- pages/cart|category|coupon|promotion 四个目录未在 app.json 注册（死目录，约 99K），
  仅 pages/goods/result 的 handleCartTap 以 `wx.switchTab('/pages/cart/index')` 引用（cart 本就不是 tab 页，该跳转原本就不可用）
- pages/user 分包的地址簿/个人信息被 usercenter tab 页跳转，属 live 功能 → 随合并保留并更新跳转路径
- services/request 被 app.js 引用 → 留主包；分包引主包 JS 合法

## 方案

合并三个模板分包（pages/user、pages/goods、pages/order）为单一分包
`packages/retail-template`，并把只被它们使用的模板数据层一并迁入
（分包间不能互引，故必须合并到同一 root 才能把 services/model/utils 挪出主包）：

```
packages/retail-template/
  pages/goods|order|user/…   ← git mv，保持相对层级
  services/activity|comments|good|order + address/list.js + usercenter/fetchPerson.js
  model/（整个目录迁入；AGENT.md「不删除 model/」= 文件全部保留，仅位置随消费方迁移）
  utils/{util,addressParse,getPermission,mock}.js
```

引用路径修复规则：

- 分包页 → 主包（config、common）：相对层级 +2（如 `../../../config/index` → `../../../../../config/index`）
- 分包页 → retail-template 内部（services/model/utils）：层级不变
- retail services/utils → 主包：+1 层
- 绝对跳转 `/pages/(goods|order|user)/…` → `/packages/retail-template/pages/…`
  （模板页内部互跳 + usercenter 两处 + project.config.json condition 示例路径）

四个提交（每个自洽可编译）：

1. `chore(miniprogram)`: packOptions.ignore 增加 plugins/、.zcode/、.mimosa/、根目录 md（非小程序文件，182K+50K）
2. `chore(sale)`: 删除未注册死目录 pages/{cart,category,coupon,promotion} 及其专属
   services/{cart,coupon,promotion}、services/good/{fetchCategoryList,fetchGoods}、
   model/{cart,category,coupon,promotion,address}、services/address/fetchAddress.js（无引用孤儿），
   并移除 goods/result 里指向 cart 的失效 switchTab（原已失效，cart 非 tab 页）
3. `refactor(miniprogram)`: 上述大迁移 + app.json 分包改写 + 路径修复
4. `chore(usercenter)`: 删除声明未渲染的 order-group 组件及 index.json 里的注册项

## 验收标准

- app.json / project.config.json JSON 合法；每个注册页面四件套存在；usingComponents 路径全部可解析
- 主包文件（排除分包 root 与 packOptions.ignore 后）合计 < 1.5M
- 交叉校验：主包代码无 `require/import` 指向 packages/retail-template；分包无跨包互引
- IDE 编译无 error；automator 抽查主包 5 个 tab 页 + retail 分包 2 页不白屏
- git 工作树干净，全部改动按显式 pathspec 分 4 个提交

## 风险

- 迁移页面的相对 require 改错 → 用脚本按规则统一改 + 交叉校验兜底
- 未发现的绝对路径跳转 → 全仓 grep `/pages/(goods|order|user)` 兜底
- model/ 位置变化影响后续会话 → 计划文档 + 提交说明中显式声明新位置
