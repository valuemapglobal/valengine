<template>
  <div class="addRuleGroup">
    <el-form
      ref="form"
      :model="ruleGroupForm"
      :rules="rules"
      label-width="120px"
    >
      <el-form-item label="规则组名称" prop="name">
        <el-input
          v-model="ruleGroupForm.name"
          placeholder="请输入规则组名称"
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        />
      </el-form-item>
      <el-form-item label="规则组描述" prop="descr">
        <el-input
          type="textarea"
          v-model="ruleGroupForm.descr"
          placeholder="请输入规则组描述"
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        />
      </el-form-item>
    </el-form>
    <div class="btnBottom" v-if="status">
      <el-button type="primary" @click="handleBeforeSubmit" v-preventReClick
        >确定
      </el-button>
      <el-button @click="handleClose">取消</el-button>
    </div>
    <!--		<confirmDialog ref="confirmDialog" :disabled="fullscreenLoading" @update="update" @reserved="reserved">
          <div>是否需要生成新的版本？</div>
        </confirmDialog>-->
  </div>
</template>

<script>
import {
  editGroupSubmit,
  versionControlFraud,
  versionReserveFraud,
} from '../api/riskModel'
import { mapState, mapGetters } from 'vuex'
import { logChange, lockStatus } from '../api'
import { handleCheckLock } from '../utils/index.js'
/*import confirmDialog from '../components/confirmDialog.vue'*/

export default {
  name: 'addRuleGroup',
  components: {
    /*confirmDialog*/
  },
  props: {
    tacticsId: {
      type: Number,
      default: 0,
    },
    formData: {
      type: Object,
      default: {},
    },
    status: {
      type: Boolean,
      default: true,
    },
    versionControl: {
      type: String,
      default: '',
    },
    tactics: {
      type: Object,
      default: () => {},
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      fullscreenLoading: false,
      tabs: {
        1: '准入',
        2: '反欺诈',
        3: '预授信',
        4: '授信',
        5: '放款（支取',
        6: '监测',
        7: '贷后',
        8: '催收',
      },
      ruleGroupForm: {
        name: null,
        descr: null,
        id: null,
        modelId: null,
      },
      projectCode: '1001',
      rules: {
        name: [
          { required: true, message: '规则组名称不能为空', trigger: 'blur' },
          // {
          //   validator: (rule, value, callback) => {
          //     let check = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/
          //     if (!check.test(value)) {
          //       callback('请输入不包含特殊字符的名称')
          //     }
          //     callback()
          //   }, trigger: 'blur'
          // },
          { max: 30, message: '规则组名称文字长度不能超过30', trigger: 'blur' },
        ],
        descr: [
          {
            max: 200,
            message: '规则组描述文字长度不能超过200',
            trigger: 'blur',
          },
        ],
      },
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
  created() {},
  watch: {
    formData: {
      handler(val) {
        this.ruleGroupForm = {
          name: val.name,
          descr: val.descr,
          id: val.id,
          modelId: val.modelId,
        }
        this.formBackUp = { ...this.ruleGroupForm }
        this.handleLogBaseData()
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
  },
  methods: {
    handleCheckLock,
    handleLogBaseData() {
      let decision = this.dataRisk.decision
      let productList = this.dataRisk.productList

      this.logRecord.projectCode = decision.projectCode || null
      this.logRecord.businessCode = decision.businessCode || null
      this.logRecord.ruleCode = decision.ruleCode || null
      this.logRecord.ownershipSubject =
        productList.find((item) => item.id === decision.projectCode)
          .productName || null

      this.logRecord.controlRecordId = this.formData?.controlRecordId || null
      this.logRecord.changeType =
        this.formData?.id && this.formData.id != null ? 'UPDATED' : 'CREATED'
      this.logRecord.currentVersion =
        this.formData?.versionObj?.newVersion || null

      console.log(this.logRecord, 'formData---formData')
    },
    handleLogData() {
      const changes = this.compareObjects(this.formBackUp, this.ruleGroupForm)
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
            targetId: this.ruleGroupForm.id || null,
            targetType: 'RULE_GROUP',
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
        name: '规则组名称',
        descr: '规则组描述',
      }
      return fieldMap[field] || field
    },
    async handleBeforeSubmit() {
      if (this.dataRisk.decision.ruleCode == 5) {
        if (!(await this.handleCheckLock())) return
      }
      this.$refs['form'].validate(async (valid) => {
        if (valid) {
          let logData = this.handleLogData()
          if (
            logData.changeDetails.length &&
            this.dataRisk.decision.ruleCode == 5
          ) {
            logChange({
              ...logData,
              parentId: this.tacticsId,
              batchId: this.batchId,
            })
              .then((res) => {
                if (res.code == 200) {
                  this.submit()
                }
              })
              .catch((err) => {})
          } else {
            this.submit()
          }
        }
      })
    },
    submit() {
      if (this.ruleCode == 2) {
        this.resetForm()
        this.$emit('success')
        return
      }

      editGroupSubmit({
        ...this.ruleGroupForm,
        ...this.dataRisk.decision,
        versionControl: this.versionControl,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success('操作成功')
            this.handleCheckLock()
            this.$emit('success', 1)
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
      this.ruleGroupForm = this.$options.data().ruleGroupForm
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
.addRuleGroup {
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

    .el-textarea {
      .el-textarea__inner {
        padding: 14px;
        background-color: #f4f6f9;
      }
    }
  }
}

.noDisabledColor {
  /deep/ .el-input__inner,
  /deep/ .el-textarea__inner {
    background-color: rgba(#000, 0.08) !important;
    color: rgba(#000, 0.85);
  }
}
</style>
