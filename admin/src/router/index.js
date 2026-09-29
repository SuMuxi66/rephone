import { createRouter, createWebHistory } from 'vue-router';

const TOKEN_KEY = 'rephone.admin.token';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
    {
      path: '/',
      component: () => import('../layout/AdminLayout.vue'),
      redirect: '/recycle',
      children: [
        {
          path: 'recycle',
          name: 'recycle',
          meta: { requiresAdmin: true },
          component: () => import('../views/RecycleOrders.vue'),
        },
        {
          path: 'repair',
          name: 'repair',
          meta: { requiresAdmin: true },
          component: () => import('../views/RepairOrders.vue'),
        },
        {
          path: 'models',
          name: 'models',
          meta: { requiresAdmin: true },
          component: () => import('../views/Models.vue'),
        },
        {
          path: 'accounts',
          name: 'accounts',
          meta: { requiresAdmin: true },
          component: () => import('../views/Accounts.vue'),
        },
        {
          path: 'addresses',
          name: 'addresses',
          meta: { requiresAdmin: true },
          component: () => import('../views/Addresses.vue'),
        },
      ],
    },
  ],
});

// 路由守卫：无 token 一律回登录页；管理页 meta.requiresAdmin
router.beforeEach((to) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (to.path !== '/login' && !token) {
    return { path: '/login' };
  }
  if (to.path === '/login' && token) {
    return { path: '/recycle' };
  }
  return true;
});

export default router;
