/*
 * @Date: 2023-10-09 12:00:08
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2023-10-23 11:17:23
 * @FilePath: \ys-platform-h5d:\work\yaosu\ys-platform-manage\src\utils\tool.js
 */
const map = new Map()

/**
 *  防抖
 * @param {*} wait 等待时间
 * @returns     防抖函数
 */
export function debounce(wait = 100) {
  if (!map.has(wait)) {
    //只会初始化时执行一次
    let timeout,
      flag = true
    map.set(wait, function (func) {
      const context = this
      if (flag) {
        //多次触发则清除上一个定时器
        flag = false
        func.apply(context)
        if (!timeout) {
          timeout = setTimeout(() => {
            timeout = null
            flag = true
          }, wait)
        }
      }
    })
  }
  return map.get(wait)
}
