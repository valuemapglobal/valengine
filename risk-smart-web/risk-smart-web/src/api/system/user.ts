import { post, get, put, del } from '@/utils/request'

enum Api {
    List = '/prod-api/system/user/list',
    Add = '/prod-api/system/user',
    Edit = '/prod-api/system/user',
    Delete = '/prod-api/system/user',
    Detail = '/prod-api/system/user', // + /{id}
    ChangeStatus = '/prod-api/system/user/changeStatus',
    ResetPwd = '/prod-api/system/user/resetPwd',
    AuthRole = '/prod-api/system/user/authRole', // + /{id} or params
    DeptTree = '/prod-api/system/dept/treeselect'
}

export function listUser(params: any) {
    return get(Api.List, params)
}

export function getUser(id: number | string) {
    return get(`${Api.Detail}/${id}`)
}

export function addUser(data: any) {
    return post(Api.Add, data)
}

export function updateUser(data: any) {
    return put(Api.Edit, data)
}

export function delUser(id: number | string) {
    return del(`${Api.Delete}/${id}`)
}

export function changeUserStatus(data: any) {
    return put(Api.ChangeStatus, data)
}

export function resetUserPwd(data: any) {
    return put(Api.ResetPwd, data)
}

export function getDeptTree() {
    return get(Api.DeptTree)
}
