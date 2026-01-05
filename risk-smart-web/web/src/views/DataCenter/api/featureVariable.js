/**
 * @author chris
 * @date 2025/4/22 9:18
 */
import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartData'
    : '/dev-api/vm/smartData'
/**
 * 获取特征变量模块列表
 * @param params
 * @returns {*}
 */
export function featureModuleList(params) {
  return request({
    url: `${url}/feature-module/list`,
    method: 'get',
    params,
  })
}
/**
 * 获取特征变量属性列表
 * @param params
 * @returns {*}
 */
export function featureAttributeList(params) {
  return request({
    url: `${url}/feature-attribute/list`,
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
    url: `${url}/feature-module`,
    method: 'post',
    data,
  })
}
/**
 * 属性添加
 * @param data
 * @returns {*}
 */
export function addAttributeData(data) {
  return request({
    url: `${url}/feature-attribute`,
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
    url: `${url}/feature-module`,
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
    url: `${url}/feature-attribute`,
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
    url: `${url}/feature-module/${id}`,
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
    url: `${url}/feature-attribute/${id}`,
    method: 'delete',
  })
}
/**
 * 文本解析
 * @param data
 * @returns {*}
 */
export function parseScript(data) {
  return request({
    url: `${url}/feature-attribute/parse-script`,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data,
  })
}
/**
 * 模块名称校验---是否重复
 * @param params
 * @returns {*}
 */
export function featureModuleCheckName(params) {
  return request({
    url: `${url}/feature-module/check-name`,
    method: 'get',
    params,
  })
}

/**
 * 模块标识校验---是否重复
 * @param params
 * @returns {*}
 */
export function featureModuleCheckCode(params) {
  return request({
    url: `${url}/feature-module/check-code`,
    method: 'get',
    params,
  })
}

/**
 * 属性名称校验---是否重复
 * @param params
 * @returns {*}
 */
export function featureAttributeCheckName(params) {
  return request({
    url: `${url}/feature-attribute/check-name`,
    method: 'get',
    params,
  })
}

/**
 * 属性参数名校验---是否重复
 * @param params
 * @returns {*}
 */
export function featureAttributeCheckCode(params) {
  return request({
    url: `${url}/feature-attribute/check-code`,
    method: 'get',
    params,
  })
}
