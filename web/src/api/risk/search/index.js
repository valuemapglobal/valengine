/*
 * @Author: detectiveBoy
 * @Date: 2022-04-24 17:27:45
 * @LastEditors: Do not edit
 * @LastEditTime: 2023-03-07 09:59:41
 * @FilePath: \vmkj\src\api\risk\search\index.js
 * @Description:
 *
 * Copyright (c) 2022 by 用户/公司名, All Rights Reserved.
 */
import request from '@/utils/request'

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

// 字典
// 获取行业、成立时间列表
export function getWorkflow(params) {
  return request({
    url: url + `/risk/companyData/getWorkflow`,
    method: 'post',
    params,
  })
}

// 获取行业、成立时间列表
export function detailsInfo(info) {
  return request({
    url: url + `/risk/companyData/detailsInfo/${info.type}`,
    method: 'post',
    data: info,
  })
}

// 获取行业、成立时间列表
export function getIndustry() {
  return request({
    url: url + '/ruoyi-system/industry/groupList',
    method: 'get',
  })
}

export function getIndustryList(data) {
  return request({
    url: url + `/ruoyi-system/dict/data/type/${data}`,
    method: 'get',
  })
}

// 用户关注
export function addCares(data) {
  return request({
    url: url + '/risk/riskCompanyFollow/insertCompany',
    method: 'post',
    data,
  })
}

// 取消用户
export function delCares(data) {
  return request({
    url: url + '/risk/riskCompanyFollow/delCompany',
    method: 'post',
    data,
  })
}

// 企业风险检索
export function RiskSearch(data) {
  return request({
    url: url + '/risk/riskCompany/companyRiskSearch',
    method: 'post',
    data,
    // multipleCancel: true,
  })
}

// 品牌搜索
export function brandSearch(data) {
  return request({
    url: url + '/monitoring/popularFeelings/queryBrand',
    method: 'post',
    data,
  })
}

// 品牌舆情
export function getBrandNews(data) {
  return request({
    url: url + '/monitoring/popularFeelings/brandSentiment',
    method: 'post',
    data,
  })
}

// 企业风险洞察
export function RiskIns(data) {
  return request({
    url: url + '/risk/companyData/getCompanyRiskIns',
    method: 'post',
    data,
  })
}

//推荐企业
//保存调查问卷
export function saveSurvey(data) {
  return request({
    url: url + '/risk/riskCompanyQnaire/submitQnaire',
    method: 'post',
    data,
  })
}

//获取推荐企业列表
export function getRecommendCompany(params) {
  return request({
    url: url + '/risk/riskCompanyQnaire/getRecommendCompany',
    method: 'get',
    params,
  })
}

//企业尽调
//添加尽调企业
export function addInvestigateCompany(data) {
  return request({
    url: url + '/risk/rickInvestigate/addInvestigate',
    method: 'post',
    data: {
      ...data,
      logoCname: JSON.parse(localStorage.getItem('userInfo')).companyName,
      logoShortName: JSON.parse(localStorage.getItem('userInfo'))
        .companyShortName,
      // logoCname: process.env.VUE_APP_logoCname,
      // logoShortName: process.env.VUE_APP_logoShortName,
    },
  })
}

//取消尽调企业
export function cancelInvestigateCompany(data) {
  return request({
    url: url + '/risk/rickInvestigate/cancelInvestigate',
    method: 'post',
    data,
  })
}

// 提交用户反馈问题
export function submitFeedback(data) {
  return request({
    url: url + '/risk/msg/helpStrategy',
    method: 'post',
    data,
  })
}

// 企业相关
// 基本信息
export function getEnterpriseList(data) {
  return request({
    url: url + '/risk/companyData/getCompanyBasicInfo',
    method: 'post',
    data,
  })
}
// 企业主副行业获取
export function getClassificationIndustry(data) {
  return request({
    url: url + '/risk/companyData/getClassificationIndustry',
    method: 'post',
    data,
  })
}

//获取工商信息
export function getGongShang(data) {
  return request({
    url: url + '/risk/companyData/getBaseInfo',
    method: 'post',
    data,
  })
}

//获取管理人员信息
export function getManager(data) {
  return request({
    url: url + '/risk/companyData/getPersonnelInfo',
    method: 'post',
    data,
  })
}

//获取机构投资信息
export function getInvestment(data) {
  return request({
    url: url + '/risk/companyData/getCapitalInfo',
    method: 'post',
    data,
  })
}

export function getCompanyList(data) {
  return request({
    url: url + '/vm-risk-value/riskCompanyFollow/getCompanyFollowList',
    method: 'get',
    params: data,
  })
}

