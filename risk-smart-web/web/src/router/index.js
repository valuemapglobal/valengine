import Vue from 'vue'
import VueRouter from 'vue-router'
import authority from '@/authority/index'
import store from '@/store'
import { configRoutes } from './config'

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
  '/xinde/login',
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
    next({ name: 'login', replace: true })
    return
  }
  // 退出登录后，重新登录时需要重新拉取当前登录人的路由信息
  if (loyout && !status) {
    localStorage.removeItem('loyout')
    status = true
  }

  let tos = to.path === '/' ? [] : to.path.split('/')
  path = to.path
  // let routerList = []
  // 取消路由权限配置方案
  // 如果不在login页并且token存在切是第一次进入则去请求路由权限
  if (status && !path.includes('login') && id_token) {
    let res = await authority()
    store.commit('setButtonList', res[1])
    // 处理路由权限为自己可用状态
    status = false
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
export default router
