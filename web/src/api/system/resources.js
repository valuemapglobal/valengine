/*
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2024-06-03 13:30:29
 * @LastEditTime: 2024-06-06 09:24:43
 * @LastEditors: Seastar 1507136388@qq.com
 * @Description:
 */
import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/billing'
    : '/dev-api/vm/billing'
/**
 * 接口文档
 * https://valuemap.feishu.cn/docx/SfvodwjGAo56qdxJqahcOfE3nDc
 */

//获取资源类型接口
export function getResourceType() {
  return request({
    url: url + `/equityTeamResource/getResourceType`,
    method: 'get',
  })
}
//资源配置-列表接口
export function adminList(data) {
  return request({
    url: url + `/equityTeamResource/adminList`,
    method: 'post',
    data,
  })
}
//资源配置-新增资源接口
export function add(data) {
  return request({
    url: url + `/equityTeamResource/add`,
    method: 'post',
    data,
  })
}
export function edit(data) {
  return request({
    url: url + `/equityTeamResource/edit`,
    method: 'post',
    data,
  })
}
//资源配置-批量上/下架
export function editsStatus(data) {
  return request({
    url: url + `/equityTeamResource/edit/status`,
    method: 'post',
    data,
  })
}
//资源配置-批量删除
export function remove(data) {
  return request({
    url: url + `/equityTeamResource/remove`,
    method: 'post',
    data,
  })
}
