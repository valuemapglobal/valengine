/*
 * @Author: Grace
 * @Date: 2023-07-017 17:43:54
 * @LastEditors: Seastar 1507136388@qq.com
 */
import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartData'
    : '/dev-api/vm/smartData'

// 新增数据场景
export function saveSourceInfo(data) {
  return request({
    url: `${url}/interfaceManage/saveSourceInfo`,
    method: 'post',
    data,
  })
}
//删除数据场景
export function removeSourceInfo(data) {
  return request({
    url: `${url}/interfaceManage/removeSourceInfo`,
    method: 'post',
    data,
  })
}
//修改数据场景
export function updateSourceInfo(data) {
  return request({
    url: `${url}/interfaceManage/updateSourceInfo`,
    method: 'post',
    data,
  })
}
// 数据场景列表
export function findSourceInfo(data) {
  return request({
    url: `${url}/interfaceManage/findSourceInfo`,
    method: 'post',
    data,
  })
}
// 列表查询接口
export function findInterfaceInfo(data) {
  return request({
    url: `${url}/interfaceManage/findInterfaceInfo`,
    method: 'post',
    data,
  })
}
// 删除接口
export function removeInterfaceInfo(data) {
  return request({
    url: `${url}/interfaceManage/removeInterfaceInfo`,
    method: 'post',
    data,
  })
}
// 添加接口
export function saveInterfaceInfo(data) {
  return request({
    url: `${url}/interfaceManage/saveInterfaceInfo`,
    method: 'post',
    data,
  })
}
// 修改接口

export function updateInterfaceInfo(data) {
  return request({
    url: `${url}/interfaceManage/updateInterfaceInfo`,
    method: 'post',
    data,
  })
}
// 查询参数
export function findInterfaceFieldIdInfo(data) {
  return request({
    url: `${url}/interfaceManage/findInterfaceFieldIdInfo`,
    method: 'post',
    data,
  })
}
// 修改参数
export function updateInterfaceFieldIdInfo(data) {
  return request({
    url: `${url}/interfaceManage/updateInterfaceFieldIdInfo`,
    method: 'post',
    data,
  })
}
// 删除参数
export function removeInterfaceFieldIdInfo(data) {
  return request({
    url: `${url}/interfaceManage/removeInterfaceFieldIdInfo`,
    method: 'post',
    data,
  })
}
// 添加参数
export function saveInterfaceFieldIdInfo(data) {
  return request({
    url: `${url}/interfaceManage/saveInterfaceFieldIdInfo`,
    method: 'post',
    data,
  })
}
// 获取数据审计-日志信息
export function findAllLog(data) {
  return request({
    url: `${url}/interfaceLog/findAllLog`,
    method: 'post',
    data,
  })
}
// 接口测试

export function findInterfaceInfoTest(data) {
  return request({
    url: `${url}/interfaceRequest/api`,
    method: 'post',
    data,
  })
}
// 元数据新增-主题

// export function themeSubmit(data) {
// 	return request({
// 		url: `${url}/metadata/theme/submit`,
// 		method: 'post',
//         data
// 	})
// }
//

//元数据-主题列表
export function getThemeList(data) {
  return request({
    url: `${url}/metadata/theme/list`,
    method: 'post',
    data,
  })
}
//获取元数据-对象列表
export function getGroupList(data) {
  return request({
    url: `${url}/metadata/group/list`,
    method: 'post',
    data,
  })
}
// 查询单个属性

export function getRecordList(data) {
  return request({
    url: `${url}/metadata/record/list`,
    method: 'post',
    data,
  })
}
// 获取特征变量-主题

export function findVariablesTopic(data) {
  return request({
    url: `${url}/characteristicVariables/findVariablesTopic`,
    method: 'post',
    data,
  })
}
//获取特征变量-分组

export function findVariablesGroup(data) {
  return request({
    url: `${url}/characteristicVariables/findVariablesGroup`,
    method: 'post',
    data,
  })
}
//获取分组-属性列表
export function findVariablesField(data) {
  return request({
    url: `${url}/characteristicVariables/findVariablesField`,
    method: 'post',
    data,
  })
}
// 获取所有元数据主题

export function themelistAll() {
  return request({
    url: `${url}/metadata/theme/listAll`,
    method: 'GET',
  })
}
// h获取所有的特征变量-主题

export function findVariablesAllTopicAll() {
  return request({
    url: `${url}/characteristicVariables/findVariablesAllTopic`,
    method: 'post',
  })
}
// 新增分析指标
export function addCategory(data) {
  return request({
    url: `${url}/analysisIndicatorsCategory/addCategory`,
    method: 'post',
    data,
  })
}
// 查询指标分类
export function getCategoryList(data) {
  return request({
    url: `${url}/analysisIndicatorsCategory/list`,
    method: 'post',
    data,
  })
}
// 修改分析指标
export function updateCategory(data) {
  return request({
    url: `${url}/analysisIndicatorsCategory/updateCategory`,
    method: 'post',
    data,
  })
}
// 删除分类指标
export function deleteCategory(data) {
  return request({
    url: `${url}/analysisIndicatorsCategory/deleteCategory`,
    method: 'post',
    data,
  })
}
// 分页查询指标对象
export function objectList(data) {
  return request({
    url: `${url}/analysisIndicatorsObject/objectList`,
    method: 'post',
    data,
  })
}
// 新增指标对象
export function addObject(data) {
  return request({
    url: `${url}/analysisIndicatorsObject/addObject`,
    method: 'post',
    data,
  })
}
// 修改指标对象
export function updateObject(data) {
  return request({
    url: `${url}/analysisIndicatorsObject/updateObject`,
    method: 'post',
    data,
  })
}
//规则列表

export function rulePage(data) {
  return request({
    url: `${url}/analysisIndicatorsRule/rulePage`,
    method: 'post',
    data,
  })
}
// 获取选项-树形列表
export function treeList(data) {
  return request({
    url: `${url}/analysisIndicatorsRule/treeList`,
    method: 'get',
    params: data,
  })
}
// 添加指标规则
export function addRule(data) {
  return request({
    url: `${url}/analysisIndicatorsRule/addRule`,
    method: 'post',
    data: data,
  })
}
// 查询单个规则

export function ruleDetails(data) {
  return request({
    url: `${url}/analysisIndicatorsRule/ruleDetails`,
    method: 'post',
    data: data,
  })
}
// 删除
export function deleteRule(data) {
  return request({
    url: `${url}/analysisIndicatorsRule/deleteRule`,
    method: 'post',
    data: data,
  })
}
// 修改指标规则
export function updateRule(data) {
  return request({
    url: `${url}/analysisIndicatorsRule/updateRule`,
    method: 'post',
    data: data,
  })
}
// 获取数据报表
export function countLog(data) {
  return request({
    url: `${url}/interfaceLog/countLog`,
    method: 'post',
    data: data,
  })
}
// 开启-关闭接口

export function interfaceOn(data) {
  return request({
    url: `${url}/interfaceManage/interfaceOn`,
    method: 'post',
    data: data,
  })
}
// 删除指标对象
export function deleteObject(data) {
  return request({
    url: `${url}/analysisIndicatorsObject/deleteObject`,
    method: 'post',
    data: data,
  })
}
