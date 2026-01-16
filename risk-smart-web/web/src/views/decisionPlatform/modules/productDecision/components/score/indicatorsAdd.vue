<template>
  <div class="addTactics">
    <el-form
      ref="formRef"
      :model="cardForm"
      :rules="rules"
      :label-width="isEnglish() ? '160px' : '100px'"
    >
      <el-form-item
        :label="$t('decisionPlatform.indicatorName')"
        prop="primaryIndex"
      >
        <el-input
          v-model="cardForm.primaryIndex"
          @input="(e) => (cardForm.primaryIndex = e.replace(/\s*/g, ''))"
          clearable
          :placeholder="$t('decisionPlatform.inputPlaceholder')"
          @change="handleScoreCode"
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        />
      </el-form-item>
      <el-form-item
        :label="$t('decisionPlatform.weight')"
        prop="weight"
        class="inputTips"
      >
        <el-input
          v-model="cardForm.weight"
          :placeholder="$t('decisionPlatform.inputPlaceholder')"
          clearable
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        >
          <span slot="suffix">%</span>
        </el-input>
        <div class="tips">
          <p>{{ $t('decisionPlatform.weightSumTip') }}</p>
        </div>
      </el-form-item>
      <el-form-item
        :label="$t('decisionPlatform.description')"
        prop="description"
      >
        <el-input
          v-model="cardForm.description"
          type="textarea"
          :placeholder="$t('decisionPlatform.inputDescription')"
          clearable
          :rows="5"
          maxlength="200"
          show-word-limit
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="btnBottom" v-if="status">
      <el-button type="primary" @click="submit" v-preventReClick
        >{{ $t('decisionPlatform.confirm') }}
      </el-button>
      <el-button @click="handleClose">{{
        $t('decisionPlatform.cancel')
      }}</el-button>
    </div>
    <!--		<confirmDialog ref="confirmDialog" :disabled="fullscreenLoading" @update="update" @reserved="reserved"
          @cancel='closeDialog'>
          <div>是否需要生成新的版本？</div>
        </confirmDialog>-->
  </div>
</template>

<script>
import {
  checkIndicatorsName,
  increaseIndicators,
  updateIndicators,
  versionControlFraud,
  versionReserveScore,
} from '../../api/score'
import { getDicts } from '../../api/index'
/*import confirmDialog from '../confirmDialog.vue'*/
import { mapState } from 'vuex'

