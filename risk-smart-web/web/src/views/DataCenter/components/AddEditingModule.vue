<template>
  <el-drawer
    size="40%"
    :visible.sync="drawer"
    :title="title"
    :before-close="closeDrawer"
  >
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      :label-width="grade == 3 ? '160px' : '110px'"
      class="demo-ruleForm"
    >
      <el-form-item
        :label="grade == 2 ? '关联元数据' : '关联元数据/特征变量'"
        v-if="
          grade != 1 &&
          ruleForm.id &&
          ruleForm.metadata &&
          ruleForm.metadata.length
        "
      >
        <el-cascader
          disabled
          v-model="ruleForm.metadata"
          :options="cascadeOptions"
          :props="cascadeProps"
          collapse-tags
          clearable
        />
      </el-form-item>
      <el-form-item label="模块名称" prop="name">
        <el-input
          v-model="ruleForm.name"
          @change="checkHandle($event, 'name')"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item label="模块唯一标识" prop="code">
        <el-input
          v-model="ruleForm.code"
          @input="(e) => (ruleForm.code = e.replace(/\s*/g, ''))"
          @change="checkHandle($event, 'code')"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item>
        <span slot="label">
          接口地址
          <el-tooltip
            class="item"
            effect="dark"
            content="通过此API地址访问此模块对应的数据"
            placement="top"
          >
            <i class="el-icon-question"></i>
          </el-tooltip>
        </span>
        <el-input
          :value="InterfaceAddress"
          placeholder="请输入"
          disabled
        ></el-input>
      </el-form-item>
      <el-form-item label="模块类型" prop="type">
        <el-select v-model="ruleForm.type" placeholder="请选择">
          <el-option label="对象" :value="0"></el-option>
          <el-option label="集合" :value="1" disabled></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="数据分类" prop="dataType">
        <el-radio-group v-model="ruleForm.dataType">
          <el-radio :label="0" border>企业</el-radio>
          <el-radio :label="1" border>个体</el-radio>
          <el-radio :label="2" border>实体资产</el-radio>
          <el-radio :label="3" border>供应链</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="ruleForm.remark" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item v-if="ruleForm.id">
        <div slot="label">
          请求体展示
          <el-tooltip
            class="item"
            effect="dark"
            content="关联元数据时自动生成"
            placement="top"
          >
            <svg
              t="1744943482706"
              class="icon"
              viewBox="0 0 1024 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="4673"
              width="10"
              height="10"
            >
              <path
                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z m0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"
                p-id="4674"
                fill="#3D3D3D"
              ></path>
              <path
                d="M623.6 316.7C593.6 290.4 554 276 512 276s-81.6 14.5-111.6 40.7C369.2 344 352 380.7 352 420v7.6c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V420c0-44.1 43.1-80 96-80s96 35.9 96 80c0 31.1-22 59.6-56.1 72.7-21.2 8.1-39.2 22.3-52.1 40.9-13.1 19-19.9 41.8-19.9 64.9V620c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-22.7c0-19.7 12.4-37.7 30.9-44.8 59-22.7 97.1-74.7 97.1-132.5 0.1-39.3-17.1-76-48.3-103.3z"
                p-id="4675"
                fill="#3D3D3D"
              ></path>
              <path
                d="M512 732m-40 0a40 40 0 1 0 80 0 40 40 0 1 0-80 0Z"
                p-id="4676"
                fill="#3D3D3D"
              ></path>
            </svg>
          </el-tooltip>
        </div>
        <el-table
          :data="tableData"
          border
          height="240px"
          :header-cell-style="tableHeaderColor"
          :row-style="IntelligentExtensionRowStyle"
          style="width: 100%"
        >
          <el-table-column prop="nameEn" label="参数名称"></el-table-column>
          <el-table-column prop="nameZh" label="说明"></el-table-column>
          <el-table-column label="参数类型" align="center">
            <template slot-scope="{ row }">
              <el-tag
                :type="typeOptions[row.type].type"
                v-if="typeOptions[row.type]"
              >
                {{ typeOptions[row.type].name }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
    <div class="drawer-footer">
      <el-button type="primary" :loading="loading" @click="onSubmit"
        >提交</el-button
      >
      <el-button :loading="loading" @click="closeDrawer">取消</el-button>
    </div>
  </el-drawer>
</template>

<script>
import {
  featureModuleCheckCode,
  featureModuleCheckName,
  getInterfaceInputParameter,
} from '../api/featureVariable'
import {
  analysisModuleCheckName,
  analysisModuleCheckCode,
} from '../api/analysisTarget'
import { checkValueRepeat } from './index'
export default {
  name: 'AddEditingModule',
  props: {
    /**
     * 使用框架等级
     * 1：元数据
     * 2：特征变量
     * 3：分析指标
     */
    grade: { type: String | Number, default: 1 },
    title: { type: String },
    //级联options值
    cascadeOptions: {
      type: Array,
      default: () => [],
    },
    //级联props值
    cascadeProps: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      drawer: false,
      ruleForm: {
        id: undefined, //编辑时存在
        metadata: [], //关联元数据的接口编号
        name: '', //模块名称
        code: '', //模块唯一标识
        type: '', //模块类型：0-对象，1-集合
        dataType: '', //数据分类：0-企业，1-个人，2-实体资产，3-供应链
        remark: undefined,
      },
      rules: {
        metadata: [
          { required: true, message: '请选择关联元数据', trigger: 'blur' },
        ],
        name: [{ required: true, message: '请输入模块名称', trigger: 'blur' }],
        code: [
          { required: true, message: '请输入模块唯一标识', trigger: 'blur' },
        ],
        type: [{ required: true, message: '请选择模块类型', trigger: 'blur' }],
        dataType: [
          { required: true, message: '请选择数据分类', trigger: 'blur' },
        ],
      },
      //请求体展示数据
      tableData: [],
      //参数类型
      typeOptions: {
        0: { type: 'primary', name: '数值' },
        1: { type: 'success', name: '字符串' },
        2: { type: 'success', name: '日期' },
        3: { type: 'primary', name: '对象' },
        4: { type: 'primary', name: '数组' },
        5: { type: 'success', name: '文件' },
        6: { type: 'success', name: '布尔' },
        7: { type: 'primary', name: '小数' },
      },
      //提交按钮loading
      loading: false,
    }
  },
  computed: {
    InterfaceAddress() {
      return `localhost:8990/${
        this.grade == 2 ? 'feature' : 'metrics'
      }/entry-point/${this.ruleForm.code}`
    },
  },
  methods: {
    openDrawer(data) {
      if (data) {
        this.ruleForm = {
          id: data.id, //编辑时存在
          metadata: data.metadata, //关联元数据的接口编号
          name: data.name, //模块名称
          code: data.code, //模块唯一标识
          type: data.type, //模块类型：0-对象，1-集合
          dataType: data.dataType, //数据分类：0-企业，1-个人，2-实体资产，3-供应链
          remark: data.remark,
        }
        let manageNoList = []
        data.metadata.forEach((item) => {
          item.forEach((child, index) => {
            if (index !== 0) {
              manageNoList.push(child)
            }
          })
        })
        if (manageNoList && manageNoList.length) {
          getInterfaceInputParameter({
            manageNoList,
          }).then((res) => {
            if (res.code === 200) {
              this.tableData = res.data.map((item) => {
                return item.parameterInfo
              })
            }
          })
        }
      } else {
        this.ruleForm = {
          id: undefined, //编辑时存在
          metadata: [], //关联元数据的接口编号
          name: '', //模块名称
          code: '', //模块唯一标识
          type: '', //模块类型：0-对象，1-集合
          dataType: '', //数据分类：0-企业，1-个人，2-实体资产，3-供应链
          remark: undefined,
        }
      }
      this.drawer = true
    },
    closeDrawer(done) {
      if (this.rules.name.length >= 2) {
        this.rules.name.splice(1, 2)
      }
      if (this.rules.code.length >= 2) {
        this.rules.code.splice(1, 2)
      }
      this.$refs.ruleForm.clearValidate()
      if (typeof done === 'function') {
        done()
      } else {
        this.drawer = false
      }
    },
    /**
     * 判断模块名称&模块唯一标识是否
     * @param e
     * @param fields
     */
    checkHandle(e, fields) {
      console.log('this.ruleForm.id', this.ruleForm.id)
      let API,
        validatorInner,
        params = { id: this.ruleForm.id }
      if (e) {
        switch (this.grade) {
          case 2:
          case '2':
            switch (fields) {
              case 'name':
                API = featureModuleCheckName
                Object.assign(params, {
                  name: this.ruleForm.name,
                })
                validatorInner = '当前模块名称重复'
                break
              case 'code':
                API = featureModuleCheckCode
                Object.assign(params, {
                  code: this.ruleForm.code,
                })
                validatorInner = '当前模块唯一标识重复'
                break
            }
            break
          case 3:
          case '3':
            switch (fields) {
              case 'name':
                API = analysisModuleCheckName
                Object.assign(params, {
                  name: this.ruleForm.name,
                })
                validatorInner = '当前模块名称重复'
                break
              case 'code':
                API = analysisModuleCheckCode
                Object.assign(params, {
                  code: this.ruleForm.code,
                })
                validatorInner = '当前模块唯一标识重复'
                break
            }
            break
        }
        API(params).then((res) => {
          if (res.code === 200) {
            if (res.data) {
              this.rules[fields].push({
                validator: (rule, value, callback) =>
                  checkValueRepeat(rule, value, callback, validatorInner),
                trigger: 'blur',
              })
            } else {
              if (this.rules[fields].length >= 2) {
                this.rules[fields].splice(1, 2)
              }
            }
            this.$refs.ruleForm.validateField(fields, (valid) => {
              console.log('valid', valid)
            })
          }
        })
      }
    },
    onSubmit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.loading = true
          this.$emit('submit', this.ruleForm)
          this.drawer = false
          this.loading = false
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
// .drawer-header {
//   padding: 30px;
//   display: flex;
//   align-items: center;
//   justify-content: space-between;

//   &-text {
//     font-weight: 500;
//     font-size: 16px;
//     color: #3f4254;
//     line-height: 26px;
//   }

//   &-icon {
//     cursor: pointer;
//   }
// }

::v-deep .el-form {
  // padding: 0 30px;

  .el-form-item {
    &__label {
      padding-right: 10px;
      font-weight: 400;
      font-size: 14px;
      line-height: 38px;
    }

    .el-select,
    .el-cascader,
    .el-input,
    .is-disabled,
    .el-textarea {
      width: 100%;

      &__inner {
        height: 42px;
        color: var(--text-color-secondary);
        background: rgba(0, 0, 0, 0.04);
        border-radius: 4px 4px 4px 4px;
        // border: none;
      }

      .el-tag.el-tag--info {
        background: #ffffff;
        border-color: #e9e9eb;
      }

      .el-input .el-select__caret {
        font-weight: bold;
        color: #1e2135;
      }
    }

    .el-radio-group {
      .el-radio {
        align-items: center;
        display: inline-flex;
        height: 40px;
        padding: 0;
        margin-right: 0;
        border-radius: 6px;

        &.is-bordered + &.is-bordered {
          margin-left: 18px;
        }

        .el-radio__input {
          display: none;
        }

        .el-radio__label {
          padding: 0 14px;
          margin-left: 0;
        }
      }
    }
  }
}

.drawer-footer {
  padding: 50px 0px 0px;
  text-align: right;

  ::v-deep .el-button {
    padding: 0 32px;
    height: 42px;
    border-radius: 6px;
    font-weight: 400;
    font-size: 16px;

    &:hover {
      opacity: 0.8;
    }
  }

  .el-button--primary {
    background: var(--primary-color);
  }
}
</style>
