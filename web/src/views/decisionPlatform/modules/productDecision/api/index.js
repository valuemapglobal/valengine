import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'

const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

//查询尽调测试数据
export function searchFlowRecord(data) {
  return request({
    url: url + `/rdenew/model/SelectListVo/List`,
    method: 'post',
    data,
  })
}

//查询对应modelID下的命中数据
export function submitModelToTest(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/snapshot/modelTest`,
    method: 'post',
    data,
  })
}

//统计命中条数
export function countHitNum(data) {
  return request({
    url: url + `/rdenew/model/test/result/ByOrderNo`,
    method: 'post',
    data,
  })
}

//修改测试记录状态
export function updateTestResult(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/snapshot/updateTestResult`,
    method: 'post',
    data,
  })
}

//发布策略
export function releaseStrategy(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/snapshot/ruleDataRule`,
    method: 'post',
    data,
  })
}

//重置测试状态
export function resetTestState(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/snapshot/resetTestState`,
    method: 'post',
    data,
  })
}

// 启用禁用状态
export function updateState(data) {
  return request({
    url: url + `/rdenew/model/antiFraud/updateState`,
    method: 'post',
    data,
  })
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType) {
  return request({
    url: url1 + '/system/dict/data/type/' + dictType,
    method: 'get',
  })
}

/**
 * 产品配置页——获取列表
 * @param data
 * @returns {*}
 */
export function selectAll(data) {
  return request({
    url: `${url1}/financing/product/selectAll`,
    method: 'post',
    data,
  })
}

/**
 * 决策中台-左侧产品列表
 * @param data
 * @returns {*}
 */
export function product_search(data) {
  return request({
    url: `${url}/product/_search`,
    method: 'post',
    data,
  })
}

/**
 * 决策中台-左侧产品列表——新增产品
 * @param data
 * @returns {*}
 */
export function newlyAddProduct(data) {
  return request({
    url: `${url}/product`,
    method: 'post',
    data,
  })
}

/**
 * 决策中台-左侧产品列表——编辑产品
 * @param data
 * @returns {*}
 */
export function editProduct(data) {
  return request({
    url: `${url}/product`,
    method: 'put',
    data,
  })
}

/**
 * 决策中台-左侧产品列表——删除产品
 * @param id
 * @returns {*}
 */
export function deleteProduct(id) {
  return request({
    url: `${url}/product/${id}`,
    method: 'DELETE',
  })
}

/**
 * 决策中台-顶部业务场景列表
 * @param data
 * @returns {*}
 */
export function business_search(data) {
  return request({
    url: `${url}/business/_search`,
    method: 'post',
    data,
  })
}

/**
 * 2.7.3 复用-标准产品下拉框
 * @returns {*}
 */
export function standardProductList() {
  return request({
    url: `${url}/product/_standard`,
    method: 'get',
  })
}

/**
 * 2.7.8 复用-标准场景下拉框
 * @param data
 * @returns {*}
 */
export function business_standard(data) {
  return request({
    url: `${url}/business/_standard`,
    method: 'get',
    data,
  })
}
/**
 * 决策中台-顶部业务场景列表——新增业务场景
 * @param data
 * @returns {*}
 */
export function newlyAddBusiness(data) {
  return request({
    url: `${url}/business`,
    method: 'post',
    data,
  })
}

/**
 * 决策中台-顶部业务场景列表——编辑业务场景
 * @param data
 * @returns {*}
 */
export function editBusiness(data) {
  return request({
    url: `${url}/business`,
    method: 'put',
    data,
  })
}

/**
 * 决策中台-顶部业务场景列表——删除业务场景
 * @param id
 * @returns {*}
 */
export function deleteBusiness(id) {
  return request({
    url: `${url}/business/${id}`,
    method: 'DELETE',
  })
}

/**
 * 2.3.1 模型测试任务启动   -- 模型测试页面
 * @param data
 * @returns {*}
 */
export function modelTest(data) {
  return request({
    url: `${url}/model-test-task/modelTest`,
    method: 'post',
    data,
  })
}

/**
 * 2.3.2 任务号查询运行drl文件  --模型测试页面
 * @param taskNo
 * @returns {*}
 */
export function selectDrl(taskNo) {
  return request({
    url: `${url}/model-test-task/selectDrl?taskNo=${taskNo}`,
    method: 'get',
  })
}

/**
 * 2.3.4 模型测试任务列表  --模型测试页面
 * @param data
 * @returns {*}
 */
export function getTestModelList(data) {
  return request({
    url: `${url}/model-test-task/selectList`,
    method: 'post',
    data,
  })
}

/**
 * https://valuemap.feishu.cn/wiki/FUq5wSs7qigvDjkB2CccDX5Ynwb
 */
// 获取通知状态
export function notificationsStatus(data) {
  return request({
    url: `${url}/approval-flow/notifications/status`,
    method: 'get',
    params: data,
  })
}
// 标记为已读
export function notificationsMarkAsRead(data) {
  return request({
    url: `${url}/approval-flow/notifications/mark-as-read`,
    method: 'get',
    params: data,
  })
}

// 获取锁定状态
export function lockStatus(data) {
  return request({
    url: `${url}/approval-flow/lock-status`,
    method: 'get',
    params: data,
  })
}

// 记录变更
export function logChange(data) {
  return request({
    url: `${url}/approval-flow/log-change`,
    method: 'post',
    data,
  })
}

// 生成版本对比报告
export function comparePreview(data) {
  return request({
    url: `${url}/approval-flow/compare-preview`,
    method: 'post',
    data,
    responseType: 'blob',
  })
}

// 提交审批
export function approvalSubmit(data) {
  return request({
    url: `${url}/approval-flow/submit`,
    method: 'post',
    data,
  })
}

// 上传对比报告
export function uploadReport() {
  return `${url}/approval-flow/upload-report`
}

// 下载对比报告
export function downloadReport(batchId) {
  return request({
    url: `${url}/approval-flow/download/report/${batchId}`,
    method: 'get',
  })
}

// 审批历史
export function approvalHistory(data) {
  return request({
    url: `${url}/approval-flow/history`,
    method: 'post',
    data,
  })
}

export function batchCode(batchId) {
  return request({
    url: `${url}/approval-flow/batch-code/${batchId}`,
    method: 'get',
  })
}
