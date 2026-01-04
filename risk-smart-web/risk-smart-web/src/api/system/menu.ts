import { post, get, put, del } from '@/utils/request'

enum Api {
    List = '/prod-api/system/menu/list',
    Add = '/prod-api/system/menu',
    Edit = '/prod-api/system/menu',
    Delete = '/prod-api/system/menu', // + /{id}
    Detail = '/prod-api/system/menu', // + /{id}
}

export function listMenu(params: any) {
    return get(Api.List, params)
}

export function getMenu(id: number | string) {
    return get(`${Api.Detail}/${id}`)
}

export function addMenu(data: any) {
    return post(Api.Add, data)
}

export function updateMenu(data: any) {
    return put(Api.Edit, data)
}

export function delMenu(id: number | string) {
    return del(`${Api.Delete}/${id}`)
}
