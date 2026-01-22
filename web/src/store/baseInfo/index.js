/*
 * @Date: 2022-08-30 15:44:00
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-09-14 10:51:03
 * @name:
 * @FilePath: /gutuProject/vmkj/src/store/kyc/kyc.js
 */
export default {
  state: {
    baseInfo: '',
    reset: null,
  },
  mutations: {
    setBaseInfo(state, data) {
      state.baseInfo = data
    },
    setReset(state, data) {
      state.reset = data
    },
  },
}
