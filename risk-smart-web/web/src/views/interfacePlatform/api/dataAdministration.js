/*
 * @Author: Grace
 * @Date: 2023-07-017 17:43:54
 * @LastEditors: Seastar 1507136388@qq.com
 */
import request from '@/utils/request'

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

// 获取数据管理tabs
export function getAllApiServiceType(type) {
  return request({
    url: `${url}/risk/apiservice/getAllApiServiceType/${type}`,
    method: 'GET',
  })
}
// 获取左侧menu列表

export function getApiCategoryByType(data) {
  return request({
    url: `${url}/risk/apiservice/getApiCategoryByType`,
    method: 'post',
    data,
  })
}
// 获取接口列表

export function apiserviceList(data) {
  return request({
    url: `${url}/risk/apiservice/list`,
    method: 'post',
    data,
  })
}
// 根据接口编号查询接口信息
export function apiserviceQuery(id) {
  return request({
    url: `${url}/risk/apiservice/query/${id}`,
    method: 'get',
  })
}
// 更新状态
export function updateApiStatus(data) {
  return request({
    url: `${url}/risk/apiservice/updateApiStatus`,
    method: 'post',
    data,
  })
}
// 根据接口编号查询请求头
export function apiserviceheaders(id) {
  return request({
    url: `${url}/risk/apiserviceheaders/list/${id}`,
    method: 'get',
  })
}
// 接口新增
export function save(data) {
  return request({
    url: `${url}/risk/apiservice/save`,
    method: 'post',
    data,
  })
}
// 接口测试
export function test(data) {
  return request({
    url: `${url}/risk/apiservice/test`,
    method: 'post',
    data,
  })
}

// 测试接口连通性并保存
export function testandsave(data) {
  return request({
    url: `${url}/risk/apiservice/testandsave`,
    method: 'post',
    data,
  })
}
