<template>
  <div class="preApproval_limit">
    <main>
      <div
        class="leftRate"
        :element-loading-text="$t('decisionPlatform.dataLoading')"
        v-if="priceList.length || priceCard != ''"
      >
        <div class="searchTop">
          <el-input
            v-model="priceCard"
            :placeholder="$t('decisionPlatform.inputPlaceholder')"
            suffix-icon="el-icon-search"
          ></el-input>
        </div>
        <div class="content" v-loading="loading">
          <template v-for="(item, index) in priceList">
            <div
              class="card"
              :key="index"
              :class="{ active: activeCard == item.id }"
              @click="handleClickCard(item, index)"
            >
              <div class="cardTop">
                <span class="title">{{ item.priceCard }}</span>
                <div class="switch" @click.stop>
                  <el-switch
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
          </template>
        </div>
      </div>
      <div class="noData" v-else>
        <img src="@/assets/images/dataRisk/noData.png" />
        <p>{{ $t('decisionPlatform.notCreatedScoreCard') }}</p>
        <p>{{ $t('decisionPlatform.pleaseConfigureFirst') }}</p>
        <el-button @click="jumpToScore">
          <img class="icon" src="@/assets/images/dataRisk/add.png" />{{
            $t('decisionPlatform.goToAdd')
          }}
        </el-button>
      </div>
      <div class="rightTable">
        <div v-if="showTable">
          <div class="title">
            {{ $t('decisionPlatform.riskPricingRuleTable') }}
          </div>
          <el-table
            v-loading="tableLoading"
            ref="elTableRef"
            :data="priceTable"
            border
            height="calc(var(--bgvh) - 355px)"
            @row-click="checkRow"
            :row-class-name="tableRowClassName"
          >
            <el-table-column
              :label="$t('decisionPlatform.rate')"
              prop="rate"
              width="116"
            />
            <el-table-column
              :label="$t('decisionPlatform.pricing')"
              prop="limitRange"
            >
              <template slot-scope="{ row }">
                <div class="editRange">
                  <div :class="{ checkFailed: row.checkPrice }">
                    <!-- <span v-if="defaultIndex != null">{{row.index > defaultIndex?'-':'+'}}</span> -->
                    <span class="value" v-if="!edit">{{ row.price }}</span>
                    <el-input
                      v-else
                      v-model="row.price"
                      :placeholder="$t('decisionPlatform.inputPlaceholder')"
                    ></el-input
                    >%
                  </div>
                  <!-- <div v-else>标准额度</div> -->
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
            <el-button type="primary" @click="editTable" :disabled="edit">{{
              $t('decisionPlatform.editLimitRule')
            }}</el-button>
            <el-button type="primary" :disabled="!edit" @click="submit">{{
              $t('decisionPlatform.confirmSubmit')
            }}</el-button>
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
  </div>
</template>

