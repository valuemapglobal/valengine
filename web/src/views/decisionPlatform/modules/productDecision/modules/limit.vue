<template>
  <div class="preApproval_limit">
    <main>
      <div
        class="leftRate"
        :element-loading-text="$t('decisionPlatform.dataLoading')"
        v-if="limitList.length > 0 || quotaCard != ''"
      >
        <div class="searchTop">
          <el-input
            v-model="quotaCard"
            :placeholder="$t('decisionPlatform.inputPlaceholder')"
            suffix-icon="el-icon-search"
          ></el-input>
        </div>
        <div class="content" v-loading="loading">
          <div v-for="(item, index) in limitList" :key="index">
            <div
              class="card"
              :class="{ active: activeCard == item.id }"
              @click="handleClickCard(item, index)"
            >
              <div class="cardTop">
                <span class="title">{{ item.quotaCard }}</span>
                <div class="switch" @click.stop>
                  <el-switch
                    v-if="hasButton('enable:disable:show')"
                    v-model="item.buttonState"
                    active-color="#D6D3D3"
                    inactive-color="var(--primary-color)"
                    :active-text="$t('decisionPlatform.disabled')"
                    :inactive-text="$t('decisionPlatform.enabled')"
                    :active-value="0"
                    :inactive-value="1"
                    @change="handleSwitch(item)"
                  />
                </div>
              </div>
              <div class="cardDescribe">
                {{ $t('decisionPlatform.description') }}：{{ item.description }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="noData" v-else>
        <img src="@/assets/images/dataRisk/noData.png" />
        <p>{{ $t('decisionPlatform.notCreatedLimitModel') }}</p>
        <p>{{ $t('decisionPlatform.pleaseConfigureFirst') }}</p>
        <el-button @click="jumpToRate">
          <img class="icon" src="@/assets/images/dataRisk/add.png" />{{
            $t('decisionPlatform.goToAdd')
          }}
        </el-button>
      </div>
      <div class="rightTable">
        <div v-if="showTable">
          <div class="title">
            <span>{{ $t('decisionPlatform.limitRuleTable') }}</span>
            <el-button
              type="primary"
              @click="openFormulaConfiguration"
              v-if="activeCard"
            >
              {{ $t('decisionPlatform.standardLimitConfiguration') }}
            </el-button>
          </div>
          <div class="search">
            <el-form ref="form" :model="queryParams">
              <el-form-item :label="$t('decisionPlatform.ratingStandard')">
                <el-select
                  v-model="queryParams.standardRate"
                  :placeholder="$t('decisionPlatform.pleaseSelect')"
                  :popper-append-to-body="false"
                  clearable
                  :disabled="!activeCard"
                >
                  <el-option
                    v-for="(item, index) in standardList"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              <!-- <el-form-item label="标准额度测算来源">
                <el-select
                  v-model="queryParams.creditSource"
                  placeholder="请选择"
                  :popper-append-to-body="false"
                  clearable
                >
                  <el-option
                    v-for="(item, index) in sourceList"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item> -->
            </el-form>
          </div>
          <el-table
            v-loading="tableLoading"
            ref="elTableRef"
            :data="limitTable"
            border
            height="calc(var(--bgvh) - 415px)"
            @row-click="checkRow"
            :row-class-name="tableRowClassName"
          >
            <el-table-column
              :label="$t('decisionPlatform.rate')"
              prop="rate"
              width="116"
            />
            <el-table-column
              :label="$t('decisionPlatform.limitFloatRange')"
              prop="limitRange"
            >
              <template slot-scope="{ row }">
                <div class="editRange">
                  <div
                    v-if="!row.default"
                    :class="{ checkFailed: row.checkLimit }"
                  >
                    <span v-if="defaultIndex != null">{{
                      row.index > defaultIndex ? '-' : '+'
                    }}</span>
                    <span class="value" v-if="!edit">{{ row.limit }}</span>
                    <el-input
                      v-else
                      v-numberOnly
                      v-model="row.limit"
                      :placeholder="$t('decisionPlatform.inputPlaceholder')"
                    ></el-input
                    >%
                  </div>
                  <div v-else>{{ $t('decisionPlatform.standardLimit') }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="remark">
              <template slot="header">
                <span style="color: red">*</span>
                {{ $t('decisionPlatform.explanation') }}
              </template>
              <template slot-scope="{ row }">
                <div
                  class="editRemark"
                  :class="{ checkFailed: row.checkRemark }"
                >
                  <span class="value" v-if="!edit">{{
                    row.remark || $t('decisionPlatform.toBeFilled')
                  }}</span>
                  <el-input
                    v-else
                    v-model="row.remark"
                    :placeholder="$t('decisionPlatform.toBeFilled')"
                  ></el-input>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="bottomBtns">
            <el-button
              type="primary"
              @click="editTable"
              :disabled="edit || !activeCard"
              >{{ $t('decisionPlatform.editLimitRule') }}</el-button
            >
            <el-button
              type="primary"
              :disabled="!edit || !activeCard"
              @click="submit"
              >{{ $t('decisionPlatform.confirmSubmit') }}</el-button
            >
          </div>
        </div>
        <div class="tableNoData" v-else>
          <img src="@/assets/images/dataRisk/tableNoData.png" />
        </div>
      </div>
    </main>
    <el-dialog
      :title="$t('decisionPlatform.warning')"
      :visible.sync="dialogVisible"
      width="350px"
      :before-close="
        () => {
          dialogVisible = false
        }
      "
      class="dialogRelease"
    >
      <div class="dialogTitle" slot="title">
        <span>{{ $t('decisionPlatform.warning') }}</span>
      </div>
      <div class="releaseContent">
        <span>{{ checkMsg }}</span>
      </div>
      <div class="releaseBtnList">
        <el-button type="primary" @click="dialogVisible = false">{{
          $t('decisionPlatform.confirm')
        }}</el-button>
      </div>
    </el-dialog>
    <FormulaConfiguration
      ref="formulaConfiguration"
      :quotaCardId="activeCard"
    />
  </div>
</template>

<script>
import FormulaConfiguration from '../components/FormulaConfiguration.vue'
import {
  getLimit,
  getLimitList,
  releaseLimit,
  submitLimit,
  updateStatusLimit,
} from '../api/limit'
import { mapState } from 'vuex'
export default {
  components: {
    FormulaConfiguration,
  },
  props: {
    step: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      mine: this,
      quotaCard: '',
      standardList: [
        { label: 'AAA', value: 'AAA' },
        // { label: 'AA+', value: 'AA+' },
        { label: 'AA', value: 'AA' },
        { label: 'A', value: 'A' },
        { label: 'BBB', value: 'BBB' },
        { label: 'BB', value: 'BB' },
        { label: 'B', value: 'B' },
        { label: 'C', value: 'C' },
        { label: 'D', value: 'D' },
        { label: 'E', value: 'E' },
      ],
      sourceList: [
        { label: 'KYC', value: 'KYC' },
        { label: '财报', value: '财报' },
      ],
      limitList: [],
      activeCard: null,
      showTable: true,
      limitTable: [
        {
          rate: 'AAA',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        // {
        //   rate: 'AA+',
        //   limit: null,
        //   remark: null,
        //   checkRemark: false,
        //   checkLimit: false,
        //   default: false,
        // },
        {
          rate: 'AA',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'A',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'BBB',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'BB',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'B',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'C',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'D',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
        {
          rate: 'E',
          limit: null,
          remark: null,
          checkRemark: false,
          checkLimit: false,
          default: false,
        },
      ],
      edit: false,
      currentRow: {}, //当前编辑行
      queryParams: {
        standardRate: null,
        creditSource: null,
      },
      dialogVisible: false,
      checkMsg: null,
      defaultIndex: null,
      loading: false,
      tableLoading: false,
    }
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  mounted() {
    //禁用tab键
    window.addEventListener(
      'keydown',
      function (e) {
        if (e.keyCode == 9) e.preventDefault()
      },
      false
    )
  },
  watch: {
    limitTable: {
      handler(val) {
        if (!Object.keys(this.currentRow).length) return
        let index = this.currentRow.index
        let currentVal = val[index]
        currentVal.checkRemark = !currentVal.remark ? true : false
        currentVal.checkLimit =
          index > this.defaultIndex && currentVal.limit > 100 ? true : false
      },
      deep: true,
    },
    queryParams: {
      handler(val) {
        this.limitTable.map((item) => {
          item.default = false
          item.checkRemark = false
          item.limit = null
          item.remark = null
          return item
        })
        if (!val.standardRate) {
          this.edit = false
        }
        if (val.standardRate) {
          let row = this.limitTable.find(
            (item) => item.rate == val.standardRate
          )
          if (row) {
            row.default = true
            row.limit = 0
            row.checkRemark = true
            this.defaultIndex = row.index
          }
        }
      },
      deep: true,
    },
    quotaCard: {
      handler() {
        this.getLimitData()
      },
    },
    'dataRisk.decision': {
      handler(val) {
        this.decision = val
        this.getLimitData()
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getLimitData() {
      this.loading = true
      getLimitList({
        quotaCard: this.quotaCard,
        ...this.decision,
      })
        .then((res) => {
          if (res.code == 200 && res.data.length) {
            let data = res.data
            this.limitList = data
            if (
              !this.activeCard ||
              data.findIndex((item) => item.id == this.activeCard) == -1
            ) {
              this.activeCard = res.data[0].id
            }
            this.handleLimit()
          } else {
            this.limitList = []
            this.activeCard = null
            this.resetLimit()
          }
          this.loading = false
        })
        .catch((err) => {})
    },
    submit() {
      let checkLimit = 0
      let checkRemark = 0
      let mapList = []
      let list = JSON.parse(JSON.stringify(this.limitTable))
      list.forEach((item, index) => {
        if (item.checkLimit) checkLimit++
        if (item.checkRemark) checkRemark++
        item.limit ? (item.limit = item.limit) : null
        let obj = {
          name: item.rate,
          quotaRangeContent: item.remark,
          value: item.limit,
        }
        if (index > this.defaultIndex && obj.value) {
          obj.value = 0 - obj.value
        }
        mapList.push(obj)
      })
      if (checkLimit) {
        this.popupPrompt(
          this.$t('decisionPlatform.currentLimitFloatRangeInvalid')
        )
        this.dialogVisible = true
        return
      }
      if (checkRemark) {
        this.popupPrompt(
          this.$t('decisionPlatform.currentLimitExplanationIncomplete')
        )
        return
      }
      submitLimit({
        ...this.decision,
        mapList: mapList,
        ...this.queryParams,
        quotaCardId: this.activeCard,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.edit = false
            this.getLimitData()
          }
        })
        .catch((err) => {
          this.edit = false
        })
    },
    handleLimit() {
      this.resetLimit()
      this.tableLoading = true
      getLimit({ quotaCardId: this.activeCard })
        .then((res) => {
          this.tableLoading = false
          if (res.code == 200 && res.data.length) {
            let data = res.data
            let defaultData = data.find((itemX) => itemX.value == 0)
            defaultData.default = true
            this.queryParams.standardRate = defaultData.name
            // this.queryParams.creditSource = data[0].creditSource
            this.$nextTick(() => {
              data.forEach((item) => {
                let index = this.limitTable.findIndex(
                  (itemX) => itemX.rate == item.name
                )
                if (index != -1) {
                  this.currentRow.index = index
                  let findData = this.limitTable[index]
                  findData.remark = item.quotaRangeContent
                  findData.checkRemark = findData.remark == null ? true : false
                  findData.limit = item.value
                }
              })
            })
          }
        })
        .catch((err) => {})
    },
    handleSwitch(data) {
      const action =
        data.buttonState == 1
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableIndicator', {
          action: action,
          name: data.quotaCard,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          let params = {
            buttonState: data.buttonState,
            id: data.id,
            ...this.decision,
          }
          updateStatusLimit({ ...params })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getLimitData()
              }
            })
            .catch((err) => {
              this.getLimitData()
            })
        })
        .catch(() => {
          if (data.buttonState == 1) {
            data.buttonState = 0
          } else {
            data.buttonState = 1
          }
        })
    },
    resetLimit() {
      this.queryParams = {
        standardRate: null,
        creditSource: null,
      }
      this.defaultIndex = null
      this.currentRow = {}
      this.limitTable = this.$options.data().limitTable
    },
    jumpToRate() {
      this.$parent.active = 'rate'
    },
    handleClickCard(data, index) {
      this.activeCard = data.id
      this.handleLimit()
    },
    editTable() {
      if (!this.activeCard) {
        this.popupPrompt(this.$t('decisionPlatform.pleaseSelectScoreCard'))
        return
      }
      if (!this.queryParams.standardRate) {
        this.popupPrompt(this.$t('decisionPlatform.pleaseSelectStandardRating'))
        return
      }
      // if (!this.queryParams.creditSource) {
      //   this.popupPrompt('请选择【测算额度来源】')
      //   return
      // }
      this.edit = !this.edit
    },
    checkRow(row) {
      this.currentRow = row
    },
    popupPrompt(msg) {
      this.checkMsg = msg
      this.dialogVisible = true
    },
    tableRowClassName({ row, rowIndex }) {
      row.index = rowIndex
      if (row.rate == this.queryParams.standardRate) return 'highlight'
    },
    releaseRule() {
      releaseLimit({
        ...this.decision,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.getLimitData()
          }
        })
        .catch((err) => {})
    },
    openFormulaConfiguration() {
      this.$refs.formulaConfiguration.handleOpen()
    },
  },
}
</script>

