import request from '@/utils/request'

const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/smartDecision'
    : '/dev-api/vm/smartDecision'

// https://valuemap.feishu.cn/wiki/HoB9wPfWMinQIJkpo8VcPDvDnDh
export function getRulePoolGage(data) {
  return request({
    url: url + `/rule-pool/page`,
    method: 'post',
    data,
  })
}
export function getRulePoolStats() {
  return request({
    url: url + `/rule-pool/stats`,
    method: 'get',
  })
}
export function getRulePoolHotKeywords() {
  return request({
    url: url + `/rule-pool/hot-keywords`,
    method: 'get',
  })
}
export function getRulePoolExport(data) {
  return request({
    url: url + `/rule-pool/export`,
    method: 'post',
    data,
    responseType: 'blob',
  })
}
