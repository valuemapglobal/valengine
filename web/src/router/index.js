import Vue from 'vue'
import VueRouter from 'vue-router'
import authority from '@/authority/index'
import store from '@/store'
import { configRoutes } from './config'
import i18n from '@/i18n'

Vue.use(VueRouter)
const routes = [
  {
    path: '/',
    redirect: '/InterfacePlatform/SmartDecision',
  },
  {
    path: '/login',
    name: 'login',
    component: () =>
      import(/* webpackChunkName:"login"*/ '@/views/login/Login.vue'),
  },
  {
    path: '/user',
    name: 'user',
    component: () => import(/* webpackChunkName:"user"*/ '@/layout/Index.vue'),
    children: [
      {
        path: 'userInfo',
        name: 'userInfo',
        component: () =>
          import(
            /* webpackChunkName:"user/userinfo"*/ '@/views/userInfo/UserInfo.vue'
          ),
      },
    ],
  },
  {
    path: '/MiddleWare',
    name: 'MiddleWare',
    meta: { title: '' },
    component: () =>
      import(/* webpackChunkName:"MiddleWare"*/ '@/middleWare/MiddleWare.vue'),
  },
  ...configRoutes,
]
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err)
}

const router = new VueRouter({
  base: process.env.BASE_URL,
  routes,
})

let status = true
let path = []
let whiteList = [
  '/login',
  '/MiddleWare',
  '/WaterTutorial',
  '/Collection',
  '/PersonalPrivacyAuthorization',
  '/OnlyOffice',
  '/DeptInvitation',
]

router.beforeEach(async (to, from, next) => {
  let id_token = localStorage.getItem('id_token')
  let loyout = localStorage.getItem('loyout')
  //如果是免登录空白页面，则不跳转到登录页
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 每次路由跳转获取token 如果没有token并且第一次进来则回到登录页，token过期同上
  if (!id_token) {
    status = true // 重置状态，下次登录后需要重新获取权限
    next({ name: 'login', replace: true })
    return
  }
  // 退出登录后，重新登录时需要重新拉取当前登录人的路由信息
  if (loyout && !status) {
    localStorage.removeItem('loyout')
    status = true
  }

  // 登录成功后需要重新获取权限（通过 needRefreshAuth 标志触发）
  let needRefreshAuth = localStorage.getItem('needRefreshAuth')
  if (needRefreshAuth) {
    localStorage.removeItem('needRefreshAuth')
    status = true
  }

  let tos = to.path === '/' ? [] : to.path.split('/')
  path = to.path
  // let routerList = []
  // 取消路由权限配置方案
  // 如果不在login页并且token存在切是第一次进入则去请求路由权限
  // 注意：只在 status 为 true 时获取权限，获取后立即设置 status = false 防止重复请求
  if (status && !path.includes('login') && id_token) {
    status = false // 立即设置为 false，防止重复请求
    let res = await authority()
    store.commit('setButtonList', res[1])
    // 判断是否有路径没有默认正常跳转
    if (tos.length) {
      next({ ...to, replace: true })
    } else {
      next({ ...to, replace: true })
    }
  } else {
    next()
  }
})

// 路由后置守卫：设置页面标题（国际化）
router.afterEach((to) => {
  if (to.meta && to.meta.title && to.name) {
    if (i18n.te(`route.${to.name}`)) {
      document.title = i18n.t(`route.${to.name}`)
    } else {
      document.title = to.meta.title
    }
  }
})
export default router
