<template>
  <div class="bairongDrawer">
    <el-form
      :model="formData"
      :label-width="isEnglish() ? '200px' : '140px'"
      ref="formDataRef"
      :rules="rules"
    >
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.appKey')"
            prop="appKey"
          >
            <el-input
              v-model="formData.appKey"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.token')"
            prop="apiCode"
          >
            <el-input
              v-model="formData.apiCode"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.phoneNumberPublicOpinion')"
            prop="phoneNumberPublicOpinion"
          >
            <el-input
              v-model="formData.phoneNumberPublicOpinion"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.naturalPersonIdentification')"
            prop="naturalPersonIdentification"
          >
            <el-input
              v-model="formData.naturalPersonIdentification"
              :placeholder="$t('common.pleaseInput')"
            ></el-input> </el-form-item
        ></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.telOnlineTime')"
            prop="telOnlineTime"
          >
            <el-input
              v-model="formData.telOnlineTime"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.telOnlineStatus')"
            prop="telOnlineStatus"
          >
            <el-input
              v-model="formData.telOnlineStatus"
              :placeholder="$t('common.pleaseInput')"
            ></el-input> </el-form-item
        ></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.executionLimit')"
            prop="executionLimit"
          >
            <el-input
              v-model="formData.executionLimit"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.executionPro')"
            prop="executionPro"
          >
            <el-input
              v-model="formData.executionPro"
              :placeholder="$t('common.pleaseInput')"
            ></el-input> </el-form-item
        ></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.specialList')"
            prop="specialList"
          >
            <el-input
              v-model="formData.specialList"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.loanIntention')"
            prop="loanIntention"
          >
            <el-input
              v-model="formData.loanIntention"
              :placeholder="$t('common.pleaseInput')"
            ></el-input> </el-form-item
        ></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.debtServiceStressIndex')"
            prop="debtServiceStressIndex"
          >
            <el-input
              v-model="formData.debtServiceStressIndex"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.gangFraud')"
            prop="gangFraud"
          >
            <el-input
              v-model="formData.gangFraud"
              :placeholder="$t('common.pleaseInput')"
            ></el-input> </el-form-item
        ></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.bairongDataUpdateTime')"
            prop="period"
          >
            <el-input
              v-model.number="formData.period"
              :placeholder="$t('common.pleaseInput')"
            >
              <template slot="append">{{
                $t('departmentManagement.days')
              }}</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="bottomBtns">
      <el-button class="btn" @click="submit">{{ $t('common.sure') }}</el-button>
      <el-button @click="handleCloase">{{ $t('common.cancel') }}</el-button>
    </div>
  </div>
</template>

<script>
import { getBairongInfo, submitBairongInfo } from '@/api/system/department.js'
export default {
  props: {
    dataInfo: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      formData: {
        id: null,
        appKey: null,
        apiCode: null,
        phoneNumberPublicOpinion: null,
        naturalPersonIdentification: null,
        telOnlineTime: null,
        telOnlineStatus: null,
        executionLimit: null,
        executionPro: null,
        specialList: null,
        loanIntention: null,
        debtServiceStressIndex: null,
        gangFraud: null,
        period: null,
      },
      deptId: null,
    }
  },
  computed: {
    rules() {
      return {
        appKey: [
          {
            required: true,
            message: this.$t('departmentManagement.inputAppKey'),
            trigger: 'blur',
          },
        ],
        apiCode: [
          {
            required: true,
            message: this.$t('departmentManagement.inputToken'),
            trigger: 'blur',
          },
        ],
        phoneNumberPublicOpinion: [
          {
            required: true,
            message: this.$t(
              'departmentManagement.inputPhoneNumberPublicOpinion'
            ),
            trigger: 'blur',
          },
        ],
        naturalPersonIdentification: [
          {
            required: true,
            message: this.$t(
              'departmentManagement.inputNaturalPersonIdentification'
            ),
            trigger: 'blur',
          },
        ],
        telOnlineTime: [
          {
            required: true,
            message: this.$t('departmentManagement.inputTelOnlineTime'),
            trigger: 'blur',
          },
        ],
        telOnlineStatus: [
          {
            required: true,
            message: this.$t('departmentManagement.inputTelOnlineStatus'),
            trigger: 'blur',
          },
        ],
        executionLimit: [
          {
            required: true,
            message: this.$t('departmentManagement.inputExecutionLimit'),
            trigger: 'blur',
          },
        ],
        executionPro: [
          {
            required: true,
            message: this.$t('departmentManagement.inputExecutionPro'),
            trigger: 'blur',
          },
        ],
        specialList: [
          {
            required: true,
            message: this.$t('departmentManagement.inputSpecialList'),
            trigger: 'blur',
          },
        ],
        loanIntention: [
          {
            required: true,
            message: this.$t('departmentManagement.inputLoanIntention'),
            trigger: 'blur',
          },
        ],
        debtServiceStressIndex: [
          {
            required: true,
            message: this.$t(
              'departmentManagement.inputDebtServiceStressIndex'
            ),
            trigger: 'blur',
          },
        ],
        gangFraud: [
          {
            required: true,
            message: this.$t('departmentManagement.inputGangFraud'),
            trigger: 'blur',
          },
        ],
        period: [
          {
            required: true,
            message: this.$t('departmentManagement.inputBairongDataUpdateTime'),
            trigger: 'blur',
          },
          {
            validator: (rule, value, callback) => {
              let checkNumber = /^\d+$/
              if (!checkNumber.test(value))
                callback(this.$t('departmentManagement.inputPositiveInteger'))
              callback()
            },
            trigger: 'blur',
          },
        ],
      }
    },
  },
  watch: {
    dataInfo: {
      handler(val) {
        if (val.hasOwnProperty('deptId')) {
          this.deptId = val.deptId
          this.getDataInfo()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getDataInfo() {
      getBairongInfo({ deptId: this.deptId })
        .then((res) => {
          if (res.code == 200 && res.hasOwnProperty('data')) {
            let data = res.data
            for (let key in this.formData) {
              if (data[key] != null) this.formData[key] = data[key]
            }
          }
        })
        .catch((err) => {})
    },
    submit() {
      this.$refs.formDataRef.validate((valid) => {
        if (valid) {
          submitBairongInfo({ deptId: this.deptId, ...this.formData })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(this.$t('common.success'))
                this.handleCloase()
              }
            })
            .catch((err) => {})
        }
      })
    },
    handleCloase() {
      this.formData = this.$options.data().formData
      this.deptId = null
      this.$emit('close')
    },
  },
}
</script>

<style lang="less" scoped>
.bairongDrawer {
  padding: 0px 20px 20px;

  .bottomBtns {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    .btn {
      color: #fff;
      border: none;
      background: #409eff;
    }
  }
}
</style>
