import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'

const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

export function selectList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/list`,
    method: 'post',
    data,
  })
}

//产品决策
export function selectNewList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/newList`,
    method: 'post',
    data,
  })
}

//测试模型
export function checkMode(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/checkRule/${data}`,
    method: 'get',
  })
}

//启动模型
export function runModelRule(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/runRule/${data}`,
    method: 'get',
  })
}

//关闭模型（暂定）
export function modelSubmit(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/submit`,
    method: 'post',
    data,
  })
}

//删除模型
export function delModel(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/delete/${data}`,
    method: 'get',
  })
}

//获取模型详情决策组表格数据
export function searchGroupList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/group/list`,
    method: 'post',
    data,
  })
}

//产品决策
export function searchGroupNewList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/group/newList`,
    method: 'post',
    data,
  })
}
//决策组编辑获取信息
export function getGroupById(id) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/group/${id}`,
    method: 'get',
  })
}
//决策组新增或编辑提交
export function editGroupSubmit(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/group/submit`,
    method: 'post',
    data,
  })
}
//决策组删除
export function delGroupSubmit(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/group/delete/${data}`,
    method: 'get',
  })
}

//获取当前模型分析对象
export function searchObjectList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/boject/list`,
    method: 'post',
    data,
  })
}
//提交模型分析对象
export function submitObjectList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/boject/submitList`,
    method: 'post',
    data,
  })
}

//获取模型对象
export function searchObjectById(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/boject/getByModelId`,
    method: 'post',
    data,
  })
}
//获取全部的分析对象
export function searchThemeList(data) {
  return request({
    url: url + `/rdenew/variable/theme/list`,
    method: 'post',
    data,
  })
}

//获取决策code表格数据
export function searchCodeList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/record/list`,
    method: 'post',
    data,
  })
}
//产品决策
export function searchCodeNewList(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/record/newList`,
    method: 'post',
    data,
  })
}

//选择规则code提交
export function searchSelectSubmit(data) {
  return request({
    // url: url + `/rdenew/model/antiFraud/rule/record/submit`,
    url: url + `/rdenew/model/antiFraud/rule/record/submitInsert`,
    method: 'post',
    data,
  })
}
//选择规则code删除
export function delCode(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/rule/record/delete/${data}`,
    method: 'get',
  })
}

export function submitModel(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/submit`,
    method: 'post',
    data,
  })
}
export function getById(id) {
  return request({
    url: url + `/rdenew/model/antiFraud/${id}`,
    method: 'get',
  })
}

//获取选择规则code的左侧表格
export function searchSelectCodeList(data) {
  return request({
    url: url + `/rdenew/model/decisionCodeLevel/list`,
    method: 'post',
    data,
  })
}

//新增code提交
export function addCodeSubmit(data) {
  return request({
    url: url + `/rdenew/model/decisionCodeLevel/submit`,
    method: 'post',
    data,
  })
}

//获取添加code规则的数据
export function searchAddCode(data) {
  return request({
    url: url + `/rdenew/model/decisionCodeLevel/getById`,
    method: 'post',
    data,
  })
}

//rdenew/variable/theme
//获取添加code 决策条件
export function getSelectListTree(data) {
  return request({
    url: url1 + `/smartData/integration/treeList`,
    method: 'get',
    params: data,
  })
}

//策略/规则组/规则 列表启用
export function submitAntiProjectFraud(data) {
  return request({
    url: url + `/rdenew/model/antiProjectFraud/submit`,
    method: 'post',
    data,
  })
}
//策略/规则组/规则 列表关闭
export function delAntiProjectFraud(data) {
  return request({
    url: url + `/rdenew/model/antiProjectFraud/delete`,
    method: 'post',
    data,
  })
}

export function delTactics(data) {
  return request({
    // url: url + `/rdenew/model/antiFraud/delete/${id}`,
    url: url + '/rdenew/model/antiFraud/delete',
    method: 'post',
    data,
  })
}
export function delRuleGroup(data) {
  return request({
    url: url + '/rdenew/model/antiFraud/rule/group/delete',
    method: 'post',
    data,
  })
}

/**
 * 规则模型&分类模型--规则删除
 * @param data
 * @returns {*}
 */
export function delRuleRecord(data) {
  return request({
    url: url + '/rdenew/model/decisionCodeLevel/logicDelete',
    method: 'post',
    data,
  })
}

//校验code是否重复
export function checkCode(data) {
  return request({
    url: url + `/rdenew/model/decisionCodeLevel/checkCode`,
    method: 'post',
    data,
  })
}

// 规则-更新版本
export function versionControlFraud(data) {
  return request({
    url: url + `/rde/model/antiFraud/version/versionControlFraud`,
    method: 'post',
    data,
  })
}
// 规则-保留原版本
export function versionReserveFraud(data) {
  return request({
    url: url + `/rde/model/antiFraud/version/versionReserveFraud`,
    method: 'post',
    data,
  })
}
// 规则-列表-模型版本三标识

