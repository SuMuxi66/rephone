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
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="机型" min-width="160">
        <template #default="{ row }">{{ row.brandName }} {{ row.modelName }}</template>
      </el-table-column>
      <el-table-column prop="storage" label="内存" width="90" />
      <el-table-column prop="conditionLabel" label="成色" width="110" />
      <el-table-column label="估价(元)" width="110" align="right">
        <template #default="{ row }">{{ fen2yuan(row.quoteFen) }}</template>
      </el-table-column>
      <el-table-column label="最终价(元)" width="110" align="right">
        <template #default="{ row }">{{ row.finalFen == null ? '--' : fen2yuan(row.finalFen) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="RECYCLE_STATUS[row.status]?.type || 'info'">
            {{ RECYCLE_STATUS[row.status]?.label || row.status }}
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

    <el-drawer v-model="drawer" title="回收单详情" size="520px">
      <template v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="机型">{{ detail.brandName }} {{ detail.modelName }}</el-descriptions-item>
          <el-descriptions-item label="配置">{{ detail.storage }} · {{ detail.conditionLabel }}</el-descriptions-item>
          <el-descriptions-item label="功能问题">{{ detail.issues?.length ? detail.issues.join('、') : '无' }}</el-descriptions-item>
          <el-descriptions-item label="估价(元)">{{ fen2yuan(detail.quoteFen) }}</el-descriptions-item>
          <el-descriptions-item label="最终价(元)">{{ detail.finalFen == null ? '--' : fen2yuan(detail.finalFen) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="RECYCLE_STATUS[detail.status]?.type || 'info'">
              {{ RECYCLE_STATUS[detail.status]?.label || detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="取件方式">{{ detail.pickupType === 20 ? '上门取件' : '用户邮寄' }}</el-descriptions-item>
          <el-descriptions-item label="取件人">{{ detail.pickupName }} {{ detail.pickupPhone }}</el-descriptions-item>
          <el-descriptions-item label="取件地址">{{ detail.pickupAddress }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.expressNo" label="运单号">{{ detail.expressCompany }} {{ detail.expressNo }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.remark" label="用户备注">{{ detail.remark }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.adminRemark" label="质检说明">{{ detail.adminRemark }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="detail.inspections?.length" class="section">
          <div class="section-title">质检记录</div>
          <el-card v-for="(item, i) in detail.inspections" :key="i" shadow="never" class="insp-card">
            <div class="insp-row">
              <span>{{ item.result }}</span>
              <b>{{ fen2yuan(item.finalFen) }} 元</b>
            </div>
            <div class="insp-time">{{ item.createTime }}</div>
          </el-card>
        </div>

        <div class="section">
          <div class="section-title">操作</div>
          <el-space wrap>
            <el-button
              v-if="detail.status === 20"
              type="primary"
              @click="onAdvance(30, '收货入检')"
            >推进到质检中</el-button>
            <el-button
              v-if="detail.status === 30"
              type="warning"
              @click="inspectionVisible = true"
            >提交质检结果</el-button>
            <el-button
              v-if="detail.status === 40"
              type="success"
              @click="onPayout"
            >触发打款（{{ fen2yuan(detail.finalFen) }} 元）</el-button>
            <span v-if="!nextActionExists" class="no-action">当前状态无可执行操作</span>
          </el-space>
        </div>
      </template>

      <el-dialog v-model="inspectionVisible" title="提交质检结果" width="440px">
        <el-form label-position="top">
          <el-form-item label="质检结论" required>
            <el-input v-model="inspectionForm.result" type="textarea" :rows="3" maxlength="500" placeholder="外观/功能检测结果描述" />
          </el-form-item>
          <el-form-item label="最终回收价（元）" required>
            <el-input-number v-model="inspectionForm.finalYuan" :min="0.01" :precision="2" :step="10" style="width: 100%" />
          </el-form-item>
          <div class="tip">最终价必须为正且不超过估价两倍（{{ fen2yuan(detail?.quoteFen) }} × 2 = {{ quoteUpperText }} 元）</div>
        </el-form>
        <template #footer>
          <el-button @click="inspectionVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="onSubmitInspection">提交</el-button>
        </template>
      </el-dialog>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  RECYCLE_STATUS,
  changeRecycleStatus,
  fetchRecycleOrderDetail,
  fetchRecycleOrders,
  fen2yuan,
  payoutRecycle,
  submitInspection,
  yuan2fen,
} from '../api/admin';

const statusFilters = { 0: '全部', 10: '待寄出', 20: '运输中', 30: '质检中', 40: '待确认', 50: '已打款', 60: '已完成', 80: '已取消' };

const activeStatus = ref(0);
const orders = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const drawer = ref(false);
const detail = ref(null);
const submitting = ref(false);
const inspectionVisible = ref(false);
const inspectionForm = reactive({ result: '', finalYuan: null });

const quoteUpperText = computed(() =>
  detail.value ? fen2yuan((detail.value.quoteFen || 0) * 2) : '--',
);

const nextActionExists = computed(() => [20, 30, 40].includes(detail.value?.status));

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (Number(activeStatus.value) > 0) {
      params.status = Number(activeStatus.value);
    }
    const page = await fetchRecycleOrders(params);
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
  detail.value = await fetchRecycleOrderDetail(row.orderNo);
  drawer.value = true;
}

async function refreshDetail() {
  if (detail.value) {
    detail.value = await fetchRecycleOrderDetail(detail.value.orderNo);
  }
  load();
}

async function onAdvance(toStatus, remark) {
  await ElMessageBox.confirm(`确认将订单推进到「${RECYCLE_STATUS[toStatus].label}」？`, '状态变更');
  await changeRecycleStatus(detail.value.orderNo, toStatus, remark);
  ElMessage.success('状态已更新');
  refreshDetail();
}

async function onSubmitInspection() {
  if (!inspectionForm.result.trim() || !inspectionForm.finalYuan) {
    ElMessage.warning('请填写质检结论与最终价');
    return;
  }
  const finalFen = yuan2fen(inspectionForm.finalYuan);
  if (finalFen <= 0 || finalFen > detail.value.quoteFen * 2) {
    ElMessage.error(`最终价须为正且不超过估价两倍（${quoteUpperText.value} 元）`);
    return;
  }
  submitting.value = true;
  try {
    await submitInspection(detail.value.orderNo, {
      result: inspectionForm.result.trim(),
      finalFen,
      images: [],
    });
    ElMessage.success('质检结果已提交，订单进入待确认');
    inspectionVisible.value = false;
    inspectionForm.result = '';
    inspectionForm.finalYuan = null;
    refreshDetail();
  } finally {
    submitting.value = false;
  }
}

async function onPayout() {
  await ElMessageBox.confirm(
    `确认向用户打款 ${fen2yuan(detail.value.finalFen)} 元？打款后订单进入「已打款」。`,
    '触发打款',
  );
  await payoutRecycle(detail.value.orderNo);
  ElMessage.success('打款流程已发起');
  refreshDetail();
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
.insp-card {
  margin-bottom: 8px;
}
.insp-row {
  display: flex;
  justify-content: space-between;
}
.insp-time {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}
.no-action {
  color: #909399;
  font-size: 13px;
}
.tip {
  color: #909399;
  font-size: 12px;
}
</style>
