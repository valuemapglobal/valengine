<template>
  <div class="addTactics">
    <el-form
      ref="formRef"
      :model="cardForm"
      :rules="rules"
      :label-width="isEnglish() ? '160px' : '100px'"
    >
      <el-form-item label="code" prop="indexRule">
        <el-input
          v-model="cardForm.indexRule"
          @input="(e) => (cardForm.indexRule = e.replace(/\s*/g, ''))"
          clearable
          :placeholder="$t('decisionPlatform.inputPlaceholder')"
          @change="handleScoreCode"
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        />
      </el-form-item>
      <el-form-item
        :label="$t('decisionPlatform.score')"
        prop="score"
        class="itemTips"
      >
        <el-input
          v-model="cardForm.score"
          :placeholder="$t('decisionPlatform.inputPlaceholder')"
          clearable
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        />
        <div class="tips">
          <p>{{ $t('decisionPlatform.scoreRange') }}</p>
          <p>-200 - 200</p>
        </div>
      </el-form-item>
      <el-form-item
        :label="$t('decisionPlatform.complexConditionConfig')"
        class="logicGroupFormItem"
      >
        <div
          class="logicGroup"
          v-for="(item, index) in logicGroupArray"
          :key="index"
        >
          <div class="logicGroup-header">
            <div class="logicGroup-header-title">
              {{ $t('decisionPlatform.logicGroup') }}{{ index + 1 }}
            </div>
            <el-button
              v-if="!readOnly"
              type="primary"
              icon="el-icon-plus"
              size="mini"
              @click="addTemplate()"
              >{{ $t('decisionPlatform.addLogicGroup') }}</el-button
            >
            <el-button
              v-if="index > 0 && !readOnly"
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="delNodeTemplate(index)"
              >{{ $t('decisionPlatform.delete') }}</el-button
            >
          </div>
          <div class="logicGroup-content">
            <div
              class="logicGroup-content-item"
              v-for="(conditionItem, conditionIndex) in item.conditionArray"
              :key="conditionIndex"
            >
              <div class="item-title">
                <span
                  >{{ $t('decisionPlatform.condition')
                  }}{{ conditionIndex + 1 }}</span
                >
              </div>
              <div class="item-condition">
                <el-row v-for="(obj, ix) in conditionItem.condition" :key="ix">
                  <el-col :span="1.5" class="title">
                    {{
                      ix === 0
                        ? $t('decisionPlatform.if')
                        : $t('decisionPlatform.or')
                    }}
                  </el-col>
                  <el-col :span="18">
                    <el-form
                      :model="obj"
                      :rules="obj.rules"
                      class="select"
                      :ref="'condition' + ix"
                      :validate-on-rule-change="false"
                      :key="ix"
                    >
                      <el-form-item prop="selectObj">
                        <el-cascader
                          v-model="obj.selectObj"
                          :placeholder="
                            $t('decisionPlatform.selectDecisionCondition')
                          "
                          :options="options"
                          :props="{ checkStrictly: true }"
                          filterable
                          clearable
                          @change="
                            (event) => {
                              handleChange(event, obj, ix)
                            }
                          "
                          :disabled="readOnly"
                          :class="{ noDisabledColor: readOnly }"
                        />
                      </el-form-item>
                      <el-form-item prop="operator">
                        <el-select
                          v-model="obj.operator"
                          :placeholder="$t('decisionPlatform.logicalOperator')"
                          clearable
                          :disabled="readOnly"
                          :class="{ noDisabledColor: readOnly }"
                        >
                          <el-option
                            v-for="item in conditionList"
                            :label="item.label"
                            :value="item.value"
                            :key="item.id"
                          />
                        </el-select>
                      </el-form-item>
                      <el-form-item prop="result">
                        <el-input
                          v-model.trim="obj.result"
                          :placeholder="$t('decisionPlatform.inputValue')"
                          clearable
                          :disabled="readOnly"
                          :class="{ noDisabledColor: readOnly }"
                        />
                      </el-form-item>
                    </el-form>
                  </el-col>
                  <el-col :span="4.5" class="operation" v-if="!readOnly">
                    <el-button
                      type="primary"
                      icon="el-icon-plus"
                      size="mini"
                      @click="addNodeTemplate(index, conditionIndex)"
                    >
                      {{ $t('decisionPlatform.or') }}
                    </el-button>
                    <el-button
                      type="danger"
                      v-if="ix > 0"
                      icon="el-icon-close"
                      size="mini"
                      @click="delNodeTemplate(index, conditionIndex, ix)"
                    >
                      {{ $t('decisionPlatform.delete') }}
                    </el-button>
                    <el-button
                      type="danger"
                      v-if="conditionIndex > 0 && ix == 0"
                      icon="el-icon-close"
                      size="mini"
                      @click="delNodeTemplate(index, conditionIndex)"
                    >
                      {{ $t('decisionPlatform.delete') }}
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div class="addBtn" v-if="!readOnly" @click="addTemplate(index)">
              <span>{{ $t('decisionPlatform.addAndJudgeCondition') }}</span>
            </div>
          </div>
        </div>
      </el-form-item>
      <!-- <div v-if="!cardForm.default">
        <el-form-item
          required
          :label="'判断条件' + (idx + 1)"
          v-for="(item, idx) in conditionArray"
          :key="idx"
        >
          <el-row>
            <div
              v-for="(obj, ix) in item.condition"
              :key="ix"
              class="condition"
            >
              <el-col :span="1.5" class="title">
                {{ ix === 0 ? '如果：' : ' 或者：' }}
              </el-col>
              <el-col :span="18">
                <el-form
                  :model="obj"
                  :rules="obj.rules"
                  class="select"
                  :ref="'condition' + ix"
                  :validate-on-rule-change="false"
                  :key="ix"
                >
                  <el-form-item prop="selectObj">
                    <el-cascader
                      ref="cascaderRef"
                      v-model="obj.selectObj"
                      placeholder="选择决策条件"
                      :options="options"
                      :props="{ checkStrictly: true }"
                      filterable
                      clearable
                      @change="
                        (event) => {
                          handleChange(event, obj, ix, item)
                        }
                      "
                    />
                  </el-form-item>
                  <el-form-item prop="operator">
                    <el-select
                      v-model="obj.operator"
                      placeholder="逻辑运算符"
                      :popper-append-to-body="false"
                      clearable
                    >
                      <el-option
                        v-for="item in conditionList"
                        :label="item.label"
                        :value="item.value"
                        :key="item.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item prop="result">
                    <el-input
                      v-model.trim="obj.result"
                      placeholder="请输入值"
                      clearable
                    />
                  </el-form-item>
                </el-form>
              </el-col>
              <el-col :span="5" class="operation">
                <el-button
                  type="primary"
                  icon="el-icon-plus"
                  size="mini"
                  @click="addNodeTemplate(item.id)"
                >
                  或者
                </el-button>
                <el-button
                  type="danger"
                  v-if="idx > 0 || (idx == 0 && ix > 0)"
                  icon="el-icon-minus"
                  size="mini"
                  @click="delNodeTemplate(item.id, obj.id)"
                >
                  删除
                </el-button>
              </el-col>
            </div>
          </el-row>
        </el-form-item>
        <el-form-item>
          <div class="addConditionBtn" @click="addTemplate">
            <span>新增并且判断条件</span>
          </div>
        </el-form-item>
      </div> -->
      <el-form-item :label="$t('decisionPlatform.priority')">
        <el-input v-model="argument" disabled class="noDisabledColor" />
      </el-form-item>
      <div class="dataTotal">
        <el-form-item :label="$t('decisionPlatform.dataTotal')">
          <el-select
            v-model="cardForm.DT_operator"
            :placeholder="$t('decisionPlatform.pleaseSelect')"
            :popper-append-to-body="false"
            clearable
            :disabled="readOnly"
            :class="{ noDisabledColor: readOnly }"
          >
            <el-option
              v-for="item in operatorList"
              :label="item.label"
              :value="item.value"
              :key="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="DT_result">
          <el-input
            v-model="cardForm.DT_result"
            :placeholder="$t('decisionPlatform.inputPlaceholder')"
            clearable
            :disabled="readOnly"
            :class="{ noDisabledColor: readOnly }"
          />
        </el-form-item>
      </div>
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
      <el-form-item
        :label="$t('decisionPlatform.independentEffect')"
        class="itemTips"
      >
        <el-radio-group
          v-model="cardForm.takeEffect"
          :disabled="readOnly"
          :class="{ noDisabledColor: readOnly }"
        >
          <el-radio :label="0">{{
            $t('decisionPlatform.notEffective')
          }}</el-radio>
          <el-radio :label="1">{{ $t('decisionPlatform.effective') }}</el-radio>
        </el-radio-group>
        <div class="tips">
          <p>{{ $t('decisionPlatform.scoreIndependentEffectTip') }}</p>
        </div>
      </el-form-item>
    </el-form>
    <div class="btnBottom" v-if="status">
      <!-- v-preventReClick -->
      <el-button type="primary" :disabled="btnLoading" @click="submit">
        <i class="el-icon-loading" v-if="btnLoading" />
        {{
          btnLoading
            ? $t('decisionPlatform.submitting')
            : $t('decisionPlatform.confirm')
        }}
      </el-button>
      <el-button @click="handleClose">{{
        $t('decisionPlatform.cancel')
      }}</el-button>
    </div>
    <!--		<confirmDialog
			ref="confirmDialog"
			@update="update"
			:disabled="fullscreenLoading"
			@reserved="reserved"
			@cancel="closeDialog"
		>
			<div>是否需要生成新的版本？</div>
		</confirmDialog>-->
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getDicts } from '../../api/index'
import {
  checkRuleCode,
  getSelectListTree,
  increaseRule,
  updateRule,
  versionControlFraud,
  versionReserveScore,
  getRuleById,
} from '../../api/score'
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
      btnLoading: false,
      cardForm: {
        indexRule: null,
        score: null,
        description: null,
        scoreCardId: null,
        parentCardId: null,
        takeEffect: 0,
        scorePrimaryId: null,
        scordPrimaryIds: null,
        scordCardId: null,
        generateRuleVO: {
          arraySize: 0,
          conditionArray: null,
        },
        default: false,
        conditionArray: null,
        id: null,
        DT_operator: null,
        DT_result: null,
      },
      logicGroupArray: [
        {
          conditionArray: [
            {
              condition: [
                {
                  selectObj: [],
                  operator: null,
                  result: null,
                  rules: {},
                },
              ],
            },
          ],
        },
      ],
      professionList: [],

      cidx: 1,
      coidx: 1,
      cmidx: 1,
      //条件对象
      conditionArray: [
        {
          id: 1,
          condition: [
            {
              id: 1,
              selectObj: [],
              operator: null,
              result: null,
              rules: {},
            },
          ],
        },
      ],
      //分析对象
      options: [],
      //更新对象
      modifyOptions: [],
    }
  },
  watch: {
    formData: {
      handler(val) {
        this.reset()
        this.getTheme()

        Object.assign(this.$data, this.$options.data())

        for (let key in this.cardForm) {
          if (![null, '', undefined].includes(val[key])) {
            this.cardForm[key] = val[key]
          }
        }

        this.$nextTick(() => {
          if (this.$refs.formRef) this.$refs.formRef.clearValidate()
        })
        this.getRuleData()
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
        indexRule: [
          {
            required: true,
            message: this.$t('decisionPlatform.ruleCodeCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
        description: [
          {
            required: true,
            message: this.$t('decisionPlatform.descriptionCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
        score: [
          {
            required: true,
            message: this.$t('decisionPlatform.scoreCannotBeEmpty'),
            trigger: 'blur',
          },
          { validator: this.checkWeight, trigger: 'blur' },
        ],
      }
    },
    conditionList() {
      return [
        {
          value: '==',
          label: this.$t('decisionPlatform.equals'),
          check: 'number',
        },
        {
          value: '!=',
          label: this.$t('decisionPlatform.notEquals'),
          check: 'number',
        },
        {
          value: '&gt;',
          label: this.$t('decisionPlatform.greaterThan'),
          check: 'number',
        },
        {
          value: '&lt;',
          label: this.$t('decisionPlatform.lessThan'),
          check: 'number',
        },
        {
          value: '&gt;=',
          label: this.$t('decisionPlatform.greaterThanOrEqual'),
          check: 'number',
        },
        {
          value: '&lt;=',
          label: this.$t('decisionPlatform.lessThanOrEqual'),
          check: 'number',
        },
        {
          value: 'contains',
          label: this.$t('decisionPlatform.contains'),
          check: 'string',
        },
        {
          value: 'not contains',
          label: this.$t('decisionPlatform.notContains'),
        },
        { value: 'memberOf', label: this.$t('decisionPlatform.exists') },
        { value: 'not memberOf', label: this.$t('decisionPlatform.notExists') },
        { value: 'matches', label: this.$t('decisionPlatform.regexMatch') },
        {
          value: 'not matches',
          label: this.$t('decisionPlatform.regexNotMatch'),
        },
        {
          value: 'checkDateDayNum',
          label: this.$t('decisionPlatform.dateDiffLessThanDay'),
        },
        {
          value: 'checkDateMonthNum',
          label: this.$t('decisionPlatform.dateDiffLessThanMonth'),
        },
        {
          value: 'checkDateYearNum',
          label: this.$t('decisionPlatform.dateDiffLessThanYear'),
        },
        {
          value: 'checkDateDayNumBig',
          label: this.$t('decisionPlatform.dateDiffGreaterThanDay'),
        },
        {
          value: 'checkDateMonthNumBig',
          label: this.$t('decisionPlatform.dateDiffGreaterThanMonth'),
        },
        {
          value: 'checkDateYearNumBig',
          label: this.$t('decisionPlatform.dateDiffGreaterThanYear'),
        },
      ]
    },
    operatorList() {
      return [
        {
          value: '==',
          label: this.$t('decisionPlatform.equals'),
          check: 'number',
        },
        {
          value: '&gt;',
          label: this.$t('decisionPlatform.greaterThan'),
          check: 'number',
        },
        {
          value: '&lt;',
          label: this.$t('decisionPlatform.lessThan'),
          check: 'number',
        },
        {
          value: '&gt;=',
          label: this.$t('decisionPlatform.greaterThanOrEqual'),
          check: 'number',
        },
        {
          value: '&lt;=',
          label: this.$t('decisionPlatform.lessThanOrEqual'),
          check: 'number',
        },
      ]
    },
    objRules() {
      return {
        selectObj: [
          {
            required: true,
            message: this.$t('decisionPlatform.decisionConditionCannotBeEmpty'),
            trigger: 'change',
          },
        ],
        operator: [
          {
            required: true,
            message: this.$t('decisionPlatform.logicalOperatorCannotBeEmpty'),
            trigger: 'change',
          },
        ],
        result: [
          {
            required: true,
            message: this.$t('decisionPlatform.valueCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
      }
    },
    argument() {
      return this.$t('decisionPlatform.autoGeneratePriority')
    },
  },
  mounted() {
    // this.init()
    // this.reset()
  },
  methods: {
    isEnglish() {
      return this.$i18n.locale === 'en'
    },
    checkWeight(rule, value, callback) {
      // let seq =
      //   /^(([-]?[1-9][0-9]?(\.\d{1,2})?)|([-]0(\.\d{1,2}))|^(0(\.\d{1,2})?)|100(\.[0]{1,2})?|([-]100(\.[0]{1,2})?))$/
      // if (!seq.test(value)) {
      //   callback(new Error('请输入-100-100的数字'))
      // }
      if (value > 200 || value < -200) {
        callback(
          new Error(this.$t('decisionPlatform.inputNumberMinus200To200'))
        )
        return
      }
      callback()
    },
    // mapType(customerType) {
    //   let type = null
    //   switch (customerType) {
    //   	case 0:
    //   		type = 'C、P'
    //   		break
    //   	case 1:
    //   		type = 'P'
    //   		break
    //   	case 2:
    //   		type = 'C'
    //   		break
    //   }
    //   return 'C'
    // },
    // // 关闭
    //   closeDialog() {
    //     // this.$emit('success')
    //     this.handleClose()
    //   },
    //   // 获取更新||保留版本参数
    //   getqueryData() {
    //     let obj = this.scoreCard.dataList.find(
    //       (item) => item.id === this.scoreCard.activeId
    //     )
    //     return {
    //       modelId: this.scoreCard.activeId,
    //       ...this.dataRisk.decision,
    //       modelName: obj.name,
    //       projectName: this.mapProObj.productName,
    //       businessName: this.dataRisk.decision.business.name,
    //       personOrCompany: this.mapType(this.mapProObj.customerType),
    //       championVersion: obj.versionObj.championVersion,
    //       versionControl: this.versionControl
    //     }
    //   },
    //   // 更新版本
    //   async update() {
    //     this.fullscreenLoading = true
    //     let promiseChain = Promise.resolve()
    //     promiseChain
    //       .then(() => {
    //         return this.submitConfirm()
    //       })
    //       .then(() => {
    //         versionControlFraud(this.getqueryData())
    //           .then((res) => {
    //             if (res.code == 200) {
    //               this.$refs.confirmDialog.visible = false
    //               this.fullscreenLoading = false
    //               this.$message.success('操作成功')
    //               this.$emit('success')
    //               this.handleClose()
    //             }
    //           })
    //           .catch(() => {
    //             this.fullscreenLoading = false
    //             this.$refs.confirmDialog.visible = false
    //           })
    //       })
    //   },
    //   // 保留原版本
    //   reserved() {
    //     this.fullscreenLoading = true
    //     let promiseChain = Promise.resolve()
    //     promiseChain
    //       .then(() => {
    //         return this.submitConfirm()
    //       })
    //       .then(() => {
    //         versionReserveScore({ ...this.getqueryData() })
    //           .then((res) => {
    //             if (res.code == 200) {
    //               this.$refs.confirmDialog.visible = false
    //               this.fullscreenLoading = false
    //               this.$message.success('操作成功')
    //               this.$emit('success')
    //               this.handleClose()
    //             }
    //           })
    //           .catch(() => {
    //             this.fullscreenLoading = false
    //             this.$refs.confirmDialog.visible = false
    //           })
    //       })
    //   },
    // init() {
    //   getDicts('sys_enterprise_industry')
    //     .then((res) => {
    //       this.professionList = res.data
    //     })
    //     .catch((err) => {})
    // },
    // async submitConfirm() {
    //     let params = {
    //       ...this.dataRisk.decision,
    //       ...this.cardForm,
    //       versionControl: this.versionControl
    //     }

    //     let APT =
    //       this.cardForm.id || this.cardForm.default ? updateRule : increaseRule
    //     return APT({ ...params, versionControl: this.versionControl })
    //       .then((res) => {
    //         if (res.code == 200) {
    //         }
    //       })
    //       .catch((err) => {})
    //   },

    getRuleData() {
      if (!this.formData.id) return
      getRuleById({ id: this.formData.id })
        .then((res) => {
          if (res.code == 200) {
            for (let key in this.cardForm) {
              if (![null, '', undefined].includes(res.data[key])) {
                this.cardForm[key] = res.data[key]
              }
            }

            // 处理逻辑组数据回显
            let logicGroupArray = res.data?.cgenerateRuleJSON?.logicGroupArray

            if (logicGroupArray && logicGroupArray.length > 0) {
              let handleLogicGroupArray = []
              logicGroupArray.forEach((item) => {
                let conditionArray = []
                item.conditionArray.forEach((itemX) => {
                  let condition = []
                  itemX.condition.forEach((itemY) => {
                    let obj = {
                      selectObj: itemY.objectLevel,
                      operator: itemY.operator,
                      result: itemY.result.result,
                      rules: this.objRules,
                    }

                    condition.push(obj)
                  })
                  conditionArray.push({ condition: condition })
                })
                handleLogicGroupArray.push({ conditionArray: conditionArray })
              })
              this.logicGroupArray = handleLogicGroupArray
            }

            // 处理数据总数回显
            let conditionArray =
              res.data?.cgenerateRuleJSON?.conditionArray ||
              res.data?.conditionArrayJSON

            if (conditionArray && conditionArray.length > 0) {
              conditionArray.forEach((item) => {
                item.condition.forEach((itemX) => {
                  if (itemX.hasOwnProperty('arraySize') && itemX.arraySize) {
                    this.$set(this.cardForm, 'DT_operator', itemX.operator)
                    this.$set(this.cardForm, 'DT_result', itemX.result.result)

                    item.clear = 1
                  }
                  itemX.result = itemX.result.result
                  itemX.selectObj = null
                  itemX.rules = this.objRules
                  if (itemX.objectLevel) {
                    itemX.selectObj = itemX.objectLevel
                  }
                })
              })

              let oldConditionArray = conditionArray.filter(
                (item) => !item.clear
              )
              if (oldConditionArray.length) {
                this.logicGroupArray = [{ conditionArray: oldConditionArray }]
              }
            }
          }
        })
        .catch((err) => {})
    },

    submit() {
      try {
        let isForm = false
        let isIF = true
        this.$refs.formRef.validate((valid) => {
          isForm = valid
        })

        this.logicGroupArray.forEach((logicGroup) => {
          if (logicGroup.conditionArray && logicGroup.conditionArray.length) {
            logicGroup.conditionArray.forEach((item) => {
              if (item.condition && item.condition.length) {
                item.condition.forEach((itemX, index) => {
                  const refs = this.$refs[`condition${index}`]

                  if (refs && refs.length > 0) {
                    // 如果有多个 ref，验证所有匹配的 ref
                    refs.forEach((ref) => {
                      ref.validate((valid) => {
                        if (!valid) {
                          isIF = false
                        }
                      })
                    })
                  }
                })
              }
            })
          }
        })

        if (isIF && isForm) {
          let cardForm = { ...this.cardForm }

          let processedLogicGroupArray = []

          this.logicGroupArray.forEach((logicGroup) => {
            if (logicGroup.conditionArray && logicGroup.conditionArray.length) {
              let copiedConditionArray = JSON.parse(
                JSON.stringify(logicGroup.conditionArray)
              )
              copiedConditionArray.forEach((item, index) => {
                item.condition.forEach((itemX) => {
                  delete itemX.rules
                  itemX.objectLevel = JSON.parse(
                    JSON.stringify(itemX.selectObj)
                  )
                  let length = itemX.selectObj.length
                  let firstLeaf,
                    secondLeaf,
                    thirdLeaf = null
                  firstLeaf = this.options.find(
                    (itemChild) => itemChild.value == itemX.selectObj[0]
                  )
                  if (length > 1 && firstLeaf && firstLeaf.children)
                    secondLeaf = firstLeaf.children.find(
                      (itemChild) => itemChild.value == itemX.selectObj[1]
                    )
                  if (length > 2 && secondLeaf && secondLeaf.children)
                    thirdLeaf = secondLeaf.children.find(
                      (itemChild) => itemChild.value == itemX.selectObj[2]
                    )
                  let obj = {
                    manageNo: null,
                    interfaceNo: null,
                    sourceNo: null,
                    objectFlag: secondLeaf ? secondLeaf.objectFlag : null,
                    stats: thirdLeaf
                      ? thirdLeaf.code
                      : secondLeaf
                      ? secondLeaf.code
                      : null,
                    dataType: thirdLeaf
                      ? thirdLeaf.dataType
                      : secondLeaf
                      ? secondLeaf.dataType
                      : null,
                  }
                  if (
                    secondLeaf &&
                    secondLeaf.interface &&
                    secondLeaf.interface.length
                  ) {
                    obj.manageNo = secondLeaf.interface[0].manageNo
                    obj.interfaceNo = secondLeaf.interface[0].interfaceNo
                    obj.objectName = secondLeaf.interface[0].interfaceNo
                    obj.anotherName = secondLeaf.interface[0].interfaceNo
                    obj.sourceNo = secondLeaf.interface[0].sourceNo
                    obj.drlCode = ''
                  }
                  let result = {
                    result: itemX.result,
                    resultType: thirdLeaf ? thirdLeaf.resultType : null,
                  }
                  itemX.result = result
                  itemX.selectObj = obj
                })
              })
              processedLogicGroupArray.push({
                conditionArray: copiedConditionArray,
              })
            }
          })

          let conditionArray = []
          // 处理数据总数
          if (cardForm.DT_operator && cardForm.DT_result) {
            cardForm.generateRuleVO.arraySize = 1
            let DT_obj = {
              // id: totalConditionCount + 1,
              condition: [
                {
                  id: 1,
                  operator: cardForm.DT_operator,
                  result: {
                    result: cardForm.DT_result,
                    resultType: cardForm.DT_result,
                  },
                  selectObj: {},
                  objectLevel: [],
                  arraySize: 1,
                },
              ],
            }
            conditionArray.push(DT_obj)
          }

          cardForm.generateRuleVO.logicGroupArray = processedLogicGroupArray
          cardForm.generateRuleVO.conditionArray = conditionArray

          // console.log(cardForm, 'cardForm--------------------')
          // return

          this.btnLoading = true
          let APT = this.cardForm.id ? updateRule : increaseRule
          return APT({
            ...cardForm,
            ...this.dataRisk.decision,
            versionControl: this.versionControl,
          })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.$emit('success')
                this.handleClose()
                this.btnLoading = false
              }
            })
            .catch((err) => {})

          // this.$refs.confirmDialog.visible = true
        }
      } catch (error) {
        console.log(error, 'error')
      }
    },
    handleScoreCode(val) {
      if (val) {
        let params = {
          indexRule: val,
          scorePrimaryId: this.cardForm.scorePrimaryId,
        }
        checkRuleCode({ ...params })
          .then((res) => {
            if (res.code == 200) {
              if (res.data) {
                this.rules.indexRule.push({
                  validator: this.checkCodeNo,
                  trigger: 'blur',
                })
              } else {
                this.rules.indexRule.pop()
              }
              this.$refs.cardForm.validateField('indexRule', (valid) => {
                console.log(valid)
              })
            } else {
              if (this.rules.length >= 2) {
                this.rules.indexRule.slice(0, 2)
              }
            }
          })
          .catch((err) => {})
      }
    },
    checkCodeNo(rule, value, callback) {
      if (value) {
        return callback(
          new Error(this.$t('decisionPlatform.ruleCodeDuplicate'))
        )
      } else {
        callback()
      }
    },

    getTheme() {
      const decisionStandardList = JSON.parse(
        sessionStorage.getItem('decisionStandard')
      )
      let params = {
        deptIds: decisionStandardList.join(','),
      }
      getSelectListTree(this.readOnly ? params : null)
        .then((res) => {
          if (res.code == 200) {
            let data = res.data
            let list = []
            for (let key in data) {
              data[key].forEach((item) => {
                item.type = key
                list.push(item)
              })
            }
            this.options = list
          }
        })
        .catch((err) => {})
    },

    handleChange(val, item, index) {
      item.result = ''
      this.$refs[`condition${index}`][0].clearValidate()
      if (val.length == 3) {
        let list = JSON.parse(JSON.stringify(val))
        let data = this.getCascaderData(list, this.options)
        if (data.code === 'ybxm_bys') {
          this.$set(data, 'type', '1')
        } else {
          this.$set(data, 'type', '3')
        }
        switch (data.type) {
          case '1':
            item.rules.result.push({
              validator: this.checkNumber,
              trigger: 'blur',
            })
            break
          case '3':
            item.rules.result.push({
              validator: this.checkString,
              trigger: 'blur',
            })
            break
        }
      }
    },

    checkNumber(rule, value, callback) {
      let check = /^([1-9]\d*.?|0.)\d*$/
      if (!value) {
        return callback(
          new Error(this.$t('decisionPlatform.valueCannotBeEmpty'))
        )
      } else if (!check.test(value)) {
        return callback(new Error(this.$t('decisionPlatform.onlyInputNumber')))
      } else {
        callback()
      }
    },
    checkString(rule, value, callback) {
      if (!value) {
        return callback(
          new Error(this.$t('decisionPlatform.valueCannotBeEmpty'))
        )
      } else {
        callback()
      }
    },

    getCascaderData(valueList, dataList) {
      let data = dataList.find((item) => item.value == valueList[0])
      let list = data.children
      if (list && valueList.length > 0) {
        valueList.shift()
        return this.getCascaderData(valueList, list)
      } else {
        return data
      }
    },

    addNodeTemplate(index, conditionIndex) {
      let newobj = {
        result: '',
        operator: null,
        selectObj: [],
        rules: this.objRules,
      }
      this.logicGroupArray[index].conditionArray[conditionIndex].condition.push(
        newobj
      )
    },
    delNodeTemplate(index, conditionIndex = null, ix = null) {
      let conditionList = this.logicGroupArray[index].conditionArray
      let list = conditionList[conditionIndex]
      if (ix != null) {
        list.condition.splice(ix, 1)
      } else if (conditionIndex != null) {
        conditionList.splice(conditionIndex, 1)
      } else {
        this.logicGroupArray.splice(index, 1)
      }
    },
    addTemplate(index = null) {
      let selectO =
        index != null
          ? this.logicGroupArray[index].conditionArray[0].condition[0].selectObj
          : []
      let newobj = {
        condition: [
          {
            selectObj: selectO,
            operator: null,
            result: null,
            rules: this.objRules,
          },
        ],
      }
      if (index != null) {
        this.logicGroupArray[index].conditionArray.push(newobj)
      } else {
        this.logicGroupArray.push({
          conditionArray: [newobj],
        })
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
    reset() {
      this.conditionArray = this.$options.data().conditionArray
      if (this.$refs['condition0'] && this.$refs['condition0'].length > 0) {
        this.$refs['condition0'][0].resetFields()
      }
    },
  },
}
</script>

<style lang="less" scoped>
.addTactics {
  padding: 0px 20px;
  box-sizing: border-box;

  .addBtn {
    width: 100%;
    height: 42px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: var(--primary-color);
    font-size: 14px;
    border: 1px var(--primary-color) dashed;
    border-radius: 6px;
  }

  .btnBottom {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    > button {
      border: none;
      font-size: 16px;
      height: 42px;
      border-radius: 6px;
    }

    > button:nth-of-type(1) {
      min-width: 84px;
    }

    > button:nth-of-type(2) {
      background: #e4e6ef;
      color: #3f4254;
    }
  }

  ::v-deep .el-form {
    .el-form-item__label,
    .el-form-item__content {
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

  .condition {
    display: flex;
    font-size: 14px;
    margin-bottom: 5px;
    align-content: center;

    .title {
      min-width: 42px;
      line-height: 50px;
      color: rgba(#000, 0.6);
    }

    .select {
      display: flex;
      align-items: center;
      justify-content: space-around;
      padding-right: 10px;

      /deep/ .el-form-item {
        width: 35%;
        margin-bottom: 0px;
      }

      /deep/ .el-input,
      .el-select,
      .el-cascader {
        // width: 195px;
        width: 96%;
      }
    }

    .operation {
      /deep/ .el-button {
        height: 48px;
      }
    }
  }

  .addConditionBtn {
    width: 100%;
    height: 42px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: var(--primary-color);
    font-size: 14px;
    border: 1px var(--primary-color) dashed;
    border-radius: 6px;
  }
}

.itemTips {
  /deep/ .el-form-item__content {
    display: flex;
    align-items: center;

    .el-input {
      width: 300px;
    }

    .el-radio {
      height: 48px;
      line-height: 48px;
      margin-right: 20px;
    }
  }

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

.noDisabledColor {
  /deep/ .el-input__inner,
  /deep/ .el-textarea__inner {
    background-color: rgba(#000, 0.08) !important;
    color: rgba(#000, 0.85);
  }

  /deep/ .el-radio__input.is-disabled.is-checked .el-radio__inner {
    background-color: #409eff;
    border-color: #409eff;

    &::after {
      background-color: #ffffff;
    }
  }

  /deep/ .el-radio__input.is-disabled.is-checked + span.el-radio__label {
    color: #409eff;
  }
}

:deep(.dataTotal) {
  display: flex;

  .el-form-item:last-child {
    .el-form-item__content {
      margin-left: 20px !important;
    }
  }

  .el-form-item__content {
    .el-select,
    .el-input {
      width: 200px;
    }
  }
}

.logicGroup {
  border: 1px dashed #e4e6ef;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 10px;

  &:last-child {
    margin-bottom: 0;
  }
  &-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    &-title {
      line-height: 24px;
      font-size: 16px;
      margin-right: 20px;
      color: #2888e8;
    }
    .el-button {
      height: 42px;
    }
  }
  &-content {
    display: flex;
    flex-direction: column;
    &-item {
      width: 100%;
      display: flex;
      align-items: start;

      .item-title {
        width: 60px;
        white-space: nowrap;
        line-height: 42px;
      }

      .item-condition {
        flex: 1;
        display: flex;
        flex-direction: column;
        font-size: 14px;
        align-content: center;

        ::v-deep .el-row {
          width: 100%;
          margin-bottom: 20px;
        }

        .title {
          min-width: 42px;
          line-height: 42px;
          color: rgba(#000, 0.6);
        }

        .select {
          display: flex;
          align-items: center;
          justify-content: space-around;
          padding-right: 10px;

          ::v-deep .el-form-item {
            width: 35%;
            margin-bottom: 0px;

            .el-form-item__content {
              padding: 0px;
              border: none;
            }
          }

          ::v-deep .el-input,
          .el-select,
          .el-cascader {
            // width: 195px;
            width: 96%;
          }
        }

        .operation {
          ::v-deep .el-button {
            height: 42px;
          }
        }
      }
    }
  }
}
</style>
