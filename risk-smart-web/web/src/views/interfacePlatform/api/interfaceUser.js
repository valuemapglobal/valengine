/*
 * @Date: 2023-10-31 15:45:26
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2023-11-01 11:05:58
 * @FilePath: \vm-micro-middleground\src\interfacePlatform\api\interfaceUser.js
 */
import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartData'
    : '/dev-api/vm/smartData'
const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

export function getUser(data) {
  return request({
    url: `${url1}/system/gmUser/list`,
    method: 'post',
    data,
  })
}

export function getUserKey() {
  return request({
    url: `${url}/interfaceUserPermissions/generateAppKeyAndSecret`,
    method: 'get',
  })
}

export function addInterfaceUser(data) {
  return request({
    url: `${url}/interfaceUserPermissions/addInterfaceUser`,
    method: 'post',
    data,
  })
}

//获取接口树形菜单
export function getTree(data) {
  return request({
    url: `${url}/interfacePermissions/treeMenuList`,
    method: 'post',
    data,
  })
}

//获取授权列表
export function getPermissions(data) {
  return request({
    url: `${url}/interfacePermissions/getPermissions`,
    method: 'post',
    data,
  })
}

//更新授权列表
export function updatePermissions(data) {
  return request({
    url: `${url}/interfacePermissions/updatePermissions`,
    method: 'post',
    data,
  })
}
