<template>
  <div class="addRule">
    <div class="add" v-loading="loading">
      <el-form
        ref="codeForm"
        :model="codeForm"
        label-width="100px"
        :rules="rules"
      >
        <el-row>
          <el-col :span="24">
            <el-form-item label="决策CODE" prop="code">
              <el-input
                v-model="codeForm.code"
                @input="(e) => (codeForm.code = e.replace(/\s*/g, ''))"
                @change="handleCode"
                placeholder="请输入code"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="决策等级" prop="level">
              <el-select
                v-model="codeForm.level"
                placeholder="请选择等级"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
                clearable
                @clear="codeForm.level = null"
              >
                <el-option
                  v-for="(item, index) in levelOptions"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="额度" prop="quatoRate">
              <el-input
                v-model="codeForm.quatoRate"
                placeholder="请输入额度"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="决策说明" prop="content">
              <el-input
                type="textarea"
                v-model.trim="codeForm.content"
                placeholder="请输入说明"
                maxlength="200"
                :rows="5"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="执行优先级" prop="salience">
              <el-input
                v-model="codeForm.salience"
                placeholder="请输入执行优先级"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
                clearable
                @clear="codeForm.salience = 0"
              /><span class="tips"
                >规则优先级，值越高的规则，优先级越高，越先执行</span
              >
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否强拒绝">
              <el-radio-group
                v-model="codeForm.stronglyReject"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              >
                <el-radio label="1">拒绝</el-radio>
                <el-radio label="0">通过</el-radio>
                <!--								<el-radio label="2">转人工</el-radio>-->
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否转人工">
              <el-radio-group
                v-model="codeForm.transferToPerson"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              >
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="复杂条件配置" class="logicGroupFormItem">
              <div
                class="logicGroup"
                v-for="(item, index) in logicGroupArray"
                :key="index"
              >
                <div class="logicGroup-header">
                  <div class="logicGroup-header-title">
                    逻辑组{{ index + 1 }}
                  </div>
                  <el-button
                    v-if="!readOnly"
                    type="primary"
                    icon="el-icon-plus"
                    size="mini"
                    @click="addTemplate()"
                    >新增逻辑组</el-button
                  >
                  <el-button
                    v-if="index > 0 && !readOnly"
                    type="danger"
                    icon="el-icon-delete"
                    size="mini"
                    @click="delNodeTemplate(index)"
                    >删除</el-button
                  >
                </div>
                <div class="logicGroup-content">
                  <div
                    class="logicGroup-content-item"
                    v-for="(
                      conditionItem, conditionIndex
                    ) in item.conditionArray"
                    :key="conditionIndex"
                  >
                    <div class="item-title">
                      <span>条件{{ conditionIndex + 1 }}</span>
                    </div>
                    <div class="item-condition">
                      <el-row
                        v-for="(obj, ix) in conditionItem.condition"
                        :key="ix"
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
                                v-model="obj.selectObj"
                                placeholder="选择决策条件"
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
                                placeholder="逻辑运算符"
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
                                placeholder="请输入值"
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
                            或者
                          </el-button>
                          <el-button
                            type="danger"
                            v-if="ix > 0"
                            icon="el-icon-close"
                            size="mini"
                            @click="delNodeTemplate(index, conditionIndex, ix)"
                          >
                            删除
                          </el-button>
                          <el-button
                            type="danger"
                            v-if="conditionIndex > 0 && ix == 0"
                            icon="el-icon-close"
                            size="mini"
                            @click="delNodeTemplate(index, conditionIndex)"
                          >
                            删除
                          </el-button>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                  <div
                    class="addBtn"
                    v-if="!readOnly"
                    @click="addTemplate(index)"
                  >
                    <span>新增并且判断条件</span>
                  </div>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24" class="dataTotal">
            <el-form-item label="数据总数">
              <el-select
                v-model="codeForm.DT_operator"
                placeholder="请选择"
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
                v-model="codeForm.DT_result"
                placeholder="请输入"
                clearable
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="this.dataRisk.decision.businessCode == 6">
            <el-form-item
              class="longLabel"
              prop="generateRuleVO.objResultCompare"
            >
              <template slot="label">预警数据<br />对比属性</template>
              <el-cascader
                style="width: 100%"
                v-model="codeForm.generateRuleVO.objResultCompare"
                placeholder="选择决策条件"
                :options="options"
                collapse-tags
                :props="{ multiple: true }"
                filterable
                clearable
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                type="textarea"
                :rows="5"
                v-model="codeForm.remark"
                placeholder="请输入备注"
                :disabled="readOnly"
                :class="{ noDisabledColor: readOnly }"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="btnBottom" v-if="info && info.type != 0 && !readOnly">
        <el-button type="primary" @click="handleBeforeSubmit">确定</el-button>
        <el-button @click="handleClose">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import addCodeRule from '../../../../decisionPlatform/components/addCodeRule'
