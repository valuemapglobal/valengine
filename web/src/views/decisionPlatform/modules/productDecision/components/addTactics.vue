<template>
  <div class="addTactics">
    <el-form
      ref="form"
      :model="modelForm"
      :rules="rules"
      :label-width="isEnglish() ? '140px' : '80px'"
    >
      <el-form-item :label="modelFormLabel.name" prop="name">
        <el-input
          v-model="modelForm.name"
          :placeholder="$t('decisionPlatform.inputModelName')"
          v-if="type == 'rule' || type === 'classify'"
          @change="handleRuleCode"
        />
        <el-select
          v-model="modelForm.name"
          :placeholder="$t('decisionPlatform.pleaseSelect')"
          clearable
          @change="handleRuleCode"
          v-else
        >
          <el-option
            v-for="(item, index) in professionList"
            :key="index"
            :label="item.dictLabel"
            :value="item.dictLabel"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item :label="modelFormLabel.descr" prop="descr">
        <el-input
          v-model="modelForm.descr"
          type="textarea"
          :placeholder="$t('decisionPlatform.inputDescription')"
          :rows="5"
        ></el-input>
      </el-form-item>

      <el-form-item
        :label="modelFormLabel.v"
        prop="v"
        v-if="type == 'rule' || type === 'classify'"
      >
        <el-input
          v-model="modelForm.newVersion"
          :placeholder="$t('decisionPlatform.autoGenerateVersion')"
          disabled
        />
      </el-form-item>
    </el-form>
    <div class="btnBottom" v-if="status">
      <el-button type="primary" @click="handleBeforeSubmit">{{
        $t('decisionPlatform.confirm')
      }}</el-button>
      <el-button @click="handleClose">{{
        $t('decisionPlatform.cancel')
      }}</el-button>
    </div>
    <div class="hint-box">
      <div class="text">{{ $t('decisionPlatform.tip') }}</div>
      <div>
        {{ $t('decisionPlatform.versionAutoGenerateTip') }}
      </div>
    </div>
    <!--		<confirmDialog ref="confirmDialog" :disabled="fullscreenLoading" @update="update" @reserved="reserved">
			<div>是否需要生成新的版本？</div>
		</confirmDialog>-->
  </div>
</template>

<script>
import { submitModel, checkName } from '../api/riskModel'
import { getDicts } from '../api/index'
import { mapState, mapGetters } from 'vuex'

// 暂时注释 20260105
// import { logChange } from '../api'
// import { handleCheckLock } from '../utils/index.js'

