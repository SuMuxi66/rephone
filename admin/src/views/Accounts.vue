<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-input v-model="keyword" placeholder="昵称/用户名/手机号" clearable style="width: 220px" @keyup.enter="reload" />
        <el-select v-model="roleFilter" style="width: 140px" @change="reload">
          <el-option label="全部角色" value="" />
          <el-option label="普通用户" value="USER" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-button type="primary" @click="reload">查询</el-button>
        <el-button type="success" @click="createVisible = true">新增管理员</el-button>
      </el-space>
    </el-card>

    <el-table v-loading="loading" :data="users" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nickname" label="昵称" min-width="140" />
      <el-table-column prop="username" label="用户名" width="140">
        <template #default="{ row }">{{ row.username || '--' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130">
        <template #default="{ row }">{{ row.phone || '--' }}</template>
      </el-table-column>
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
            {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="openidMasked" label="openid" width="150" />
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.role !== 'ADMIN'"
            link :type="row.status === 1 ? 'danger' : 'success'"
            @click="onToggleStatus(row)"
          >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          <el-button v-if="row.role === 'ADMIN'" link type="warning" @click="openReset(row)">重置密码</el-button>
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

    <el-dialog v-model="createVisible" title="新增管理员" width="420px">
      <el-form label-position="top">
        <el-form-item label="用户名（4-32 位字母/数字/下划线）" required>
          <el-input v-model="createForm.username" />
        </el-form-item>
        <el-form-item label="密码（8-64 位）" required>
          <el-input v-model="createForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="createForm.nickname" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resetVisible" title="重置管理员密码" width="420px">
      <div class="tip">将为「{{ resetTarget?.nickname }}」设置新密码，重置后其旧密码立即失效。</div>
      <el-input v-model="resetPassword" type="password" show-password placeholder="8-64 位新密码" style="margin-top: 12px" />
      <template #footer>
        <el-button @click="resetVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onReset">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createAdminAccount,
  fetchUsers,
  resetAdminPassword,
  setUserStatus,
} from '../api/admin';

const keyword = ref('');
const roleFilter = ref('');
const users = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const submitting = ref(false);
const createVisible = ref(false);
const createForm = reactive({ username: '', password: '', nickname: '' });
const resetVisible = ref(false);
const resetTarget = ref(null);
const resetPassword = ref('');

async function load() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize };
    if (keyword.value.trim()) params.keyword = keyword.value.trim();
    if (roleFilter.value) params.role = roleFilter.value;
    const page = await fetchUsers(params);
    users.value = page.records || [];
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

async function onToggleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用';
  await ElMessageBox.confirm(`确认${action}用户「${row.nickname}」？`, '账号状态');
  await setUserStatus(row.id, row.status === 1 ? 0 : 1);
  ElMessage.success(`已${action}`);
  load();
}

async function onCreate() {
  if (!createForm.username.trim() || !createForm.password || !createForm.nickname.trim()) {
    ElMessage.warning('请完整填写用户名、密码与昵称');
    return;
  }
  submitting.value = true;
  try {
    await createAdminAccount({ ...createForm, username: createForm.username.trim() });
    ElMessage.success('管理员已创建');
    createVisible.value = false;
    createForm.username = '';
    createForm.password = '';
    createForm.nickname = '';
    reload();
  } finally {
    submitting.value = false;
  }
}

function openReset(row) {
  resetTarget.value = row;
  resetPassword.value = '';
  resetVisible.value = true;
}

async function onReset() {
  if (!resetPassword.value || resetPassword.value.length < 8) {
    ElMessage.warning('新密码至少 8 位');
    return;
  }
  submitting.value = true;
  try {
    await resetAdminPassword(resetTarget.value.id, resetPassword.value);
    ElMessage.success('密码已重置');
    resetVisible.value = false;
  } finally {
    submitting.value = false;
  }
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
.tip {
  color: #909399;
  font-size: 13px;
}
</style>
