<template>
  <div>
    <div
      class="nav_bar"
      :class="isStatic ? 'nav_bar_static' : isFixed ? 'nav_bar_fixed' : ''"
      v-if="isShow()"
      @click="navHandle(true)"
    >
      <div class="nav_bar_left">
        <img class="nav_bar_logo" :src="getLogo" alt="" />
        <span class="logo-text">ValEngine</span>

        <el-menu
          :default-active="activeName"
          @select="handleSelect"
          :background-color="navBackground"
          :text-color="navTextColor"
          :active-text-color="activeTextColor"
          class="el-menu-demo"
          mode="horizontal"
        >
          <template v-for="(nav, i) in navList">
            <el-menu-item
              :index="nav.name"
              :key="i"
              v-if="!nav.values || (nav.isChange && nav.values)"
              :disabled="nav.disabled"
              :class="{ 'is-active': activeIndex === i }"
            >
              {{ nav.label }}
            </el-menu-item>
            <template v-else>
              <el-submenu
                :popper-append-to-body="false"
                :index="nav.name"
                :key="nav.name"
                v-if="
                  (hasButton(nav.show) || !nav.show) &&
                  nav.values &&
                  nav.values.length
                "
              >
                <template slot="title">{{ nav.label }} </template>
                <template v-for="(navChild, navChildIndex) in nav.values">
                  <el-menu-item
                    v-if="hasButton(navChild.show) || !navChild.show"
                    :index="navChild.name"
                    :key="navChildIndex"
                  >
                    <a
                      :href="navChild.url"
                      v-if="navChild.type"
                      target="_blank"
                      class="gutuLink"
                    >
                      {{ navChild.label }}
                    </a>
                    <span v-else>
                      {{ navChild.label }}
                    </span>
                  </el-menu-item>
                </template>
              </el-submenu>
              <el-menu-item v-if="nav.url" :index="nav.name" :key="nav.name">
                <a :href="nav.url" target="_blank">
                  {{ nav.label }}
                </a>
              </el-menu-item>
            </template>
          </template>
        </el-menu>
      </div>
      <div class="nav_bar_right">
        <template>
          <el-popover
            :visible-arrow="false"
            :popper-options="{ gpuAcceleration: true }"
            :append-to-body="false"
            :trigger="infoVisible ? 'click' : 'manual'"
            placement="bottom-end"
            width="342"
            v-model="infoVisible"
          >
            <div class="person-info">
              <div class="person-info-head">
                <div style="display: flex; align-items: center">
                  <el-image
                    fit="cover"
                    :src="userInfo.avatar || avatarUrl[Number(userInfo.sex) || 0]"
                    alt=""
                  />
                  <div class="person-info-head-center">
                    <div class="person-name-status">
                      <div class="person-name">{{ userInfo.nickName }}</div>
                    </div>
                    <div class="person-phone-btn">
                      <div class="person-phone">{{ userInfo.phonenumber }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="person-info-item" @click="toUserInfo(3)">
                <div class="person-info-item-icon">
                  <img src="../../../public/images/home/user-1.png" alt="" />
                </div>
                <div class="person-info-item-content">
                  <div class="item-content-title">个人信息</div>
                  <!-- <div class="item-content-dec">基本资料和编辑</div> -->
                </div>
              </div>
              <div class="person-info-item" @click="exit">
                <div class="person-info-item-icon">
                  <img src="../../../public/images/home/user-6.png" alt="" />
                </div>
                <div class="person-info-item-content">
                  <div class="item-content-title">退出登录</div>
                </div>
              </div>
            </div>
            <div slot="reference" class="avatar_wrapper" @click="toInfo">
              <span>{{ userInfo.nickName }}</span>
              <el-avatar
                :size="30"
                :src="userInfo.avatar || avatarUrl[Number(userInfo.sex) || 0]"
              >
              </el-avatar>
            </div>
          </el-popover>
        </template>

        <div class="theme-btn" @click="changeTheme">
          <i class="el-icon-sunny" v-if="theme == 'light'" />
          <i class="el-icon-moon" v-else />
        </div>
      </div>
    </div>
    <div class="nav-bar-drown" v-if="activeValues.length">
      <img src="../../../public/images/home/nav-left.png" alt="" />
      <div class="nav-bar-drown-content">
        <template v-for="(subItem, subIndex) in activeValues">
          <div
            class="nav-bar-drown-item"
            :class="{ 'drown-active': subIndex === subActiveIndex }"
            :key="subItem.name"
            @click="subClick(subItem, subIndex)"
            v-if="!subItem.hidden"
          >
            {{ subItem.label }}
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NavBar',
  props: {
    userInfo: { type: Object },
    //导航栏是否固定
    isFixed: { type: Boolean },
    //是否展示position: static;
    isStatic: { type: Boolean },
    //查企业、查品牌默认选择项
    selectVal: { type: String, default: '0' },
  },
  data() {
    return {
      theme: localStorage.getItem('theme') || 'light',
      activeIndex: '',
      activeName: '',
      activeValues: [],
      subActiveIndex: 0, //二级下标
      navBackground: 'rgba(255,255,255,0)', //导航栏背景色
      navTextColor: '#ffffff', //导航栏及昵称字体颜色
      activeTextColor: 'var(--primary-color)', //导航栏选中字体颜色
      baseNavList: [
        {
          label: '接口平台',
          name: 'InterfacePlatform',
          // show: 'Interface:Platform:show',
          // isChange: true,
          values: [
            {
              label: '数据场景管理',
              name: 'SmartDecision',
            },
            {
              label: '接口管理',
              name: 'InterfaceManagement',
            },
            {
              label: '数据审计',
              name: 'DataDuditing',
            },
            {
              label: '数据报表',
              name: 'InterfaceUser',
            },
          ],
        },
        {
          label: '数据平台',
          name: 'DataCenter',
          // isChange: true,
          // show: 'Data:Platform2:show',
          values: [
            {
              label: '元数据',
              name: 'BasicVariables',
            },
            {
              label: '特征变量',
              name: 'FeatureVariable',
            },
            {
              label: '分析指标',
              name: 'AnalysisTarget',
            },
          ],
        },
        {
          label: '规则池',
          name: 'RulePool',
          values: [
            {
              label: '策略规则池',
              name: 'Strategy',
            },
          ],
        },
        {
          label: '决策中台',
          name: 'ProductDecision',
          // isChange: true,
          // show: 'Decision:making:platformshow',
          values: [
            {
              label: '产品模型',
              name: 'ProductDecision',
            },
            // 暂时注释 20260105
            // {
            //   label: '审批列表',
            //   name: 'Review',
            // },
          ],
        },
        {
          label: '平台引擎',
          name: 'PlatformEngine',
          // isChange: true,
          // show: 'Platform:engine:show',
          values: [
            {
              label: '流程管理',
              name: 'WorkflowEngine',
            },
            {
              label: '任务列表',
              name: 'ProcessTask',
            },
          ],
        },
        // 暂时注释 20260105
        // {
        //   label: '流程回溯',
        //   name: 'Backtrack',
        //   values: [
        //     {
        //       label: '历史任务列表',
        //       name: 'HistoryTask',
        //     },
        //     {
        //       label: '回溯任务列表',
        //       name: 'BacktrackTask',
        //     },
        //   ],
        // },
        {
          label: '监测',
          name: 'Monitor',
          isChange: true,
          values: [
            {
              label: '预警任务',
              name: 'WarningTask',
            },
          ],
        },
        {
          label: '系统管理',
          name: 'System',
          // show: 'system:router:show',
          // isChange: false,
          values: [
            {
              label: '用户管理',
              name: 'UserManage',
            },
            {
              label: '角色管理',
              name: 'RoleManage',
            },
            {
              label: '菜单管理',
              name: 'MenuManage',
            },
            {
              label: '字典管理',
              name: 'DictionaryManagement',
            },
            {
              label: '部门管理',
              name: 'Department',
            },
            // 暂时注释 20260105
            // {
            //   label: '审批授权',
            //   name: 'ApprovalAuth',
            // },
            {
              label: '操作日志',
              name: 'OperlogManage',
            },
            {
              label: '决策日志',
              name: 'DecisionManage',
            },
            {
              label: '登录日志',
              name: 'RegisterLog',
            },
          ],
        },
      ],
      navList: [], //导航栏展示数据
      tipUrl: require('../../../public/images/home/message_icon.png'), //消息icon路径
      avatarUrl: [
        require('../../../public/images/nanAvatar.png'),
        require('../../../public/images/nvAvatar.png'),
      ],
      infoVisible: false, //是否显示个人信息弹窗
    }
  },
  computed: {
    //获取logo地址
    getLogo() {
      return process.env.VUE_APP_LOAD
    },
    //获取导航栏展示logo
    getNavBarLogo() {
      if (localStorage.getItem('userInfo'))
        return JSON.parse(localStorage.getItem('userInfo')).logoUrl
    },
  },
  watch: {
    '$route.name': {
      handler(cur) {
        this.activeName = cur
        this.navList = this.handleNavList(this.baseNavList)
        this.setValuesData()
      },
      deep: true,
      immediate: true,
    },
    // isFixed: {
    //   handler(cur) {
    //     if (cur) {
    //       this.navBackground = 'rgba(255,255,255,1)'
    //       this.navTextColor = 'rgba(0, 0, 0, 0.85)'
    //       this.tipUrl = require('../../../public/images/home/msg_icon.png')
    //     } else {
    //       this.navBackground = 'rgba(255,255,255,0)'
    //       this.navTextColor = 'rgba(255,255,255,1)'
    //       this.tipUrl = require('../../../public/images/home/message_icon.png')
    //     }
    //   },
    //   deep: true,
    //   immediate: true,
    // },
  },
  mounted() {
    this.$store.dispatch('initTheme')
  },
  methods: {
    isShow() {
      let noNavList = ['RiskReport']
      return !noNavList.includes(this.$route.name)
    },
    handleNavList(nav) {
      let that = this
      function handleNav(list) {
        list = list.filter((item) => {
          if (
            !item.url &&
            ((item.hasOwnProperty('show') && that.hasButton(item.show)) ||
              (item.hasOwnProperty('notShow') &&
                !that.hasButton(item.notShow)) ||
              (!item.show && !item.notShow))
          ) {
            if (item.values && item.values.length) {
              item.values = handleNav(item.values)
            }

            return item
          }
        })
        return list
      }
      return handleNav(nav)
    },
    setValuesData() {
      this.activeValues = []
      this.navList.map((item, index) => {
        if (item.values && item.values.length) {
          item.values.map((child, childIndex) => {
            if (child.name === this.activeName && item.isChange) {
              this.subActiveIndex = childIndex
              switch (this.activeName) {
                case 'Dashboard':
                  item.values[0].hidden = false
                  item.values[1].hidden = true
                  break
                case 'PublicOpinionDashboard':
                  item.values[0].hidden = true
                  item.values[1].hidden = false
                  break
                case 'WeeklyReport':
                  item.values[0].hidden = false
                  item.values[1].hidden = true
                  break
                case 'WeeklyPublicOpinion':
                  item.values[0].hidden = true
                  item.values[1].hidden = false
                  break
                case 'Monitoring':
                  item.values[4].hidden = false
                  item.values[5].hidden = true
                  break
                case 'PublicMonitoring':
                  item.values[4].hidden = true
                  item.values[5].hidden = false
                  break
                default:
                  break
              }
              this.activeValues = item.values
              this.activeIndex = index
            }
          })
        }
      })
    },
    //导航栏@click
    navHandle(bol) {
      if (bol) this.$emit('click')
    },
    //导航栏tab点击
    handleSelect(key, keyPath) {
      const that = this
      this.activeIndex = ''
      let currentI = that.navList.findIndex((item) => {
        if (item.name === key) return item
      })
      if (
        currentI > -1 &&
        that.navList[currentI].values &&
        that.navList[currentI].isChange
      ) {
        let active = that.navList[currentI].values.find((child) => {
          if (
            (!child.show || (child.show && that.hasButton(child.show))) &&
            child.name !== 'ThreadManagement' &&
            child.name !== 'BusinessManagement'
          ) {
            return child
          }
        })
        that.navList[currentI].name = active ? active.name : ''
        that.activeName = active ? active.name : ''
        this.setValuesData()
      } else {
        that.activeName = key
      }
      if (keyPath.length === 2) {
        that.$router.push({ name: that.activeName }).catch(() => {})
      } else if (
        keyPath.length === 1 &&
        that.activeName !== 'Index' &&
        that.activeName !== 'About'
      ) {
        // that.activeName = key
        that.$router.push({ name: that.activeName }).catch(() => {})
      }
    },
    subClick(data, i) {
      if (this.subActiveIndex === i) return
      this.subActiveIndex = i
      this.$router.push({ name: data.name }).catch(() => {})
      console.log(data, 'sub')
    },
    //查看个人信息
    toInfo() {
      this.infoVisible = !this.infoVisible
    },
    /**
     * @name: toUserInfo
     * @msg: 跳转到用户信息--补充资料&&个人信息&&企业信息
     * @param {*} type
     * @return {*}
     */
    toUserInfo(type) {
      this.infoVisible = false
      this.$router.push({
        name: 'userInfo',
      })
    },
    /**
     * @name: exit
     * @msg: 退出登录
     * @return {*}
     */
    exit() {
      let sortName = JSON.parse(
        localStorage.getItem('userInfo')
      ).companyShortName
      // 信德企科技
      localStorage.removeItem('id_token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('companyinfo')
      localStorage.removeItem('WEIXINL')
      localStorage.setItem('loyout', true)
      this.$message.success('退出成功!')
      if (sortName === '信德企科技') {
        this.$router.replace({
          path: '/xinde/login',
        })
      } else if (sortName === '鑫汇村镇银行') {
        this.$router.replace({
          path: '/xinhui/login',
        })
      } else {
        this.$router.push('/login').catch((err) => {})
      }
    },
    changeTheme() {
      this.theme = this.theme === 'light' ? 'dark' : 'light'
      this.$store.dispatch('setTheme', this.theme)
    },
  },
}
</script>

<style lang="less" scoped>
.nav_bar {
  width: 100%;
  height: 42px;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-family: PingFang SC-Medium;
  border-bottom: var(--nav-border-color);

  ::v-deep .el-menu {
    background-color: var(--bg-color) !important;
    .el-menu-item {
      color: var(--text-color) !important;
    }
  }
  :deep .el-menu--horizontal {
    .el-submenu {
      .el-submenu__title {
        display: flex;
        align-items: center;

        &:after {
          content: '';
          display: inline-block;
          width: 12px;
          height: 6px;
          background-size: 100%;
          margin-left: 4px;
        }
      }
      .el-submenu__title,
      .el-menu-item {
        color: var(--text-color) !important;
      }
    }
  }

  &_fixed,
  &_static {
    position: fixed;
    background: var(--bg-color);

    :deep .el-menu--horizontal > .el-submenu .el-submenu__title {
      &:after {
        background: var(--caret-image) no-repeat;
      }
    }
  }

  &_static {
    position: static;
  }

  &_left,
  &_right {
    display: flex;
    align-items: center;
  }

  &_left {
    margin-left: 30px;

    :deep .el-menu-demo {
      height: 42px;
      border-bottom: var(--nav-border-color);

      a {
        vertical-align: top;
      }

      > li {
        height: 42px;
        line-height: 42px;
        border-bottom: none;
        background: transparent !important;

        > div {
          height: 42px !important;
          line-height: 42px !important;
          padding: 0 !important;
          margin: 0 15px !important;
          background: transparent !important;
        }
      }

      .is-active .el-submenu__title {
        border-bottom: none;
      }
    }

    :deep .el-menu--horizontal {
      > .el-submenu {
        .el-submenu__title {
          border-bottom: none;
        }

        .el-submenu__icon-arrow {
          display: none;
        }
      }

      > .el-menu-item {
        padding: 0 !important;
        margin: 0 15px !important;

        &.is-disabled {
          opacity: 1;
          color: #929292 !important;
        }
      }

      > .is-active {
        .el-submenu__title {
          border: none !important;
          color: rgb(40, 136, 232) !important;
        }

        .el-submenu__title {
          &:after {
            background: url('../../../public/images/home/caret.png') no-repeat;
          }
        }
      }
    }
  }

  &_logo {
    width: 36px;
    height: 36px;
    margin-right: 15px;
    // cursor: pointer;
  }

  .logo-text {
    font-size: 24px;
    font-weight: bold;
    background: linear-gradient(
      to right,
      #2888e8 0%,
      #10b981 70%,
      #2888e8 100%
    );
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
    margin-right: 10px;
  }

  &_right {
    margin-right: 20px;

    .search_box {
      position: relative;
      height: 30px;
      display: flex;
      align-items: center;
      margin-right: 44px;
      background: rgba(0, 0, 0, 0.04);
      border-radius: 2px;

      :deep .el-select {
        position: relative;
        color: var(--primary-color);

        &::after {
          position: absolute;
          right: 14px;
          bottom: 50%;
          margin-bottom: -3px;
          content: '';
          display: inline-block;
          width: 12px;
          height: 6px;
          background: url('../../../public/images/home/caret.png') no-repeat;
        }

        .el-input .el-select__caret {
          display: none;
        }

        .el-input__inner {
          font-size: 14px;
          width: 88px;
          color: var(--primary-color);
        }
      }

      :deep .el-input {
        &__inner {
          width: 290px;
          height: 30px;
          font-size: 14px;
          border-radius: 0;
          border: none;
          background: transparent;
        }
      }

      .search_line {
        width: 1px;
        height: 24px;
        background: rgba(0, 0, 0, 0.08);
      }

      .el-button {
        width: 70px;
        height: 30px;
        display: flex;
        align-items: center;
        border-radius: 2px;
        font-size: 14px;
        background: var(--primary-color);
        border-color: var(--primary-color);

        &:hover {
          opacity: 0.8;
        }
      }
    }

    .message_wrapper {
      display: flex;
      align-items: center;
      font-size: 14px;
      cursor: pointer;

      .message_icon {
        width: 18px;
        height: 20.22px;
        margin-right: 10px;
        cursor: not-allowed;
      }
    }

    .login_btn {
      display: flex;
      align-items: center;
      font-size: 14px;
      width: 104px;
      height: 30px;
      margin-left: 20px;
      border-radius: 6px;
    }

    .avatar_wrapper {
      font-size: 14px;
      display: flex;
      align-items: center;
      margin-left: 30px;
      cursor: pointer;

      span {
        color: var(--text-color);
      }

      .el-avatar {
        margin-left: 5px;
      }
    }

    .theme-btn {
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 6px;
      margin-left: 10px;
      cursor: pointer;
      color: var(--text-color);
    }

    .theme-btn:hover {
      background: var(--header-bg);
    }
  }

  ::v-deep .el-menu--horizontal .el-menu .el-menu-item,
  .el-menu--horizontal .el-menu .el-submenu__title {
    background-color: var(--bg-color) !important;
    color: var(--text-color) !important;

    &:focus,
    &:hover {
      color: var(--primary-color) !important;
      background: #e9f3fd !important;
    }
  }

  ::v-deep .el-menu--popup {
    width: 105px;
    padding: 0;
    border-radius: 6px;
    overflow: hidden;
    border: var(--nav-border-color);
  }

  .gutuLink {
    color: #0a2540;

    &:hover {
      color: var(--primary-color) !important;
    }
  }
}

.nav-bar-drown {
  z-index: 99;
  position: fixed;
  right: 0;
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;

  img {
    height: 60px;
  }

  &-content {
    position: relative;
    height: 60px;
    display: flex;
    align-items: center;
    padding-right: 20px;
    background: linear-gradient(to right, #ffffff, #eaf3fc);
    border-bottom: 2px solid #a3d1ff;
    //&::before{
    //	height: 100%;
    //	position: absolute;
    //	content:"";
    //	display: block;
    //	width: 100px;
    //	border: 1px solid #81B8F0;
    //}
    .nav-bar-drown-item {
      cursor: pointer;
      padding: 6px 20px;
      font-size: 16px;
      font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      color: #79b5f0;
      line-height: 22px;
      position: relative;
      z-index: 2;

      &.drown-active {
        border-radius: 10px;
        font-family: PingFang SC-Medium, PingFang SC;
        font-weight: 500;
        color: #ffffff;

        &::after {
          z-index: -1;
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          border-radius: 8px;
          background: linear-gradient(to right, #2a89e8 0%, #3662ec 100%);
          transform: skewX(-15deg);
        }
      }
    }
  }
}

.right-menu {
  font-size: 14px;
  display: flex;
  align-items: center;

  &-item {
    display: flex;
    align-items: center;
    cursor: pointer;
    margin-left: 30px;

    &.is-disabled {
      cursor: not-allowed;
      color: #929292 !important;
    }

    &.blue {
      background: linear-gradient(to right, #2a8df8 0%, #1e53e4 94%);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;

      &.is-disabled {
        background: linear-gradient(to right, #94c2fb 0%, #8fabf2 95%);
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }
  }
}

/deep/ .el-popover {
  padding: 0px !important;

  .el-popover--plain {
    &:has(.person-info) {
      padding: 0 0 20px;
    }
  }
}

.person-info {
  margin-bottom: 20px;

  &-head {
    padding: 20px;
    display: flex;
    justify-content: space-between;
    background: linear-gradient(
      90deg,
      rgba(40, 136, 231, 0.04) 0%,
      rgba(54, 98, 236, 0.04) 100%
    );

    .el-image {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      margin-right: 14px;
    }

    &-center {
      .person-name-status {
        .person-name {
          font-weight: 500;
          font-size: 16px;
          color: #3f4254;
          line-height: 22px;
          margin-bottom: 6px;
        }
      }

      .person-vip {
        display: flex;
        align-items: center;
        font-size: 12px;
        &-level {
          padding: 4px 8px;
          background: var(--primary-color);
          color: #fff;
          border-radius: 8px;
          margin-right: 8px;
          cursor: pointer;
        }

        &-time {
          display: flex;
          padding: 0px 8px;
          // border: 1px solid var(--primary-color);
          border-left: 1px solid rgba(#333, 0.3);

          > span:first {
            display: block;
            margin-right: 8px;
            color: var(--primary-color);
            border-radius: 8px;
          }
        }
      }

      .person-phone-btn {
        display: flex;
        align-items: center;
        font-weight: 400;
        font-size: 14px;
        line-height: 20px;

        .person-phone {
          color: #b5b5c3;
        }

        .person-line {
          width: 1px;
          height: 14px;
          background: #b5b5c3;
          margin: 0 6px;
        }

        .person-btn {
          color: var(--primary-color);
          cursor: pointer;
        }
      }
    }

    &-arrow {
      img {
        width: 16px;
        height: 16px;
        margin-right: 0;
        margin-top: 8px;
      }
    }
  }

  &-item {
    cursor: pointer;
    display: flex;
    align-items: center;
    margin-top: 20px;
    padding-left: 20px;

    &:hover {
      .item-content-title,
      .item-content-dec {
        color: var(--primary-color);
      }
    }

    &-icon {
      img {
        height: 20px;
        margin-right: 10px;
      }
    }

    &-content {
      .item-content-title {
        font-weight: 500;
        font-size: 14px;
        color: #3f4254;
        line-height: 20px;
      }

      .item-content-dec {
        font-weight: 400;
        font-size: 12px;
        color: #b5b5c3;
        line-height: 18px;
        margin-top: 4px;
      }
    }

    &.is-disabled {
      cursor: not-allowed;

      &:hover {
        .item-content-title {
          color: #3f4254;
        }

        .item-content-dec {
          color: #b5b5c3;
        }
      }
    }
  }
}

//企业微信二维码
.wechat-code {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: url(../../../public/images/weixinbg.png) no-repeat;
  background-size: 100% 100%;
  padding: 25px;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 24px;
  z-index: 999;

  &-mask {
    position: fixed;
    top: 0;
    right: 0;
    left: 0;
    bottom: 0;
    z-index: 112;
    background: rgba(0, 0, 0, 0.6);
  }

  .qr-code {
    margin-top: 10px;
    width: 250px;
    height: 250px;
  }

  .close {
    position: absolute;
    cursor: pointer;
    bottom: -80px;
  }
}
</style>
