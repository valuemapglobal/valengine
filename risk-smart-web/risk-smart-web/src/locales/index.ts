import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'

export type LocaleType = 'zh-CN' | 'en-US'

// Get stored language or default to Chinese
function getStoredLocale(): LocaleType {
  const stored = localStorage.getItem('locale')
  if (stored === 'zh-CN' || stored === 'en-US') {
    return stored
  }
  // Auto detect browser language
  const browserLang = navigator.language
  if (browserLang.startsWith('zh')) {
    return 'zh-CN'
  }
  return 'en-US'
}

const i18n = createI18n({
  legacy: false, // Use Composition API mode
  locale: getStoredLocale(),
  fallbackLocale: 'en-US',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
})

export function setLocale(locale: LocaleType) {
  i18n.global.locale.value = locale
  localStorage.setItem('locale', locale)
  // Update HTML lang attribute
  document.documentElement.setAttribute('lang', locale)
}

export function getLocale(): LocaleType {
  return i18n.global.locale.value as LocaleType
}

export default i18n
