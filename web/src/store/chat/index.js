/*
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2024-04-15 14:48:51
 * @LastEditTime: 2024-04-15 14:50:46
 * @LastEditors: Seastar 1507136388@qq.com
 * @Description:
 * @FilePath: \vm-micro-middleground\src\store\chat\index.js
 */
export default {
  state: {
    historyChatList: [],
  },
  mutations: {
    setHistoryChatList(state, data) {
      state.historyChatList = data
    },
  },
  getters: {
    historyChatList: (state) => state.historyChatList,
  },
}
