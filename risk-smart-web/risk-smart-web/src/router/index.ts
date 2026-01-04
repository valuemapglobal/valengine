import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: 'Login', requiresAuth: false },
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/interface/management',
    children: [
      // 接口平台
      {
        path: 'interface',
        name: 'Interface',
        redirect: '/interface/management',
        meta: { title: '接口平台', icon: 'interface' },
        children: [
          {
            path: 'scene',
            name: 'InterfaceScene',
            component: () => import('@/views/interface/dataScenario/index.vue'),
            meta: { title: '数据场景管理' },
          },
          {
            path: 'management',
            name: 'InterfaceManagement',
            component: () => import('@/views/interface/management/index.vue'),
            meta: { title: '接口管理' },
          },
          {
            path: 'audit',
            name: 'DataAudit',
            component: () => import('@/views/interface/audit/index.vue'),
            meta: { title: '数据审计' },
          },
          {
            path: 'reports',
            name: 'DataReports',
            component: () => import('@/views/interface/reports/index.vue'),
            meta: { title: '数据报表' },
          },
        ],
      },
      // 数据中心
      {
        path: 'data-center',
        name: 'DataCenter',
        redirect: '/data-center/metadata',
        meta: { title: '数据平台', icon: 'data' },
        children: [
          {
            path: 'metadata',
            name: 'Metadata',
            component: () => import('@/views/dataCenter/metadata/index.vue'),
            meta: { title: '元数据管理' },
          },
          {
            path: 'variable',
            name: 'Variable',
            component: () => import('@/views/dataCenter/variable/index.vue'),
            meta: { title: '特征变量管理' },
          },
          {
            path: 'indicator',
            name: 'Indicator',
            component: () => import('@/views/dataCenter/indicator/index.vue'),
            meta: { title: '分析指标管理' },
          },
        ],
      },
      // 规则池
      {
        path: 'rule-pool',
        name: 'RulePool',
        redirect: '/rule-pool/strategy',
        meta: { title: '规则池', icon: 'list' },
        children: [
          {
            path: 'strategy',
            name: 'RuleStrategy',
            component: () => import('@/views/rulePool/strategy/index.vue'),
            meta: { title: '规则策略列表' },
          }
        ]
      },
      // 决策平台
      {
        path: 'decision',
        name: 'Decision',
        redirect: '/decision/product',
        meta: { title: '决策中台', icon: 'monitor' },
        children: [
          {
            path: 'product',
            name: 'ProductDecision',
            component: () => import('@/views/decision/product/index.vue'),
            meta: { title: '产品决策' }
          }
        ]
      },
      // 平台引擎
      {
        path: 'platform-engine',
        name: 'PlatformEngine',
        redirect: '/platform-engine/flow',
        meta: { title: '平台引擎', icon: 'cpu' },
        children: [
          {
            path: 'flow',
            name: 'WorkflowEngine',
            component: () => import('@/views/platformEngine/flow/index.vue'),
            meta: { title: '流程管理' }
          },
          {
            path: 'approval',
            name: 'TaskApproval',
            component: () => import('@/views/platformEngine/approval/index.vue'),
            meta: { title: '任务审批' }
          },
          {
            path: 'record',
            name: 'TaskRecords',
            component: () => import('@/views/platformEngine/record/index.vue'),
            meta: { title: '任务记录' }
          }
        ]
      },
      // 系统管理
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'setting' },
        children: [
          {
            path: 'user',
            name: 'UserManagement',
            component: () => import('@/views/system/user/index.vue'),
            meta: { title: '用户管理' },
          },
          {
            path: 'role',
            name: 'RoleManagement',
            component: () => import('@/views/system/role/index.vue'),
            meta: { title: '角色管理' },
          },
          {
            path: 'menu',
            name: 'MenuManagement',
            component: () => import('@/views/system/menu/index.vue'),
            meta: { title: '菜单管理' },
          },
        ],
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation guard
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth !== false && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
