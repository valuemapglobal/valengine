import request from '@/utils/request'

const url = process.env.VUE_APP_ENV === 'production' ? '' : '/dev-api'

/**
 * 获取字典列表
 * @param params 字典类型
 * @returns {*}
 */
export function getDomain(params) {
	return request({
		url: url + '/vm/system/dict/data/type/' + params,
		method: 'get',
	})
}
