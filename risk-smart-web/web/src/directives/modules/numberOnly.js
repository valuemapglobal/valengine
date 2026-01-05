export default {
  /**
   * 限制只能输入正整数
   */
  bind(el, data) {
    el.handler = function () {
      let input = el.querySelector('input')
      input.value = input.value.replace(/\D+/, '')
    }
    el.addEventListener('input', el.handler)
  },
  unbind(el) {
    el.removeEventListener('input', el.handler)
  },
}
