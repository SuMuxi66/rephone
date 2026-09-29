<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">RePhone 管理后台</div>
      <el-menu :default-active="route.path" router>
        <el-menu-item index="/recycle">
          <el-icon><Box /></el-icon>
          <span>回收单管理</span>
        </el-menu-item>
        <el-menu-item index="/repair">
          <el-icon><Tools /></el-icon>
          <span>维修单管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div />
        <el-dropdown @command="onCommand">
          <span class="user">
            <el-icon><UserFilled /></el-icon>
            {{ profile.nickname || '管理员' }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { Box, Tools, UserFilled } from '@element-plus/icons-vue';
import { getProfile, clearSession } from '../api/http';

const route = useRoute();
const router = useRouter();
const profile = getProfile();

function onCommand(cmd) {
  if (cmd === 'logout') {
    clearSession();
    router.push('/login');
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
}
.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.user {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #303133;
}
</style>
