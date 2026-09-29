# P5 极简管理后台 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 交付浏览器管理后台（Vue3 + Element Plus），运营可对回收订单执行 列表→详情→改状态→录质检（含照片上传）→打款 的完整闭环。

**Architecture:** 独立前端 `admin/`（Vite + Vue3 + Element Plus + axios + vue-router），开发期 Vite 代理 `/api` 到本机 8080 后端；复用已就绪的 `/api/admin/recycle/*` 四个接口与 ADMIN_TOKEN Bearer 鉴权；后端补 2 个小接口（管理端订单详情、管理端 COS 签名）。照片经 COS 预签名直传，不经后端。

**Tech Stack:** Spring Boot 3（已有）/ Vue 3.5 / Element Plus / axios / Vite 7

## Global Constraints（摘自 AI_PLAN，每个任务隐含遵守）

- 所有业务表带 `tenant_id`；管理端查询按 `TenantContextHolder` 现状（admin 无租户上下文 → 不追加过滤，P7 RBAC 收紧）
- 金额：后端分（BIGINT），管理界面输入/展示用元（×100 互转，`Math.round(yuan*100)`）
- 密钥只走环境变量；代码/仓库不得出现真实 key
- 订单号业务生成，禁止用自增 ID 展示为订单号
- 每任务可独立运行验证；小步提交（feat/granular commit）
- 提交前 Mimosa 钩子如拦"协议强制算法"，引用路线图第一节白名单决议，不改动算法

---

### Task 0: 提交已暂存的 P3/P4 成果（前置）

**Files:** 无新增（git 操作）

- [ ] **Step 1: 确认白名单已配置后，恢复产物窗口期并提交**

```bash
move "E:\code-start\RePhone\miniprogram_npm" "E:\code-start\_rephone_npm_stash"
git commit -m "feat(p3/p4): recycle order loop, cos sign, kuaidi100+callback, subscribe msg, payout (see staged message)"
move "E:\code-start\_rephone_npm_stash" "E:\code-start\RePhone\miniprogram_npm"
```

Expected: `git log --oneline -1` 出现 feat(p3/p4) 提交；若钩子仍拦，停止并回报用户。

---

### Task 1: 后端补管理端详情接口 + 管理端 COS 签名接口（TDD）

**Files:**
- Modify: `server/service/src/main/java/com/rephone/service/RecycleOrderService.java`
- Create: `server/controller/src/main/java/com/rephone/controller/AdminCosController.java`
- Modify: `server/controller/src/main/java/com/rephone/controller/AdminRecycleController.java`
- Test: `server/start/src/test/java/com/rephone/P3OrderFlowTest.java`

**Interfaces:**
- Consumes: `CosSignService.signPutObject(String ext)` → `CosUploadSign`；`RecycleOrderService.detailWithInspections(orderNo)`
- Produces: `GET /api/admin/recycle/order/{orderNo}` → `R<RecycleOrderDetail>`；`GET /api/admin/cos/upload-sign?ext=jpg` → `R<CosUploadSign>`（Task 3/4 前端消费）

- [ ] **Step 1: 写失败测试**（在 `P3OrderFlowTest` 的 `cos_sign_mock_and_admin_auth` 末尾追加）

```java
        // 管理端详情 + 管理端 COS 签名
        ResponseEntity<String> adminDetail = rest.exchange("/api/admin/recycle/order/" + orderNoOfFirstOrder(),
                HttpMethod.GET, new HttpEntity<>(admin()), String.class);
        assertEquals(0, objectMapper.readTree(adminDetail.getBody()).get("code").asInt());
        ResponseEntity<String> adminSign = rest.exchange("/api/admin/cos/upload-sign?ext=png",
                HttpMethod.GET, new HttpEntity<>(admin()), String.class);
        assertTrue(objectMapper.readTree(adminSign.getBody()).path("data").path("key").asText().startsWith("recycle/"));
```

同文件加辅助方法（复用 DB 查最新订单号，避免跨测试传参）：

```java
    private String orderNoOfFirstOrder() {
        return jdbcTemplate.queryForObject(
                "select order_no from recycle_order order by id desc limit 1", String.class);
    }
```

