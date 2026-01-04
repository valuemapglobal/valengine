-- ============================================================
-- RiskSmart 初始化数据脚本
-- 包含: 系统初始用户、角色、菜单等基础数据
-- ============================================================

-- 设置字符集确保中文正确处理
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ===================== 系统库初始化数据 =====================
USE `risk_smart_system`;

-- 初始化部门
INSERT INTO sys_dept VALUES(100, 0, '0', 'RiskSmart', 0, NULL, NULL, NULL, '0', '0', 'admin', sysdate(), '', NULL);
INSERT INTO sys_dept VALUES(101, 100, '0,100', '技术部', 1, NULL, NULL, NULL, '0', '0', 'admin', sysdate(), '', NULL);
INSERT INTO sys_dept VALUES(102, 100, '0,100', '运营部', 2, NULL, NULL, NULL, '0', '0', 'admin', sysdate(), '', NULL);

-- 初始化岗位
INSERT INTO sys_post VALUES(1, 'ceo', '董事长', 1, '0', 'admin', sysdate(), '', NULL, NULL);
INSERT INTO sys_post VALUES(2, 'manager', '项目经理', 2, '0', 'admin', sysdate(), '', NULL, NULL);
INSERT INTO sys_post VALUES(3, 'developer', '开发工程师', 3, '0', 'admin', sysdate(), '', NULL, NULL);

-- 初始化用户 (密码: admin123)
INSERT INTO sys_user VALUES(1, 101, 'admin', '超级管理员', '00', 'admin@risksmart.com', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', NULL, 'admin', sysdate(), '', NULL, '超级管理员');

-- 初始化角色
INSERT INTO sys_role VALUES(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', sysdate(), '', NULL, '超级管理员');
INSERT INTO sys_role VALUES(2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', sysdate(), '', NULL, '普通角色');

-- 初始化用户角色关联
INSERT INTO sys_user_role VALUES(1, 1);

-- 初始化菜单 (基础菜单)
INSERT INTO sys_menu VALUES(1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', sysdate(), '', NULL, '系统管理目录');
INSERT INTO sys_menu VALUES(100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', sysdate(), '', NULL, '用户管理菜单');
INSERT INTO sys_menu VALUES(101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', sysdate(), '', NULL, '角色管理菜单');
INSERT INTO sys_menu VALUES(102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', sysdate(), '', NULL, '菜单管理菜单');
INSERT INTO sys_menu VALUES(103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', sysdate(), '', NULL, '部门管理菜单');
INSERT INTO sys_menu VALUES(104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', sysdate(), '', NULL, '岗位管理菜单');
INSERT INTO sys_menu VALUES(105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', sysdate(), '', NULL, '字典管理菜单');
INSERT INTO sys_menu VALUES(106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', sysdate(), '', NULL, '参数设置菜单');

-- 初始化角色菜单关联 (admin拥有所有菜单)
INSERT INTO sys_role_menu VALUES(1, 1);
INSERT INTO sys_role_menu VALUES(1, 100);
INSERT INTO sys_role_menu VALUES(1, 101);
INSERT INTO sys_role_menu VALUES(1, 102);
INSERT INTO sys_role_menu VALUES(1, 103);
INSERT INTO sys_role_menu VALUES(1, 104);
INSERT INTO sys_role_menu VALUES(1, 105);
INSERT INTO sys_role_menu VALUES(1, 106);

-- 初始化字典类型
INSERT INTO sys_dict_type VALUES(1, '用户性别', 'sys_user_sex', '0', 'admin', sysdate(), '', NULL, '用户性别列表');
INSERT INTO sys_dict_type VALUES(2, '菜单状态', 'sys_show_hide', '0', 'admin', sysdate(), '', NULL, '菜单状态列表');
INSERT INTO sys_dict_type VALUES(3, '系统开关', 'sys_normal_disable', '0', 'admin', sysdate(), '', NULL, '系统开关列表');
INSERT INTO sys_dict_type VALUES(4, '任务状态', 'sys_job_status', '0', 'admin', sysdate(), '', NULL, '任务状态列表');
INSERT INTO sys_dict_type VALUES(5, '系统是否', 'sys_yes_no', '0', 'admin', sysdate(), '', NULL, '系统是否列表');
INSERT INTO sys_dict_type VALUES(6, '通知类型', 'sys_notice_type', '0', 'admin', sysdate(), '', NULL, '通知类型列表');
INSERT INTO sys_dict_type VALUES(7, '通知状态', 'sys_notice_status', '0', 'admin', sysdate(), '', NULL, '通知状态列表');
INSERT INTO sys_dict_type VALUES(8, '操作类型', 'sys_oper_type', '0', 'admin', sysdate(), '', NULL, '操作类型列表');
INSERT INTO sys_dict_type VALUES(9, '系统状态', 'sys_common_status', '0', 'admin', sysdate(), '', NULL, '登录状态列表');

-- 初始化字典数据
INSERT INTO sys_dict_data VALUES(1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', sysdate(), '', NULL, '性别男');
INSERT INTO sys_dict_data VALUES(2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '性别女');
INSERT INTO sys_dict_data VALUES(3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '性别未知');
INSERT INTO sys_dict_data VALUES(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', sysdate(), '', NULL, '显示菜单');
INSERT INTO sys_dict_data VALUES(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', sysdate(), '', NULL, '隐藏菜单');
INSERT INTO sys_dict_data VALUES(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', sysdate(), '', NULL, '正常状态');
INSERT INTO sys_dict_data VALUES(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', sysdate(), '', NULL, '停用状态');
INSERT INTO sys_dict_data VALUES(8, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', sysdate(), '', NULL, '系统默认是');
INSERT INTO sys_dict_data VALUES(9, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', sysdate(), '', NULL, '系统默认否');
INSERT INTO sys_dict_data VALUES(10, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', NULL, '正常状态');
INSERT INTO sys_dict_data VALUES(11, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', sysdate(), '', NULL, '停用状态');

-- 初始化系统参数
INSERT INTO sys_config VALUES(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', sysdate(), '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO sys_config VALUES(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', sysdate(), '', NULL, '初始化密码 123456');
INSERT INTO sys_config VALUES(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', sysdate(), '', NULL, '深色主题theme-dark，浅色主题theme-light');

-- ============================================================
-- 初始化完成
-- ============================================================
