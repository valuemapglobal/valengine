import request from '@/utils/request'
const url =
  process.env.VUE_APP_ENV === 'production'
    ? '/vm/financing'
    : '/dev-api/vm/financing'
const url2 = process.env.VUE_APP_ENV === 'production' ? '' : '/dev-api'
const url3 = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

/*
 * 融资列表批量导入 —— 点击上传
 * */
export function upLoadFinanceUrl() {
  return `${url}/financingInformation/importFinancing`
}

/*
 *融资详情资料填写 —— 上传附件
 *金融产品管理 添加产品—— 上传logo
 * 贷后明细批量导入 —— 点击上传
 * */
export function uploadFileUrl() {
  return `${url2}/vm/file/upload`
}
export function getDicts(data) {
  return request({
    url: url3 + `/system/dict/data/type/${data}`,
    method: 'get',
  })
}

/**
 * 埋点接口
 * @param data
 * @returns {*}
 */
export function buryingPointAPI(data) {
  return request({
    url:`${url3}/event/event_user/save_event`,
    method:"post",
    data
  })
}