import { mapState, mapGetters } from 'vuex'
import {
  addCodeSubmit,
  checkCode,
  getSelectListTree,
  searchAddCode,
  searchObjectList,
  searchSelectCodeList,
  searchSelectSubmit,
  lockStatus,
} from '../api/riskModel'

// 暂时注释 20260105
// import { logChange } from '../api/index'
// import { handleCheckLock } from '../utils/index.js'

export default {
  name: 'addRule',
  props: {
    info: {
      type: Object,
      default: () => {},
    },
    tacticsId: {
      type: Number,
      default: 0,
    },
    ruleGroupId: {
      type: Number,
      default: 0,
    },
    selectList: {
      type: Array,
      default: () => [],
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
  components: {
    Search,
    addCodeRule,
    /*confirmDialog*/
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
      codeForm: {
        id: null,
        code: null,
        level: null,
        content: null,
        remark: null,
        generateRuleVO: {
          arraySize: 0,
          conditionArray: [],
          objResultCompare: [],
        },
        stronglyReject: '0',
        transferToPerson: '0',
        quatoRate: null,
        DT_operator: null, //数据总数运算符
        DT_result: null, //数据总数值
        salience: 0,
        termWarn: [],
      },
      rules: {
        salience: [
          { required: true, message: '执行优先级不能为空', trigger: 'blur' },
          {
            pattern: /^(-)?\d+$/,
            message: '执行优先级只能输入整数',
            trigger: ['blur', 'change'],
          },
        ],
        code: [
          { required: true, message: '决策code不能为空', trigger: 'blur' },
          // {
          //   validator: (rule, value, callback) => {
          //     let check = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/
          //     if (!check.test(value)) {
          //       callback('请输入不包含特殊字符的名称')
          //     }
          //     callback()
          //   }, trigger: 'blur'
          // },
        ],
        level: [
          { required: true, message: '决策等级不能为空', trigger: 'blur' },
          {
            pattern: /^(-)?\d+$/,
            message: '决策等级只能输入数字',
            trigger: ['blur', 'change'],
          },
        ],
        content: [
          { required: true, message: '决策说明不能为空', trigger: 'blur' },
        ],
        remark: [
          { max: 200, message: '备注文字长度不能超过200', trigger: 'blur' },
        ],
        DT_result: [
          {
            pattern: /^(-)?\d+$/,
            message: '请输入数字',
            trigger: ['blur', 'change'],
          },
        ],
        'generateRuleVO.objResultCompare': [
          {
            required: true,
            message: '预警数据对比属性不能为空',
            trigger: 'change',
          },
        ],
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
      objRules: {
        selectObj: [
          { required: true, message: '决策条件不能为空', trigger: 'change' },
        ],
        operator: [
          { required: true, message: '逻辑运算符不能为空', trigger: 'change' },
        ],
        result: [{ required: true, message: '值不能为空', trigger: 'blur' }],
      },
      conditionList: [
        { value: '==', label: '等于', check: 'number' },
        { value: '!=', label: '不等于', check: 'number' },
        // { value: '>', label: '大于', check: 'number' },
        // { value: '<', label: '小于', check: 'number' },
        // { value: '>=', label: '大于等于', check: 'number' },
        // { value: '<=', label: '小于等于', check: 'number' },
        { value: '&gt;', label: '大于', check: 'number' },
        { value: '&lt;', label: '小于', check: 'number' },
        { value: '&gt;=', label: '大于等于', check: 'number' },
        { value: '&lt;=', label: '小于等于', check: 'number' },
        { value: 'contains', label: '包含', check: 'string' },
        { value: 'not contains', label: '不包含' },
        { value: 'memberOf', label: '存在' },
        { value: 'not memberOf', label: '不存在' },
        { value: 'matches', label: '正则命中' },
        { value: 'not matches', label: '正则取反' },
        { value: 'checkDateDayNum', label: '和当前日期相差小于天' },
        { value: 'checkDateMonthNum', label: '和当前日期相差小于月' },
        { value: 'checkDateYearNum', label: '和当前日期相差小于年' },
        { value: 'checkDateDayNumBig', label: '和当前日期相差大于天' },
        { value: 'checkDateMonthNumBig', label: '和当前日期相差大于月' },
        { value: 'checkDateYearNumBig', label: '和当前日期相差大于年' },
        { value: 'notArrayOr', label: '多条件集合不存在' },
        { value: 'ArrayOr', label: '多条件集合存在' },
      ],
      operatorList: [
        { value: '==', label: '等于', check: 'number' },
        // { value: '>', label: '大于', check: 'number' },
        // { value: '<', label: '小于', check: 'number' },
        // { value: '>=', label: '大于等于', check: 'number' },
        // { value: '<=', label: '小于等于', check: 'number' },
        { value: '&gt;', label: '大于', check: 'number' },
        { value: '&lt;', label: '小于', check: 'number' },
        { value: '&gt;=', label: '大于等于', check: 'number' },
        { value: '&lt;=', label: '小于等于', check: 'number' },
      ],
      cidx: 1,
      coidx: 1,
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
      objResultCompareBackup: [],
      objResultCompare: [],
      //分析对象
      options: [],
      //更新对象
      modifyOptions: [],
      loading: false,
      editSearchList: [
        {
          type: 'input',
          placeholder: '请输入决策code',
          prop: {
            key: 'code',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: '请输入说明',
          prop: {
            key: 'content',
            value: null,
          },
        },
      ],
      levelOptions: [
        { value: '1', label: '低风险' },
        { value: '2', label: '中低风险' },
        { value: '3', label: '中风险' },
        { value: '4', label: '中高风险' },
        { value: '5', label: '高风险' },
      ],
      enterpriseTable: {
        dataList: [],
        loading: false,
        tableBottom: 185,
        totalNum: 0,
        searchForm: {
          pageNum: 1,
          pageSize: 10,
          themeIds: null,
          type: 1,
        },
        list: [
          {
            prop: 'code',
            width: '200px',
            label: '规则code',
          },
          {
            prop: 'content',
            width: '',
            label: '说明',
          },
        ],
        pagination: 'prev, pager, next, jumper',
      },
      selectTable: {
        dataList: [],
        tableBottom: 120,
        loading: false,
        list: [
          {
            prop: 'code',
            width: '200px',
            label: '规则code',
          },
          {
            prop: 'content',
            width: '',
            label: '说明',
          },
        ],
      },
      searchSelectCode: [],
      ObjectIdList: [],
      projectCode: null,

      valueMap: new Map(), // 规则value映射
      valueMapSize: 0,

      formBackup: null,
      conditionBackup: null,
      logicGroupArrayBackup: null,
      logRecord: {
        controlRecordId: null,
        ownershipSubject: null,
        currentVersion: null,
        projectCode: null,
        businessCode: null,
        ruleCode: null,
        changeType: null, //CREATED,UPDATED,DELETED
      },
    }
  },
  watch: {
    info: {
      handler(val) {
        //查询分析主体
        this.getTheme()
        this.handleLogBaseData()
        if (val && val.id) {
          this.getDetailCode()
        } else {
          this.reset()
        }
      },
      immediate: true,
      deep: true,
    },
    selectList: {
      handler(val) {
        if (val && val.length > 0) {
          this.selectTable.dataList = val
        }
      },
      immediate: true,
      deep: true,
    },

    valueMapSize: {
      handler() {
        this.handleFeedback()
      },
      deep: true,
    },
  },
  methods: {
    // handleCheckLock, 暂时注释 20260105
    handleLogBaseData() {
      if (this.readOnly) return
      let decision = this.dataRisk.decision
      let productList = this.dataRisk.productList

      this.logRecord.projectCode = decision.projectCode || null
      this.logRecord.businessCode = decision.businessCode || null
      this.logRecord.ruleCode = decision.ruleCode || null
      this.logRecord.ownershipSubject =
        productList.find((item) => item.id === decision.projectCode)
          .productName || null

      this.logRecord.controlRecordId = this.info?.controlRecordId || null
      this.logRecord.changeType = Object.keys(this.info).length
        ? 'UPDATED'
        : 'CREATED'
      this.logRecord.currentVersion = this.info?.versionControl || null
    },

    handleChange(val, item, index) {
      console.log(item, 'item----------')

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
    checkNumber: (rule, value, callback) => {
      console.log(value)
      let check = /^([1-9]\d*.?|0.)\d*$/
      if (!value) {
        return callback(new Error('值不能为空'))
      } else if (!check.test(value)) {
        return callback(new Error('只能输入数字'))
      } else {
        callback()
      }
    },
    checkString: (rule, value, callback) => {
      if (!value) {
        return callback(new Error('值不能为空'))
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
    handleCode(val) {
      if (val) {
        checkCode({
          code: val,
          ...this.dataRisk.decision,
          id: this.info.codeId,
          moduleId: this.tacticsId,
        })
          .then((res) => {
            if (res.code == 200) {
              if (res.data) {
                this.rules.code.push({
                  validator: this.checkCodeNo,
                  trigger: 'blur',
                })
              } else if (this.rules.code.length > 1) {
                this.rules.code.pop()
              }
              this.$refs.codeForm.validateField('code', (valid) => {
                console.log(valid)
              })
            } else {
              if (this.rules.length >= 2) {
                this.rules.code.slice(0, 2)
              }
            }
          })
          .catch((err) => {
            console.log(err, 'err--------------')
          })
      }
    },

    checkCodeNo: (rule, value, callback) => {
      if (value) {
        return callback(new Error('当前决策code重复'))
      } else {
        callback()
      }
    },

    handleSelectCode(data) {
      this.enterpriseTable.searchForm.pageNum = 1
      this.searchSelectCode = data
      this.getSelectCodeList()
    },
    handleSelectCodeCurrent(val) {
      this.enterpriseTable.searchForm.pageNum = val
      this.getSelectCodeList()
    },
    handleSelectCodeSize(val) {
      this.enterpriseTable.searchForm.pageSize = val
      this.getSelectCodeList()
    },
    getSelectCodeList() {
      this.enterpriseTable.searchForm.themeIds = this.ObjectIdList.join(',')
      this.enterpriseTable.loading = true
      searchSelectCodeList({
        ...this.enterpriseTable.searchForm,
        ...this.searchSelectCode,
      })
        .then((res) => {
          if (res.code == 200) {
            this.enterpriseTable.dataList = res.rows
            this.enterpriseTable.totalNum = res.total
            this.enterpriseTable.loading = false
          }
        })
        .catch((err) => {})
    },

    getDetailCode() {
      this.reset()
      searchAddCode({ id: this.info.codeId })
        .then((res) => {
          for (let key in this.codeForm) {
            if (![null, '', undefined].includes(res.data[key])) {
              this.codeForm[key] = res.data[key]
            }
          }

          // 处理逻辑组数据回显
          let logicGroupArray = res.data?.cgenerateRuleJSON?.logicGroupArray
          if (logicGroupArray && logicGroupArray.length > 0) {
            // 备份数据 对比变更
            this.logicGroupArrayBackup = logicGroupArray
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
          let conditionArray = res.data?.conditionsJSON

          if (conditionArray && conditionArray.length > 0) {
            conditionArray.forEach((item) => {
              item.condition.forEach((itemX) => {
                if (itemX.hasOwnProperty('arraySize') && itemX.arraySize) {
                  this.$set(this.codeForm, 'DT_operator', itemX.operator)
                  this.$set(this.codeForm, 'DT_result', itemX.result.result)
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

            let oldConditionArray = conditionArray.filter((item) => !item.clear)
            if (oldConditionArray.length) {
              this.logicGroupArray = [{ conditionArray: oldConditionArray }]
            }

            this.conditionBackup = [
              ...conditionArray.filter((item) => item.clear),
            ]
          }

          this.handleFeedback()

          this.formBackup = { ...this.codeForm }
          this.objResultCompareBackup = [
            ...this.codeForm.generateRuleVO.objResultCompare,
          ]
        })
        .catch((err) => {
          console.log(err, 'err--------------')
        })
    },

    handleFeedback() {
      if (
        this.codeForm.generateRuleVO.objResultCompare.length ||
        !this.valueMap.size
      )
        return

      let termWarn = this.codeForm.termWarn
      if (termWarn && termWarn.length) {
        termWarn.forEach((item) => {
          let path = this.valueMap.get(item.value)
          if (path) {
            this.codeForm.generateRuleVO.objResultCompare.push(
              path.map((item) => item.value)
            )
          }
        })
      }
    },

    getTheme() {
      this.loading = true
      const decisionStandardList = JSON.parse(
        sessionStorage.getItem('decisionStandard')
      )
      let params = {
        deptIds: decisionStandardList.join(','),
      }
      getSelectListTree(this.readOnly ? params : null)
        .then((res) => {
          let data = res.data
          let list = []
          for (let key in data) {
            data[key].forEach((item) => {
              item.type = key
              list.push(item)
            })
          }
          let that = this
          function traverse(node, path) {
            try {
              // 创建当前节点的新路径（复制原路径并添加当前节点）
              const currentPath = [...path, node]

              // 检查是否为叶子节点（没有children）
              if (
                !node.hasOwnProperty('children') ||
                (node.children && node.children.length === 0)
              ) {
                that.valueMap.set(node.value, currentPath)
              }

              // 递归处理子节点
              if (node.children && node.children.length) {
                node.children.forEach((child) => {
                  traverse(child, currentPath)
                })
              }
            } catch (error) {
              console.log(error, 'error')
            }
          }

          // 遍历根节点数组
          list.forEach((rootNode) => {
            traverse(rootNode, [])
          })

          this.valueMapSize = this.valueMap.size

          this.options = list
          this.loading = false
        })
        .catch((err) => {
          this.loading = false
        })
    },

    async handleBeforeSubmit() {
      // if (!(await this.handleCheckLock())) return 暂时注释 20260105
      let isForm = false
      let isIF = true
      this.$refs.codeForm.validate((valid) => {
        isForm = valid
      })
      // 验证判断条件
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
        let codeForm = { ...this.codeForm }
        this.loading = true

        let processedLogicGroupArray = []

        this.logicGroupArray.forEach((logicGroup) => {
          if (logicGroup.conditionArray && logicGroup.conditionArray.length) {
            let copiedConditionArray = JSON.parse(
              JSON.stringify(logicGroup.conditionArray)
            )
            copiedConditionArray.forEach((item, index) => {
              item.condition.forEach((itemX) => {
                delete itemX.rules
                itemX.objectLevel = JSON.parse(JSON.stringify(itemX.selectObj))
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
        if (codeForm.DT_operator && codeForm.DT_result) {
          codeForm.generateRuleVO.arraySize = 1
          let DT_obj = {
            // id: totalConditionCount + 1,
            condition: [
              {
                id: 1,
                operator: codeForm.DT_operator,
                result: {
                  result: codeForm.DT_result,
                  resultType: codeForm.DT_result,
                },
                selectObj: {},
                objectLevel: [],
                arraySize: 1,
              },
            ],
          }
          conditionArray.push(DT_obj)
        }

        codeForm.generateRuleVO.logicGroupArray = processedLogicGroupArray
        codeForm.generateRuleVO.conditionArray = conditionArray

        if (codeForm.generateRuleVO.objResultCompare.length) {
          let list = []
          let objResultCompare =
            this.codeForm.generateRuleVO.objResultCompare.map((item) => {
              return item[item.length - 1]
            })

          objResultCompare.forEach((item) => {
            let objList = this.valueMap.get(item).map((itemX) => {
              return {
                code: itemX.code,
                value: itemX.value,
              }
            })
            list.push(objList[objList.length - 1])
          })

          codeForm.generateRuleVO.objResultCompare = list
        }

        this.$nextTick(() => {
          // 暂时注释 20260105
          // let formBackup = this.formBackup
          // // 表单基本信息对比
          // const changes = this.compareObjects(formBackup, codeForm)
          // // 数据总数 对比
          // let checkCondition = this.deepEqual(
          //   this.conditionBackup,
          //   conditionArray
          // )
          // if (!checkCondition) {
          //   changes.push({
          //     targetId: this.info.id || null,
          //     targetType: 'RULE',
          //     fieldName: 'conditionArray',
          //     fieldDisplayName: '数据总数',
          //     oldValue: this.conditionBackup,
          //     newValue: conditionArray,
          //   })
          // }
          // 判断条件 对比
          // let checkLogicGroup = this.deepEqual(
          //   this.logicGroupArrayBackup,
          //   processedLogicGroupArray
          // )
          // if (!checkLogicGroup) {
          //   changes.push({
          //     targetId: this.info.id || null,
          //     targetType: 'RULE',
          //     fieldName: 'logicGroup',
          //     fieldDisplayName: '判断条件',
          //     oldValue: this.logicGroupArrayBackup,
          //     newValue: processedLogicGroupArray,
          //   })
          // }
          // // 预警数据对比属性 对比
          // let objResultCompareChanges = this.deepEqual(
          //   this.objResultCompareBackup,
          //   codeForm.generateRuleVO.objResultCompare
          // )
          // if (!objResultCompareChanges) {
          //   changes.push({
          //     targetId: this.info.id || null,
          //     targetType: 'RULE',
          //     fieldName: 'term',
          //     fieldDisplayName: '预警数据对比属性',
          //     oldValue: this.objResultCompareBackup,
          //     newValue: codeForm.generateRuleVO.objResultCompare,
          //   })
          // }
          // if (changes.length > 0) {
          //   let logData = {
          //     ...this.logRecord,
          //     parentId: this.ruleGroupId,
          //     changeDetails: changes,
          //     batchId: this.batchId,
          //   }

          //   logChange({ ...logData })
          //     .then((res) => {
          //       if (res.code == 200) {
          //         this.submit(codeForm)
          //       }
          //     })
          //     .catch()
          // } else {
          //   this.submit(codeForm)
          // }

          this.submit(codeForm)
        })
      }
    },
    compareObjects(obj1, obj2, fields = null) {
      const differences = []

      // 如果没有指定字段，则比较所有字段
      const fieldsToCompare = fields || [
        ...new Set([...Object.keys(obj1 || {}), ...Object.keys(obj2 || {})]),
      ]

      fieldsToCompare.forEach((field) => {
        if (field === 'generateRuleVO') {
          return
        }
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
            targetId: this.info.id || null,
            targetType: 'RULE',
            fieldName: field,
            fieldDisplayName: this.getFieldLabel(field),
            oldValue: value1 || null,
            newValue: value2 || null,
          })
        }
      })

      return differences
    },
    getFieldLabel(field) {
      const fieldMap = {
        code: '规则code',
        level: '规则等级',
        content: '决策说明',
        remark: '备注',
        stronglyReject: '是否强拒',
        transferToPerson: '是否转人工',
        quatoRate: '配额',
        DT_operator: '数据总数运算符', //数据总数运算符
        DT_result: '数据总数值', //数据总数值
      }
      return fieldMap[field] || field
    },
    deepEqual(a, b) {
      // 1) 数组
      if (Array.isArray(a) && Array.isArray(b)) {
        if (a.length !== b.length) return false
        const used = new Array(b.length).fill(false)
        return a.every((itemA) => {
          for (let i = 0; i < b.length; i++) {
            if (!used[i] && this.deepEqual(itemA, b[i])) {
              used[i] = true
              return true
            }
          }
          return false // itemA 找不到匹配
        })
      }
      if (Array.isArray(a) || Array.isArray(b)) return false

      // 2) 对象
      if (a && b && typeof a === 'object' && typeof b === 'object') {
        const keysA = Object.keys(a)
        const keysB = Object.keys(b)
        if (keysA.length !== keysB.length) return false
        for (const k of keysA) {
          if (!keysB.includes(k)) return false
          if (!this.deepEqual(a[k], b[k])) return false
        }
        return true
      }

      // 3) 基本类型，统一字符串比较
      return String(a) === String(b)
    },

    submit(codeForm) {
      // console.log({
      //   ...codeForm,
      //   ...this.dataRisk.decision,
      //   purposeCategory: codeForm.code,
      //   modelId: this.tacticsId,
      //   groupId: this.ruleGroupId,
      //   versionControl: this.versionControl,
      //   recordId: this.info.id,
      // })
      // return
      addCodeSubmit({
        ...codeForm,
        ...this.dataRisk.decision,
        purposeCategory: codeForm.code,
        modelId: this.tacticsId,
        groupId: this.ruleGroupId,
        versionControl: this.versionControl,
        recordId: this.info.id,
      })
        .then((res) => {
          if (res.code == 200) {
            this.loading = false
            this.$message.success('操作成功')
            // this.handleCheckLock() 暂时注释 20260105
            this.$emit('success', 2)
            this.reset()
          }
        })
        .catch((err) => {
          this.loading = false
          this.$message.error('操作失败')
        })
    },
    handleClose() {
      this.reset()
      this.$emit('close')
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
    reset() {
      this.codeForm = this.$options.data().codeForm
      this.logicGroupArray = [
        {
          conditionArray: [
            {
              condition: [
                {
                  selectObj: [],
                  operator: null,
                  result: null,
                  rules: this.objRules,
                },
              ],
            },
          ],
        },
      ]

      if (this.$refs.codeForm) this.$refs.codeForm.resetFields()
      if (this.$refs['condition0'] && this.$refs['condition0'].length > 0) {
        this.$refs['condition0'][0].resetFields()
      }
    },
  },
}
</script>

<style lang="less" scoped>
.addRule {
  .add,
  .edit {
    height: 100%;
    padding: 0px 20px 20px;
    box-sizing: border-box;
  }

  .edit {
    .search {
      margin: 5px 0px 10px 0px;

      :deep .el-input__inner,
      :deep input {
        background: #f5f5f5;
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

      ::v-deep .el-form-item {
        width: 35%;
        margin-bottom: 0px;
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
        height: 48px;
      }
    }
  }

  .tips {
    color: red;
    font-size: 12px;
    // font-family: "PingFang SC-Medium", PingFang SC;
  }

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
      width: 84px;
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
}

::v-deep .el-tabs {
  .el-tabs__nav {
    transform: translateX(20px) !important;

    .el-tabs__active-bar {
      height: 3px;
    }
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

::v-deep(.longLabel) {
  .el-form-item__label {
    line-height: 20px !important;
  }
}

.noDisabledColor {
  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    background-color: rgba(#000, 0.04) !important;
    color: rgba(#000, 0.85);
  }

  ::v-deep .el-radio__input.is-disabled.is-checked .el-radio__inner {
    background-color: #409eff;
    border-color: #409eff;

    &::after {
      background-color: #ffffff;
    }
  }

  ::v-deep .el-radio__input.is-disabled.is-checked + span.el-radio__label {
    color: #409eff;
  }
}
.salience-desc {
  height: 48px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: rgb(191, 191, 24);
  padding-left: 20px;
}
.logicGroupFormItem {
  ::v-deep .el-form-item__content {
    display: flex;
    flex-direction: column;
    padding: 10px;
    border: 1px solid #e4e6ef;
    border-radius: 6px;
    line-height: 32px !important;
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