- [ ] **Step 2: 跑测试确认失败**

Run: `cd /d E:\code-start\RePhone\server && mvnw.cmd test -Dtest=P3OrderFlowTest`
Expected: FAIL（404 / 接口不存在）

- [ ] **Step 3: 实现**

`RecycleOrderService` 抽出组装并新增管理端入口（把现有 `detailWithInspections` 的组装体抽成私有 `assemble`，供两处调用）：

```java
    /** 管理端详情（不做用户归属校验，P7 RBAC 收紧）。 */
    public RecycleOrderDetail adminDetail(String orderNo) {
        RecycleOrder o = getByOrderNo(orderNo);
        return new RecycleOrderDetail(o.getOrderNo(), o.getBrandName(), o.getModelName(), o.getStorage(),
                o.getConditionLabel(), fromJsonList(o.getIssuesJson()), o.getQuoteFen(), o.getFinalFen(),
                o.getStatus(), desc(o.getStatus()), o.getPickupType(), o.getPickupName(), o.getPickupPhone(),
                o.getPickupAddress(), o.getExpressCompany(), o.getExpressNo(), o.getRemark(), o.getAdminRemark(),
                o.getCreateTime() == null ? "" : o.getCreateTime().toString(), inspections(orderNo));
    }
```

`AdminRecycleController` 加：

```java
    @GetMapping("/order/{orderNo}")
    public R<RecycleOrderDetail> detail(@PathVariable String orderNo) {
        return R.ok(orderService.adminDetail(orderNo));
    }
```

（import `com.rephone.pojo.dto.RecycleOrderDetail`）

新建 `AdminCosController.java`：

```java
package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.CosUploadSign;
import com.rephone.service.CosSignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 管理端 COS 直传签名（质检照片等）。 */
@RestController
@RequestMapping("/api/admin")
public class AdminCosController {

    private final CosSignService cosSignService;

    public AdminCosController(CosSignService cosSignService) {
        this.cosSignService = cosSignService;
    }

    @GetMapping("/cos/upload-sign")
    public R<CosUploadSign> uploadSign(@RequestParam(defaultValue = "jpg") String ext) {
        return R.ok(cosSignService.signPutObject(ext));
    }
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `cd /d E:\code-start\RePhone\server && mvnw.cmd test`
Expected: `Tests run: 8, Failures: 0, Errors: 0`，BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add server
git commit -m "feat(p5): admin order detail + admin cos upload-sign endpoints"
```

---

### Task 2: 管理前端脚手架 + 登录（可独立验证）

**Files:**
- Create: `admin/package.json`、`admin/vite.config.js`、`admin/index.html`
- Create: `admin/src/main.js`、`admin/src/api.js`、`admin/src/router.js`
- Create: `admin/src/views/Login.vue`

**Interfaces:**
- Produces: `api.js` 导出默认 axios 实例（baseURL `/api`，自动带 `localStorage.adminToken`，401 清 token 跳 `#/login`，响应拦截解包 `code===0 → data`）；路由 `/login`、`/orders`（守卫无 token 重定向 login）。Task 3/4 直接 `import api from '../api'`

- [ ] **Step 1: 写脚手架文件**

`admin/package.json`：

```json
{
  "name": "rephone-admin",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": { "dev": "vite", "build": "vite build" },
  "dependencies": {
    "axios": "^1.7.9",
    "element-plus": "^2.9.3",
    "vue": "^3.5.13",
    "vue-router": "^4.5.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.2.1",
    "vite": "^7.1.0"
  }
}
```

`admin/vite.config.js`：

```js
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } },
  },
});
```

`admin/index.html`：

```html
<!doctype html>
<html lang="zh-CN">
  <head><meta charset="UTF-8" /><title>RePhone 管理后台</title></head>
  <body><div id="app"></div><script type="module" src="/src/main.js"></script></body>
</html>
```

`admin/src/main.js`：

```js
import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue';
import router from './router';

createApp(App).use(ElementPlus).use(router).mount('#app');
```

（`admin/src/App.vue`：`<template><router-view /></template>`）

