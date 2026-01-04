import { post } from '@/utils/request'

enum Api {
  SaveSource = '/vm/smartData/interfaceManage/saveSourceInfo',
  RemoveSource = '/vm/smartData/interfaceManage/removeSourceInfo',
  UpdateSource = '/vm/smartData/interfaceManage/updateSourceInfo',
  FindSource = '/vm/smartData/interfaceManage/findSourceInfo',
}

export interface SourceInfoParams {
  pageNum?: number
  pageSize?: number
  sourceName?: string
  [key: string]: any
}

// Data Scenario Add
export function saveSourceInfo(data: any) {
  return post(Api.SaveSource, data)
}

// Data Scenario Delete
export function removeSourceInfo(data: any) {
  return post(Api.RemoveSource, data)
}

// Data Scenario Update
export function updateSourceInfo(data: any) {
  return post(Api.UpdateSource, data)
}

// Data Scenario List
export function findSourceInfo(data: SourceInfoParams) {
  return post(Api.FindSource, data)
}
