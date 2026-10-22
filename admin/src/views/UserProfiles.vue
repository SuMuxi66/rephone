<template>
  <div v-loading="loading">
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-input v-model="keyword" placeholder="昵称/手机号" clearable style="width: 220px" @keyup.enter="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
        <span class="hint">点击卡片看明细，右上角按钮翻到 AI 分析背面</span>
      </el-space>
    </el-card>

    <el-empty v-if="!cards.length && !loading" description="暂无用户行为数据（等待小程序上报）" />

    <el-row :gutter="12">
      <el-col v-for="c in cards" :key="c.userId" :span="6" class="card-col">
        <div class="flip-card" :class="{ 'is-flipped': flipped[c.userId] }">
          <div class="flip-inner">
            <el-card shadow="hover" class="face front" @click="openDetail(c)">
              <div class="user-head">
                <el-avatar :size="40" :src="c.avatarUrl || undefined">{{ (c.nickname || '?').slice(0, 1) }}</el-avatar>
                <div class="user-name">
                  <div class="name">{{ c.nickname || '未命名用户' }}</div>
                  <div class="phone">{{ c.phone || '--' }}</div>
                </div>
                <el-button
                  class="flip-toggle"
                  size="small"
                  text
                  @click.stop="toggleFlip(c)"
                >AI</el-button>
              </div>
              <div class="stats">
                <div class="stat"><div class="num">{{ c.openCount }}</div><div class="lbl">打开</div></div>
                <div class="stat"><div class="num">{{ c.viewCount }}</div><div class="lbl">浏览</div></div>
                <div class="stat"><div class="num">{{ c.topPriceBand || '--' }}</div><div class="lbl">价格档</div></div>
              </div>
              <div class="brand">偏好：{{ c.topBrand || '--' }} · 浏览均价 {{ yuan(c.avgPriceFen) }}</div>
              <div class="last-open">最近打开：{{ c.lastOpenTime || '--' }}</div>
            </el-card>
            <el-card shadow="hover" class="face back">
              <div class="back-title">AI 画像分析</div>
              <div class="ai-text">{{ c.aiSummary || '尚未生成 AI 分析' }}</div>
              <div class="back-actions">
                <el-button
                  size="small"
                  type="primary"
                  :loading="genLoading[c.userId]"
                  @click.stop="generate(c)"
                >{{ c.aiSummary ? '重新生成' : '生成分析' }}</el-button>
                <el-button size="small" text @click.stop="toggleFlip(c)">返回正面</el-button>
              </div>
            </el-card>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-pagination
      class="pager"
      layout="prev, pager, next, total"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      @current-change="onPage"
    />

    <el-drawer v-model="drawerVisible" size="58%" :title="detail ? (detail.nickname || '用户画像') : '用户画像'">
      <div v-if="detail" v-loading="detailLoading">
        <el-descriptions :column="3" border size="small" class="block">
          <el-descriptions-item label="手机号">{{ detail.phone || '--' }}</el-descriptions-item>
          <el-descriptions-item label="打开次数">{{ detail.openTimes.length }}</el-descriptions-item>
          <el-descriptions-item label="浏览次数">{{ detail.views.length }}</el-descriptions-item>
          <el-descriptions-item label="出售单">{{ detail.saleOrders }}</el-descriptions-item>
          <el-descriptions-item label="回收单">{{ detail.recycleOrders }}</el-descriptions-item>
          <el-descriptions-item label="维修单">{{ detail.repairOrders }}</el-descriptions-item>
        </el-descriptions>

        <div class="block">
          <div class="section-title">品牌偏好 / 价格档分布</div>
          <el-space wrap>
            <el-tag v-for="(n, b) in detail.brandDist" :key="'b' + b" type="warning">{{ b }} × {{ n }}</el-tag>
            <el-tag v-for="(n, b) in detail.priceBandDist" :key="'p' + b" type="info">{{ b }} × {{ n }}</el-tag>
          </el-space>
        </div>

        <div class="block">
          <div class="section-title">最近浏览</div>
          <el-table :data="detail.views" border size="small">
            <el-table-column prop="goodsName" label="商品" min-width="180" show-overflow-tooltip />
            <el-table-column prop="brand" label="品牌" width="90" />
            <el-table-column label="价格（元）" width="100" align="right">
              <template #default="{ row }">{{ row.priceFen == null ? '--' : yuan(row.priceFen) }}</template>
            </el-table-column>
            <el-table-column prop="time" label="时间" width="170" />
          </el-table>
        </div>

        <div class="block">
          <div class="section-title">打开记录（最近 50 次）</div>
          <el-timeline>
            <el-timeline-item v-for="(t, i) in detail.openTimes" :key="i" :timestamp="t">
              打开小程序
            </el-timeline-item>
          </el-timeline>
        </div>

        <div class="block">
          <div class="section-title with-actions">
            <span>AI 画像分析</span>
            <el-button
              size="small"
              type="primary"
              :loading="genLoading[detail.userId]"
              @click="generateInDrawer(detail.userId)"
            >{{ detail.aiAnalysis ? '重新生成' : '生成分析' }}</el-button>
          </div>
          <div class="ai-block">{{ detail.aiAnalysis || '尚未生成。生成后内容会展示在用户卡片背面。' }}</div>
          <div v-if="detail.aiGeneratedAt" class="ai-meta">模型 {{ detail.aiModel }} · {{ detail.aiGeneratedAt }}</div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { fen2yuan, fetchUserProfileDetail, fetchUserProfilePage, generateUserProfileAi } from '../api/admin';

