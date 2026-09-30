<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-select v-model="statusFilter" style="width: 140px" @change="reload">
          <el-option v-for="(s, k) in AFTER_SALE_STATUS" :key="k" :label="s.label" :value="Number(k)" />
          <el-option label="全部状态" :value="0" />
        </el-select>
        <el-input v-model="keyword" placeholder="售后单号/订单号" clearable style="width: 260px" @keyup.enter="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
      </el-space>
    </el-card>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="asNo" label="售后单号" width="220" />
      <el-table-column prop="orderNo" label="关联订单号" width="220" />
      <el-table-column label="类型" width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ row.type === 10 ? '仅退款' : row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="申请原因" min-width="180" show-overflow-tooltip />
      <el-table-column label="退款金额" width="110" align="right">
        <template #default="{ row }">{{ row.refundFen ? '¥' + fen2yuan(row.refundFen) : '--' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="AFTER_SALE_STATUS[row.status]?.type || 'info'">
            {{ AFTER_SALE_STATUS[row.status]?.label || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="170" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 10" link type="primary" @click="openReview(row)">审核</el-button>
          <span v-else class="no-action">{{ AFTER_SALE_STATUS[row.status]?.label }}{{ row.adminRemark ? '·' + row.adminRemark : '' }}</span>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="prev, pager, next, total" :total="total" :page-size="pageSize"
      :current-page="pageNum" @current-change="onPage" />

    <el-dialog v-model="reviewVisible" title="售后审核（仅退款）" width="460px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="售后单号">{{ target?.asNo }}</el-descriptions-item>
        <el-descriptions-item label="关联订单">{{ target?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ target?.reason }}</el-descriptions-item>
      </el-descriptions>
      <el-input v-model="adminRemark" type="textarea" :rows="2" maxlength="255" placeholder="审核说明（将同步给订单备注）"
        style="margin-top: 14px" />
      <template #footer>
        <el-button type="danger" :loading="submitting" @click="onReject">拒绝退款</el-button>
        <el-button type="primary" :loading="submitting" @click="onAgree">同意并退款（mock 到账）</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  AFTER_SALE_STATUS,
  agreeAfterSale,
  fetchAfterSales,
  fen2yuan,
  rejectAfterSale,
} from '../api/admin';

const statusFilter = ref(0);
const keyword = ref('');
const records = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const submitting = ref(false);
const reviewVisible = ref(false);
const target = ref(null);
const adminRemark = ref('');

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (statusFilter.value > 0) params.status = statusFilter.value;
    if (keyword.value.trim()) params.keyword = keyword.value.trim();
    const page = await fetchAfterSales(params);
    records.value = page.records || [];
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

function openReview(row) {
  target.value = row;
  adminRemark.value = '';
  reviewVisible.value = true;
}

async function onAgree() {
  if (!adminRemark.value.trim()) {
    ElMessage.warning('请填写审核说明');
    return;
  }
  submitting.value = true;
  try {
    await agreeAfterSale(target.value.asNo, adminRemark.value.trim());
    ElMessage.success('已同意，退款 mock 到账，订单已置为已退款');
    reviewVisible.value = false;
    load();
  } finally {
    submitting.value = false;
  }
}

async function onReject() {
  if (!adminRemark.value.trim()) {
    ElMessage.warning('请填写拒绝原因');
    return;
  }
  submitting.value = true;
  try {
    await rejectAfterSale(target.value.asNo, adminRemark.value.trim());
    ElMessage.success('已拒绝');
    reviewVisible.value = false;
    load();
  } finally {
    submitting.value = false;
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
.no-action {
  color: #909399;
  font-size: 12px;
}
</style>
