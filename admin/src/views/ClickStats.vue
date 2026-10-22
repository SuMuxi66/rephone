<template>
  <div v-loading="loading">
    <el-row :gutter="12" class="cards">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="card-label">今日商品点击</div>
          <div class="card-value">{{ summary.today.clicks || 0 }}</div>
          <div class="card-sub">曝光 {{ summary.today.exposes || 0 }} · 启动 {{ summary.today.appOpens || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="card-label">今日点击率</div>
          <div class="card-value">{{ pct(summary.today.ctr) }}</div>
          <div class="card-sub">点击 / 曝光</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="card-label">近 7 天点击率</div>
          <div class="card-value">{{ pct(summary.last7.ctr) }}</div>
          <div class="card-sub">点击 {{ summary.last7.clicks || 0 }} / 曝光 {{ summary.last7.exposes || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="card-label">近 30 天启动</div>
          <div class="card-value">{{ summary.last30.appOpens || 0 }}</div>
          <div class="card-sub">曝光 {{ summary.last30.exposes || 0 }} · 点击 {{ summary.last30.clicks || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="cards">
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">新增订单转化</div>
          <el-table :data="orderRows" border size="small">
            <el-table-column prop="label" label="窗口" width="110" />
            <el-table-column prop="sale" label="出售单" align="right" />
            <el-table-column prop="recycle" label="回收单" align="right" />
            <el-table-column prop="repair" label="维修单" align="right" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title with-actions">
            <span>启动 / 曝光 / 点击趋势</span>
            <el-radio-group v-model="trendDays" size="small" @change="loadTrend">
              <el-radio-button :value="7">7 天</el-radio-button>
              <el-radio-button :value="14">14 天</el-radio-button>
              <el-radio-button :value="30">30 天</el-radio-button>
            </el-radio-group>
          </div>
          <div class="trend">
            <div
              v-for="d in trend"
              :key="d.date"
              class="trend-col"
              :title="`${d.date} 启动${d.appOpens} 曝光${d.exposes} 点击${d.clicks}`"
            >
              <div class="trend-bars">
                <div class="bar bar-open" :style="{ height: barHeight(d.appOpens) }" />
                <div class="bar bar-expose" :style="{ height: barHeight(d.exposes) }" />
                <div class="bar bar-click" :style="{ height: barHeight(d.clicks) }" />
              </div>
              <div class="trend-date">{{ d.date.slice(5) }}</div>
            </div>
          </div>
          <div class="legend">
            <span class="dot dot-open" />启动
            <span class="dot dot-expose" />曝光
            <span class="dot dot-click" />点击
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title with-actions">
            <span>商品点击榜</span>
            <el-radio-group v-model="topDays" size="small" @change="loadTop">
              <el-radio-button :value="7">7 天</el-radio-button>
              <el-radio-button :value="30">30 天</el-radio-button>
            </el-radio-group>
          </div>
          <el-table :data="top.goods" border size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="商品" min-width="160" show-overflow-tooltip />
            <el-table-column prop="exposes" label="曝光" width="70" align="right" />
            <el-table-column prop="clicks" label="点击" width="70" align="right" />
            <el-table-column label="点击率" width="80" align="right">
              <template #default="{ row }">{{ pct(row.ctr) }}</template>
            </el-table-column>
            <el-table-column prop="orders" label="出售单" width="70" align="right" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div class="section-title">机型点击榜</div>
          <el-table :data="top.models" border size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="机型" min-width="160" show-overflow-tooltip />
            <el-table-column prop="clicks" label="点击" width="70" align="right" />
            <el-table-column prop="recycleOrders" label="回收单" width="70" align="right" />
            <el-table-column prop="repairOrders" label="维修单" width="70" align="right" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { fetchClickSummary, fetchClickTop, fetchClickTrend } from '../api/admin';

const loading = ref(false);
const summary = ref({});
const trend = ref([]);
const top = ref({ goods: [], models: [] });
const trendDays = ref(14);
const topDays = ref(30);

const orderRows = computed(() => [
  { label: '今日', sale: summary.value.today?.saleOrders || 0, recycle: summary.value.today?.recycleOrders || 0, repair: summary.value.today?.repairOrders || 0 },
  { label: '近 7 天', sale: summary.value.last7?.saleOrders || 0, recycle: summary.value.last7?.recycleOrders || 0, repair: summary.value.last7?.repairOrders || 0 },
  { label: '近 30 天', sale: summary.value.last30?.saleOrders || 0, recycle: summary.value.last30?.recycleOrders || 0, repair: summary.value.last30?.repairOrders || 0 },
]);

const maxTrend = computed(() => Math.max(1, ...trend.value.map((d) => Math.max(d.appOpens, d.exposes, d.clicks))));

function barHeight(count) {
  return `${Math.round((count / maxTrend.value) * 96) + 4}px`;
}

function pct(ctr) {
  return ctr == null ? '--' : `${(ctr * 100).toFixed(2)}%`;
}

async function loadTrend() {
  trend.value = (await fetchClickTrend(trendDays.value)) || [];
}

async function loadTop() {
  top.value = (await fetchClickTop({ days: topDays.value, limit: 10 })) || { goods: [], models: [] };
}

async function load() {
  loading.value = true;
  try {
    const [s] = await Promise.all([fetchClickSummary(), loadTrend(), loadTop()]);
    summary.value = s || {};
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
.with-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
.bar-open {
  background: #3a4048;
}
.bar-expose {
  background: #b4530f;
}
.bar-click {
  background: #2e7d53;
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
.dot-open {
  background: #3a4048;
}
.dot-expose {
  background: #b4530f;
}
.dot-click {
  background: #2e7d53;
}
</style>