const loading = ref(false);
const cards = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 12;
const keyword = ref('');
const flipped = reactive({});
const genLoading = reactive({});

const drawerVisible = ref(false);
const detail = ref(null);
const detailLoading = ref(false);

const yuan = (fen) => (fen == null ? '--' : `¥${fen2yuan(fen)}`);

async function load() {
  loading.value = true;
  try {
    const page = await fetchUserProfilePage({ pageNum: pageNum.value, pageSize, keyword: keyword.value.trim() });
    cards.value = page.records || [];
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

function toggleFlip(card) {
  flipped[card.userId] = !flipped[card.userId];
}

async function openDetail(card) {
  drawerVisible.value = true;
  detailLoading.value = true;
  detail.value = null;
  try {
    detail.value = await fetchUserProfileDetail(card.userId);
  } finally {
    detailLoading.value = false;
  }
}

async function generate(card) {
  genLoading[card.userId] = true;
  try {
    const info = await generateUserProfileAi(card.userId);
    card.aiSummary = (info.analysis || '').slice(0, 80);
    if (detail.value && detail.value.userId === card.userId) {
      detail.value.aiAnalysis = info.analysis;
      detail.value.aiModel = info.model;
      detail.value.aiGeneratedAt = info.generatedAt;
    }
  } finally {
    genLoading[card.userId] = false;
  }
}

async function generateInDrawer(userId) {
  genLoading[userId] = true;
  try {
    const info = await generateUserProfileAi(userId);
    detail.value.aiAnalysis = info.analysis;
    detail.value.aiModel = info.model;
    detail.value.aiGeneratedAt = info.generatedAt;
    const card = cards.value.find((c) => c.userId === userId);
    if (card) {
      card.aiSummary = (info.analysis || '').slice(0, 80);
    }
  } finally {
    genLoading[userId] = false;
  }
}

onMounted(load);
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.hint {
  color: #909399;
  font-size: 12px;
}
.card-col {
  margin-bottom: 12px;
}
.flip-card {
  perspective: 1000px;
  height: 190px;
}
.flip-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  transition: transform 0.45s;
}
.flip-card.is-flipped .flip-inner {
  transform: rotateY(180deg);
}
.face {
  position: absolute;
  inset: 0;
  backface-visibility: hidden;
  cursor: pointer;
}
.face :deep(.el-card__body) {
  height: 100%;
  box-sizing: border-box;
}
.back {
  transform: rotateY(180deg);
  cursor: default;
}
.user-head {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-name {
  flex: 1;
  min-width: 0;
}
.name {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.phone {
  color: #909399;
  font-size: 12px;
}
.flip-toggle {
  padding: 0 4px;
}
.stats {
  display: flex;
  margin-top: 14px;
}
.stat {
  flex: 1;
  text-align: center;
}
.stat .num {
  font-size: 18px;
  font-weight: 700;
}
.stat .lbl {
  color: #909399;
  font-size: 12px;
  margin-top: 2px;
}
.brand {
  margin-top: 12px;
  color: #5c6067;
  font-size: 13px;
}
.last-open {
  margin-top: 6px;
  color: #b0b4ba;
  font-size: 12px;
}
.back-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.ai-text {
  font-size: 13px;
  line-height: 1.7;
  color: #303133;
  max-height: 96px;
  overflow: auto;
}
.back-actions {
  margin-top: 10px;
}
.pager {
  margin-top: 16px;
}
.block {
  margin-bottom: 18px;
}
.section-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #303133;
}
.with-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.ai-block {
  background: #f7f8fa;
  border-radius: 6px;
  padding: 12px;
  font-size: 13px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}
.ai-meta {
  margin-top: 6px;
  color: #b0b4ba;
  font-size: 12px;
}
</style>
