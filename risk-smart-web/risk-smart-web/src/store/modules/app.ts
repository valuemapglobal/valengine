import { defineStore } from 'pinia'
import { setLocale, getLocale, type LocaleType } from '@/locales'

interface AppState {
  locale: LocaleType
  sidebarCollapsed: boolean
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    locale: getLocale(),
    sidebarCollapsed: false,
  }),

  actions: {
    setLocale(locale: LocaleType) {
      this.locale = locale
      setLocale(locale)
    },

    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
  },
})
