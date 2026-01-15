<template>
  <div class="container-warp">
    <div class="content-list">
      <div class="content-item">
        <div style="display: flex">
          <img src="../image/info.png" class="img" />
          <div class="item-text">
            <div class="item-header">{{ itemInfo.interfaceName }}</div>
            <div class="item-dec">
              <span>{{ $t('interfacePlatform.description') }}</span>
              <span>{{ itemInfo.interfaceDescription }}</span>
            </div>
            <div class="item-dec">
              <span>{{ $t('interfacePlatform.interfaceId') }}</span>
              <span>{{ itemInfo.interfaceNo }}</span>
            </div>
          </div>
        </div>
        <div class="item-btn">
          <el-button
            v-show="itemInfo.interfaceOn === 0"
            @click="changeStatus(1)"
            >{{ $t('interfacePlatform.close') }}</el-button
          >
          <el-button
            type="primary"
            v-show="itemInfo.interfaceOn === 1"
            @click="changeStatus(0)"
            >{{ $t('interfacePlatform.open') }}</el-button
          >
        </div>
      </div>
    </div>
    <div class="tab-warp">
      <el-radio-group v-model="form.radio">
        <el-radio-button :label="radioOptions.productDetail"></el-radio-button>
        <el-radio-button :label="radioOptions.businessParam"></el-radio-button>
        <el-radio-button :label="radioOptions.returnCode"></el-radio-button>
        <el-radio-button :label="radioOptions.interfaceTest"></el-radio-button>
      </el-radio-group>
      <div
        class="inner-content"
        v-show="form.radio == radioOptions.productDetail"
      >
        <div class="inner-title">
          {{ $t('interfacePlatform.productFeatures') }}
        </div>
        <div class="inner-data">{{ itemInfo.interfaceFeatures }}</div>
      </div>
      <div v-show="form.radio == radioOptions.businessParam">
        <div class="inner-content">
          <div class="inner-title">
            {{ $t('interfacePlatform.productRequestParam') }}
          </div>
          <div class="inner-data inner-table">
            <el-table border :data="reqlist" style="width: 100%" height="250px">
              <el-table-column
                prop="interfaceFieIdName"
                :label="$t('interfacePlatform.parameterName')"
              >
              </el-table-column>
              <el-table-column
                prop="interfaceFieIdDataType"
                :label="$t('interfacePlatform.dataTypeLabel')"
              >
                <template slot-scope="scope">
                  <span>{{
                    selectDictLabel(
                      dataTypeList,
                      scope.row.interfaceFieIdDataType
                    )
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="interfaceFieIdDescription"
                :label="$t('interfacePlatform.parameterDescription')"
              >
              </el-table-column>
              <el-table-column
                prop="interfaceFieIdRemark"
                :label="$t('interfacePlatform.parameterRemark')"
              >
              </el-table-column>
            </el-table>
          </div>
        </div>
        <div class="inner-content">
          <div class="inner-title">
            {{ $t('interfacePlatform.productResponseResult') }}
          </div>
          <div class="inner-data inner-table">
            <el-table border :data="reslist" style="width: 100%" height="250px">
              <el-table-column
                prop="interfaceFieIdName"
                :label="$t('interfacePlatform.parameterName')"
              >
              </el-table-column>
              <el-table-column
                prop="interfaceFieIdDataType"
                :label="$t('interfacePlatform.dataTypeLabel')"
              >
                <template slot-scope="scope">
                  <span>{{
                    selectDictLabel(
                      dataTypeList,
                      scope.row.interfaceFieIdDataType
                    )
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="interfaceFieIdDescription"
                :label="$t('interfacePlatform.parameterDescription')"
              >
              </el-table-column>
              <el-table-column
                prop="interfaceFieIdRemark"
                :label="$t('interfacePlatform.parameterRemark')"
              >
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
      <div class="inner-content" v-show="form.radio == radioOptions.returnCode">
        <div class="inner-title">
          {{ $t('interfacePlatform.returnStatusCode') }}
        </div>
        <div class="inner-data inner-table">
          <el-table border :data="codeList" style="width: 100%" height="200px">
            <el-table-column
              prop="dictValue"
              :label="$t('interfacePlatform.name')"
            >
            </el-table-column>
            <el-table-column
              prop="dictLabel"
              :label="$t('interfacePlatform.description')"
            >
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div
        class="inner-content"
        v-show="form.radio == radioOptions.interfaceTest"
      >
        <div class="inner-title">
          <span>{{ $t('interfacePlatform.requestParam') }}</span>
          <el-button
            type="primary"
            style="border-radius: 6px"
            v-if="options.length > 0"
            @click="handleTest"
            :disabled="btnLoading"
            ><i v-if="btnLoading" class="el-icon-loading"></i>
            {{ $t('interfacePlatform.test') }}</el-button
          >
        </div>
        <div class="inner-select">
          <el-form
            ref="interfaceFormRef"
            :model="interfaceForm"
            label-width="auto"
            :rules="rules"
            :validate-on-rule-chang="false"
          >
            <el-form-item
              v-for="(item, index) in options"
              :label="item.interfaceFieIdDescription + '：'"
              :prop="item.interfaceFieIdAlias"
              :key="index"
            >
              <div style="display: flex">
                <el-input
                  :placeholder="$t('common.pleaseInput')"
                  v-model="interfaceForm[item.interfaceFieIdAlias]"
                ></el-input>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div
        class="inner-content"
        v-show="form.radio == radioOptions.interfaceTest"
        style="min-height: 135px"
      >
        <div
          class="inner-title"
          style="position: sticky; top: 0px; left: 0px; z-index: 99"
        >
          {{ $t('interfacePlatform.responseResultTitle') }}
        </div>
        <div class="json-container" v-if="res">
          <json-viewer
            :value="JSON.parse(JSON.stringify(res))"
            :expand-depth="1"
            expanded
            copyable
          ></json-viewer>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { apiserviceQuery } from '@/api/dataRisk/basicVariables'
import {
  findInterfaceFieIdInfo,
  findInterfaceInfoTest,
  interfaceOn,
} from '@/views/interfacePlatform/api/dataList'
import { getDicts } from '@/api/index'
import { v4 as uuidv4 } from 'uuid'
import { validateObject, validateArray } from '@/utils/validator'
export default {
  props: ['itemInfo', 'sourceNo'],
  data() {
    return {
      optionValue: '', // 参数名称
      res: '', // 接口测试返回
      options: [], // 参数名称下拉
      reqlist: [],
      reslist: [],
      tableData: [],
      form: {
        radio: '',
      },
      interfaceForm: {},
      rules: {},
      codeList: [], // 状态码
      serviceInfo: {},
      query: {
        pageNum: 1,
        pageSize: 10,
      },
      dataTypeList: [],
      btnLoading: false,
      validateMap: new Map([
        [3, validateObject],
        [4, validateArray],
      ]),
    }
  },
  computed: {
    radioOptions() {
      return {
        productDetail: this.$t('interfacePlatform.productDetail'),
        businessParam: this.$t('interfacePlatform.businessParam'),
        returnCode: this.$t('interfacePlatform.returnCode'),
        interfaceTest: this.$t('interfacePlatform.interfaceTest'),
      }
    },
  },
  watch: {
    itemInfo: {
      handler(n, o) {
        this.getfindInterfaceFieIdInfo()
        this.getOption()
        this.res = ''

        if (this.$refs.interfaceFormRef)
          this.$refs.interfaceFormRef.clearValidate()
      },
    },
    form: {
      handler(n, o) {
        if (n.radio == this.radioOptions.businessParam) {
          // this.getApiserviceheaders()
          this.getfindInterfaceFieIdInfo()
        }
        if (n.radio == this.radioOptions.returnCode) {
          this.getCode()
        }
        if (n.radio == this.radioOptions.interfaceTest) {
          this.getOption()
        }
      },
      immediate: true,
      deep: true,
    },
  },
  mounted() {
    // 初始化 radio 值为产品详情
    this.form.radio = this.radioOptions.productDetail
    getDicts('decision_data_type')
      .then((res) => {
        if (res.code == 200) {
          this.dataTypeList = res.data.map((item) => {
            return {
              label: item.dictLabel,
              value: item.dictValue,
            }
          })
        }
      })
      .catch((err) => {})
  },
  methods: {
    handleTest() {
      this.$refs.interfaceFormRef.validate((valid) => {
        if (valid) {
          let interfaceForm = JSON.parse(JSON.stringify(this.interfaceForm))
          this.options.map((item, index) => {
            let key = item.interfaceFieIdName
            if (
              [3, 4].includes(item.interfaceFieIdDataType) &&
              interfaceForm[key]
            ) {
              interfaceForm[key] = JSON.parse(interfaceForm[key])
            }
          })
          let data = {
            manageNo: this.itemInfo.interfaceManageNo,
            sourceNo: this.sourceNo,
            interfaceNo: this.options[0].interfaceNo,
            paramData: interfaceForm,
            orderId: uuidv4().replace(/-/g, ''),
          }
          // if (data.paramData && data.paramData.hasOwnProperty('orderNo') && data.paramData.orderNo) {
          data.orderId = data.paramData.orderNo || data.paramData.orderId
          // }
          this.btnLoading = true
          findInterfaceInfoTest(data)
            .then((res) => {
              if (res.code == 200) {
                this.res = res.data
                this.btnLoading = false
              }
            })
            .catch(() => {
              this.btnLoading = false
            })
        }
      })
    },
    close() {
      this.$emit('close')
    },
    // 接口状态更新
    changeStatus(type) {
      let data = {
        manageNo: this.itemInfo.interfaceManageNo,
        sourceNo: this.itemInfo.interfaceSourceNo,
        interfaceOn: type,
      }
      interfaceOn(data).then((res) => {
        if (res.code == 200) {
          if (this.itemInfo.interfaceOn == 1) {
            this.itemInfo.interfaceOn = 0
          } else {
            this.itemInfo.interfaceOn = 1
          }
          // this.$emit('changeStatus',this.itemInfo)
        }
      })
    },
    // 获取接口返回状态码
    getCode() {
      getDicts('api_service_returncode').then((res) => {
        this.codeList = res.data
      })
    },
    getDetail() {
      apiserviceQuery(this.itemInfo.apiServiceId).then((res) => {
        this.serviceInfo = res.data
      })
    },
    // // 获取请求头参数
    // getApiserviceheaders() {
    //     apiserviceheaders(this.itemInfo.apiServiceId).then((res) => {
    //         this.tableData = res.data
    //     })
    // }
    // 获取接口测试-参数名称
    getOption() {
      this.rules = {}
      findInterfaceFieIdInfo({
        manageNo: this.itemInfo.interfaceManageNo,
      }).then((res) => {
        if (res.code == 200) {
          // 只有入参才能测试
          this.options = res.data.list.filter(
            (item) => item.interfaceFieIdType === 0
          )
          this.options.forEach((item) => {
            this.$set(this.interfaceForm, item.interfaceFieIdAlias, null)
            this.$set(this.rules, item.interfaceFieIdAlias, [
              {
                validator: this.validateMap.get(item.interfaceFieIdDataType),
                trigger: 'blur',
              },
            ])
          })
          this.$nextTick(() => {
            if (this.$refs.interfaceFormRef)
              this.$refs.interfaceFormRef.clearValidate()
          })
        }
      })
    },

    // 获取参数
    getfindInterfaceFieIdInfo() {
      findInterfaceFieIdInfo({
        manageNo: this.itemInfo.interfaceManageNo,
      }).then((res) => {
        if (res.code == 200) {
          this.reqlist = res.data.list.filter(
            (item, index) => item.interfaceFieIdType == 0
          )
          this.reslist = res.data.list.filter(
            (item, index) => item.interfaceFieIdType == 1
          )
        }
      })
    },
    selectDictLabel(datas, value) {
      var actions = []
      Object.keys(datas).some((key) => {
        if (datas[key].value == value) {
          actions.push(datas[key].label)
          return true
        }
      })
      return actions.join('')
    },
  },
}
</script>

<style lang="less" scoped>
.container-warp {
  height: 100%;
  display: flex;
  width: 100%;
  flex-direction: column;
}

.content-list {
  height: 100%;
  box-sizing: border-box;
  height: 148px;
  background-color: var(--bg-color);

  .content-item {
    display: flex;
    align-items: center;
    padding: 30px 30px 30px 30px;
    box-sizing: border-box;
    position: relative;
    justify-content: space-between;

    .img {
      width: 89px;
      height: 88px;
      margin-right: 16px;
    }

    .head-line-cancel {
      position: absolute;
      top: -15px;
      right: -4px;
      z-index: 20;
    }

    .item-text {
      .item-header {
        font-size: 18px;
        font-weight: normal;
        line-height: 26px;
        letter-spacing: 0px;
        color: var(--text-color-secondary);
        margin-bottom: 10px;
      }

      .item-dec {
        font-weight: 400;
        font-size: 14;
        margin-bottom: 10px;

        span {
          color: var(--text-color-tertiary);
        }
        span:first-child {
          color: var(--text-color-secondary);
        }
      }
    }

    .item-btn {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      .el-button {
        padding: 0px 24px;
        height: 42px;
        font-size: 14px;
        border-radius: 8px;
      }
    }
  }
}

.tab-warp {
  width: 100%;
  height: 635px;
  border-radius: 4px;
  flex: 1;
  background: var(--bg-color);
  margin-top: 20px;
  overflow-y: auto;
}

::v-deep .el-radio-button--small .el-radio-button__inner {
  padding: 14px 20px !important;
  border: none !important;
  font-size: 18px;
}

.inner-select {
  padding: 20px;
  box-sizing: border-box;

  ::v-deep .el-form {
    .el-form-item__label {
      line-height: 42px;
      white-space: nowrap;
      text-align: right;
    }
  }

  .label {
    font-size: 14px;
    font-weight: 400;
    color: rgba(0, 0, 0, 0.85);
    margin-right: 20px;
  }

  ::v-deep .el-input--small .el-input__inner {
    width: 352px;
    height: 45px;
    background: var(--bg-color-lighter);
    border-radius: 6px 6px 6px 6px;
  }
}

.inner-content {
  box-sizing: border-box;
  height: 340px;
  overflow: auto;
  border-radius: 6px;
  background: var(--bg-color);
  box-sizing: border-box;
  border: 1px solid var(--border-color);
  margin: 30px;

  .inner-title {
    width: 100%;
    height: 50px;
    background: var(--table-header-bg);
    font-size: 18px;
    font-weight: normal;
    letter-spacing: 0px;
    line-height: 50px;
    color: var(--text-color-secondary);
    padding: 0px 20px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-button {
      width: 85px;
      height: 42px;
      margin-left: 0px;
      font-size: 18px;
    }
  }

  .inner-data {
    padding: 20px 20px 0px 20px;
    box-sizing: border-box;
    color: var(--text-color-secondary);
  }
}

::v-deep.el-radio-group {
  width: 100%;
  border-bottom: 1px solid var(--border-color);
  .el-radio-button__inner {
    color: var(--text-color-secondary);
    background: var(--bg-color);
  }
  .is-active {
    .el-radio-button__inner {
      color: #fff !important;
      background: var(--primary-color) !important;
    }
  }
}

.inner-table {
  height: calc(100% - 70px);
}

::v-deep.el-table {
  font-size: 16px !important;

  .has-gutter {
    font-size: 16px !important;
  }
}
</style>
