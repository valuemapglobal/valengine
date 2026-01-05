/*
 * @Date: 2022-03-24 10:52:52
 * @LastEditors: Do not edit
 * @LastEditTime: 2023-02-02 20:33:22
 * @name:
 * @FilePath: \vmkj\src\utils\request.js
 */
import axios from "axios"
import { blobValidate, tansParams } from "@/utils/rouyi";
import { Notification, Loading, Message } from "element-ui"
import errorCode from "@/utils/errorCode"
import { saveAs } from "file-saver";
import { getToken } from "@/utils/auth"

axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8"
// 创建axios实例
let downloadLoadingInstance;
const service = axios.create({
	// axios中请求配置有baseURL选项，表示请求URL公共部分
	// baseURL: process.env.VUE_APP_BASE_API,
	// 超时
	// transformResponse: [
	// 	function (data) {
	// 		// Do whatever you want to transform the data
	// 		return JSONbig.parse(data)
	// 	},
	// ],
	timeout: 600000
})
// request拦截器
service.interceptors.request.use(
	(config) => {
		if (getToken()) {
			// config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
			config.headers.Authorization = getToken()
		}
		// if (config.method === 'post') {
		// config.data = JSON.stringify(config.data)
		// }
		// 发送请求之前做某件事
		return config
	},
	(error) => {
		console.log(error)
		Promise.reject(error)
	}
)

// 请求地址白名单不会报错 ['数据埋点上报接口地址']
const noErrorMsgList = ["/vm/markdown/event/save_event"]

// 是否关闭错误信息
function isNoErrorMsg(url) {
	if (noErrorMsgList.some((i) => url.indexOf(i) > -1)) {
		return true
	} else {
		return false
	}
}

// 响应拦截器
service.interceptors.response.use(
	(res) => {
		if (res.data) {
			// 未设置状态码则默认成功状态
			if (res.data.data) {
				if (res.data.data.access_token) {
					localStorage.setItem("id_token", res.data.data.access_token)
				}
			}
		}

		const code = res.data.code || 200
		// 获取错误信息
		const msg = errorCode[code] || res.data.msg || errorCode.default

		switch (code) {
			case 600:
				// 统一跳到登录页
				window.location.href = "/#/login"
				return
			case 601:
				// 跳转到完善信息
				window.location.href = "/#/login?type=6"
				return
			case 602:
				// 跳转到联系工作人员
				Message.error("暂无权限，请联系管理员")
				window.location.href = "/#/login?type=1"
				return
			case 401:
				// 登录过期，清空token跳转到首页
				localStorage.removeItem("id_token")
				window.location.href = "/#/"
				window.location.reload()
				return
		}
		// 接口白名单
		const whiteApiList = ["/vm/risk/kyc/uploadExcel"]
		if (whiteApiList.some((i) => res.request.responseURL.indexOf(i) > -1)) {
			return res.data
		}
		if (isNoErrorMsg(res.request.responseURL)) {
			console.debug("关闭响应报错信息")
			return res.data
		}

		if (code === 500) {
			Message.closeAll() //手动关闭所有消息提示实例
			Message({
				message: msg,
				type: "error"
			})
			return Promise.reject(new Error(msg))
		} else if (code !== 200 && code !== 701 && code !== "0" && code !== 1001 && code !== 201) {
			Notification.error({
				title: msg
			})
			return Promise.reject("error")
		} else {
			return res
		}
	},
	(error) => {
		console.log("拦截错误err", error)
		let {
			message,
			request: { responseURL }
		} = error

		if (isNoErrorMsg(responseURL)) {
			console.debug("关闭响应报错信息")
			return Promise.reject(error)
		}

		if (message == "Network Error") {
			message = "后端接口连接异常"
		} else if (message.includes("timeout")) {
			message = "系统接口请求超时"
		} else if (message.includes("Request failed with status code")) {
			message = "系统接口" + message.substr(message.length - 3) + "异常"
		}
		Message({
			message: message,
			type: "error",
			duration: 5 * 1000
		})
		return Promise.reject(error)
	}
);

// 通用下载方法
export function download(url, params, filename) {
	downloadLoadingInstance = Loading.service({
		text: "正在下载数据，请稍候",
		spinner: "el-icon-loading",
		background: "rgba(0, 0, 0, 0.7)"
	})
	return service.post(url, params, {
		transformRequest: [(params) => {
			return tansParams(params)
		}],
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		},
		responseType: "blob"
	}).then(async (data) => {
		const isLogin = await blobValidate(data);
		if (isLogin) {
			const blob = new Blob([data])
			saveAs(blob, filename)
		} else {
			Message.error("无效的会话，或者会话已过期，请重新登录。");
		}
		downloadLoadingInstance.close();
	}).catch((r) => {
		console.error(r)
		Message.error("下载文件出现错误，请联系管理员！")
		downloadLoadingInstance.close();
	})
}

export default service
