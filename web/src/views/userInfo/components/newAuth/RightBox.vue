<!--
 * @Date: 2022-04-24 13:48:57
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-06-27 11:22:22
 * @name: 右侧部分 RightBox
 * @FilePath: /gutuProject/vmkj/src/userInfo/components/RightBox.vue
-->
<template>
  <div class="rightBox">
    <div class="header">
      <el-tabs v-model="activeTabs">
        <el-tab-pane label="个人信息" name="PersonalInfo" />
        <!-- <el-tab-pane label="账号认证" name="AccountAuth" /> -->
      </el-tabs>
    </div>
    <component ref="authRef" :is="activeTabs"></component>
  </div>
</template>

<script>
import PersonalInfo from './PersonalInfo'
// import AccountAuth from './AccountAuth'
export default {
  name: 'RightBox',
  components: {
    PersonalInfo,
    // AccountAuth,
  },

  data() {
    return {
      activeTabs: 'PersonalInfo',
    }
  },
  watch: {
    '$route.query': {
      handler(val) {
        if (Object.keys(val).length) {
          for (let key in val) {
            if (key.includes('auth')) {
              this.activeTabs = 'AccountAuth'
              this.$nextTick(() => {
                this.$refs.authRef.currentComponent =
                  val[key] == 0 ? 'PersonalAuth' : 'EnterpriseAuth'
              })
            }
          }
        }
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {},
}
</script>

<style lang="less" scoped>
.rightBox {
  width: 1142px;
  border-radius: 6px;
  background-color: #ffffff;

  .header {
    ::v-deep(.el-tabs) {
      .el-tabs__header {
        margin: 0px;
      }
      .el-tabs__nav-scroll {
        padding: 0px 30px;
        .el-tabs__nav {
          .el-tabs__item {
            height: 52px;
            line-height: 56px;
            font-size: 16px;
            font-family: PingFang SC-Medium;
            color: #191c31;
          }
        }
      }
    }
  }
}
</style>
