import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/prod-api" : "/dev-api";


//查询操作日志
export function selectAll(params) {
  return request({
    url: url + `/system/operlog/list`,
    method: 'get',
    params
  })
}

export function delOperlog(operId) {
  return request({
    url: url + '/system/operlog/' + operId,
    method: 'delete'
  })
}

// 清空操作日志
export function cleanOperlog() {
  return request({
    url: url + '/system/operlog/clean',
    method: 'delete'
  })
}

export function exportData(data) {
  return url + `/system/operlog/export`
}