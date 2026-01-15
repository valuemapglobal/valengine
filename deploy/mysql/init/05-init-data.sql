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
INSERT INTO sys_user VALUES(1, 101, 'admin', '超级管理员', '00', 'admin@risksmart.com', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', NULL, NULL, 'admin', sysdate(), '', NULL, '超级管理员');

-- 初始化角色
INSERT INTO sys_role VALUES(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', sysdate(), '', NULL, '超级管理员');
INSERT INTO sys_role VALUES(2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', sysdate(), '', NULL, '普通角色');

-- 初始化用户角色关联
INSERT INTO sys_user_role VALUES(1, 1);

-- 初始化菜单 (基础菜单)
-- 一级目录
INSERT INTO sys_menu VALUES(1, '系统管理', 0, 99, 'System', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', sysdate(), '', NULL, '系统管理目录');
INSERT INTO sys_menu VALUES(2, '接口平台', 0, 1, 'InterfacePlatform', NULL, '', 1, 0, 'M', '0', '0', '', 'api', 'admin', sysdate(), '', NULL, '接口平台目录');
INSERT INTO sys_menu VALUES(3, '数据平台', 0, 2, 'DataCenter', NULL, '', 1, 0, 'M', '0', '0', '', 'database', 'admin', sysdate(), '', NULL, '数据平台目录');
INSERT INTO sys_menu VALUES(4, '决策平台', 0, 3, 'DecisionPlatform', NULL, '', 1, 0, 'M', '0', '0', '', 'decision', 'admin', sysdate(), '', NULL, '决策平台目录');
INSERT INTO sys_menu VALUES(5, '规则池', 0, 4, 'RulePool', NULL, '', 1, 0, 'M', '0', '0', '', 'rule', 'admin', sysdate(), '', NULL, '规则池目录');
INSERT INTO sys_menu VALUES(6, '平台引擎', 0, 5, 'PlatformEngine', NULL, '', 1, 0, 'M', '0', '0', '', 'engine', 'admin', sysdate(), '', NULL, '平台引擎目录');

-- 系统管理子菜单
INSERT INTO sys_menu VALUES(100, '用户管理', 1, 1, 'UserManage', 'system/userManage/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', sysdate(), '', NULL, '用户管理菜单');
INSERT INTO sys_menu VALUES(101, '角色管理', 1, 2, 'RoleManage', 'system/roleManage/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', sysdate(), '', NULL, '角色管理菜单');
INSERT INTO sys_menu VALUES(102, '菜单管理', 1, 3, 'MenuManage', 'system/menuManagement/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', sysdate(), '', NULL, '菜单管理菜单');
INSERT INTO sys_menu VALUES(103, '部门管理', 1, 4, 'Department', 'system/department/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', sysdate(), '', NULL, '部门管理菜单');
INSERT INTO sys_menu VALUES(104, '字典管理', 1, 5, 'DictionaryManagement', 'system/dictionaryManagement/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', sysdate(), '', NULL, '字典管理菜单');
INSERT INTO sys_menu VALUES(105, '操作日志', 1, 6, 'OperlogManage', 'system/operlogManage/index', '', 1, 0, 'C', '0', '0', 'system:operlog:list', 'log', 'admin', sysdate(), '', NULL, '操作日志菜单');

-- 接口平台子菜单
INSERT INTO sys_menu VALUES(200, '数据场景管理', 2, 1, 'SmartDecision', 'interfacePlatform/modules/SmartDecision', '', 1, 0, 'C', '0', '0', 'interface:scene:list', 'scene', 'admin', sysdate(), '', NULL, '数据场景管理菜单');
INSERT INTO sys_menu VALUES(201, '接口管理', 2, 2, 'InterfaceManagement', 'interfacePlatform/modules/InterfaceManagement', '', 1, 0, 'C', '0', '0', 'interface:api:list', 'api', 'admin', sysdate(), '', NULL, '接口管理菜单');
INSERT INTO sys_menu VALUES(202, '数据审计', 2, 3, 'DataDuditing', 'interfacePlatform/modules/DataDuditing', '', 1, 0, 'C', '0', '0', 'interface:audit:list', 'audit', 'admin', sysdate(), '', NULL, '数据审计菜单');
INSERT INTO sys_menu VALUES(203, '数据报表', 2, 4, 'InterfaceUser', 'interfacePlatform/modules/interfaceUser', '', 1, 0, 'C', '0', '0', 'interface:report:list', 'chart', 'admin', sysdate(), '', NULL, '数据报表菜单');

-- 数据平台子菜单
INSERT INTO sys_menu VALUES(300, '元数据', 3, 1, 'BasicVariables', 'DataCenter/modules/basicVariables', '', 1, 0, 'C', '0', '0', 'data:meta:list', 'metadata', 'admin', sysdate(), '', NULL, '元数据菜单');
INSERT INTO sys_menu VALUES(301, '特征变量', 3, 2, 'FeatureVariable', 'DataCenter/modules/featureVariable', '', 1, 0, 'C', '0', '0', 'data:feature:list', 'variable', 'admin', sysdate(), '', NULL, '特征变量菜单');
INSERT INTO sys_menu VALUES(302, '分析指标', 3, 3, 'AnalysisTarget', 'DataCenter/modules/analysisTarget', '', 1, 0, 'C', '0', '0', 'data:analysis:list', 'target', 'admin', sysdate(), '', NULL, '分析指标菜单');

-- 决策平台子菜单
INSERT INTO sys_menu VALUES(400, '产品决策', 4, 1, 'ProductDecision', 'decisionPlatform/modules/productDecision/index', '', 1, 0, 'C', '0', '0', 'decision:product:list', 'product', 'admin', sysdate(), '', NULL, '产品决策菜单');
INSERT INTO sys_menu VALUES(401, '模型决策', 4, 2, 'ModelDecision', 'decisionPlatform/modules/modelDecision/index', '', 1, 0, 'C', '0', '0', 'decision:model:list', 'model', 'admin', sysdate(), '', NULL, '模型决策菜单');

-- 规则池子菜单
INSERT INTO sys_menu VALUES(500, '策略规则池', 5, 1, 'Strategy', 'rulePool/strategy/index', '', 1, 0, 'C', '0', '0', 'rule:strategy:list', 'strategy', 'admin', sysdate(), '', NULL, '策略规则池菜单');

-- 产品决策按钮权限 (共19列)
INSERT INTO sys_menu VALUES(4001, '查看', 400, 1, '', '', '', 1, 0, 'F', '0', '0', 'strategy:view:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4002, '编辑', 400, 2, '', '', '', 1, 0, 'F', '0', '0', 'strategy:edit:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4003, '测试', 400, 3, '', '', '', 1, 0, 'F', '0', '0', 'strategy:test:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4004, '发布', 400, 4, '', '', '', 1, 0, 'F', '0', '0', 'strategy:release:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4005, '复用', 400, 5, '', '', '', 1, 0, 'F', '0', '0', 'strategy:reuse:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4006, '导出', 400, 6, '', '', '', 1, 0, 'F', '0', '0', 'strategy:export:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4007, '导入策略', 400, 7, '', '', '', 1, 0, 'F', '0', '0', 'import:strategy:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4008, '策略新增', 400, 8, '', '', '', 1, 0, 'F', '0', '0', 'Newstrategy:addition:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4009, '策略删除', 400, 9, '', '', '', 1, 0, 'F', '0', '0', 'Strategy:deletion:show', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(4010, '启用禁用', 400, 10, '', '', '', 1, 0, 'F', '0', '0', 'enable:disable:show', '#', 'admin', sysdate(), '', NULL, '');

-- 平台引擎子菜单
INSERT INTO sys_menu VALUES(600, '流程引擎', 6, 1, 'WorkflowEngine', 'platformEngine/modules/workflowEngine/index', '', 1, 0, 'C', '0', '0', 'engine:workflow:list', 'workflow', 'admin', sysdate(), '', NULL, '流程引擎菜单');
INSERT INTO sys_menu VALUES(601, '任务审批', 6, 2, 'TaskApproval', 'platformEngine/modules/taskApproval/index', '', 1, 0, 'C', '0', '0', 'engine:approval:list', 'approval', 'admin', sysdate(), '', NULL, '任务审批菜单');
INSERT INTO sys_menu VALUES(602, '任务记录', 6, 3, 'TaskRecord', 'platformEngine/modules/taskRecord/index', '', 1, 0, 'C', '0', '0', 'engine:record:list', 'record', 'admin', sysdate(), '', NULL, '任务记录菜单');
INSERT INTO sys_menu VALUES(603, '流程任务', 6, 4, 'ProcessTask', 'platformEngine/modules/processTask/index', '', 1, 0, 'C', '0', '0', 'engine:process:list', 'process', 'admin', sysdate(), '', NULL, '流程任务菜单');

-- 初始化角色菜单关联 (admin拥有所有菜单)
INSERT INTO sys_role_menu VALUES(1, 1);
INSERT INTO sys_role_menu VALUES(1, 2);
INSERT INTO sys_role_menu VALUES(1, 3);
INSERT INTO sys_role_menu VALUES(1, 4);
INSERT INTO sys_role_menu VALUES(1, 5);
INSERT INTO sys_role_menu VALUES(1, 6);
INSERT INTO sys_role_menu VALUES(1, 100);
INSERT INTO sys_role_menu VALUES(1, 101);
INSERT INTO sys_role_menu VALUES(1, 102);
INSERT INTO sys_role_menu VALUES(1, 103);
INSERT INTO sys_role_menu VALUES(1, 104);
INSERT INTO sys_role_menu VALUES(1, 105);
INSERT INTO sys_role_menu VALUES(1, 200);
INSERT INTO sys_role_menu VALUES(1, 201);
INSERT INTO sys_role_menu VALUES(1, 202);
INSERT INTO sys_role_menu VALUES(1, 203);
INSERT INTO sys_role_menu VALUES(1, 300);
INSERT INTO sys_role_menu VALUES(1, 301);
INSERT INTO sys_role_menu VALUES(1, 302);
INSERT INTO sys_role_menu VALUES(1, 400);
INSERT INTO sys_role_menu VALUES(1, 401);
INSERT INTO sys_role_menu VALUES(1, 500);
INSERT INTO sys_role_menu VALUES(1, 600);
INSERT INTO sys_role_menu VALUES(1, 601);
INSERT INTO sys_role_menu VALUES(1, 602);
INSERT INTO sys_role_menu VALUES(1, 603);
-- 产品决策按钮权限
INSERT INTO sys_role_menu VALUES(1, 4001);
INSERT INTO sys_role_menu VALUES(1, 4002);
INSERT INTO sys_role_menu VALUES(1, 4003);
INSERT INTO sys_role_menu VALUES(1, 4004);
INSERT INTO sys_role_menu VALUES(1, 4005);
INSERT INTO sys_role_menu VALUES(1, 4006);
INSERT INTO sys_role_menu VALUES(1, 4007);
INSERT INTO sys_role_menu VALUES(1, 4008);
INSERT INTO sys_role_menu VALUES(1, 4009);
INSERT INTO sys_role_menu VALUES(1, 4010);

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
