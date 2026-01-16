<template>
  <div class="addTactics">
    <el-form
      ref="formRef"
      :model="cardForm"
      :rules="rules"
      :label-width="isEnglish() ? '160px' : '100px'"
    >
      <el-form-item
        :label="$t('decisionPlatform.scoreCardName')"
        prop="scoreCard"
      >
        <el-input
          v-model="cardForm.scoreCard"
          @input="(e) => (cardForm.scoreCard = e.replace(/\s*/g, ''))"
          clearable
          :placeholder="$t('decisionPlatform.inputPlaceholder')"
          @change="handleScoreCode"
        />
      </el-form-item>
      <el-form-item :label="$t('decisionPlatform.industry')" prop="profession">
        <el-select
          v-model="cardForm.profession"
          :popper-append-to-body="false"
          :placeholder="$t('decisionPlatform.pleaseSelect')"
        >
          <el-option
            v-for="(item, index) in professionList"
            :key="index"
            :label="item.dictLabel"
            :value="item.dictLabel"
          ></el-option>
        </el-select>
      </el-form-item>
      <!--			<el-form-item label="参数">
              <el-input v-model="argument" disabled class="noDisabledColor" />
            </el-form-item>-->
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
        ></el-input>
      </el-form-item>
      <el-form-item
        :label="$t('decisionPlatform.highestScore')"
        prop="modelScore"
      >
        <el-input
          v-model="cardForm.modelScore"
          :placeholder="$t('decisionPlatform.inputHighestScore')"
        />
      </el-form-item>
      <el-form-item :label="$t('decisionPlatform.versionNumber')">
        <el-input
          v-model="cardForm.versionControl"
          disabled
          :placeholder="$t('decisionPlatform.autoGenerateVersionNumber')"
        />
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
    <div class="hint-box">
      <div class="text">{{ $t('decisionPlatform.tip') }}</div>
      <div>
        {{ $t('decisionPlatform.versionNumberAutoGenerateTip') }}
      </div>
    </div>
    <!--		<confirmDialog ref="confirmDialog" @update="update" :disabled="fullscreenLoading" @reserved="reserved"
			@cancel='closeDialog'>
			<div>是否需要生成新的版本？</div>
		</confirmDialog>-->
  </div>
</template>

