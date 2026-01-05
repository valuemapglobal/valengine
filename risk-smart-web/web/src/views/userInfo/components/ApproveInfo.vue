<template>
  <div style="height: 100%">
    <InfoTitle
      title="企业认证"
      tips="将由本平台汇款至对公账户，以核对企业信息。请您关注账户的交易记录"
    >
      <template slot="right">
        <el-button type="primary" @click="onSubmit"> 提交认证 </el-button>
      </template>
    </InfoTitle>
    <div class="formBox">
      <div class="enterprise-form">
        <el-form ref="form" :model="form" :rules="rules" label-width="143px">
          <div class="enterprise-form-item">
            <div class="enterprise-form-title">1.企业基本信息</div>
            <el-form-item label="企业名称：" prop="enterpriseName">
              <el-input
                v-model="form.enterpriseName"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="社会统一信用代码：" prop="creditCode">
              <el-input
                v-model="form.creditCode"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="法人姓名：" prop="legalPerson">
              <el-input
                v-model="form.legalPerson"
                clearable
                placeholder="请填写"
              />
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
            <el-form-item label="营业执照：" prop="file">
              <template>
                <div
                  class="license-img el-upload-list--picture-card"
                  v-if="form.businessUrl"
                >
                  <div class="el-upload-list__item">
                    <el-image :src="form.businessUrl" fit="scale-down" />
                    <div class="el-upload-list__item-actions">
                      <span
                        class="el-upload-list__item-delete"
                        @click="handleRemove"
                      >
                        <i class="el-icon-delete"></i>
                      </span>
                    </div>
                  </div>
                </div>
                <el-upload
                  action="#"
                  :accept="uploadInfo.accept"
                  list-type="picture-card"
                  :auto-upload="false"
                  :before-upload="beforeUpload"
                  :on-change="uploadChange"
                  class="upload-demo"
                  :class="{ 'upload-hidden': form.businessUrl }"
                >
                  <div class="upload-tip">
                    <div class="el-icon-plus" />
                    <div class="upload-tip-text">上传正面照</div>
                  </div>
                </el-upload>
                <div class="upload-footer">支持jpg、png等格式</div>
              </template>
            </el-form-item>
          </div>
          <div class="enterprise-form-item">
            <div class="enterprise-form-title">2.对公银行账户信息</div>
            <el-form-item label="银行开户名：" prop="bankAccountName">
              <el-input
                v-model="form.bankAccountName"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="对公银行账户：" prop="bankAccount">
              <el-input
                v-model="form.bankAccount"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="开户行：" prop="bankCode">
              <el-select
                :popper-append-to-body="false"
                v-model="form.bankCode"
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
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="开户所在省市：" class="multiple" required>
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
          </div>
        </el-form>
        <div class="drawer-footer">
          <el-button v-prevent-re-click type="primary" @click="onSubmit"
            >提交认证</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { authSubmit, bankList, getLoginInfo } from '@/api/base'
import { getRegionData } from '@/api/financing/productConfiguration'
import { InfoTitle } from '@/views/userInfo/components/components'
import { CompanyIndustryTypes, CompanyScales } from '@/views/userInfo/const'
import { Notification } from 'element-ui'

