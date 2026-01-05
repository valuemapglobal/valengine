/**
 * @author chris
 * @date 2023/5/12 14:23
 */
import request from "@/utils/request";

const url = process.env.VUE_APP_ENV === "production" ? "/vm" : "/dev-api/vm";

/**
 * 贷后管理列表接口（废弃）
 * @param data
 * @returns {*}
 */
export function getLoanAfterList(data) {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/selectAll`,
		method: 'post',
		data
	})
}

/**
 * 贷后管理列表接口
 * @param data
 * @returns {*}
 */
export function getLoanAfterList2(data) {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/selectAll2`,
		method: 'post',
		data
	})
}

/**
 * 贷后管理-获取客户经理列表
 * @returns {*}
 */
export function selectAccountManagerFilter() {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/selectAccountManagerFilter`,
		method: "get"
	})
}

/**
 * 贷后管理-还款期限筛选条件接口
 * @returns {*}
 */
export function repaymentTermFilter() {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/repaymentTermFilter`,
		method: "get"
	})
}

/**
 * 贷后管理-还款计划表
 * @param params
 * @returns {*}
 */
export function repaymentScheduleLoan(params) {
	return request({
		url: `${url}/financing/vmFinancingRepayments/repaymentScheduleLoan`,
		method: "get",
		params
	})
}

/**
 * 贷后管理-逾期还款&&手动还款
 * @param data
 * @returns {*}
 */
export function operate(data) {
	return request({
		url: `${url}/financing/vmFinancingRepayments/operate`,
		method: "post",
		data
	})
}

/**
 * 逾期还款详情接口
 * @param data
 * @returns {*}
 */
export function overdueRepaymentDetails(data) {
	return request({
		url:`${url}/financing/vmFinancingRepayments/overdueRepaymentDetails`,
		method: "post",
		data
	})
}
/**
 * 提前还款-获取详情接口
 * @param data
 * @returns {*}
 */
export function detailsOfPrepaymentApproval(data) {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/detailsOfPrepaymentApproval`,
		method: "post",
		data
	})
}

/**
 * 提前还款-按钮点击（通过、拒绝）
 * @param data
 * @returns {*}
 */
export function prepaymentReviewSubmit(data) {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/prepaymentReviewSubmit`,
		method: "post",
		data
	})
}

/**
 * 2.3.43 pc-更改贷后客户经理接口
 * @param data
 * @returns {AxiosPromise}
 */
export function updateLoanFollowUpPersonnelId(data) {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/updateLoanFollowUpPersonnelId`,
		method: "post",
		data
	})
}

/**
 * 2.3.25 pc-产品运营人员列表接口
 * @param params
 * @returns {*}
 */
export function followUpPersonnelIdList(params) {
	return request({
		url:`${url}/financing/productSupplementOperate/followUpPersonnelIdList`,
		method: "get",
		params
	})
}
