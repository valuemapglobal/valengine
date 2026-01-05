<template>
  <div id="app">
    <router-view />
  </div>
</template>
<!-- 引入组件库 -->
<script src="https://unpkg.com/element-ui/lib/index.js"></script>
<script>
export default {
  data() {
    return {
      intetimer: null,
    }
  },
  components: {
    // NewGuide,
  },
  mounted() {
    setTimeout(() => {
      this.$nextTick(() => {
        this.resize()
      })
    }, 500)

    window.addEventListener('resize', this.resize)

    if (
      !this.$store.state.userInfo ||
      !this.$store.state.userInfo.phonenumber ||
      (this.$store.state.userInfo &&
        this.$store.state.userInfo.companyShortName === '信德企科技')
    )
      return
    this.checkUserWei()
    this.intetimer = setInterval(() => {
      this.checkUserWei()
    }, 5000)
  },
  beforeDestroy() {
    clearInterval(this.intetimer)
  },
  methods: {
    resize() {
      let h5 = false
      let devices = ['iPhone', 'Android', 'Windows Phone']
      let ua = window.navigator.userAgent
      for (let i = 0; i < devices.length; i++) {
        if (ua.indexOf(devices[i]) !== -1) {
          h5 = true
        }
      }
      if (h5) return

      let app = document.body
      let index = document.querySelector('#index')
      if (window.innerWidth < 1600) {
        app.style.setProperty('--bgvh', '125vh')
        app.style.setProperty('--zoom', '0.8')
        app.style.setProperty('--mapZoom', 1.25)
        app.style.setProperty('--mapZoom2', 0.8)
      } else {
        app.style.setProperty('--bgvh', '100vh')
        app.style.setProperty('--zoom', '1')
        app.style.setProperty('--mapZoom', 1)
        app.style.setProperty('--mapZoom2', 1)
      }
    },
    // 可轮询调用该接口来确认用户是否已经添加好友
    checkUserWei() {
      let client_user_id = this.$store.state.userInfo.phonenumber
      // 可轮询调用该接口来确认用户是否已经添加好友
      wlRegisterComp.checkUserState(
        client_user_id, // 用户在客户平台的唯一ID
        ({ code, data }) => {
          if (code === 200) {
            if (!data.is_added) {
              console.log('尚未加好友')
            } else {
              console.log('已加好友')
            }
          }
        }
      )
    },
  },
}
</script>

<style lang="less">
@import '@/assets/less/theme/index.less';
@import '@/assets/less/element.less';
@import '@/assets/css/bootstrap.min.css';
@import '@/assets/css/newGiude.less';

@font-face {
  font-family: PingFang SC-Medium;
  src: url('/public/fonts/PingFang Medium.ttf') format('truetype');
}

@font-face {
  font-family: PingFang SC-Regular;
  src: url('/public/fonts/PingFang SC Regular.ttf') format('truetype');
}
@font-face {
  font-family: Alimama ShuHeiTi-Bold;
  src: url('/public/fonts/Alimama ShuHeiTi-Bold.ttf') format('truetype');
}

//刷新数据提示，导入数据成功提示
.refresh-message-warning,
.task-message-success {
  &.el-message {
    border-radius: 6px;
  }

  &.el-message--warning,
  &.el-message--success {
    .el-message__content {
      font-size: 14px;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.9);
    }
  }
}

.refresh-message-warning {
  &.el-message {
    background: #eff3ff;
    border-color: #eff3ff;
    top: calc(var(--bgvh) - 96%) !important;

    .el-icon-warning {
      color: var(--primary-color);
    }
  }
}

.task-message-success {
  &.el-message {
    min-width: auto;
    padding: 13px 16px;
    background: #ffffff;
    border-color: #dcdcdc;
    top: calc(100vh - 90%) !important;

    .el-icon-warning {
      color: #2ba471;
    }
  }
}

canvas {
  zoom: var(--mapZoom);
  transform: translate(-50%, -50%) scale(var(--mapZoom2));
  left: 50% !important;
  top: 50% !important;
}

.watermark {
  position: absolute;
  left: 0;
  bottom: 0;
  z-index: 999999;
  width: 100%;
  height: 100%;
  // background-color: var(--primary-color);
  pointer-events: none;
  background-image: url('/public/images/watermark.png');
}

.el-dropdown-menu__item {
  white-space: nowrap;
}

.warningDashboard {
  top: 40px !important;
  width: 256px !important;
  min-width: auto !important;
  height: 36px;
  background: #000000 !important;
  border-radius: 18px !important;
  opacity: 0.8;
}

body {
  --bgvh: 100vh;
  --zoom: 1;
  --mapZoom: 1;
  --mapZoom2: 1;
  font-family: PingFang SC-Medium;
}

.el-dialog__wrapper {
  display: flex;
  align-items: center;

  > .el-dialog {
    margin-top: 0 !important;
  }
}

#app {
  position: relative;
  zoom: var(--zoom);
  height: var(--bgvh);

  > .background {
    height: 100%;
  }
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.el-menu--popup-bottom-start {
  min-width: 0 !important;
}

ul,
li {
  list-style: none;
  padding: 0;
  margin: 0;
}

a,
a:focus {
  text-decoration: none !important;
}

button {
  border: none;
}

html {
  overflow: auto hidden !important;
}

body {
  padding-right: 0 !important;
}

.flowWordReverse {
  font-size: 12px;
  text-align: left;
  color: #828282;
  margin-bottom: 8px;
  margin-top: 5px;
}
</style>
