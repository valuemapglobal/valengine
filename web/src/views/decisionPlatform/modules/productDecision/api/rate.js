import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'
const url1 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

export function getRateList(data) {
  return request({
    url: url + `/rate-card-record/list`,
    method: 'post',
    data,
  })
}
export function submitRate(data) {
  return request({
    url: url + `/rate-card-radius/submit`,
    method: 'post',
    data,
  })
}
export function getRate(data) {
  return request({
    url: url + `/rate-card-radius/get`,
    method: 'post',
    data,
  })
}
export function releaseRate(data) {
  return request({
    url: url + `/rate-card-record/release`,
    method: 'post',
    data,
  })
}
export function updateStatusRate(data) {
  return request({
    url: url + `/rate-card-record/updateStatus`,
    method: 'post',
    data,
  })
}
