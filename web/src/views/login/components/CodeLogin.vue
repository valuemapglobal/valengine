<!--
 * @Date: 2022-04-21 18:33:23
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-01-26 01:22:08
 * @name: PassWordLogin 密码登录
 * @FilePath: \vm-micro-middleground\src\login\components\CodeLogin.vue
-->
<template>
  <div class="code_login">
    <el-form ref="form" :model="form" :rules="rules" label-width="0">
      <!-- <span class="text">手机号</span> -->
      <el-form-item prop="phoneNumber">
        <el-input
          class="ipt"
          v-model="form.phoneNumber"
          placeholder="请输入手机号"
        />
      </el-form-item>

      <!-- <span class="text">验证码</span> -->
      <el-form-item prop="verifyCode">
        <el-input
          class="iptCode"
          placeholder="验证码"
          v-model="form.verifyCode"
          show-verifyCode
        >
          <template slot="append">
            <div class="send" v-show="!state" @click="sendLogin">
              发送验证码
            </div>
            <div class="send" v-show="state">{{ time }}</div>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <!-- 注册 -->
    <!-- <span
      v-if="!isCustomisation"
      style="cursor: pointer; font-size: 14px; color: var(--primary-color); font-weight: 500"
      @click="
        () => {
          $emit('toRegister', 2)
        }
      "
    >
      立即注册
    </span> -->
    <!-- 用户同意权限 -->
    <!-- <Authority v-model="status" /> -->

    <div class="btns">
      <el-button type="primary" @click="submitForm" :loading="loading">
        登录
      </el-button>
      <!-- <el-button type="primary" plain @click="toRegister(0)"
        >使用密码登录</el-button
      > -->
    </div>
    <!-- <el-divider
			class="divider"
			v-if="!isCustomisation"
		>
			<div class="box">
				返回
			</div>
		</el-divider>
		<div
			class="loginBtn"
			v-if="!isCustomisation"
			@click="() => {
				$emit('toRegister', 9)
			}"
		>
			<img
				src="@/assets/images/login/wechat.png"
				alt=""
			>
			<span>微信登录</span>
		</div> -->
    <div class="authority" v-if="!isCustomisation">
      登录表示您同意估图数科
      <a href="https://portal.valuemap.cn/sm-agreement/index.html"
        >《用户协议》</a
      >和<a
        href="https://portal.valuemap.cn/aivisit-agreement/private_agreement_v1.0_20211201.html"
        >《个人信息保护政策》</a
      >
    </div>
  </div>
</template>

<script>
import { sendLoginSms, verifyLogin } from '@/api/base/index.js'
import Authority from './Authority.vue'
import { toRegister } from './toRegister'
export default {
  name: 'PassWordLogin',
  mixins: [toRegister],
  components: {
    Authority,
  },
  props: {
    //是否定制
    isCustomisation: { type: Boolean, default: false },
  },
  data() {
    return {
      time: 60, //60秒倒计时
      state: false, //倒计时状态
      loading: false,
      form: { phoneNumber: null, verifyCode: null, loginType: 0 },
      status: true, //用户同意状态
      rules: {
        phoneNumber: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          {
            required: true,
            pattern: /^1[3456789]\d{9}$/,
            message: '手机号格式不正确',
            trigger: 'blur',
          },
        ],
        verifyCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
        ],
      },
    }
  },
  methods: {
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (!this.status) {
            this.$message.warning('请选同意用户协议！')
            return
          }
          this.loading = true
          verifyLogin(this.form)
            .then((res) => {
              // this.$message.success("登录成功！");
              localStorage.setItem('id_token', res.data.access_token)
              this.$parent.phoneNumber = this.form.phoneNumber
              this.$emit('success')
              this.loading = false
            })
            .catch((err) => {
              this.loading = false
            })
        } else {
          return false
        }
      })
    },
    /**
     * @name: sendLogin
     * @msg: 发送验证码
     * @param {*}
     * @return {*}
     */
    sendLogin() {
      let partner // 合作方名称
      if (this.isCustomisation && !window.location.href.includes('liantou')) {
        partner = window.location.hash.split('/')[1]
      }
      if (
        !this.form.phoneNumber ||
        !/^1[3456789]\d{9}$/.test(this.form.phoneNumber)
      ) {
        this.$message.warning('请输入正确的手机号')
        return
      }
      this.state = true //开启倒计时
      sendLoginSms({ phonenumber: this.form.phoneNumber, partner })
        .then((res) => {
          this.$message.success('发送成功')
          this.startTimer()
        })
        .catch(() => {})
    },
    /**
     * @name: startTimer
     * @msg: 开启定时器
     * @param {*}
     * @return {*}
     */
    startTimer() {
      let interval = setInterval(() => {
        this.time--
        if (!this.time) {
          clearInterval(interval)
          this.state = false //关闭倒计时
          this.time = 60
        }
      }, 1000)
    },
  },
}
</script>
<style lang="less" scoped>
.code_login {
  font-family: PingFang SC-Regular;
}

.textBox {
  display: flex;
  justify-content: space-between;
}

.ipt {
  margin: 10px 0 0 0;

  :deep input {
    // background-color: #f3f6f9;
    border-radius: 6px;
    height: 58px;
  }
}

.iptCode {
  margin: 10px 0 0 0;

  :deep input {
    // background-color: #f3f6f9;
    border-radius: 6px 0 0 6px;
    height: 58px;
  }

  :deep .el-input__inner {
    border-right: none;
  }
}

a {
  color: #3699ff;
}

.btns {
  // display: flex;
  margin-top: 30px;
  width: 100%;

  > button {
    width: 100%;
    height: 54px;
    padding: 16px 34px;
    font-size: 16px;
    border-radius: 8px;
  }
}

// .send {
//   // width: 100%;
//   // height: 56px;
//   box-sizing: content-box;
//   display: flex;
//   align-items: center;
//   justify-content: center;
// }

// :deep .el-input-group__append {
//   padding: 0;
// }
/deep/ .el-divider {
  margin-top: 30px;

  .el-divider__text {
    background-color: #f7f9fe;
  }

  .box {
    font-size: 14px;
    font-family: PingFangSC-Regular, PingFang SC;
    font-weight: 400;
    color: #6d7278;
    line-height: 20px;

    > img {
      position: absolute;
      top: 30px;
      left: 50%;
      transform: translateX(-50%);
      cursor: pointer;
    }
  }
}

/deep/ .el-input {
  // border: 1px solid #c0c4cc;
  border-radius: 6px;

  .el-input__inner {
    border: none;
  }

  .el-input-group__append {
    background-color: #fff;
    border: none;
    color: var(--primary-color);
    cursor: pointer;
  }
}

/deep/ .is-error {
  .el-input {
    border-color: #f56c6c;
  }
}

.loginBtn {
  width: 108px;
  height: 36px;
  margin: 0px auto;
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

.authority {
  font-size: 14px;
  font-weight: 400;
  color: #3f4254;
  line-height: 22px;
  margin-top: 20px;
}
</style>
