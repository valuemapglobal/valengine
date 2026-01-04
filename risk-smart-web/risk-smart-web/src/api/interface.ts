import { post } from '@/utils/request'

const BASE_URL = '/vm/smartData'

// ==================== 数据场景管理 ====================

export interface DataSource {
  id: number
  interfaceSourceNo: string
  dataName: string
  description?: string
  createTime?: string
  updateTime?: string
  current?: boolean
  children?: InterfaceItem[]
}

export interface DataSourceParams {
  pageNum: number
  pageSize: number
  sourceName?: string
}

export interface PageResult<T> {
  list: T[]
  total: number
}

/** 查询数据场景列表 */
export function getSourceList(params: DataSourceParams) {
  return post<PageResult<DataSource>>(`${BASE_URL}/interfaceManage/findSourceInfo`, params)
}

/** 新增数据场景 */
export function addSource(data: { dataName: string; description?: string }) {
  return post<boolean>(`${BASE_URL}/interfaceManage/saveSourceInfo`, data)
}

/** 编辑数据场景 */
export function updateSource(data: { interfaceSourceNo: string; dataName: string; description?: string }) {
  return post<boolean>(`${BASE_URL}/interfaceManage/updateSourceInfo`, data)
}

/** 删除数据场景 */
export function deleteSource(data: { interfaceSourceNo: string }) {
  return post<boolean>(`${BASE_URL}/interfaceManage/removeSourceInfo`, data)
}

// ==================== 接口管理 ====================

export interface InterfaceItem {
  id: number
  interfaceNo: string
  interfaceName: string
  interfaceManageNo: string
  interfaceSourceNo: string
  interfaceDescription?: string
  interfaceFeatures?: string
  interfaceOn: number // 0: 开启, 1: 关闭
  createTime?: string
  updateTime?: string
  current?: boolean
}

export interface InterfaceParams {
  sourceNo: string
  pageNum?: number
  pageSize?: number
  interfaceName?: string
}

/** 查询接口列表 */
export function getInterfaceList(params: InterfaceParams) {
  return post<PageResult<InterfaceItem>>(`${BASE_URL}/interfaceManage/findInterfaceInfo`, params)
}

/** 新增接口 */
export function addInterface(data: Partial<InterfaceItem>) {
  return post<boolean>(`${BASE_URL}/interfaceManage/saveInterfaceInfo`, data)
}

/** 编辑接口 */
export function updateInterface(data: Partial<InterfaceItem>) {
  return post<boolean>(`${BASE_URL}/interfaceManage/updateInterfaceInfo`, data)
}

/** 删除接口 */
export function deleteInterface(data: { manageNo: string }) {
  return post<boolean>(`${BASE_URL}/interfaceManage/removeInterfaceInfo`, data)
}

/** 开启/关闭接口 */
export function toggleInterfaceStatus(data: { manageNo: string; sourceNo: string; interfaceOn: number }) {
  return post<boolean>(`${BASE_URL}/interfaceManage/interfaceOn`, data)
}

// ==================== 接口参数管理 ====================

export interface InterfaceField {
  id: number
  interfaceNo: string
  interfaceFieIdName: string
  interfaceFieIdAlias: string
  interfaceFieIdDataType: number
  interfaceFieIdType: number // 0: 入参, 1: 出参
  interfaceFieIdDescription?: string
  interfaceFieIdRemark?: string
  createTime?: string
}

export interface InterfaceFieldParams {
  manageNo: string
  pageNum?: number
  pageSize?: number
}

/** 查询接口参数列表 */
export function getInterfaceFields(params: InterfaceFieldParams) {
  return post<PageResult<InterfaceField>>(`${BASE_URL}/interfaceManage/findInterfaceFieIdInfo`, params)
}

/** 新增接口参数 */
export function addInterfaceField(data: Partial<InterfaceField>) {
  return post<boolean>(`${BASE_URL}/interfaceManage/saveInterfaceFieIdInfo`, data)
}

/** 编辑接口参数 */
export function updateInterfaceField(data: Partial<InterfaceField>) {
  return post<boolean>(`${BASE_URL}/interfaceManage/updateInterfaceFieIdInfo`, data)
}

/** 删除接口参数 */
export function deleteInterfaceField(data: { id: number }) {
  return post<boolean>(`${BASE_URL}/interfaceManage/removeInterfaceFieIdInfo`, data)
}

// ==================== 接口测试 ====================

export interface InterfaceTestParams {
  manageNo: string
  sourceNo: string
  interfaceNo: string
  paramData: Record<string, unknown>
  orderId: string
}

export interface InterfaceTestResult {
  code: number
  message: string
  data: unknown
}

/** 接口测试 */
export function testInterface(data: InterfaceTestParams) {
  return post<InterfaceTestResult>(`${BASE_URL}/interfaceRequest/apiPage`, data)
}

// ==================== 数据审计 ====================

export interface InterfaceLog {
  id: number
  interfaceNo: string
  interfaceName: string
  requestTime: string
  responseTime: string
  status: string
  errorMessage?: string
}

export interface InterfaceLogParams {
  pageNum: number
  pageSize: number
  interfaceNo?: string
  startTime?: string
  endTime?: string
}

/** 查询接口日志 */
export function getInterfaceLogs(params: InterfaceLogParams) {
  return post<PageResult<InterfaceLog>>(`${BASE_URL}/interfaceLog/findAllLog`, params)
}

/** 查询流量统计 */
export function getTrafficStatistics(params: Record<string, unknown>) {
  return post<Record<string, unknown>>(`${BASE_URL}/interfaceLog/countLog`, params)
}
