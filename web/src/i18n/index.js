import Vue from 'vue'
import VueI18n from 'vue-i18n'
import zh from './locales/zh'
import en from './locales/en'

Vue.use(VueI18n)

// 从 localStorage 获取保存的语言设置，默认为中文
const locale = localStorage.getItem('locale') || 'zh'

const i18n = new VueI18n({
  locale,
  fallbackLocale: 'zh',
  messages: {
    zh,
    en,
  },
})

export default i18n
