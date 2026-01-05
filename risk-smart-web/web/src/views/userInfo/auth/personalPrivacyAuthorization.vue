<template>
  <div class="personalPrivacyAuthorization">
    <template v-if="status == 1">
      <header>
        <div class="info">
          <p>个人实名认证</p>
          <p>请认真填写并保证信息正确无误！</p>
        </div>
        <div class="cirularProgress">
          <el-progress
            type="circle"
            :percentage="percentage"
            :show-text="false"
          ></el-progress>
          <div class="tips">
            <p>进度</p>
            <p>{{ percentage }}%</p>
          </div>
        </div>
      </header>
      <main>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          :hide-required-asterisk="true"
        >
          <el-form-item
            label="姓名"
            prop="name"
          >
            <el-input
              v-model="form.name"
              disabled
              placeholder="请填写"
            ></el-input>
          </el-form-item>
          <el-form-item
            label="身份证号"
            prop="idNumber"
          >
            <el-input
              v-model="form.idNumber"
              disabled
              placeholder="请填写"
            ></el-input>
          </el-form-item>
          <el-form-item
            label="手机号"
            class="inputCode"
            prop="mobile"
          >
            <el-input
              v-model="form.mobile"
              disabled
              placeholder="请填写"
            ></el-input>
            <span
              class="send"
              @click="sendLogin"
            >{{
              !state ? '获取验证码' : time
            }}</span>
          </el-form-item>
          <el-form-item
            label="验证码"
            prop="verifyCode"
          >
            <el-input
              v-model="form.verifyCode"
              placeholder="请填写"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-button
          type="primary"
          :disabled="btnLoading"
          @click="handleSubmit"
        >
          开始人脸识别认证 <i
            class="el-icon-loading"
            v-if="btnLoading"
          ></i>
        </el-button>
      </main>
    </template>
    <div
      class="landingPage"
      v-else
    >
      <img :src="
          require(`@/assets/images/comment/${statusMap.get(status).icon}.png`)
        " />
      <span class="status">{{ statusMap.get(status).label }}</span>
      <span class="tips">{{
        status == 4
          ? '本次授权申请已失效，请联系授权发起人重新激活'
          : '本次授权结束，感谢您的配合'
      }}</span>

      <el-button
        type="primary"
        v-if="showBtn"
        @click="handleTry"
      >
        再试一次
      </el-button>
    </div>
    <el-dialog
      :visible.sync="dialogVisible"
      width="75%"
      :before-close="dialogBeforeClose"
      :show-close="false"
    >
      <div class="dialogHeader">
        <span>温馨提示</span>
      </div>
      <div class="dialogContent">
        <span>
          {{ name }}您好，代理机构{{ deptName }}，正在申请尽调服务。<br />
          您在开始授权认证之前，应当认真阅读<a href="#">《授权协议》</a><a href="#">《尽职调查报告服务协议》</a>，充分理解协议中相关条款内容。<br />
          【特别提示】<b>请您在点击“同意”之前仔细阅读本授权协议</b>，确保对其内容特别是字体加黑内容的含义及相应法律后果已全部知晓并充分理解。<b>您点击“同意”并确认提交即视为您接受本个人信息保护政策，我方将按照相关法律法规及本政策来合法使用和保护您的个人信息。</b>
        </span>
        <div class="agreement">
          <div
            class="checkBox"
            :class="{ activeCheckBox: checked }"
            @click="handleClickCheckBox"
          >
            <i
              class="el-icon-check"
              v
              v-if="checked"
            />
          </div>
          <span>本人已阅读并同意<a href="#">《授权协议》</a><a href="#">《尽职调查报告服务协议》</a></span>
        </div>
        <div class="bottomBtns">
          <el-button
            type="primary"
            :disabled="!checked"
            @click="dialogBeforeClose"
          >同意</el-button>
          <el-button @click="closeCurrentPage">不同意</el-button>
        </div>
      </div>
    </el-dialog>
    <GutuProtocolRead
      ref="gutuProtocolReadRef"
      @confirm="checked = true"
    />
  </div>
