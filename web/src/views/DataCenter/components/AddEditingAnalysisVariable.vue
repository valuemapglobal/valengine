<template>
  <!--	分析指标--新增||编辑属性-->
  <el-drawer
    size="40%"
    :visible.sync="drawer"
    :title="title"
    :wrapperClosable="false"
    :before-close="closeDrawer"
  >
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      :label-width="isEnglish() ? '160px' : '140px'"
    >
      <el-form-item :label="$t('dataCenter.analysisTargetName')" prop="name">
        <el-input
          v-model="ruleForm.name"
          @change="checkHandle($event, 'name')"
          :placeholder="$t('common.pleaseInput')"
        />
      </el-form-item>
      <el-form-item :label="$t('dataCenter.paramName')" prop="code">
        <el-input
          v-model="ruleForm.code"
          @input="(e) => (ruleForm.code = e.replace(/\s*/g, ''))"
          @change="checkHandle($event, 'code')"
          :placeholder="$t('common.pleaseInput')"
        />
      </el-form-item>
      <el-form-item :label="$t('dataCenter.moduleName')">
        <el-input
          v-model="ruleForm.moduleName"
          :placeholder="$t('dataCenter.fixedRelateUpperLevel')"
          disabled
        ></el-input>
      </el-form-item>
      <div class="group-title">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          fill="none"
          version="1.1"
          width="14"
          height="14"
          viewBox="0 0 14 14"
        >
          <defs>
            <clipPath id="master_svg0_14_3894">
              <rect x="0" y="0" width="14" height="14" rx="0" />
            </clipPath>
          </defs>
          <g clip-path="url(#master_svg0_14_3894)">
            <g>
              <path
                d="M6.457915,2.687C4.222945,2.687,2.414865,4.49507,2.414865,6.73004C2.414865,8.24933,3.243565,9.56771,4.4866150000000005,10.2583L4.4866150000000005,10.4466C4.4866150000000005,10.861,4.825635,11.2126,5.252535,11.2126L7.663295,11.2126C8.077645,11.2126,8.429215,10.8735,8.429215,10.4466L8.429215,10.2583C9.672265,9.56771,10.500965,8.24933,10.500965,6.71749C10.500965,4.49507,8.692895,2.687,6.457915,2.687ZM8.567335,5.2861C8.504555,5.33632,8.416665,5.36144,8.341325000000001,5.36144C8.228325,5.36144,8.127875,5.31121,8.052534999999999,5.23587C7.713525,4.85919,7.236395,4.60807,6.721595,4.54529C6.520695,4.52018,6.382585,4.35695,6.382585,4.15605C6.382585,3.9426,6.558365,3.76682,6.771815,3.76682L6.822045,3.76682C7.525185,3.85471,8.178094999999999,4.18117,8.655225,4.70852C8.730565,4.79641,8.768235,4.90942,8.755675,5.02242C8.718005,5.12287,8.655225,5.22332,8.567335,5.2861ZM9.483925,5.86368C9.534155,5.98924,9.571825,6.1148,9.609485,6.2278C9.659715,6.4287,9.534155,6.64215,9.333255,6.70493C9.295585,6.71749,9.270475,6.71749,9.232805,6.71749C9.057025,6.71749,8.893795,6.59193,8.843565,6.41614C8.818455,6.3157,8.793345,6.2278,8.755675,6.15247C8.692895,5.98924,8.743125,5.78834,8.893795,5.68789C8.956575,5.63767,9.031905,5.61256,9.119805,5.61256C9.270475,5.61256,9.421145,5.713,9.483925,5.86368ZM7.914425,11.7022L5.001415,11.7022C4.775405,11.7022,4.587065,11.8906,4.587065,12.1291C4.587065,12.3677,4.775405,12.5561,5.001415,12.5561L7.914425,12.5561C8.140435,12.5561,8.328775,12.3677,8.328775,12.1291C8.328775,11.8906,8.140435,11.7022,7.914425,11.7022ZM7.248945,13.1462L5.666885,13.1462C5.415765,13.1462,5.202315,13.3345,5.202315,13.5731C5.202315,13.8117,5.415765,14,5.666885,14L7.248955,14C7.500075,14,7.713525,13.8117,7.713525,13.5731C7.713525,13.3345,7.500075,13.1462,7.248945,13.1462ZM6.545815,1.90852L6.382585,1.90852C6.056125,1.90852,5.792445,1.64484,5.792445,1.31839L5.792445,0.590135C5.792445,0.263677,6.056125,0,6.382585,0L6.545815,0C6.872265,0,7.135945,0.263677,7.135945,0.590135L7.135945,1.31839C7.135945,1.64484,6.872265,1.90852,6.545815,1.90852ZM8.944015,2.58655L8.805905,2.49865C8.529665,2.33543,8.429215,1.9713,8.592445,1.69507L8.956575,1.05471C9.119805,0.778475,9.483925,0.678027,9.760165,0.841256L9.898275,0.929148C10.174515,1.09238,10.274955,1.4565,10.111725,1.73274L9.747605,2.37309C9.584375,2.66188,9.220255,2.74978,8.944015,2.58655ZM10.676725,4.38206L10.588825,4.24395C10.425635,3.96771,10.526125,3.60359,10.802325,3.44036L11.442625,3.07623C11.718925,2.913,12.083025,3.01345,12.246225,3.28969L12.334125,3.4278C12.497425,3.70404,12.396925,4.06816,12.120725,4.23139L11.480325,4.59552C11.191525,4.75874,10.840025,4.67085,10.676725,4.38206ZM4.122495,2.51121L3.984375,2.5991C3.708145,2.76233,3.344015,2.66188,3.180785,2.38565L2.816665,1.74529C2.653435,1.46906,2.753885,1.10493,3.030115,0.941704L3.168235,0.853812C3.444465,0.690583,3.808595,0.791031,3.971815,1.06726L4.335945000000001,1.70762C4.499175,1.98386,4.398725000000001,2.34798,4.122495,2.51121ZM2.326975,4.24395L2.239085,4.38206C2.075855,4.6583,1.711735,4.75874,1.435497,4.59552L0.807694,4.23139C0.5314613,4.06816,0.4310128,3.70404,0.5942415,3.4278L0.682134,3.28969C0.845363,3.01345,1.209488,2.913,1.4857209999999998,3.07623L2.126085,3.44036C2.389755,3.60359,2.490205,3.95516,2.326975,4.24395Z"
                fill="#FFC300"
                fill-opacity="1"
              />
            </g>
          </g>
        </svg>
        {{ $t('dataCenter.indicatorLogicConfig') }}
      </div>
      <div class="group-content">
        <div style="display: flex; align-items: flex-end">
          <!--					预属性-->
          <div class="group-content-inner">
            <div class="group-content-inner--title">
              {{ $t('dataCenter.preAttribute') }}
            </div>
            <div class="group-content-inner--content">
              <el-input
                v-model="ruleForm.variableInput"
                :placeholder="$t('dataCenter.searchPreAttribute')"
                class="search-input"
                @input="preAttributeInput"
              >
                <span slot="prefix">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    fill="none"
                    version="1.1"
                    width="14"
                    height="14"
                    viewBox="0 0 14 14"
                  >
                    <g>
                      <g>
                        <path
                          d="M10.5024,9.67056C10.502,9.67023,10.5017,9.6699,10.5014,9.66957C10.4607,9.62748,10.451,9.56183,10.4873,9.51591C11.2794,8.51482,11.7516,7.24983,11.7516,5.87486C11.7516,2.63017,9.12098,0,5.87578,0C2.63059,0,0,2.63017,0,5.87486C0,9.11866,2.62916,11.7483,5.87314,11.7497C5.87509,11.7497,5.87647,11.7497,5.87841,11.7497C7.20121,11.7513,8.48444,11.3047,9.51957,10.4845C9.56536,10.4482,9.63011,10.4591,9.67135,10.5005C9.67161,10.5008,9.67187,10.501,9.67212,10.5013L13.0002,13.8288C13.1102,13.9388,13.2596,14.0004,13.4152,14C13.5654,13.9996,13.7095,13.9414,13.8177,13.8378C13.8257,13.8301,13.833,13.8228,13.8406,13.8148C14.058,13.5849,14.0511,13.2188,13.8316,12.9987C13.8311,12.9982,13.8308,12.9979,13.8303,12.9974L10.5038,9.67198C10.5032,9.67145,10.5029,9.6711,10.5024,9.67056M5.87543,10.5747C8.47151,10.5747,10.5761,8.47053,10.5761,5.87486C10.5761,3.27918,8.47151,1.17497,5.87543,1.17497C3.27935,1.17497,1.1748,3.27918,1.1748,5.87486C1.1748,8.47053,3.27935,10.5747,5.87543,10.5747Z"
                          fill-rule="evenodd"
                          fill="#3D3D3D"
                          fill-opacity="0.6000000238418579"
                        />
                      </g>
                    </g>
                  </svg>
                </span>
              </el-input>
              <div
                class="select-item"
                v-for="(item, key) in ruleForm.variableInput
                  ? variableFuzzy
                  : ruleForm.variable"
                :key="key"
              >
                <span>{{ key }} =</span>
                <el-form-item
                  style="margin-bottom: 0"
                  :prop="'variable.' + key"
                  :rules="{
                    required: true,
                    message: $t('common.pleaseSelect'),
                    trigger: 'change',
                  }"
                >
                  <el-cascader
                    ref="cascadeRef"
                    v-model="ruleForm.variable[key]"
                    :options="cascadeOptions"
                    :props="cascadeProps"
                    clearable
                    :placeholder="$t('common.pleaseSelect')"
                  />
                </el-form-item>
              </div>
              <div class="add-item">
                <el-button
                  icon="el-icon-plus"
                  type="warning"
                  plain
                  @click="addVariable"
                >
                  {{ $t('dataCenter.addVariable') }}
                </el-button>
              </div>
            </div>
          </div>
          <!--					公式列表-->
          <div class="group-content-inner">
            <div class="group-content-inner--title">公式列表</div>
            <div class="group-content-inner--content formula-content">
              <el-input
                v-model="ruleForm.formulaInput"
                placeholder="搜索公式"
                class="search-input"
                @input="formulaInput"
              >
                <span slot="prefix">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    fill="none"
                    version="1.1"
                    width="14"
                    height="14"
                    viewBox="0 0 14 14"
                  >
                    <g>
                      <g>
                        <path
                          d="M10.5024,9.67056C10.502,9.67023,10.5017,9.6699,10.5014,9.66957C10.4607,9.62748,10.451,9.56183,10.4873,9.51591C11.2794,8.51482,11.7516,7.24983,11.7516,5.87486C11.7516,2.63017,9.12098,0,5.87578,0C2.63059,0,0,2.63017,0,5.87486C0,9.11866,2.62916,11.7483,5.87314,11.7497C5.87509,11.7497,5.87647,11.7497,5.87841,11.7497C7.20121,11.7513,8.48444,11.3047,9.51957,10.4845C9.56536,10.4482,9.63011,10.4591,9.67135,10.5005C9.67161,10.5008,9.67187,10.501,9.67212,10.5013L13.0002,13.8288C13.1102,13.9388,13.2596,14.0004,13.4152,14C13.5654,13.9996,13.7095,13.9414,13.8177,13.8378C13.8257,13.8301,13.833,13.8228,13.8406,13.8148C14.058,13.5849,14.0511,13.2188,13.8316,12.9987C13.8311,12.9982,13.8308,12.9979,13.8303,12.9974L10.5038,9.67198C10.5032,9.67145,10.5029,9.6711,10.5024,9.67056M5.87543,10.5747C8.47151,10.5747,10.5761,8.47053,10.5761,5.87486C10.5761,3.27918,8.47151,1.17497,5.87543,1.17497C3.27935,1.17497,1.1748,3.27918,1.1748,5.87486C1.1748,8.47053,3.27935,10.5747,5.87543,10.5747Z"
                          fill-rule="evenodd"
                          fill="#3D3D3D"
                          fill-opacity="0.6000000238418579"
                        />
                      </g>
                    </g>
                  </svg>
                </span>
              </el-input>
              <template v-if="ruleForm.formulaInput">
                <div class="select-item" style="flex-direction: column">
                  <p
                    v-for="(item, index) in formulaFuzzy"
                    :key="index"
                    @click="formulaFuzzyHandle(item)"
                    :class="{
                      isActive:
                        item.value === formulaValue.commonOperatorsValue ||
                        item.value === formulaValue.statisticalValue ||
                        item.value === formulaValue.mathematicalValue ||
                        item.value === formulaValue.logicValue,
                    }"
                  >
                    {{ item.label }}
                  </p>
                </div>
              </template>
              <template v-else>
                <div class="select-item">
                  <el-select
                    v-model="formulaValue.commonOperatorsValue"
                    :placeholder="$t('dataCenter.commonOperator')"
                    @change="formulaChange($event, 'CommonOperators')"
                  >
                    <el-option
                      :label="item.label"
                      :value="item.value"
                      v-for="(item, index) in formulaList.CommonOperators"
                      :key="index"
                    ></el-option>
                  </el-select>
                </div>
                <div class="select-item">
                  <el-select
                    v-model="formulaValue.statisticalValue"
                    :placeholder="$t('dataCenter.statisticalFunction')"
                    @change="formulaChange($event, 'Statistical')"
                  >
                    <el-option
                      :label="item.label"
                      :value="item.value"
                      v-for="(item, index) in formulaList.Statistical"
                      :key="index"
                    ></el-option>
                  </el-select>
                </div>
                <div class="select-item">
                  <el-select
                    v-model="formulaValue.mathematicalValue"
                    :placeholder="$t('dataCenter.mathematicalFunction')"
                    @change="formulaChange($event, 'Mathematical')"
                  >
                    <el-option
                      :label="item.label"
                      :value="item.value"
                      v-for="(item, index) in formulaList.Mathematical"
                      :key="index"
                    ></el-option>
                  </el-select>
                </div>
                <div class="select-item">
                  <el-select
                    v-model="formulaValue.logicValue"
                    :placeholder="$t('dataCenter.logicFunction')"
                    @change="formulaChange($event, 'LogicList')"
                  >
                    <el-option
                      :label="item.label"
                      :value="item.value"
                      v-for="(item, index) in formulaList.LogicList"
                      :key="index"
                    ></el-option>
                  </el-select>
                </div>
              </template>
            </div>
          </div>
          <div class="group-content-inner" v-if="formulaData.name">
            <div class="group-content-inner--content">
              <div class="formula-title">{{ formulaData.name }}</div>
              <div class="formula-explain">
                {{ formulaData.paraphrase }}
              </div>
            </div>
          </div>
        </div>
        <div
          style="margin-top: 30px"
          class="group-content-inner clear-el-form-item"
        >
          <el-form-item
            prop="formula"
            :rules="{
              required: true,
              message: $t('dataCenter.pleaseInputFormulaConfig'),
              trigger: 'change',
            }"
          >
            <TributeEditor
              v-if="drawer"
              ref="tributeEditorRef"
              :atList="atList"
              :default-value="ruleForm.formulaZh"
              :default-value-zh="ruleForm.formula"
              @input="getFormulaData"
            />
          </el-form-item>
        </div>
      </div>
    </el-form>
    <div class="drawer-footer">
      <el-button type="primary" @click="onSubmit">{{
        $t('common.submit')
      }}</el-button>
      <el-button @click="closeDrawer">{{ $t('common.cancel') }}</el-button>
    </div>
  </el-drawer>
