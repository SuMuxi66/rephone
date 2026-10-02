# 维修寄修场景（service_type=20）：运单流转 + 轨迹查询（2026-10-02）

> **状态：已完成（2026-10-02）。** 实际 5 个提交：bcd0b6b（后端）、d223ec0（小程序）、
> edc0cc0（admin）、a9dcb9e（TEXT 修正）、+本文档。
> 与原方案差异：①轨迹快照列 VARCHAR(8000)×2 在真实库触发 MySQL 行超限（repair_order 已有
> items_json 2048 等大列），改 TEXT（schema 与 patch 同步）；②状态流转全部改 CAS 条件更新
> （原计划沿用 updateById 非原子）；③快照命中时 cached 标记需重建置 true。
> 验证：H2 全量 62 项全绿（含 RepairMailInFlowTest 4 项：全流程/上门禁填单/取消窗口/快照命中）；
> 真实库手工补丁（TEXT 版）后容器重建；真实环境 11 项断言全过——含真实微信 code2session
> （并行会话已换绑真实 appid，wx.login 恢复正常、mock 登录关闭）与真实快递100 回包
> （测试单号 50024「暂无轨迹」=签名/账号/连通通过）；全流程单 F2026100214025984476553 落库核对。

> 前置：快递100 实时查询已切真实凭据（EXPRESS_MOCK=false），回收单运单模式可整体复用。

## 目标

用户可选「寄修」：下单后自行寄出并填运单号 → 商家收件维修 → 修好回寄（商家填回寄单号）→
用户确认收货完成。全程两端可查物流轨迹（快递100 + 30 分钟快照缓存，防超频锁单）。

## 状态机（新增 25 / 45 两个寄修专属态，其余复用）

```
上门(10): 10 待确认 → 20 已预约 → 30 维修中 → 40 待验收 → 50 已完成；10 可取消
寄修(20): 10 待确认 → 20 待寄出 → 25 已寄出 → 30 维修中 → 40 待回寄 → 45 回寄中 → 50 已完成
                    （用户填单）         （商家收件）  （维修完成） （商家填回寄单） （用户确认收货）
取消：上门 10；寄修 10 与 20（寄出前）
```

- 25=已寄出、45=回寄中（仅寄修使用）；ADMIN_TRANSITIONS 按 serviceType 分支并改 CAS。
- 40→45 与 20→25 不走普通推进：分别由「商家填回寄单号」「用户填寄出单号」专用接口完成（携带运单数据）。

## Schema（repair_order 加列；真实库需手工补丁）

寄出：express_com / express_company / express_no / express_trace / trace_at
回寄：return_express_com / return_express_company / return_express_no / return_express_trace / return_trace_at
新建库走 schema-repair.sql（更新 CREATE）；存量库走 patch-repair-express.sql（ALTER），
**真实 MySQL 需 docker exec 手工执行补丁**（AGENT.md §6）。

## 接口

| 端 | 接口 | 说明 |
|---|---|---|
| wx | PUT /api/wx/repair/order/{no}/express | 属主+寄修+状态20；填寄出单号，CAS 20→25 |
| wx | GET /api/wx/repair/order/{no}/trace | 寄出轨迹（快照 TTL 30 分钟，回源 queryTrace） |
| wx | GET /api/wx/repair/order/{no}/return-trace | 回寄轨迹 |
| wx | PUT /api/wx/repair/order/{no}/confirm | 属主确认收货，CAS 45→50 |
| admin | PUT /api/admin/repair/order/{no}/return-express | 寄修+状态40；填回寄单号，CAS 40→45 |
| admin | GET /api/admin/repair/order/{no}/trace?direction= | 商家查双向轨迹 |
| admin | GET /api/admin/repair/express-companies | 快递公司字典（回寄填单选择器，repair:manage） |

下单接口放开 serviceType=20；寄修不填预约时间（appoint_time 存空串），address 语义=回寄收件地址。
快递公司解析复用 ExpressCompanies.byCom/byName；轨迹复用 ExpressService.queryTrace 与
ExpressTraceResult（含快照标记）。

## 前端

- 小程序 create 页：新增「上门维修 / 寄修」服务方式选择；寄修隐藏预约时间、地址文案改回寄收件地址。
- 小程序 detail 页：寄修显示寄出/回寄运单卡 + 查物流（轨迹节点列表）+ 45 态「确认收货」按钮；
  时间线按 serviceType 切换（common/repair-status.js 增 25/45 与 flowFor(serviceType)）。
- admin RepairOrders 抽屉：按 serviceType 分支推进按钮（寄修：确认→收件开修→修好→填回寄单→结算），
  运单区展示双向单号与轨迹，回寄单号弹窗（快递公司下拉+单号）。

## 提交拆分

1. docs(plan)：本计划
2. feat(server)：schema/patch/entity/DTO/状态机分支/七个新端点 + RepairMailInFlowTest
3. feat(miniprogram)：服务方式选择 + 详情页运单/轨迹/确认收货 + 状态字典
4. feat(admin)：抽屉寄修分支 + 回寄填单 + 状态字典
5. chore(ops)：真实库补丁 + 容器重建 + 全链路验证

## 非目标

寄修运费/到付结算、寄件上门取件（快递100 寄件产品未签约）、真实支付（仍外置）。

## 验收

- H2：寄修全流程（建单→确认→填单→收件→修好→回寄→确认收货）+ 守卫（上门单禁填运单、
  跳步推进拒绝、取消窗口）+ 轨迹快照；存量 P7RepairFlowTest（上门）回归全绿。
- 真实库：手工补丁后容器重建，真实寄修单走通 25 态轨迹查询（快递100 真实回包）。
- admin/小程序：静态校验 + 页面实测。
