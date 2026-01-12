/**
 * https://valuemap.feishu.cn/wiki/FUq5wSs7qigvDjkB2CccDX5Ynwb
 */

import request from '@/utils/request'

const url =  process.env.VUE_APP_ENV === 'production'? '/prod-api/smartDecision' : '/dev-api/smartDecision'


//获取我的待处理审批任务
export function getApprovalsList(data) {
  return request({
    url: url + `/approval-flow/my-approvals`,
    method: 'post',
    data,
  })
}

// 获取审批批次详情
export function getBatchDetail(batchId) {
  return request({
    url: url + `/approval-flow/batch-detail/${batchId}`,
    method: 'get',
  })
}

// 处理审批任务
export function processApproval(data) {
  return request({
    url: url + `/approval-flow/process`,
    method: 'post',
    data,
  })
}