</template>

<script>
import TributeEditor from './TributeEditor'
import { CommonOperators, LogicList, Mathematical, Statistical } from '../const'
import {
  findInterfaceFieldIdInfo,
  findInterfaceInfo,
  findSourceInfo,
  analysisAttributeCheckName,
  analysisAttributeCheckCode,
} from '../api/analysisTarget'
import { checkValueRepeat } from './index'
export default {
  name: 'AddEditingAnalysisVariable',
  components: {
    TributeEditor,
  },
  props: {
    title: { type: String },
  },
  data() {
    return {
      drawer: false,
      ruleForm: {
        name: '', //分析指标名称
        code: '', //参数名
        moduleId: '', //所属模块的id
        moduleName: '',
        formulaZh: '', //公式（中文展示）
        formula: '', //公式
        variable: {}, //预属性值
        variableInput: '', //预属性搜索值
        formulaInput: '', //公式搜索值
      },
      variableFuzzy: {}, //预属性--模糊搜索的展示结果
      rules: {},
      cascadeOptions: [],
      cascadeProps: {
        lazy: true, //是否动态加载子节点
        lazyLoad: this.getFindInterfaceInfo,
      },
      formulaList: {
        CommonOperators,
        Statistical,
        Mathematical,
        LogicList,
      },
      formulaFuzzy: [],
      formulaValue: {
        commonOperatorsValue: null,
        statisticalValue: null,
        mathematicalValue: null,
        logicValue: null,
      },
      formulaData: {
        name: '',
        paraphrase: '',
      },
      atList: [],
      //提交按钮loading
      loading: false,
    }
  },
  mounted() {
    this.getFindSourceInfo()
  },
  watch: {
    'ruleForm.variable': {
      handler(cur) {
        this.atList = []
        Object.keys(cur).forEach((item, index) => {
          if (
            this.$refs.cascadeRef &&
            this.$refs.cascadeRef[index] &&
            this.$refs.cascadeRef[index].getCheckedNodes().length
          ) {
            this.atList.push({
              name: item,
              value: this.$refs.cascadeRef[index].getCheckedNodes()[0].label,
            })
          }
        })
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    openDrawer(data) {
      this.atList = []
      this.ruleForm = this.$options.data().ruleForm
      this.formulaValue = this.$options.data().formulaValue
      this.drawer = true
      if (data.moduleId) this.ruleForm.moduleId = data.moduleId
      if (data) {
        if (!data.id) {
          return
        }

        const { createTime, updateTime, ...newData } = data
        this.ruleForm = newData
        Object.keys(newData.variable).forEach((item, index) => {
          this.setCascadeEcho(newData.variable[item], item)
        })
      }
    },
    /**
     * 公式配置&公式展示的值
     * @param formula
     * @param formulaZh
     */
    getFormulaData(formulaZh, formula) {
      this.ruleForm.formulaZh = formulaZh
      this.ruleForm.formula = formula
    },
    /**
     * @description: 设置级联数据的回显
     * @param selectObj
     * @param name 对象字段名
     */
    setCascadeEcho(selectObj, name) {
      const firstLayerIndex = this.cascadeOptions.findIndex(
        (firstLayerItem) => firstLayerItem.value === selectObj[0]
      )
      if (
        typeof firstLayerIndex === 'number' &&
        firstLayerIndex >= 0 &&
        !this.cascadeOptions[firstLayerIndex].children
      ) {
        findInterfaceInfo({
          pageNum: 1,
          pageSize: 999,
          interfaceName: undefined,
          sourceNo: selectObj[0], //供应商编号
        }).then((res) => {
          if (res.code === 200) {
            // options配置的懒加载数据给children赋值的时候，我们要用this.$set,不然回显不会显示
            this.$set(
              this.cascadeOptions[firstLayerIndex],
              'children',
              res.data.list.map((item) => {
                return {
                  value: item.interfaceManageNo,
                  label: item.interfaceName,
                  leaf: false,
                }
              })
            )
            const secondLayerIndex = this.cascadeOptions[
              firstLayerIndex
            ].children.findIndex(
              (secondLayerItem) => secondLayerItem.value === selectObj[1]
            )
            if (
              typeof secondLayerIndex === 'number' &&
              secondLayerIndex >= 0 &&
              !this.cascadeOptions[firstLayerIndex].children[secondLayerIndex]
                .children
            )
              findInterfaceFieldIdInfo({
                pageNum: 1,
                pageSize: 999,
                interfaceFieldIdName: undefined,
                manageNo: selectObj[1],
                interfaceFieldIdType: 1, //参数类型(0-入参，1-出参)
              }).then((res2) => {
                this.$set(
                  this.cascadeOptions[firstLayerIndex].children[
                    secondLayerIndex
                  ],
                  'children',
                  res2.data.list.map((item) => {
                    if (item.interfaceFieldIdManage === selectObj[2]) {
                      // console.log(item.interfaceFieldIdName);
                      this.atList.push({
                        name,
                        value: item.interfaceFieldIdName,
                      })
                    }
                    return {
                      value: item.interfaceFieldIdManage,
                      label: item.interfaceFieldIdName,
                      leaf: true,
                    }
                  })
                )
              })
          }
        })
      }
    },
    /**
     * @description: 关联元数据第一级
     */
    getFindSourceInfo() {
      findSourceInfo({
        pageNum: 1,
        pageSize: 30,
        sourceName: undefined,
        interfaceDataType: [0, 1], //0 元数据，1 特征变量，2 分析指标
      }).then((res) => {
        if (res.code === 200)
          this.cascadeOptions = res.data.list.map((item) => {
            return {
              value: item.interfaceSourceNo,
              label: item.dataName,
            }
          })
      })
    },
    /**
     * @description: 加载关联元数据下一级
     * @param node
     * @param resolve
     */
    getFindInterfaceInfo(node, resolve) {
      const { level } = node
      let nodes = []
      if (!node.children.length) {
        let API,
          queryData = {
            pageNum: 1,
            pageSize: 999,
          }
        switch (level) {
          case 1:
            API = findInterfaceInfo
            Object.assign(queryData, {
              interfaceName: undefined,
              sourceNo: node.value, //供应商编号
            })
            break
          case 2:
            API = findInterfaceFieldIdInfo
            Object.assign(queryData, {
              interfaceFieldIdName: undefined,
              manageNo: node.value,
              interfaceFieldIdType: 1, //参数类型(0-入参，1-出参)
            })
            break
        }
        API(queryData).then((res) => {
          if (res.code === 200) {
            nodes = res.data.list.map((item) => {
              return {
                value:
                  level === 1
                    ? item.interfaceManageNo
                    : item.interfaceFieldIdManage,
                label:
                  level === 1 ? item.interfaceName : item.interfaceFieldIdName,
                leaf: level >= 2,
              }
            })
            resolve(nodes)
          }
        })
      } else {
        resolve(nodes)
      }
    },
    /**
     * 判断分析指标名称&参数名是否重复
     * @param e
     * @param fields
     */
    checkHandle(e, fields) {
      let API,
        validatorInner,
        params = {
          id: this.ruleForm.id,
          moduleId: this.ruleForm.moduleId,
        }
      if (e) {
        switch (fields) {
          case 'name':
            API = analysisAttributeCheckName
            Object.assign(params, {
              name: this.ruleForm.name,
            })
            validatorInner = '当前分析指标名称重复'
            break
          case 'code':
            API = analysisAttributeCheckCode
            Object.assign(params, {
              code: this.ruleForm.code,
            })
            validatorInner = this.$t(
              'dataCenter.moduleUniqueIdentifierDuplicate'
            )
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
            this.$refs.ruleForm.validateField(fields, () => {})
          }
        })
      }
    },
    /**
     * 预属性搜索值
     * @param e
     */
    preAttributeInput(e) {
      this.variableFuzzy = Object.keys(this.ruleForm.variable).reduce(
        (acc, key) => {
          if (key.includes(e)) {
            acc[key] = this.ruleForm.variable[key]
          }
          return acc
        },
        {}
      )
    },
    //新增预属性
    addVariable() {
      const variableLength = Object.keys(this.ruleForm.variable).length
      this.$set(this.ruleForm.variable, `字段${variableLength + 1}`, [])
    },
    /**
     * 公式搜索值
     * @param e
     */
    formulaInput(e) {
      this.formulaFuzzy = []
      const { CommonOperators, Statistical, Mathematical, LogicList } =
        this.formulaList
      CommonOperators.forEach((item) => {
        if (item.label.includes(e)) {
          this.formulaFuzzy.push({ ...item, fieldName: 'CommonOperators' })
        }
      })
      Statistical.forEach((item) => {
        if (item.label.includes(e)) {
          this.formulaFuzzy.push({ ...item, fieldName: 'Statistical' })
        }
      })
      Mathematical.forEach((item) => {
        if (item.label.includes(e)) {
          this.formulaFuzzy.push({ ...item, fieldName: 'Mathematical' })
        }
      })
      LogicList.forEach((item) => {
        if (item.label.includes(e)) {
          this.formulaFuzzy.push({ ...item, fieldName: 'LogicList' })
        }
      })
    },
    /**
     * @description: 公式模糊搜索结果--点击事件
     * @param item
     */
    formulaFuzzyHandle(item) {
      switch (item.fieldName) {
        case 'CommonOperators':
          this.formulaValue.commonOperatorsValue = item.value
          break
        case 'Statistical':
          this.formulaValue.statisticalValue = item.value
          break
        case 'Mathematical':
          this.formulaValue.mathematicalValue = item.value
          break
        case 'LogicList':
          this.formulaValue.logicValue = item.value
          break
      }
      this.formulaChange(item.value, item.fieldName)
    },
    /**
     * 公式列表-select@change
     * @param e
     * @param fieldName
     */
    formulaChange(e, fieldName) {
      const current = this.formulaList[fieldName].find(
        (item) => item.value === e
      )
      if (!current) return
      let name
      switch (fieldName) {
        case 'CommonOperators':
          name = `常用运算符-${current.label}`
          Object.assign(this.formulaValue, {
            statisticalValue: '',
            mathematicalValue: '',
            logicValue: '',
          })
          break
        case 'Statistical':
          name = `统计函数-${current.label}`
          Object.assign(this.formulaValue, {
            commonOperatorsValue: '',
            mathematicalValue: '',
            logicValue: '',
          })
          break
        case 'Mathematical':
          name = `数学函数-${current.label}`
          Object.assign(this.formulaValue, {
            commonOperatorsValue: '',
            statisticalValue: '',
            logicValue: '',
          })
          break
        case 'LogicList':
          name = `逻辑函数-${current.label}`
          Object.assign(this.formulaValue, {
            commonOperatorsValue: '',
            statisticalValue: '',
            mathematicalValue: '',
          })
          break
      }
      Object.assign(this.formulaData, {
        name,
        paraphrase: `${current.paraphrase}`,
      })
    },
    closeDrawer(done) {
      if (Object.keys(this.rules).length) {
        if (this.rules.name.length >= 2) {
          this.rules.name.splice(1, 2)
        }
        if (this.rules.code.length >= 2) {
          this.rules.code.splice(1, 2)
        }
      }
      this.$refs.ruleForm.clearValidate()
      this.$refs.tributeEditorRef.tributeDestroy()
      if (typeof done === 'function') {
        done()
      } else {
        this.drawer = false
      }
    },
    onSubmit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          const { variableInput, formulaInput, moduleName, ...formData } =
            this.ruleForm
          this.$emit('submit', formData)
          this.closeDrawer()
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep .el-form {
  .el-form-item {
    &__label {
      padding-right: 10px;
      font-weight: 400;
      font-size: 14px;
      line-height: 38px;
    }

    .el-select,
    .el-input,
    .is-disabled,
    .el-textarea {
      width: 100%;

      &__inner {
        height: 42px;
        color: rgba(0, 0, 0, 0.85);
        background: rgba(0, 0, 0, 0.04);
        border-radius: 4px 4px 4px 4px;
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

  .group-title {
    display: flex;
    align-items: center;
    font-weight: 500;
    font-size: 16px;
    color: #ff8f1f;
    line-height: 26px;

    svg {
      margin-right: 4px;
    }
  }

  .group-content {
    margin-top: 20px;
    padding: 20px 14px;
    background: rgba(0, 0, 0, 0.04);
    border-radius: 12px 12px 12px 12px;
    border: 1px dashed #ff8f1f;

    .group-content-inner + .group-content-inner {
      margin-left: 14px;
    }

    &-inner {
      &.clear-el-form-item {
        .el-form-item {
          margin-bottom: 0;

          .el-form-item__content {
            margin-left: 0 !important;

            .el-textarea {
              width: 350px;

              .el-textarea__inner {
                height: 152px;
                border: none;
                background: var(--bg-color);
              }
            }
          }
        }
      }

      &--title {
        font-weight: 400;
        font-size: 14px;
        color: var(--text-color-secondary);
        line-height: 20px;
        margin-bottom: 10px;
      }

      &--content {
        padding: 0px 4px;
        position: relative;
        height: 228px;
        overflow-y: auto;
        background: var(--bg-color);
        border-radius: 4px;
        border: 1px solid var(--border-color);

        .search-input {
          top: 0;
          position: sticky;
          z-index: 9;
          border-bottom: 1px solid var(--border-color);

          .el-input__inner {
            height: 36px;
            border: none;
            border-radius: 0;
            padding-left: 34px;
            padding-right: 14px;
            background: var(--bg-color);
          }

          .el-input__prefix {
            left: 14px;

            > span {
              height: 100%;
              display: flex;
              align-items: center;
            }
          }
        }

        .select-item {
          display: flex;
          align-items: center;
          padding: 14px;
          font-weight: 500;
          font-size: 12px;
          color: rgba(0, 0, 0, 0.85);
          line-height: 18px;

          & + .select-item {
            padding-top: 0;
          }

          > span {
            color: var(--text-color-secondary);
          }

          > p {
            cursor: pointer;
            width: 100%;
            padding: 10px;
            background: rgba(0, 0, 0, 0.04);
            border-radius: 6px;

            &.isActive,
            &:hover {
              color: var(--primary-color);
              background: rgba(40, 136, 232, 0.1);
            }
          }

          .el-form-item__content {
            margin-left: 4px !important;
          }

          .el-select,
          .el-cascader {
            margin: 0 4px;
            width: 110px;

            .el-input__inner {
              padding-left: 8px;
              padding-right: 8px;
              height: 30px;
              background: rgba(0, 0, 0, 0.04);
              border-radius: 4px;
              // border: none;
            }

            .el-select__caret {
              font-weight: bold;
              color: #1e2135;
            }
          }

          .el-tag {
            height: 15px;
            line-height: 15px;
            padding: 0 4px;
            font-weight: 400;
            font-size: 8px;
            border: none;

            &--success {
              color: #00b578;
              background: rgba(0, 181, 120, 0.1);
            }
          }
        }

        .select-item + .add-item {
          padding-top: 0;
        }

        .add-item {
          position: sticky;
          bottom: 0;
          padding: 14px;
          display: flex;
          justify-content: center;
          background: var(--bg-color);

          .el-button--warning {
            width: 100%;
            font-weight: 400;
            font-size: 12px;
            color: #ff8f1f !important;
            line-height: 20px;
            background: #fff3e8;
            border-radius: 4px;
            border: 1px solid #ff8f1f;
          }
        }

        &.formula-content {
          .select-item {
            width: 100%;

            .el-select {
              width: 100%;
              margin: 0;
            }
          }
        }

        .formula-title {
          display: flex;
          align-items: center;
          height: 42px;
          font-weight: 500;
          font-size: 14px;
          color: #ff8f1f;
          line-height: 18px;
          padding: 0 14px;
          border-bottom: 1px solid var(--border-color);
        }

        .formula-explain {
          max-width: 204px;
          font-weight: 400;
          font-size: 12px;
          color: var(--text-color-secondary);
          line-height: 18px;
          padding: 10px 14px;
          white-space: pre-wrap;
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
