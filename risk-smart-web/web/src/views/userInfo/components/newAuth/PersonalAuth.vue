<template>
  <div class="personalAuth" v-loading="loading">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="110px"
      v-if="!authStatus"
    >
      <el-form-item label="真实姓名：" prop="realName">
        <el-input
          v-model="form.realName"
          placeholder="请输入"
          size="normal"
          clearable
        />
      </el-form-item>
      <el-form-item label="身份证号：" prop="idNumber">
        <el-input
          v-model="form.idNumber"
          placeholder="请输入"
          size="normal"
          clearable
        />
      </el-form-item>
      <!-- <el-form-item label="手机号(测试)：">
        <el-input
          v-model="form.mobile"
          placeholder="请输入"
          size="normal"
          clearable
        />
      </el-form-item> -->
      <el-form-item label="手机号：" prop="mobile">
        <!-- <div class="number">
          <span>{{
            userInfo && userInfo.phonenumber
              ? userInfo.phonenumber.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
              : '-'
          }}</span>
        </div> -->
        <el-input v-model="form.mobile" placeholder="请输入手机号"></el-input>
        <span>请确保注册手机号可用</span>
      </el-form-item>
      <el-form-item label="验证码" class="inputCode" prop="verifyCode">
        <el-input
          v-model="form.verifyCode"
          placeholder="请输入验证码"
          size="normal"
        />
        <div class="code" @click="getCode">
          {{ state ? time : '获取验证码' }}
        </div>
      </el-form-item>
    </el-form>
    <div class="authCard" v-else>
      <div class="authCard-row" v-for="(item, index) in infoList" :key="index">
        <span class="label">{{ item.label }}：</span>
        <span class="content">{{ item.data }}</span>
      </div>
    </div>
    <div
      class="bottomBtns"
      :style="{ 'padding-left': !authStatus ? 20 : 0 + 'px' }"
    >
      <el-button v-if="!authStatus" class="authBtn" @click="handleSubmit">
        认证
      </el-button>
      <el-button @click="handleClose">返回</el-button>
    </div>
  </div>
</template>

<script>
import {
  sendSmsAuthCode,
  userInfoSubmit,
  userInfoList,
  getUserAuth,
} from '@/api/base/userAuth'
import { getLoginInfo } from '@/api/base/index.js'
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      form: {
        realName: null,
        idNumber: null,
        verifyCode: null,
        mobile: null,
      },
      rules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
        ],
        idNumber: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          {
            pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
            message: '请输入正确的身份证号',
          },
        ],
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          {
            pattern: /^(?:(?:\+|00)86)?1[3-9]\d{9}$/,
            message: '请输入正确的手机号',
          },
        ],
        verifyCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
        ],
      },
      infoList: [
        { label: '真实姓名', field: 'realName', data: '-' },
        { label: '身份证号', field: 'idNumber', data: '-' },
        { label: '手机号', field: 'mobile', data: '-' },
      ],
      time: 60, //60秒倒计时
      state: false, //倒计时状态
      authStatus: false,
      loading: true,
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
  },
  mounted() {
    this.init()
  },
  methods: {
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          // let param = { ...this.form }
          // param.mobile = this.userInfo.phonenumber
          // console.log(param, 'params=-----------')
          // return
          userInfoSubmit({ ...this.form })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('认证成功')
                setTimeout(() => {
                  this.init()
                  this.resetUserInfo()
                }, 500)
              }
            })
            .catch((err) => {})
        }
      })
    },
    init() {
      this.loading = true
      getUserAuth()
        .then((reponse) => {
          let { code, data } = reponse
          if (code == 200) {
            let status = data.authStatus
            this.loading = false
            if (status == 1) {
              this.authStatus = true
              userInfoList()
                .then((res) => {
                  if (res.code == 200) {
                    let data = res.data
                    this.infoList.forEach((item) => {
                      if (data[item.field] != null) item.data = data[item.field]
                    })
                  }
                })
                .catch((err) => {})
            } else {
              this.authStatus = false
            }
          }
        })
        .catch((err) => {})
    },
    resetUserInfo() {
      getLoginInfo()
        .then((res) => {
          // this.userInfo = res.user
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
    getCode() {
      this.$refs.formRef.validateField('mobile', (valid) => {
        if (valid == '') {
          sendSmsAuthCode({
            mobile: this.form.mobile,
            type: 'PERSONAL_AUTH',
          })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('发送成功')
                this.state = true
                this.startTimer()
              }
            })
            .catch((err) => {})
        }
      })
    },
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
    handleClose() {
      this.$emit('close')
    },
  },
}
</script>

<style lang="less" scoped>
.personalAuth {
  ::v-deep(.el-form) {
    .el-form-item {
      &:has(.el-checkbox-group) {
        .el-form-item__label {
          line-height: 22px;
        }
      }
      .el-form-item__label {
        line-height: 48px;
      }
      .el-form-item__content {
        width: 580px;
      }
    }
    .el-input,
    .el-textarea {
      .el-input__inner,
      .el-textarea__inner,
      .el-input__count {
        background-color: #f4f6f9;
      }
      .el-input__inner {
        height: 48px;
      }
    }
    .el-select,
    .el-cascader {
      width: 100%;
    }
    .el-checkbox {
      margin-right: 0;

      &:nth-child(6) {
        margin-left: 0;
      }

      .el-checkbox__inner {
        border: none;
        background: transparent;
      }

      &.is-checked {
        background: var(--primary-color);
        border-color: var(--primary-color);
        .el-checkbox__label {
          color: #fff;
        }
        .el-checkbox__inner {
          background: transparent;
        }
      }
    }
  }

  .number {
    display: flex;
    flex-direction: column;
    line-height: 48px;
    position: relative;
    margin-bottom: 10px;

    > span {
      font-size: 14px;
      color: rgba(#000, 0.6);
    }
    > span:first-child {
      font-family: PingFang SC-Medium;
      color: #414355;
      font-weight: 500;
    }
    > span:last-child {
      position: absolute;
      top: 25px;
    }
  }
  ::v-deep(.inputCode) {
    .el-form-item__content {
      width: 260px !important;
    }
    .el-input {
      .el-input__inner {
        padding-right: 100px;
      }
    }
    .code {
      text-align: center;
      position: absolute;
      top: 50%;
      right: 0;
      transform: translate(0, -50%);
      width: 95px;
      height: 20px;
      line-height: 20px;
      padding: 0px 12px;
      border-left: 1px solid rgba(#000, 0.08);
      color: #3662ec;
      cursor: pointer;
    }
  }
  .bottomBtns {
    padding-left: 90px;
    margin-top: 20px;
    .el-button {
      width: 96px;
      height: 50px;
      border-radius: 6px;
      font-family: PingFang SC-Medium;
      font-size: 18px;
      color: rgba(#000, 0.6);
    }
    .authBtn {
      background: var(--primary-color);
      color: #fff;
    }
  }

  .authCard {
    width: 100%;
    padding: 20px;
    background: #f6f7f9;
    &-row {
      width: 100%;
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      .label {
        white-space: nowrap;
        color: #333333;
      }
      .content {
        color: #414355;
        font-family: PingFang SC-Medium;
        font-weight: 500;
      }
    }
    &-row:last-child {
      margin-bottom: 0px;
    }
  }
}
</style>
