/**
 * @author chris
 * @date 2024/2/22 11:20
 */
import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/vm" : "/dev-api/vm";

/**
 * 获取模块类别下拉框
 * @param data
 * @returns {*}
 */
export function getTypeList(data) {
	return request({
		url: `${url}/billing/interfaceInput/typeList`,
		method: 'post',
		data
	})
}

/**
 * 新增接口
 * @param data
 * @returns {*}
 */
export function saveInterface(data) {
	return request({
		url: `${url}/billing/interfaceInput/saveInterface`,
		method: 'post',
		data
	})
}

/**
 * 删除接口
 * @param data
 * @returns {*}
 */
export function removeInterface(data) {
	return request({
		url: `${url}/billing/interfaceInput/removeInterface`,
		method: 'post',
		data
	})
}

/**
 * 修改接口
 * @param data
 * @returns {*}
 */
export function updateInterface(data) {
	return request({
		url: `${url}/billing/interfaceInput/updateInterface`,
		method: 'post',
		data
	})
}

/**
 * 查询接口
 * @param data
 * @returns {*}
 */
export function queryInterface(data) {
	return request({
		url: `${url}/billing/interfaceInput/queryInterface`,
		method: 'post',
		data
	})
}
