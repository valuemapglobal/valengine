import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/vm/financing" : "/dev-api/vm/financing";

//金融机构列表查询
export function selectAll(data) {
  return request({
    url: url + '/product/selectAll',
    method: 'post',
    data: data
  })
}
export function selectById(data) {
  return request({
    url: url + '/product/selectById',
    method: 'get',
    params: data
  })
}

//金融产品新增/修改  新增时状态传待上架
export function addAndUpdateProduct(data) {
  return request({
    url: url + '/product/addAndUpdateProduct',
    method: 'post',
    data: data
  })
}

//删除金融产品
export function delProduct(data) {
  return request({
    url: url + '/product/delProduct',
    method: 'get',
    params: data
  })
}
