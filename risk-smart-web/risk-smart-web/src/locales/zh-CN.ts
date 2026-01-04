export default {
  // Common
  common: {
    confirm: '确认',
    cancel: '取消',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    add: '添加',
    search: '搜索',
    reset: '重置',
    back: '返回',
    submit: '提交',
    loading: '加载中...',
    noData: '暂无数据',
    success: '操作成功',
    failed: '操作失败',
    tip: '提示',
    warning: '警告',
    detail: '详情',
    yes: '是',
    no: '否',
    noMore: '没有更多了...',
    pleaseEnter: '请输入',
  },

  // Login
  login: {
    title: 'RiskSmart',
    subtitle: '风险管理平台',
    platformTitle: 'RiskSmart',
    platformSubtitle: '企业级智能风控决策平台，提供全方位的风险评估与实时监控能力',
    username: '用户名',
    password: '密码',
    rememberMe: '记住我',
    login: '登录',
    loginSuccess: '登录成功',
    loginFailed: '登录失败',
    pleaseEnterUsername: '请输入用户名',
    pleaseEnterPassword: '请输入密码',
    // New login page keys
    codeLogin: '验证码登录',
    passwordLogin: '密码登录',
    usernameOrPhone: '用户名/手机号',
    pleaseEnterUsernameOrPhone: '请输入用户名或手机号',
    forgotPassword: '忘记密码',
    readAndAgree: '我已阅读并同意',
    userAgreement: '《用户协议》',
    privacyPolicy: '《隐私政策》',
    and: '和',
    pleaseAgreePolicy: '请先同意用户协议和隐私政策',
    pleaseEnterPhone: '请输入手机号',
    invalidPhoneFormat: '手机号格式不正确',
    pleaseEnterCode: '请输入验证码',
    pleaseEnterValidPhone: '请输入正确的手机号',
    sendCode: '获取验证码',
    codeSent: '验证码已发送',
    sendCodeFailed: '发送验证码失败',
    loginAgreement: '登录即表示同意',
  },

  // Layout
  layout: {
    home: '首页',
    profile: '个人中心',
    logout: '退出登录',
    logoutConfirm: '确定要退出登录吗？',
    account: '账户',
  },

  // Menu
  menu: {
    dashboard: '仪表盘',
    system: '系统管理',
    userManagement: '用户管理',
    roleManagement: '角色管理',
    menuManagement: '菜单管理',
    dictManagement: '字典管理',
    dataCenter: '数据中心',
    decisionPlatform: '决策平台',
    riskMonitor: '风险监控',
    rulePool: '规则池',
    ruleStrategyList: '规则策略列表',
    platformEngine: '平台引擎',
    workflowEngine: '流程管理',
    taskApproval: '任务审批',
    taskRecords: '任务记录',
    // Interface Platform
    interfacePlatform: '接口平台',
    dataSceneManagement: '数据场景管理',
    interfaceManagement: '接口管理',
    // Data Center sub-menus
    metadata: '元数据',
    featureVariable: '特征变量',
    // Decision Platform sub-menus
    productModel: '产品模型',
  },

  // Dashboard
  dashboard: {
    totalUsers: '用户总数',
    riskAlerts: '风险预警',
    decisionsToday: '今日决策',
    successRate: '成功率',
    riskTrend: '风险趋势',
    recentAlerts: '最近预警',
    noAlerts: '暂无预警',
  },

  // User
  user: {
    username: '用户名',
    nickname: '昵称',
    email: '邮箱',
    phone: '手机号',
    status: '状态',
    role: '角色',
    createTime: '创建时间',
    actions: '操作',
  },

  // Error pages
  error: {
    notFound: '页面未找到',
    backToHome: '返回首页',
    forbidden: '无权限访问',
    serverError: '服务器错误',
  },

  // Settings
  settings: {
    language: '语言',
    theme: '主题',
    lightMode: '浅色模式',
    darkMode: '深色模式',
  },

  // Decision Platform
  decision: {
    // Product
    pleaseEnterProductName: '请输入产品名称',
    deleteProductConfirm: '确定要删除该产品吗？',
    // Business
    pleaseEnterBusinessName: '请输入业务场景名称',
    deleteBusinessConfirm: '确定要删除该业务场景吗？',
    // Models
    ruleModel: '规则模型',
    classifyModel: '分类模型',
    scoreModel: '评分模型',
    rateModel: '评级模型',
    limitModel: '额度模型',
    priceModel: '定价模型',
    // Actions
    versionCompare: '版本对比',
    confirmRelease: '确认发布',
    // Rule
    addRuleGroup: '新增规则组',
    addRule: '新增规则',
    ruleName: '规则名称',
    ruleCode: '规则编码',
    priority: '优先级',
    enabled: '启用',
    disabled: '禁用',
    // Classify
    addClassify: '新增分类',
    classifyName: '分类名称',
    classifyCode: '分类编码',
    description: '描述',
    // Score
    addScoreCard: '新增评分卡',
    scoreName: '评分卡名称',
    scoreCode: '评分卡编码',
    baseScore: '基础分',
    minScore: '最低分',
    maxScore: '最高分',
    configure: '配置',
    // Rate
    addRate: '新增评级',
    rateName: '评级名称',
    rateCode: '评级编码',
    scoreRange: '分数范围',
    // Limit
    addLimit: '新增额度策略',
    limitName: '额度策略名称',
    limitCode: '额度策略编码',
    amountRange: '额度范围',
    // Price
    addPrice: '新增定价策略',
    priceName: '定价策略名称',
    priceCode: '定价策略编码',
    baseRate: '基础利率',
    rateRange: '利率范围',
  },

  // Data Center
  dataCenter: {
    // Theme
    theme: '主题',
    themeName: '主题名称',
    packageName: '包名称',
    themeType: '主题类型',
    themeDetail: '主题详情',
    analysisObject: '分析对象',
    derivedObject: '衍生对象',
    // Object
    object: '对象',
    objectName: '对象名称',
    objectCode: '对象Code',
    objectType: '对象类型',
    objectDetail: '对象详情',
    version: '版本号',
    // Variable
    viewProperties: '查看属性',
    propertyName: '属性名称',
    propertyCode: '属性编码',
    propertyType: '属性类型',
    required: '必填',
  },

  // Interface Platform
  interface: {
    // Data Source
    sourceName: '数据场景名称',
    pleaseEnterSourceName: '请输入数据场景名称',
    deleteSourceConfirm: '确定要删除该数据场景吗？',
    addSource: '新增数据场景',
    description: '描述',
    pleaseEnterDescription: '请输入描述',
    selectInterface: '请选择接口',
    // Interface Info
    interfaceId: '接口ID',
    open: '开启',
    close: '关闭',
    // Tabs
    productDetail: '产品详情',
    businessParams: '业务参数',
    returnCodes: '返回码',
    interfaceTest: '接口测试',
    // Content
    productFeatures: '产品特点',
    requestParams: '产品请求参数',
    responseParams: '产品响应结果',
    statusCodes: '返回状态码',
    responseResult: '响应结果',
    // Table columns
    paramName: '参数名称',
    dataType: '数据类型',
    paramDesc: '参数说明',
    paramRemark: '参数备注',
    codeName: '名称',
    codeDesc: '说明',
    // Test
    test: '测试',
    noParams: '暂无参数',
    noResult: '暂无结果',
  },
}
