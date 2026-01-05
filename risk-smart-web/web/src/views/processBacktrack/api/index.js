import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'

export function historicalList(data) {
  return request({
    url: `${url}/model/traceback-tasks/historical-list`,
    method: 'post',
    data,
  })
}
export function tracebackTasks(data) {
  return request({
    url: `${url}/model/traceback-tasks`,
    method: 'post',
    data,
  })
}

// 回溯任务列表
export function tracebackTasksList(data) {
  return request({
    url: `${url}/model/traceback-tasks/list`,
    method: 'post',
    data,
  })
}
export function tracebackTasksDetail(tracebackNo) {
  return request({
    url: `${url}/model/traceback-tasks/${tracebackNo}`,
    method: 'get',
  })
}
