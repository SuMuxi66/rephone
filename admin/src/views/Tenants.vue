<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-input v-model="keyword" placeholder="租户名称" clearable style="width: 200px" @keyup.enter="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
        <el-button type="success" @click="openCreate">新增租户</el-button>
      </el-space>
    </el-card>

    <el-table v-loading="loading" :data="tenants" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="租户名称" min-width="180" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="adminCount" label="管理员数" width="100" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openRename(row)">改名</el-button>
          <el-button
            v-if="row.id !== 0"
            link :type="row.status === 1 ? 'danger' : 'success'"
            @click="onToggle(row)"
          >{{ row.status === 1 ? '停用' : '启用' }}</el-button>
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

    <el-dialog v-model="createVisible" title="新增租户" width="440px">
      <el-form label-position="top">
        <el-form-item label="租户名称（1-64 字符）" required>
          <el-input v-model="createForm.name" maxlength="64" />
        </el-form-item>
        <el-divider content-position="left">附带租户管理员（可选）</el-divider>
        <el-form-item label="管理员用户名（4-32 位字母/数字/下划线）">
          <el-input v-model="createForm.adminUsername" placeholder="留空则不创建账号" />
        </el-form-item>
        <el-form-item label="管理员密码（8-64 位）">
          <el-input v-model="createForm.adminPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="管理员昵称">
          <el-input v-model="createForm.adminNickname" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="renameVisible" title="租户改名" width="400px">
      <el-input v-model="renameName" maxlength="64" />
      <template #footer>
        <el-button @click="renameVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onRename">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createTenant, fetchTenants, updateTenant } from '../api/admin';

const keyword = ref('');
const tenants = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const submitting = ref(false);
const createVisible = ref(false);
const createForm = reactive({ name: '', adminUsername: '', adminPassword: '', adminNickname: '' });
const renameVisible = ref(false);
const renameTarget = ref(null);
const renameName = ref('');

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (keyword.value.trim()) params.keyword = keyword.value.trim();
    const page = await fetchTenants(params);
    tenants.value = page.records || [];
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

function openCreate() {
  createForm.name = '';
  createForm.adminUsername = '';
  createForm.adminPassword = '';
  createForm.adminNickname = '';
  createVisible.value = true;
}

async function onCreate() {
  if (!createForm.name.trim()) {
    ElMessage.warning('请填写租户名称');
    return;
  }
  if (createForm.adminUsername.trim() && (createForm.adminPassword || '').length < 8) {
    ElMessage.warning('附带管理员时密码至少 8 位');
    return;
  }
  submitting.value = true;
  try {
    await createTenant({
      name: createForm.name.trim(),
      adminUsername: createForm.adminUsername.trim() || undefined,
      adminPassword: createForm.adminPassword || undefined,
      adminNickname: createForm.adminNickname.trim() || undefined,
    });
    ElMessage.success('租户已创建');
    createVisible.value = false;
    reload();
  } finally {
    submitting.value = false;
  }
}

function openRename(row) {
  renameTarget.value = row;
  renameName.value = row.name;
  renameVisible.value = true;
}

async function onRename() {
  if (!renameName.value.trim()) {
    ElMessage.warning('名称不能为空');
    return;
  }
  submitting.value = true;
  try {
    await updateTenant(renameTarget.value.id, { name: renameName.value.trim() });
    ElMessage.success('已保存');
    renameVisible.value = false;
    load();
  } finally {
    submitting.value = false;
  }
}

async function onToggle(row) {
  const action = row.status === 1 ? '停用' : '启用';
  await ElMessageBox.confirm(
    action === '停用'
      ? `停用后「${row.name}」的租户管理员将无法登录，确认停用？`
      : `确认启用「${row.name}」？`,
    `租户${action}`,
  );
  await updateTenant(row.id, { status: row.status === 1 ? 0 : 1 });
  ElMessage.success(`已${action}`);
  load();
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
</style>
