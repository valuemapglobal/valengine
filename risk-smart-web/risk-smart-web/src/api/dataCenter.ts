import { get, post, put, del } from '@/utils/request'

const BASE_URL = '/vm/smartDecision'

// ==================== 主题管理 ====================

export interface Theme {
  id: number
  themeNo: string
  name: string
  keycode: string
  packageType: string
  remark?: string
  createTime?: string
}

export interface ThemeListParams {
  pageNum: number
  pageSize: number
  name?: string
  packageType?: number
  orderByColumn?: string
  isAsc?: string
}

export interface PageResult<T> {
  rows: T[]
  total: number
}

/** 查询主题列表 */
export function getThemeList(params: ThemeListParams) {
  return post<PageResult<Theme>>(`${BASE_URL}/theme/_search`, params)
}

/** 获取主题详情 */
export function getThemeById(id: number) {
  return get<Theme>(`${BASE_URL}/theme/${id}`)
}

/** 新增主题 */
export function addTheme(data: Partial<Theme>) {
  return post<boolean>(`${BASE_URL}/theme`, data)
}

/** 编辑主题 */
export function updateTheme(data: Partial<Theme>) {
  return put<boolean>(`${BASE_URL}/theme`, data)
}

/** 删除主题 */
export function deleteTheme(id: number) {
  return del<boolean>(`${BASE_URL}/theme/${id}`)
}

// ==================== 对象/变量组管理 ====================

export interface VariableGroup {
  id: number
  themeNo: string
  name: string
  keycode: string
  type: number
  interfaceVersion?: string
  listName?: string
  parentName?: string
  remark?: string
  createTime?: string
}

export interface GroupListParams {
  themeNo: string
  pageNum: number
  pageSize: number
  name?: string
}

/** 查询对象/变量组列表 */
export function getGroupList(params: GroupListParams) {
  return post<PageResult<VariableGroup>>(`${BASE_URL}/group/_search`, params)
}

/** 获取对象详情 */
export function getGroupById(id: number) {
  return get<VariableGroup>(`${BASE_URL}/group/${id}`)
}

/** 新增对象 */
export function addGroup(data: Partial<VariableGroup>) {
  return post<boolean>(`${BASE_URL}/group`, data)
}

/** 编辑对象 */
export function updateGroup(data: Partial<VariableGroup>) {
  return put<boolean>(`${BASE_URL}/group`, data)
}

/** 删除对象 */
export function deleteGroup(id: number) {
  return del<boolean>(`${BASE_URL}/group/${id}`)
}

// ==================== 变量/属性管理 ====================

export interface Variable {
  id: number
  groupId: number
  name: string
  keycode: string
  type: number
  defaultValue?: string
  description?: string
  required: boolean
  createTime?: string
}

export interface VariableListParams {
  groupId: number
  pageNum: number
  pageSize: number
  name?: string
}

/** 查询变量列表 */
export function getVariableList(params: VariableListParams) {
  return post<PageResult<Variable>>(`${BASE_URL}/variable/_search`, params)
}

/** 获取变量详情 */
export function getVariableById(id: number) {
  return get<Variable>(`${BASE_URL}/variable/${id}`)
}

/** 新增变量 */
export function addVariable(data: Partial<Variable>) {
  return post<boolean>(`${BASE_URL}/variable`, data)
}

/** 编辑变量 */
export function updateVariable(data: Partial<Variable>) {
  return put<boolean>(`${BASE_URL}/variable`, data)
}

/** 删除变量 */
export function deleteVariable(id: number) {
  return del<boolean>(`${BASE_URL}/variable/${id}`)
}

// ==================== 分析指标管理 ====================

export interface AnalysisIndicator {
  id: number
  name: string
  code: string
  formula?: string
  description?: string
  status: number
  createTime?: string
}

/** 查询分析指标列表 */
export function getIndicatorList(params: {
  pageNum: number
  pageSize: number
  name?: string
}) {
  return post<PageResult<AnalysisIndicator>>(`${BASE_URL}/indicator/_search`, params)
}

/** 新增分析指标 */
export function addIndicator(data: Partial<AnalysisIndicator>) {
  return post<boolean>(`${BASE_URL}/indicator`, data)
}

/** 编辑分析指标 */
export function updateIndicator(data: Partial<AnalysisIndicator>) {
  return put<boolean>(`${BASE_URL}/indicator`, data)
}

/** 删除分析指标 */
export function deleteIndicator(id: number) {
  return del<boolean>(`${BASE_URL}/indicator/${id}`)
}

// ==================== 特征变量管理 ====================

export interface FeatureVariable {
  id: number
  name: string
  code: string
  type: number
  formula?: string
  description?: string
  status: number
  createTime?: string
}

/** 查询特征变量列表 */
export function getFeatureList(params: {
  pageNum: number
  pageSize: number
  name?: string
}) {
  return post<PageResult<FeatureVariable>>(`${BASE_URL}/feature/_search`, params)
}

/** 新增特征变量 */
export function addFeature(data: Partial<FeatureVariable>) {
  return post<boolean>(`${BASE_URL}/feature`, data)
}

/** 编辑特征变量 */
export function updateFeature(data: Partial<FeatureVariable>) {
  return put<boolean>(`${BASE_URL}/feature`, data)
}

/** 删除特征变量 */
export function deleteFeature(id: number) {
  return del<boolean>(`${BASE_URL}/feature/${id}`)
}

// ==================== 字典查询 ====================

export interface DictItem {
  dictValue: string
  dictLabel: string
}

/** 根据字典类型查询字典数据 */
export function getDictsByType(dictType: string) {
  return get<DictItem[]>(`/vm/system/dict/data/type/${dictType}`)
}
