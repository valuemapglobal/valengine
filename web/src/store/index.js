/*
 * @Date: 2022-07-11 11:20:41
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-04-15 14:14:41
 * @name:
 * @FilePath: \vm-micro-middleground\src\store\index.js
 */
import Vue from 'vue'
import Vuex from 'vuex'
import baseInfo from './baseInfo'
import chat from './chat'
import dataRisk from './dataRisk'
import breadcrumbs from './module/breadcrumbs.module'
import monitorDaily from './monitorDaily'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    theme: 'light', //主题 light 亮色  dark 暗色
    mouseLocation: {},
    nodeDetailView: {
      companyName: '',
      legalPersonName: '',
      registeredCapital: '',
      registrationTime: '',
    },
    isNodeshow: false,
    isScreenFull: true, //股东图的大屏展示
    newGuideProcess:
      JSON.parse(localStorage.getItem('NewGuideProcessList')) || [],
    newgiudeType: null,
    personTabs: '', //个人尽调卡片
    bussinessOrindividual: JSON.parse(
      localStorage.getItem('BussinessOrindividual')
    ), //判断现在是企业尽调还是个人尽调
    invistePersonInfo: {}, //个人尽调qi
    invistePersonId: '', //个人尽调id
    userInfo: null, //用户信息
    userAuthInfo: null, //用户补充资料信息
    benefitAccount: null, //用户权益
    userPreference: {
      list: [],
      status: 0,
    },
    companyinfo: null,
    buttonList: [], //按钮权限列表
    AllOcrObj: {}, //返回的所有Ocr信息
    OcrErrList: [], //[ocr错误信息;列表]
    OcrContrast: [],
    domCave: [], //存储ocr修改变色值
    oCRmark: true, //ocr修改值变化
    modelData: {},
    chooseValue: 0, //选改项
    mustValue: 0, //必改项
    orderNumber: null, //流水号
    Emaildictionary: [], //邮件字典存储
    from: {
      industry: '',
      category: '',
      format: '',
      target: '总店数',
      targets: '门店数量排行榜',
      time: '',
      pro: '',
      city: '',
      country: '',
      employed: '全量',
      brand_name: '',
    },
    headerDelicer: {},
    routers: [],
    workflow: {},
    timeRange: {
      maxYear: new Date().getFullYear(),
      maxMonth: new Date().getMonth() + 1,
      minYear: new Date().getFullYear() - 1,
      minMonth: new Date().getMonth() + 1,
    },
    accountList: [],
    companyDueIsFullScreen: false,
    isBankflow: true, //默认为银行流水
  },
  getters: {
    timeRange: (state) => state.timeRange,
    userInfo: (state) => state.userInfo,
    benefitAccount: (state) => state.benefitAccount,
    userPreference: (state) => state.userPreference,
    userAuthInfo: (state) => state.userAuthInfo,
    accountList: (state) => state.accountList,
    companyDueIsFullScreen: (state) => state.companyDueIsFullScreen,
    idNumber: (state) => {
      if (state.invistePersonInfo.idNumber) {
        return state.invistePersonInfo.idNumber
      }
    },
    personName: (state) => {
      if (state.invistePersonInfo.name) {
        return state.invistePersonInfo.name
      }
    },
  },
  mutations: {
    changeScreenFull(state, data) {
      state.isScreenFull = data
    },
    // 改变个人尽调颜色
    changeInvistePerson(state, data) {
      state.invistePersonInfo = data
    },
    changeInvistePersonId(state, data) {
      state.invistePersonId = data
    },
    changeChooseValue(state, data) {
      state.chooseValue = data
    },
    changeOrderNumber(state, data) {
      state.orderNumber = data
    },
    // 进行Ocr列表的对比
    changeOcrContrast(state, data) {
      state.OcrContrast = data
    },
    changeMustValue(state, data) {
      state.mustValue = data
    },
    changeDomCave(state, data) {
      state.domCave = data
    },
    changeOcrMark(state, data) {
      state.oCRmark = data
    },
    changeOcrErrList(state, data) {
      state.OcrErrList = data
    },
    changeAllOcrObj(state, data) {
      state.AllOcrObj = data
    },
    changeHeader(state, data) {
      state.headerDelicer = data
    },
    setRouters(state, data) {
      // let AuthorityData = Authority(data)
      state.routers = data
    },
    setWorkflow(state, workflow) {
      state.workflow = workflow
    },
    setUserInfo(state, data) {
      state.userInfo = data
    },
    setUserAuthInfo(state, data) {
      state.userAuthInfo = data
    },
    setUserPreference(state, data) {
      state.userPreference = { ...data }

      let userPer = data.list.find((item) => item.function === 'hy')

      if (userPer && userPer.userPreference) {
        let { procode, citycode, countrycode, industry, pro, city, country } =
          userPer.userPreference
        let userAttention = {
          procode,
          citycode,
          industry,
          pro,
          city,
        }
        if (country !== '市辖区') {
          Object.assign(userAttention, {
            country,
            countrycode,
          })
        }
        localStorage.setItem('userAttention', JSON.stringify(userAttention))
      }
    },
    setBenefitAccount(state, data) {
      state.benefitAccount = data
    },
    setCompanyinfo(state, data) {
      state.companyinfo = data
    },
    changeData(state, data) {
      state.modelData = data
    },
    chageBussinessOrindividual(state, data) {
      state.bussinessOrindividual = data
      localStorage.setItem('BussinessOrindividual', JSON.stringify(data))
    },
    setNewGuideProcess(state, data) {
      state.newGuideProcess.push(data)
      state.newGuideProcess = Array.from(new Set(state.newGuideProcess))
      localStorage.setItem(
        'NewGuideProcessList',
        JSON.stringify(state.newGuideProcess)
      )
    },
    sendData(state, data) {
      state.from.industry = data.industry == null ? '' : data.industry
      state.from.category = data.category == null ? '' : data.category
      state.from.format = data.format == null ? '' : data.format
      state.from.target = data.target == null ? '总店数' : data.target
      state.from.time = data.time == null ? '' : data.time
      state.from.pro = data.pro == null ? '' : data.pro
      state.from.country = data.country == null ? '' : data.country
      state.from.city = data.city == null ? '' : data.city
      state.from.employed = data.employed == null ? '' : data.employed
      state.from.brand_name = data.brand_name == null ? '' : data.brand_name
    },
    setButtonList(state, payload) {
      state.buttonList = payload
    },
    SET_TIME_RANGE(state, payload) {
      state.timeRange = payload
    },
    SET_ACCOUNT_LIST(state, payload) {
      state.accountList = payload
    },
    SET_CompanyDueIsFullScreen(state, payload) {
      state.companyDueIsFullScreen = payload
    },
    // 修改邮件存储
    setEmaildictionary(state, data) {
      state.Emaildictionary = data
    },
    // 切换个人尽调卡片
    changePersonTabs(state, data) {
      state.personTabs = data
    },
    //改变新手页面
    changeNoviceGuide(state, data) {
      state.newgiudeType = data
    },
    SET_BankFlowType(state, data) {
      state.isBankflow = data
    },
    // 改变法人姓名
    changeNodeDetailView(state, data) {
      state.nodeDetailView = data
    },
    // 控制弹框节点的展示
    changeNodeshow(state, data) {
      state.isNodeshow = data
    },
    // 穿透图坐标
    changeMouseLocation(state, data) {
      state.mouseLocation = data
    },

    SET_THEME(state, theme) {
      state.theme = theme
      localStorage.setItem('theme', theme)
      document.documentElement.className = theme
    },
  },
  actions: {
    setTimeRange({ commit }, payload) {
      commit('SET_TIME_RANGE', payload)
    },
    setAccountList({ commit }, payload) {
      commit('SET_ACCOUNT_LIST', payload)
    },

    setTheme({ commit }, theme) {
      commit('SET_THEME', theme)
    },
    initTheme({ commit }) {
      const savedTheme = localStorage.getItem('theme')
      if (savedTheme) {
        commit('SET_THEME', savedTheme)
      }
    },
  },
  modules: { breadcrumbs, baseInfo, monitorDaily, dataRisk, chat },
})
