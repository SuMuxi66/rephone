<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">RePhone 管理后台</div>
      <el-menu :default-active="route.path" router>
        <el-menu-item v-for="item in visibleMenu" :key="item.index" :index="item.index">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
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
            <el-tag size="small" :type="profile.tenantId > 0 ? 'warning' : 'danger'" class="role-tag">
              {{ roleLabel(profile.roleCode) }}
            </el-tag>
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
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  Box, Tools, UserFilled, Iphone, Location, Sell, ChatDotRound, GoodsFilled,
  DataBoard, OfficeBuilding, Key, Money,
} from '@element-plus/icons-vue';
import { getProfile, clearSession } from '../api/http';
import { hasPermission, roleLabel } from '../api/permissions';

const route = useRoute();
const router = useRouter();
const profile = getProfile();

const MENU = [
  { index: '/dashboard', label: '数据看板', icon: DataBoard, permission: 'dashboard:read' },
  { index: '/recycle', label: '回收单管理', icon: Box, permission: 'recycle:manage' },
  { index: '/repair', label: '维修单管理', icon: Tools, permission: 'repair:manage' },
  { index: '/sale', label: '售出管理', icon: Sell, permission: 'sale:manage' },
  { index: '/after-sales', label: '售后管理', icon: ChatDotRound, permission: 'sale:manage' },
  { index: '/goods', label: '商品管理', icon: GoodsFilled, permission: 'goods:manage' },
  { index: '/models', label: '机型管理', icon: Iphone, permission: 'goods:manage' },
  { index: '/tenants', label: '租户管理', icon: OfficeBuilding, permission: 'tenant:manage' },
  { index: '/roles', label: '角色管理', icon: Key, permission: 'role:manage' },
  { index: '/finance', label: '财务对账', icon: Money, permission: 'finance:read' },
  { index: '/accounts', label: '账号管理', icon: UserFilled, permission: 'account:manage' },
  { index: '/addresses', label: '地址管理', icon: Location, permission: 'address:manage' },
];

const visibleMenu = computed(() => MENU.filter((item) => hasPermission(item.permission)));

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
.role-tag {
  margin-left: 2px;
}
</style>