export default {
  name: 'addTactics',
  components: {
    /*confirmDialog*/
  },

  props: {
    formData: {
      type: Object || null,
      default: null,
    },
    tactics: {
      type: Object,
      default: () => {},
    },
    status: {
      type: Boolean,
      default: true,
    },
    type: {
      type: String,
      default: 'rule',
    },
    versionControl: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      fullscreenLoading: false,
      // tabs: {
      //   1: '准入',
      //   2: '反欺诈',
      //   3: '预授信',
      //   4: '授信',
      //   5: '放款（支取',
      //   6: '监测',
      //   7: '贷后',
      //   8: '催收',
      // },
      modelForm: {
        name: '',
        descr: '',
      },
      professionList: [],

      logRecord: {
        controlRecordId: null,
        ownershipSubject: null,
        currentVersion: null,
        projectCode: null,
        businessCode: null,
        ruleCode: null,
        changeType: null, //CREATED,UPDATED,DELETED
      },
      formBackUp: null,
    }
  },
  watch: {
    formData: {
      handler(val) {
        this.modelForm = {
          newVersion: val.versionObj ? val.versionObj.newVersion : '',
          name: val.name,
          descr: val.descr,
          id: val.id,
        }
        this.formBackUp = { ...this.modelForm }

        // this.handleLogBaseData()
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    ...mapState(['dataRisk']),
    ...mapGetters(['batchId']),
    mapProObj() {
      let findObj = this.dataRisk.productList.find(
        (item) => item.id === this.dataRisk.decision.projectCode
      )
      return findObj
    },
    modelFormLabel() {
      if (this.type == 'rule' || this.type === 'classify') {
        return {
          name: this.$t('decisionPlatform.modelName'),
          descr: this.$t('decisionPlatform.modelDescription'),
          personOrCompany: this.$t('decisionPlatform.customerType'),
          v: this.$t('decisionPlatform.versionNumber'),
        }
      } else {
        return {
          name: this.$t('decisionPlatform.applicationIndustry'),
          descr: this.$t('decisionPlatform.strategyDescription'),
        }
      }
    },
    rules() {
      return {
        name: [
          {
            required: true,
            message: this.$t('decisionPlatform.cannotBeEmpty'),
            trigger: 'blur',
          },
          {
            max: 30,
            required: true,
            message: this.$t('decisionPlatform.modelNameLengthExceeded'),
            trigger: 'blur',
          },
        ],
        descr: [
          {
            max: 200,
            message: this.$t('decisionPlatform.modelDescriptionLengthExceeded'),
            trigger: 'blur',
          },
        ],
      }
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    // 暂时注释 20260105
    // handleCheckLock,
    /**
     * 处理日志变更基础数据
     */
    // handleLogBaseData() {
    //   let decision = this.dataRisk.decision
    //   let productList = this.dataRisk.productList

    //   this.logRecord.projectCode = decision.projectCode || null
    //   this.logRecord.businessCode = decision.businessCode || null
    //   this.logRecord.ruleCode = decision.ruleCode || null
    //   this.logRecord.ownershipSubject =
    //     productList.find((item) => item.id === decision.projectCode)
    //       .productName || null

    //   this.logRecord.controlRecordId = this.formData?.controlRecordId || null
    //   this.logRecord.changeType =
    //     this.formData?.id && this.formData.id != null ? 'UPDATED' : 'CREATED'
    //   this.logRecord.currentVersion =
    //     this.formData?.versionObj?.newVersion || null
    // },

    handleLogData() {
      // 比较表单数据变化，生成日志数据
      const changes = this.compareObjects(this.formBackUp, this.modelForm)

      return {
        ...this.logRecord,
        changeDetails: changes,
      }
    },

    /**
     * 比较两个对象的值差异
     * @param {Object} obj1 - 原始对象
     * @param {Object} obj2 - 比较对象
     * @param {Array} fields - 需要比较的字段数组，如果不传则比较所有字段
     * @returns {Array} 返回差异数组，包含字段名、原值、新值
     */
    compareObjects(obj1, obj2, fields = null) {
      const differences = []

      // 如果没有指定字段，则比较所有字段
      const fieldsToCompare = fields || [
        ...new Set([...Object.keys(obj1 || {}), ...Object.keys(obj2 || {})]),
      ]

      fieldsToCompare.forEach((field) => {
        const value1 = obj1?.[field]
        const value2 = obj2?.[field]

        // 处理null和undefined的情况
        const normalizedValue1 =
          value1 === null || value1 === undefined ? '' : value1
        const normalizedValue2 =
          value2 === null || value2 === undefined ? '' : value2

        // 如果值不同，记录差异
        if (normalizedValue1 !== normalizedValue2) {
          differences.push({
            targetId: this.modelForm.id || null,
            targetType: 'POLICY',
            fieldName: field,
            fieldDisplayName: this.getFieldLabel(field),
            oldValue: value1 || null,
            newValue: value2 || null,
          })
        }
      })

      return differences
    },

    /**
     * 根据字段名获取对应的中文标签
     * @param {String} field - 字段名
     * @returns {String} 字段标签
     */
    getFieldLabel(field) {
      const fieldMap = {
        name: this.$t('decisionPlatform.modelName'),
        descr: this.$t('decisionPlatform.modelDescription'),
        newVersion: this.$t('decisionPlatform.versionNumber'),
      }
      return fieldMap[field] || field
    },
    handleRuleCode(val) {
      if (val) {
        let params = {
          ...this.dataRisk.decision,
          name: this.modelForm.name,
        }
        checkName({ ...params })
          .then((res) => {
            if (res.code == 200) {
              if (res.data) {
                this.rules.name.push({
                  validator: this.checkCodeNo,
                  trigger: 'blur',
                })
              } else {
                if (this.rules.name.length > 1) this.rules.name.pop()
              }
              this.$refs.form.validateField('name', (valid) => {})
            } else {
              if (this.rules.length >= 2) {
                this.rules.name.slice(0, 2)
              }
            }
          })
          .catch((err) => {})
      }
    },
    checkCodeNo(rule, value, callback) {
      if (value) {
        return callback(
          new Error(this.$t('decisionPlatform.strategyNameDuplicate'))
        )
      } else {
        callback()
      }
    },
    init() {
      getDicts('sys_enterprise_industry')
        .then((res) => {
          this.professionList = res.data
          if (!this.hasButton('productDecision:gutu:show')) {
            this.professionList.shift()
          }
        })
        .catch((err) => {})
    },
    async handleBeforeSubmit() {
      // 暂时注释 20260105
      // if (this.dataRisk.decision.ruleCode == 5) {
      //   if (!(await this.handleCheckLock())) return
      // }
      this.$refs['form'].validate(async (valid) => {
        if (valid) {
          // 暂时注释 20260105
          // let logData = this.handleLogData()
          // if (
          //   this.type == 'rule' &&
          //   logData.changeDetails.length &&
          //   this.dataRisk.decision.ruleCode == 5
          // ) {
          //   logChange({ ...logData, batchId: this.batchId })
          //     .then((res) => {
          //       if (res.code == 200) {
          //         this.submit()
          //       }
          //     })
          //     .catch((err) => {})
          // } else {
          //   this.submit()
          // }

          this.submit()
        }
      })
    },
    submit() {
      let data = {
        projectName: this.mapProObj.productName,
        personOrCompany: 'c',
        judgment: this.modelForm.id ? 1 : 0,
      }
      submitModel({
        ...this.modelForm,
        ...this.dataRisk.decision,
        ...data,
        versionControl: this.versionControl,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            // 暂时注释 20260105
            // if (this.dataRisk.decision.ruleCode == 5) {
            //   this.handleCheckLock()
            // }
            this.$emit('success', 0)
            this.resetForm()
          }
        })
        .catch((err) => {})
    },
    handleClose() {
      this.resetForm()
      this.$emit('close')
    },
    resetForm() {
      this.modelForm = {
        name: '',
        descr: '',
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
    },
    clearForm() {
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
  },
}
</script>

<style lang="less" scoped>
.addTactics {
  padding: 0px 20px;
  box-sizing: border-box;

  .btnBottom {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-top: 124px;

    > button {
      border: none;
      font-size: 16px;
      height: 42px;
      border-radius: 6px;
    }

    > button:nth-of-type(1) {
      width: 84px;
    }

    > button:nth-of-type(2) {
      background: #f0f2f5;
      color: rgba(#000, 0.85);
    }
  }

  /deep/ .el-form {
    .el-form-item__label {
      line-height: 48px;
    }

    .el-input {
      .el-input__inner {
        height: 48px;
        padding: 14px;
        background-color: #f4f6f9;
      }
    }

    .el-select {
      width: 100%;
    }

    .el-textarea {
      .el-textarea__inner {
        padding: 14px;
        background-color: #f4f6f9;
      }
    }
  }
}

.hint-box {
  color: rgba(#000, 0.65);
  font-size: 14px;
  font-family: PingFang SC, PingFang SC;
  font-weight: 500;
  margin-top: 20px;

  .text {
    color: #ff8f1f;
  }
}
</style>