`admin/src/api.js`：

```js
import axios from 'axios';

const api = axios.create({ baseURL: '/api', timeout: 10000 });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('adminToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => {
    if (res.data && res.data.code === 0) return res.data.data;
    throw new Error((res.data && res.data.message) || '请求失败');
  },
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('adminToken');
      location.hash = '#/login';
    }
    throw new Error(err.response?.data?.message || '网络错误');
  },
);

export default api;
```

`admin/src/router.js`：

```js
import { createRouter, createWebHashHistory } from 'vue-router';
import Login from './views/Login.vue';
import Orders from './views/Orders.vue';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/login', component: Login },
    { path: '/orders', component: Orders },
    { path: '/', redirect: '/orders' },
  ],
});

router.beforeEach((to) => {
  if (to.path !== '/login' && !localStorage.getItem('adminToken')) return '/login';
});

export default router;
```

`admin/src/views/Login.vue`：

```vue
<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2>RePhone 管理后台</h2>
      <el-input v-model="token" placeholder="ADMIN_TOKEN" show-password @keyup.enter="login" />
      <el-button type="primary" style="width:100%;margin-top:16px" :loading="loading" @click="login">登录</el-button>
      <div v-if="error" class="err">{{ error }}</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';

const router = useRouter();
const token = ref('');
const loading = ref(false);
const error = ref('');

async function login() {
  loading.value = true;
  error.value = '';
  try {
    localStorage.setItem('adminToken', token.value);
    await api.get('/admin/recycle/orders', { params: { pageNum: 1, pageSize: 1 } });
    router.push('/orders');
  } catch (e) {
    localStorage.removeItem('adminToken');
    error.value = e.message;
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-wrap { display:flex; align-items:center; justify-content:center; height:100vh; background:#f5f6f8; }
.login-card { width:360px; text-align:center; }
.err { color:#f56c6c; margin-top:12px; font-size:13px; }
</style>
```

- [ ] **Step 2: 安装依赖并启动**

Run: `cd /d E:\code-start\RePhone\admin && npm install && npm run dev`
Expected: Vite 输出 `Local: http://localhost:5173/`

- [ ] **Step 3: 验证登录链路（后端在 8080 运行中）**

浏览器打开 `http://localhost:5173/#/login`，输入 `local-admin-token` 登录 → 应跳转 `/orders`（空列表页占位，Task 3 实现）。
curl 兜底验证代理：`curl http://localhost:5173/api/health` → `{"code":0,...}`

- [ ] **Step 4: Commit**

```bash
git add admin
git commit -m "feat(p5): admin console scaffold with token login"
```

---

### Task 3: 回收订单列表页（筛选 + 分页 + 行点击开抽屉）

**Files:**
- Create: `admin/src/views/Orders.vue`
- Create: `admin/src/components/OrderDrawer.vue`（本任务先建骨架，Task 4 填充动作）

**Interfaces:**
- Consumes: `api.get('/admin/recycle/orders', { params: { status, pageNum, pageSize } })` → `{ records, total }`（records 元素含 orderNo/brandName/modelName/storage/conditionLabel/quoteFen/finalFen/status/statusDesc/createTime）
- Produces: `OrderDrawer` 组件接口：props `{ orderNo: String }`，`expose: reload()`，emit `changed`（状态变更后通知列表刷新）。STATUS 常量：`{10:'待寄出',20:'运输中',30:'质检中',40:'待确认',50:'已打款',60:'已完成',80:'已取消'}`

- [ ] **Step 1: 写列表页**

`admin/src/views/Orders.vue`：

