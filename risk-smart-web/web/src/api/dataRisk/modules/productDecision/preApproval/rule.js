import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/afterLoan'
    : '/dev-api/vm/afterLoan'

//获取主题列表数据
export function searchRuleTableList(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/list`,
    method: 'post',
    data,
  })
}
