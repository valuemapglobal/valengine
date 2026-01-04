import { post, get, put, del } from '@/utils/request'

enum Api {
    // Product
    ProductSelectAll = '/vm/financing/product/selectAll',
    ProductSearch = '/vm/smartDecision/product/_search',
    ProductAdd = '/vm/smartDecision/product',
    ProductEdit = '/vm/smartDecision/product',
    ProductDelete = '/vm/smartDecision/product',
    StandardProductList = '/vm/smartDecision/product/_standard',

    // Business Scene
    BusinessSearch = '/vm/smartDecision/business/_search',
    BusinessStandard = '/vm/smartDecision/business/_standard',
    BusinessAdd = '/vm/smartDecision/business',
    BusinessEdit = '/vm/smartDecision/business',
    BusinessDelete = '/vm/smartDecision/business',
}

// Product APIs
export function product_search(data: any) {
    return post(Api.ProductSearch, data)
}

export function newlyAddProduct(data: any) {
    return post(Api.ProductAdd, data)
}

export function editProduct(data: any) {
    return put(Api.ProductEdit, data)
}

export function deleteProduct(id: string | number) {
    return del(`${Api.ProductDelete}/${id}`)
}

export function standardProductList() {
    return get(Api.StandardProductList)
}

// Business Scene APIs
export function business_search(data: any = {}) {
    return post(Api.BusinessSearch, data)
}

export function newlyAddBusiness(data: any) {
    return post(Api.BusinessAdd, data)
}

export function editBusiness(data: any) {
    return put(Api.BusinessEdit, data)
}

export function deleteBusiness(id: string | number) {
    return del(`${Api.BusinessDelete}/${id}`)
}

export function business_standard(data: any) {
    return get(Api.BusinessStandard, data)
}
