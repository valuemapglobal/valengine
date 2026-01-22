/*
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2024-06-28 16:00:51
 * @LastEditTime: 2024-07-09 13:16:29
 * @LastEditors: Seastar 1507136388@qq.com
 * @Description:
 */
import request from '@/utils/request.js'

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

//发送短信验证码
export function sendSmsAuthCode(data) {
  return request({
    url: url + '/system/userInfo/sendSmsAuthCode',
    method: 'post',
    data,
  })
}
//获取个人认证状态
export function getUserAuth(data) {
  return request({
    url: url + '/system/userInfo/getUserAuth',
    method: 'post',
    data,
  })
}
//提交个人认证信息
export function userInfoSubmit(data) {
  return request({
    url: url + '/system/userInfo/submit',
    method: 'post',
    data,
  })
}
//获取个人认证信息
export function userInfoList(data) {
  return request({
    url: url + '/system/userInfo/list',
    method: 'post',
    data,
  })
}

//企业认证提交
export function authSubmit(data) {
  return request({
    url: url + '/system/userEnterprise/authSubmit',
    method: 'post',
    data,
  })
}

//隐私授权-个人-发起授权接口
export function launch(data) {
  return request({
    url: url + '/risk/accreditPerson/launch',
    method: 'post',
    data,
  })
}
//隐私授权-个人-查询授权情况接口
export function getAccreditPersonStatus(data) {
  return request({
    url: url + '/risk/accreditPerson/getAccreditPersonStatus',
    method: 'post',
    data,
  })
}

//隐私授权-个人-拒绝授权接口
export function refuseAccredit(data) {
  return request({
    url: url + '/risk/accreditPerson/refuseAccredit',
    method: 'post',
    data,
  })
}

//隐私授权-个人-c端回显信息接口
export function getPersonInfo(data) {
  return request({
    url: url + '/risk/accreditPerson/getPersonInfo',
    method: 'post',
    data,
  })
}
// 隐私授权-个人-c端发送短信接口
export function getVerificationCode(data) {
  return request({
    url: url + '/risk/accreditPerson/getVerificationCode',
    method: 'post',
    data,
  })
}

// 隐私授权-个人-c端验证短信接口
export function personThreeEleAuth(data) {
  return request({
    url: url + '/risk/accreditPerson/personThreeEleAuth',
    method: 'post',
    data,
  })
}
