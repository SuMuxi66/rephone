# 协议签署体系设计（通用组件 + 四场景门禁 + 留痕方案）

日期：2026-10-29 ｜ 范围：小程序端已实现；服务端为设计稿（未实现）

## 1. 目标

在全局首启、估价、回收下单、维修下单、人脸核验五类节点强制用户阅读并签署对应协议，未签署则阻断业务流转；签署状态本地留存避免重复弹窗，并预留后端留痕通道。

## 2. 架构（三层）

```
pages（home / estimate / recycle-create / repair-create）
  │  await ensureAgreements(this, 'recycle')   ← 唯一门禁入口
  ▼
common/agreement-manager.js   签署状态读写 + 门禁封装（fail-closed）
  │                            common/agreement-docs.js     8 份协议内容 + 场景队列（唯一内容源）
  ▼
components/agreement-modal/   通用弹窗：read=逐篇阅读签署 / confirm=清单勾选
```

- **内容库** `agreement-docs.js`：8 份协议全文（取自仓库根 协议文件/ docx，已剥离文末「□ 我已阅读…」勾选行与【…】作者注释）；每份带 `version`，场景表 `AGREEMENT_SCENES` 声明每类节点签哪些、按什么顺序签。
- **管理器** `agreement-manager.js`：`ensureAgreements(page, scene)` 返回 Promise<boolean>；组件未注册视为未同意（fail-closed）并 console.error。
- **组件** `agreement-modal`：不耦合业务，`open({scene, mode, docs})` 返回 Promise，签完 resolve、拒绝/关闭/销毁 reject；`docs` 可任意传入复用。

## 3. 交互设计

| 场景 | mode | 文档 | 交互 |
|---|---|---|---|
| 全局首启（首页 onLoad） | confirm | 免责协议、隐私协议、用户服务协议 | 清单勾选 + 底部全选；点标题阅读全文；全部勾选才能「同意并继续」；**无关闭/拒绝出口** |
| 估价（获取预估价前） | read | 回收估价用户须知 | 全文阅读 → 勾选 → 确认 |
| 回收下单（提交前） | read | 维修知情用户须知 → 设备来源合法承诺书 → 用户承诺书 | 逐篇签署，`requireRead` 文档必须滑到底部才能勾选（内容不足一屏时自动放行，防锁死） |
| 维修下单（提交前） | read | 维修知情用户须知 → 设备来源合法承诺书 | 同上 |
| 人脸核验（核身接入后） | read | 人脸识别协议 | 单独弹窗单独同意（敏感个人信息，不随其他协议打包授权） |

拒绝路径：read 模式提供「暂不同意」按钮 → `ensureAgreements` 返回 false → 页面 toast 并阻断提交。全局场景按业务要求「不同意则无法使用」实现为**常驻弹窗**——微信小程序没有公开的「退出小程序」API，无法编程强制退出，这是合规场景下的标准替代做法。

## 4. 签署状态设计

- 本地缓存 `wx.storage`，key = `agreement.signatures.v1`：

```json
{ "privacy@V1.0": { "time": 1730000000000, "scene": "global" }, "device-source@V1.0": { "time": "...", "scene": "recycle" } }
```

- 记账粒度为 `docKey@version`：协议文本修订时把 `agreement-docs.js` 里对应 `version` 递增（如 V1.0→V1.1），所有已签用户自动重新弹窗——这是协议「重新签署」的唯一开关。
- 后端留痕：`recordSignatures` 内置异步上报（`POST /api/agreements/signatures`），当前由 `REMOTE_SYNC_ENABLED = false` 关闭；上报失败不回滚本地（避免用户重复签），服务上线后置 true 即启用。

## 5. 服务端设计（未实现，落地时照此执行）

### 5.1 表结构（放 `server/start/src/main/resources/db/`，幂等建表；若已有环境需同步提供 `patch-agreement.sql`）

```sql
CREATE TABLE IF NOT EXISTS agreement_signature (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '多租户',
  user_id BIGINT NOT NULL,
  agreement_key VARCHAR(64) NOT NULL,
  agreement_version VARCHAR(16) NOT NULL,
  scene VARCHAR(32) NOT NULL COMMENT '签署场景：global/estimate/recycle/repair/face',
  device_id VARCHAR(64) NULL,
  signed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_doc (tenant_id, user_id, agreement_key, agreement_version),
  KEY idx_user_scene (tenant_id, user_id, scene)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='协议签署留痕';

-- 可选二期：协议内容管理端可配（当前内容内置在小程序包里，改文案需发版）
CREATE TABLE IF NOT EXISTS agreement_doc (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  agreement_key VARCHAR(64) NOT NULL,
  version VARCHAR(16) NOT NULL,
  title VARCHAR(128) NOT NULL,
  content MEDIUMTEXT NOT NULL,
  require_read TINYINT NOT NULL DEFAULT 0,
  enabled TINYINT NOT NULL DEFAULT 1,
  published_at DATETIME NULL,
  UNIQUE KEY uk_doc (tenant_id, agreement_key, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='协议文档版本';
```

### 5.2 接口契约（统一返回体 R<T>）

- `POST /api/agreements/signatures`，body `{ "scene": "recycle", "items": [{"key":"device-source","version":"V1.0"}] }`；逐条幂等落库（重复签不报错）。
- `GET /api/agreements/status?keys=privacy,device-source` → `data: { "privacy": {"version":"V1.0","signedAt":"..."} }`；客户端冷启动时可对照本地，换设备/清缓存后恢复签署状态。

### 5.3 服务端 fail-closed（强烈建议）

本地缓存可被清除，签署门禁的前端拦截只是体验层。**提交回收/维修订单时，服务端应校验该场景要求的协议均已签**（查 `agreement_signature`），未签返回业务错误码——与「支付统一入口 fail-closed」同一哲学。人脸核验的《人脸识别协议》签署记录应在核身结果回传前强制存在。

## 6. 人脸核验场景接入指引

代码库中目前没有微信人脸核身调用点。接入时在发起核验前（回收下单提交链或个人中心实名入口）加一行：

```js
if (!(await ensureAgreements(this, 'face'))) {
  wx.showToast({ title: '需同意《人脸识别协议》后进行人脸核验', icon: 'none' });
  return;
}
```

对应页面 json/wxml 需注册 `<agreement-modal id="agreement-modal" />`（分包页面可引用主包组件）。

## 7. 业务方待确认项

1. 《用户承诺书》与《设备来源合法承诺书》条款高度重叠（均为权属/来源承诺），当前按业务要求在回收下单队列中**两份都签**；若法律上只需其一，删 `AGREEMENT_SCENES.recycle` 中对应 key 即可。
2. docx 中「订单编号/承诺人」占位已改为通用文案（提交订单时自动生成 / 当前登录用户）；如需落真实值，应在签署时传入并让服务端留存快照。
3. 免责/隐私/用户服务协议原文末尾的【拒绝即自动退出小程序】标注：无公开退出 API，已实现为常驻阻断弹窗。
4. 《回收估价用户须知》提到「请阅读并同步签署《设备来源合法承诺书》」，该承诺书当前放在回收下单环节签署（估价环节不签）；若需估价环节即签，把 `device-source` 加进 `AGREEMENT_SCENES.estimate.docs`。

## 8. 验证情况

- 静态校验：新增/改动 JS 经 @babel/parser（sourceType: module）解析通过；JSON 经 JSON.parse 校验；WXML 标签配对检查通过。
- 未做微信开发者工具运行时验证（本会话未起 IDE 自动化）。
