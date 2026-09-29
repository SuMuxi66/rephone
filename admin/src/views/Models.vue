<template>
  <div class="models-page">
    <el-card shadow="never" class="brand-card">
      <template #header>
        <div class="card-head">
          <span>品牌</span>
          <el-button size="small" type="primary" @click="brandVisible = true">新增品牌</el-button>
        </div>
      </template>
      <div
        v-for="b in brands"
        :key="b.id"
        class="brand-item"
        :class="{ active: b.id === activeBrandId }"
        @click="selectBrand(b.id)"
      >
        <span>{{ b.name }}</span>
        <el-tag size="small" type="info">{{ b.modelCount }} 机型</el-tag>
      </div>
    </el-card>

    <el-card shadow="never" class="model-card">
      <template #header>
        <div class="card-head">
          <span>机型{{ activeBrand ? `（${activeBrand.name}）` : '' }}</span>
          <el-button size="small" type="primary" :disabled="!activeBrandId" @click="openCreate">新增机型</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="models" border stripe>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="price-panel">
              <div v-for="p in row.prices" :key="p.storage" class="price-row">
                <span class="storage">{{ p.storage }}</span>
                <span class="price">{{ p.priceYuan }} 元</span>
                <el-button size="small" link type="primary" @click="openPrice(row, p)">改价</el-button>
              </div>
              <el-button size="small" @click="openPrice(row, null)">+ 新增内存档</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="机型" min-width="180" />
        <el-table-column prop="releaseYear" label="发布年份" width="100">
          <template #default="{ row }">{{ row.releaseYear || '--' }}</template>
        </el-table-column>
        <el-table-column label="内存档数" width="100">
          <template #default="{ row }">{{ row.prices.length }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="brandVisible" title="新增品牌" width="380px">
      <el-input v-model="brandName" placeholder="品牌名称（不超过 64 字）" />
      <template #footer>
        <el-button @click="brandVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onCreateBrand">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="modelVisible" :title="editing ? '编辑机型' : '新增机型'" width="560px">
      <el-form label-position="top">
        <el-form-item label="机型名称" required>
          <el-input v-model="modelForm.name" placeholder="如 iPhone 16 Pro Max" />
        </el-form-item>
        <el-form-item label="发布年份">
          <el-input-number v-model="modelForm.releaseYear" :min="2010" :max="2030" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="!editing" label="内存基准价（元，估价 = 基准价 × 成色系数 × 屏幕系数 − 故障扣减）" required>
          <div v-for="(p, i) in modelForm.prices" :key="i" class="dialog-price-row">
            <el-input v-model="p.storage" placeholder="如 128GB" style="width: 160px" />
            <el-input-number v-model="p.priceYuan" :min="1" :precision="2" :controls="false" placeholder="基准价(元)" style="width: 160px" />
            <el-button link type="danger" @click="modelForm.prices.splice(i, 1)">删除</el-button>
          </div>
          <el-button size="small" @click="modelForm.prices.push({ storage: '', priceYuan: null })">+ 加一档内存</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="modelVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmitModel">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="priceVisible" title="内存基准价" width="420px">
      <el-form label-position="top">
        <el-form-item label="内存规格" required>
          <el-input v-model="priceForm.storage" :disabled="!!priceForm.existing" placeholder="如 128GB" />
        </el-form-item>
        <el-form-item label="基准回收价（元）" required>
          <el-input-number v-model="priceForm.priceYuan" :min="1" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="priceVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmitPrice">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  createBrand,
  createModel,
  fetchBrands,
  fetchModels,
  updateModel,
  updateModelPrice,
} from '../api/admin';

const brands = ref([]);
const activeBrandId = ref(null);
const models = ref([]);
const loading = ref(false);
const submitting = ref(false);

const brandVisible = ref(false);
const brandName = ref('');
const modelVisible = ref(false);
const editing = ref(null);
const modelForm = reactive({ name: '', releaseYear: 2026, prices: [] });
const priceVisible = ref(false);
const priceForm = reactive({ modelId: null, storage: '', priceYuan: null, existing: null });

const activeBrand = computed(() => brands.value.find((b) => b.id === activeBrandId.value));

async function loadBrands() {
  brands.value = await fetchBrands();
  if (!activeBrandId.value && brands.value.length) {
    selectBrand(brands.value[0].id);
  }
}

async function selectBrand(id) {
  activeBrandId.value = id;
  loading.value = true;
  try {
    models.value = await fetchModels(id);
  } finally {
    loading.value = false;
  }
}

async function onCreateBrand() {
  if (!brandName.value.trim()) {
    ElMessage.warning('请输入品牌名');
    return;
  }
  submitting.value = true;
  try {
    await createBrand(brandName.value.trim());
    ElMessage.success('品牌已创建');
    brandVisible.value = false;
    brandName.value = '';
    await loadBrands();
  } finally {
    submitting.value = false;
  }
}

function openCreate() {
  editing.value = null;
  modelForm.name = '';
  modelForm.releaseYear = 2026;
  modelForm.prices = [{ storage: '128GB', priceYuan: null }];
  modelVisible.value = true;
}

function openEdit(row) {
  editing.value = row;
  modelForm.name = row.name;
  modelForm.releaseYear = row.releaseYear;
  modelVisible.value = true;
}

async function onSubmitModel() {
  if (!modelForm.name.trim()) {
    ElMessage.warning('请输入机型名称');
    return;
  }
  submitting.value = true;
  try {
    if (editing.value) {
      await updateModel(editing.value.id, {
        name: modelForm.name.trim(),
        releaseYear: modelForm.releaseYear,
      });
      ElMessage.success('机型已更新');
    } else {
      const prices = modelForm.prices.filter((p) => p.storage.trim() && p.priceYuan);
      if (!prices.length) {
        ElMessage.warning('请至少配置一档有效的内存基准价');
        return;
      }
      await createModel({
        brandId: activeBrandId.value,
        name: modelForm.name.trim(),
        releaseYear: modelForm.releaseYear,
        prices,
      });
      ElMessage.success('机型已创建，估价即时生效');
    }
    modelVisible.value = false;
    selectBrand(activeBrandId.value);
  } finally {
    submitting.value = false;
  }
}

function openPrice(row, p) {
  priceForm.modelId = row.id;
  priceForm.existing = p;
  priceForm.storage = p ? p.storage : '';
  priceForm.priceYuan = p ? p.priceYuan : null;
  priceVisible.value = true;
}

async function onSubmitPrice() {
  if (!priceForm.storage.trim() || !priceForm.priceYuan) {
    ElMessage.warning('请填写内存规格与基准价');
    return;
  }
  submitting.value = true;
  try {
    await updateModelPrice(priceForm.modelId, priceForm.storage.trim(), priceForm.priceYuan);
    ElMessage.success('基准价已更新，估价即时生效');
    priceVisible.value = false;
    selectBrand(activeBrandId.value);
  } finally {
    submitting.value = false;
  }
}

onMounted(loadBrands);
</script>

<style scoped>
.models-page {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.brand-card {
  width: 240px;
  flex-shrink: 0;
}
.model-card {
  flex: 1;
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
.brand-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  color: #303133;
}
.brand-item:hover {
  background: #f5f7fa;
}
.brand-item.active {
  background: #ecf5ff;
  color: #409eff;
}
.price-panel {
  padding: 8px 24px;
}
.price-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 6px 0;
}
.storage {
  width: 100px;
  color: #303133;
}
.price {
  color: #e6a23c;
  font-weight: 600;
}
.dialog-price-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}
</style>
