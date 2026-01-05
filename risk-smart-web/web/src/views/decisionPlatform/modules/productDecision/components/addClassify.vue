<template>
  <div class="addClassify">
    <div v-loading="loading">
      <el-form
        ref="codeForm"
        :model="codeForm"
        label-width="100px"
        :rules="rules"
      >
        <el-row>
          <el-col :span="24">
            <el-form-item label="规则名称" prop="code">
              <el-input
                v-model="codeForm.code"
                @input="(e) => (codeForm.code = e.replace(/\s*/g, ''))"
                placeholder="请输入"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="规则等级" prop="level">
              <el-input v-model="codeForm.level" placeholder="请输入" />
              <span class="tips">规则等级范围为0~5，数字越小等级越高。</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="执行优先级" prop="salience">
              <el-input
                v-model="codeForm.salience"
                placeholder="请输入执行优先级"
                clearable
                @clear="codeForm.salience = 0"
              /><span class="tips"
                >规则优先级，值越高的规则，优先级越高，越先执行</span
              >
            </el-form-item>
          </el-col>
          <!--					<el-col :span="24">
                      <el-form-item
                        label="流水类型"
                        prop='level'
                      >
                        <el-select
                          v-model="codeForm.categoryType"
                          placeholder="请选择流水类型"
                        >
                          <el-option
                            v-for="(item, index) in natureList"
                            :label="item.dictLabel"
                            :value="item.dictSort + ''"
                            :key="index"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </el-col>-->
          <el-col :span="24">
            <el-form-item label="规则描述" prop="content">
              <el-input
                type="textarea"
                v-model.trim="codeForm.content"
                placeholder="请输入说明"
                :rows="5"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item
              required
              :label="`规则逻辑${idx + 1}`"
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

                  <el-col :span="18" class="select">
                    <el-cascader
                      v-model="obj.selectObj"
                      placeholder="选择决策条件"
                      :options="options"
                      :props="{ checkStrictly: true }"
                      filterable
                    />
                    <el-select v-model="obj.operator" placeholder="逻辑运算符">
                      <el-option
                        v-for="item in conditionList"
                        :label="item.label"
                        :value="item.value"
                        :key="item.id"
                      />
                    </el-select>
                    <el-input
                      v-model.trim="obj.result"
                      placeholder="请输入值"
                    />
                  </el-col>
                  <el-col :span="4.5" class="operation">
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
          </el-col>
          <el-col :span="24">
            <el-form-item label="">
              <!-- <span class="tips">为确保流水分类规则生效，请手动设置“收入金额大于0”或“支出金额大于0”条件。</span> -->
              <div class="addBtn" @click="addTemplate">
                <span><i class="el-icon-plus" /> 新增并且判断条件</span>
              </div>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="24">
            <el-form-item
              label="备注"
              prop="remark"
            >
              <el-input
                type="textarea"
                :rows='5'
                v-model="codeForm.remark"
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-col> -->
          <el-col :span="24" class="dataTotal">
            <el-form-item label="数据总数">
              <el-select
                v-model="codeForm.DT_operator"
                placeholder="请选择"
                clearable
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
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="btnBottom" v-if="info && info.type != 0">
        <el-button type="primary" @click="submit">确定 </el-button>
        <el-button @click="handleClose">取消</el-button>
      </div>
    </div>
    <!--		<confirmDialog
          ref="confirmDialog"
          :disabled="fullscreenLoading"
          @update="update"
          @reserved="reserved"
        >
          <div>是否需要生成新的版本？</div>
        </confirmDialog>-->
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getDicts } from '../api/index'
import {
  addCodeSubmit,
  getSelectListTree,
  searchAddCode,
  versionControlFraud,
  versionReserveFraud,
} from '../api/riskModel'
/*import confirmDialog from '../components/confirmDialog.vue'*/