```vue
<template>
  <div class="page">
    <el-radio-group v-model="activeStatus" @change="reload">
      <el-radio-button v-for="f in FILTERS" :key="f.status" :value="f.status">{{ f.label }}</el-radio-button>
    </el-radio-group>

    <el-table :data="rows" v-loading="loading" @row-click="openDrawer" style="margin-top:16px">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="机型" min-width="180">
        <template #default="{ row }">{{ row.brandName }} {{ row.modelName }}</template>
      </el-table-column>
      <el-table-column prop="storage" label="内存" width="90" />
      <el-table-column prop="conditionLabel" label="成色" width="110" />
      <el-table-column label="预估价(元)" width="110">
        <template #default="{ row }">{{ fen2yuan(row.quoteFen) }}</template>
      </el-table-column>
      <el-table-column label="最终价(元)" width="110">
        <template #default="{ row }">{{ row.finalFen == null ? '-' : fen2yuan(row.finalFen) }}</template>
      </el-table-column>
      <el-table-column prop="statusDesc" label="状态" width="90" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
    </el-table>

    <el-pagination
      style="margin-top:16px;justify-content:flex-end"
      layout="prev, pager, next, total"
      :total="total" :page-size="pageSize" :current-page="pageNum"
      @current-change="onPage" />

    <OrderDrawer ref="drawer" :order-no="currentOrderNo" @changed="reload" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';
import OrderDrawer from '../components/OrderDrawer.vue';

const FILTERS = [
  { label: '全部', status: 0 }, { label: '待寄出', status: 10 }, { label: '运输中', status: 20 },
  { label: '质检中', status: 30 }, { label: '待确认', status: 40 }, { label: '已打款', status: 50 },
  { label: '已完成', status: 60 }, { label: '已取消', status: 80 },
];
const fen2yuan = (fen) => (fen == null ? '-' : (fen / 100).toFixed(2));

const activeStatus = ref(0);
const rows = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const drawer = ref(null);
const currentOrderNo = ref('');

async function reload() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (activeStatus.value > 0) params.status = activeStatus.value;
    const page = await api.get('/admin/recycle/orders', { params });
    rows.value = page.records || [];
    total.value = page.total || 0;
  } finally {
    loading.value = false;
  }
}

function onPage(p) { pageNum.value = p; reload(); }
function openDrawer(row) { currentOrderNo.value = row.orderNo; drawer.value?.open(); }

onMounted(reload);
</script>

<style scoped>
.page { padding: 24px; }
</style>
```

`admin/src/components/OrderDrawer.vue`（骨架）：

```vue
<template>
  <el-drawer v-model="visible" :title="orderNo || '订单详情'" size="520px">
    <p>详情加载中（Task 4 实现）</p>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue';
defineProps({ orderNo: { type: String, default: '' } });
const visible = ref(false);
function open() { visible.value = true; }
defineExpose({ open });
</script>
```

- [ ] **Step 2: 浏览器验证**

Vite dev 保持运行；访问 `/orders`：8 个筛选 tab 切换数据变化、分页可用、点行打开抽屉。
curl 对照：`curl -H "Authorization: Bearer local-admin-token" "http://localhost:8080/api/admin/recycle/orders?pageNum=1&pageSize=2"`
Expected: 表格行数与接口 total 一致

- [ ] **Step 3: Commit**

```bash
git add admin/src
git commit -m "feat(p5): admin recycle order list with filters and pagination"
```

---

### Task 4: 订单详情抽屉（按状态出操作按钮 + 质检表单）

**Files:**
- Modify: `admin/src/components/OrderDrawer.vue`（整文件替换）

**Interfaces:**
- Consumes: `api.get('/admin/recycle/order/'+orderNo)` → RecycleOrderDetail（含 inspections[]）；`api.put('/admin/recycle/order/'+orderNo+'/status', {toStatus, remark})`；`api.post('/admin/recycle/order/'+orderNo+'/inspection', {result, finalFen, images})`；`api.post('/admin/recycle/order/'+orderNo+'/payout')`；Task 5 的 `uploadToCos(file) → key`
- Produces: 状态动作规则——20→[开始质检], 30→[质检表单→提交], 40→[触发打款], 50→[标记完成]；每次动作成功 emit('changed')

- [ ] **Step 1: 整文件替换 `OrderDrawer.vue`**