<script>
import {
  checkScoreName,
  increaseScore,
  updateScore,
  versionControlFraud,
  versionReserveScore,
} from '../../api/score'
import { getDicts } from '../../api/index'
import { mapState } from 'vuex'
/*import confirmDialog from '../confirmDialog.vue'*/

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
    scoreCard: {
      type: Object,
      default: {},
    },
    versionControl: {
      type: String,
      default: '',
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
        scoreCard: null,
        description: null,
        profession: null,
        versionControl: null,
        id: null,
        modelScore: null,
      },
      argument: '自动生成参数',
      professionList: [],
    }
  },
  watch: {
    formData: {
      handler(val) {
        this.init()
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
        scoreCard: [
          {
            required: true,
            message: this.$t('decisionPlatform.scoreCardNameCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
        profession: [
          {
            required: true,
            message: this.$t('decisionPlatform.selectIndustry'),
            trigger: 'change',
          },
        ],
        modelScore: [
          {
            required: true,
            message: this.$t('decisionPlatform.highestScoreCannotBeEmpty'),
          },
          {
            pattern: /^(0\.\d{1,2}|[1-9]\d*\.\d{1,2}|0|[1-9]\d*)$/,
            message: this.$t('decisionPlatform.highestScoreMustBeNumber'),
          },
        ],
      }
    },
  },
  mounted() {},
  methods: {
    isEnglish() {
      return this.$i18n.locale === 'en'
    },
    /*// 取消
      closeDialog() {
        // this.$emit('success')
        this.handleClose()
      },
      // 获取更新||保留版本参数
      getqueryData() {
        return {
          modelId: this.scoreCard.activeId,
          ...this.dataRisk.decision,
          modelName: this.scoreCard.dataList.find(item => item.id === this.scoreCard.activeId).name,
          projectName: this.mapProObj.productName,
          businessName: this.dataRisk.decision.business.name,
          personOrCompany: this.mapType(this.mapProObj.customerType),
          championVersion: this.scoreCard.dataList.find(item => item.id === this.scoreCard.activeId).versionObj.championVersion,
          versionControl: this.versionControl
        }
      },
      // 更新版本
      update() {
        this.fullscreenLoading = true
        let promiseChain = Promise.resolve();
        promiseChain.then(() => {
          return this.submitComfirm()
        }).then(() => {
          versionControlFraud(this.getqueryData()).then((res) => {
            if (res.code == 200) {
              this.$refs.confirmDialog.visible = false
              this.fullscreenLoading = false
              this.$message.success('操作成功')
              this.$emit('success')
              this.handleClose()
            }
          }).catch(() => {
            this.fullscreenLoading = false
            this.$refs.confirmDialog.visible = false
          })
        })
      },
      // 保留原版本
      reserved() {
        this.fullscreenLoading = true
        let promiseChain = Promise.resolve();
        promiseChain.then(() => {
          return this.submitComfirm()
        }).then(() => {
          versionReserveScore({ ...this.getqueryData(), versionControl: this.versionControl }).then((res) => {
            if (res.code == 200) {
              this.$refs.confirmDialog.visible = false
              this.fullscreenLoading = false
              this.$message.success('操作成功');
              this.$emit('success')
              this.handleClose()
            }
          }).catch(() => {
            this.$refs.confirmDialog.visible = false
            this.fullscreenLoading = false
          })
        })
      },*/
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
    init() {
      getDicts('sys_enterprise_industry')
        .then((res) => {
          this.professionList = res.data
        })
        .catch((err) => {})
    },
    /*async submitComfirm() {
        let data = {
          projectName: this.mapProObj.productName,
          businessName: this.dataRisk.decision.business.name,
          personOrCompany: this.mapType(this.mapProObj.customerType)
        }

        let params = {
          ...this.dataRisk.decision, ...this.cardForm, ...data,
          modelName: this.cardForm.scoreCard,
          versionControl: this.versionControl
        }
        let API = params.id ? updateScore : increaseScore
        if (params.id) {
          return API({ ...params }).then((res) => {

          }).catch((err) => {

          })

        } else {
          return API({ ...params })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(this.$t('decisionPlatform.operationSuccess'))
                this.$emit('success')
              }
            })
            .catch((err) => { })
        }

      },*/
    submit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          let data = {
            projectName: this.mapProObj.productName,
            personOrCompany: this.mapType(this.mapProObj.customerType),
          }
          let params = {
            ...this.dataRisk.decision,
            ...this.cardForm,
            ...data,
            modelName: this.cardForm.scoreCard,
          }
          let API = params.id ? updateScore : increaseScore
          /*if (params.id) {
              this.$refs.confirmDialog.visible = true
            } else {*/
          API(params)
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.$emit('success')
                this.handleClose()
              }
            })
            .catch((err) => {})
          /*}*/
        }
      })
    },
    handleScoreCode(val) {
      if (val) {
        let params = {
          ...this.dataRisk.decision,
          scoreCard: val,
        }
        checkScoreName({ ...params })
          .then((res) => {
            if (res.code == 200) {
              if (res.data) {
                this.rules.scoreCard.push({
                  validator: this.checkCodeNo,
                  trigger: 'blur',
                })
              } else {
                if (this.rules.scoreCard.length > 1) this.rules.scoreCard.pop()
              }
              this.$refs.formRef.validateField('scoreCard', (valid) => {})
            } else {
              if (this.rules.length >= 2) {
                this.rules.scoreCard.slice(0, 2)
              }
            }
          })
          .catch((err) => {})
      }
    },
    checkCodeNo(rule, value, callback) {
      if (value) {
        return callback(
          new Error(this.$t('decisionPlatform.scoreCardNameDuplicate'))
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

.noDisabledColor {
  /deep/ .el-input__inner {
    background-color: rgba(#000, 0.08) !important;
    color: rgba(#000, 0.85);
  }
}
</style>
