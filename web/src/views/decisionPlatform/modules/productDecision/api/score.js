import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'
const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

export function getScoreList(data) {
  return request({
    url: url + `/score-card-record/list`,
    method: 'post',
    data,
  })
}

//评分卡发布
export function releaseScore(data) {
  return request({
    url: url + `/score-card-record/release`,
    method: 'post',
    data,
  })
}

//评分卡新增
export function increaseScore(data) {
  return request({
    url: url + `/score-card-record/submit`,
    method: 'post',
    data,
  })
}

//评分卡编辑
export function updateScore(data) {
  return request({
    url: url + `/score-card-record/update`,
    method: 'post',
    data,
  })
}

//评分卡状态编辑
export function updateScoreStatus(data) {
  return request({
    url: url + `/score-card-record/updateStatus`,
    method: 'post',
    data,
  })
}

//评分卡删除
export function deleteScore(data) {
  return request({
    url: url + `/score-card-record/delete`,
    method: 'post',
    data,
  })
}

//评分卡名称同名判断
export function checkScoreName(data) {
  return request({
    url: url + `/score-card-record/checkName`,
    method: 'post',
    data,
  })
}

//指标卡------------------------------------------------------------------------------
//指标卡列表
export function getIndicatorsList(data) {
  return request({
    url: url + `/score-primary-index/list`,
    method: 'post',
    data,
  })
}

//指标卡新增
export function increaseIndicators(data) {
  return request({
    url: url + `/score-primary-index/submit`,
    method: 'post',
    data,
  })
}

//指标卡修改
export function updateIndicators(data) {
  return request({
    url: url + `/score-primary-index/update`,
    method: 'post',
    data,
  })
}

export function updateStatusIndicators(data) {
  return request({
    url: url + `/score-primary-index/updateStatus`,
    method: 'post',
    data,
  })
}

//指标名称同名判断
export function checkIndicatorsName(data) {
  return request({
    url: url + `/score-primary-index/checkName`,
    method: 'post',
    data,
  })
}

//指标卡删除
export function deleteIndicatorsCard(data) {
  return request({
    url: url + `/score-primary-index/deleteCard`,
    method: 'post',
    data,
  })
}

//单个指标卡删除
export function deleteIndicators(data) {
  return request({
    url: url + `/score-primary-index/delete`,
    method: 'post',
    data,
  })
}

//规则 --------------------------------------------------------------------------------

// 查询规则列表
export function getRuleList(data) {
  return request({
    url: url + `/score-index-rule/list`,
    method: 'post',
    data,
  })
}

// 规则code唯一
export function checkRuleCode(data) {
  return request({
    url: url + `/score-index-rule/checkCode`,
    method: 'post',
    data,
  })
}

//查找单个规则
export function getRuleById(data) {
  return request({
    url: url + `/score-index-rule/getById`,
    method: 'post',
    data,
  })
}

//新增规则
export function increaseRule(data) {
  return request({
    url: url + `/score-index-rule/submit`,
    method: 'post',
    data,
  })
}

//编辑规则
export function updateRule(data) {
  return request({
    url: url + `/score-index-rule/update`,
    method: 'post',
    data,
  })
}

//单个规则删除
export function deleteRules(data) {
  return request({
    url: url + `/score-index-rule/delete`,
    method: 'post',
    data,
  })
}

export function updateIncreaseStatus(data) {
  return request({
    url: url + `/score-index-rule/updateStatus`,
    method: 'post',
    data,
  })
}

//规则排序
export function setRuleSort(data) {
  return request({
    url: url + `/score-index-rule/setSort`,
    method: 'post',
    data,
  })
}

//新增规则 -> 规则筛选
export function getSelectListTree(data) {
  return request({
    url: url1 + `/smartData/integration/treeList`,
    method: 'get',
    params: data,
  })
}

//评分模型-更新版本
export function versionControlFraud(data) {
  return request({
    url: url + `/score-card-record-version/versionControlFraud`,
    method: 'post',
    data,
  })
}

//评分模型-保留版本
export function versionReserveScore(data) {
  return request({
    url: url + `/score-card-record-version/versionReserveScore`,
    method: 'post',
    data,
  })
}

/**
 * 2.7.2 自建评分列表
 * @param data
 * @returns {*}
 */
export function selfBuiltScoreList(data) {
  return request({
    url: `${url}/score-card-record/newBuildList`,
    method: 'post',
    data,
  })
}

/**
 * 2.7.3 评分卡模型复用-标准产品下拉框
 * @returns {*}
 */
export function standardProductList() {
  return request({
    url: `${url}/product/_standard`,
    method: 'get',
  })
}

/**
 * 2.7.3 复用-标准评分列表
 * @param data
 * @returns {*}
 */
export function standardBuiltScoreList(data) {
  return request({
    url: `${url}/score-card-record/standardList`,
    method: 'post',
    data,
  })
}

/**
 * 2.7.7 查询已复用的标准评分卡
 * @param data
 * @returns {*}
 */
export function selectParentCard(data) {
  return request({
    url: `${url}/score-card-reuse/selectParentCard`,
    method: 'post',
    data,
  })
}

/**
 * 2.7.5 评分卡复用
 * @param data
 * @returns {*}
 */
export function addStandardModel(data) {
  return request({
    url: `${url}/score-card-reuse/addStandardModel`,
    method: 'post',
    data,
  })
}

/**
 * 2.7.6 复用的标准评分卡删除
 * @param data
 * @returns {*}
 */
export function deleteParentCard(data) {
  return request({
    url: `${url}/score-card-reuse/deleteParentCard`,
    method: 'post',
    data,
  })
}
