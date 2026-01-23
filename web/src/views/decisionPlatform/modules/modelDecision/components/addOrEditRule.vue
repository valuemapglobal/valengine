<template>
  <div class="addRule">
    <div class="add" v-loading="loading" v-if="activeName === 'add'">
      <el-form
        ref="codeForm"
        :model="codeForm"
        label-width="90px"
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
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="决策等级" prop="level">
              <el-input v-model="codeForm.level" placeholder="请输入等级" />
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
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
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
                        />
                      </el-form-item>
                      <el-form-item prop="operator">
                        <el-select
                          v-model="obj.operator"
                          placeholder="逻辑运算符"
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
                      /></el-form-item>
                    </el-form>
                  </el-col>
                  <el-col :span="4.5" class="operation">
                    <el-button
                      type="primary"
                      icon="el-icon-plus"
                      size="mini"
                      @click="addNodeTemplate(item.id)"
                    >
                      添加
                    </el-button>
                    <el-button
                      type="danger"
                      v-if="idx > 0 || (idx == 0 && ix > 0)"
                      icon="el-icon-close"
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
              <div class="addBtn" @click="addTemplate">
                <span>新增判断条件</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                type="textarea"
                :rows="5"
                v-model="codeForm.remark"
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="btnBottom" v-if="info && info.type != 0">
        <el-button type="primary" @click="submit">确定</el-button>
        <el-button @click="handleClose">取消</el-button>
      </div>
    </div>
    <div class="edit" v-else-if="activeName === 'edit'">
      <Search
        class="search"
        :list="editSearchList"
        :onlySearch="true"
        ref="search"
        @search="handleSelectCode"
        dark
      />
      <addCodeRule
        class="addCodeRule"
        :tableConfig="enterpriseTable"
        :selectTableConfig="selectTable"
        @turnPage="handleSelectCodeCurrent"
        @turnSize="handleSelectCodeSize"
      />
      <div class="btnBottom" v-if="status">
        <el-button type="primary" @click="submitSelectCode">确定</el-button>
        <el-button @click="handleClose">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import {
  searchAddCode,
  getSelectListTree,
  addCodeSubmit,
  searchSelectCodeList,
  searchObjectList,
  searchSelectSubmit,
  checkCode,
} from '@/api/dataRisk/riskModel.js'
import Search from '@/components/pageSearch.vue'
import addCodeRule from './addCodeRule.vue'
import { mapState } from 'vuex'

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
      default: [],
    },
    status: {
      type: Boolean,
      default: true,
    },
  },
  components: {
    Search,
    addCodeRule,
  },
  data() {
    return {
      activeName: 'add',
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
      },
      rules: {
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
            pattern: /^[0-9]*$/,
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
      },
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
        { value: 'date day', label: '和当前日期相差小于天' },
        { value: 'date month', label: '和当前日期相差小于月' },
        { value: 'date year', label: '和当前日期相差小于年' },
        { value: 'date big day', label: '和当前日期相差大于天' },
        { value: 'date big month', label: '和当前日期相差大于月' },
        { value: 'date big year', label: '和当前日期相差大于年' },
      ],
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
      ruleCode: null,
    }
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  watch: {
    'dataRisk.config': {
      handler(val) {
        this.projectCode = val.projectCode
        this.ruleCode = val.ruleCode
      },
      deep: true,
      immediate: true,
    },
    info: {
      handler(val) {
        //查询分析主体
        this.getTheme()
        if (val && val.id) {
          this.getDetailCode()
        } else {
          this.reset()
        }
      },
      immediate: true,
      deep: true,
    },
    tacticsId: {
      handler(val) {
        this.getAnalysisObject(val)
        this.getSelectCodeList()
      },
      deep: true,
      immediate: true,
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
  },
  // created() {
  //   this.getAnalysisObject(this.tacticsId)
  // },
  methods: {
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
    checkNumber: (rule, value, callback) => {
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
      let data = dataList.find((item) => item.id == parseInt(valueList[0]))
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
        checkCode({ code: val })
          .then((res) => {
            if (res.code == 200) {
              if (res.data) {
                this.rules.code.push({
                  validator: this.checkCodeNo,
                  trigger: 'blur',
                })
              } else {
                // this.rules.code.pop()
                if (this.rules.length >= 2) {
                  this.rules.code.slice(0, 2)
                }
              }
              this.$refs.codeForm.validateField('code', () => {})
            }
          })
          .catch((err) => {})
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
    getAnalysisObject(id) {
      let search = {
        pageNum: 1,
        pageSize: 999,
        modelId: id,
      }
      searchObjectList(search)
        .then((res) => {
          if (res.code == 200) {
            this.ObjectIdList = []
            res.rows.forEach((item) => {
              this.ObjectIdList.push(item.themeId)
            })
            this.objectList = res.rows
          }
        })
        .catch((err) => {})
    },
    //提交规则选项code
    submitSelectCode() {
      let uploadList = this.selectTable.dataList
      let list = []
      if (uploadList.length > 0) {
        uploadList.forEach((item, index) => {
          let uploadForm = {
            code: item.code,
            codeId: item.codeId ? item.codeId : item.id,
            name: item.name,
            descr: item.riskDescription,
            modelId: this.tacticsId,
            groupId: this.ruleGroupId,
            projectCode: 1001,
          }
          list.push(uploadForm)
        })
        searchSelectSubmit({ rdeModelAntiFraudRuleRecords: list })
          .then((res) => {
            this.$message.success('操作成功')
            setTimeout(() => {
              this.$emit('resetStep')
              this.handleClose()
            }, 1000)
          })
          .catch((err) => {})
        // this.handleCloseSelectCode()
      } else {
        this.$message.warning('请先选择规则code')
      }
    },

    getDetailCode() {
      this.reset()
      searchAddCode({ id: this.info.codeId })
        .then((res) => {
          this.codeForm = res.data
          if (res.data.conditionArray.length > 0) {
            // let list = res.data.conditionArray
            // list.forEach((item) => {
            //   item.condition.forEach((itemX, indexX) => {
            //     console.log(itemX, 'itemX');
            //     this.handleChange(itemX.selectObj, item, indexX)
            //   })
            // })
            this.conditionArray = res.data.conditionArray
            this.cidx = res.data.conditionArray.length
            this.coidx = 10
          }
          if (res.data.modifyArray.length > 0) {
            this.modifyArray = res.data.modifyArray
            this.cmidx = res.data.modifyArray.length
          }
          if (res.data.propertyArray.length > 0) {
            this.propertyArray = res.data.propertyArray
          }
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
      getSelectListTree({ ...uploadForm })
        .then((res) => {
          this.options = res.data
          this.loading = false
        })
        .catch((err) => {
          this.loading = false
        })
    },
    submit() {
      let isForm = false
      let isIF = false
      this.$refs.codeForm.validate((valid) => {
        isForm = valid
      })
      this.conditionArray.forEach((item) => {
        if (item.condition && item.condition.length) {
          item.condition.forEach((itemX, index) => {
            this.$refs[`condition${index}`][0].validate((valid) => {
              isIF = valid
            })
          })
        }
      })
      if (isIF && isForm) {
        this.loading = true
        this.codeForm.conditionArray = this.conditionArray
        if (this.modifyArray[0].selectObj) {
          this.codeForm.modifyArray = this.modifyArray
        }
        if (this.propertyArray[0].keyCode) {
          this.codeForm.propertyArray = this.propertyArray
        }
        //复制
        if (this.info && this.info.type == 1) {
          this.codeForm.id = null
        }
        this.codeForm.type = 1
        addCodeSubmit({
          ...this.codeForm,
          projectCode: this.projectCode,
          purposeCategory: this.codeForm.code,
          ruleCode: this.ruleCode,
          modelId: this.tacticsId,
          groupId: this.ruleGroupId,
        })
          .then((res) => {
            if (res.code == 200) {
              this.$message.success('操作成功')
              this.loading = false
              this.$emit('success')
              this.$emit('resetStep')
              this.$emit('close')
            }

            // if (this.codeForm.code) {
            //   //数据关联
            //   let form = {
            //     code: this.codeForm.code,
            //     codeId: res.data,
            //     descr: this.codeForm.content,
            //     modelId: this.tacticsId,
            //     groupId: this.ruleGroupId,
            //     projectCode: 1001
            //   }
            //   searchSelectSubmit({ rdeModelAntiFraudRuleRecords: [form] }).then((res) => { }).catch((err) => { });
            // }
          })
          .catch((err) => {
            this.loading = false
            this.$message.error('操作失败')
          })
      }
    },
    handleClose() {
      this.reset()
      this.$emit('close', 3)
    },
    //子项增加
    addNodeTemplate(pid) {
      this.coidx = this.coidx + 1
      this.conditionArray.some((item, i) => {
        if (item.id == pid) {
          let newobj = {
            id: this.coidx,
            result: '',
            operator: null,
            selectObj: [],
            rules: this.objRules,
          }
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
      let newobj = {
        id: this.cidx,
        condition: [
          {
            id: 1,
            selectObj: selectO,
            operator: null,
            result: null,
            rules: this.objRules,
          },
        ],
      }
      this.conditionArray.push(newobj)
    },
    reset() {
      this.activeName = 'add'
      this.codeForm = {
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
      }
      this.conditionArray = [
        {
          id: 1,
          condition: [
            {
              id: 1,
              selectObj: [],
              operator: null,
              result: null,
              rules: this.objRules,
            },
          ],
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
    padding: 0px 20px;
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

      /deep/.el-form-item {
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

  /deep/.el-form {
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
/deep/.el-tabs {
  .el-tabs__nav {
    transform: translateX(20px) !important;

    .el-tabs__active-bar {
      height: 3px;
    }
  }
}
</style>
