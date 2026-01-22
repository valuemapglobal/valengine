/**
 * @author chris
 * @date 2024/2/22 14:34
 */
import request from "@/utils/request";

const url = process.env.VUE_APP_ENV === "production" ? "/vm" : "/dev-api/vm";

/**
 * 获取授权部门
 * @param data
 * @returns {*}
 */
export function queryDept(data) {
	return request({
		url: `${url}/billing/billingRules/queryDept`,
		method: "post",
		data
	})
}

/**
 * 根据部门查询接口配置
 * @param data
 * @returns {*}
 */
export function queryDeptInterface(data) {
	return request({
		url: `${url}/billing/billingRules/queryDeptInterface`,
		method: "post",
		data
	})
}

/**
 * 1.11 修改部门接口使用次数
 * @param data
 * @returns {*}
 */
export function updateDeptCount(data) {
	return request({
		url: `${url}/billing/billingRules/updateDeptCount`,
		method: "post",
		data
	})
}
