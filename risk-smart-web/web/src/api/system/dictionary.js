import request from '@/utils/request'
const url = process.env.VUE_APP_ENV === "production" ? "/prod-api" : "/dev-api/prod-api";

export function selectAll(params) {
  return request({
    url: url + `/system/dict/type/list`,
    method: 'get',
    params,
  })
}
export function addDictionary(data) {
  return request({
    url: url + `/system/dict/type`,
    method: 'post',
    data,
  })
}

export function updateType(data) {
  return request({
    url: url + '/system/dict/type',
    method: 'put',
    data,
  })
}
export function deleteType(data) {
  return request({
    url: url + '/system/dict/type/' + data,
    method: 'delete',
  })
}
export function deleteData(data) {
  return request({
    url: url + '/system/dict/data/' + data,
    method: 'delete',
  })
}

export function selectById(data) {
  return request({
    url: url + `/system/dict/type/` + data,
    method: 'get',
  })
}
export function getData(data) {
  return request({
    url: url + `/system/dict/data/` + data,
    method: 'get',
  })
}
export function updateData(data) {
  return request({
    url: url + `/system/dict/data`,
    method: 'put',
    data,
  })
}
export function insertData(data) {
  return request({
    url: url + `/system/dict/data`,
    method: 'post',
    data,
  })
}
export function selectList(params) {
  return request({
    url: url + `/system/dict/data/list`,
    method: 'get',
    params,
  })
}
