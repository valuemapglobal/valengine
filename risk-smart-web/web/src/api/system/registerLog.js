import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/prod-api" : "/dev-api/prod-api";


//查询操作日志
export function selectAll(params) {
  return request({
    url: url + `/system/logininfor/list`,
    method: 'get',
    params
  })
}

// 删除登录日志
export function delLogininfor(infoId) {
  return request({
    url: url + '/system/logininfor/' + infoId,
    method: 'delete'
  })
}

// 清空登录日志
export function cleanLogininfor() {
  return request({
    url: '/system/logininfor/clean',
    method: 'delete'
  })
}

export function exportData(data) {
  return url + `/system/logininfor/export`
}