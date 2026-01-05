/**
 * @author chris
 * @date 2023/5/4 17:36
 */
import request from '@/utils/request'
const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

/**
 * 融资列表查询接口
 * @param data
 * @returns {*}
 */
export function getFinancingList(data) {
  return request({
    url: `${url}/financing/financingInformation/selectAll`,
    method: 'post',
    data,
  })
}

/**
 * 获取产品选项数据
 * @returns {*}
 */
export function getProductList() {
  return request({
    url: `${url}/financing/financingInformation/selectProductGroup`,
    method: 'get',
  })
}

/**
 * 获取产品经理选项数据
 * @returns {*}
 */
export function getManagerList() {
  return request({
    url: `${url}/financing/financingInformation/selectAccountManagerGroup`,
    method: 'get',
  })
}

/**
 * 获取融资详情信息
 * @returns {*}
 */
export function getFinancingInfo(informationNo) {
  return request({
    url: `${url}/financing/financingInformation/getFinancingInfo?informationNo=${informationNo}`,
    method: 'get',
  })
}

/**
 * 融资审核、放款接口
 * status: 1审核通过、2审核不通过、3确认放款
 * @returns {*}
 */
export function setFinancingOpera(data) {
  return request({
    url: `${url}/financing/financingInformation/financingOpera`,
    method: 'post',
    data,
  })
}
/**
 * 线上放款
 */
export function setOnlineLoan(data) {
  return request({
    url: `${url}/financing/financingInformation/onlineLoan`,
    method: 'post',
    data,
  })
}

/**
 * 还款计划表
 * appId: 贷后记录id
 * @returns {*}
 */
export function getSelectRepayment(params) {
  return request({
    url: `${url}/financing/vmFinancingRepayments/repaymentScheduleLoan`,
    method: 'get',
    params,
  })
}

/**
 * 获取新注册用户分组
 * @param data
 * @returns {*}
 */
export function getSelectNewUser(data) {
  return request({
    url: `${url}/financing/financingInformation/selectNewUser`,
    method: 'post',
    data,
  })
}

/**
 * 融资详情导出
 * @param params
 * @returns {*}
 */
export function exportFinancingInformation(params) {
  return request({
    url: `${url}/financing/financingInformation/exportFinancingInformation`,
    method: 'get',
    responseType: 'blob',
    params,
  })
}

export function getProductCount(data) {
  return request({
    url: `${url}/financing/product/getProductCount`,
    method: 'post',
    data
  })
}

/**
 * 2.3.34 pc-更改授信&贷后客户经理接口
 * @param data
 * @returns {*}
 */
export function updateFinancingCreditReviewPersonnelId(data) {
  return request({
    url:`${url}/financing/financingCredit/updateFinancingCreditReviewPersonnelId`,
    method: 'post',
    data
  })
}

/**
 * 11. 任务客户信息-根据报告编号返回客户编号接口
 * @param params
 * @returns {*}
 */
export function getCustomerNoByReportNo(params) {
  return request({
    url:`${url}/risk/batchReportCustomerAiPerson/getCustomerInfoByReportNo`,
    method: 'get',
    params
  })
}
