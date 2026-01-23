/**
 * @author chris
 * @date 2024/8/5 9:35
 */
import request from '@/utils/request';

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm';

/**
 * 2.3.46 pc-渠道-列表接口
 * @param data
 * @returns {*}
 */
export function getChannelList(data) {
	return request({
		url: `${url}/financing/marketingChannel/getChannelList`,
		method: 'post',
		data
	})
}

/**
 * 2.3.47 pc-渠道-邀请明细列表接口
 * @param data
 * @returns {*}
 */
export function getInviteList(data) {
	return request({
		url: `${url}/financing/marketingChannelInvite/getDetaileList`,
		method: 'post',
		data
	})
}

/**
 * 2.3.48 pc-渠道-收益明细列表接口
 * @param data
 * @returns {*}
 */
export function getIncomeList(data) {
	return request({
		url: `${url}/financing/marketingChannelIncome/getDetaileList`,
		method: 'post',
		data
	})
}

/**
 * 2.3.49 pc-渠道-提现记录列表接口
 * @param data
 * @returns {*}
 */
export function getWithdrawList(data) {
	return request({
		url: `${url}/financing/marketingWalletTransaction/getDetaileList`,
		method: 'post',
		data
	})
}

/**
 * pc-渠道-获取客户经理列表接口
 * @param params
 * @returns {*}
 */
export function getMainProductFollowUpPersonnelIdList(params) {
	return request({
		url: `${url}/financing/productSupplementOperate/getMainProductFollowUpPersonnelIdList`,
		method: 'get',
		params
	})
}

/**
 * 2.3.50 pc-渠道-变更客户经理接口
 * @param data
 * @returns {*}
 */
export function assignManager(data) {
	return request({
		url: `${url}/financing/marketingChannel/assignManager`,
		method: 'post',
		data
	})
}

/**
 * 2.3.51 pc-佣金-详情接口
 * @returns {*}
 */
export function commissionDetails() {
	return request({
		url: `${url}/financing/vmMarketingCommissionRate/details`,
		method: 'get'
	})
}

/**
 * 2.3.52 pc-佣金-修改接口
 * @param data
 * @returns {*}
 */
export function updateCommissionRate(data) {
	return request({
		url: `${url}/financing/vmMarketingCommissionRate/updateCommissionRate`,
		method: 'post',
		data
	})
}

/**
 * 2.3.53 pc-佣金-修改明细列表接口
 * @param data
 * @returns {*}
 */
export function commissionModificationDetails(data) {
	return request({
		url: `${url}/financing/vmMarketingCommissionRateLog/commissionModificationDetails`,
		method: 'post',
		data
	})
}

/**
 * 2.3.52 pc-渠道-启用/禁用渠道
 * @param data
 * @returns {*}
 */
export function changeChannelStatus(data) {
	return request({
		url: `${url}/financing/marketingChannel/changeChannelStatus`,
		method: 'post',
		data
	})
}

/**
 * 2.3.53 pc-渠道-详情-邀请明细导出接口
 * @param data
 * @returns {*}
 */
export function getPcChannelInviteDetailedExport(data) {
	return request({
		url: `${url}/financing/marketingChannelInvite/getPcChannelInviteDetailedExport`,
		method: 'post',
		responseType: 'blob',
		data
	})
}
