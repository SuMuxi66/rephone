import { createRouter, createWebHistory } from 'vue-router';
import { hasPermission } from '../api/permissions';
import { clearSession, getProfile, TOKEN_KEY } from '../api/http';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
    {
      path: '/',
      component: () => import('../layout/AdminLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          // 看板是权限兜底页：所有管理角色都可见，守卫对它直接放行避免重定向环
          path: 'dashboard',
          name: 'dashboard',
          meta: { requiresAdmin: true },
          component: () => import('../views/Dashboard.vue'),
        },
        {
          path: 'recycle',
          name: 'recycle',
          meta: { requiresAdmin: true, permission: 'recycle:manage' },
          component: () => import('../views/RecycleOrders.vue'),
        },
        {
          path: 'repair',
          name: 'repair',
          meta: { requiresAdmin: true, permission: 'repair:manage' },
          component: () => import('../views/RepairOrders.vue'),
        },
        {
          path: 'sale',
          name: 'sale',
          meta: { requiresAdmin: true, permission: 'sale:manage' },
          component: () => import('../views/SaleOrders.vue'),
        },
        {
          path: 'after-sales',
          name: 'after-sales',
          meta: { requiresAdmin: true, permission: 'sale:manage' },
          component: () => import('../views/AfterSales.vue'),
        },
        {
          path: 'goods',
          name: 'goods',
          meta: { requiresAdmin: true, permission: 'goods:manage' },
          component: () => import('../views/Goods.vue'),
        },
        {
          path: 'models',
          name: 'models',
          meta: { requiresAdmin: true, permission: 'goods:manage' },
          component: () => import('../views/Models.vue'),
        },
        {
          path: 'tenants',
          name: 'tenants',
          meta: { requiresAdmin: true, permission: 'tenant:manage' },
          component: () => import('../views/Tenants.vue'),
        },
        {
          path: 'roles',
          name: 'roles',
          meta: { requiresAdmin: true, permission: 'role:manage' },
          component: () => import('../views/Roles.vue'),
        },
        {
          path: 'finance',
          name: 'finance',
          meta: { requiresAdmin: true, permission: 'finance:read' },
          component: () => import('../views/Finance.vue'),
        },
        {
          path: 'accounts',
          name: 'accounts',
          meta: { requiresAdmin: true, permission: 'account:manage' },
          component: () => import('../views/Accounts.vue'),
        },
        {
          path: 'addresses',
          name: 'addresses',
          meta: { requiresAdmin: true, permission: 'address:manage' },
          component: () => import('../views/Addresses.vue'),
        },
      ],
    },
  ],
});

// 路由守卫：无 token 回登录页；旧格式登录态（升级前保存、无 permissions）强制重新登录；
// 无对应权限点的页面回落看板（看板自身放行，防止重定向环）
router.beforeEach((to) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (to.path !== '/login' && !token) {
    return { path: '/login' };
  }
  if (to.path === '/login' && token) {
    return { path: '/dashboard' };
  }
  const profile = getProfile();
  if (token && !Array.isArray(profile.permissions)) {
    clearSession();
    return { path: '/login' };
  }
  if (to.meta && to.meta.permission && to.path !== '/dashboard' && !hasPermission(to.meta.permission)) {
    return { path: '/dashboard' };
  }
  return true;
});

export default router;
