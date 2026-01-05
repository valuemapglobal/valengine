/*
 * @Date: 2023-10-11 10:07:18
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2023-10-13 09:24:29
 * @FilePath: \vm-micro-middleground\src\store\dataRisk\index.js
 */
export default {
  state: {
    //模型决策
    //名称为对应的缩写
    modelDecision: {
      currentProduct: 'yhls', //数智决策默认产品
      currentModule: 'ysh', //产品下当前启用的模块
      currentFun: 'rule', //当前启用模块下启用的功能
    },
    config: {
      projectCode: '1001',
      ruleCode: 0,
    },
    map: {
      projectCode: new Map([
        ['yhls', '1001'],
        ['zfls', '1002'],
      ]),
      ruleCode: new Map([
        ['rule', 0],
        ['classify', 1],
        ['tags', 2],
      ]),
    },
    batch: {
      batchId: null,
      owner: null,
    },

    //产品决策
    decision: {
      projectCode: null, //左侧产品导航标识
      businessCode: 1, //业务场景导航标识
      businessName: null, //业务场景导航标识
      ruleCode: 1, //策略类型导航标识
      moduleName: null, //模块名称
    },
    business: null,
    productList: [], // 产品列表
  },
  getters: {
    batchId: (state) => {
      return state.batch.batchId
    },
    isOwner: (state) => {
      return state.batch.owner
    },
  },
  mutations: {
    SET_MODELDECISION(state, data) {
      state.modelDecision = { ...state.modelDecision, ...data }
    },
    SET_PRODUCTDECISION(state, data) {
      state.decision = { ...state.decision, ...data }
    },
    SET_BUSINESS(state, data) {
      state.business = { ...state.business, ...data }
    },
    SET_CONFIG(state, data) {
      state.config = { ...state.config, ...data }
    },
    handleConfig(state, data) {
      if (data.hasOwnProperty('currentFun')) {
        state.config = {
          ...state.config,
          ruleCode: state.map['ruleCode'].get(data.currentFun),
        }
      }
    },
    SET_PRODUCTLIST(state, data) {
      state.productList = data
    },
    SET_BATCH(state, data) {
      state.batch = { ...state.batch, ...data }
    },
  },
  actions: {
    changeModel({ state, commit }, data) {
      let handleData = state.modelDecision
      let obj = {}
      if (typeof data === 'object') {
        // debugger
        for (let key in handleData) {
          if (data.hasOwnProperty(key)) {
            // this.$set(obj, key, data[key])
            obj[key] = data[key]
          }
        }
      }
      commit('SET_MODELDECISION', obj)
      commit('handleConfig', obj)
    },

    changeBusiness({ commit }, data) {
      commit('SET_BUSINESS', data)
    },

    changeProductDecision({ state, commit }, data) {
      let decision = state.decision
      let obj = {}
      if (typeof data === 'object') {
        for (let key in decision) {
          if (data.hasOwnProperty(key)) {
            obj[key] = data[key]
          }
        }
      }
      commit('SET_PRODUCTDECISION', obj)
    },
    getProList({ state, commit }, data) {
      commit('SET_PRODUCTLIST', data)
    },
    changeBatch({ state, commit }, data) {
      let batch = state.batch
      let obj = {}
      if (typeof data === 'object') {
        for (let key in batch) {
          if (data.hasOwnProperty(key)) {
            obj[key] = data[key]
          }
        }
      }

      commit('SET_BATCH', obj)
    },
  },
}
