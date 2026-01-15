<template>
  <div class="preApproval_rule">
    <main>
      <div
        class="leftRate"
        :element-loading-text="$t('decisionPlatform.dataLoading')"
        v-if="rateList.length || rateCard != ''"
      >
        <div class="searchTop">
          <el-input
            v-model="rateCard"
            :placeholder="$t('decisionPlatform.inputPlaceholder')"
            suffix-icon="el-icon-search"
          ></el-input>
        </div>
        <div class="content" v-loading="loading">
          <div v-for="(item, index) in rateList" :key="index">
            <div
              class="card"
              :class="{ active: activeCard == item.id }"
              @click="handleClickCard(item, index)"
            >
              <div class="cardTop">
                <span class="title">{{ item.rateCard }}</span>
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
        <p>{{ $t('decisionPlatform.notCreatedRateCard') }}</p>
        <p>{{ $t('decisionPlatform.pleaseConfigureFirst') }}</p>
        <el-button @click="jumpToScore">
          <img class="icon" src="@/assets/images/dataRisk/add.png" />{{
            $t('decisionPlatform.goToAdd')
          }}
        </el-button>
      </div>
      <div class="rightTable">
        <div v-if="showTable">
          <div class="title">{{ $t('decisionPlatform.rateRuleTable') }}</div>
          <el-table
            v-loading="tableLoading"
            :data="rateRulesTable"
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
              :label="$t('decisionPlatform.scoreRange')"
              prop="rateRange"
            >
              <template slot-scope="{ row }">
                <div class="editRange">
                  <div :class="{ checkFailed: row.checkDy }">
                    <span>{{ $t('decisionPlatform.greaterThan') }}</span>
                    <span class="value" v-if="!edit">{{ row.dy }}</span>
                    <el-input
                      v-else
                      v-numberOnly
                      v-model="row.dy"
                      placeholder=""
                    ></el-input>
                  </div>
                  <div
                    v-if="row.rateRange"
                    :class="{ checkFailed: row.checkXydy }"
                  >
                    <span>{{ $t('decisionPlatform.lessThanOrEqual') }}</span>
                    <span class="value" v-if="!edit">{{ row.xydy }}</span>
                    <el-input
                      v-else
                      v-numberOnly
                      v-model="row.xydy"
                      placeholder=""
                    ></el-input>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="remark">
              <template slot="header">
                <span style="color: red">*</span>
                {{ $t('decisionPlatform.remark') }}
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
                    max="200"
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
              >{{ $t('decisionPlatform.editRateRule') }}</el-button
            >
            <el-button
              @click="submit"
              type="primary"
              :disabled="!edit || !activeCard"
              >{{ $t('decisionPlatform.submit') }}</el-button
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
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  getRate,
  getRateList,
  releaseRate,
  submitRate,
  updateStatusRate,
} from '../api/rate'
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
      keyword: null,
      tableLoading: false,
      rateList: [],
      activeCard: null,
      showTable: true,
      rateRulesTable: [
        {
          rate: 'AAA',
          rateRange: false,
          dy: null,
          checkDy: false,
          checkRemark: false,
          remark: null,
        },
        // {
        //   rate: 'AA+',
        //   rateRange: true,
        //   dy: null,
        //   xydy: null,
        //   checkDy: false,
        //   checkXydy: false,
        //   checkRemark: false,
        //   remark: null,
        // },
        {
          rate: 'AA',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'A',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'BBB',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'BB',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'B',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'C',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'D',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
        {
          rate: 'E',
          rateRange: true,
          dy: null,
          xydy: null,
          checkDy: false,
          checkXydy: false,
          checkRemark: false,
          remark: null,
        },
      ],
      edit: false,
      currentRow: {}, //当前编辑行
      decision: null,
      rateCard: '',
      rateCardId: null,
      dialogVisible: false,
      checkMsg: null,
      loading: false,
      tableLoading: false,
    }
  },
  created() {},
  mounted() {
    //禁用tab键
    window.addEventListener(
      'keydown',
      function (e) {
        if (e.keyCode == 9) e.preventDefault()
      },
      false
    )
    // this.getRateListData()
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  watch: {},
  watch: {
    rateCard: {
      handler() {
        this.getRateListData()
      },
    },
    'dataRisk.decision': {
      handler(val) {
        this.decision = val
        if (val.projectCode) {
          this.getRateListData()
        }
      },
      deep: true,
      immediate: true,
    },
    rateRulesTable: {
      handler(val) {
        let index = this.currentRow.index
        let currentVal = val[index]
        let prevVal = index != 0 ? val[index - 1] : null
        let nextVal = index != val.length ? val[index + 1] : null

        if (currentVal && currentVal.remark == null)
          currentVal.checkRemark = true
        else currentVal.checkRemark = false

        //当前行的 [小于等于] 必须等于上一行的 [大于]
        if (
          prevVal &&
          prevVal.dy &&
          currentVal.xydy &&
          parseInt(prevVal.dy) != parseInt(currentVal.xydy)
        ) {
          currentVal.checkXydy = true
        } else currentVal.checkXydy = false

        //当前行的 [小于等于] 必须大于 [大于]
        if (
          currentVal.xydy &&
          currentVal.dy &&
          parseInt(currentVal.dy) >= parseInt(currentVal.xydy)
        ) {
          currentVal.checkDy = true
        } else currentVal.checkDy = false

        if (nextVal) {
          //当前行的 [大于] 自动带入下行的 [小于等于]
          nextVal.xydy = currentVal.dy

          //下一行的 [小于等于] 必须大于 [大于]
          if (nextVal.xydy && parseInt(nextVal.dy) >= parseInt(nextVal.xydy)) {
            nextVal.checkDy = true
          } else {
            nextVal.checkDy = false
            nextVal.checkXydy = false
          }
        }
      },
      deep: true,
    },
  },
  methods: {
    getRateListData() {
      this.loading = true
      getRateList({
        rateCard: this.rateCard,
        ...this.decision,
      })
        .then((res) => {
          if (res.code == 200 && res.data.length) {
            let data = res.data
            this.rateList = data
            if (
              !this.activeCard ||
              data.findIndex((item) => item.id == this.activeCard) == -1
            ) {
              this.activeCard = res.data[0].id
            }
            this.handleRate()
          } else {
            this.rateList = []
            this.activeCard = null
            this.resetRate()
          }
          this.loading = false
        })
        .catch((err) => {})
    },
    submit() {
      let mapList = []
      let checkRang = 0
      let checkRemark = 0
      this.rateRulesTable.forEach((item) => {
        if (item.rate == 'AAA') item.xydy = 99999999
        if (
          item.checkDy ||
          item.checkXyd ||
          item.dy == null ||
          item.xydy == null
        ) {
          checkRang++
        } else if (item.checkRemark) {
          checkRemark++
        } else {
          mapList.push({
            name: item.rate,
            value: `${item.dy ? item.dy : null},${
              item.xydy ? item.xydy : null
            }`,
            rateRangeContent: item.remark,
          })
        }
      })

      if (checkRang > 0) {
        this.popupPrompt(this.$t('decisionPlatform.currentScoreRangeInvalid'))
        return
      }
      if (checkRemark > 0) {
        this.popupPrompt(
          this.$t('decisionPlatform.currentRateRemarkIncomplete')
        )
        return
      }
      let params = {
        mapList: mapList,
        ...this.decision,
        rateCardId: this.activeCard,
      }
      submitRate({ ...params })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.edit = false
            this.getRateListData()
          }
        })
        .catch((err) => {
          this.edit = false
        })
    },
    jumpToScore() {
      this.$parent.active = 'score'
    },
    handleClickCard(data, index) {
      this.activeCard = data.id
      this.edit = false
      this.resetRate()
      this.handleRate()
    },
    handleRate() {
      this.tableLoading = true
      getRate({ rateCardId: this.activeCard })
        .then((res) => {
          if (res.code == 200 && res.data.length) {
            let data = res.data
            data.forEach((item) => {
              let index = this.rateRulesTable.findIndex(
                (itemX) => itemX.rate == item.name
              )
              if (index != -1) {
                this.currentRow.index = index
                let findData = this.rateRulesTable[index]
                findData.remark = item.rateRangeContent
                findData.checkRemark = findData.remark == null ? true : false
                let moneyRange = item.value.split(',')
                findData.dy = moneyRange[0]
                if (findData.hasOwnProperty('xydy') && moneyRange.length > 1)
                  findData.xydy = moneyRange[1]
              }
            })
          }
          this.tableLoading = false
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
          name: data.rateCard,
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
          updateStatusRate({ ...params })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getRateListData()
              }
            })
            .catch((err) => {
              this.getRateListData()
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
    resetRate() {
      this.currentRow = {}
      this.rateRulesTable = this.$options.data().rateRulesTable
    },
    editTable() {
      if (!this.activeCard) {
        this.popupPrompt(this.$t('decisionPlatform.pleaseSelectRateCard'))
        return
      }
      this.edit = !this.edit
    },
    checkRow(row) {
      this.currentRow = row
    },
    tableRowClassName({ row, rowIndex }) {
      row.index = rowIndex
    },
    popupPrompt(msg) {
      this.checkMsg = msg
      this.dialogVisible = true
    },
    releaseRule() {
      releaseRate({
        ...this.decision,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.getRateListData()
          }
        })
        .catch((err) => {})
    },
  },
}
</script>

<style lang="less" scoped>
.preApproval_rule {
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
        font-size: 18px;
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
        overflow-y: auto;
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
