import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import i18n from './i18n'

Vue.config.productionTip = false

import '@/fontsize/iconfont.css'
import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Element.DropdownMenu.props.appendToBody = false
Element.Cascader.props.appendToBody = false
Element.Select.props.popperAppendToBody = false

Vue.use(Element, {
  size: 'small',
  zIndex: 3000,
})

import JsonViewer from 'vue-json-viewer'
Vue.use(JsonViewer)

import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

import './directives/index' // 全局注册自定义指令

import { download } from '@/utils/request.js'
import CryptoJS from 'crypto-js'

Vue.prototype.download = download
//混入全局方法
Vue.mixin({
  methods: {
    isEnglish() {
      // 判断当前是否为英文
      return this.$i18n.locale === 'en'
    },
    //判断按钮是否有权限
    hasButton(buttonCode) {
      const buttonList = store.state.buttonList
      // 处理管理员通配符权限
      if (buttonList.includes('*:*:*')) {
        return true
      }
      return buttonList.includes(buttonCode)
    },
    //金额千分位分割
    formatMoney(money) {
      if (money || money === 0) {
        // 将金额拆分成整数部分和小数部分
        const parts = money.toString().split('.')
        const integerPart = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',')
        const decimalPart = parts.length > 1 ? `.${parts[1]}` : ''

        // 合并整数部分和小数部分
        return `${integerPart}${decimalPart}`
      }
      return money ? money : '0'
    },
    //设置表格头样式
    tableHeaderColor() {
      return 'background-color: #F7FAFE; color: rgba(0,0,0,0.85); font-size: 14px; font-weight: 400;'
    },
    //设置智能拓客中的单元格样式
    IntelligentExtensionRowStyle(background = '#ffffff') {
      return {
        'font-size': '14px',
        'font-weight': 400,
        color: 'rgba(0,0,0,0.85)',
        background: background,
      }
    },

    /**
     * 设置敏感词加密
     * @param value 需要处理的值
     * @param firstIndex 第一个截取下标
     * @param lastIndex 最后截取下标
     * @param substitute 要替换的字符串，如****
     * @returns {string}
     */
    setSensitiveWord(value, firstIndex, lastIndex, substitute) {
      let startValue = '',
        endValue = ''
      if (value) {
        startValue = value.slice(0, firstIndex)
        endValue = value.slice(value.length - lastIndex)
      }
      return startValue + substitute + endValue
    },
    /**
     * 设置文字省略号
     * @param text 对应文本
     * @param len 最大长度
     * @param more 是否已经展示全部文本
     */
    setEllipsisFun(text, len, more) {
      if (text && text.length >= len && !more) {
        return text.slice(0, len) + '...'
      } else {
        return text
      }
    },
    /**
     * AES加密方法
     * @param data
     */
    setAesString(data) {
      const key = CryptoJS.enc.Utf8.parse(process.env.VUE_APP_AES_KEY)
      const iv = CryptoJS.enc.Utf8.parse(process.env.VUE_APP_AES_IV)
      const dataHex = CryptoJS.enc.Utf8.parse(data)
      const encrypted = CryptoJS.AES.encrypt(dataHex, key, {
        iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
      })
      return encrypted.ciphertext.toString().toUpperCase()
    },
    /**
     * AES解密方法
     * @param data
     * @returns {string}
     */
    getAesString(data) {
      const key = CryptoJS.enc.Utf8.parse(process.env.VUE_APP_AES_KEY)
      const iv = CryptoJS.enc.Utf8.parse(process.env.VUE_APP_AES_IV)
      const encryptedData = CryptoJS.enc.Hex.parse(data) // 十六进制字符串
      const decrypted = CryptoJS.AES.decrypt(
        { ciphertext: encryptedData },
        key,
        {
          iv,
          mode: CryptoJS.mode.CBC,
          padding: CryptoJS.pad.Pkcs7,
        }
      )

      return decrypted.toString(CryptoJS.enc.Utf8) // 转换为UTF-8字符串
    },
  },
})
Vue.prototype.$getBrowserInfo = () => {
  let name = null
  var ua = navigator.userAgent
  let chrome = ua.indexOf('Chrome')
  let firefox = ua.indexOf('Firefox')
  if (chrome !== -1 && ua.split(' ').length === 12) {
    return true
  }
  if (firefox !== -1) {
    return true
  }
  return false
}

//防止重复点击
Vue.directive('preventReClick', {
  inserted: function (el, binding) {
    el.addEventListener('click', () => {
      if (el.style['pointer-events'] !== 'none') {
        el.style['pointer-events'] = 'none'
        setTimeout(() => {
          el.style['pointer-events'] = 'auto'
        }, binding.value || 5000)
      }
    })
  },
})

// 添加水印
Vue.directive('watermark', {
  // 当被绑定的元素插入到 DOM 中时……
  inserted: function (el, node) {
    //如果有传参，并且值为false则不展示水印
    if (node.expression && !node.value) {
      return
    }
    el.style.position = 'relative'
    let div = document.createElement('div')
    div.className = 'watermark'
    el.appendChild(div)
  },
})

// 判断是否为新用户方法封装
Vue.prototype.$isNewUser = (guideType) => {
  const isVistor = JSON.parse(localStorage.getItem('isNewVisitor'))
  let flag = store.state.newGuideProcess.includes(guideType)
  if (isVistor && !flag) {
    return true
    // 如果是新用户的话,弹出新手引导页面
    // store.commit('changeNoviceGuide', guideType)
  } else {
    return false
  }
}

new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App),
}).$mount('#app')
