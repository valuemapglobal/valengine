import request from '@/utils/request'
import req from './request'
const url = process.env.VUE_APP_ENV === 'production' ? '' : '/api'
const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

//拓客引导-搜索金融机构
export function selectInstitution(data) {
  return request({
    url: url1 + `/financing/tokerGuidance/selectInstitution`,
    method: 'post',
    data,
  })
}
//拓客引导-根据金融机构查询金融产品
export function selectInstitutionProduct(data) {
  return request({
    url: url1 + `/financing/tokerGuidance/selectInstitutionProduct`,
    method: 'post',
    data,
  })
}
export function getInstitutionalName() {
  return request({
    url: url1 + `/financing/productInstitutionalProduct/getInstitutionalName`,
    method: 'get',
  })
}
export function addYDProduct(data) {
  return request({
    url: url1 + `/financing/product/addYDProduct`,
    method: 'post',
    data,
  })
}

//保存用户选择偏好
export function saveSysUserChange(data) {
  return request({
    url: url1 + `/system/sysFunctionUser/save`,
    method: 'post',
    data,
  })
}
//获取偏好
export function getSysUserPreference(params) {
  return request({
    url: url1 + `/system/sysFunction/list`,
    method: 'get',
    params,
  })
}

export function saveUserGuidePage(data) {
  return request({
    url: url1 + `/system/sysPreference/saveUserGuidePage`,
    method: 'post',
    data,
  })
}

// 获取高德地图行政区域查询
export function getdistrict() {
  return req({
    url: url + '/v3/config/district?parameters',
    method: 'get',
    params: {
      key: '21b297274a35b2dabdd1d7c933179761',
      keyWords: '中国',
      subdistrict: 3, // 要获取的行政区划的级别：省、市、县三级
    },
  })
}
// 行研分析引导
export function researchGuidance(data) {
  return req({
    url: url1 + '/risk/conductResearch/researchGuidance',
    method: 'post',
    data,
  })
}
// 监测引导
export function guide() {
  return req({
    url: url1 + '/monitoring/monitoringKycUser/guide',
    method: 'post',
  })
}

/**
 * 监测引导状态
 * @returns {*}
 */
export function guideStatus() {
  return req({
    url: url1 + '/monitoring/monitoringKycUser/guideStatus',
    method: 'get',
  })
}
// 企业风险检索
export function RiskSearch(data) {
  return req({
    url: url1 + '/risk/riskCompany/companyRiskSearch',
    method: 'post',
    data,
  })
}
// 尽调引导

export function dueDiligencePreferenceSelection(data) {
  return req({
    url: url1 + '/risk/kyc/dueDiligencePreferenceSelection',
    method: 'post',
    data,
  })
}
//添加尽调企业
export function addInvestigateCompany(data) {
  return req({
    url: url1 + '/risk/rickInvestigate/addInvestigate',
    method: 'post',
    data: {
      ...data,
    },
  })
}