<script>
import {
  getPrice,
  getPriceList,
  releasePrice,
  submitPrice,
  updateStatusPrice,
} from '../api/price'
import { mapState } from 'vuex'
export default {
  components: {},
  props: {
    step: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      mine: this,
      priceCard: '',
      priceList: [],
      activeCard: 0,
      showTable: true,
      priceTable: [
        {
          rate: 'AAA',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        // {
        //   rate: 'AA+',
        //   price: null,
        //   remark: null,
        //   checkRemark: false,
        //   checkPrice: false,
        //   default: false,
        // },
        {
          rate: 'AA',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'A',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'BBB',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'BB',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'B',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'C',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'D',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
          default: false,
        },
        {
          rate: 'E',
          price: null,
          remark: null,
          checkRemark: false,
          checkPrice: false,
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
    priceTable: {
      handler(val) {
        if (!Object.keys(this.currentRow).length) return
        let index = this.currentRow.index
        let currentVal = val[index]
        currentVal.checkRemark = !currentVal.remark ? true : false

        let seq = /^[1-9]\d*$/
        let seq1 = /^-[1-9]\d*$/
        if (!seq.test(currentVal.price) && !seq1.test(currentVal.price))
          currentVal.checkPrice = true
        else currentVal.checkPrice = false
      },
      deep: true,
    },
    queryParams: {
      handler(val) {
        this.priceTable.map((item) => {
          item.default = false
          item.checkRemark = false
          item.price = null
          item.remark = null
          return item
        })
        if (!val.standardRate || !val.creditSource) {
          this.edit = false
        }
        if (val.standardRate) {
          let row = this.priceTable.find(
            (item) => item.rate == val.standardRate
          )
          if (row) {
            row.default = true
            this.defaultIndex = row.index
          }
        }
      },
      deep: true,
    },
    priceCard: {
      handler() {
        this.getPriceData()
      },
    },
    'dataRisk.decision': {
      handler(val) {
        this.decision = val
        this.getPriceData()
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getPriceData() {
      this.loading = true
      getPriceList({
        priceCard: this.priceCard,
        ...this.decision,
      })
        .then((res) => {
          if (res.code == 200 && res.data.length) {
            let data = res.data
            this.priceList = data
            if (
              !this.activeCard ||
              data.findIndex((item) => item.id == this.activeCard) == -1
            ) {
              this.activeCard = res.data[0].id
            }
            this.handlePrice()
          } else {
            this.priceList = []
            this.activeCard = null
            this.resetPrice()
          }
          this.loading = false
        })
        .catch((err) => {})
    },
    submit() {
      let checkPrice = 0
      let checkRemark = 0
      let mapList = []
      let list = JSON.parse(JSON.stringify(this.priceTable))
      list.forEach((item, index) => {
        if (item.checkPrice) checkPrice++
        if (item.checkRemark) checkRemark++
        item.price ? (item.price = item.price) : null
        let obj = {
          name: item.rate,
          priceRangeContent: item.remark,
          value: item.price,
        }
        mapList.push(obj)
      })
      if (checkPrice) {
        this.popupPrompt(
          this.$t('decisionPlatform.currentPricingFloatRangeInvalid')
        )
        this.dialogVisible = true
        return
      }
      if (checkRemark) {
        this.popupPrompt(
          this.$t('decisionPlatform.currentPricingExplanationIncomplete')
        )
        return
      }
      submitPrice({
        ...this.decision,
        mapList: mapList,
        ...this.queryParams,
        priceCardId: this.activeCard,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.edit = false
            this.getPriceData()
          }
        })
        .catch((err) => {
          this.edit = false
        })
    },
    handlePrice() {
      this.tableLoading = true
      this.resetPrice()
      getPrice({ priceCardId: this.activeCard })
        .then((res) => {
          this.tableLoading = false
          if (res.code == 200 && res.data.length) {
            let data = res.data
            data.forEach((item) => {
              let index = this.priceTable.findIndex(
                (itemX) => itemX.rate == item.name
              )
              if (index != -1) {
                this.currentRow.index = index
                let findData = this.priceTable[index]
                findData.remark = item.priceRangeContent
                findData.checkRemark = findData.remark == null ? true : false
                findData.price = item.value
              }
            })
          }
        })
        .catch((err) => {})
    },
    resetPrice() {
      this.queryParams = {
        standardRate: null,
        creditSource: null,
      }
      this.defaultIndex = null
      this.currentRow = {}
      this.priceTable = this.$options.data().priceTable
    },
    jumpToScore() {
      this.$parent.active = 'score'
    },
    handleClickCard(data, index) {
      this.activeCard = data.id
      this.handlePrice()
    },
    editTable() {
      if (!this.activeCard) {
        this.popupPrompt(this.$t('decisionPlatform.pleaseSelectScoreCard'))
        return
      }
      this.edit = !this.edit
    },
    checkRow(row) {
      this.currentRow = row
    },
    handleSwitch(data) {
      const action =
        data.buttonState == 1
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableIndicator', {
          action: action,
          name: data.priceCard,
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
          updateStatusPrice({ ...params })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getPriceData()
              }
            })
            .catch((err) => {
              this.getPriceData()
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
    popupPrompt(msg) {
      this.checkMsg = msg
      this.dialogVisible = true
    },
    tableRowClassName({ row, rowIndex }) {
      row.index = rowIndex
    },
    releaseRule() {
      releasePrice({
        ...this.decision,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.getPriceData()
          }
        })
        .catch((err) => {})
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
        color: var(--text-color-secondary);
        margin-bottom: 20px;
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
      height: 26px;
      padding: 0px 9px;
      font-size: 14px;
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
