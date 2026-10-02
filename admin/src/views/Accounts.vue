<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-input v-model="keyword" placeholder="昵称/用户名/手机号" clearable style="width: 220px" @keyup.enter="reload" />
        <el-select v-model="roleFilter" style="width: 150px" @change="reload">
          <el-option label="全部角色" value="" />
          <el-option v-for="(label, code) in ROLE_LABELS" :key="code" :label="label" :value="code" />
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
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'USER' ? 'info' : 'danger'">{{ roleLabel(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="openidMasked" label="openid" width="150" />
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.role === 'USER'"
            link :type="row.status === 1 ? 'danger' : 'success'"
            @click="onToggleStatus(row)"
          >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          <el-button v-if="row.role !== 'USER'" link type="warning" @click="openReset(row)">重置密码</el-button>
          <el-button
            v-if="row.role !== 'USER' && row.id !== (profile.userId || 0)"
            link type="primary"
            @click="openChangeRole(row)"
          >改角色</el-button>
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

    <el-dialog v-model="createVisible" title="新增管理员" width="440px">
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
        <el-form-item label="角色">
          <el-select v-model="createForm.roleCode" style="width: 100%">
            <el-option v-for="r in adminRoleOptions" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="createForm.roleCode === 'TENANT_ADMIN'" label="所属租户 ID（见租户管理页）" required>
          <el-input-number v-model="createForm.tenantId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="changeRoleVisible" title="修改角色" width="400px">
      <div class="tip">将为「{{ roleTarget?.nickname }}」设置新角色（{{ roleTarget?.role }} → 新角色）。</div>
      <el-select v-model="changeRoleCode" style="width: 100%; margin-top: 12px">
        <el-option v-for="r in adminRoleOptions" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
      </el-select>
      <template #footer>
        <el-button @click="changeRoleVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onChangeRole">确认</el-button>
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
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  changeAccountRole,
  createAdminAccount,
  fetchRoles,
  fetchUsers,
  resetAdminPassword,
  setUserStatus,
} from '../api/admin';
import { getProfile } from '../api/http';
import { roleLabel, ROLE_LABELS } from '../api/permissions';

const profile = getProfile();

const keyword = ref('');
const roleFilter = ref('');
const users = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 20;
const loading = ref(false);
const submitting = ref(false);
const createVisible = ref(false);
const createForm = reactive({ username: '', password: '', nickname: '', roleCode: 'ADMIN', tenantId: 1 });
const resetVisible = ref(false);
const resetTarget = ref(null);
const resetPassword = ref('');
const roleOptions = ref([]);
const changeRoleVisible = ref(false);
const roleTarget = ref(null);
const changeRoleCode = ref('');

const adminRoleOptions = computed(() => roleOptions.value.filter((r) => r.status === 1));

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
  if (createForm.roleCode === 'TENANT_ADMIN' && !(createForm.tenantId >= 1)) {
    ElMessage.warning('租户管理员必须填写所属租户 ID');
    return;
  }
  submitting.value = true;
  try {
    await createAdminAccount({
      username: createForm.username.trim(),
      password: createForm.password,
      nickname: createForm.nickname.trim(),
      roleCode: createForm.roleCode,
      tenantId: createForm.roleCode === 'TENANT_ADMIN' ? createForm.tenantId : undefined,
    });
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

function openChangeRole(row) {
  roleTarget.value = row;
  changeRoleCode.value = row.role;
  changeRoleVisible.value = true;
}

async function onChangeRole() {
  if (!changeRoleCode.value) {
    ElMessage.warning('请选择新角色');
    return;
  }
  submitting.value = true;
  try {
    await changeAccountRole(roleTarget.value.id, changeRoleCode.value);
    ElMessage.success('角色已更新');
    changeRoleVisible.value = false;
    reload();
  } finally {
    submitting.value = false;
  }
}

async function loadRoles() {
  try {
    roleOptions.value = (await fetchRoles()) || [];
  } catch (e) {
    roleOptions.value = [];
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

onMounted(() => {
  load();
  loadRoles();
});
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
