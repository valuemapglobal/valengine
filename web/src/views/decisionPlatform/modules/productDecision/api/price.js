import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'
const url1 = process.env.VUE_APP_ENV === 'production' ? '/' : '/ht'

export function getPriceList(data) {
  return request({
    url: url + `/price-card-record/list`,
    method: 'post',
    data,
  })
}
export function submitPrice(data) {
  return request({
    url: url + `/price-card-radius/submit`,
    method: 'post',
    data,
  })
}
export function getPrice(data) {
  return request({
    url: url + `/price-card-radius/get`,
    method: 'post',
    data,
  })
}
export function updateStatusPrice(data) {
  return request({
    url: url + `/price-card-record/updateStatus`,
    method: 'post',
    data,
  })
}
export function releasePrice(data) {
  return request({
    url: url + `/price-card-record/release`,
    method: 'post',
    data,
  })
}
