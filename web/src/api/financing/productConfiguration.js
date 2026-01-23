/**
 * @author chris
 * @date 2023/4/25 9:32
 */
import request from '@/utils/request'
const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

/**
 * 地区信息接口
 * @returns {*}
 */
export function getRegionData() {
  return request({
    url: `${url}/financing/bigDataTransfer/region`,
    method: 'get',
  })
}

/**
 * 产品配置页——新增产品
 * @param data
 * @returns {*}
 */
export function addProduct(data) {
  return request({
    url: `${url}/financing/product/addProduct`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——修改产品  - 旧
 * 编辑 基本信息，产品说明 - 新
 * @param data
 * @returns {*}
 */
export function updateProduct(data) {
  return request({
    url: `${url}/financing/product/updateProduct`,
    method: 'post',
    data,
  })
}

// 拓客偏好
export function selectProductAttach(data) {
  return request({
    url: `${url}/financing/product/selectProductAttach`,
    method: 'post',
    data,
  })
}
// 修改拓客偏好
export function updateProductAttach(data) {
  return request({
    url: `${url}/financing/product/updateProductAttach`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——删除产品
 * @param params
 * @returns {*}
 */
export function delProduct(params) {
  return request({
    url: `${url}/financing/product/delProduct`,
    method: 'get',
    params,
  })
}

/**
 * 产品配置页——补充说明（新增）
 * @param data
 * @returns {*}
 */
export function addProductSupplement(data) {
  return request({
    url: `${url}/financing/productSupplement/addProductSupplement`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——补充说明（修改）
 * @param data
 * @returns {*}
 */
export function editProductSupplement(data) {
  return request({
    url: `${url}/financing/productSupplement/edit`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——补充说明（查询）
 * @param productSupplementId
 * @returns {*}
 */
export function searchProductSupplement(productSupplementId) {
  return request({
    url: `${url}/financing/productSupplement/${productSupplementId}`,
    method: 'post',
  })
}

/**
 * 产品配置页——上架产品或下架产品
 * @param data
 * @returns {*}
 */
export function updateProductStatus(data) {
  return request({
    url: `${url}/financing/product/updateProductStatus`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——关键字（新增）
 * @param data
 * @returns {*}
 */
export function setKeyWord(data) {
  return request({
    url: `${url}/financing/productKeyword/add`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——关键字（查询）
 * @param data
 * @returns {*}
 */
export function getKeyWord(data) {
  return request({
    url: `${url}/financing/productKeyword/selectList`,
    method: 'post',
    data,
  })
}

/**
 * 产品配置页——获取列表
 * @param data
 * @returns {*}
 */
export function selectAll(data) {
  return request({
    url: `${url}/financing/product/selectAll`,
    method: 'post',
    data,
  })
}

export function product_search(data) {
  return request({
    url: `${url}/smartDecision/product/_search`,
    method: 'post',
    data,
  })
}

/**
 * 运营配置获取跟进人员
 * @param params
 * @returns {*}
 */
export function listByDeptId(params) {
  return request({
    url: url + `/system/user/listByDeptId`,
    method: 'get',
    params,
  })
}

export function getProductCategoryFundUseList() {
  return request({
    url: `${url}/financing/categoryFundUse/getProductCategoryFundUseList`,
    method: 'get',
  })
}
