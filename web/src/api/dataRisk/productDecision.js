import request from "@/utils/request";

const url = process.env.VUE_APP_ENV === "production" ? "/vm/afterLoan" : "/dev-api/vm/afterLoan";

//查询尽调测试数据
export function searchFlowRecord(data) {
	return request({
		url: url + `/rde/model/SelectListVo/List`,
		method: 'post',
		data
	})
}

//查询对应modelID下的命中数据
export function submitModelToTest(data) {
	return request({
		url: url + `/rde/model/antiFraud/snapshot/modelTest`,
		method: 'post',
		data
	})
}

//查询对应modelID下的命中数据(支付流水)
export function submitModelToTestPay(data) {
	return request({
		url: url + `/rde/model/antiFraud/snapshot/modelTestPay`,
		method: 'post',
		data
	})
}

//统计命中条数
export function countHitNum(data) {
	return request({
		url: url + `/rde/model/test/result/ByOrderNo`,
		method: 'post',
		data
	})
}

//测试结果汇总
export function testResultCollect(data) {
	return request({
		url: url + `/rde/model/test/result/testResultCollect`,
		method: 'post',
		data
	})
}
//流水明细分页列表
export function testListPage(data) {
	return request({
		url: url + "/excel/details/listPage",
		method: 'post',
		data
	})
}
// 流水明细按需查询（表头筛选）
export function testFilterDetail(data) {
  return request({
    url: url + "/excel/details/filterDetail",
    method: 'post',
    data
  })
}
//修改测试记录状态
export function updateTestResult(data) {
	return request({
		url: url + `/rde/model/antiFraud/snapshot/updateTestResult`,
		method: 'post',
		data
	})
}

//发布策略
export function releaseStrategy(data) {
	return request({
		url: url + `/rde/model/antiFraud/snapshot/ruleDataRule`,
		method: 'post',
		data
	})
}

//重置测试状态
export function resetTestState(data) {
	return request({
		url: url + `/rde/model/antiFraud/snapshot/resetTestState`,
		method: 'post',
		data
	})
}

// 启用禁用状态
export function updateState(data) {
	return request({
		url: url + `/rde/model/antiFraud/updateState`,
		method: 'post',
		data
	})
}
