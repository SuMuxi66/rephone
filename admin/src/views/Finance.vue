<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-date-picker
          v-model="range"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
          @change="reload"
        />
        <el-button type="primary" @click="reload">查询</el-button>
      </el-space>
    </el-card>

    <el-row :gutter="12" class="cards">
      <el-col :span="5">
        <el-card shadow="never">
          <div class="card-label">出售收款</div>
          <div class="card-value income">¥{{ yuan(summary.saleIncomeFen) }}</div>
          <div class="card-sub">{{ summary.saleCount || 0 }} 笔</div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="never">
          <div class="card-label">维修收款</div>
          <div class="card-value income">¥{{ yuan(summary.repairIncomeFen) }}</div>
          <div class="card-sub">{{ summary.repairCount || 0 }} 笔</div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="never">
          <div class="card-label">回收打款</div>
          <div class="card-value payout">¥{{ yuan(summary.recyclePayFen) }}</div>
          <div class="card-sub">{{ summary.recycleCount || 0 }} 笔</div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="never">
          <div class="card-label">出售退款</div>
          <div class="card-value payout">¥{{ yuan(summary.saleRefundFen) }}</div>
          <div class="card-sub">{{ summary.refundCount || 0 }} 笔</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never" class="net-card">
          <div class="card-label">净额</div>
          <div class="card-value" :class="(summary.netFen || 0) >= 0 ? 'income' : 'payout'">
            ¥{{ yuan(summary.netFen) }}
          </div>
          <div class="card-sub">收 + 修 − 退 − 付</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-space wrap class="flow-toolbar">
        <el-select v-model="bizFilter" style="width: 140px" @change="reloadFlows">
          <el-option label="全部业务" :value="0" />
          <el-option label="回收打款" :value="10" />
          <el-option label="出售收款" :value="20" />
          <el-option label="维修收款" :value="30" />
        </el-select>
      </el-space>
      <el-table v-loading="flowLoading" :data="flows" border stripe>
        <el-table-column prop="orderNo" label="单号" min-width="220" />
        <el-table-column label="业务" width="110">
          <template #default="{ row }">
            <el-tag :type="(FINANCE_BIZ[row.biz] || {}).type || 'info'">
              {{ (FINANCE_BIZ[row.biz] || {}).label || row.biz }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="方向" width="90">
          <template #default="{ row }">
            {{ (FLOW_DIRECTION[row.direction] || {}).label || row.direction }}
          </template>
        </el-table-column>
        <el-table-column label="金额(元)" width="130" align="right">
          <template #default="{ row }">
            <span :class="row.direction === 'income' ? 'income' : 'payout'">¥{{ yuan(row.amountFen) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态值" width="90" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
      <el-pagination
        class="pager"
        layout="prev, pager, next, total"
        :total="flowTotal"
        :page-size="flowPageSize"
        :current-page="flowPageNum"
        @current-change="onFlowPage"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { fen2yuan, fetchFinanceFlows, fetchFinanceSummary, FINANCE_BIZ, FLOW_DIRECTION } from '../api/admin';

const range = ref(defaultRange());
const summary = ref({});
const bizFilter = ref(0);
const flows = ref([]);
const flowTotal = ref(0);
const flowPageNum = ref(1);
const flowPageSize = 20;
const flowLoading = ref(false);

function defaultRange() {
  const end = new Date();
  const start = new Date(end.getTime() - 29 * 24 * 3600 * 1000);
  const fmt = (d) => d.toISOString().slice(0, 10);
  return [fmt(start), fmt(end)];
}

const yuan = (fen) => fen2yuan(fen);

function summaryParams() {
  return { from: range.value[0], to: range.value[1] };
}

async function loadSummary() {
  summary.value = await fetchFinanceSummary(summaryParams());
}

async function loadFlows() {
  flowLoading.value = true;
  try {
    const params = { ...summaryParams(), pageNum: flowPageNum.value, pageSize: flowPageSize };
    if (bizFilter.value) params.biz = bizFilter.value;
    const page = await fetchFinanceFlows(params);
    flows.value = page.records || [];
    flowTotal.value = page.total || 0;
  } finally {
    flowLoading.value = false;
  }
}

function reloadFlows() {
  flowPageNum.value = 1;
  loadFlows();
}

function onFlowPage(page) {
  flowPageNum.value = page;
  loadFlows();
}

function reload() {
  loadSummary();
  reloadFlows();
}

onMounted(reload);
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.cards {
  margin-bottom: 16px;
}
.card-label {
  color: #909399;
  font-size: 13px;
}
.card-value {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
}
.card-sub {
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 4px;
}
.income {
  color: #2e7d53;
}
.payout {
  color: #a8321f;
}
.net-card {
  background: #fafafa;
}
.flow-toolbar {
  margin-bottom: 12px;
}
.pager {
  margin-top: 16px;
}
</style>
