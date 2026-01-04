import { get, post, put, del } from '@/utils/request'

const BASE_URL = '/vm/smartDecision'

// ==================== 产品管理 ====================

export interface Product {
  id: number
  name: string
  productName?: string
  createTime?: string
  updateTime?: string
}

export interface ProductListParams {
  name?: string
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  list: T[]
  total: number
}

/** 查询产品列表 */
export function getProductList(params: ProductListParams) {
  return post<PageResult<Product>>(`${BASE_URL}/product/_search`, params)
}

/** 新增产品 */
export function addProduct(data: { name: string }) {
  return post<boolean>(`${BASE_URL}/product`, data)
}

/** 编辑产品 */
export function updateProduct(data: { id: number; name: string }) {
  return put<boolean>(`${BASE_URL}/product`, data)
}

/** 删除产品 */
export function deleteProduct(id: number) {
  return del<boolean>(`${BASE_URL}/product/${id}`)
}

// ==================== 业务场景管理 ====================

export interface Business {
  id: number
  name: string
  deptId?: number
  createTime?: string
  updateTime?: string
}

/** 查询业务场景列表 */
export function getBusinessList(params?: Record<string, unknown>) {
  return post<PageResult<Business>>(`${BASE_URL}/business/_search`, params || {})
}

/** 新增业务场景 */
export function addBusiness(data: { name: string }) {
  return post<boolean>(`${BASE_URL}/business`, data)
}

/** 编辑业务场景 */
export function updateBusiness(data: { id: number; name: string }) {
  return put<boolean>(`${BASE_URL}/business`, data)
}

/** 删除业务场景 */
export function deleteBusiness(id: number) {
  return del<boolean>(`${BASE_URL}/business/${id}`)
}

/** 获取标准产品下拉框 */
export function getStandardProducts() {
  return get<Product[]>(`${BASE_URL}/product/_standard`)
}

/** 获取标准场景下拉框 */
export function getStandardBusiness() {
  return get<Business[]>(`${BASE_URL}/business/_standard`)
}

// ==================== 规则/策略管理 ====================

export interface RuleGroup {
  id: number
  name: string
  code: string
  description?: string
  status: number
  createTime?: string
}

export interface Rule {
  id: number
  name: string
  code: string
  groupId: number
  expression?: string
  description?: string
  status: number
  priority: number
  createTime?: string
}

/** 发布策略 */
export function releaseStrategy(data: {
  projectCode: string
  businessCode: string
  ruleCode: string
}) {
  return post<boolean>(`${BASE_URL}/rdenew/model/antiFraud/snapshot/ruleDataRule`, data)
}

/** 更新状态 */
export function updateState(data: { id: number; state: number }) {
  return post<boolean>(`${BASE_URL}/rdenew/model/antiFraud/updateState`, data)
}

// ==================== 模型测试 ====================

export interface TestTask {
  id: number
  taskNo: string
  status: string
  createTime?: string
  result?: string
}

/** 模型测试任务启动 */
export function startModelTest(data: Record<string, unknown>) {
  return post<TestTask>(`${BASE_URL}/model-test-task/modelTest`, data)
}

/** 任务号查询运行drl文件 */
export function selectDrl(taskNo: string) {
  return get<string>(`${BASE_URL}/model-test-task/selectDrl`, { taskNo })
}

/** 模型测试任务列表 */
export function getTestModelList(data: Record<string, unknown>) {
  return post<PageResult<TestTask>>(`${BASE_URL}/model-test-task/selectList`, data)
}

// ==================== 审批流程 ====================

export interface ApprovalHistory {
  id: number
  batchId: string
  status: string
  operator: string
  operateTime: string
  remark?: string
}

export interface NotificationStatusParams {
  projectCode: string
  businessCode: string
  ruleCode: string
}

/** 获取通知状态 */
export function getNotificationsStatus(params: NotificationStatusParams) {
  return get<{ hasNew: boolean }>(`${BASE_URL}/approval-flow/notifications/status`, params)
}

/** 标记为已读 */
export function markAsRead(params: NotificationStatusParams) {
  return get<boolean>(`${BASE_URL}/approval-flow/notifications/mark-as-read`, params)
}

/** 获取锁定状态 */
export function getLockStatus(params: NotificationStatusParams) {
  return get<{ locked: boolean; lockedBy?: string }>(`${BASE_URL}/approval-flow/lock-status`, params)
}

/** 记录变更 */
export function logChange(data: Record<string, unknown>) {
  return post<boolean>(`${BASE_URL}/approval-flow/log-change`, data)
}

/** 提交审批 */
export function submitApproval(data: Record<string, unknown>) {
  return post<boolean>(`${BASE_URL}/approval-flow/submit`, data)
}

/** 审批历史 */
export function getApprovalHistory(data: NotificationStatusParams) {
  return post<ApprovalHistory[]>(`${BASE_URL}/approval-flow/history`, data)
}

/** 获取批次代码 */
export function getBatchCode(batchId: string) {
  return get<string>(`${BASE_URL}/approval-flow/batch-code/${batchId}`)
}
