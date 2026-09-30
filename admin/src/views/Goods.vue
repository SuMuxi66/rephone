<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-input v-model="keyword" placeholder="商品名" clearable style="width: 200px" @keyup.enter="reload" />
        <el-select v-model="statusFilter" style="width: 120px" @change="reload">
          <el-option label="全部状态" :value="-1" />
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="reload">查询</el-button>
        <el-button type="success" @click="openCreate">新增商品</el-button>
      </el-space>
    </el-card>

    <el-table v-loading="loading" :data="goods" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image v-if="row.image" :src="row.image" fit="contain" class="thumb" :preview-src-list="[row.image]" preview-teleported />
          <div v-else class="thumb-ph">无图</div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名" min-width="180" />
      <el-table-column prop="brand" label="品牌" width="100" />
      <el-table-column prop="storage" label="内存" width="90" />
      <el-table-column prop="conditionLevel" label="成色" width="90" />
      <el-table-column label="售价" width="110" align="right">
        <template #default="{ row }">¥{{ fen2yuan(row.priceFen) }}</template>
      </el-table-column>
      <el-table-column label="原价" width="100" align="right">
        <template #default="{ row }">{{ row.originalPriceFen ? '¥' + fen2yuan(row.originalPriceFen) : '--' }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" align="right" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openInspect(row)">质检报告</el-button>
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'danger' : 'success'" @click="onToggle(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="prev, pager, next, total" :total="total" :page-size="pageSize"
      :current-page="pageNum" @current-change="onPage" />

    <el-dialog v-model="dialog" :title="editing ? '编辑商品' : '新增商品'" width="480px">
      <el-form label-position="top">
        <el-form-item label="商品名" required>
          <el-input v-model="form.name" maxlength="128" />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="form.brand" placeholder="如 Apple" maxlength="32" />
        </el-form-item>
        <el-form-item label="内存/容量">
          <el-input v-model="form.storage" placeholder="如 256G" maxlength="16" />
        </el-form-item>
        <el-form-item label="成色">
          <el-input v-model="form.conditionLevel" placeholder="如 95新" maxlength="16" />
        </el-form-item>
        <el-form-item label="标签（英文逗号分隔）">
          <el-input v-model="form.tags" placeholder="如 官方自营,已验机" maxlength="255" />
        </el-form-item>
        <el-form-item label="商品图（可复用机型图片上传，或直接填 URL）">
          <el-input v-model="form.image" placeholder="/img/models/xxx.jpg 或完整 URL" />
        </el-form-item>
        <el-form-item label="售价（元）" required>
          <el-input-number v-model="form.priceYuan" :min="0.01" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原价（元，可选）">
          <el-input-number v-model="form.originalPriceYuan" :min="0" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.descText" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="inspectDialog" :title="`质检报告 · ${inspectGoods ? inspectGoods.name : ''}`" width="760px">
      <el-form label-position="top">
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="质检工程师">
              <el-input v-model="inspectForm.inspector" maxlength="32" placeholder="如 质检员小李" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="质检时间">
              <el-date-picker
                v-model="inspectForm.inspectedAt"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="默认当前时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电池健康度（%）">
              <el-input-number v-model="inspectForm.batteryHealth" :min="0" :max="100" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="综合结论">
          <el-input v-model="inspectForm.summary" type="textarea" :rows="2" maxlength="512"
            placeholder="如：整机功能正常，屏幕左上角有细微划痕" />
        </el-form-item>
        <el-form-item label="报告图片 URL（每行一个，最多 9 张）">
          <el-input v-model="inspectForm.imagesText" type="textarea" :rows="2" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="检查项">
          <el-table :data="inspectForm.items" border size="small">
            <el-table-column label="分组" width="110">
              <template #default="{ row }">
                <el-select v-model="row.category" size="small">
                  <el-option v-for="c in CATEGORIES" :key="c" :label="c" :value="c" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="检查项" width="150">
              <template #default="{ row }">
                <el-input v-model="row.name" size="small" maxlength="32" placeholder="如 屏幕显示" />
              </template>
            </el-table-column>
            <el-table-column label="结论" width="150">
              <template #default="{ row }">
                <el-select v-model="row.result" size="small" filterable allow-create default-first-option>
                  <el-option v-for="r in RESULTS" :key="r" :label="r" :value="r" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="说明（损伤位置等）">
              <template #default="{ row }">
                <el-input v-model="row.note" size="small" maxlength="128" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70">
              <template #default="{ $index }">
                <el-button link type="danger" @click="inspectForm.items.splice($index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button class="add-item" @click="addInspectItem">新增检查项</el-button>
          <div class="inspect-tip">
            检查项与结论为空的行使会被忽略；把检查项全部清空再保存 = 删除该商品的质检报告。
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inspectDialog = false">取消</el-button>
        <el-button type="primary" :loading="inspectSubmitting" @click="onSaveInspection">保存报告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createGoods,
  fetchGoods,
  fetchGoodsInspection,
  fen2yuan,
  saveGoodsInspection,
  setGoodsStatus,
  updateGoods,
} from '../api/admin';

