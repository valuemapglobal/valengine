import { post, get } from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userInfo: {
    id: string
    username: string
    avatar?: string
    roles: string[]
    permissions: string[]
  }
}

export function login(data: LoginParams) {
  return post<LoginResult>('/auth/login', data)
}

export function logout() {
  return post('/auth/logout')
}

export function getUserInfo() {
  return get('/auth/user-info')
}