export default {
  name: 'ApproveInfo',
  components: {
    InfoTitle,
  },
  data() {
    return {
      userInfo: null,
      CompanyIndustryTypes,
      CompanyScales,
      uploadInfo: {
        accept: '.jpg,.png,.jpeg',
      },
      form: {
        bankCityId: null,
        businessUrl: null, //营业执照照片
        file: null, //营业执照文件
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
      bankCodeOptions: [], //开户行选项
      provinceOptions: [], //省选项
      cityOptions: [], //市选项
    }
  },
  watch: {
    '$store.state.userInfo': {
      handler(newV) {
        this.userInfo = newV
      },
      deep: true,
      immediate: true,
    },
    '$store.state.companyinfo': {
      handler(newV) {
        if (newV) {
          this.form = newV
        } else {
          this.form = {
            bankCityId: null,
            businessUrl: null, //营业执照照片
            file: null, //营业执照文件
          }
        }
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {
    // 获取开户行--列表数据
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
  methods: {
    /**
     * 省change事件
     * @param value
     * @param isChange 是否为@change事件
     */
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
    /*
     * 上传文件之前
     * */
    beforeUpload(file) {
      const that = this
      //上传的文件类型
      const file_type = file.name
        .slice(file.name.lastIndexOf('.'))
        .toLocaleLowerCase()
      if (!that.uploadInfo.accept.split(',').includes(file_type)) {
        that.$message.warning('文件类型错误！')
        return false
      }
    },
    /**
     * 上传文件改变change
     * @param file
     * @param fileList
     */
    uploadChange(file, fileList) {
      this.form.businessUrl = file.url
      this.form.file = file.raw
    },
    /**
     * 删除已上传的文件
     */
    handleRemove() {
      this.form.businessUrl = null
      this.form.file = null
    },
    /**
     * 提交认证
     */
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          let { businessUrl, ...formData } = this.form,
            params
          if (!formData.file) {
            let { file, ...data } = formData
            params = data
          } else {
            params = formData
          }
          authSubmit(params).then((res) => {
            if (res.code === 200) {
              Notification.success(res.msg)
              localStorage.setItem('data', JSON.stringify(res.data))
              this.$emit('submit')
              getLoginInfo().then((res) => {
                this.$store.commit('setUserInfo', res.user)
                this.$store.commit(
                  'setUserAuthInfo',
                  res.userAuthPersonFinancingExpertInfo
                )
              })
              localStorage.setItem(
                'bankInfo',
                JSON.stringify({
                  ...this.form,
                  bankName: this.bankCodeOptions.filter((res) => {
                    return res.bankCode === this.form.bankCode
                  })[0].bankName, //到账银行
                })
              )
            } else {
              Notification.warning(res.msg)
            }
          })
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
@import '../../assets/scss/productConfiguration';

.formBox {
  padding: 18px 60px;
  overflow-y: auto;
  height: calc(100% - 90px);
}

.enterprise-form {
  &-title {
    font-size: 16px;
    font-weight: 600;
    color: #191c31;
    line-height: 19px;
    margin-bottom: 20px;
  }

  /deep/ .el-form {
    display: flex;
    justify-content: space-between;

    .enterprise-form-item {
      width: 46%;
    }

    &-item {
      &.is-required:not(.is-no-asterisk) > .el-form-item__label:before {
        margin-right: 0;
      }

      &__label {
        padding-right: 10px;
        justify-content: flex-end;
      }

      &.multiple {
        .el-form-item__content {
          display: flex;

          &::before,
          &::after {
            display: none;
          }
        }

        .el-form-item {
          margin: 0;
          width: 100%;
        }

        .el-form-item + .el-form-item {
          margin-left: 7px;
        }
      }

      .license-img {
        position: relative;
        width: 100%;
        height: 166px;
        border-radius: 6px;
        overflow: hidden;
        border: 1px dashed rgba(151, 151, 151, 0.4982);

        .el-image {
          width: 100%;
          height: 100%;
        }
      }

      .el-upload-list--picture-card {
        display: inline-block;

        .el-upload-list__item {
          margin: 0;
          width: 100%;
          height: 166px;
          border: none;
        }
      }

      .upload-demo {
        .el-upload-list--picture-card {
          display: none;
        }

        .el-upload {
          width: 100%;
          height: 166px;
          background: #f4f6f9;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          border-radius: 6px;
          border: 1px dashed rgba(151, 151, 151, 0.4982);

          .upload-tip {
            display: flex;
            flex-direction: column;

            .el-icon-plus {
              font-weight: 600;
              font-size: 30px;
              color: #999999;
            }

            &-text {
              margin-top: 14px;
              font-size: 14px;
              font-weight: 400;
              color: #666666;
              line-height: 22px;
            }
          }
        }
      }

      .upload-hidden {
        display: none;
      }

      .upload-footer {
        margin-top: 20px;
        font-size: 14px;
        font-weight: 400;
        color: #999999;
        line-height: 22px;
      }
    }
  }

  .drawer-footer {
    margin-top: 5px;
    text-align: center;
  }
}
</style>
