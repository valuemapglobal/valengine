import request from '@/utils/request'
// import request2 from '@/utils/requestTest'
const url =
  process.env.VUE_APP_ENV === 'production' ? '/prod-api' : '/dev-api/prod-api'
// 获取菜单列表
export const getMenuList = (params) => {
  return request({
    url: url + '/system/menu/list',
    method: 'get',
    params,
  })
}
//删除菜单列表
export const DeleteMenuList = (id) => {
  return request({
    url: url + `/system/menu/${id}`,
    method: 'DELETE',
  })
}
// 增加与修改问题
export const AddOrUpdateMenuList = (data, dig) => {
  return request({
    url: url + `/system/menu`,
    method: dig && dig === 'add' ? 'post' : 'put',
    data,
  })
}
// 获取状态字典
export const getStateDictionary = () => {
  return request({
    url: url + '/system/dict/data/type/sys_normal_disable',
    method: 'get',
  })
}