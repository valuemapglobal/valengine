/**
 * @author chris
 * @date 2025/4/24 14:05
 */
import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartData'
    : '/dev-api/vm/smartData'
/**
 * 模块列表
 * @param params
 * @returns {*}
 */
export function metricsModuleList(params) {
  return request({
    url: `${url}/metrics-module/list`,
    method: 'get',
    params,
  })
}
/**
 * 属性列表
 * @param params
 * @returns {*}
 */
export function metricsAttributeList(params) {
  return request({
    url: `${url}/metrics-attribute/list`,
    method: 'get',
    params,
  })
}
/**
 * 供应商列表
 * @param data
 * @returns {*}
 */
export function findSourceInfo(data) {
  return request({
    url: `${url}/interfaceManage/findSourceInfo`,
    method: 'post',
    data,
  })
}
/**
 * 接口列表
 * @param data
 * @returns {*}
 */
export function findInterfaceInfo(data) {
  return request({
    url: `${url}/interfaceManage/findInterfaceInfo`,
    method: 'post',
    data,
  })
}
/**
 * 参数列表
 * @param data
 * @returns {*}
 */
export function findInterfaceFieIdInfo(data) {
  return request({
    url: `${url}/interfaceManage/findInterfaceFieIdInfo`,
    method: 'post',
    data,
  })
}
/**
 * 模块添加
 * @param data
 * @returns {*}
 */
export function addModuleData(data) {
  return request({
    url: `${url}/metrics-module`,
    method: 'post',
    data,
  })
}
/**
 * 模块添加
 * @param data
 * @returns {*}
 */
export function addAttributeData(data) {
  return request({
    url: `${url}/metrics-attribute`,
    method: 'post',
    data,
  })
}
/**
 * 模块编辑
 * @param data
 * @returns {*}
 */
export function editModuleData(data) {
  return request({
    url: `${url}/metrics-module`,
    method: 'put',
    data,
  })
}
/**
 * 属性编辑
 * @param data
 * @returns {*}
 */
export function editAttributeData(data) {
  return request({
    url: `${url}/metrics-attribute`,
    method: 'put',
    data,
  })
}
/**
 * 获取多个接口入参（并集）
 * @param data
 * @returns {*}
 */
export function getInterfaceInputParameter(data) {
  return request({
    url: `${url}/interfaceManage/getInterfaceInputParameter`,
    method: 'post',
    data,
  })
}
/**
 * 模块删除
 * @param id
 * @returns {*}
 */
export function deleteModule(id) {
  return request({
    url: `${url}/metrics-module/${id}`,
    method: 'delete',
  })
}
/**
 * 属性删除
 * @param id
 * @returns {*}
 */
export function deleteAttribute(id) {
  return request({
    url: `${url}/metrics-attribute/${id}`,
    method: 'delete',
  })
}

/**
 * 模块名称校验---是否重复
 * @param params
 * @returns {*}
 */
export function analysisModuleCheckName(params) {
  return request({
    url: `${url}/metrics-module/check-name`,
    method: 'get',
    params,
  })
}

/**
 * 模块标识校验---是否重复
 * @param params
 * @returns {*}
 */
export function analysisModuleCheckCode(params) {
  return request({
    url: `${url}/metrics-module/check-code`,
    method: 'get',
    params,
  })
}

/**
 * 属性名称校验---是否重复
 * @param params
 * @returns {*}
 */
export function analysisAttributeCheckName(params) {
  return request({
    url: `${url}/metrics-attribute/check-name`,
    method: 'get',
    params,
  })
}

/**
 * 属性参数名校验---是否重复
 * @param params
 * @returns {*}
 */
export function analysisAttributeCheckCode(params) {
  return request({
    url: `${url}/metrics-attribute/check-code`,
    method: 'get',
    params,
  })
}
