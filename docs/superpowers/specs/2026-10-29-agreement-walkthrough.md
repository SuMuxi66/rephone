# 协议签署走查记录（模拟器已测）+ 真机补充清单

日期：2026-10-29 ｜ 环境：微信开发者工具 v2.02.2609231 模拟器（iPhone），基础库编译本仓 feature/recycle-mvp @ 6b81747
关联设计：[2026-10-29-agreement-signing.md](2026-10-29-agreement-signing.md)

## 一、模拟器走查结果（全部通过）

> 方法说明：本 CLI 自动化引擎无法穿透自定义组件内部节点（`>>>` 落在组件宿主），
> 真实 tap 只能覆盖组件外壳。因此**交互以 evaluate 直驱组件真实方法**执行（等价于
> 对应事件回调被触发：`onConfirm`/`onToggleAll`/`onRefuse`/`onReachEnd`），
> 连点用同步重入调用模拟（比真实双击更严苛）；**渲染与视觉以截图取证**。

| # | 场景 | 步骤 | 结果 |
|---|---|---|---|
| S1 | 全局弹窗渲染 | 进首页（账本为空） | ✅ 三份清单+全选行+置灰按钮+遮罩，截图 `.tmp/walkthrough/s1-global-popup.png` |
| S1a | 连点同意（重入） | 全选后同步调用 `onConfirm()` ×3 | ✅ 弹窗只关闭一次、无异常；账本恰好 3 条、同一时间戳（单次记账） |
| S1b | 防重复弹窗 | `wx.reLaunch` 重建首页 | ✅ 新实例 `modalOpened=false`，已签不再弹 |
| S2 | 估价弹窗 | 表单 setData 后 `onSubmit()` | ✅ 弹《回收估价用户须知》，read 模式、requireRead=false |
| S2a | 拒绝阻断 | `onRefuse()` | ✅ 弹窗关闭、仍在估价页、`submitting=false`（估价请求未发出）、账本无记录 |
| S2b | 再弹与放行 | 再次提交 → 勾选 → 确认 | ✅ 未签自动重弹；确认后关闭+入账；**估价请求真实成功并跳转结果页**（本地后端在跑，放行链路全通） |
| S3 | 长文档滑底门禁 | 60 段超长文档（requireRead） | ✅ 未勾选拒；勾选未滑底仍拒；`onReachEnd` 后放行 |
| S3a | 翻篇状态复位 | 确认进第二篇 | ✅ agreed/reachedEnd 重置；`scrollTop` 0→0.5 交替生效（阅读区回顶） |
| S3b | 短文档自动放行 | 2 行短文档（requireRead） | ✅ 渲染后 `checkOverflow` 量高自动置 `reachedEnd=true`，仅勾选即可确认（不会被锁死） |
| S4 | 人脸独立弹窗 | 单篇《人脸识别协议》打开 | ✅ 队列长度=1、单独勾选、拒绝不入账——与任何其他协议无捆绑 |

测试残留已清理（`long@T1`/`tiny@T1`/`face-consent@V1.0` 已删；保留全局三份+估价须知为真实签署）。

## 二、真机走查补充清单（模拟器覆盖不到的部分）

前置：删除对话调试口令后用预览码进真机；进之前在 vConsole 执行 `wx.removeStorageSync('agreement.signatures.v1')` 清账本。

| # | 项 | 步骤 | 预期 |
|---|---|---|---|
| Z1 | 真实双击同意 | 全局弹窗全选后**手指快速双击**「同意并继续」 | 只关一次；首页正常可用；无报错（重入锁） |
| Z2 | 遮罩点击 | 估价弹窗点弹窗外空白处 | 等同「暂不同意」：关闭+阻断+不记账 |
| Z3 | 长文滑动手感 | 回收下单触发的三连签：快速甩动滑到底 vs 慢慢拖到底 | 两种方式都能点亮确认键（scrolltolower lower-threshold=60） |
| Z4 | 翻篇回顶 | 三连签翻篇瞬间观察 | 新一篇从顶部开始展示，不停留在上一篇底部 |
| Z5 | 键盘遮挡 | 弹窗打开时点击输入框（如有焦点残留）再关闭 | 弹窗层级不被键盘顶起、关闭后页面正常 |
| Z6 | 安全区 | 全面屏机型看弹窗底部按钮 | `safe-area-inset-bottom` 生效，按钮不贴 Home 条 |
| Z7 | 弱网放行 | 飞行模式下完成签署 | 本地立即记账放行，不因网络失败卡门禁 |
| Z8 | 冷启动重弹 | 修改 `agreement-docs.js` 任一 version 后重新预览 | 已签用户重新收到弹窗 |

## 三、待办与日程

1. **人脸核身接入（已提上日程）**：代码库尚无人脸调用点。接入时**必须**在发起核验前置入
   `ensureAgreements(this, 'face')`（未同意阻断核身），且服务端在核身结果回传前校验
   《人脸识别协议》签署记录存在（fail-closed）。详见设计文档 §6。
2. **后端幂等契约（后端对齐中）**：`agreement_signature` 唯一键
   `uk(tenant_id, user_id, agreement_key, agreement_version)`；重复签署返回成功（幂等）；
   `POST /api/agreements/signatures` 支持批量 items；上线后置 `agreement-manager.js`
   的 `REMOTE_SYNC_ENABLED = true`，建议同时在下单接口做服务端 fail-closed 校验。

## 四、自动化工具链：官方 miniprogram-automator 已跑通（17/17）

官方入口 = `cli.bat auto --auto-port 9420` + npm 包 `miniprogram-automator`（本机装在
`.tmp/e2e/`，未入库；SDK v0.13.0）。沉淀脚本：`tools/e2e/agreement-e2e.js`，
运行方式见脚本头注释（Windows 下 `automator.launch` spawn .bat 会失败，
用 `cli auto` + `automator.connect({wsEndpoint})` 已验证）。

与 wechatide-skill CLI（`automation_*` 工具族）的差异：CLI 的选择器引擎穿不进
自定义组件内部节点（`>>>` 落在组件宿主上，tap 打空炮）；官方 SDK 的
`page.$('#agreement-modal')` 可直接取组件内部元素做**真实 tap / 真实 touch 序列**。
E2E 结果（同日）：17/17 全过——S1 真实双击只关一次、S2 真实点「暂不同意」后
`submitting=false` 未发请求、S3 三连签翻篇回顶（截图 `s3-doc2-top.png`）、
第三篇拒绝后未下单；唯一兜底点：合成 touch 手势驱动 scroll-view 滚动在模拟器
不生效，脚本退回 `trigger('scrolltolower')`（真实滑动手感仍以真机 Z3 为准）。
