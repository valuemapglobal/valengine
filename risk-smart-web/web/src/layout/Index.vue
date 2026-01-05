<!--
 * @Date: 2022-07-11 11:28:03
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-04-15 13:52:41
 * @name:
 * @FilePath: \vm-micro-middleground\src\layout\Index.vue
-->
<template>
  <div class="background">
    <!-- 头部导航 -->
    <NavBar
      :userInfo="userInfo"
      :isFixed="true"
      :isStatic="true"
      @toInfo="show = true"
    />
    <!-- <ShowInfo v-model="show" :userInfo="userInfo" /> -->
    <keep-alive include="risk" :max="1">
      <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
import { getLoginInfo } from '@/api/base/index.js'
import { getToken } from '@/utils/auth'
import NavBar from './components/NavBar'
export default {
  name: 'Index',
  components: {
    NavBar,
  },
  data() {
    return {
      activeIndex: '',
      userInfo: {},
      show: false, //用户信息弹窗
    }
  },
  watch: {
    '$router.options': {
      handler(newV) {
        // console.log(newV, "-=-=-=");
      },
      deep: true,
      immediate: true,
    },
    '$route.name': {
      handler(newV) {
        this.activeIndex = newV
      },
      deep: true,
      immediate: true,
    },
  },
  created() {
    if (getToken()) this.getLoginInfo()
  },
  methods: {
    getLoginInfo() {
      getLoginInfo()
        .then((res) => {
          //   localStorage.setItem("userInfo", JSON.stringify(res.user));=
          this.userInfo = res.user
          this.$store.commit('setUserInfo', res.user)
          this.$store.commit(
            'setUserAuthInfo',
            res.userAuthPersonFinancingExpertInfo
          )
        })
        .catch((err) => {
          console.log(err)
        })
    },
  },
}
</script>
<style lang="less" scoped>
.background {
  background-color: var(--main-bg);
  height: var(--bgvh);
  overflow: hidden auto;
  min-width: 1400px;

  :deep .nav_bar {
    position: static;
  }
}

.userInfo {
  display: flex;
  align-items: center;
  justify-content: space-between;

  > img {
    margin-right: 30px;
    cursor: pointer;
  }

  > div {
    display: flex;
    align-items: center;
    margin-right: 36px;
    cursor: pointer;
  }
}
</style>
