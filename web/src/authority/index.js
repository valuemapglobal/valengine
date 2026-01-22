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
        localStorage.setItem('userInfo', JSON.stringify(res.user))
        store.commit('setUserInfo', res.user)
        store.commit('setBenefitAccount', res.benefitAccount)

        // 获取权限并设置到 store
        let permissions = res.permissions || []
        let data = permissions.filter(
          (i) => i.match(/:/g) && i.match(/:/g).length === 2
        )
        // 直接设置按钮权限
        store.commit('setButtonList', data)
        console.log('[authority] setButtonList:', data)

        try {
          store.commit('setUserPreference', {
            list: res.userPreferenceList,
            status: res.userGuidePageStatus,
          })
        } catch (error) {
          console.error('[authority] setUserPreference error:', error)
        }

        resolve(data)
      })
      .catch((err) => {
        reject(err)
      })
  })
  return Promise.all([one, two])
}
