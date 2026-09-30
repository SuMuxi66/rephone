<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-select v-model="statusFilter" style="width: 140px" @change="reload">
          <el-option v-for="(s, k) in SALE_STATUS" :key="k" :label="s.label" :value="Number(k)" />
          <el-option label="全部状态" :value="0" />
        </el-select>
        <el-input v-model="keyword" placeholder="订单号/商品/收件人/手机号" clearable style="width: 240px" @keyup.enter="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
      </el-space>
    </el-card>

    <el-table v-loading="loading" :data="orders" border stripe>
      <el-table-column prop="orderNo" label="订单号" width="210" />
      <el-table-column prop="goodsName" label="商品" min-width="160" />
      <el-table-column label="单价" width="100" align="right">
        <template #default="{ row }">¥{{ fen2yuan(row.priceFen) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="70" align="center" />
      <el-table-column label="合计" width="110" align="right">
        <template #default="{ row }">¥{{ fen2yuan(row.totalFen) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="SALE_STATUS[row.status]?.type || 'info'">
            {{ SALE_STATUS[row.status]?.label || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收件人" width="120">
        <template #default="{ row }">{{ row.receiverName }} {{ row.receiverPhone }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="prev, pager, next, total" :total="total" :page-size="pageSize"
      :current-page="pageNum" @current-change="onPage" />

    <el-drawer v-model="drawer" title="出售订单详情" size="480px">
      <template v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ detail.goodsName }} × {{ detail.quantity }}</el-descriptions-item>
          <el-descriptions-item label="合计">¥{{ fen2yuan(detail.totalFen) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="SALE_STATUS[detail.status]?.type || 'info'">
              {{ SALE_STATUS[detail.status]?.label || detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收件人">{{ detail.receiverName }} {{ detail.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ detail.receiverAddr }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.payNo" label="支付流水">{{ detail.payNo }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.expressNo" label="快递">{{ detail.expressCompany }} {{ detail.expressNo }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.refundFen" label="退款">¥{{ fen2yuan(detail.refundFen) }}（{{ detail.refundReason }}）</el-descriptions-item>
          <el-descriptions-item v-if="detail.remark" label="买家备注">{{ detail.remark }}</el-descriptions-item>
        </el-descriptions>

        <div class="section">
          <div class="section-title">操作</div>
          <el-space wrap>
            <el-button v-if="detail.status === 20" type="warning" @click="shipVisible = true">发货</el-button>
            <el-button
              v-if="detail.status === 20 || detail.status === 30"
              type="danger"
              @click="onRefund"
            >整单退款（¥{{ fen2yuan(detail.totalFen) }}）</el-button>
            <span v-if="![20, 30].includes(detail.status)" class="no-action">当前状态无可执行操作</span>
          </el-space>
        </div>
      </template>

      <el-dialog v-model="shipVisible" title="发货" width="420px">
        <el-form label-position="top">
          <el-form-item label="快递公司" required>
            <el-input v-model="shipForm.expressCompany" placeholder="如 顺丰速运" />
          </el-form-item>
          <el-form-item label="快递单号" required>
            <el-input v-model="shipForm.expressNo" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="shipVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="onShip">确认发货</el-button>
        </template>
      </el-dialog>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  SALE_STATUS,
  fetchSaleOrderDetail,
  fetchSaleOrders,
  fen2yuan,
  refundSaleOrder,
  shipSaleOrder,
} from '../api/admin';

const statusFilter = ref(0);
const keyword = ref('');
const orders = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const drawer = ref(false);
const detail = ref(null);
const submitting = ref(false);
const shipVisible = ref(false);
const shipForm = reactive({ expressCompany: '', expressNo: '' });

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (statusFilter.value > 0) params.status = statusFilter.value;
    if (keyword.value.trim()) params.keyword = keyword.value.trim();
    const page = await fetchSaleOrders(params);
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
  detail.value = await fetchSaleOrderDetail(row.orderNo);
  drawer.value = true;
}

async function refreshDetail() {
  if (detail.value) {
    detail.value = await fetchSaleOrderDetail(detail.value.orderNo);
  }
  load();
}

async function onShip() {
  if (!shipForm.expressCompany.trim() || !shipForm.expressNo.trim()) {
    ElMessage.warning('请填写快递公司与单号');
    return;
  }
  submitting.value = true;
  try {
    await shipSaleOrder(detail.value.orderNo, { ...shipForm });
    ElMessage.success('已发货');
    shipVisible.value = false;
    shipForm.expressCompany = '';
    shipForm.expressNo = '';
    refreshDetail();
  } finally {
    submitting.value = false;
  }
}

async function onRefund() {
  const { value } = await ElMessageBox.prompt('整单退款将原路退回（mock），请输入退款原因：', '整单退款', {
    inputPlaceholder: '退款原因（必填）',
    inputValidator: (v) => (v && v.trim() ? true : '退款原因必填'),
  });
  await refundSaleOrder(detail.value.orderNo, value.trim());
  ElMessage.success('退款完成（mock 到账）');
  refreshDetail();
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