export default {
  name: 'addClassify',
  components: {
    /*confirmDialog*/
  },
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
    activeRow: {
      type: Object,
      default: () => {},
    },
    versionControl: {
      type: String,
      default: '',
    },
    tactics: {
      type: Object,
      default: () => {},
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
        riskDescription: null,
        sortNum: null,
        startRate: null,
        endRate: null,
        status: null,
        term: null,
        termPackage: null,
        termKey: null,
        termValue: null,
        termOther: null,
        termRule: null,
        remark: null,
        updateBy: null,
        createBy: null,
        updateTime: null,
        createTime: null,
        termArray: [],
        propertyArray: [],
        methodArray: [],
        generateRuleVO: { arraySize: 0, conditionArray: [] },
        categoryType: null,
        DT_operator: null, //数据总数运算符
        DT_result: null, //数据总数值
        salience: 0,
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
          { required: true, message: '分类名称不能为空', trigger: 'blur' },
        ],
        level: [
          { required: true, message: '决策等级不能为空', trigger: 'blur' },
          {
            pattern: /^[0-5]$/,
            message: '决策等级只能输入0-5的数字',
            trigger: ['blur', 'change'],
          },
        ],
        DT_result: [
          {
            pattern: /^(-)?\d+$/,
            message: '请输入数字',
            trigger: ['blur', 'change'],
          },
        ],
        content: [
          { required: true, message: '分类描述不能为空', trigger: 'blur' },
        ],
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
      cmidx: 1,
      //条件对象
      conditionArray: [
        {
          id: 1,
          condition: [{ id: 1, selectObj: [], operator: null, result: null }],
        },
      ],
      //更新对象
      modifyArray: [{ id: 1 }],
      //分析对象
      options: [],
      //更新对象
      modifyOptions: [],
      //选择框选项
      propertyArray: [
        {
          keyCode: undefined,
          keyValue: undefined,
          descr: undefined,
          type: undefined,
          remark: undefined,
        },
      ],
      loading: false,
      natureList: [],
      projectCode: null,
      ruleCode: null,
    }
  },
  computed: {
    ...mapState(['dataRisk']),
    mapProObj() {
      let findObj = this.dataRisk.productList.find(
        (item) => item.id === this.dataRisk.decision.projectCode
      )
      return findObj
    },
  },
  watch: {
    // 'dataRisk.config': {
    //   handler(val) {
    //     this.projectCode = val.projectCode
    //     this.ruleCode = val.ruleCode
    //   }, deep: true, immediate: true
    // },
    info: {
      handler(val) {
        //type 0:查看   1:复制   null:编辑
        this.reset()
        if (val && val.id) {
          this.getDetailCode()
        }
        //查询分析主体
        this.getTheme()
      },
      immediate: true,
      deep: true,
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    mapType(customerType) {
      // let type = null
      // switch (customerType) {
      // 	case 0:
      // 		type = 'C、P'
      // 		break
      // 	case 1:
      // 		type = 'P'
      // 		break
      // 	case 2:
      // 		type = 'C'
      // 		break
      // }
      return 'C'
    },
    /*// 获取更新||保留版本参数
			 getqueryData() {
			 return {
			 modelId: this.tactics.activeId,
			 ...this.dataRisk.decision,
			 modelName: this.tactics.dataList.find(
			 (item) => item.id === this.tactics.activeId
			 ).name,
			 projectName: this.mapProObj.productName,
			 businessName: this.tabs[this.dataRisk.decision.businessCode],
			 personOrCompany: this.mapType(this.mapProObj.customerType),
			 championVersion: this.tactics.dataList.find(
			 (item) => item.id === this.tactics.activeId
			 ).versionObj.championVersion,
			 versionControl: this.versionControl
			 }
			 },
			 // 更新版本
			 update() {
			 this.fullscreenLoading = true
			 let promiseChain = Promise.resolve()
			 promiseChain
			 .then(() => {
			 return this.submitComfirm()
			 })
			 .then(() => {
			 versionControlFraud(this.getqueryData())
			 .then((res) => {
			 if (res.code == 200) {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 this.$message.success('操作成功')
			 this.$emit('resetStep')
			 this.handleClose()
			 }
			 })
			 .catch(() => {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 })
			 })
			 },
			 // 保留原版本
			 reserved() {
			 this.fullscreenLoading = true
			 let promiseChain = Promise.resolve()
			 promiseChain
			 .then(() => {
			 return this.submitComfirm()
			 })
			 .then(() => {
			 versionReserveFraud({ ...this.getqueryData() })
			 .then((res) => {
			 if (res.code == 200) {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 this.$message.success('操作成功')
			 this.$emit('resetStep')
			 this.handleClose()
			 }
			 })
			 .catch(() => {
			 this.$refs.confirmDialog.visible = false
			 this.fullscreenLoading = false
			 })
			 })
			 },*/
    init() {
      getDicts('fluid_property')
        .then((res) => {
          if (res.code == 200) {
            this.natureList = res.data
          }
        })
        .catch((err) => {})
    },
    getDetailCode() {
      this.reset()
      searchAddCode({ id: this.info.codeId })
        .then((res) => {
          this.codeForm = res.data
          if (!this.codeForm.generateRuleVO)
            this.codeForm.generateRuleVO = { arraySize: 0, conditionArray: [] }
          let data = res.data.conditionsJSON
          data.forEach((item) => {
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
          this.conditionArray = data.filter((item) => !item.clear)
        })
        .catch((err) => {})
    },
    getTheme() {
      let list = []
      let uploadForm = {
        packageType: 0,
        dataStatus: 0,
        ids: null,
      }
      if (this.info && this.info.objectList) {
        this.info.objectList.forEach((item) => {
          list.push(item.themeId)
        })
        uploadForm.ids = list.join(',')
      }

      this.loading = true
      getSelectListTree()
        .then((res) => {
          let data = res.data
          let list = []
          for (let key in data) {
            data[key].forEach((item) => {
              item.type = key
              list.push(item)
            })
          }
          this.options = list
          this.loading = false
        })
        .catch((err) => {
          this.loading = false
        })
    },
    /*async submitComfirm() {
				return addCodeSubmit({
					...this.codeForm,
					...this.dataRisk.decision,
					purposeCategory: this.codeForm.code,
					modelId: this.tacticsId,
					groupId: this.ruleGroupId,
					versionControl: this.versionControl
				})
					.then((res) => {
						if (res.code == 200) {
							this.loading = false
							// this.$message.success('操作成功')
							// this.$emit('success')
							// this.$emit('close')
						}
					})
					.catch((err) => {
						this.loading = false
						this.$message.error('操作失败')
					})
			},*/
    submit() {
      this.$refs.codeForm.validate((valid) => {
        if (valid) {
          // if (this.ruleCode == 2) {
          //   this.handleClose()
          //   return
          // }
          let that = this
          let isIF = false
          that.conditionArray.forEach((item) => {
            if (!item.condition) {
              this.$message({ message: '条件判断不能为空', type: 'warning' })
              isIF = false
              return
            }
            if (item.condition && item.condition.length) {
              item.condition.forEach((childEle) => {
                if (
                  !childEle.selectObj ||
                  (childEle.selectObj && childEle.selectObj.length === 0) ||
                  !childEle.operator ||
                  !childEle.result
                ) {
                  if (
                    document.getElementsByClassName('el-message').length === 0
                  )
                    this.$message({
                      message: '条件判断不能为空',
                      type: 'warning',
                    })
                  isIF = false
                } else {
                  isIF = true
                }
              })
            }
          })
          if (isIF) {
            this.loading = true
            // this.codeForm.conditionArray = this.conditionArray;
            let conditionArray = JSON.parse(JSON.stringify(this.conditionArray))
            conditionArray.forEach((item, index) => {
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
                if (length > 1)
                  secondLeaf = firstLeaf.children.find(
                    (itemChild) => itemChild.value == itemX.selectObj[1]
                  )
                if (length > 2)
                  thirdLeaf = secondLeaf.children.find(
                    (itemChild) => itemChild.value == itemX.selectObj[2]
                  )

                let obj = {
                  manageNo: null,
                  interfaceNo: null,
                  sourceNo: null,
                  objectFlag: secondLeaf ? secondLeaf.objectFlag : null,
                  stats: thirdLeaf ? thirdLeaf.code : secondLeaf.code,
                  dataType: thirdLeaf
                    ? thirdLeaf.dataType
                    : secondLeaf.dataType,
                }
                if (
                  secondLeaf &&
                  secondLeaf.interface &&
                  secondLeaf.interface.length
                ) {
                  obj.manageNo = secondLeaf.interface[0].manageNo
                  obj.interfaceNo = secondLeaf.interface[0].interfaceNo
                  obj.sourceNo = secondLeaf.interface[0].sourceNo
                  obj.objectName = secondLeaf.interface[0].interfaceNo
                  obj.anotherName = secondLeaf.interface[0].interfaceNo
                }
                let result = {
                  result: itemX.result,
                  resultType: thirdLeaf ? thirdLeaf.resultType : null,
                }
                itemX.result = result
                itemX.selectObj = obj
                itemX.drlList = secondLeaf ? secondLeaf.drlList : null
              })
            })

            if (this.codeForm.DT_operator && this.codeForm.DT_result) {
              this.codeForm.generateRuleVO.arraySize = 1
              let DT_obj = {
                id: conditionArray.length + 1,
                condition: [
                  {
                    id: 1,
                    operator: this.codeForm.DT_operator,
                    result: {
                      result: this.codeForm.DT_result,
                      resultType: this.codeForm.DT_result,
                    },
                    selectObj: {},
                    objectLevel: [],
                    arraySize: 1,
                  },
                ],
              }
              conditionArray.push(DT_obj)
            }

            this.codeForm.generateRuleVO.conditionArray = conditionArray
            this.codeForm.type = 1
            this.codeForm.content.replace('>', '&gt;')
            this.codeForm.content.replace('<', '&lt;')
            /*this.$refs.confirmDialog.visible = true*/
            addCodeSubmit({
              ...this.codeForm,
              ...this.dataRisk.decision,
              purposeCategory: this.codeForm.code,
              modelId: this.tacticsId,
              groupId: this.ruleGroupId,
              versionControl: this.versionControl,
              recordId: this.info.id,
            })
              .then((res) => {
                if (res.code == 200) {
                  this.loading = false
                  this.$message.success('操作成功')
                  this.$emit('resetStep')
                  this.reset()
                  this.$emit('success', 2)
                }
              })
              .catch((err) => {
                this.loading = false
                this.$message.error('操作失败')
              })
          }
        }
      })
    },
    handleClose() {
      this.reset()
      this.$emit('close')
    },
    //子项增加
    addNodeTemplate(pid) {
      this.coidx = this.coidx + 1
      this.conditionArray.some((item, i) => {
        if (item.id == pid) {
          let newobj = { id: this.coidx }
          this.conditionArray[i].condition.push(newobj)
          // 在数组的some方法中，如果return true，就会立即终止这个数组的后续循环,所以相比较foreach，如果想要终止循环，那么建议使用some
          return true
        }
      })
    },
    //子项删除
    delNodeTemplate(pid, nid) {
      this.conditionArray.some((item, i) => {
        if (item.id == pid) {
          this.conditionArray[i].condition.some((nitem, n) => {
            if (nitem.id == nid) {
              this.conditionArray[i].condition.splice(n, 1)
              // 在数组的some方法中，如果return true，就会立即终止这个数组的后续循环,所以相比较foreach，如果想要终止循环，那么建议使用some
              return true
            }
          })
          if (this.conditionArray[i].condition.length == 0) {
            this.conditionArray.splice(i, 1)
          }
          // 在数组的some方法中，如果return true，就会立即终止这个数组的后续循环,所以相比较foreach，如果想要终止循环，那么建议使用some
          return true
        }
      })
    },
    //增加模板
    addTemplate() {
      this.cidx = this.cidx + 1
      this.coidx = this.coidx + 1
      let selectO = this.conditionArray[0].condition[0].selectObj
      console.log(selectO)
      let newobj = {
        id: this.cidx,
        condition: [{ id: this.coidx, selectObj: selectO }],
      }
      this.conditionArray.push(newobj)
    },
    reset() {
      this.codeForm = this.$options.data().codeForm
      this.conditionArray = [
        {
          id: 1,
          condition: [{ id: 1, selectObj: [], operator: null, result: null }],
        },
      ]
      this.modifyArray = [{ id: 1 }]
      this.propertyArray = [
        {
          keyCode: undefined,
          keyValue: undefined,
          descr: undefined,
          type: undefined,
          remark: undefined,
        },
      ]

      if (this.$refs.codeForm) {
        this.$refs.codeForm.resetFields()
      }
    },
  },
}
</script>

<style lang="less" scoped>
.addClassify {
  > div {
    position: relative;
    padding: 0px 20px 20px 20px;
    height: 100%;
  }

  .condition {
    display: flex;
    font-size: 14px;
    margin-bottom: 5px;
    align-items: center;

    .title {
      min-width: 42px;
      color: rgba(#000, 0.6);
    }

    .select {
      display: flex;
      align-items: center;
      justify-content: space-around;
      padding-right: 10px;

      /deep/ .el-cascader,
      .el-select,
      .el-input {
        width: 35%;
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
      height: 44px;
      border-radius: 6px;
    }

    > button:nth-of-type(1) {
      width: 100px;
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
</style>
