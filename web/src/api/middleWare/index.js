import request from "@/utils/request";

const url = process.env.VUE_APP_ENV === 'production' ? '/vm' : '/dev-api/vm'

//  获取登录权限信息
export function getAuthData(data) {
    return request({
        url: `${url}/auth/autoLogin`,
        method: 'post',
        data
    })
}