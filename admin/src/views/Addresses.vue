<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-input v-model="keyword" placeholder="收件人/手机号/地区" clearable style="width: 220px" @keyup.enter="reload" />
        <el-input v-model="userIdFilter" placeholder="按用户 ID 精确筛选" clearable style="width: 180px" @keyup.enter="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
      </el-space>
      <div class="note">地址簿数据由小程序端用户维护；删除用于用户要求删除个人信息的合规场景。</div>
    </el-card>

    <el-table v-loading="loading" :data="addresses" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column prop="userName" label="用户昵称" min-width="120" />
      <el-table-column prop="name" label="收件人" width="110" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="地址" min-width="220">
        <template #default="{ row }">{{ row.region }} {{ row.detail }}</template>
      </el-table-column>
      <el-table-column label="默认" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.isDefault === 1" type="success" size="small">默认</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
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

    <el-empty v-if="!loading && total === 0" description="暂无地址数据：地址簿在小程序端由用户维护" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { deleteAddress, fetchAddresses } from '../api/admin';

const keyword = ref('');
const userIdFilter = ref('');
const addresses = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (keyword.value.trim()) params.keyword = keyword.value.trim();
    if (userIdFilter.value.trim()) params.userId = userIdFilter.value.trim();
    const page = await fetchAddresses(params);
    addresses.value = page.records || [];
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

async function onDelete(row) {
  await ElMessageBox.confirm(
    `确认删除「${row.name}」的地址（${row.region} ${row.detail}）？删除不可恢复。`,
    '隐私删除',
  );
  await deleteAddress(row.id);
  ElMessage.success('已删除');
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
