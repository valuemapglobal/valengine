import request from '@/utils/request'

const url = process.env.VUE_APP_ENV === 'production' ? '/vm/smartDecision' : '/dev-api/vm/smartDecision'

// https://valuemap.feishu.cn/wiki/S8BYwXP3ditvLpkg8htcqlnynqb

//监测任务列表
export function taskList(data) {
  return request({
    url: url + `/monitor/warning/task/list`,
    method: 'post',
    data,
  })
}

//新增、编辑监测任务接口
export function saveOrUpdate(data) {
  return request({
    url: url + `/monitor/warning/task/saveOrUpdate`,
    method: 'PUT',
    data,
  })
}

// 流程策略列表
export function processPolicyList(data) {
  return request({
    url: url + `/process-policy/list`,
    method: 'post',
    data
  })
}

// 流程策略列表（启用）
export function processPolicyEnableList() {
  return request({
    url: url + `/monitor/warning/task/processPolicyList`,
    method: 'get'
  })
}

// 删除监测任务接口
export function deleteTask(id) {
  return request({
    url: url + `/monitor/warning/task/${id}`,
    method: 'delete',
  })
}

// 取消、启动监测状态
export function changeTaskStatus(data) {
  let { taskId, status } = data
  return request({
    url: url + `/monitor/warning/task/changeStatus/${taskId}/${status}`,
    method: 'put',
  })
}

// 监测客户列表查询接口
export function monitorTargetList(data) {
  return request({
    url: url + `/monitor/warning/target/list`,
    method: 'post',
    data
  })
}

// 监测客户取消、批量取消接口
export function batchCancel(data) {
  return request({
    url: url + `/monitor/warning/target/batchCancel`,
    method: 'put',
    data
  })
}

//监测客户添加
export function addMonitor(data) {
  return request({
    url: url + `/monitor/warning/target/save`,
    method: 'post',
    data
  })
}

// 批量添加
export function batchAdd(data) {
  return request({
    url: url + `/monitor/warning/target/batchAdd`,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data
  })
}


// 监测动态
export function recordList(data) {
  return request({
    url: url + `/monitor/warning/record/list`,
    method: 'post',
    data,
  })
}

// 风险类别详情
export function riskTypeDetail(id) {
  return request({
    url: url + `/monitor/warning/risk-type/detail/${id}`,
    method: 'get',
  })
}