<!--
 * @Date: 2022-08-29 16:52:07
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-01-25 18:34:58
 * @name:
 * @FilePath: \vm-micro-middleground\src\login\components\Wxlogin.vue
-->
<template>
  <div class="wxlogin">
    <div id="test"></div>
    <el-divider class="divider">返回</el-divider>
    <div
      class="loginBtn"
      @click="
        () => {
          $emit('toRegister', 1)
        }
      "
    >
      <img src="@/assets/images/login/phone.png" alt="" />
      <span>手机登录</span>
    </div>
    <div class="authority">
      扫码表示您同意估图数科
      <a href="https://portal.valuemap.cn/sm-agreement/index.html"
        >《用户协议》</a
      >和<a
        href="https://portal.valuemap.cn/aivisit-agreement/private_agreement_v1.0_20211201.html"
        >《个人信息保护政策》</a
      >
    </div>
    <!-- <div
      @click="
        () => {
          $emit('toRegister', 1);
        }
      "
      style="
        cursor: pointer;
        color: var(--primary-color);
        font-size: 14px;
        width: 500px;
        display: flex;
        justify-content: center;
        margin-top: 20px;
      "
    >
      已有账号？去登录
    </div> -->
  </div>
</template>

<script>
import { verifyLogin } from '@/api/base/index.js'

export default {
  data() {
    return {}
  },
  watch: {
    '$route.query.code': {
      handler(newV) {
        if (!newV) {
          this.$nextTick(() => {
            var obj = new window.WxLogin({
              self_redirect: false,
              id: 'test',
              appid: 'wxdbf4ec0858c49fee',
              scope: 'snsapi_login',
              redirect_uri:
                'https%3A%2F%2Fwww.valuemap.cn%2Fsaas%2F%23%2Flogin%3Ftype%3D9',
              // "https%3A%2F%2Fwww.valuemap.cn%2F%23%2Fcompany%2F%23%2Flogin%3Ftype%3D9",
              // "https%3A%2F%2Fwww.valuemap.cn%2Ftest%2F%23%2Flogin%3Ftype%3D9"
            })
            // fast_login: 0,//启用或禁用快速登录功能，值为0时将禁用快速登录。
            const iframe = document.getElementsByTagName('iframe')[0]
            if (iframe) {
              iframe.src = iframe.src + '&fast_login=0'
            }
            console.log('document', document.getElementsByTagName('iframe')[0])
          })
        } else {
          verifyLogin({ code: this.$route.query.code, loginType: 1 }).then(
            (res) => {
              if (res.code === 701) {
                localStorage.setItem('dmp_secreat', res.data.dmp_secreat)
                this.$emit('toRegister', 10)
              }
              if (res.code === 200) {
                localStorage.setItem('id_token', res.data.access_token)
                this.$emit('success')
              }
            }
          )
        }
      },
      deep: true,
      immediate: true,
    },
  },
}
</script>

<style scoped lang="less">
#test {
  display: flex;
  justify-content: center;
  color: #000;

  > :deep iframe {
    width: 400px !important;
    height: 411px !important;
  }
}

.authority {
  font-size: 14px;
  font-weight: 400;
  color: #3f4254;
  line-height: 22px;
}

/deep/ .el-divider--horizontal {
  margin: 0px 0px 24px 0px;
}

/deep/ .el-divider {
  .el-divider__text {
    background-color: #f7f9fe;
  }

  > div {
    color: #6d7278;
  }
}

.loginBtn {
  height: 36px;
  display: flex;
  align-items: center;
  color: rgba(#000, 0.85);
  font-family: PingFang SC-Regular;
  border: 1px solid rgba(#000, 0.08);
  font-size: 14px;
  padding: 6px 10px;
  cursor: pointer;
  margin-bottom: 20px;

  img {
    width: 24px;
    margin-right: 4px;
  }
}

.wxlogin {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-family: PingFang SC-Regular;
}
</style>
