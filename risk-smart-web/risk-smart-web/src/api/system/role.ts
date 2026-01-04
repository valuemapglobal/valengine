import { post, get, put, del } from '@/utils/request'

enum Api {
    List = '/prod-api/system/role/list',
    Add = '/prod-api/system/role',
    Edit = '/prod-api/system/role',
    Delete = '/prod-api/system/role',
    Detail = '/prod-api/system/role', // + /{id}
    ChangeStatus = '/prod-api/system/role/changeStatus',
    DataScope = '/prod-api/system/role/dataScope',
    MenuTreeSelect = '/prod-api/system/menu/treeselect',
    RoleMenuTreeSelect = '/prod-api/system/menu/roleMenuTreeselect', // + /{id}
}

export function listRole(params: any) {
    return get(Api.List, params)
}

export function getRole(id: number | string) {
    return get(`${Api.Detail}/${id}`)
}

export function addRole(data: any) {
    return post(Api.Add, data)
}

export function updateRole(data: any) {
    return put(Api.Edit, data)
}

export function delRole(id: number | string) {
    return del(`${Api.Delete}/${id}`)
}

export function changeRoleStatus(data: any) {
    return put(Api.ChangeStatus, data)
}

export function dataScope(data: any) {
    return put(Api.DataScope, data)
}

export function getMenuTree() {
    return get(Api.MenuTreeSelect)
}

export function getRoleMenuTree(roleId: number | string) {
    return get(`${Api.RoleMenuTreeSelect}/${roleId}`)
}
