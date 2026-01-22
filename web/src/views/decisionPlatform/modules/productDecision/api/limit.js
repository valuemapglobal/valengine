import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'
const url1 =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartData'
    : '/dev-api/vm/smartData'

export function getLimitList(data) {
  return request({
    url: url + `/quota-card-record/list`,
    method: 'post',
    data,
  })
}
export function submitLimit(data) {
  return request({
    url: url + `/quota-card-radius/submit`,
    method: 'post',
    data,
  })
}

export function getLimit(data) {
  return request({
    url: url + `/quota-card-radius/get`,
    method: 'post',
    data,
  })
}
export function releaseLimit(data) {
  return request({
    url: url + `/quota-card-record/release`,
    method: 'post',
    data,
  })
}
export function updateStatusLimit(data) {
  return request({
    url: url + `/quota-card-record/updateStatus`,
    method: 'post',
    data,
  })
}

export function interfaceTree() {
  return request({
    url: url1 + `/interfaceSource/quota/interface-tree`,
    method: 'get',
  })
}

export function standardQuotaFormula(data) {
  return request({
    url: url + `/quota-card-record/standard-quota-formula`,
    method: 'post',
    data,
  })
}
export function getStandardQuotaFormula(quotaCardId) {
  return request({
    url: url + `/quota-card-record/standard-quota-formula/${quotaCardId}`,
    method: 'get',
  })
}
