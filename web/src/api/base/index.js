import request from "@/utils/request.js";

const url = process.env.VUE_APP_ENV === "production" ? "/vm" : "/dev-api/vm";

export function wxlogin(data) {
  return request({
    url: url + `/system/wx/weixinLogin?code=${data.code}`,
    method: "post",
    data,
  });
}

export function login(data) {
  return request({
    url: url + "/auth/login",
    method: "post",
    data,
  });
}

export function getInfo(token) {
  return request({
    url: url + "/vue-admin-template/user/info",
    method: "get",
    params: { token },
  });
}

export function logout() {
  return request({
    url: url + "/vue-admin-template/user/logout",
    method: "post",
  });
}

// 用户个人查询
export function getLoginInfo() {
  return request({
    url: url + "/system/user/getLoginInfo",
    method: "get",
  });
}

// 用户验证码登录
export function sendRegisterSms() {
  return request({
    url: url + "/vue-admin-template/user/sendRegisterSms",
    method: "post",
  });
}

// 新用户注册
export function registerUserCatch(data) {
  return request({
    url: url + "/system/user/registerUserCatch",
    method: "post",
    data: data,
  });
}

// 验证码登录发送短信验证码
export function sendLoginSms(data) {
  return request({
    url: url + "/system/user/sendLoginSms",
    method: "post",
    data: data,
  });
}

// 验证码登录
export function verifyLogin(data) {
  return request({
    url: url + "/system/login/verifyLogin",
    method: "post",
    data: data,
  });
  // return request({
  // 	url: url + '/system/user/verifyLogin',
  // 	method: 'post',
  // 	data: data,
  // })
}

// 忘记密码发送短信验证码
export function sendForgetPwdSms(data) {
  return request({
    url: url + "/system/user/sendForgetPwdSms",
    method: "post",
    data: data,
  });
}

// 重置密码
export function resetPwd(data) {
  return request({
    url: url + "/system/user/resetPwd",
    method: "post",
    data: data,
  });
}

// 企业查看
export function list(data) {
  return request({
    url: url + "/system/userEnterprise/list",
    method: "post",
    data,
  });
}

/**
 * 获取单个企业认证信息状态
 * @param data
 * @returns {*}
 */
export function getAuthStatusTag(data) {
  return request({
    url: url + "/system/userEnterprise/authStatusTag",
    method: "post",
    data,
  });
}

/**
 * 查询用户认证信息主状态
 * @param data
 * @returns {*}
 */
export function getAuthStatus(data) {
  return request({
    url: url + "/system/userEnterprise/authStatus",
    method: "post",
    data,
  });
}

// 近期消息
export function getlist(data) {
  return request({
    url: url + "/system/messageInside/list",
    method: "get",
    data,
  });
}

// 修改企业
export function submit(data) {
  return request({
    url: url + "/system/userEnterprise/submit",
    method: "post",
    data,
  });
}

// 修改密码
export function changePwd(data) {
  return request({
    url: url + "/system/user/changePwd",
    method: "post",
    data,
  });
}

// 修改个人信息
export function updateUser(data) {
  return request({
    url: url + "/system/user/updateUser",
    method: "post",
    data,
  });
}

// 删除近期消息
export function del() {
  return request({
    url: url + "/system/messageInside/delete",
    method: "post",
  });
}

// 企业认证提交
export function authSubmit(data) {
  return request({
    url: url + "/system/userEnterprise/authSubmit	",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    data,
  });
}

//企业金额认证
export function moneyCert(data) {
  return request({
    url: url + "/system/userEnterprise/moneyVaild",
    method: "post",
    data,
  });
}

// 个人认证提交
export function userSubmit(data) {
  return request({
    url: url + "/system/userInfo/submit",
    method: "post",
    data,
  });
}

export const initChatUserinfo = (data) => {
  return request({
    url: url + '/communication/chat/init/userinfo',
    method: 'post',
    data,
  })
}

//  完善企业信息提交
export function InformationSubmit(data) {
  return request({
    url: url + "/system/userInfo/informationSubmit  ",
    method: "post",
    data: data,
  });
}

//获取银行列表
export function bankList() {
  return request({
    url: url + "/system/basebank/list	",
    method: "get",
  });
}

//获取个人认证信息
export function userAuthInfo() {
  return request({
    url: url + "/system/userInfo/list",
    method: "post",
  });
}

//企业信息完善
export function infoComplate(data) {
  return request({
    url: url + "/system/userEnterprise/improveSubmit",
    method: "post",
    data,
  });
}

//获取企业详情
export function getCompanyDetail(params) {
  return request({
    url: url + "/risk/riskCompany/getCompanyDetail",
    method: "get",
    params,
  });
}

export const selectInstitution = (data) => {
  return request({
    url: `${url}/financing/tokerGuidance/selectInstitution`,
    method: "post",
    data
  })
}

/**
 * 专家——补充资料提交
 * @param data
 * @returns {*}
 */
export const institutionSubmit = (data) => {
	return request({
		url: `${url}/system/userAuthPersonFinancingExpert/submit`,
		method: "post",
		data
	})
}

/**
 * 专家——补充资料回显
 * @returns {*}
 */
export const getInstitutionInfo = () => {
  return request({
    url: `${url}/system/userAuthPersonFinancingExpert/getInfo`,
    method: "get"
  })
}

/**
 * 专家——更新补充资料
 * @param data
 * @returns {*}
 */
export const updateInstitutionInfo = (data) => {
  return request({
    url: `${url}/system/userAuthPersonFinancingExpert/update`,
    method: "post",
    data
  })
}
