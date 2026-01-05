/*
 * @Author: Grace
 * @Date: 2023-07-017 17:43:54
 * @LastEditors: Seastar 1507136388@qq.com
 */
import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'
const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

import axios from 'axios'
const CancelToken = axios.CancelToken

// 新增流程策略
export function addSubmit(data) {
  return request({
    url: `${url}/process-policy/submit`,
    method: 'post',
    data,
  })
}
// 编辑流程策略
export function updateSubmit(data) {
  return request({
    url: `${url}/process-policy/update`,
    method: 'post',
    data,
  })
}
// 流程策略列表
export function policyList(data) {
  return request({
    url: `${url}/process-policy/list`,
    method: 'post',
    data,
  })
}
// 删除-流程策略

export function deletePolicy(data) {
  return request({
    url: `${url}/process-policy/delete`,
    method: 'post',
    data,
  })
}
// 更新状态
export function updateStatus(data) {
  return request({
    url: `${url}/process-policy/updateStatus`,
    method: 'post',
    data,
  })
}

// 获取下拉字典
export function getType(dict_type) {
  return request({
    url: `${url1}/system/dict/data/type/${dict_type}`,
    method: 'get',
  })
}

//获取业务场景
export function getBusinessScenario() {
  return request({
    url: `${url}/business/_search`,
    method: 'post',
  })
}

// 获取策略模型
export function getmoduleId(data) {
  return request({
    url: `${url}/process-policy/getmoduleId`,
    method: 'post',
    data: data,
  })
}
// 获取评分名称下拉模型
export function getmoduleName(data) {
  return request({
    url: `${url}/process-policy/getmodule`,
    method: 'post',
    data: data,
  })
}
// 流程策略同名校验

export function checkName(data) {
  return request({
    url: `${url}/process-policy/checkName`,
    method: 'post',
    data: data,
  })
}
// 下拉框获取审批人员

export function listByDeptId() {
  return request({
    url: `${url1}/system/user/listByDeptIdHidden`,
    method: 'get',
  })
}
// 策略编辑
export function policyUpdate(data) {
  return request({
    url: `${url}/process-policy/update`,
    method: 'post',
    data,
  })
}
// 获取待审批任务
export function getTask(data) {
  return request({
    url: `${url}/process-policy-task/getTask`,
    method: 'post',
    data,
  })
}
// 获取任务列表
export function getTaskList(data, that) {
  return request({
    url: `${url}/process-policy-task/getTaskList`,
    method: 'post',
    data,
    cancelToken: new CancelToken(function executor(c) {
      console.log(that, 'that-----------')
      that.cancel = c
    }),
  })
}
//审批
export function getApproval(data) {
  return request({
    url: `${url}/process-policy-task/approval`,
    method: 'post',
    data,
  })
}
// 获取流程详情

export function getTaskDetail(data) {
  return request({
    url: `${url}/process-policy-task/getTaskDetail`,
    method: 'post',
    data,
  })
}
//获取审核状态、
export function getStatusType(dict_type) {
  return request({
    url: `${url1}/system/dict/data/type/${dict_type}`,
    method: 'get',
  })
}
//获取国贸报告
export function getGMReport(data) {
  return request({
    // url: `${url}/gm/report`,
    url: `${url}/gm/reportV2`,
    method: 'post',
    data,
  })
}

//模型轨迹日志
export function addModelChangeLog(data) {
  return request({
    // url: `${url}/gm/report`,
    url: `${url1}/smartData/modelChangeLog/addModelChangeLog`,
    method: 'post',
    data,
  })
}

export function getPramById(params) {
  return request({
    url: `${url}/process-policy-task/getPramById`,
    method: 'get',
    params,
  })
}
export function submitPolicyTask(data) {
  return request({
    url: `${url}/process-policy-task/submit`,
    method: 'post',
    data,
  })
}

//导出司法数据
export function judicialExcelExport(data) {
  return request({
    url: `${url1}/case/combine/judicialExcelExport`,
    method: 'post',
    data,
  })
}

/**
 * 2.3.3 流程策略列表-下拉框  --流程使用页面
 * @returns {*}
 */
export function getProcessStrategySelect() {
  return request({
    url: `${url}/process-policy/select`,
    method: 'get',
  })
}

/**
 * 2.3.5 流程ID获取入参
 * @param policyId
 * @returns {*}
 */
export function getRequestData(policyId) {
  return request({
    url: `${url}/process-policy/getRequestData/${policyId}`,
    method: 'get',
  })
}

/**
 * 2.3.6 通用流程启动 --流程使用页面 异步
 * @param data
 * @returns {*}
 */
export function processTaskInitiation(data) {
  return request({
    url: `${url}/model-task-record/processTaskInitiation`,
    method: 'post',
    data,
  })
}

// 通用流程启动 --流程使用页面 同步
export function processTaskInitiationSync(data) {
  return request({
    url: `${url}/model-task-record/processTaskInitiationSync`,
    method: 'post',
    data,
  })
}

/**
 * 2.3.7 流程任务列表 --流程任务页面
 * @param data
 * @returns {*}
 */
export function taskRecordList(data) {
  return request({
    url: `${url}/model-task-record/taskRecordList`,
    method: 'post',
    data,
  })
}
// 批次列表
export function batchList(data) {
  return request({
    url: `${url}/model-task-record-batch/batchList`,
    method: 'post',
    data,
  })
}

/**
 * 2.3.8 数据响应形式 --流程任务页面
 * @param data
 * @returns {*}
 */
export function dataResponseForm(data) {
  return request({
    url: `${url}/model-task-record/dataResponseForm`,
    method: 'post',
    data,
  })
}

// https://valuemap.feishu.cn/wiki/AJodwa6IKiMYXsklPVSc3GY1n2D
// 流程执行失败详情接口
export function dataResponseFailure(taskNo) {
  return request({
    url: `${url}/model-task-record/failure/${taskNo}`,
    method: 'get',
  })
}

// https://valuemap.feishu.cn/wiki/DBCyw6bc6iskEXkL6k4cY7GVn8e
// 评估报告接口
export function reportResponse(data) {
  return request({
    url: `${url}/model-task-record/report-response`,
    method: 'post',
    data,
  })
}

// https://valuemap.feishu.cn/wiki/G7g6wasLBizJp1kKlZJcra0Vnrc
// 批量上传文件
export function uploadFile(data) {
  return `${url}/model-task-record-batch/upload-file`
}

export function getTemplateExcel(policyId) {
  return request({
    url: `${url}/process-policy/getRequestDataExcel/${policyId}`,
    method: 'get',
    responseType: 'blob',
  })
}
export function submitBatch(data) {
  return request({
    url: `${url}/model-task-record-batch/submit-batch`,
    method: 'post',
    data,
  })
}
