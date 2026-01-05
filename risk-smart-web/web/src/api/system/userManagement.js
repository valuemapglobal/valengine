import request from '@/utils/request'
const url = process.env.VUE_APP_ENV === 'production' ? '/prod-api' : '/dev-api/prod-api'

export function getTreeselect(cname) {
  return request({
    url: url + `/system/dept/treeselect`,
    method: 'get',
  })
}
export function getList(data) {
  return request({
    url: url + `/system/user/list`,
    method: 'get',
    params: data
  })
}
export function changeStatus(data) {
  return request({
    url: url + `/system/user/changeStatus`,
    method: 'put',
    data
  })
}
export function putUser(data) {
  return request({
    url: url + `/system/user`,
    method: 'put',
    data
  })
}
export function getUserInfo(id) {
  return request({
    url: url + `/system/user/${id}`,
    method: 'get',
  })
}
export function getUser(data) {
  return request({
    url: url + `/system/user/`,
    method: 'get',
    params: data
  })
}
export function postUser(data) {
  return request({
    url: url + `/system/user/`,
    method: 'post',
    data
  })
}
export function deletetUser(id) {
  return request({
    url: url + `/system/user/${id}`,
    method: 'delete',
  })
}
export function resetPwd(data) {
  return request({
    url: url + `/system/user/resetPwd`,
    method: 'put',
    data
  })
}
export function authRole(id) {
  return request({
    url: url + `/system/user/authRole/${id}`,
    method: 'get',
  })
}
export function putAuthRole(data) {
  return request({
    url: url + `/system/user/authRole`,
    method: 'put',
    params: data
  })
}
export function exportData(data) {
  return url + `/system/user/export`
}

export function getTemplate(data) {
  return url + `/system/user/importTemplate`
}