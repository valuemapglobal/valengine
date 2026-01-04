export default {
  // Common
  common: {
    confirm: 'Confirm',
    cancel: 'Cancel',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    add: 'Add',
    search: 'Search',
    reset: 'Reset',
    back: 'Back',
    submit: 'Submit',
    loading: 'Loading...',
    noData: 'No Data',
    success: 'Success',
    failed: 'Failed',
    tip: 'Tip',
    warning: 'Warning',
    detail: 'Detail',
    yes: 'Yes',
    no: 'No',
    noMore: 'No more data...',
    pleaseEnter: 'Please enter',
  },

  // Login
  login: {
    title: 'RiskSmart',
    subtitle: 'Risk Management Platform',
    platformTitle: 'RiskSmart',
    platformSubtitle: 'Enterprise-level intelligent risk control decision platform, providing comprehensive risk assessment and real-time monitoring capabilities',
    username: 'Username',
    password: 'Password',
    rememberMe: 'Remember me',
    login: 'Login',
    loginSuccess: 'Login successful',
    loginFailed: 'Login failed',
    pleaseEnterUsername: 'Please enter username',
    pleaseEnterPassword: 'Please enter password',
    // New login page keys
    codeLogin: 'SMS Login',
    passwordLogin: 'Password Login',
    usernameOrPhone: 'Username / Phone',
    pleaseEnterUsernameOrPhone: 'Please enter username or phone',
    forgotPassword: 'Forgot Password',
    readAndAgree: 'I have read and agree to',
    userAgreement: 'User Agreement',
    privacyPolicy: 'Privacy Policy',
    and: 'and',
    pleaseAgreePolicy: 'Please agree to the user agreement and privacy policy',
    pleaseEnterPhone: 'Please enter phone number',
    invalidPhoneFormat: 'Invalid phone format',
    pleaseEnterCode: 'Please enter verification code',
    pleaseEnterValidPhone: 'Please enter a valid phone number',
    sendCode: 'Get Code',
    codeSent: 'Code sent',
    sendCodeFailed: 'Failed to send code',
    loginAgreement: 'By logging in, you agree to',
  },

  // Layout
  layout: {
    home: 'Home',
    profile: 'Profile',
    logout: 'Logout',
    logoutConfirm: 'Are you sure you want to logout?',
    account: 'Account',
  },

  // Menu
  menu: {
    dashboard: 'Dashboard',
    system: 'System',
    userManagement: 'User Management',
    roleManagement: 'Role Management',
    menuManagement: 'Menu Management',
    dictManagement: 'Dictionary',
    dataCenter: 'Data Center',
    decisionPlatform: 'Decision Platform',
    riskMonitor: 'Risk Monitor',
    rulePool: 'Rule Pool',
    // Interface Platform
    interfacePlatform: 'Interface Platform',
    dataSceneManagement: 'Data Scene Management',
    interfaceManagement: 'Interface Management',
    // Data Center sub-menus
    metadata: 'Metadata',
    featureVariable: 'Feature Variables',
    // Decision Platform sub-menus
    productModel: 'Product Model',
  },

  // Dashboard
  dashboard: {
    totalUsers: 'Total Users',
    riskAlerts: 'Risk Alerts',
    decisionsToday: 'Decisions Today',
    successRate: 'Success Rate',
    riskTrend: 'Risk Trend',
    recentAlerts: 'Recent Alerts',
    noAlerts: 'No Alerts',
  },

  // User
  user: {
    username: 'Username',
    nickname: 'Nickname',
    email: 'Email',
    phone: 'Phone',
    status: 'Status',
    role: 'Role',
    createTime: 'Created At',
    actions: 'Actions',
  },

  // Error pages
  error: {
    notFound: 'Page Not Found',
    backToHome: 'Back to Home',
    forbidden: 'Access Denied',
    serverError: 'Server Error',
  },

  // Settings
  settings: {
    language: 'Language',
    theme: 'Theme',
    lightMode: 'Light Mode',
    darkMode: 'Dark Mode',
  },

  // Decision Platform
  decision: {
    // Product
    pleaseEnterProductName: 'Please enter product name',
    deleteProductConfirm: 'Are you sure you want to delete this product?',
    // Business
    pleaseEnterBusinessName: 'Please enter business scenario name',
    deleteBusinessConfirm: 'Are you sure you want to delete this business scenario?',
    // Models
    ruleModel: 'Rule Model',
    classifyModel: 'Classification Model',
    scoreModel: 'Score Model',
    rateModel: 'Rating Model',
    limitModel: 'Limit Model',
    priceModel: 'Pricing Model',
    // Actions
    versionCompare: 'Version Compare',
    confirmRelease: 'Confirm Release',
    // Rule
    addRuleGroup: 'Add Rule Group',
    addRule: 'Add Rule',
    ruleName: 'Rule Name',
    ruleCode: 'Rule Code',
    priority: 'Priority',
    enabled: 'Enabled',
    disabled: 'Disabled',
    // Classify
    addClassify: 'Add Classification',
    classifyName: 'Classification Name',
    classifyCode: 'Classification Code',
    description: 'Description',
    // Score
    addScoreCard: 'Add Scorecard',
    scoreName: 'Scorecard Name',
    scoreCode: 'Scorecard Code',
    baseScore: 'Base Score',
    minScore: 'Min Score',
    maxScore: 'Max Score',
    configure: 'Configure',
    // Rate
    addRate: 'Add Rating',
    rateName: 'Rating Name',
    rateCode: 'Rating Code',
    scoreRange: 'Score Range',
    // Limit
    addLimit: 'Add Limit Strategy',
    limitName: 'Limit Strategy Name',
    limitCode: 'Limit Strategy Code',
    amountRange: 'Amount Range',
    // Price
    addPrice: 'Add Pricing Strategy',
    priceName: 'Pricing Strategy Name',
    priceCode: 'Pricing Strategy Code',
    baseRate: 'Base Rate',
    rateRange: 'Rate Range',
  },

  // Data Center
  dataCenter: {
    // Theme
    theme: 'Theme',
    themeName: 'Theme Name',
    packageName: 'Package Name',
    themeType: 'Theme Type',
    themeDetail: 'Theme Detail',
    analysisObject: 'Analysis Object',
    derivedObject: 'Derived Object',
    // Object
    object: 'Object',
    objectName: 'Object Name',
    objectCode: 'Object Code',
    objectType: 'Object Type',
    objectDetail: 'Object Detail',
    version: 'Version',
    // Variable
    viewProperties: 'View Properties',
    propertyName: 'Property Name',
    propertyCode: 'Property Code',
    propertyType: 'Property Type',
    required: 'Required',
  },

  // Interface Platform
  interface: {
    // Data Source
    sourceName: 'Data Source Name',
    pleaseEnterSourceName: 'Please enter data source name',
    deleteSourceConfirm: 'Are you sure you want to delete this data source?',
    addSource: 'Add Data Source',
    description: 'Description',
    pleaseEnterDescription: 'Please enter description',
    selectInterface: 'Please select an interface',
    // Interface Info
    interfaceId: 'Interface ID',
    open: 'Open',
    close: 'Close',
    // Tabs
    productDetail: 'Product Details',
    businessParams: 'Business Parameters',
    returnCodes: 'Return Codes',
    interfaceTest: 'Interface Test',
    // Content
    productFeatures: 'Product Features',
    requestParams: 'Request Parameters',
    responseParams: 'Response Parameters',
    statusCodes: 'Status Codes',
    responseResult: 'Response Result',
    // Table columns
    paramName: 'Parameter Name',
    dataType: 'Data Type',
    paramDesc: 'Description',
    paramRemark: 'Remark',
    codeName: 'Code',
    codeDesc: 'Description',
    // Test
    test: 'Test',
    noParams: 'No parameters',
    noResult: 'No result',
  },
}
