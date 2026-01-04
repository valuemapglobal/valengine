import { defineStore } from 'pinia'

interface UserInfo {
  id: string
  username: string
  avatar?: string
  roles: string[]
  permissions: string[]
}

interface UserState {
  token: string | null
  userInfo: UserInfo | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token'),
    userInfo: null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    permissions: (state) => state.userInfo?.permissions ?? [],
  },

  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(info: UserInfo) {
      this.userInfo = info
    },

    logout() {
      this.token = null
      this.userInfo = null
      localStorage.removeItem('token')
    },

    hasPermission(code: string): boolean {
      return this.permissions.includes(code)
    },
  },
})
