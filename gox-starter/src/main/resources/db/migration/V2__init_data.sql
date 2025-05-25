-- 初始化管理员用户
INSERT INTO sys_user (username, enabled, password, nickname)
VALUES ('admin', TRUE, '$2a$10$aewa/XxpIRpidDfHxb9OsOfXpJRBYKDsHrQr8aMTtMO7r5ETWP3q.', '管理员');

-- 初始化测试用户
INSERT INTO sys_user (username, enabled, password, nickname)
VALUES ('test', TRUE, '$2a$10$aewa/XxpIRpidDfHxb9OsOfXpJRBYKDsHrQr8aMTtMO7r5ETWP3q.', 'Test User');

-- 初始化角色数据
INSERT INTO sys_role (name, code, description, enabled)
VALUES ('管理员', 'ADMIN', '系统管理员角色，拥有最高权限', TRUE),
       ('用户', 'USER', '普通用户角色，拥有基本权限', TRUE),
       ('测试角色', 'TEST', '测试用户角色，用于权限拦截器测试', TRUE);

-- 初始化权限数据
INSERT INTO sys_permission (name, code, description)
VALUES ('所有权限', 'ALL', '拥有系统所有权限'),
       ('查看用户', 'USER_VIEW', '查看用户信息权限'),
       ('编辑用户', 'USER_EDIT', '编辑用户信息权限'),
       ('查看角色', 'ROLE_VIEW', '查看角色信息权限'),
       ('编辑角色', 'ROLE_EDIT', '编辑角色信息权限');

-- 初始化用户角色关联数据
-- 管理员用户关联管理员角色
INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1);

-- 测试用户关联测试角色
INSERT INTO sys_user_role (user_id, role_id)
VALUES (2, 3);

-- 初始化角色权限关联数据
-- 管理员角色拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (1, 1);

-- 用户角色拥有基本查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (2, 2),
       (2, 4);

-- 测试角色拥有部分权限（用于测试权限拦截器）
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (3, 2), -- 查看用户权限
       (3, 4); -- 查看角色权限
