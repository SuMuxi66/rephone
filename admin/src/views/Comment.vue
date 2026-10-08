<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-select v-model="orderType" placeholder="订单类型" clearable style="width: 140px" @change="reload">
          <el-option label="回收单" :value="10" />
          <el-option label="出售单" :value="20" />
          <el-option label="维修单" :value="30" />
        </el-select>
        <el-select v-model="score" placeholder="星级" clearable style="width: 120px" @change="reload">
          <el-option v-for="s in 5" :key="s" :label="`${s} 星`" :value="s" />
        </el-select>
        <el-input v-model="orderNo" placeholder="订单号（模糊）" clearable style="width: 220px" @keyup.enter="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
      </el-space>
      <div class="note">用户对已完成的维修/回收/出售订单各可评价一次；图片仅存 COS key，不在此回显。</div>
    </el-card>

    <el-table v-loading="loading" :data="comments" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="订单类型" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="typeMeta(row.orderType).type">{{ typeMeta(row.orderType).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单号" min-width="160" />
      <el-table-column prop="nickname" label="用户昵称" width="130" />
      <el-table-column label="评分" width="150">
        <template #default="{ row }">
          <el-rate :model-value="row.score" disabled size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">{{ (row.images || []).length ? row.images.length + ' 张' : '--' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="评价时间" width="170" />
    </el-table>

    <el-pagination
      class="pager"
      layout="prev, pager, next, total"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      @current-change="onPage"
    />

    <el-empty v-if="!loading && total === 0" description="暂无评价：用户在订单完成后于小程序端提交" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { COMMENT_ORDER_TYPE, fetchComments } from '../api/admin';

const orderType = ref(null);
const score = ref(null);
const orderNo = ref('');
const comments = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);

const typeMeta = (t) => COMMENT_ORDER_TYPE[t] || { label: t, type: 'info' };

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (orderType.value) params.orderType = orderType.value;
    if (score.value) params.score = score.value;
    if (orderNo.value.trim()) params.orderNo = orderNo.value.trim();
    const page = await fetchComments(params);
    comments.value = page.records || [];
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

onMounted(load);
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.note {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}
.pager {
  margin-top: 16px;
}
</style>
