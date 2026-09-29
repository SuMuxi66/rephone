<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <div class="login-title">RePhone 管理后台</div>
      <div class="login-sub">请使用管理员账号登录</div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="管理员用户名" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="onSubmit">登 录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { saveSession, clearSession } from '../api/http';
import { fetchMe, login } from '../api/admin';

const router = useRouter();
const formRef = ref();
const loading = ref(false);
const form = reactive({ username: '', password: '' });
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

async function onSubmit() {
  await formRef.value.validate().catch(() => Promise.reject());
  loading.value = true;
  try {
    const data = await login(form.username.trim(), form.password);
    // 先落登录态，再核验角色：管理账号自动切换到管理员工作台
    saveSession(data.token, { userId: data.userId, nickname: data.nickname, role: data.role });
    if (data.role !== 'ADMIN') {
      clearSession();
      ElMessage.error('该账号无管理员权限');
      return;
    }
    const me = await fetchMe().catch(() => null);
    if (me && me.role !== 'ADMIN') {
      clearSession();
      ElMessage.error('该账号无管理员权限');
      return;
    }
    ElMessage.success(`欢迎，${data.nickname || '管理员'}`);
    // 自动进入管理员权限页面
    router.push('/recycle');
  } catch (e) {
    // 错误提示已由拦截器统一处理
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 380px;
  padding: 12px 8px;
}
.login-title {
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  color: #303133;
}
.login-sub {
  margin: 8px 0 20px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}
.login-btn {
  width: 100%;
}
</style>