```vue
<template>
  <el-drawer v-model="visible" :title="orderNo" size="560px" @open="load">
    <template v-if="detail">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="状态">{{ detail.statusDesc }}</el-descriptions-item>
        <el-descriptions-item label="机型">{{ detail.brandName }} {{ detail.modelName }}</el-descriptions-item>
        <el-descriptions-item label="配置">{{ detail.storage }} · {{ detail.conditionLabel }}</el-descriptions-item>
        <el-descriptions-item label="功能问题">{{ detail.issues.join('、') || '无' }}</el-descriptions-item>
        <el-descriptions-item label="取件人">{{ detail.pickupName }} {{ detail.pickupPhone }}</el-descriptions-item>
        <el-descriptions-item label="取件地址">{{ detail.pickupAddress }}</el-descriptions-item>
        <el-descriptions-item label="运单号">{{ detail.expressNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预估价">¥{{ fen2yuan(detail.quoteFen) }}</el-descriptions-item>
        <el-descriptions-item label="最终价">¥{{ detail.finalFen == null ? '-' : fen2yuan(detail.finalFen) }}</el-descriptions-item>
      </el-descriptions>

      <template v-if="detail.inspections.length">
        <h4>质检记录</h4>
        <div v-for="it in detail.inspections" :key="it.createTime" class="insp">
          <span>{{ it.result }}</span><b>¥{{ fen2yuan(it.finalFen) }}</b>
        </div>
      </template>

      <div class="actions">
        <el-button v-if="detail.status === 20" type="primary" @click="toInspecting">开始质检</el-button>
        <el-button v-if="detail.status === 40" type="warning" @click="payout">触发打款</el-button>
        <el-button v-if="detail.status === 50" type="success" @click="complete">标记完成</el-button>
      </div>

      <el-form v-if="detail.status === 30" label-width="90px" style="margin-top:16px">
        <h4>提交质检结果</h4>
        <el-form-item label="质检结论">
          <el-input v-model="form.result" type="textarea" :rows="3" maxlength="200" />
        </el-form-item>
        <el-form-item label="最终价(元)">
          <el-input-number v-model="form.finalYuan" :precision="2" :min="0" :step="10" />
        </el-form-item>
        <el-form-item label="质检照片">
          <input type="file" multiple accept="image/*" @change="onFiles" />
          <div class="tips">{{ form.images.length ? `已选 ${form.images.length} 张` : '未配置 COS 时仅登记文件名' }}</div>
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="submitInspection">提交质检（进入待确认）</el-button>
      </el-form>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../api';
import { uploadToCos } from '../cos';

const props = defineProps({ orderNo: { type: String, default: '' } });
const emit = defineEmits(['changed']);
const visible = ref(false);
const detail = ref(null);
const saving = ref(false);
const form = ref({ result: '', finalYuan: 0, images: [] });
const fen2yuan = (fen) => (fen == null ? '-' : (fen / 100).toFixed(2));

function open() { visible.value = true; }
defineExpose({ open });

async function load() {
  detail.value = await api.get(`/admin/recycle/order/${props.orderNo}`);
}

async function act(fn, tip) {
  await fn();
  ElMessage.success(tip);
  emit('changed');
  await load();
}

const toInspecting = () => act(() => api.put(`/admin/recycle/order/${props.orderNo}/status`, { toStatus: 30, remark: '开始质检' }), '已进入质检中');
const payout = () => act(() => api.post(`/admin/recycle/order/${props.orderNo}/payout`), '打款成功');
const complete = () => act(() => api.put(`/admin/recycle/order/${props.orderNo}/status`, { toStatus: 60, remark: '订单完成' }), '已完成');

async function onFiles(e) {
  form.value.images = [];
  for (const file of e.target.files) {
    form.value.images.push(await uploadToCos(file));
  }
}

async function submitInspection() {
  if (!form.value.result) return ElMessage.warning('请填写质检结论');
  saving.value = true;
  try {
    await api.post(`/admin/recycle/order/${props.orderNo}/inspection`, {
      result: form.value.result,
      finalFen: Math.round(form.value.finalYuan * 100),
      images: form.value.images,
    });
    ElMessage.success('质检已提交，订单进入待确认');
    emit('changed');
    await load();
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.actions { margin-top: 16px; display: flex; gap: 8px; }
.insp { display: flex; justify-content: space-between; font-size: 13px; padding: 6px 0; border-bottom: 1px dashed #eee; }
.tips { color: #999; font-size: 12px; }
</style>
```