<style lang="less" scoped>
.preApproval_limit {
  width: 100%;
  height: 100%;
  main {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-between;
    .leftRate,
    .noData {
      width: 340px;
      height: 100%;
      padding: 20px;
      background-color: rgba(#000, 0.02);
      border: none;
    }
    .noData {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      > img {
        width: 100px;
      }
      > p {
        font-size: 16px;
        color: #9e9e9e;
        font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        line-height: 26px;
        margin-bottom: 0px;
      }
      > .el-button {
        margin-top: 16px;
        background-color: rgba(#ff8f1f, 0.1);
        height: 40px;
        border: none;
        display: flex;
        align-items: center;
        font-size: 14px;
        font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        color: #ff8f1f;
        .icon {
          width: 20px;
          height: 20px;
          margin-right: 6px;
        }
      }
    }
    .leftRate {
      .searchTop {
        :deep(.el-input) {
          margin-bottom: 20px;
          .el-input__inner {
            height: 40px;
            background-color: rgba(#000, 0.04);
          }
          .el-input__icon {
            font-size: 20px;
          }
        }
      }
      .content {
        height: calc(100% - 60px);
        .card {
          width: 100%;
          height: 104px;
          padding: 14px;
          margin-bottom: 20px;
          cursor: pointer;
          background-color: var(--decision-model-card-bg);
          .cardTop {
            width: 100%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            color: var(--text-color-secondary);
            .title {
              width: calc(100% - 50px);
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
              font-size: 16px;
              font-family: PingFang SC-Medium, PingFang SC;
              font-weight: 500;
              color: var(--text-color-secondary);
            }
            .switch {
              display: flex;
              justify-content: flex-end;
              position: relative;

              /deep/.el-switch {
                .el-switch__core {
                  width: 50px !important;
                  height: 20px;
                  border-radius: 12px;
                  &:after {
                    width: 12px;
                    height: 12px;
                    left: 4px;
                    top: 3px;
                  }
                }
                &.el-switch {
                  &.is-checked {
                    .el-switch__core {
                      &:after {
                        left: 100%;
                      }
                    }
                  }
                }
                &.is-checked {
                  .el-switch__label--left {
                    opacity: 0;
                  }
                  .el-switch__label--right {
                    opacity: 1;
                  }
                }
                .el-switch__label {
                  position: absolute;
                  top: 0;
                  display: flex;
                  align-items: center;
                  > span {
                    font-size: 10px !important;
                  }
                }
                .el-switch__label--left {
                  right: 0;
                  color: #fff !important;
                  z-index: 1;
                  margin-right: 5px;
                }
                .el-switch__label--right {
                  left: 0;
                  color: #969696 !important;
                  opacity: 0;
                  margin-left: 5px;
                }
              }
            }
          }
          .cardDescribe {
            margin-top: 8px;
            height: 45px;
            font-size: 14px;
            font-family: PingFang SC-Regular, PingFang SC;
            font-weight: 400;
            color: var(--text-color-tertiary);
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
          }
        }
        .active {
          background-color: var(--decision-model-card-active-bg);
          border: 1px solid var(--primary-color);
        }
      }
    }

    .rightTable {
      width: calc(100% - 360px);
      background-color: rgba(#000, 0.02);
      position: relative;
      padding: 20px;
      .title {
        font-size: 18px;
        font-family: PingFang SC-Medium, PingFang SC;
        font-weight: 500;
        color: rgba(0, 0, 0, 0.85);
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .el-button {
          font-size: 14px;
        }
      }
      .search {
        :deep(.el-form) {
          display: flex;
          .el-form-item {
            display: flex;
            margin-right: 20px;
          }
          .el-form-item__label {
            white-space: nowrap;
          }
        }
      }

      .tableNoData {
        display: flex;
        justify-content: center;
        align-items: center;
      }
      .bottomBtns {
        position: absolute;
        bottom: 20px;
        right: 20px;
      }
    }
  }
}
.editRange,
.editRemark {
  display: flex;

  > div {
    display: flex;
    align-items: center;
  }
  span {
    display: inline-block;
    white-space: nowrap;
    margin-right: 10px;
  }
  .value {
    height: 26px;
    line-height: 26px;
    border-radius: 13px;
    background-color: var(--decision-rate-card-bg);
    border: var(--decision-rate-card-border);
    padding: 0px 10px;
    font-size: 14px;
  }
  :deep(.el-input) {
    margin-right: 10px;
    .el-input__inner {
      height: 26px !important;
      padding: 0px 9px !important;
      font-size: 14px !important;
    }
  }
}
.editRange {
  .value {
    width: 60px;
  }
  :deep(.el-input) {
    width: auto;
    .el-input__inner {
      width: 60px;
    }
  }
}
.editRemark {
  .value {
    width: 100%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
.checkFailed {
  .value {
    border: #fa5151 1px solid;
  }
  :deep(.el-input) {
    .el-input__inner {
      border: #fa5151 1px solid;
    }
  }
}
/deep/.el-table .highlight {
  background-color: rgba(#fa5151, 0.04) !important;
}
.dialogRelease {
  /deep/.el-dialog {
    border-radius: 4px;

    .el-dialog__body {
      padding: 0px 0px 20px 0px;
    }
    .dialogTitle {
      display: flex;
      align-items: center;
      > img {
        width: 22px;
        height: 22px;
        margin-right: 16px;
      }
      > span {
        font-size: 16px;
        color: rgba(#000, 0.85);
        font-weight: 500;
        font-family: PingFang SC-Medium, PingFang SC;
      }
    }
    .releaseContent {
      padding: 0px 60px;
      font-weight: 400;
      font-size: 14px;
      font-family: PingFang SC-Regular, PingFang SC;
      text-align: center;
    }
    .releaseBtnList {
      display: flex;
      justify-content: flex-end;
      margin-top: 14px;
      padding-right: 20px;
      .btnDisable {
        background-color: #f0f2f5;
        color: rgba(#000000, 0.3);
      }
      .btnWarn {
        border: none;
        background-color: rgba(#ff8f1f, 0.1);
        color: #ff8f1f;
      }
    }
  }
}
::-webkit-scrollbar {
  width: 0 !important;
}
::-webkit-scrollbar {
  width: 0 !important;
}
</style>
