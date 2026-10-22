-- 老库补丁：为内置角色追加「点击统计」「用户画像」权限点。
-- 新库由 schema-role.sql 种子直接带出，无需执行本补丁；
-- 已上线的库角色行为 INSERT IGNORE 幂等种子，不会自动获得新权限点，需人工执行：
--   mysql -u<user> -p rephone < patch-stats-permissions.sql
UPDATE `role`
SET `permissions_json` = REPLACE(`permissions_json`, '"]', '","stats:click","profile:read"]')
WHERE `role_code` IN ('TENANT_ADMIN', 'OPERATOR', 'VIEWER')
  AND `permissions_json` NOT LIKE '%stats:click%';