- [ ] **Step 2: 浏览器验证**

列表点开一条状态 20 的订单 → 「开始质检」→ 状态变 30 → 表单出现；填结论+价格提交 → 40；「触发打款」→ 50；「标记完成」→ 60。
（测试数据不足时用小程序/curl 造单推进到对应状态）

- [ ] **Step 3: Commit**

```bash
git add admin/src/components/OrderDrawer.vue
git commit -m "feat(p5): order drawer with status actions and inspection form"
```

---

### Task 5: COS 上传助手（前端）

**Files:**
- Create: `admin/src/cos.js`

**Interfaces:**
- Consumes: `GET /admin/cos/upload-sign?ext=` → `{mock, host, key, authorization}`
- Produces: `uploadToCos(file: File) → Promise<string>`（返回对象 key；mock 模式仅登记 key 并 console.warn，保证开发闭环可用）

- [ ] **Step 1: 写 `admin/src/cos.js`**

```js
import api from './api';

/**
 * 上传文件到腾讯云 COS（PutObject 预签名直传）。
 * mock 模式（后端未配 COS 密钥）只返回模拟 key，便于本地闭环。
 */
export async function uploadToCos(file) {
  const ext = (file.name.split('.').pop() || 'jpg').toLowerCase().slice(0, 8);
  const sign = await api.get('/admin/cos/upload-sign', { params: { ext } });
  if (sign.mock) {
    console.warn('[cos] mock 模式：仅登记 key，未真实上传', sign.key);
    return sign.key;
  }
  const res = await fetch(`${sign.host}/${sign.key}`, {
    method: 'PUT',
    headers: { Authorization: sign.authorization },
    body: file,
  });
  if (!res.ok) throw new Error(`COS 上传失败(${res.status})`);
  return sign.key;
}
```

- [ ] **Step 2: 浏览器验证**

质检表单选 1 张图片 → Console 出现 `[cos] mock 模式...` 或上传成功 → 提交质检后详情抽屉质检记录可见。

- [ ] **Step 3: Commit**

```bash
git add admin/src/cos.js
git commit -m "feat(p5): cos presigned upload helper for inspection photos"
```

---

### Task 6: 端到端验收 + 文档

**Files:**
- Create: `admin/README.md`

- [ ] **Step 1: 写 README**

```markdown
# RePhone 管理后台（P5）

## 运行
1. 后端：`cd server && docker compose up -d && mvnw.cmd -DskipTests package`
   `set ADMIN_TOKEN=你的token && java -jar start\target\rephone-server.jar`（需 WX_MOCK_LOGIN=true 等开发环境变量）
2. 前端：`cd admin && npm install && npm run dev` → http://localhost:5173
3. 登录：输入 ADMIN_TOKEN

## 功能
回收订单列表（状态筛选/分页）→ 详情抽屉 → 开始质检(20→30) → 提交质检(30→40，照片经 COS 直传)
→ 触发打款(40→50) → 标记完成(50→60)；每次状态流转写 order_status_log 并触发用户订阅消息。
```

- [ ] **Step 2: 全流程 E2E**

小程序造单（估价→下单→填运单号）→ 管理后台推进 20→30→40（质检 100.00 元）→ 小程序"确认打款"→ 50 → 后台标记 60 → 小程序详情显示"已完成"。
Expected: 两端状态一致；`order_status_log` 每单 7 条。

- [ ] **Step 3: Commit**

```bash
git add admin/README.md
git commit -m "docs(p5): admin console runbook"
```

---

## Self-Review 记录

- 覆盖检查：P5 验收（列表/详情/改状态/录质检/传照片）→ Task 3/4/5 全覆盖；后端缺口（详情、管理端签名）→ Task 1 ✅
- 占位符扫描：无 TBD/TODO；所有代码步骤给出完整代码 ✅
- 类型一致性：`RecycleOrderDetail` 字段与 P3 后端 DTO 一致；`uploadToCos` 返回 key 被 inspection.images 消费 ✅
