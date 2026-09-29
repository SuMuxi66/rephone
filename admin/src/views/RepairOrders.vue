<template>
  <div>
    <el-tabs v-model="activeStatus" @tab-change="reload">
      <el-tab-pane
        v-for="(label, key) in statusFilters"
        :key="key"
        :label="label"
        :name="key"
      />
    </el-tabs>

    <el-table v-loading="loading" :data="orders" border stripe>
      <el-table-column prop="orderNo" label="维修单号" width="200" />
      <el-table-column label="机型" min-width="160">
        <template #default="{ row }">{{ row.brandName }} {{ row.modelName }}</template>
      </el-table-column>
      <el-table-column label="合计(元)" width="120" align="right">
        <template #default="{ row }">{{ fen2yuan(row.totalFen) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="REPAIR_STATUS[row.status]?.type || 'info'">
            {{ REPAIR_STATUS[row.status]?.label || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      layout="prev, pager, next, total"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      @current-change="onPage"
    />

    <el-drawer v-model="drawer" title="维修单详情" size="500px">
      <template v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="维修单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="机型">{{ detail.brandName }} {{ detail.modelName }}</el-descriptions-item>
          <el-descriptions-item label="维修项目">
            {{ (detail.items || []).map((it) => `${it.name}（${fen2yuan(it.priceFen)}元）`).join('、') }}
          </el-descriptions-item>
          <el-descriptions-item label="合计(元)">{{ fen2yuan(detail.totalFen) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="REPAIR_STATUS[detail.status]?.type || 'info'">
              {{ REPAIR_STATUS[detail.status]?.label || detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="服务方式">{{ detail.serviceType === 20 ? '寄修' : '上门维修' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.contactName }} {{ detail.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="上门地址">{{ detail.address }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ detail.appointTime }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.remark" label="故障描述">{{ detail.remark }}</el-descriptions-item>
          <el-descriptions-item label="质保">{{ detail.warrantyDays }} 天</el-descriptions-item>
        </el-descriptions>

        <div class="section">
          <div class="section-title">操作</div>
          <el-space wrap>
            <el-button
              v-if="nextStatus"
              type="primary"
              @click="onAdvance"
            >推进到「{{ REPAIR_STATUS[nextStatus].label }}」</el-button>
            <span v-else class="no-action">当前状态无可执行操作</span>
          </el-space>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  REPAIR_STATUS,
  changeRepairStatus,
  fetchRepairOrderDetail,
  fetchRepairOrders,
  fen2yuan,
} from '../api/admin';

const statusFilters = { 0: '全部', 10: '待确认', 20: '已预约', 30: '维修中', 40: '待验收', 50: '已完成', 80: '已取消' };
const FLOW = [10, 20, 30, 40, 50];

const activeStatus = ref(0);
const orders = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const drawer = ref(false);
const detail = ref(null);

const nextStatus = computed(() => {
  const s = detail.value?.status;
  if (!FLOW.includes(s)) return null;
  return FLOW[FLOW.indexOf(s) + 1] || null;
});

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (Number(activeStatus.value) > 0) {
      params.status = Number(activeStatus.value);
    }
    const page = await fetchRepairOrders(params);
    orders.value = page.records || [];
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

async function openDetail(row) {
  detail.value = await fetchRepairOrderDetail(row.orderNo);
  drawer.value = true;
}

async function onAdvance() {
  const to = nextStatus.value;
  await ElMessageBox.confirm(`确认将维修单推进到「${REPAIR_STATUS[to].label}」？`, '状态变更');
  await changeRepairStatus(detail.value.orderNo, to, '后台更新状态');
  ElMessage.success('状态已更新');
  detail.value = await fetchRepairOrderDetail(detail.value.orderNo);
  load();
}

onMounted(load);
</script>

<style scoped>
.pager {
  margin-top: 16px;
}
.section {
  margin-top: 20px;
}
.section-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #303133;
}
.no-action {
  color: #909399;
  font-size: 13px;
}
</style>