const CATEGORIES = ['外观', '屏幕', '功能', '拆修', '其他'];
const RESULTS = ['正常', '轻微划痕', '明显划痕', '轻微磕碰', '凹陷', '已更换', '异常'];

const keyword = ref('');
const statusFilter = ref(-1);
const goods = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const submitting = ref(false);
const dialog = ref(false);
const editing = ref(null);
const form = reactive({
  name: '',
  image: '',
  priceYuan: null,
  originalPriceYuan: null,
  stock: 1,
  descText: '',
  brand: '',
  storage: '',
  conditionLevel: '',
  tags: '',
});

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (keyword.value.trim()) params.keyword = keyword.value.trim();
    if (statusFilter.value >= 0) params.status = statusFilter.value;
    const page = await fetchGoods(params);
    goods.value = page.records || [];
    total.value = page.total || 0;
  } finally {
    loading.value = false;
  }
}

function reload() {
  pageNum.value = 1;
  load();
}

function onPage(page) {
  pageNum.value = page;
  load();
}

function openCreate() {
  editing.value = null;
  Object.assign(form, {
    name: '',
    image: '',
    priceYuan: null,
    originalPriceYuan: null,
    stock: 1,
    descText: '',
    brand: '',
    storage: '',
    conditionLevel: '',
    tags: '',
  });
  dialog.value = true;
}

function openEdit(row) {
  editing.value = row;
  Object.assign(form, {
    name: row.name,
    image: row.image || '',
    priceYuan: row.priceFen / 100,
    originalPriceYuan: row.originalPriceFen ? row.originalPriceFen / 100 : null,
    stock: row.stock,
    descText: row.descText || '',
    brand: row.brand || '',
    storage: row.storage || '',
    conditionLevel: row.conditionLevel || '',
    tags: row.tags || '',
  });
  dialog.value = true;
}

async function onSubmit() {
  if (!form.name.trim() || !form.priceYuan) {
    ElMessage.warning('请填写商品名与售价');
    return;
  }
  submitting.value = true;
  try {
    if (editing.value) {
      await updateGoods(editing.value.id, { ...form });
      ElMessage.success('商品已更新');
    } else {
      await createGoods({ ...form });
      ElMessage.success('商品已创建并上架');
    }
    dialog.value = false;
    load();
  } finally {
    submitting.value = false;
  }
}

async function onToggle(row) {
  await ElMessageBox.confirm(`确认${row.status === 1 ? '下架' : '上架'}「${row.name}」？`, '商品状态');
  await setGoodsStatus(row.id, row.status === 1 ? 0 : 1);
  ElMessage.success('已更新');
  load();
}

// ===== 质检报告 =====
const inspectDialog = ref(false);
const inspectSubmitting = ref(false);
const inspectGoods = ref(null);
const inspectForm = reactive({
  inspector: '',
  inspectedAt: '',
  batteryHealth: null,
  summary: '',
  imagesText: '',
  items: [],
});

async function openInspect(row) {
  inspectGoods.value = row;
  Object.assign(inspectForm, {
    inspector: '',
    inspectedAt: '',
    batteryHealth: null,
    summary: '',
    imagesText: '',
    items: [],
  });
  try {
    const report = await fetchGoodsInspection(row.id);
    if (report) {
      Object.assign(inspectForm, {
        inspector: report.inspector || '',
        // 后端 LocalDateTime 为 ISO（2026-09-30T10:30:00），需转成 picker 的 value-format
        inspectedAt: report.inspectedAt ? String(report.inspectedAt).replace('T', ' ').slice(0, 19) : '',
        batteryHealth: report.batteryHealth ?? null,
        summary: report.summary || '',
        imagesText: (report.images || []).join('\n'),
        items: (report.items || []).map((it) => ({ ...it })),
      });
    }
  } catch {
    // 尚无质检报告属于正常情况，保持空表单
  }
  inspectDialog.value = true;
}

function addInspectItem() {
  inspectForm.items.push({ category: '外观', name: '', result: '正常', note: '' });
}

async function onSaveInspection() {
  if (!inspectGoods.value) return;
  inspectSubmitting.value = true;
  try {
    const images = inspectForm.imagesText
      .split('\n')
      .map((s) => s.trim())
      .filter(Boolean);
    await saveGoodsInspection(inspectGoods.value.id, {
      inspector: inspectForm.inspector,
      inspectedAt: inspectForm.inspectedAt || null,
      batteryHealth: inspectForm.batteryHealth,
      summary: inspectForm.summary,
      images,
      items: inspectForm.items,
    });
    ElMessage.success('质检报告已保存');
    inspectDialog.value = false;
  } finally {
    inspectSubmitting.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.pager {
  margin-top: 16px;
}
.thumb {
  width: 44px;
  height: 44px;
}
.thumb-ph {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 6px;
  color: #c0c4cc;
  font-size: 12px;
}
.add-item {
  width: 100%;
  margin-top: 8px;
}
.inspect-tip {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}
</style>
