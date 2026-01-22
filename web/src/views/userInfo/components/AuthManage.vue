<template>
  <div class="auth-manage">
    <InfoTitle title="企业认证管理">
      <template slot="right">
        <el-button type="primary" @click="toAuth()"> 新增认证 </el-button>
      </template>
    </InfoTitle>
    <div class="auth-manage-content">
      <div class="content-left">
        <div
          class="content-left-item"
          :class="{ active: companyActive === index }"
          v-for="(item, index) in companyList"
          :key="item.enterpriseCode"
          @click="companyActive = index"
        >
          <div class="company-info">
            <div class="company-name">{{ item.enterpriseName }}</div>
            <el-tag
              :type="authStatusData[item.authStatus].type"
              v-if="authStatusData[item.authStatus]"
            >
              {{ authStatusData[item.authStatus].label }}
            </el-tag>
            <el-tag type="success" v-if="false">已过期</el-tag>
          </div>
        </div>
      </div>
      <div class="content-right">
        <div class="content-right-fail" v-if="formData.authStatus === 4">
          <AuthFail />
        </div>
        <template v-else>
          <div class="valid-tips">
            认证有效期为认证开始后一年，认证后不允许修改或删除认证信息
          </div>
          <el-form
            ref="form"
            :model="formData"
            :rules="rules"
            label-width="143px"
          >
            <el-form-item label="企业名称：" prop="enterpriseName">
              <el-input
                disabled
                :value="formData.enterpriseName"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="社会统一信用代码：" prop="creditCode">
              <el-input
                disabled
                :value="formData.creditCode"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="法人姓名：" prop="legalPerson">
              <el-input
                disabled
                :value="formData.legalPerson"
                clearable
                placeholder="请填写"
              />
            </el-form-item>
            <el-form-item label="企业行业：" prop="industryType">
              <el-select
                disabled
                :popper-append-to-body="false"
                :value="formData.industryType"
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
                disabled
                :popper-append-to-body="false"
                :value="formData.enterpriseSize"
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
              <el-image :src="formData.businessUrl" fit="scale-down">
                <div slot="error" class="image-slot">暂无照片</div>
              </el-image>
            </el-form-item>
          </el-form>
          <div class="drawer-footer" v-if="formData.authStatus !== 2">
            <el-button
              v-prevent-re-click
              type="primary"
              v-if="formData.authStatus === 0"
              @click="toAuth(formData)"
            >
              去认证
            </el-button>
            <el-button
              v-prevent-re-click
              type="primary"
              v-else-if="formData.authStatus === 6"
              @click="paymentAuthHandle"
            >
              打款认证
            </el-button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { InfoTitle, AuthFail } from '@/views/userInfo/components/components'
import { CompanyIndustryTypes, CompanyScales } from '@/views/userInfo/const'
import { list, getAuthStatusTag, bankList } from '@/api/base'

export default {
  name: '',
  components: {
    InfoTitle,
    AuthFail,
  },
  data() {
    return {
      bankCodeOptions: [], //开户行选项
      //认证公司列表
      companyList: [],
      companyActive: 0,
      authStatusData: {
        0: { type: 'info', label: '未认证' },
        1: { type: 'warning', label: '认证中' },
        6: { type: 'warning', label: '认证中' }, //可以打款
        2: { type: '', label: '已认证' },
        4: { type: 'danger', label: '认证失败' },
      },
      CompanyIndustryTypes,
      CompanyScales,
      formData: {},
      rules: {},
    }
  },
  mounted() {
    // 获取开户行--列表数据
    bankList().then((res) => {
      if (res.code === 200) {
        this.bankCodeOptions = res.data
      }
    })
    this.getCompanyList()
  },
  watch: {
    companyActive: {
      handler(cur) {
        if (this.companyList.length) {
          this.getCompanyDetail(this.companyList[cur].enterpriseId)
        }
      },
    },
    deep: true,
  },
  methods: {
    getCompanyList() {
      list({}).then((res) => {
        if (res.code === 200) {
          this.companyList = res.data || []
          if (this.companyList.length) {
            this.getCompanyDetail(
              this.companyList[this.companyActive].enterpriseId
            )
          }
        }
      })
    },
    getCompanyDetail(enterpriseId) {
      getAuthStatusTag({ enterpriseId }).then((res) => {
        if (res.code === 200) {
          this.formData = res.data || {}
        }
      })
    },
    /**
     * 去认证&新增认证
     * @param row
     */
    toAuth(row = null) {
      let newRow
      if (row) {
        newRow = {
          enterpriseId: row.enterpriseId,
          legalPerson: row.legalPerson,
          enterpriseName: row.enterpriseName,
          creditCode: row.creditCode,
          bankCode: row.bankCode,
          bankProvinceId: row.bankProvinceId,
          bankCityId: row.bankCityId,
          bankAccount: row.bankAccount,
          industryType: row.industryType,
          enterpriseSize: row.enterpriseSize,
          bankAccountName: row.bankAccountName,
          bankBranch: row.bankBranch,
          businessUrl: row.businessUrl,
        }
      } else {
        newRow = row
      }

      this.$store.commit('setCompanyinfo', newRow)
      this.$emit('to')
    },
    /**
     * 打款认证按钮点击
     */
    paymentAuthHandle() {
      localStorage.setItem(
        'data',
        JSON.stringify({
          orderId: this.formData.orderId,
          enterpriseId: this.formData.enterpriseId,
        })
      )
      localStorage.setItem(
        'bankInfo',
        JSON.stringify({
          ...this.formData,
          bankName: this.bankCodeOptions.filter((res) => {
            return res.bankCode === this.formData.bankCode
          })[0].bankName, //到账银行
        })
      )
      this.$emit('paymentHandle')
    },
  },
}
</script>