</template>

<script>
import {
  refuseAccredit,
  getPersonInfo,
  getVerificationCode,
  personThreeEleAuth,
} from '@/api/base/userAuth'
import GutuProtocolRead from '@/components/gutu/gutuProtocolRead.vue'
export default {
  components: {
    GutuProtocolRead,
  },
  data() {
    return {
      state: false,
      time: 60,
      percentage: 0,
      dialogVisible: true,
      checked: false,
      form: {
        name: null,
        idNumber: null,
        mobile: null,
        verifyCode: null,
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        idNumber: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
        ],
        mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        verifyCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
        ],
      },
      showBtn: false,
      status: 1, //1-待授权 2-已授权 3-拒绝授权 4-授权失效
      statusMap: new Map([
        [2, { label: '授权成功', icon: 'success' }],
        [3, { label: '授权失败', icon: 'error' }],
        [4, { label: '授权失效', icon: 'error' }],
      ]),
      taskNo: null,
      name: null,
      deptName: null,
      backUpForm: null,
      btnLoading: false,
    }
  },
  watch: {
    '$route.query': {
      handler(val) {
        if (val.hasOwnProperty('type')) {
          this.showBtn = true
        }
        if (val.hasOwnProperty('status')) {
          this.status = Number(val.status)
          this.dialogVisible = false
          return
        }
        this.taskNo = val.taskNo || null
        this.name = val.name || null
        this.deptName = val.deptName || null
        if (this.taskNo) this.handleCheckStatus()
      },
      immediate: true,
    },
    form: {
      handler(val) {
        let num = 0
        for (let key in val) {
          if (![null, '', undefined].includes(val[key])) {
            num++
          }
        }
        this.percentage = (num / 4) * 100
      },
      deep: true,
    },
  },
  mmounted() { },
  methods: {
    handleCheckStatus() {
      getPersonInfo({ taskNo: this.taskNo })
        .then((res) => {
          if (res.code == 200) {
            let data = res.data
            if (data.status == 1) {
              this.backUpForm = data
            } else {
              this.dialogVisible = false
              this.status = data.status
            }
          }
        })
        .catch((err) => { })
    },
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.btnLoading = true
          personThreeEleAuth({
            taskNo: this.taskNo,
            smsVerificationCode: this.form.verifyCode,
          })
            .then((res) => {
              if (res.code == 200) {
                setTimeout(() => {
                  // window.open(res.data)
                  window.location.href = res.data
                  this.btnLoading = false
                }, 0)
              }
            })
            .catch((err) => { })
        }
      })
    },
    sendLogin() {
      if (this.state) return
      this.$refs.formRef.validateField('mobile', (err) => {
        if (err == '') {
          this.state = true //开启倒计时
          getVerificationCode({ mobile: this.form.mobile })
            .then((res) => {
              this.$message.success('发送成功')
              this.startTimer()
            })
            .catch((err) => {
              console.log(err)
              this.state = false //关闭倒计时
              this.time = 60
            })
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
    handleClickCheckBox() {
      if (this.checked) {
        this.checked = false
        return
      }
      this.$refs.gutuProtocolReadRef.open()
    },

    dialogBeforeClose() {
      for (let key in this.form) {
        if (this.backUpForm[key] != null) this.form[key] = this.backUpForm[key]
      }
      this.dialogVisible = false
    },
    closeCurrentPage() {
      refuseAccredit({ taskNo: this.taskNo })
        .then((res) => {
          if (res.code == 200) {
            this.dialogVisible = false
            this.status = 3
          }
        })
        .catch((err) => { })
    },
    handleTry() {
      this.status = 1
    },
  },
}
</script>

<style lang="less" scoped>
.personalPrivacyAuthorization {
  font-family: PingFang SC-Regular;
  background-color: #f3f3f3;
  width: var(--bgvw);
  height: var(--bgvh);
  header {
    width: 100%;
    height: 90px;
    background: linear-gradient(180deg, #d4e7fa 0%, #ffffff 100%);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    .info {
      p {
        font-size: 13px;
        color: rgba(#000, 0.3);
        margin-bottom: 0px;
      }
      p:first-child {
        font-family: Alimama ShuHeiTi-Bold;
        font-size: 23px;
        font-weight: 700;
        color: #3662ec;
        margin-bottom: 5px;
      }
    }
    .cirularProgress {
      position: relative;
      /deep/.el-progress {
        .el-progress-circle {
          width: 53px !important;
          height: 53px !important;
        }
      }
      .tips {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        display: flex;
        align-items: center;
        flex-direction: column;
        p {
          margin-bottom: 0px;
          color: rgba(#000, 0.6);
          font-size: 11px;
        }
        p:last-child {
          color: rgba(#000, 0.85);
          font-family: PingFang SC-Medium;
          font-size: 12px;
        }
      }
    }
  }
  main {
    padding: 20px;
    ::v-deep(.el-form) {
      background: #fff;
      padding: 20px;
      .el-form-item {
        padding: 15px 0px;
        border-bottom: 1px solid #f0f0f0;
        margin-bottom: 0px;
      }
      .el-form-item:first-child {
        padding-top: 0px;
      }
      .el-form-item:last-child {
        border: none;
        padding-bottom: 0px;
      }
      .el-form-item__label {
        text-align: left;
        font-size: 14px;
      }
      .el-input.is-disabled .el-input__inner {
        background-color: #fff !important;
      }
      .el-input__inner {
        font-size: 14px;
        border: none;
      }
      .el-form-item__error {
        padding-left: 15px;
      }
    }
    .inputCode {
      position: relative;
      /deep/.el-input__inner {
        width: 60%;
      }
      .send {
        position: absolute;
        top: 50%;
        right: 0px;
        transform: translate(0px, -50%);
        font-size: 15px;
        color: #3662ec;
      }
    }
    .el-button {
      width: 100%;
      margin-top: 15px;
      height: 44px;
      border-radius: 12px;
      font-size: 14px;
    }
  }

  .landingPage {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    > img {
      width: 53px;
      height: 53px;
      margin-top: 60%;
      margin-bottom: 20px;
    }

    .status {
      font-family: PingFang SC-Medium;
      font-size: 18px;
      color: #333;
      margin-bottom: 10px;
      font-weight: 600;
    }
    .tips {
      font-size: 14px;
      color: #9e9e9e;
    }

    .el-button {
      width: calc(100% - 60px);
      height: 49px;
      font-size: 14px;
      margin-top: 20px;
      background: #3662ec;
      border: none;
      border-radius: 6px;
    }
  }
}
::v-deep(.el-dialog) {
  border-radius: 12px;
  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 15px;
    color: rgba(#000, 0.85);

    .dialogHeader {
      width: 100%;
      text-align: center;
      font-family: PingFang SC-Medium;
      font-size: 18px;
      margin: 5px 0px 15px;
    }
    .dialogContent {
      font-size: 14px;
      // line-height: 26px;

      .agreement {
        display: flex;
        align-items: flex-start;
        margin-top: 15px;
        color: rgba(#000, 0.6);

        .checkBox {
          min-width: 16px;
          height: 16px;
          margin-right: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid rgba(#000, 0.6);
          border-radius: 3px;
          margin-top: 4px;
        }
        .activeCheckBox {
          border-color: #409eff;
          background: #409eff;
          color: #fff;
          font-size: 14px;
        }
      }
      .bottomBtns {
        width: 100%;
        display: flex;
        flex-direction: column;
        margin-top: 15px;

        .el-button {
          margin-left: 0px;
          height: 44px;
          border: none;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