// 经营环境
export function getSurroundingsList(data) {
  return request({
    url: url + '/vm-risk-value/companyData/getCompanyBusEnv',
    method: 'post',
    data,
  })
}

// 经营情况
export function getConditionList(data) {
  return request({
    url: url + '/vm-risk-value/companyData/getCompanyBusSit',
    method: 'post',
    data,
  })
}

// 发展潜力
export function getPotentialList(data) {
  return request({
    url: url + '/vm-risk-value/companyData/getCompanyDevDot',
    method: 'post',
    data,
  })
}

// 合规信息
export function getComplianceList(data) {
  return request({
    url: url + '/vm-risk-value/companyData/getCompanyComInfo',
    method: 'post',
    data,
  })
}

//获取企业详情
export function getCompanyDetail(params) {
  return request({
    url: url + '/risk/riskCompany/getCompanyDetail',
    method: 'get',
    params,
  })
}

//搜索记录保存
export function saveSearchRecord(data) {
  return request({
    url: url + '/risk/tagHistory/save',
    method: 'post',
    data,
  })
}

//记录搜索历史
export function getSearchRecord() {
  return request({
    url: url + '/risk/tagHistory/query',
    method: 'get',
  })
}

//搜索记录删除
export function delSearchRecord() {
  return request({
    url: url + '/risk/tagHistory/delete',
    method: 'get',
  })
}

//企业选择记录保存
export function saveCompanyRecord(data) {
  return request({
    url: url + '/risk/chooseHistory/save',
    method: 'post',
    data,
  })
}

//企业选择记录
export function getCompanySelectRecord() {
  return request({
    url: url + '/risk/chooseHistory/query',
    method: 'get',
  })
}

//企业选择记录删除
export function delCompanyRecord() {
  return request({
    url: url + '/risk/chooseHistory/delete',
    method: 'get',
  })
}

//查询舆情新鲜事-舆情列表和用户查看具体舆情新鲜
export function getNews(data) {
  return request({
    url: url + '/monitoring/popularFeelings/news',
    method: 'post',
    data,
  })
}

//判断品牌无详情
export function brandDetailJudge(data) {
  return request({
    url: url + '/monitoring/popularFeelings/brandDetailJudge',
    method: 'post',
    data,
  })
}

//品牌无详情
export function brandDetail(data) {
  return request({
    url: url + '/monitoring/popularFeelings/brandDetail',
    method: 'post',
    data,
  })
}

// 品牌搜索记录保存‘
export function saveBrandRecord(data) {
  return request({
    url: url + '/monitoring/tagHistory/save',
    method: 'post',
    data,
  })
}

// 品牌搜索记录查询
export function getSearchBrandRecord() {
  return request({
    url: url + '/monitoring/tagHistory/query',
    method: 'get',
  })
}

// 品牌搜索记录删除
export function delBrandSearchRecord() {
  return request({
    url: url + '/monitoring//tagHistory/delete',
    method: 'get',
  })
}

// 保存所选品牌记录
export function saveSelectBrandRecord(data) {
  return request({
    url: url + '/monitoring/chooseHistory/save',
    method: 'post',
    data,
  })
}

export function companySentiment(data) {
  return request({
    url: url + '/monitoring/popularFeelings/companySentiment',
    method: 'post',
    data,
  })
}

// 获取所选品牌记录
export function getSelectBrandRecord() {
  return request({
    url: url + '/monitoring/chooseHistory/query',
    method: 'get',
  })
}

// 删除所选品牌记录
export function delSelectBrandRecord() {
  return request({
    url: url + '/monitoring/chooseHistory/delete',
    method: 'get',
  })
}
//获取股东信息表
export function shareholdersInformation(data) {
  return request({
    url: url + '/risk/riskCompanyRelaChart/getholdersAndInvestment',
    method: 'post',
    data,
  })
}
//获取股东信息
export function getHolders(data) {
  return request({
    url: url + '/risk/riskCompanyRelaChart/getHolders',
    method: 'post',
    data,
  })
}
//获取对外投资信息
export function getInvestments(data) {
  return request({
    url: url + '/risk/riskCompanyRelaChart/getInvestments',
    method: 'post',
    data,
  })
}
// 获取公司详情
export function toGetCompanyDetails(data) {
  return request({
    url: url + '/risk/riskCompanyRelaChart/getCompanyInfo',
    method: 'post',
    data,
  })
}

/**
 * 刷新公司详情信息
 * @param data
 * @returns {*}
 */
export function updateCompanyDetail(data) {
  return request({
    url: url + '/risk/riskCompany/updateCompanyDetail',
    method: 'post',
    data,
  })
}
// 股东信息-最新公式
export function holderList(data) {
  return request({
    url: url + '/risk/highQualityRecommend/holderList',
    method: 'post',
    data,
  })
}
