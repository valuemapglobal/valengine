<template>
  <!--	特征变量--新增||编辑属性-->
  <el-drawer
    size="40%"
    :title="title"
    :visible.sync="drawer"
    :wrapperClosable="false"
    :before-close="closeDrawer"
  >
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      :label-width="isEnglish() ? '160px' : '140px'"
    >
      <el-form-item :label="$t('dataCenter.featureVariableName')" prop="name">
        <el-input
          v-model="ruleForm.name"
          @change="checkHandle($event, 'name')"
          :placeholder="$t('common.pleaseInput')"
        />
      </el-form-item>
      <el-form-item :label="$t('dataCenter.featureVariableType')" prop="type">
        <el-select
          v-model="ruleForm.type"
          :placeholder="$t('common.pleaseSelect')"
        >
          <el-option
            :label="$t('dataCenter.characterType')"
            :value="0"
          ></el-option>
          <el-option :label="$t('dataCenter.decimal')" :value="1"></el-option>
          <el-option :label="$t('dataCenter.number')" :value="2"></el-option>
        </el-select>
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
          disabled
          :placeholder="$t('dataCenter.fixedRelateUpperLevel')"
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
        {{ $t('dataCenter.variableLogicConfig') }}
      </div>
      <el-form-item class="group-content">
        <el-form-item
          :label="$t('dataCenter.isFixedThreshold')"
          prop="thresholdType"
        >
          <el-radio-group v-model="ruleForm.thresholdType">
            <el-radio :label="0" border>{{ $t('common.yes') }}</el-radio>
            <el-radio :label="1" border>{{ $t('common.no') }}</el-radio>
            <el-radio :label="2" border>{{ $t('dataCenter.custom') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <!--				是-->
        <template v-if="ruleForm.thresholdType === 0">
          <div
            class="group-condition"
            v-for="(thresholdItem, thresholdIndex) in ruleForm.configThreshold"
            :key="'threshold' + thresholdIndex"
          >
            <div class="group-condition-title">
              {{ $t('dataCenter.combinationCalculationCondition')
              }}{{ thresholdIndex + 1 }}
              {{
                thresholdIndex === 0
                  ? 'if'
                  : ruleForm.configThreshold[thresholdIndex - 1].condition
              }}
            </div>
            <el-form-item
              style="width: 230px; margin-left: 10px; margin-bottom: 0"
              :prop="'configThreshold.' + thresholdIndex + '.selectObj'"
              :rules="{
                required: true,
                message: $t('dataCenter.metadataInterfaceFieldCannotBeEmpty'),
                trigger: 'blur',
              }"
            >
              <el-cascader
                v-model="thresholdItem.selectObj"
                :options="cascadeOptions"
                :props="cascadeProps"
                clearable
                :placeholder="$t('dataCenter.metadataInterfaceField')"
              />
            </el-form-item>
            <el-form-item
              style="width: 170px; margin-left: 10px; margin-bottom: 0"
              :prop="'configThreshold.' + thresholdIndex + '.operator'"
              :rules="{
                required: true,
                message: $t('dataCenter.logicConditionCannotBeEmpty'),
                trigger: 'blur',
              }"
            >
              <el-select
                v-model="thresholdItem.operator"
                :placeholder="$t('dataCenter.logicCondition')"
              >
                <el-option
                  :label="item.label"
                  :value="item.value"
                  v-for="item in thresholdConditionOptions"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              style="width: 56px; margin-left: 10px; margin-bottom: 0"
              :prop="'configThreshold.' + thresholdIndex + '.value'"
              :rules="{
                required: true,
                message: $t('common.pleaseInput'),
                trigger: 'blur',
              }"
            >
              <el-input
                v-model="thresholdItem.value"
                :placeholder="$t('dataCenter.threshold')"
              ></el-input>
            </el-form-item>
            <el-form-item
              v-if="thresholdIndex !== ruleForm.configThreshold.length - 1"
              style="width: 110px; margin-left: 10px; margin-bottom: 0"
              :prop="'configThreshold.' + thresholdIndex + '.condition'"
              :rules="{
                required: true,
                message: $t('common.pleaseSelect'),
                trigger: 'blur',
              }"
            >
              <el-select
                v-model="thresholdItem.condition"
                :placeholder="$t('dataCenter.and')"
              >
                <el-option
                  :label="$t('dataCenter.and')"
                  value="and"
                ></el-option>
                <el-option :label="$t('dataCenter.or')" value="or"></el-option>
              </el-select>
            </el-form-item>
            <el-button
              v-if="thresholdIndex === ruleForm.configThreshold.length - 1"
              style="margin-left: 10px"
              type="primary"
              icon="el-icon-plus"
              circle
              @click="addConfig('configThreshold')"
            ></el-button>
            <el-button
              v-if="thresholdIndex !== 0"
              style="margin-left: 10px"
              type="danger"
              icon="el-icon-delete"
              circle
              @click="delConfig(thresholdIndex, 'configThreshold')"
            ></el-button>
          </div>
        </template>
        <!--				否-->
        <template v-if="ruleForm.thresholdType === 1">
          <div class="group-condition">
            <div class="group-condition-title">统计逻辑条件</div>
            <el-form-item
              style="width: 230px; margin-left: 10px; margin-bottom: 0"
              :prop="'configCompute.selectObj'"
              :rules="{
                required: true,
                message: $t('dataCenter.metadataInterfaceFieldCannotBeEmpty'),
                trigger: 'blur',
              }"
            >
              <el-cascader
                v-model="ruleForm.configCompute.selectObj"
                :options="cascadeOptions"
                :props="cascadeProps"
                clearable
                :placeholder="$t('dataCenter.metadataInterfaceField')"
              />
            </el-form-item>
            <el-form-item
              style="width: 170px; margin-left: 10px; margin-bottom: 0"
              :prop="'configCompute.operator'"
              :rules="{
                required: true,
                message: $t('dataCenter.logicConditionCannotBeEmpty'),
                trigger: 'blur',
              }"
            >
              <el-select
                v-model="ruleForm.configCompute.operator"
                :placeholder="$t('dataCenter.logicCondition')"
              >
                <el-option
                  :label="item.label"
                  :value="item.value"
                  v-for="item in computeConditionOptions"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </template>
        <!--				自定义-->
        <el-form-item v-if="ruleForm.thresholdType === 2" label="代码脚本">
          <div class="group-condition">
            <el-form-item prop="configScript">
              <el-input
                type="textarea"
                v-model="ruleForm.configScript"
                resize="none"
              />
            </el-form-item>
            <div class="upload-copy">
              <el-tooltip effect="dark" placement="top">
                <div slot="content">
                  上传文件<br />
                  格式支持：js、txt<br />
                </div>
                <el-upload
                  ref="uploadRef"
                  class="upload-demo"
                  accept=".js,.txt"
                  action="#"
                  multiple
                  :limit="1"
                  :before-upload="beforeUpload"
                  :on-change="uploadFile"
                  :auto-upload="false"
                  :show-file-list="false"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    fill="none"
                    version="1.1"
                    width="16"
                    height="16"
                    viewBox="0 0 16 16"
                  >
                    <defs>
                      <mask
                        id="master_svg0_12_4344"
                        style="mask-type: alpha"
                        maskUnits="objectBoundingBox"
                      >
                        <g>
                          <rect
                            x="0"
                            y="0"
                            width="16"
                            height="16"
                            rx="0"
                            fill="#FFFFFF"
                            fill-opacity="1"
                          />
                        </g>
                      </mask>
                    </defs>
                    <g mask="url(#master_svg0_12_4344)">
                      <g>
                        <path
                          d="M5.66123,5.70855C5.8746100000000006,5.91006,6.22236,5.91006,6.4357500000000005,5.70855L7.46317,4.73828L7.46317,8.49247C7.46317,8.773109999999999,7.70343,9,8.000589999999999,9C8.29776,9,8.53801,8.773109999999999,8.53801,8.49247L8.53801,4.73828L9.565439999999999,5.70855C9.77883,5.91006,10.126570000000001,5.91006,10.33996,5.70855C10.55335,5.50703,10.55335,5.17863,10.33996,4.97711L8.41947,3.163453C8.188690000000001,2.9455156,7.81408,2.9455156,7.5833,3.163453L5.66123,4.97711C5.4462600000000005,5.1801200000000005,5.4462600000000005,5.50703,5.66123,5.70855ZM12.40483,9.5913L12.40483,10.64348C12.40483,11.29739,11.91523,11.82609,11.30966,11.82609L4.69034,11.82609C4.08477,11.82609,3.59517,11.29739,3.59517,10.64348L3.59517,9.5913C3.59517,9.26435,3.350366,9,3.047584,9C2.744802,9,2.5,9.26435,2.5,9.5913L2.5,10.62609C2.5,11.93739,3.484041,13,4.69839,13L11.30161,13C12.516,13,13.5,11.93739,13.5,10.62609L13.5,9.5913C13.5,9.26435,13.2552,9,12.9524,9C12.6496,9,12.40483,9.26435,12.40483,9.5913Z"
                          fill="#979797"
                          fill-opacity="1"
                        />
                      </g>
                    </g>
                  </svg>
                </el-upload>
              </el-tooltip>
              <div
                style="margin-left: 10px"
                @click="copyText(ruleForm.configScript)"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  fill="none"
                  version="1.1"
                  width="16"
                  height="16"
                  viewBox="0 0 16 16"
                >
                  <defs>
                    <mask
                      id="master_svg0_12_4341"
                      style="mask-type: alpha"
                      maskUnits="objectBoundingBox"
                    >
                      <g>
                        <rect
                          x="0"
                          y="0"
                          width="16"
                          height="16"
                          rx="0"
                          fill="#FFFFFF"
                          fill-opacity="1"
                        />
                      </g>
                    </mask>
                  </defs>
                  <g mask="url(#master_svg0_12_4341)">
                    <g>
                      <path
                        d="M11.5968,10.8304C11.26278,10.8304,10.992,10.55962,10.992,10.2256C10.992,9.891580000000001,11.26278,9.6208,11.5968,9.6208C11.91978,9.6208,12.1816,9.35897,12.1816,9.036L12.1816,3.7944C12.1816,3.47142,11.91978,3.2096,11.5968,3.2096L6.3552,3.2096C6.03223,3.2096,5.7704,3.47142,5.7704,3.7944C5.7704,4.12842,5.49962,4.3992,5.1655999999999995,4.3992C4.831580000000001,4.3992,4.5608,4.12842,4.5608,3.7944C4.563000000000001,2.804293,5.3651,2.00219965,6.3552,2L11.5968,2C12.5869,2.00219965,13.389,2.804293,13.3912,3.7944L13.3912,9.036C13.389,10.02611,12.5869,10.8282,11.5968,10.8304ZM8.915199999999999,13.452L3.8952,13.452C2.847963,13.4533,1.99867392,12.604,2.00000286102,11.5568L2.00000286102,6.5168C1.99823093,5.469250000000001,2.84765,4.61947,3.8952,4.6208L8.915199999999999,4.6208C9.96244,4.61947,10.81173,5.46876,10.8104,6.516L10.8104,11.556C10.8304,12.6048,9.964,13.452,8.915199999999999,13.452ZM3.8952,5.8304C3.51526,5.82726,3.20646,6.13606,3.2096,6.516L3.2096,11.556C3.20646,11.93594,3.51526,12.2447,3.8952,12.2416L8.915199999999999,12.2416C9.29514,12.2447,9.60394,11.93594,9.6008,11.556L9.6008,6.516C9.60394,6.13606,9.29514,5.82726,8.915199999999999,5.8304L3.8952,5.8304Z"
                        fill="#979797"
                        fill-opacity="1"
                      />
                    </g>
                  </g>
                </svg>
              </div>
            </div>
          </div>
        </el-form-item>
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
  featureAttributeCheckCode,
  featureAttributeCheckName,
  findInterfaceFieIdInfo,
  findInterfaceInfo,
  findSourceInfo,
  parseScript,
} from '../api/featureVariable'
import { checkValueRepeat } from './index'
export default {
  name: 'AddEditingVariable',
  props: {
    title: { type: String },
  },
  data() {
    return {
      drawer: false,
      ruleForm: {
        name: '', //特征变量名称
        type: '', //特征变量类型：0-字符型，1-小数，2-数值
        code: '', //参数名
        moduleName: '',
        moduleId: '', //所属模块的id
        thresholdType: 0, //是否固定阈值：0-是，1-否，2-自定义
        configThreshold: [
          { selectObj: [], operator: '', value: '', condition: '' },
        ], //阈值配置
        configCompute: { selectObj: [], operator: '' }, //计算配置
        configScript: '', //脚本配置
      },
      rules: {},
      thresholdConditionOptions: [
        { label: '大于', value: 'GT' },
        { label: '小于', value: 'LT' },
        { label: '等于', value: 'EQ' },
        { label: '不等于', value: 'NE' },
        { label: '包含', value: 'INCLUDE' },
        { label: '不包含', value: 'NINCLUDE' },
        { label: '距今大于等于多少年', value: 'GEY' },
        { label: '距今大于等于多少月', value: 'GEM' },
        { label: '距今大于等于多少天', value: 'GED' },
        { label: '距今小于等于多少年', value: 'LEY' },
        { label: '距今小于等于多少月', value: 'LEM' },
        { label: '距今小于等于多少天', value: 'LED' },
      ],
      computeConditionOptions: [
        { label: '求和', value: 'SUM' },
        { label: '次数统计', value: 'COUNT' },
      ],
      cascadeOptions: [],
      cascadeProps: {
        lazy: true, //是否动态加载子节点
        lazyLoad: this.getFindInterfaceInfo,
      },
      //提交按钮loading
      loading: false,
    }
  },
  computed: {
    rules() {
      return {
        name: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseInputFeatureVariableName'),
            trigger: 'blur',
          },
        ],
        type: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseSelectFeatureVariableType'),
            trigger: 'blur',
          },
        ],
        code: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseInputParamName'),
            trigger: 'blur',
          },
        ],
        thresholdType: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseSelectIsFixedThreshold'),
            trigger: 'blur',
          },
        ],
        configScript: [
          {
            required: true,
            message: this.$t('dataCenter.pleaseInputCodeScript'),
            trigger: 'blur',
          },
        ],
      }
    },
    thresholdConditionOptions() {
      return [
        { label: this.$t('dataCenter.greaterThan'), value: 'GT' },
        { label: this.$t('dataCenter.lessThan'), value: 'LT' },
        { label: this.$t('dataCenter.equal'), value: 'EQ' },
        { label: this.$t('dataCenter.notEqual'), value: 'NE' },
        { label: this.$t('dataCenter.include'), value: 'INCLUDE' },
        { label: this.$t('dataCenter.notInclude'), value: 'NINCLUDE' },
        { label: this.$t('dataCenter.greaterThanEqualYears'), value: 'GEY' },
        { label: this.$t('dataCenter.greaterThanEqualMonths'), value: 'GEM' },
        { label: this.$t('dataCenter.greaterThanEqualDays'), value: 'GED' },
        { label: this.$t('dataCenter.lessThanEqualYears'), value: 'LEY' },
        { label: this.$t('dataCenter.lessThanEqualMonths'), value: 'LEM' },
        { label: this.$t('dataCenter.lessThanEqualDays'), value: 'LED' },
      ]
    },
  },
  mounted() {
    this.getFindSourceInfo()
  },
  methods: {
    openDrawer(data) {
      this.drawer = true
      if (data) {
        if (!data.id) {
          this.ruleForm = {
            id: undefined,
            name: '', //特征变量名称
            type: '', //特征变量类型：0-字符型，1-小数，2-数值
            code: '', //参数名
            moduleName: data.moduleName, //
            moduleId: data.moduleId, //所属模块的id
            thresholdType: 0, //是否固定阈值：0-是，1-否，2-自定义
            configThreshold: [
              { selectObj: [], operator: '', value: '', condition: '' },
            ], //阈值配置
            configCompute: { selectObj: [], operator: '' }, //计算配置
            configScript: '', //脚本配置
          }
          return
        }
        const { createTime, updateTime, configThreshold, ...newData } = data
        this.ruleForm = newData
        if (
          this.ruleForm.configCompute &&
          this.ruleForm.configCompute.selectObj &&
          this.ruleForm.configCompute.selectObj.length
        ) {
          // console.log('this.ruleForm.configCompute',this.ruleForm.configCompute.selectObj)
          this.setCascadeEcho(this.ruleForm.configCompute.selectObj)
        }
        if (configThreshold && configThreshold.length) {
          let configThresholdArr = []
          configThreshold.forEach((item, index) => {
            /**
             * 如果当前数组只有一个对象，则前数组的最后一个对象为并且关系;
             * 如果当前数组存在多个对象，则当前对象为或者关系；
             * 如果是数组中最后一个，则当前关系为空
             */
            item.forEach((child) => {
              this.$set(child, 'condition', 'or')
            })
            this.$set(item[item.length - 1], 'condition', 'and')
            if (configThreshold.length === index + 1) {
              this.$set(item[item.length - 1], 'condition', '')
            }
            item.forEach((child) => {
              configThresholdArr.push(child)
            })
          })
          this.$set(this.ruleForm, 'configThreshold', configThresholdArr)
          //处理元数据、接口变化、字段名称级联数据的回显
          configThresholdArr.forEach((item) => {
            this.setCascadeEcho(item.selectObj)
          })
          console.log('this.cascadeOptions', this.cascadeOptions)
        }
      }
    },
    /**
     * @description:设置级联数据的回显
     * @param selectObj
     */
    setCascadeEcho(selectObj) {
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
                console.log(item, 'item')

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
              findInterfaceFieIdInfo({
                pageNum: 1,
                pageSize: 999,
                interfaceFieIdName: undefined,
                manageNo: selectObj[1],
                interfaceFieIdType: 1, //参数类型(0-入参，1-出参)
              }).then((res2) => {
                this.$set(
                  this.cascadeOptions[firstLayerIndex].children[
                    secondLayerIndex
                  ],
                  'children',
                  res2.data.list.map((item) => {
                    return {
                      value: item.interfaceFieIdManage,
                      label: item.interfaceFieIdName,
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
     * @description:关联元数据第一级
     */
    getFindSourceInfo() {
      findSourceInfo({
        pageNum: 1,
        pageSize: 30,
        sourceName: undefined,
        interfaceDataType: [0], //0 元数据，1 特征变量，2 分析指标
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
     * @description:加载关联元数据下一级
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
            API = findInterfaceFieIdInfo
            Object.assign(queryData, {
              interfaceFieIdName: undefined,
              manageNo: node.value,
              interfaceFieIdType: 1, //参数类型(0-入参，1-出参)
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
                    : item.interfaceFieIdManage,
                label:
                  level === 1 ? item.interfaceName : item.interfaceFieIdName,
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
     * 判断特征变量名称&参数名是否重复
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
            API = featureAttributeCheckName
            Object.assign(params, {
              name: this.ruleForm.name,
            })
            validatorInner = '当前特征变量名称重复'
            break
          case 'code':
            API = featureAttributeCheckCode
            Object.assign(params, {
              code: this.ruleForm.code,
            })
            validatorInner = '当前参数名重复'
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
              console.log(this.rules[fields])
            }
            this.$refs.ruleForm.validateField(fields, (valid) => {
              console.log('valid', valid)
            })
          }
        })
      }
    },
    /**
     * @description:新增组合计算条件
     * @param fieldName 字段名
     */
    addConfig(fieldName) {
      let defaultObj = { selectObj: [], operator: '', condition: '' }
      switch (fieldName) {
        case 'configThreshold':
          this.$set(defaultObj, 'value', '')
          break
        case 'configCompute':
          break
      }
      this.ruleForm[fieldName].push(defaultObj)
    },
    /**
     * @description:删除组合计算条件
     * @param index 下标
     * @param fieldName 字段名
     */
    delConfig(index, fieldName) {
      this.ruleForm[fieldName].splice(index, 1)
    },
    /**
     * 文件上传之前
     * @param file
     * @returns {boolean}
     */
    beforeUpload(file) {
      const { name, size } = file
      const accept = ['js', 'txt']
      if (
        !accept.some(
          (i) => name.slice(name.lastIndexOf('.') + 1).toLowerCase() == i
        )
      ) {
        setTimeout(() => {
          this.$message.warning('上传文件格式错误！')
        }, 0)
        return false
      }
    },
    /**
     * @description:脚本上传
     * @param file
     */
    uploadFile(file) {
      parseScript({ script: file.raw }).then((res) => {
        if (res.code === 200) {
          this.$message.success('脚本上传成功！')
          this.$refs.uploadRef.clearFiles()
          this.ruleForm.configScript = res.data
        }
      })
    },
    /**
     * 脚本复制
     * @param textToCopy
     */
    copyText(textToCopy) {
      const dom = document.createElement('input')
      dom.value = textToCopy
      document.body.appendChild(dom)
      dom.select()
      document.execCommand('copy')
      document.body.removeChild(dom)
      this.$message.success('文本已复制到剪贴板')
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
      if (typeof done === 'function') {
        done()
      } else {
        this.drawer = false
      }
    },
    onSubmit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.loading = true
          const { moduleName, configThreshold, ...ruleForm } = this.ruleForm
          let configThresholdArr = []
          configThreshold.forEach((item, index) => {
            const previousItem =
              index > 0 ? configThreshold[index - 1] : undefined
            const { condition, ...currentItem } = item
            if (!previousItem) {
              configThresholdArr.push([currentItem])
            } else {
              switch (previousItem.condition) {
                case 'and':
                  configThresholdArr.push([currentItem])
                  console.log('configThresholdArr----and:', configThresholdArr)
                  break
                case 'or':
                  configThresholdArr[configThresholdArr.length - 1].push(
                    currentItem
                  )
                  console.log('configThresholdArr----or:', configThresholdArr)
                  break
              }
            }
          })
          console.log({
            configThreshold: configThresholdArr,
            ...ruleForm,
          })
          this.$emit('submit', {
            configThreshold: configThresholdArr,
            ...ruleForm,
          })
          this.closeDrawer()
          this.loading = false
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

      .el-input__inner {
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
    background: var(--table-body-bg);
    border-radius: 12px 12px 12px 12px;
    border: 1px dashed #ff8f1f;

    .el-radio {
      background: #ffffff;
    }

    .group-condition {
      display: flex;
      align-items: center;

      & + .group-condition {
        margin-top: 16px;
      }

      .el-cascader {
        width: 100%;
      }

      .el-input__inner,
      .el-textarea__inner {
        background: #ffffff;
        border: 1px solid #dddddd;
      }

      .el-form-item:has(.el-textarea) {
        width: 100%;
      }

      .el-textarea {
        //width: 596px;

        .el-textarea__inner {
          height: 140px;
          padding-right: 70px;
        }
      }

      .group-condition-title {
        min-width: 130px;
        text-align: right;
        color: var(--text-color-secondary);
      }

      .upload-copy {
        display: flex;
        align-items: center;
        position: absolute;
        top: 0;
        right: 14px;

        svg {
          cursor: pointer;
        }
      }
    }

    .el-form-item__content {
      margin-left: 0 !important;
    }
  }
}

.drawer-footer {
  padding: 50px 20px 20px;
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
