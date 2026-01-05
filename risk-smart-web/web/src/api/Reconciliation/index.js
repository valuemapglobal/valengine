/**
 * @author chris
 * @date 2024/5/11 10:23
 */
import request from "@/utils/request";

const url = process.env.VUE_APP_ENV === "production" ? "/vm" : "/dev-api/vm";

/**
 * 2.4.10 根据部门ID获取全部用户信息列表
 * @param deptId
 * @returns {*}
 */
export function getUserList(deptId) {
  return request({
    url: `${url}/system/user/listByDeptIdHidden?listByDeptIdHidden=${deptId}`,
    method: "get",
  });
}

/**
 * 2.4.9 获取部门列表
 * @returns {*}
 */
export function treeSelectHidden() {
  return request({
    url: `${url}/system/dept/treeSelectHidden`,
    method: "get",
  })
}
/**
 * 计费统计列表数据展示
 * @param data
 * @returns {*}
 */
export function statisticsList(data) {
  return request({
    url:`${url}/risk/billingStatistics/statisticsList`,
    method: "post",
    data,
  })
}

/**
 * 3.1.2 计费统计-查询当天去重数
 * @param data
 * @returns {*}
 */
export function statisticsWeightQueryTotal(data) {
  return request({
    url:`${url}/risk/billingStatistics/statisticsWeightQueryTotal`,
    method: "post",
    data,
  })
}
/**
 * 3.1.2 计费统计列表数据导出(批量导出)
 * @param data
 * @returns {*}
 */
export function statisticsExcel(data) {
  return request({
    url: `${url}/risk/billingStatistics/statisticsExcel`,
    method: "post",
    data,
  });
}

/**
 * 3.1.3 计费统计列表数据展示 -- api
 * @param data
 * @returns {*}
 */
export function statisticsListApi(data) {
  return request({
    url:`${url}/risk/billingStatistics/statisticsListApi`,
    method: "post",
    data,
  })
}

/**
 * 3.1.4 计费统计列表数据导出(批量导出)  -- api
 * @param data
 * @returns {*}
 */
export function statisticsExcelApi(data) {
  return request({
    url:`${url}/risk/billingStatistics/statisticsExcelApi`,
    method: "post",
    data,
  })
}
