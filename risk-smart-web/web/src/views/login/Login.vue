<!--
 * @Date: 2022-04-21 18:11:51
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-01-26 01:19:53
 * @name:
 * @FilePath: \vm-micro-middleground\src\login\Login.vue
-->
<template>
  <div class="login_main">
    <div class="login_background">
      <img src="./img/login.png" alt="" srcset="" />
      <div class="login_background-top">
        <div class="title">{{ $t('login.title') }}</div>
        <div class="subtitle">
          {{ $t('login.subtitle') }}
        </div>
      </div>
    </div>
    <div class="login_content">
      <Titles :type="type" @toRegister="toRegister" />
      <!-- 密码登录框 -->
      <PassWordLogin
        v-if="type === 0"
        @toRegister="toRegister"
        @success="authority"
      />
      <!-- 验证码登录框 -->
      <CodeLogin
        v-if="type === 1"
        @toRegister="toRegister"
        @success="authority"
      />
      <!-- 微信扫码登录 -->
      <Wxlogin
        v-if="type === 9"
        @success="authority"
        @toRegister="toRegister"
      />
      <!-- 微信手机号绑定 -->
      <Register
        v-if="type === 10"
        @toRegister="toRegister"
        @success="authority"
      />
    </div>
    <div class="tips" v-if="!showTips">
      <div>{{ $t('login.browserTip') }}</div>
      <span>{{ $t('login.browserTip2') }}</span>
      <div>{{ $t('login.browserTip3') }}</div>
      <i
        style="font-size: 20px; cursor: pointer"
        class="el-icon-close"
        @click="showTips = true"
      ></i>
    </div>
  </div>
</template>

<script>
import CodeLogin from './components/CodeLogin.vue'
import PassWordLogin from './components/PassWordLogin.vue'
import Titles from './components/Titles.vue'
import Wxlogin from './components/Wxlogin.vue'

export default {
  name: 'Login',
  components: {
    Titles,
    CodeLogin, //0密码登录
    PassWordLogin, //1验证码登录
    Wxlogin, //微信登录
  },
  data() {
    return {
      phoneNumber: '', //电话号码
      loginForm: {}, //登录的请求体
      showTips: true,
      type: 9, //0密码登录,1验证码登录,2注册第一步,3注册第二步,4忘记密码,5忘记密码第二步
    }
  },
  computed: {
    getLogo() {
      return process.env.VUE_APP_LOGO
    },
    getHref() {
      return window.location.href.includes('liantou')
    },
  },
  watch: {
    '$route.query': {
      handler(newV) {
        if (newV.type != undefined) {
          this.type = Number(newV.type)
        } else {
          this.type = 0
        }
      },
      deep: true,
      immediate: true,
    },
  },
  created() {},
  mounted() {
    this.showTips = this.$getBrowserInfo()

    setTimeout(() => {
      this.getLogin()
    }, 0)
    window.addEventListener('resize', () => {
      if (!this.showTips) {
        this.getLogin()
      }
    })
  },
  methods: {
    authority() {
      localStorage.setItem('time', +new Date())
      localStorage.removeItem('dmp_secreat')
      this.$router.push({ path: '/' })
    },
    getLogin() {
      let login = document.querySelector('.login'),
        background = document.querySelector('.background')
      let tips = document.querySelector('.tips')
      if (!tips) return
      tips.style.left =
        (login.offsetWidth - 444) / 2 + background.offsetWidth + 'px'
    },
    /**
     * @name: toRegister
     * @msg: 去注册
     * @param {*}
     * @return {*}
     */
    toRegister(num) {
      this.$router.push({ path: 'login' })
      this.$nextTick(() => {
        this.type = num
      })
    },
  },
}
</script>

<style lang="less" scoped>
.login_main {
  display: flex;
  min-width: 1200px;
  background-image: url('./img/bg.png');
}

.login_background {
  position: relative;
  height: var(--bgvh);
  width: 55%;
  display: flex;
  align-items: center;
  justify-content: center;
  > img {
    width: 80%;
    position: absolute;
  }

  &-top {
    position: absolute;
    top: 60px;
    left: 100px;
    font-family: 600;
    .title {
      font-size: 52px;
      font-family: Alimama ShuHeiTi-Bold;
      margin-bottom: 20px;
    }

    .subtitle {
      font-size: 16px;
      line-height: 28px;
      font-family: PingFang SC-Medium;
    }
  }
}

.login_content {
  height: var(--bgvh);
  width: 45%;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  > div {
    width: 480px;
  }
  > img {
    width: 420px;
  }
}

.tips {
  position: fixed;
  width: 444px;
  bottom: 40px;
  // right: 100px;
  left: 50%;
  transform: translateX(-50%);
  height: 36px;
  background: #000000;
  border-radius: 18px;
  opacity: 0.74;
  display: flex;
  align-items: center;
  color: #ffffff;
  padding: 0 18px;
  font-size: 14px;
  font-family: PingFangSC-Regular, PingFang SC;
  font-weight: 400;
  color: #ffffff;
  line-height: 24px;
  justify-content: space-between;

  > span {
    flex-shrink: 0;
    color: #1e94ff;
  }

  > div {
    flex-shrink: 0;
  }
}
</style>
