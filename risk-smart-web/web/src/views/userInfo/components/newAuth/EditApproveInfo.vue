<!--
 * @Date: 2022-04-25 19:13:46
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-07-01 18:26:11
 * @name:
 * @FilePath: \gutu-projeckt\smartValue\src\userInfo\components\EditApproveInfo.vue
-->
<template>
  <div
    style="height: 100%"
    class="enterprisecert-2"
  >
    <!-- 头部信息 -->
    <div class="formBox">
      <div class="tip">
        <div class="icon">
          <i class="el-icon-info"></i>
        </div>
        <span class="tip-text">您提交的对公账户资料({{ bankInfo.bankName }}：{{
            bankInfo.bankAccount
          }})，初步审核通过<br />
          平台已于{{ NowTime }}给账户“{{
            bankInfo.enterpriseName
          }}”提交一笔随机数目的打款申请，因银行处理速度不同，请耐心等待。
          查询明细后，在此确认打款金额。</span>
      </div>
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="170px"
      >
        <el-form-item
          label="汇款账户名："
          class="readonly"
        >
          <el-input value="估图（上海）科技有限公司" />
        </el-form-item>
        <el-form-item
          label="汇款银行："
          class="readonly"
        >
          <el-input value="招商银行" />
        </el-form-item>
        <el-form-item
          label="汇款账户："
          class="readonly"
        >
          <el-input value="121931876810701" />
        </el-form-item>
        <el-form-item
          label="认证企业："
          class="readonly"
        >
          <el-input :value="bankInfo.enterpriseName" />
        </el-form-item>
        <el-form-item
          label="企业对公银行账号："
          class="readonly"
        >
          <el-input :value="bankInfo.bankAccount" />
        </el-form-item>
        <el-form-item
          label="开户支行："
          class="readonly"
        >
          <el-input :value="bankInfo.bankBranch" />
        </el-form-item>
        <el-form-item
          label="到账金额："
          prop="money"
        >
          <el-input
            v-model="form.money"
            placeholder="请填写"
          >
            <span slot="suffix">元</span>
          </el-input>
        </el-form-item>
      </el-form>
      <div class="drawer-footer">
        <el-button
          v-prevent-re-click
          type="primary"
          @click="onSubmit"
        >确认提交</el-button>
      </div>
    </div>
    <!--		打款金额错误提示-->
    <el-dialog
      title="提示"
      :visible.sync="failDialog"
      width="32%"
      class="fail-dialog"
    >
      <AuthFail />
    </el-dialog>
  </div>
</template>

<script>
// import imgUrl from "@/components/utils/avatar.js";
import { InfoTitle, AuthFail } from '@/views/userInfo/components/components'
import { moneyCert, list, getLoginInfo } from '@/api/base/index.js'

export default {
  name: 'PersonInfo',
  components: {
    InfoTitle,
    AuthFail,
  },
  data() {
    return {
      form: {
        money: 0.01,
      },
      rules: {
        money: [
          { required: true, message: '请填写到账金额' },
          {
            validator: (rule, value, callback) => {
              if (value > 0.99 || value < 0.01) {
                callback(new Error('请正确填写到账金额'))
                return
              }
              callback()
            },
            trigger: 'blur',
          },
        ],
      },
      bankInfo: {},
      centerDialogVisible: true,
      NowTime: null,
      failDialog: false, //打款金额错误提示
    }
  },
  created() {
    this.NowTime = this.getNowTime()
    this.bankInfo = JSON.parse(localStorage.getItem('bankInfo')) || {}
  },
  methods: {
    onSubmit() {
      let userinfo = JSON.parse(localStorage.getItem('userInfo'))
      this.$refs.form.validate((valid) => {
        if (valid) {
          let data = JSON.parse(localStorage.getItem('data'))
          moneyCert({
            ...this.form,
            ...data,
          })
            .then((res) => {
              if (res.code === 200) {
                this.$message.success('到账金额填写正确，企业信息认证成功!')
                getLoginInfo().then((res1) => {
                  localStorage.setItem('userInfo', JSON.stringify(res1.user))
                  list({ enterpriseId: 1 }).then((res2) => {
                    if (res2.code === 200) {
                      localStorage.setItem(
                        'companyinfo',
                        JSON.stringify(res2.data)
                      )
                      this.$alert(
                        '到账金额填写正确，企业信息认证成功！',
                        '成功',
                        {
                          confirmButtonText: '确定',
                          callback: (action) => {
                            userinfo.enterpriseAuthStatus = 2 //认证成功
                            localStorage.setItem(
                              'userInfo',
                              JSON.stringify(userinfo)
                            )
                            this.$emit('back')
                          },
                        }
                      )
                    }
                  })
                })
              }
            })
            .catch((err) => {
              this.failDialog = false
              userinfo.enterpriseAuthStatus = 3 //认证失败
              localStorage.setItem('userInfo', JSON.stringify(userinfo))
              this.$emit('back')
            })
        }
      })
    },

    // 获取当前时间
    getNowTime() {
      const yy = new Date().getFullYear()
      const MM =
        new Date().getMonth() + 1 < 10
          ? '0' + (new Date().getMonth() + 1)
          : new Date().getMonth() + 1
      const dd =
        new Date().getDate() < 10
          ? '0' + new Date().getDate()
          : new Date().getDate()
      const HH =
        new Date().getHours() < 10
          ? '0' + new Date().getHours()
          : new Date().getHours()
      const mm =
        new Date().getMinutes() < 10
          ? '0' + new Date().getMinutes()
          : new Date().getMinutes()
      const ss =
        new Date().getSeconds() < 10
          ? '0' + new Date().getSeconds()
          : new Date().getSeconds()
      return yy + '年' + MM + '月' + dd + '日' + HH + ':' + mm
    },
  },
}
</script>

<style lang="less" scoped>
@import "@/assets/scss/productConfiguration";

.formBox {
  // padding: 20px 522px 20px 20px;
  overflow-y: auto;
  // height: calc(100% - 90px);
}

.enterprisecert-2 {
  .tip {
    background: #edf5ff;
    border-radius: 6px;
    border: 1px solid #97c3ff;
    display: flex;
    align-items: center;
    padding: 20px 16px;

    .icon {
      font-size: 20px;
      color: #1677ff;
    }

    .tip-text {
      margin-left: 10px;
      color: #666666;
      font-weight: 400;
    }
  }

  /deep/ .el-form {
    margin-top: 25px;

    &-item {
      margin-bottom: 0;

      &__label {
        height: 37px;
        line-height: 17px;
        padding-right: 44px;
        justify-content: flex-end;
      }

      &.readonly {
        .el-input__inner {
          padding: 0;
          height: 37px;
          line-height: 17px;
          font-weight: 500;
          color: #414355;
          background: transparent;
        }
      }
    }
  }

  .drawer-footer {
    margin-top: 62px;
  }
}

.fail-dialog {
  /deep/ .el-dialog {
    height: auto;

    &__body {
      padding: 24px 38px 73px;
    }
  }
}
</style>
