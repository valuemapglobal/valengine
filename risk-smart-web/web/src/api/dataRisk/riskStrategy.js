import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/vm/afterLoan" : "/dev-api/vm/afterLoan";

export function selectList(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/list`,
    method: 'post',
    data
  })
}
export function deleteById(data) {
  return request({
    url: url + `/rde/model/decisionCodeLevel/delete/${data}`,
    method: 'get'
  })
}
