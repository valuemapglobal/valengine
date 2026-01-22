import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/vm/afterLoan" : "/dev-api/vm/afterLoan";

export function selectList(data) {
  return request({
    url: url + `/rde/model/antiFraud/list`,
    method: 'post',
    data
  })
}

//产品决策
export function selectNewList(data) {
  return request({
    url: url + `/rde/model/antiFraud/newList`,
    method: 'post',
    data
  })
}

//测试模型
export function checkMode(data) {
  return request({
    url: url + `/rde/model/antiFraud/checkRule/${data}`,
    method: 'get',
  })
}

//启动模型
export function runModelRule(data) {
  return request({
    url: url + `/rde/model/antiFraud/runRule/${data}`,
    method: 'get',
  })
}

//关闭模型（暂定）
export function modelSubmit(data) {
  return request({
    url: url + `/rde/model/antiFraud/submit`,
    method: 'post',
    data
  })
}

//删除模型
export function delModel(data) {
  return request({
    url: url + `/rde/model/antiFraud/delete/${data}`,
    method: 'get'
  })
}

//获取模型详情决策组表格数据
export function searchGroupList(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/group/list`,
    method: 'post',
    data
  })
}

//产品决策
export function searchGroupNewList(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/group/newList`,
    method: 'post',
    data
  })
}
//决策组编辑获取信息
export function getGroupById(id) {
  return request({
    url: url + `/rde/model/antiFraud/rule/group/${id}`,
    method: 'get',
  })
}
//决策组新增或编辑提交
export function editGroupSubmit(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/group/submit`,
    method: 'post',
    data
  })
}
//决策组删除
export function delGroupSubmit(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/group/delete/${data}`,
    method: 'get',
  })
}

//获取当前模型分析对象
export function searchObjectList(data) {
  return request({
    url: url + `/rde/model/antiFraud/boject/list`,
    method: 'post',
    data
  })
}
//提交模型分析对象
export function submitObjectList(data) {
  return request({
    url: url + `/rde/model/antiFraud/boject/submitList`,
    method: 'post',
    data
  })
}

//获取模型对象
export function searchObjectById(data) {
  return request({
    url: url + `/rde/model/antiFraud/boject/getByModelId`,
    method: 'post',
    data
  })
}
//获取全部的分析对象
export function searchThemeList(data) {
  return request({
    url: url + `/rde/variable/theme/list`,
    method: 'post',
    data
  })
}

//获取决策code表格数据
export function searchCodeList(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/record/list`,
    method: 'post',
    data
  })
}
//产品决策
export function searchCodeNewList(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/record/newList`,
    method: 'post',
    data
  })
}

//选择规则code提交
export function searchSelectSubmit(data) {
  return request({
    // url: url + `/rde/model/antiFraud/rule/record/submit`,
    url: url + `/rde/model/antiFraud/rule/record/submitInsert`,
    method: 'post',
    data
  })
}
//选择规则code删除
export function delCode(data) {
  return request({
    url: url + `/rde/model/antiFraud/rule/record/delete/${data}`,
    method: 'get'
  })
}

export function submitModel(data) {
  return request({
    url: url + `/rde/model/antiFraud/submit`,
    method: 'post',
    data
  })
}
export function getById(id) {
  return request({
    url: url + `/rde/model/antiFraud/${id}`,
    method: 'get'
  })
}


//获取选择规则code的左侧表格
export function searchSelectCodeList(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/list`,
    method: 'post',
    data
  })
}



//新增code提交
export function addCodeSubmit(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/submit`,
    method: 'post',
    data
  })
}

//获取添加code规则的数据
export function searchAddCode(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/getById`,
    method: 'post',
    data
  })
}

//rde/variable/theme
//获取添加code 决策条件
export function getSelectListTree(data) {
  return request({
    url: url + `/rde/variable/theme/list/tree`,
    method: 'post',
    data
  })
}

//策略/规则组/规则 列表启用
export function submitAntiProjectFraud(data) {
  return request({
    url: url + `/rde/model/antiProjectFraud/submit`,
    method: 'post',
    data
  })
}
//策略/规则组/规则 列表关闭
export function delAntiProjectFraud(data) {
  return request({
    url: url + `/rde/model/antiProjectFraud/delete`,
    method: 'post',
    data
  })
}


export function delTactics(id) {
  return request({
    url: url + `/rde/model/antiFraud/delete/${id}`,
    method: 'get',
  })
}
export function delRuleGroup(id) {
  return request({
    url: url + `/rde/model/antiFraud/rule/group/delete/${id}`,
    method: 'get',
  })
}
export function delRuleRecord(id) {
  return request({
    url: url + `/rde/model/antiFraud/rule/record/delete/${id}`,
    method: 'get',
  })
}

//校验code是否重复
export function checkCode(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/checkCode`,
    method: 'post',
    data
  })
}