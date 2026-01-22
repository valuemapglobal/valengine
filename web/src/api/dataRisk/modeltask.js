import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/vm/afterLoan" : "/dev-api/vm/afterLoan";

//获取列表数据
export function selectList(data) {
  return request({
    url: url + `/plm/warningTasks/list`,
    method: 'post',
    data
  })
}

//提交
export function submitForm(data) {
  return request({
    url: url + `/plm/warningTasks/submit`,
    method: 'post',
    data
  })
}
//删除
export function delForm(data) {
  return request({
    url: url + `/plm/warningTasks/delete`,
    method: 'post',
    data
  })
}
export function stop(data) {
  return request({
    url: url + `/plm/task/job/stop`,
    method: 'post',
    data
  })
}
export function start(data) {
  return request({
    url: url + `/plm/task/job/start`,
    method: 'post',
    data
  })
}

//预警任务

//获取企业信息
export function searchEnterprisetList(data) {
  return request({
    url: url + `/plm/enterpriseBaseInfo/list`,
    method: 'post',
    data
  })
}
//获取已选择的企业信息
export function searchSelectEnterprisetList(data) {
  return request({
    url: url + `/plm/warningTasksCustomer/list`,
    method: 'post',
    data
  })
}

//获取已选择的模板列表
export function searchSelectModelList(data) {
  return request({
    url: url + `/plm/warningTasksRule/list`,
    method: 'post',
    data
  })
}


//获取模型数据
export function searchModelList(data) {
  return request({
    url: url + `/rde/model/antiFraud/list`,
    method: 'post',
    data
  })
}

export function searchTasksRuleList(data) {
  return request({
    url: url + `/plm/warningTasksRule/list`,
    method: 'post',
    data
  })
}
