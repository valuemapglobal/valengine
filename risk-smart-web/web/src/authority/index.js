/*
 * @Date: 2022-07-12 14:53:19
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-01-24 15:19:30
 * @name:
 * @FilePath: \vm-micro-middleground\src\authority\index.js
 */
import store from '@/store'
import request from '@/utils/request'

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

function getPermission(url) {
  return request({
    url: url,
    method: 'get',
  })
}

export default function () {
  let one = new Promise((resolve, reject) => {
    getPermission(url + '/system/menu/getRouters')
      .then((res) => {
        try {
          resolve(res.data)
        } catch (error) {
          resolve([])
        }
      })
      .catch((err) => {
        reject(err)
      })
  })
  let two = new Promise((resolve, reject) => {
    getPermission(url + '/system/user/getLoginInfo')
      .then((res) => {
        console.log('[权限加载] API响应:', res)

        localStorage.setItem('userInfo', JSON.stringify(res.user))
        store.commit('setUserInfo', res.user)
        store.commit('setBenefitAccount', res.benefitAccount)
        try {
          let { permissions } = res
          console.log('[权限加载] 原始权限:', permissions)
          store.commit('setUserPreference', {
            list: res.userPreferenceList || [],
            status: res.userGuidePageStatus || 0,
          })
          // 确保 permissions 是数组
          if (!Array.isArray(permissions)) {
            console.warn('[权限加载] permissions 不是数组:', permissions)
            permissions = []
          }
          let data = permissions.filter(
            (i) => i.match(/:/g) && i.match(/:/g).length === 2
          )
          console.log('[权限加载] 过滤后权限:', data)
          resolve(data)
        } catch (error) {
          console.error('[权限加载] 错误:', error)
          resolve([])
        }
      })
      .catch((err) => {
        reject(err)
      })
  })
  return Promise.all([one, two])
}