<style lang="less" scoped>
@import '../../assets/scss/productConfiguration';

.auth-manage {
  height: 100%;

  &-content {
    margin: 40px 30px 30px;
    height: calc(100% - 150px);
    border-radius: 12px;
    border: 1px solid rgba(0, 0, 0, 0.08);
    display: flex;

    .content-left,
    .content-right {
      height: 100%;
      overflow-x: hidden;
      overflow-y: auto;
    }

    .content-left {
      padding: 20px;

      .content-left-item + .content-left-item {
        margin-top: 14px;
      }

      &-item {
        cursor: pointer;
        width: 302px;
        border-radius: 6px;
        padding: 10px 14px 12px;
        background: rgba(0, 0, 0, 0.04);
        border: 1px solid transparent;

        &:hover {
          background: #f7faff;
        }

        &.active {
          background: #f7faff;
          border: 1px solid #3d7fff;
          box-shadow: 0 3px 8px 0 rgba(61, 127, 255, 0.12);
        }

        .company-info {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .company-name {
            flex: 1;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }

          .el-tag {
            padding: 2px 7px;
            font-size: 12px;
            font-weight: 400;
            color: #3d7fff;
            line-height: 18px;
            border-radius: 2px;
            border: none;
            background: rgba(61, 127, 255, 0.1);

            &--success {
              color: #eb2f96;
              background: rgba(235, 47, 150, 0.1);
            }

            &--info {
              color: rgba(0, 0, 0, 0.6);
              background: rgba(0, 0, 0, 0.08);
            }

            &--warning {
              color: #ff8f1f;
              background: rgba(255, 143, 31, 0.1);
            }

            &--danger {
              color: #fa5151;
              background: rgba(250, 81, 81, 0.1);
            }
          }
        }

        .company-valid {
          margin-top: 7px;
          font-size: 12px;
          font-weight: 400;
          line-height: 17px;
          color: rgba(0, 0, 0, 0.3);
        }
      }
    }

    .content-right {
      width: calc(100% - 302px);
      padding: 20px;
      border-left: 1px solid rgba(0, 0, 0, 0.08);

      &-fail {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;

        .auth-fail {
          width: 432px;
        }
      }

      .valid-tips {
        font-size: 14px;
        font-weight: 400;
        color: #ff8f1f;
        line-height: 20px;
      }

      /deep/ .el-form {
        padding-right: 176px;
        margin-top: 20px;

        &-item {
          &.is-required:not(.is-no-asterisk) > .el-form-item__label:before {
            margin-right: 0;
          }

          &__label {
            padding-right: 10px;
            justify-content: flex-end;
          }

          .el-input.is-disabled .el-input__inner {
            color: rgba(0, 0, 0, 0.6);
          }

          .el-input__suffix {
            display: none;
          }

          .el-image,
          .image-slot {
            width: 330px;
            height: 166px;
          }

          .el-image {
            border-radius: 6px;
            border: 1px dashed rgba(151, 151, 151, 0.4982);
          }

          .image-slot {
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f4f6f9;
            color: rgba(0, 0, 0, 0.6);
          }
        }
      }

      .drawer-footer {
        margin-left: 137px;
        text-align: left;

        .el-button {
          padding: 10px 20px;
        }
      }
    }
  }
}
</style>
