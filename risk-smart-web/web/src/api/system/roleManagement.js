/*
 * @Date: 2022-10-13 09:45:31
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-10-14 01:14:04
 * @name: 
 * @FilePath: /vmkj/src/api/roleManagement/index.js
 */
import request from '@/utils/request'
const url = process.env.VUE_APP_ENV === 'production' ? '/prod-api' : '/dev-api'

export function getTreeselect(cname) {
  return request({
    url: url + `/system/menu/treeselect`,
    method: 'get',
  })
}
export function getList(data) {
  return request({
    url: url + `/system/role/list`,
    method: 'get',
    params: data
  })
}
export function changeStatus(data) {
  return request({
    url: url + `/system/role/changeStatus`,
    method: 'put',
    data
  })
}
export function putUser(data) {
  return request({
    url: url + `/system/role`,
    method: 'put',
    data
  })
}
export function getUserInfo(id) {
  return request({
    url: url + `/system/role/${id}`,
    method: 'get',
  })
}
export function roleMenuTreeselect(id) {
  return request({
    url: url + `/system/menu/roleMenuTreeselect/${id}`,
    method: 'get',
  })
}
export function getUser(data) {
  return request({
    url: url + `/system/role/`,
    method: 'get',
    params: data
  })
}
export function postUser(data) {
  return request({
    url: url + `/system/role/`,
    method: 'post',
    data
  })
}
export function deletetUser(id) {
  return request({
    url: url + `/system/role/${id}`,
    method: 'delete',
  })
}
export function resetPwd(data) {
  return request({
    url: url + `/system/role/resetPwd`,
    method: 'put',
    data
  })
}
export function dataScope(data) {
  return request({
    url: url + `/system/role/dataScope`,
    method: 'put',
    data
  })
}
export function authRole(id) {
  return request({
    url: url + `system/user/authRole/${id}`,
    method: 'get',
  })
}
export function getAllocatedList(data) {
  return request({
    url: url + `/system/role/authUser/allocatedList`,
    method: 'get',
    params: data
  })
}
export function putCancel(data) {
  return request({
    url: url + `/system/role/authUser/cancel`,
    method: 'put',
    data
  })
}
export function putCancelAll(data) {
  return request({
    url: url + `/system/role/authUser/cancelAll?roleId=${data.roleId}&userIds=${data.userId}`,
    method: 'put',
  })
}
export function unallocatedList(data) {
  return request({
    url: url + `/system/role/authUser/unallocatedList`,
    method: 'get',
    params: data
  })
}
export function authUserSelectAll(data) {
  return request({
    url: url + `/system/role/authUser/selectAll?roleId=${data.roleId}&userIds=${data.userIds}`,
    method: 'put',
  })
}
export function exportData(data) {
  return url + `/system/role/export`

}