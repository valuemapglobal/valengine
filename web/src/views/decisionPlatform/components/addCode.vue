<template>
  <div class="addCode">
    <div v-loading="loading">
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

                  <el-col :span="18" class="select">
                    <el-cascader
                      v-model="obj.selectObj"
                      placeholder="选择决策条件"
                      :options="options"
                      :props="{ checkStrictly: true }"
                      filterable
                    />
                    <el-select
                      v-model="obj.operator"
                      placeholder="逻辑运算符"
                      :popper-append-to-body="false"
                    >
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
                      type="success"
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
  </div>
</template>

<script>
import {
  searchAddCode,
  getSelectListTree,
  addCodeSubmit,
} from '@/api/dataRisk/riskModel.js'

export default {
  props: {
    info: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
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
      },
      conditionList: [
        { value: '==', label: '等于' },
        { value: '!=', label: '不等于' },
        { value: '>', label: '大于' },
        { value: '<', label: '小于' },
        { value: '>=', label: '大于等于' },
        { value: '<=', label: '小于等于' },
        { value: 'contains', label: '包含' },
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
    }
  },
  watch: {
    info: {
      handler(val) {
        //type 0:查看   1:复制   null:编辑
        if (val && val.id) {
          this.getDetailCode()
        } else {
          this.reset()
        }
        //查询分析主体
        this.getTheme()
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {
    getDetailCode() {
      searchAddCode({ id: this.info.id })
        .then((res) => {
          this.codeForm = res.data
          if (res.data.conditionArray.length > 0) {
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
      this.$refs.codeForm.validate((valid) => {
        if (valid) {
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
            addCodeSubmit({ ...this.codeForm })
              .then((res) => {
                if (res.code == 200) {
                  this.$message.success('操作成功')
                  this.loading = false
                  this.$emit('success')
                  this.$emit('close')
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
      let newobj = {
        id: this.cidx,
        condition: [{ id: this.coidx, selectObj: selectO }],
      }
      this.conditionArray.push(newobj)
    },
    reset() {
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
    },
  },
}
</script>

<style lang="less" scoped>
.addCode {
  .condition {
    display: flex;
    font-size: 14px;
    margin-bottom: 5px;
    align-content: center;

    .title {
      min-width: 42px;
      color: rgba(#000, 0.6);
    }

    .select {
      display: flex;
      align-items: center;
      justify-content: space-around;
      padding-right: 10px;

      /deep/ .el-input,
      .el-select,
      .el-cascader {
        // width: 195px;
        width: 96%;
      }
    }

    .operation {
      /deep/ .el-button {
        height: 32px;
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
}
</style>