export default {
  name: 'addTactics',
  components: {
    /*confirmDialog*/
  },
  props: {
    formData: {
      type: Object,
      default: () => {},
    },
    status: {
      type: Boolean,
      default: true,
    },
    scordPrimaryIds: {
      type: Array,
      default: () => [],
    },
    versionControl: {
      type: String,
      default: '',
    },
    scoreCard: {
      type: Object,
      default: {},
    },
    //页面是否为只读，不可修改
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
      cardForm: {
        id: null,
        primaryIndex: null,
        weight: null,
        description: null,
        scoreCardId: null,
        parentCardId: null,
      },
      argument: '自动生成参数',
      professionList: [],
    }
  },
  watch: {
    formData: {
      handler(val) {
        Object.assign(this.$data, this.$options.data())
        this.$nextTick(() => {
          if (this.$refs.formRef) this.$refs.formRef.clearValidate()
        })
        for (let key in this.cardForm) {
          if (val[key]) this.cardForm[key] = val[key]
        }
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    ...mapState(['dataRisk']),
    mapProObj() {
      let findObj = this.dataRisk.productList.find(
        (item) => item.id === this.dataRisk.decision.projectCode
      )
      return findObj
    },
    rules() {
      return {
        primaryIndex: [
          {
            required: true,
            message: this.$t('decisionPlatform.indicatorNameCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
        weight: [
          {
            required: true,
            message: this.$t('decisionPlatform.weightCannotBeEmpty'),
            trigger: 'blur',
          },
          { validator: this.checkWeight, trigger: 'blur' },
        ],
      }
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    isEnglish() {
      return this.$i18n.locale === 'en'
    },
    checkWeight(rule, value, callback) {
      let seq = /^(\d{1,2}(\.\d{1,2})?|100(\.0{1,2})?)$/
      const weightTotalPercentage = Number(
          localStorage.getItem('weightTotalPercentage')
        ),
        weightLastPercentage = localStorage.getItem('weightLast')
          ? Number(JSON.parse(localStorage.getItem('weightLast')).weight)
          : 0
      if (!seq.test(value)) {
        callback(new Error(this.$t('decisionPlatform.inputNumber0To100')))
        return
      }
      if (
        Number(value) >
        Number(100 - weightTotalPercentage + weightLastPercentage)
      ) {
        callback(
          new Error(this.$t('decisionPlatform.totalWeightCannotExceed100'))
        )
        return
      }
      callback()
    },
    mapType(customerType) {
      // let type = null
      // switch (customerType) {
      // 	case 0:
      // 		type = 'C、P';
      // 		break
      // 	case 1:
      // 		type = 'P';
      // 		break
      // 	case 2:
      // 		type = 'C';
      // 		break
      // }
      return 'C'
    },
    /*// 关闭
			 closeDialog() {
			 this.handleClose()
			 },
			 // 获取更新||保留版本参数
			 getqueryData() {
			 let obj = this.scoreCard.dataList.find(item => item.id === this.scoreCard.activeId)
			 return {
			 modelId: this.scoreCard.activeId,
			 ...this.dataRisk.decision,
			 modelName: obj.name,
			 projectName: this.mapProObj.productName,
			 businessName: this.dataRisk.decision.business.name,
			 personOrCompany: this.mapType(this.mapProObj.customerType),
			 championVersion: obj.versionObj.championVersion

			 }
			 },
			 // 更新版本
			 update() {
			 this.fullscreenLoading = true
			 let promiseChain = Promise.resolve();
			 promiseChain.then(() => {
			 return this.submitComfirm()
			 }).then(() => {
			 versionControlFraud({ ...this.getqueryData(), versionControl: this.versionControl }).then((res) => {
			 if (res.code == 200) {
			 const weightTotalPercentage = Number(localStorage.getItem('weightTotalPercentage')),
			 weightLast = localStorage.getItem('weightLast') ? JSON.parse(localStorage.getItem('weightLast')) : undefined;
			 if (weightLast && this.cardForm.weight !== Number(100 - weightTotalPercentage)) {
			 const lastWeightValue1 = Number(100 - (weightTotalPercentage + Number(this.cardForm.weight)));
			 const lastWeightValue2 = Number(100 - (weightTotalPercentage - weightLast.weight + Number(this.cardForm.weight)));
			 console.log('weightTotalPercentage:', weightTotalPercentage, 'weightLast.weight:', weightLast.weight)
			 let weight = weightTotalPercentage == weightLast.weight ? Number(100 - Number(this.cardForm.weight)) : Number(this.cardForm.weight) > Number(100 - weightTotalPercentage) ? lastWeightValue2 : lastWeightValue1;
			 updateIndicators({
			 businessCode: weightLast.businessCode,
			 description: weightLast.description,
			 weight,
			 id: weightLast.id,
			 primaryIndex: weightLast.primaryIndex,
			 projectCode: weightLast.projectCode,
			 ruleCode: weightLast.ruleCode,
			 scoreCardId: weightLast.scoreCardId,
			 versionControl: weightLast.versionControl
			 }).then(() => {
			 versionReserveScore({
			 ...this.getqueryData(), versionControl: weightLast.versionControl
			 }).then((res2) => {
			 if (res2.code === 200) {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 this.$message.success('操作成功');
			 this.$emit('success')
			 this.handleClose()
			 }
			 })
			 })
			 } else {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 this.$message.success('操作成功');
			 this.$emit('success')
			 this.handleClose()
			 }
			 }
			 }).catch(() => {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 })
			 })
			 },
			 // 保留原版本
			 async reserved() {
			 this.fullscreenLoading = true
			 let promiseChain = Promise.resolve();
			 await this.submitComfirm()
			 promiseChain.then(() => {
			 versionReserveScore({ ...this.getqueryData(), versionControl: this.versionControl }).then((res) => {
			 if (res.code == 200) {
			 const weightTotalPercentage = Number(localStorage.getItem('weightTotalPercentage')),
			 weightLast = localStorage.getItem('weightLast') ? JSON.parse(localStorage.getItem('weightLast')) : undefined;
			 if (weightLast && Number(this.cardForm.weight) !== Number(100 - weightTotalPercentage)) {
			 const lastWeightValue1 = Number(100 - (weightTotalPercentage + Number(this.cardForm.weight)));
			 const lastWeightValue2 = Number(100 - (weightTotalPercentage - weightLast.weight + Number(this.cardForm.weight)));
			 let weight = weightTotalPercentage == weightLast.weight ? Number(100 - Number(this.cardForm.weight)) : this.cardForm.weight > Number(100 - weightTotalPercentage) ? lastWeightValue2 : lastWeightValue1;
			 updateIndicators({
			 businessCode: weightLast.businessCode,
			 description: weightLast.description,
			 weight,
			 id: weightLast.id,
			 primaryIndex: weightLast.primaryIndex,
			 projectCode: weightLast.projectCode,
			 ruleCode: weightLast.ruleCode,
			 scoreCardId: weightLast.scoreCardId,
			 versionControl: weightLast.versionControl
			 }).then(() => {
			 versionReserveScore({
			 ...this.getqueryData(), versionControl: weightLast.versionControl
			 }).then((res2) => {
			 if (res2.code === 200) {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 this.$message.success('操作成功');
			 this.$emit('success')
			 this.handleClose()
			 }
			 })
			 })
			 } else {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 this.$message.success('操作成功');
			 this.$emit('success')
			 this.handleClose()
			 }
			 }
			 }).catch(() => {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 })
			 })
			 },*/
    init() {
      getDicts('sys_enterprise_industry')
        .then((res) => {
          this.professionList = res.data
        })
        .catch((err) => {})
    },
    /*async submitComfirm(updateParams) {
				let params = updateParams || {
					...this.dataRisk.decision, ...this.cardForm,
					parentCardIds: this.scordPrimaryIds.join(';'),
					versionControl: this.versionControl
				}
				let API = params.id ? updateIndicators : increaseIndicators
				return API({ ...params })
					.then((res) => {

					})
					.catch((err) => { })
			},*/
    submit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          let params = {
            ...this.dataRisk.decision,
            ...this.cardForm,
            parentCardIds: this.scordPrimaryIds.join(';'),
            versionControl: this.versionControl,
          }
          let API = params.id ? updateIndicators : increaseIndicators
          API(params)
            .then(() => {
              const weightTotalPercentage = Number(
                  localStorage.getItem('weightTotalPercentage')
                ),
                weightLast = localStorage.getItem('weightLast')
                  ? JSON.parse(localStorage.getItem('weightLast'))
                  : undefined
              if (
                weightLast &&
                Number(this.cardForm.weight) !==
                  Number(100 - weightTotalPercentage)
              ) {
                const lastWeightValue1 = Number(
                  100 - (weightTotalPercentage + Number(this.cardForm.weight))
                )
                const lastWeightValue2 = Number(
                  100 -
                    (weightTotalPercentage -
                      weightLast.weight +
                      Number(this.cardForm.weight))
                )
                let weight =
                  weightTotalPercentage == weightLast.weight
                    ? Number(100 - Number(this.cardForm.weight))
                    : this.cardForm.weight > Number(100 - weightTotalPercentage)
                    ? lastWeightValue2
                    : lastWeightValue1
                updateIndicators({
                  businessCode: weightLast.businessCode,
                  description: weightLast.description,
                  weight,
                  id: weightLast.id,
                  primaryIndex: weightLast.primaryIndex,
                  projectCode: weightLast.projectCode,
                  ruleCode: weightLast.ruleCode,
                  scoreCardId: weightLast.scoreCardId,
                  versionControl: weightLast.versionControl,
                }).then((res) => {
                  if (res.code == 200) {
                    this.$message.success(
                      this.$t('decisionPlatform.operationSuccess')
                    )
                    this.$emit('success')
                    this.handleClose()
                  }
                })
              } else {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.$emit('success')
                this.handleClose()
              }
            })
            .catch((err) => {})
        }
      })
    },
    handleScoreCode(val) {
      if (val) {
        let params = {
          ...this.dataRisk.decision,
          primaryIndex: val,
          scoreCardId: this.cardForm.scoreCardId,
        }
        checkIndicatorsName({ ...params })
          .then((res) => {
            if (res.code == 200) {
              if (res.data) {
                this.rules.primaryIndex.push({
                  validator: this.checkCodeNo,
                  trigger: 'blur',
                })
              } else {
                if (this.rules.primaryIndex.length > 1)
                  this.rules.primaryIndex.pop()
              }
              this.$refs.codeForm.validateField('primaryIndex', (valid) => {
                console.log(valid)
              })
            } else {
              if (this.rules.length >= 2) {
                this.rules.primaryIndex.slice(0, 2)
              }
            }
          })
          .catch((err) => {})
      }
    },
    checkCodeNo(rule, value, callback) {
      if (value) {
        return callback(
          new Error(this.$t('decisionPlatform.indicatorNameDuplicate'))
        )
      } else {
        callback()
      }
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
    margin-top: 20px;

    > button {
      border: none;
      font-size: 16px;
      height: 42px;
      border-radius: 6px;
      background-color: var(--primary-color);
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

    .el-select {
      width: 100%;
    }

    .el-input {
      .el-input__inner {
        height: 48px;
        padding: 14px;
        background-color: #f4f6f9;
      }

      .el-input__suffix {
        line-height: 48px;
        margin-right: 5px;
      }
    }

    .el-textarea {
      .el-textarea__inner {
        padding: 14px;
        background-color: #f4f6f9;
      }

      .el-input__count {
        background-color: #f4f6f9;
      }
    }
  }
}

.inputTips {
  /deep/ .el-form-item__content {
    display: flex;
    align-items: center;

    .tips {
      text-align: center;
      margin-left: 10px;

      > p {
        font-size: 12px;
        margin-bottom: 0px;
        line-height: 18px;
        // font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        color: #ff8f1f;
        white-space: nowrap;
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
