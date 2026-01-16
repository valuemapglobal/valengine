/**
 * 判断默认(标准)是否和当前账号deptId一致，一致则展示编辑按钮
 * @param deptId 默认(标准)对应deptId
 * @returns {boolean}
 */
export function isEditShow(deptId) {
  if (!deptId) return false
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  return userInfo.dept.deptId === deptId
}

/**
 * 判断当前账号是否为标准部门账号
 */
export function isStandardDept() {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  const decisionStandardList = JSON.parse(
    sessionStorage.getItem('decisionStandard')
  )
  if (decisionStandardList.indexOf(userInfo.dept.deptId) === -1) {
    // console.log("非标准部门");
    return false
  } else {
    // console.log("标准部门");
    return true
  }
}

import { lockStatus } from '../api/index'
export async function handleCheckLock() {
  let decision = this.$store.state.dataRisk.decision
  const res = await lockStatus({
    projectCode: decision.projectCode,
    businessCode: decision.businessCode,
    ruleCode: decision.ruleCode,
  })
  if (res.code == 200) {
    let data = res.data
    this.$store.dispatch('changeBatch', data)
    if (data.locked && !data.owner) {
      this.$message.warning('当前规则模型已锁定，请等待模型解锁后进行操作')
      return false
    }
    return true
  }
}
