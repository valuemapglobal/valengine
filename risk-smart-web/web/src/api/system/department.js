import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production' ? '/prod-api' : '/dev-api/prod-api'

const url1 =
  process.env.VUE_APP_ENV === 'production' ? '/vm/api' : '/dev-api/vm/api'
const url2 =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartData'
    : '/dev-api/vm/smartData'

export function listDept(params) {
  return request({
    url: url + `/system/dept/list`,
    method: 'get',
    params,
  })
}
export function listData(params) {
  return request({
    url: url + `/system/dict/data/list`,
    method: 'get',
    params,
  })
}
// 新增部门
export function addDept(data) {
  return request({
    url: url + '/system/dept',
    method: 'post',
    data,
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: url + '/system/dept',
    method: 'put',
    data,
  })
}

// 查询部门详细
export function getDept(deptId) {
  return request({
    url: url + '/system/dept/' + deptId,
    method: 'get',
  })
}

// 查询部门列表（排除节点）
export function listDeptExcludeChild(deptId) {
  return request({
    url: url + '/system/dept/list/exclude/' + deptId,
    method: 'get',
  })
}

// 删除部门
export function delDept(deptId) {
  return request({
    url: url + '/system/dept/' + deptId,
    method: 'delete',
  })
}

export function getBairongInfo(data) {
  return request({
    url: url1 + '/systemDeptParam/get',
    method: 'post',
    data,
  })
}
export function submitBairongInfo(data) {
  return request({
    url: url1 + '/systemDeptParam/submit',
    method: 'post',
    data,
  })
}

export function getValueMapInfo(data) {
  return request({
    url: url2 + '/interfaceDeptApp/queryDeptAppPage',
    method: 'post',
    data,
  })
}
export function submitValueMapInfo(data) {
  return request({
    url: url2 + '/interfaceDeptApp/submit',
    method: 'post',
    data,
  })
}
