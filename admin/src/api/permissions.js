import { getProfile } from './http';

/** 权限点清单（与后端 AdminPermissions 一致，供角色矩阵与菜单显隐使用）。 */
export const PERMISSIONS = [
  { code: 'recycle:manage', label: '回收单管理' },
  { code: 'repair:manage', label: '维修单管理' },
  { code: 'sale:manage', label: '售出与售后' },
  { code: 'goods:manage', label: '商品与机型' },
  { code: 'address:manage', label: '地址管理' },
  { code: 'account:manage', label: '账号管理' },
  { code: 'tenant:manage', label: '租户管理' },
  { code: 'role:manage', label: '角色管理' },
  { code: 'finance:read', label: '财务对账' },
  { code: 'dashboard:read', label: '数据看板' },
];

/** 角色编码 → 展示名（含内置与可能的普通用户角色）。 */
export const ROLE_LABELS = {
  ADMIN: '平台超管',
  TENANT_ADMIN: '租户管理员',
  OPERATOR: '运营',
  FINANCE: '财务',
  VIEWER: '只读',
  USER: '用户',
};

export const roleLabel = (code) => ROLE_LABELS[code] || code || '--';

/** 当前登录者是否持有某权限点（支持 * 全权）。 */
export function hasPermission(code) {
  const perms = getProfile().permissions || [];
  return perms.includes('*') || perms.includes(code);
}
