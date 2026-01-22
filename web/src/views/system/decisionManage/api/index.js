import request from '@/utils/request'

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'
// 获取下拉字典
export function getType(dict_type) {
  return request({
    url: `${url}/system/dict/data/type/${dict_type}`,
    method: 'get',
  })
}
// 获取运行日志
export function getlog(data) {
  return request({
    url: `${url}/smartDecision/log/getlog`,
    method: 'post',
    data,
  })
}
//获取审核状态、
export function getStatusType(dict_type) {
  return request({
    url: `${url}/system/dict/data/type/${dict_type}`,
    method: 'get',
  })
}
// 获取部门下拉
export function getuserdp() {
  return request({
    url: `${url}/system/dept/list`,
    method: 'get',
  })
}
//审批
export function getApproval(data) {
  return request({
    url: `${url}/smartDecision/process-policy-task/approval`,
    method: 'post',
    data,
  })
}
//
export function selectOneUserByUser(data) {
  return request({
    url: `${url}/system/user/selectOneUserByUser`,
    method: 'post',
    data,
  })
}
// 获取金融产品
export function selectAll(data) {
  return request({
    url: `${url}/financing/product/selectAll`,
    method: 'post',
    data,
  })
}

export function selectProduct(data) {
  return request({
    url: `${url}/smartDecision/product/_search`,
    method: 'post',
    data,
  })
}
