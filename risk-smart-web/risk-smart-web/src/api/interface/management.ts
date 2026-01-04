import { post } from '@/utils/request'

enum Api {
    FindInterface = '/vm/smartData/interfaceManage/findInterfaceInfo',
    RemoveInterface = '/vm/smartData/interfaceManage/removeInterfaceInfo',
    SaveInterface = '/vm/smartData/interfaceManage/saveInterfaceInfo',
    UpdateInterface = '/vm/smartData/interfaceManage/updateInterfaceInfo',
    InterfaceOn = '/vm/smartData/interfaceManage/interfaceOn',

    // Params
    FindParams = '/vm/smartData/interfaceManage/findInterfaceFieIdInfo',
    UpdateParams = '/vm/smartData/interfaceManage/updateInterfaceFieIdInfo',
    RemoveParams = '/vm/smartData/interfaceManage/removeInterfaceFieIdInfo',
    SaveParams = '/vm/smartData/interfaceManage/saveInterfaceFieIdInfo',

    // Test
    InterfaceTest = '/vm/smartData/interfaceRequest/apiPage'
}

export interface InterfaceParams {
    pageNum?: number
    pageSize?: number
    interfaceName?: string
    [key: string]: any
}

// Interface List
export function findInterfaceInfo(data: InterfaceParams) {
    return post(Api.FindInterface, data)
}

// Remove Interface
export function removeInterfaceInfo(data: any) {
    return post(Api.RemoveInterface, data)
}

// Add Interface
export function saveInterfaceInfo(data: any) {
    return post(Api.SaveInterface, data)
}

// Update Interface
export function updateInterfaceInfo(data: any) {
    return post(Api.UpdateInterface, data)
}

// Interface On/Off
export function interfaceOn(data: any) {
    return post(Api.InterfaceOn, data)
}

// Query Params
export function findInterfaceFieIdInfo(data: any) {
    return post(Api.FindParams, data)
}

// Update Params
export function updateInterfaceFieIdInfo(data: any) {
    return post(Api.UpdateParams, data)
}

// Remove Params
export function removeInterfaceFieIdInfo(data: any) {
    return post(Api.RemoveParams, data)
}

// Save Params
export function saveInterfaceFieIdInfo(data: any) {
    return post(Api.SaveParams, data)
}

// Interface Test
export function findInterfaceInfoTest(data: any) {
    return post(Api.InterfaceTest, data)
}
