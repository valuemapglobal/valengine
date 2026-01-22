<template>
  <div class="enterpriseAuth">
    <div v-if="step == 0">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="企业名称：" prop="enterpriseName">
          <el-input
            v-model="form.enterpriseName"
            placeholder="请输入企业名称"
            size="normal"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item prop="creditCode" class="longLabel">
          <template slot="label">社会统一<br />信用代码：</template>
          <el-input v-model="form.creditCode" clearable placeholder="请填写" />
        </el-form-item>
        <el-form-item label="法人姓名：" prop="legalPerson">
          <el-input v-model="form.legalPerson" clearable placeholder="请填写" />
        </el-form-item>
        <el-form-item label="企业行业：" prop="industryType">
          <el-select
            :popper-append-to-body="false"
            v-model="form.industryType"
            clearable
          >
            <el-option
              v-for="item in CompanyIndustryTypes"
              :key="item.value"
              :label="item.label"
              :value="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="企业规模：" prop="enterpriseSize">
          <el-select
            :popper-append-to-body="false"
            v-model="form.enterpriseSize"
            clearable
          >
            <el-option
              v-for="item in CompanyScales"
              :key="item.value"
              :label="item.label"
              :value="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="银行开户名：" prop="bankAccountName">
          <el-input
            v-model="form.bankAccountName"
            placeholder="请输入银行开户名"
            size="normal"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="银行账户：" prop="bankAccount">
          <el-input
            v-model="form.bankAccount"
            placeholder="请输入银行账户"
            size="normal"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="开户银行：" prop="bankCode">
          <el-select
            v-model="form.bankCode"
            placeholder="请选择开户银行"
            clearable
            filterable
          >
            <el-option
              v-for="item in bankCodeOptions"
              :key="item.bankCode"
              :label="item.bankName"
              :value="item.bankCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开户支行：" prop="bankBranch">
          <el-input
            v-model="form.bankBranch"
            clearable
            placeholder="请填写开户支行"
          />
        </el-form-item>
        <el-form-item label="开户所在省市：" class="rowItem">
          <el-form-item prop="bankProvinceId">
            <el-select
              :popper-append-to-body="false"
              v-model="form.bankProvinceId"
              clearable
              @change="provinceChange"
            >
              <el-option
                v-for="item in provinceOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
          <el-form-item prop="bankCityId">
            <el-select
              :popper-append-to-body="false"
              v-model="form.bankCityId"
              clearable
              @change="cityChange"
            >
              <el-option
                v-for="item in cityOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
        </el-form-item>
      </el-form>
      <div class="bottomBtns">
        <el-button class="authBtn" :disabled="btnLoading" @click="handleNext">
          {{ btnLoading ? '提交中' : '下一步' }}
          <i v-if="btnLoading" class="el-icon-loading" />
        </el-button>
        <el-button :disabled="btnLoading" @click="handleClose">返回</el-button>
      </div>
    </div>
    <div v-else>
      <div class="tips">
        <i class="el-icon-warning" />
        <span>
          平台已向您的对公账户转入随机金额(0.01-~0.99元，0~3天到账，请注意查收并回填"到账金额"，8天内验证有效收。
        </span>
      </div>
      <div class="infoCard">
        <div class="rowInfo" v-for="(item, index) in infoList" :key="index">
          <span class="label">{{ item.label }}：</span>
          <span class="content">{{ item.data }}</span>
        </div>
      </div>
      <el-form
        ref="collectionFormRef"
        :model="collectionForm"
        :rules="collectionRules"
        label-width="100px"
      >
        <el-form-item label="收款金额" prop="money">
          <el-input
            v-model="collectionForm.money"
            placeholder="请输入收款金额"
            size="normal"
            clearable
          />
        </el-form-item>
        <div class="inputTips">
          <p>1.收款金额在数字0.01~0.99之间;</p>
          <p>2.您有<span>2次验证机会</span>，若2次都输错金额，需重新认证</p>
        </div>
      </el-form>
      <div class="bottomBtns" style="padding-left: 100px">
        <el-button class="authBtn" v-prevent-re-click @click="handleSubmit">
          提交审核
        </el-button>
        <!-- <el-button @click="step--">上一步</el-button> -->
      </div>
    </div>
  </div>
</template>

<script>
import { bankList, moneyCert, list, getLoginInfo } from '@/api/base'
import { authSubmit } from '@/api/base/userAuth'
import { getRegionData } from '@/api/financing/productConfiguration'
import { CompanyIndustryTypes, CompanyScales } from '@/views/userInfo/const'
export default {
  data() {
    return {
      form: {
        enterpriseName: null,
        creditCode: null,
        legalPerson: null,
        industryType: null,
        enterpriseSize: null,
        bankAccountName: null,
        bankAccount: null,
        bankCode: null,
        bankBranch: null,
        bankProvinceId: null,
        bankCityId: null,
      },
      rules: {
        enterpriseName: { required: true, message: '请填写企业名称' },
        creditCode: { required: true, message: '请填写社会统一信用代码' },
        legalPerson: { required: true, message: '请填写法人姓名' },
        industryType: { required: true, message: '请选择企业行业' },
        enterpriseSize: { required: true, message: '请选择企业规模' },
        bankAccountName: { required: true, message: '请填写银行开户名' },
        bankAccount: {
          required: true,
          validator: (rule, value, callback) => {
            if (!value) {
              callback(new Error('请选择对公银行账户'))
              return
            }
            if (!/^\d+$|^\d+[.]?\d+$/.test(value)) {
              callback(new Error('对公银行账户只能是数字'))
              return
            }
            if (value.length < 12 || value.length > 19) {
              callback(new Error('最小限制12个数字，最大限制19个数字'))
              return
            }
            callback()
          },
        },
        bankCode: { required: true, message: '请选择开户行' },
        bankBranch: { required: true, message: '请选择开户支行' },
        bankProvinceId: { required: true, message: '请选择省' },
        bankCityId: { required: true, message: '请选择市' },
      },
      collectionForm: {
        money: null,
      },
      collectionRules: {
        money: [
          { required: true, message: '请填写付款金额' },
          { pattern: /^0\.[0-9]{2}$/, message: '请输入正确的金额' },
        ],
      },
      step: 1,
      btnLoading: false,
      bankCodeOptions: [],
      provinceOptions: [],
      cityOptions: [],
      CompanyIndustryTypes,
      CompanyScales,
      authStatus: false,
      infoList: [
        {
          label: '汇款方',
          field: 'enterpriseName',
          data: '估图（上海）科技有限公司',
        },
        { label: '汇款银行', field: 'enterpriseName', data: '招商银行' },
        { label: '汇款账户', field: 'enterpriseName', data: '121931876810701' },
      ],
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      bankList().then((res) => {
        if (res.code === 200) {
          this.bankCodeOptions = res.data
        }
      })
      getRegionData().then((res) => {
        this.provinceOptions = res.data
        if (this.form.bankProvinceId && this.form.bankProvinceId !== '0') {
          this.provinceChange(this.form.bankProvinceId, false)
        }
      })
    },
    handleNext() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          let params = { ...this.form }

          authSubmit(params).then((res) => {
            if (res.code === 200) {
              // Notification.success(res.msg)
              this.$message.success(res.msg)
              localStorage.setItem(
                'enterpriseAuthData',
                JSON.stringify(res.data)
              )
              this.step = 1
              getLoginInfo().then((res) => {
                this.$store.commit('setUserInfo', res.user)
                this.$store.commit(
                  'setUserAuthInfo',
                  res.userAuthPersonFinancingExpertInfo
                )
              })
              // localStorage.setItem(
              //   'bankInfo',
              //   JSON.stringify({
              //     ...this.form,
              //     bankName: this.bankCodeOptions.filter((res) => {
              //       return res.bankCode === this.form.bankCode
              //     })[0].bankName, //到账银行
              //   })
              // )
            } else {
              this.$message.warning(res.msg)
            }
          })
        }
      })
    },
    handleSubmit() {
      let userinfo = JSON.parse(localStorage.getItem('userInfo'))
      this.$refs.collectionFormRef.validate((valid) => {
        if (valid) {
          let data = JSON.parse(localStorage.getItem('enterpriseAuthData'))
          moneyCert({
            ...this.collectionForm,
            ...data,
          })
            .then((res) => {
              if (res.code == 200) {
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
              userinfo.enterpriseAuthStatus = 3 //认证失败
              localStorage.setItem('userInfo', JSON.stringify(userinfo))
              // this.$emit("back");
            })
        }
      })
    },
    provinceChange(value, isChange = true) {
      this.cityOptions = [] //省改变，则市数据清空
      if (isChange) {
        this.cityChange(null)
      }
      if (!value) return
      //获取当前选中省的下标值，给市选项复制用
      let _index = this.provinceOptions.findIndex((item) => {
        if (item.code === value) {
          return item
        }
      })
      if (_index > -1) this.cityOptions = this.provinceOptions[_index].childs
    },
    cityChange(value) {
      this.form.bankCityId = value
    },
  },
}
</script>

