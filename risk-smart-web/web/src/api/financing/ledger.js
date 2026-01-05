/**
 * @author chris
 * @date 2023/11/6 11:27
 */
import request from "@/utils/request";

const url = process.env.VUE_APP_ENV === "production" ? "/vm" : "/dev-api/vm";

/**
 * 贷后台账页面列表接口
 * @param data
 * @returns {*}
 */
export function getSelectLedgerAll(data) {
	return request({
		url: `${url}/financing/vmFinancingLoanAppInfo/selectLedgerAll`,
		method: "post",
		data
	})
}

/**
 * 台账列表导出
 * @param data
 * @returns {*}
 */
export const ledgerExport = (data) => {
  return request({
		url:`${url}/financing/vmFinancingLoanAppInfo/ledgerExport`,
		method: "post",
		responseType: 'blob',
		data
	})
}
