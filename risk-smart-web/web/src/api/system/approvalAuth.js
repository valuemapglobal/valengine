/**
 * https://valuemap.feishu.cn/wiki/FUq5wSs7qigvDjkB2CccDX5Ynwb
 */

import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/prod-api/smartDecision'
    : '/dev-api/prod-api/smartDecision'

// 权限列表查询
export function listByDeptIdHidden(data) {
  return request({
    url: `${url}/user/listByDeptIdHidden`,
    method: 'post',
    data,
  })
}

// 权限列表查询
export function approvalAuthList(data) {
  return request({
    url: `${url}/approval-permission/list`,
    method: 'post',
    data,
  })
}

// 分配权限
export function approvalAuthAssign(data) {
  return request({
    url: `${url}/approval-permission/assign`,
    method: 'post',
    data,
  })
}

// 删除权限
export function approvalAuthRemove(permissionId) {
  return request({
    url: `${url}/approval-permission/remove/${permissionId}`,
    method: 'post',
  })
}

// 权限级别列表查询
export function approvers(data) {
  return request({
    url: `${url}/approval-permission/approvers`,
    method: 'get',
    params: data,
  })
}
