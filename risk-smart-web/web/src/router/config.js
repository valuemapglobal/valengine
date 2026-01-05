export const module = ['Diligence', 'Map', 'Cashflow', 'Risk', 'Aivisit']
export const configRoutes = [
  {
    path: '/',
    name: 'index',
    component: () => import(/* webpackChunkName:"Index"*/ '@/layout/Index.vue'),
    children: [
      {
        path: 'InterfacePlatform',
        name: 'InterfacePlatform',
        meta: {
          title: '接口平台',
        },
        component: () =>
          import(
            /* webpackChunkName:"System"*/
            '@/views/index.vue'
          ),
        children: [
          {
            path: 'SmartDecision',
            name: 'SmartDecision',
            meta: {
              title: '数据场景管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"SmartDecision"*/
                '@/views/interfacePlatform/modules/SmartDecision.vue'
              ),
          },
          {
            path: 'InterfaceManagement',
            name: 'InterfaceManagement',
            meta: {
              title: '接口管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"InterfaceManagement"*/
                '@/views/interfacePlatform/modules/InterfaceManagement.vue'
              ),
          },
          {
            path: 'DataDuditing',
            name: 'DataDuditing',
            meta: {
              title: '数据审计',
            },
            component: () =>
              import(
                /* webpackChunkName:"DataDuditing"*/
                '@/views/interfacePlatform/modules/DataDuditing.vue'
              ),
          },
          {
            path: 'InterfaceUser',
            name: 'InterfaceUser',
            meta: {
              title: '数据报表',
            },
            component: () =>
              import(
                /* webpackChunkName:"TrafficStatistics"*/
                '@/views/interfacePlatform/modules/interfaceUser.vue'
              ),
          },
        ],
      },
      {
        path: 'DataCenter',
        name: 'DataCenter',
        meta: {
          title: '数据平台',
        },
        component: () =>
          import(
            /* webpackChunkName:"DataCenter"*/
            '@/views/index'
          ),
        children: [
          {
            path: 'BasicVariables',
            name: 'BasicVariables',
            meta: {
              title: '元数据',
            },
            component: () =>
              import(
                /* webpackChunkName:"FeatureVariable"*/
                '@/views/DataCenter/modules/basicVariables.vue'
              ),
          },
          {
            path: 'FeatureVariable',
            name: 'FeatureVariable',
            meta: {
              title: '特征变量',
            },
            component: () =>
              import(
                /* webpackChunkName:"FeatureVariable"*/
                '@/views/DataCenter/modules/featureVariable.vue'
              ),
          },
          {
            path: 'AnalysisTarget',
            name: 'AnalysisTarget',
            meta: {
              title: '分析指标',
            },
            component: () =>
              import(
                /* webpackChunkName:"AnalysisTarget"*/
                '@/views/DataCenter/modules/analysisTarget.vue'
              ),
          },
        ],
      },
      {
        path: 'DecisionPlatform',
        name: 'DecisionPlatform',
        meta: {
          title: '决策平台',
        },
        component: () =>
          import(
            /* webpackChunkName:"System"*/
            '@/views/index.vue'
          ),
        children: [
          {
            path: 'ProductDecision',
            name: 'ProductDecision',
            meta: {
              title: '产品决策',
            },
            component: () =>
              import(
                /* webpackChunkName:"ProductDecision"*/
                '@/views/decisionPlatform/modules/productDecision/index.vue'
              ),
          },
          {
            path: 'Review',
            name: 'Review',
            meta: {
              title: '产品决策',
            },
            component: () =>
              import(
                /* webpackChunkName:"Review"*/
                '@/views/decisionPlatform/modules/review/index.vue'
              ),
          },
          {
            path: 'ModelDecision',
            name: 'ModelDecision',
            meta: {
              title: '模型决策',
            },
            component: () =>
              import(
                /* webpackChunkName:"ModelDecision"*/
                '@/views/index.vue'
              ),
          },
        ],
      },
      {
        path: 'RulePool',
        name: 'RulePool',
        meta: {
          title: '规则池',
        },
        component: () =>
          import(
            /* webpackChunkName:"System"*/
            '@/views/index.vue'
          ),
        children: [
          {
            path: 'Strategy',
            name: 'Strategy',
            meta: {
              title: '策略规则池',
            },
            component: () =>
              import(
                /* webpackChunkName:"ProductDecision"*/
                '@/views/rulePool/strategy/index.vue'
              ),
          },
        ],
      },
      {
        path: 'PlatformEngine',
        name: 'PlatformEngine',
        meta: {
          title: '平台引擎',
        },
        component: () =>
          import(
            /* webpackChunkName:"System"*/
            '@/views/index.vue'
          ),
        children: [
          {
            path: 'WorkflowEngine',
            name: 'WorkflowEngine',
            meta: {
              title: '流程引擎',
            },
            component: () =>
              import(
                /* webpackChunkName:"BasicVariables"*/
                '@/views/platformEngine/modules/workflowEngine/index.vue'
              ),
          },
          {
            path: 'TaskApproval',
            name: 'TaskApproval',
            meta: {
              title: '任务审批',
            },
            component: () =>
              import(
                /* webpackChunkName:"CharacteristicVariable"*/
                '@/views/platformEngine/modules/taskApproval/index.vue'
              ),
          },
          {
            path: 'TaskRecord',
            name: 'TaskRecord',
            meta: {
              title: '任务记录',
            },
            component: () =>
              import(
                /* webpackChunkName:"AnalysisIndicators"*/
                '@/views/platformEngine/modules/taskRecord/index.vue'
              ),
          },
          {
            path: 'ProcessTask',
            name: 'ProcessTask',
            meta: {
              title: '流程任务',
            },
            component: () =>
              import(
                /* webpackChunkName:"processTask"*/
                '@/views/platformEngine/modules/processTask/index.vue'
              ),
          },
          {
            path: 'RiskReport',
            name: 'RiskReport',
            meta: {
              title: '评估报告',
            },
            component: () =>
              import(
                /* webpackChunkName:"RiskReport"*/
                '@/views/platformEngine/modules/processTask/riskReport.vue'
              ),
          },
          {
            path: 'RiskReport',
            name: 'RiskReport',
            meta: {
              title: '评估报告',
            },
            component: () =>
              import(
                /* webpackChunkName:"RiskReport"*/
                '@/views/platformEngine/modules/processTask/riskReport.vue'
              ),
          },
        ],
      },
      {
        path: 'Backtrack',
        name: 'Backtrack',
        meta: {
          title: '流程回溯',
        },
        component: () =>
          import(/* webpackChunkName:"Backtrack"*/ '@/views/index.vue'),
        children: [
          {
            path: 'HistoryTask',
            name: 'HistoryTask',
            meta: {
              title: '历史任务列表',
            },
            component: () =>
              import(
                /* webpackChunkName:"HistoryTaskList"*/ '@/views/processBacktrack/historyTask/index.vue'
              ),
          },
          {
            path: 'BacktrackTask',
            name: 'BacktrackTask',
            meta: {
              title: '回溯任务列表',
            },
            component: () =>
              import(
                /* webpackChunkName:"HistoryTaskList"*/ '@/views/processBacktrack/backtrackTask/index.vue'
              ),
          },
        ],
      },
      {
        path: 'Monitor',
        name: 'Monitor',
        meta: {
          title: '监测',
        },
        component: () =>
          import(
            /* webpackChunkName:"System"*/
            '@/views/index.vue'
          ),
        children: [
          {
            path: 'WarningTask',
            name: 'WarningTask',
            meta: {
              title: '预警任务',
            },
            component: () =>
              import(
                /* webpackChunkName:"BasicVariables"*/
                '@/views/monitor/warningTask/index.vue'
              ),
          },
        ],
      },
      {
        path: 'System',
        name: 'System',
        meta: {
          title: '系统管理',
        },
        component: () =>
          import(
            /* webpackChunkName:"System"*/
            '@/views/index.vue'
          ),
        children: [
          {
            path: 'UserManage',
            name: 'UserManage',
            meta: {
              title: '用户管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"UserManage"*/
                '@/views/system/userManage/index.vue'
              ),
          },
          {
            path: 'RoleManage',
            name: 'RoleManage',
            meta: {
              title: '角色管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"RoleManage"*/
                '@/views/system/roleManage/index.vue'
              ),
          },
          {
            path: 'MenuManage',
            name: 'MenuManage',
            meta: {
              title: '菜单管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"MenuManage"*/
                '@/views/system/menuManagement/index.vue'
              ),
          },
          {
            path: 'ApprovalAuth',
            name: 'ApprovalAuth',
            meta: {
              title: '审批授权',
            },
            component: () =>
              import(
                /* webpackChunkName:"MenuManage"*/
                '@/views/system/approvalAuth/index.vue'
              ),
          },
          {
            path: 'DictionaryManagement',
            name: 'DictionaryManagement',
            meta: {
              title: '字典管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"DictionaryManagement"*/
                '@/views/system/dictionaryManagement/index.vue'
              ),
          },
          {
            path: 'DictionaryDetail',
            name: 'DictionaryDetail',
            meta: {
              title: '字典详情',
            },
            component: () =>
              import(
                /* webpackChunkName:"DictionaryDetail"*/
                '@/views/system/dictionaryManagement/dictionaryDetail.vue'
              ),
          },
          {
            path: 'Department',
            name: 'Department',
            meta: {
              title: '部门管理',
            },
            component: () =>
              import(
                /* webpackChunkName:"Department"*/
                '@/views/system/department/index.vue'
              ),
          },
          // {
          //   path: 'Account',
          //   name: 'Account',
          //   meta: {
          //     title: '部门管理',
          //   },
          //   component: () =>
          //     import(
          //       /* webpackChunkName:"Department"*/
          //       '@/views/system/accountResources/index.vue'
          //     ),
          // },
          {
            path: 'DecisionManage',
            name: 'DecisionManage',
            meta: {
              title: '操作日志',
            },
            component: () =>
              import(
                /* webpackChunkName:"DecisionManage"*/
                '@/views/system/decisionManage/index.vue'
              ),
          },
          {
            path: 'OperlogManage',
            name: 'OperlogManage',
            meta: {
              title: '操作日志',
            },
            component: () =>
              import(
                /* webpackChunkName:"OperlogManage"*/
                '@/views/system/operlogManage/index.vue'
              ),
          },
          {
            path: 'RegisterLog',
            name: 'RegisterLog',
            meta: {
              title: '操作日志',
            },
            component: () =>
              import(
                /* webpackChunkName:"RegisterLog"*/
                '@/views/system/registerLog/index.vue'
              ),
          },
        ],
      },
    ],
  },
]
