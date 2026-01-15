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
      :label-width="
        grade == 3
          ? isEnglish()
            ? '240px'
            : '160px'
          : isEnglish()
          ? '180px'
          : '110px'
      "
      class="demo-ruleForm"
    >
      <el-form-item
        :label="
          grade == 2
            ? $t('dataCenter.relateMetadata')
            : $t('dataCenter.relateMetadataOrFeatureVariable')
        "
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
      <el-form-item :label="$t('dataCenter.moduleName')" prop="name">
        <el-input
          v-model="ruleForm.name"
          @change="checkHandle($event, 'name')"
          :placeholder="$t('common.pleaseInput')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('dataCenter.moduleUniqueIdentifier')"
        prop="code"
      >
        <el-input
          v-model="ruleForm.code"
          @input="(e) => (ruleForm.code = e.replace(/\s*/g, ''))"
          @change="checkHandle($event, 'code')"
          :placeholder="$t('common.pleaseInput')"
        />
      </el-form-item>
      <el-form-item>
        <span slot="label">
          {{ $t('dataCenter.interfaceAddress') }}
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('dataCenter.interfaceAddressTip')"
            placement="top"
          >
            <i class="el-icon-question"></i>
          </el-tooltip>
        </span>
        <el-input
          :value="InterfaceAddress"
          :placeholder="$t('common.pleaseInput')"
          disabled
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('dataCenter.moduleType')" prop="type">
        <el-select
          v-model="ruleForm.type"
          :placeholder="$t('common.pleaseSelect')"
        >
          <el-option
            :label="$t('dataCenter.objectTypeValue')"
            :value="0"
          ></el-option>
          <el-option
            :label="$t('dataCenter.collection')"
            :value="1"
            disabled
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('dataCenter.dataClassification')"
        prop="dataType"
      >
        <el-radio-group v-model="ruleForm.dataType">
          <el-radio :label="0" border>{{
            $t('dataCenter.enterprise')
          }}</el-radio>
          <el-radio :label="1" border>{{
            $t('dataCenter.individual')
          }}</el-radio>
          <el-radio :label="2" border>{{
            $t('dataCenter.entityAsset')
          }}</el-radio>
          <el-radio :label="3" border>{{
            $t('dataCenter.supplyChain')
          }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="$t('common.remark')">
        <el-input
          v-model="ruleForm.remark"
          :placeholder="$t('common.pleaseInput')"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="ruleForm.id">
        <div slot="label">
          {{ $t('dataCenter.requestBodyDisplay') }}
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('dataCenter.requestBodyAutoGenerateTip')"
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
          <el-table-column
            prop="nameEn"
            :label="$t('dataCenter.parameterName')"
          ></el-table-column>
          <el-table-column
            prop="nameZh"
            :label="$t('dataCenter.parameterDescription')"
          ></el-table-column>
          <el-table-column
            :label="$t('dataCenter.parameterType')"
            align="center"
          >
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
      <el-button type="primary" :loading="loading" @click="onSubmit">{{
        $t('common.submit')
      }}</el-button>
      <el-button :loading="loading" @click="closeDrawer">{{
        $t('common.cancel')
      }}</el-button>
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
      rules: {},
      //请求体展示数据
      tableData: [],
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
    rules() {
      return {
        metadata: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseSelectRelateMetadata'),
            trigger: 'blur',
          },
        ],
        name: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseInputModuleName'),
            trigger: 'blur',
          },
        ],
        code: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseInputModuleUniqueIdentifier'),
            trigger: 'blur',
          },
        ],
        type: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseSelectModuleType'),
            trigger: 'blur',
          },
        ],
        dataType: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseSelectDataClassification'),
            trigger: 'blur',
          },
        ],
      }
    },
    typeOptions() {
      return {
        0: { type: 'primary', name: this.$t('dataCenter.number') },
        1: { type: 'success', name: this.$t('dataCenter.string') },
        2: { type: 'success', name: this.$t('dataCenter.date') },
        3: { type: 'primary', name: this.$t('dataCenter.objectTypeValue') },
        4: { type: 'primary', name: this.$t('dataCenter.array') },
        5: { type: 'success', name: this.$t('dataCenter.file') },
        6: { type: 'success', name: this.$t('dataCenter.boolean') },
        7: { type: 'primary', name: this.$t('dataCenter.decimal') },
      }
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
      console.log(this.rules, 'this.rules')

      if (Object.keys(this.rules).length) {
        if (this.rules.name.length >= 2) {
          this.rules.name.splice(1, 2)
        }
        if (this.rules.code.length >= 2) {
          this.rules.code.splice(1, 2)
        }
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
                validatorInner = this.$t('dataCenter.moduleNameDuplicate')
                break
              case 'code':
                API = featureModuleCheckCode
                Object.assign(params, {
                  code: this.ruleForm.code,
                })
                validatorInner = this.$t(
                  'dataCenter.moduleUniqueIdentifierDuplicate'
                )
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
                validatorInner = this.$t('dataCenter.moduleNameDuplicate')
                break
              case 'code':
                API = analysisModuleCheckCode
                Object.assign(params, {
                  code: this.ruleForm.code,
                })
                validatorInner = this.$t(
                  'dataCenter.moduleUniqueIdentifierDuplicate'
                )
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