<style lang="less" scoped>
.enterpriseAuth {
  width: 100%;
  height: calc(var(--bgvh) - 190px);
  overflow-y: auto;

  ::v-deep(.el-form) {
    .el-form-item {
      &:has(.el-checkbox-group) {
        .el-form-item__label {
          line-height: 22px;
        }
      }
      .el-form-item__label {
        line-height: 48px;
        color: #333;
      }
      .el-form-item__content {
        width: 580px;
        color: #414355;
      }
    }
    .el-input,
    .el-textarea {
      .el-input__inner,
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
      .el-select-dropdown {
        top: auto !important;
        bottom: 52px !important;
      }
      .popper__arrow {
        display: none;
      }
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
  ::v-deep(.rowItem) {
    > .el-form-item__content {
      display: flex;
      .el-form-item {
        width: calc(50% - 10px);
        margin-left: 20px;
        margin-bottom: 0px;
        .el-form-item__content {
          width: 100%;
        }
      }
      .el-form-item:first-child {
        margin-left: 0px;
      }
    }
  }
  ::v-deep(.longLabel) {
    .el-form-item__label {
      line-height: 20px !important;
    }
  }
  .bottomBtns {
    padding-left: 130px;
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
      width: 120px;
      background: var(--primary-color);
      color: #fff;
    }
  }
  .tips {
    width: 100%;
    height: 48px;
    background: #edf5ff;
    border: 1px solid #97c3ff;
    margin-bottom: 20px;
    border-radius: 6px;
    padding: 0px 16px;
    display: flex;
    align-items: center;
    color: #666;
    font-size: 14px;

    .el-icon-warning {
      margin-right: 8px;
      color: #1677ff;
    }
  }
  .infoCard {
    background: #f6f7f9;
    border-radius: 6px;
    padding: 20px;
    margin-bottom: 20px;
    .rowInfo {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      .label,
      .content {
        font-size: 14px;
      }
      .label {
        text-align: right;
        min-width: 90px;
        color: #333333;
      }
      .content {
        font-family: PingFang SC-Medium;
        color: #414355;
      }
    }
    .rowInfo:last-child {
      margin-bottom: 0px;
    }
  }
  .inputTips {
    padding-left: 100px;
    margin-top: 20px;
    p {
      margin-bottom: 0px;
      color: rgba(#000, 0.6);
      font-size: 14px;
      line-height: 20px;
      > span {
        color: #ff8f1f;
      }
    }
  }
}
</style>
