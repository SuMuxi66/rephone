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
      <el-table-column label="状态" width="110">
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

    <el-drawer v-model="drawer" title="维修单详情" size="520px">
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
          <el-descriptions-item :label="mailIn ? '回寄收件地址' : '上门地址'">{{ detail.address }}</el-descriptions-item>
          <el-descriptions-item v-if="!mailIn" label="预约时间">{{ detail.appointTime || '--' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.remark" label="故障描述">{{ detail.remark }}</el-descriptions-item>
          <el-descriptions-item label="质保">{{ detail.warrantyDays }} 天</el-descriptions-item>
        </el-descriptions>

        <!-- 寄修运单区 -->
        <div v-if="mailIn" class="section">
          <div class="section-title">寄修运单</div>
          <el-space direction="vertical" fill style="width: 100%">
            <el-space wrap>
              <el-tag size="small" type="info">寄出</el-tag>
              <span v-if="detail.expressNo">{{ detail.expressCompany }} {{ detail.expressNo }}</span>
              <span v-else class="no-action">用户尚未寄出</span>
              <el-button v-if="detail.expressNo" link type="primary" @click="openTrace('out')">查轨迹</el-button>
            </el-space>
            <el-space wrap>
              <el-tag size="small" type="info">回寄</el-tag>
              <span v-if="detail.returnExpressNo">{{ detail.returnExpressCompany }} {{ detail.returnExpressNo }}</span>
              <span v-else class="no-action">维修完成后填写回寄单号</span>
              <el-button v-if="detail.returnExpressNo" link type="primary" @click="openTrace('return')">查轨迹</el-button>
            </el-space>
          </el-space>
        </div>

        <div class="section">
          <div class="section-title">操作</div>
          <el-space wrap>
            <el-button v-if="nextStatus" type="primary" @click="onAdvance">
              推进到「{{ REPAIR_STATUS[nextStatus].label }}」
            </el-button>
            <el-button v-if="mailIn && detail.status === 40" type="success" @click="openReturnSheet">
              填写回寄运单号
            </el-button>
            <span v-if="!nextStatus && !(mailIn && detail.status === 40)" class="no-action">
              当前状态无可执行操作
            </span>
          </el-space>
        </div>
      </template>
    </el-drawer>

    <!-- 回寄填单 -->
    <el-dialog v-model="returnVisible" title="填写回寄运单号" width="420px">
      <el-form label-position="top">
        <el-form-item label="快递公司" required>
          <el-select v-model="returnForm.expressCom" style="width: 100%" placeholder="选择快递公司">
            <el-option
              v-for="c in companies"
              :key="c.com"
              :label="c.name"
              :value="c.com"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="运单号（至少 6 位）" required>
          <el-input v-model="returnForm.expressNo" maxlength="32" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmitReturn">提交</el-button>
      </template>
    </el-dialog>

    <!-- 轨迹 -->
    <el-dialog v-model="traceVisible" :title="traceDirection === 'return' ? '回寄物流轨迹' : '寄出物流轨迹'" width="520px">
      <div v-loading="traceLoading" class="trace-body">
        <el-empty v-if="!traceLoading && !traceNodes.length" description="暂无物流轨迹" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="(node, i) in traceNodes"
            :key="i"
            :timestamp="node.time"
            :type="i === 0 ? 'primary' : ''"
          >
            {{ node.context }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  REPAIR_NEXT,
  REPAIR_STATUS,
  REPAIR_STATUS_FILTERS,
  changeRepairStatus,
  fetchExpressCompanies,
  fetchRepairOrderDetail,
  fetchRepairOrders,
  fetchRepairTrace,
  fillRepairReturnExpress,
  fen2yuan,
} from '../api/admin';

const statusFilters = REPAIR_STATUS_FILTERS;

const activeStatus = ref(0);
const orders = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const drawer = ref(false);
const detail = ref(null);
const submitting = ref(false);
const returnVisible = ref(false);
const companies = ref([]);
const returnForm = reactive({ expressCom: '', expressNo: '' });
const traceVisible = ref(false);
const traceLoading = ref(false);
const traceDirection = ref('out');
const traceNodes = ref([]);

const mailIn = computed(() => detail.value?.serviceType === 20);

const nextStatus = computed(() => {
  const d = detail.value;
  if (!d) return null;
  const map = d.serviceType === 20 ? REPAIR_NEXT.mailIn : REPAIR_NEXT.onsite;
  return map[d.status] || null;
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

async function openReturnSheet() {
  returnForm.expressCom = '';
  returnForm.expressNo = '';
  returnVisible.value = true;
  if (!companies.value.length) {
    try {
      companies.value = (await fetchExpressCompanies()) || [];
    } catch (e) {
      companies.value = [];
    }
  }
}

async function onSubmitReturn() {
  if (!returnForm.expressCom) {
    ElMessage.warning('请选择快递公司');
    return;
  }
  if (!returnForm.expressNo || returnForm.expressNo.trim().length < 6) {
    ElMessage.warning('请输入正确的运单号');
    return;
  }
  submitting.value = true;
  try {
    await fillRepairReturnExpress(detail.value.orderNo, {
      expressCom: returnForm.expressCom,
      expressCompany: (companies.value.find((c) => c.com === returnForm.expressCom) || {}).name || '',
      expressNo: returnForm.expressNo.trim(),
    });
    ElMessage.success('回寄单号已登记');
    returnVisible.value = false;
    detail.value = await fetchRepairOrderDetail(detail.value.orderNo);
    load();
  } finally {
    submitting.value = false;
  }
}

async function openTrace(direction) {
  traceDirection.value = direction;
  traceVisible.value = true;
  traceLoading.value = true;
  traceNodes.value = [];
  try {
    const trace = await fetchRepairTrace(detail.value.orderNo, direction);
    traceNodes.value = (trace.items || []).slice(0, 10);
  } finally {
    traceLoading.value = false;
  }
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
.trace-body {
  min-height: 160px;
  max-height: 50vh;
  overflow-y: auto;
}
</style>
