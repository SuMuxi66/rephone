<template>
  <div>
    <el-card shadow="never" class="toolbar">
      <el-space wrap>
        <el-button type="success" @click="openCreate">新增角色</el-button>
        <span class="tip">权限点决定菜单可见性与接口访问；「平台超管」固定全权不可改。</span>
      </el-space>
    </el-card>

    <el-table v-loading="loading" :data="roles" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="roleCode" label="编码" width="160" />
      <el-table-column prop="roleName" label="名称" width="140" />
      <el-table-column label="权限点" min-width="320">
        <template #default="{ row }">
          <template v-if="row.permissions.includes('*')">
            <el-tag type="danger">全部权限</el-tag>
          </template>
          <template v-else>
            <el-tag
              v-for="p in row.permissions"
              :key="p"
              size="small"
              class="perm-tag"
            >{{ permissionLabel(p) }}</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="内置" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.builtIn === 1" size="small" type="warning">内置</el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.roleCode !== 'ADMIN'"
            link type="primary"
            @click="openEdit(row)"
          >编辑</el-button>
          <el-button
            v-if="row.builtIn !== 1"
            link type="danger"
            @click="onDelete(row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editTarget ? '编辑角色' : '新增角色'" width="520px">
      <el-form label-position="top">
        <el-form-item label="角色编码（大写字母开头，2-16 位大写字母/数字/下划线）" required>
          <el-input v-model="form.roleCode" :disabled="!!editTarget" maxlength="16" />
        </el-form-item>
        <el-form-item label="角色名称" required>
          <el-input v-model="form.roleName" maxlength="32" />
        </el-form-item>
        <el-form-item label="权限点">
          <el-checkbox-group v-model="form.permissions">
            <el-checkbox v-for="p in PERMISSIONS" :key="p.code" :value="p.code">{{ p.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" maxlength="255" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createRole, deleteRole, fetchRoles, updateRole } from '../api/admin';
import { PERMISSIONS } from '../api/permissions';

const roles = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const editTarget = ref(null);
const form = reactive({ roleCode: '', roleName: '', permissions: [], remark: '' });

const permissionLabel = (code) => (PERMISSIONS.find((p) => p.code === code) || {}).label || code;

async function load() {
  loading.value = true;
  try {
    roles.value = (await fetchRoles()) || [];
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editTarget.value = null;
  form.roleCode = '';
  form.roleName = '';
  form.permissions = ['dashboard:read'];
  form.remark = '';
  dialogVisible.value = true;
}

function openEdit(row) {
  editTarget.value = row;
  form.roleCode = row.roleCode;
  form.roleName = row.roleName;
  form.permissions = [...row.permissions];
  form.remark = row.remark || '';
  dialogVisible.value = true;
}

async function onSave() {
  if (!form.roleCode.trim() || !form.roleName.trim()) {
    ElMessage.warning('请填写角色编码与名称');
    return;
  }
  if (!form.permissions.length) {
    ElMessage.warning('至少选择一个权限点');
    return;
  }
  submitting.value = true;
  try {
    if (editTarget.value) {
      await updateRole(editTarget.value.id, {
        roleName: form.roleName.trim(),
        permissions: form.permissions,
        remark: form.remark,
      });
      ElMessage.success('角色已更新');
    } else {
      await createRole({
        roleCode: form.roleCode.trim(),
        roleName: form.roleName.trim(),
        permissions: form.permissions,
        remark: form.remark,
      });
      ElMessage.success('角色已创建');
    }
    dialogVisible.value = false;
    load();
  } finally {
    submitting.value = false;
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确认删除角色「${row.roleName}」？删除后不可恢复。`, '删除角色');
  await deleteRole(row.id);
  ElMessage.success('已删除');
  load();
}

onMounted(load);
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.tip {
  color: #909399;
  font-size: 13px;
}
.perm-tag {
  margin: 2px;
}
</style>
