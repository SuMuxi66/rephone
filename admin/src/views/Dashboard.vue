<template>
  <div v-loading="loading">
    <el-row :gutter="12" class="cards">
      <el-col v-for="c in orderCards" :key="c.label" :span="6">
        <el-card shadow="never">
          <div class="card-label">{{ c.label }}</div>
          <div class="card-value">{{ c.value }}</div>
          <div class="card-sub">三线订单合计</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="cards">
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">订单总量</div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="回收单">{{ summary.recycleTotal || 0 }}</el-descriptions-item>
            <el-descriptions-item label="出售单">{{ summary.saleTotal || 0 }}</el-descriptions-item>
            <el-descriptions-item label="维修单">{{ summary.repairTotal || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">待处理</div>
          <el-descriptions :column="4" border size="small">
            <el-descriptions-item label="回收在途/质检">{{ summary.pendingRecycle || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待发货">{{ summary.pendingShip || 0 }}</el-descriptions-item>
            <el-descriptions-item label="维修进行中">{{ summary.pendingRepair || 0 }}</el-descriptions-item>
            <el-descriptions-item label="售后待审">{{ summary.pendingReview || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="cards">
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">近 30 天资金（元）</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="净额">
              <span :class="(summary.netFen30 || 0) >= 0 ? 'income' : 'payout'">{{ yuan(summary.netFen30) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="出售收款">{{ yuan(summary.saleIncomeFen30) }}</el-descriptions-item>
            <el-descriptions-item label="维修收款">{{ yuan(summary.repairIncomeFen30) }}</el-descriptions-item>
            <el-descriptions-item label="回收打款">{{ yuan(summary.recyclePayFen30) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">近 14 天单量趋势</div>
          <div class="trend">
            <div v-for="d in trend" :key="d.date" class="trend-col" :title="`${d.date} 回收${d.recycle} 出售${d.sale} 维修${d.repair}`">
              <div class="trend-bars">
                <div class="bar bar-recycle" :style="{ height: barHeight(d.recycle) }" />
                <div class="bar bar-sale" :style="{ height: barHeight(d.sale) }" />
                <div class="bar bar-repair" :style="{ height: barHeight(d.repair) }" />
              </div>
              <div class="trend-date">{{ d.date.slice(5) }}</div>
            </div>
          </div>
          <div class="legend">
            <span class="dot dot-recycle" />回收
            <span class="dot dot-sale" />出售
            <span class="dot dot-repair" />维修
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">近 30 天热门回收机型</div>
          <el-table :data="top.recycleModels" border size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="subName" label="品牌" width="110" />
            <el-table-column prop="name" label="机型" min-width="160" />
            <el-table-column prop="count" label="单数" width="80" align="right" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">近 30 天热销商品</div>
          <el-table :data="top.goods" border size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="商品" min-width="200" />
            <el-table-column prop="count" label="件数" width="80" align="right" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { fen2yuan, fetchDashboardSummary, fetchDashboardTop, fetchDashboardTrend } from '../api/admin';

const loading = ref(false);
const summary = ref({});
const trend = ref([]);
const top = ref({ recycleModels: [], goods: [] });

const orderCards = computed(() => [
  { label: '今日单量', value: summary.value.todayOrders || 0 },
  { label: '昨日单量', value: summary.value.yesterdayOrders || 0 },
  { label: '近 7 天', value: summary.value.last7Orders || 0 },
  { label: '近 30 天', value: summary.value.last30Orders || 0 },
]);

const maxTrend = computed(() => Math.max(1, ...trend.value.map((d) => Math.max(d.recycle, d.sale, d.repair))));

function barHeight(count) {
  return `${Math.round((count / maxTrend.value) * 96) + 4}px`;
}

const yuan = (fen) => fen2yuan(fen);

async function load() {
  loading.value = true;
  try {
    const [s, t, tp] = await Promise.all([
      fetchDashboardSummary(),
      fetchDashboardTrend(14),
      fetchDashboardTop(5),
    ]);
    summary.value = s || {};
    trend.value = t || [];
    top.value = tp || { recycleModels: [], goods: [] };
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.cards {
  margin-bottom: 16px;
}
.card-label {
  color: #909399;
  font-size: 13px;
}
.card-value {
  font-size: 24px;
  font-weight: 700;
  margin-top: 6px;
}
.card-sub {
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 4px;
}
.section-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}
.income {
  color: #2e7d53;
}
.payout {
  color: #a8321f;
}
.trend {
  display: flex;
  align-items: flex-end;
  gap: 6px;
  height: 130px;
  padding: 0 4px;
}
.trend-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.trend-bars {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 100px;
}
.bar {
  width: 8px;
  min-height: 2px;
  border-radius: 2px 2px 0 0;
}
.bar-recycle {
  background: #b4530f;
}
.bar-sale {
  background: #2e7d53;
}
.bar-repair {
  background: #3a4048;
}
.trend-date {
  margin-top: 4px;
  font-size: 11px;
  color: #9ba0a7;
  transform: scale(0.9);
}
.legend {
  margin-top: 8px;
  color: #5c6067;
  font-size: 12px;
}
.dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 2px;
  margin: 0 4px 0 10px;
}
.dot-recycle {
  background: #b4530f;
}
.dot-sale {
  background: #2e7d53;
}
.dot-repair {
  background: #3a4048;
}
</style>