// export function getVersion(data) {
//   return request({
//     url: 'http://192.168.1.126:7091'  + `/model-version-classification/getVersion`,
//     method: 'post',
//     data,
//   })
// }
export function getVersion(data) {
  return request({
    url: url + `/model-version-classification/getVersion`,
    method: 'post',
    data,
  })
}
// A/b 测试列表
export function getTaskListByModelId(data) {
  return request({
    url: url + `/ab-test/getTaskListByModelId`,
    method: 'post',
    data,
  })
}
//点击模型回溯
export function submitAB(data) {
  return request({
    url: url + `/ab-test/submit`,
    method: 'post',
    data,
  })
}
// 修改该条任务记录的客户是否为坏客户
export function updateCustomFlag(data) {
  return request({
    url: url + `/ab-test/updateCustomFlag`,
    method: 'post',
    data,
  })
}
//上传回溯列表数据
// export function uploadAB(data) {
//   return request({
//     url: url  + `/ab-test/upload`,
//     method: 'post',
//     data,
//   })
// }
export function uploadAB() {
  return url + `/ab-test/upload`
}
// 导出回溯列表数据
export function exportAB(data) {
  return request({
    url: url + `/ab-test/export`,
    method: 'post',
    responseType: 'blob',
    data,
  })
}
// 查询历史最高版本
export function queryLastVersion(data) {
  return request({
    url: url + `/ab-test/queryLastVersion`,
    method: 'get',
    params: data,
  })
}
// 规则模型名称校验
export function checkName(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/checkName`,
    method: 'post',
    data,
  })
}

/**
 * 规则模型&分类模型复用-自建策略列表
 * @param data
 * @returns {*}
 */
export function selfBuiltStrategyList(data) {
  return request({
    url: `${url}/rdenew/model/antiFraud/newBuildList`,
    method: 'post',
    data,
  })
}

/**
 * 规则模型&分类模型复用-标准策略列表
 * @param data
 * @returns {*}
 */
export function standardBuiltStrategyList(data) {
  return request({
    url: `${url}/rdenew/model/antiFraud/standardList`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.4 规则模型&分类模型-策略复用
 * @param data
 * @returns {*}
 */
export function addStandardModel(data) {
  return request({
    url: `${url}/rule-record-reuse/addStandardModel`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.11 查询已复用的标准策略
 * @param data
 * @returns {*}
 */
export function selectPolicyGroup(data) {
  return request({
    url: `${url}/rule-record-reuse/selectPolicyGroup`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.5 复用-自建规则组列表
 * @param data
 * @returns {*}
 */
export function selfBuiltRuleGroupList(data) {
  return request({
    url: `${url}/rdenew/model/antiFraud/rule/group/newBuildList`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.6 复用-标准规则组列表
 * @param data
 * @returns {*}
 */
export function standardBuiltRuleGroupList(data) {
  return request({
    url: `${url}/rdenew/model/antiFraud/rule/group/standardList`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.7 复用-规则组复用
 * @param data
 * @returns {*}
 */
export function addStandardRuleGroup(data) {
  return request({
    url: `${url}/rule-record-reuse/addStandardRuleGroup`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.8 复用-自建规则列表
 * @param data
 * @returns {*}
 */
export function selfBuiltRuleList(data) {
  return request({
    url: `${url}/rdenew/model/antiFraud/rule/record/newBuildList`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.9 复用-标准规则列表
 * @param data
 * @returns {*}
 */
export function standardBuiltRuleList(data) {
  return request({
    url: `${url}/rdenew/model/antiFraud/rule/record/standardList`,
    method: 'post',
    data,
  })
}

/**
 * 2.8.10 规则复用
 * @param data
 * @returns {*}
 */
export function addStandardRuleRecord(data) {
  return request({
    url: `${url}/rule-record-reuse/addStandardRuleRecord`,
    method: 'post',
    data,
  })
}

/**
 * 规则模型和分类模型-复用的标准数据删除
 * @param data
 * @returns {*}
 */
export function deletePolicyGroup(data) {
  return request({
    url: `${url}/rule-record-reuse/deletePolicyGroup`,
    method: 'post',
    data,
  })
}

/**
 * 规则策略导出-json文件
 * @param params
 * @returns {*}
 */
export function exportRuleJSON(id) {
  return request({
    url: `${url}/decision-manage/export-rule/json?strategyId=${id}`,
    method: 'get',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
/**
 * 导入策略
 * @param data
 * @returns {*}
 */
export function importRuleJSON(data) {
  return request({
    url: `${url}/decision-manage/import-rule/json`,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    data,
  })
}
