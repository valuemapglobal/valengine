import request from "@/utils/request";
const url = process.env.VUE_APP_ENV === "production" ? "/vm/afterLoan" : "/dev-api/vm/afterLoan";

//获取主题列表数据
export function getThemeDataList(data) {
  return request({
    url: url + `/rde/variable/theme/list`,
    method: 'post',
    data
  })
}

//新增或者修改主题
export function submitTheme(data) {
  return request({
    url: url + `/rde/variable/theme/submit`,
    method: 'post',
    data
  })
}

//通过id删除主题列表数据
export function delThemeById(id) {
  return request({
    url: url + `/rde/variable/theme/delete/ + ${id}`,
    method: 'get'
  })
}

//获取主题对应对象列表数据
export function getVariableGroupList(id) {
  return request({
    url: url + `/rde/variable/group/list?themeId=${id}`,
    method: 'post',
    // data
  })
}


//提交新增或者修改对象数据
export function submitVariableGroup(data) {
  return request({
    url: url + `/rde/variable/group/submit`,
    method: 'post',
    data
  })
}

//通过id获取对象数据
export function getVariableGroupById(id) {
  return request({
    url: url + `/rde/variable/group/${id}`,
    method: 'get'
  })
}
//通过id删除对象数据
export function delVariableGroupById(id) {
  return request({
    url: url + `/rde/variable/group/delete/${id}`,
    method: 'get'
  })
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType) {
  return request({
    url: url + '/system/dict/data/dictType/' + dictType,
    method: 'get'
  })
}

export function getVariableRecordList(pageSize, pageNum, groupId) {
  return request({
    url: url + `/rde/variable/record/list?pageSize=${pageSize}&pageNum=${pageNum}&groupId=${groupId}`,
    method: 'post',
  })
}
export function getVariableRecordById(id) {
  return request({
    url: url + `/rde/variable/record/${id}`,
    method: 'get'
  })
}
export function delVariableRecordById(id) {
  return request({
    url: url + `/rde/variable/record/delete/${id}`,
    method: 'get'
  })
}

export function submitVariableRecord(data) {
  return request({
    url: url + `/rde/variable/record/submit`,
    method: 'post',
    data
  })
}