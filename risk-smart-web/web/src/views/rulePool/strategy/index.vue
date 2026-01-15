<template>
  <div class="strategy-rule-pool">
    <div class="board-container">
      <div
        class="board-container-item"
        v-for="(item, index) in boardList"
        :key="index"
      >
        <div class="board-container-item-label">{{ item.label }}</div>
        <div class="board-container-item-value">{{ item.value }}</div>
        <el-progress
          class="line"
          :show-text="false"
          :percentage="item.rate"
          :color="'#1cd400'"
        ></el-progress>
        <div class="board-container-item-compared">
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            v-if="item.rate > 0"
            style="color: #1cd400"
          >
            <path
              fill="currentColor"
              d="M11 18V8.8l-3.6 3.6L6 11l6-6l6 6l-1.4 1.4L13 8.8V18z"
            />
          </svg>
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            v-else-if="item.rate < 0"
            style="color: #ff0000"
          >
            <path
              fill="currentColor"
              d="m12 18l-6-6l1.4-1.4l3.6 3.6V5h2v9.2l3.6-3.6L18 12z"
            />
          </svg>
          <span
            :class="{
              increase: item.rate > 0,
              decrease: item.rate < 0,
            }"
            >{{ item.rate }}%</span
          >
          {{ $t('rulePool.comparedLastMonth') }}
        </div>
      </div>
    </div>
    <div class="search-container">
      <div class="search-input">
        <el-input
          v-model="queryParams.keyword"
          :placeholder="$t('rulePool.searchPlaceholder')"
          clearable
          style="width: 100%"
          @keyup.enter="handleSearch"
          @clear="queryParams.keyword = null"
        >
          <div class="search-icon" slot="prefix">
            <svg width="24" height="24" viewBox="0 0 24 24">
              <path
                fill="currentColor"
                d="m19.6 21l-6.3-6.3q-.75.6-1.725.95T9.5 16q-2.725 0-4.612-1.888T3 9.5t1.888-4.612T9.5 3t4.613 1.888T16 9.5q0 1.1-.35 2.075T14.7 13.3l6.3 6.3zM9.5 14q1.875 0 3.188-1.312T14 9.5t-1.312-3.187T9.5 5T6.313 6.313T5 9.5t1.313 3.188T9.5 14"
              />
            </svg>
          </div>
        </el-input>
        <div class="search-button">
          <el-button type="primary" @click="handleSearch">{{
            $t('rulePool.search')
          }}</el-button>
        </div>
      </div>
      <div class="hot-search" v-if="hotList.length > 0">
        <span>{{ $t('rulePool.hotSearch') }}</span>
        <span
          class="hot-tag"
          @click="handleHotSearch(item)"
          v-for="(item, index) in hotList"
          :key="index"
          >{{ item }}</span
        >
      </div>
      <div class="search-options">
        <div class="search-options-header">
          <el-button type="primary" @click="handleSearch">{{
            $t('rulePool.applyFilter')
          }}</el-button>
          <el-button type="" @click="handleReset">{{
            $t('rulePool.resetFilter')
          }}</el-button>
          <el-button type="" @click="handleExport" :disabled="exportLoading">
            <svg
              v-if="!exportLoading"
              width="18"
              height="18"
              viewBox="0 0 24 24"
            >
              <g fill="currentColor" fill-rule="evenodd" clip-rule="evenodd">
                <path
                  d="M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25z"
                />
                <path
                  d="M9.657 15.874L7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0M17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2z"
                />
              </g>
            </svg>
            <i class="el-icon-loading" v-if="exportLoading" />
            {{ $t('rulePool.exportData') }}
          </el-button>
        </div>

        <div
          class="search-options-content"
          :style="{ height: openStatus ? 'auto' : '42px' }"
        >
          <el-form
            :model="queryParams"
            :label-width="isEnglish() ? '160px' : '100px'"
          >
            <el-form-item :label="$t('rulePool.strategyName') + '：'">
              <el-input
                v-model="queryParams.strategy"
                :placeholder="$t('rulePool.inputStrategyName')"
                clearable
                @clear="queryParams.strategy = null"
              />
            </el-form-item>
            <el-form-item :label="$t('rulePool.ruleStatus') + '：'">
              <el-select
                v-model="queryParams.status"
                :placeholder="$t('rulePool.selectRuleStatus')"
                clearable
                @clear="queryParams.status = null"
              >
                <el-option
                  v-for="(value, index) in statusOptions"
                  :key="index"
                  :label="value.label"
                  :value="value.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('rulePool.riskLevel') + '：'">
              <el-select
                v-model="queryParams.riskLevel"
                :placeholder="$t('rulePool.selectHitAction')"
                clearable
                @clear="queryParams.riskLevel = null"
              >
                <el-option
                  v-for="(value, index) in riskLevelOptions"
                  :key="index"
                  :label="value.label"
                  :value="value.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('rulePool.hitAction') + '：'">
              <el-select
                v-model="queryParams.hitAction"
                :placeholder="$t('rulePool.selectHitAction')"
                clearable
                @clear="queryParams.hitAction = null"
              >
                <el-option
                  v-for="(value, index) in hitActionOptions"
                  :key="index"
                  :label="value.label"
                  :value="value.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('rulePool.businessScene') + '：'">
              <el-select
                v-model="queryParams.businessScene"
                :placeholder="$t('rulePool.selectModelType')"
                clearable
                @clear="queryParams.businessScene = null"
              >
                <el-option
                  v-for="(value, index) in businessSceneOptions"
                  :key="index"
                  :label="value.name"
                  :value="value.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('rulePool.modelType') + '：'">
              <el-select
                v-model="queryParams.modelType"
                :placeholder="$t('rulePool.selectModelType')"
                clearable
                @clear="queryParams.modelType = null"
              >
                <el-option
                  v-for="(value, index) in modelTypeOptions"
                  :key="index"
                  :label="value.label"
                  :value="value.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('rulePool.productName') + '：'">
              <el-input
                v-model="queryParams.referenceProduct"
                clearable
                @clear="queryParams.referenceProduct = null"
                :placeholder="$t('rulePool.inputProductName')"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('rulePool.timeRange') + '：'">
              <el-date-picker
                v-model="timeRange"
                type="daterange"
                value-format="yyyy-MM-dd"
                range-separator="-"
                :start-placeholder="$t('rulePool.startDate')"
                :end-placeholder="$t('rulePool.endDate')"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="openBtn" @click="handleOpenSearch">
        <div>
          <svg v-if="!openStatus" width="16" height="16" viewBox="0 0 24 24">
            <path
              fill="none"
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M10 5H3m9 14H3M14 3v4m2 10v4m5-9h-9m9 7h-5m5-14h-7m-6 5v4m0-2H3"
            />
          </svg>
          <svg v-else width="18" height="18" viewBox="0 0 24 24">
            <path
              fill="currentColor"
              fill-rule="evenodd"
              d="M11.512 8.43a.75.75 0 0 1 .976 0l7 6a.75.75 0 1 1-.976 1.14L12 9.987l-6.512 5.581a.75.75 0 1 1-.976-1.138z"
              clip-rule="evenodd"
            />
          </svg>
          <span>{{
            openStatus
              ? $t('rulePool.collapseFilter')
              : $t('rulePool.moreFilter')
          }}</span>
        </div>
        <svg width="140" height="35" viewBox="0 0 140 35">
          <path
            d="M 20,35 L 120,35 Q 135,20 140,0 L 0,0 Q 5,20 20,35 Z"
            fill="#fff"
            stroke-width="1"
          />
        </svg>
      </div>
    </div>
    <div class="table-container">
      <div class="table-container-header">
        <span class="table-container-header-title">{{
          $t('rulePool.ruleList')
        }}</span>
        <div>
          {{ $t('rulePool.totalRules', { total }) }}
        </div>
      </div>
      <div class="table-container-table">
        <GutuTable
          :dataList="dataList"
          :loading="loading"
          :columnConfig="columnConfig"
          :collectionList="collectionList"
          :query="params"
          :selection="true"
          :tableHeight="300"
          :handle="tableHandle"
          @handleSelection="handleSelection"
        >
          <template #bt_handle="{ data }">
            <div class="bt_handle">
              <el-button
                type="text"
                size="medium"
                @click="handleDetails(data.row)"
              >
                {{ $t('rulePool.viewDetails') }}
              </el-button>
            </div>
          </template>
        </GutuTable>
      </div>
      <div class="pagination">
        <GutuPagination
          style="margin-top: 10px"
          ref="gutuPaginationRef"
          layout="sizes, prev, pager, next, jumper"
          :total="total"
          @searchData="updateParams"
        />
      </div>
    </div>
    <el-drawer
      v-if="currentRow"
      :title="$t('rulePool.viewRule')"
      :visible.sync="drawer.visible"
      direction="rtl"
      :modal="false"
      :wrapperClosable="false"
      :size="drawer.width"
    >
      <addOrEditRule
        v-if="![$t('rulePool.score')].includes(currentRow.modelType)"
        :info="currentRow"
        :readOnly="true"
        @close="drawer.visible = false"
      ></addOrEditRule>
      <addOrEditScoreRule
        v-else
        :formData="currentRow"
        :readOnly="true"
        @close="drawer.visible = false"
      ></addOrEditScoreRule>
    </el-drawer>
  </div>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import AddOrEditRule from '@/views/decisionPlatform/modules/productDecision/components/addOrEditRule.vue'
import AddOrEditScoreRule from '@/views/decisionPlatform/modules/productDecision/components/score/ruleAdd.vue'
import {
  getRulePoolGage,
  getRulePoolStats,
  getRulePoolHotKeywords,
  getRulePoolExport,
} from './api'
import { business_search } from '@/views/decisionPlatform/modules/productDecision/api'
import { getFileNameFromHeaders } from '@/utils/rouyi'
import { getDicts } from '@/api'

export default {
  components: {
    GutuTable,
    GutuPagination,
    AddOrEditRule,
    AddOrEditScoreRule,
  },
  data() {
    return {
      drawer: {
        title: '',
        visible: false,
        width: '1200px',
      },
      queryParams: {
        keyword: null,
        status: null,
        riskLevel: null,
        strategy: null,
        hitAction: null,
        businessScene: null,
        modelType: null,
        referenceProduct: null,
        createTimeStart: null,
        createTimeEnd: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      timeRange: [],
      hotList: [],
      businessSceneOptions: [],
      openStatus: false,
      dataList: [],
      total: 0,
      loading: false,
      collectionList: {},
      selectRowMap: new Map(),
      boardData: {},
      exportLoading: false,
      currentRow: null,
    }
  },
  computed: {
    boardList() {
      return [
        {
          label: this.$t('rulePool.totalRulesCount'),
          field: 'totalRules',
          value: this.boardData.totalRules || 0,
          rateField: 'totalRulesRate',
          rate: this.boardData.totalRulesRate || 0,
        },
        {
          label: this.$t('rulePool.activeRules'),
          field: 'activeRules',
          value: this.boardData.activeRules || 0,
          rateField: 'activeRulesRate',
          rate: this.boardData.activeRulesRate || 0,
        },
        {
          label: this.$t('rulePool.productCount'),
          field: 'productCount',
          value: this.boardData.productCount || 0,
          rateField: 'productCountRate',
          rate: this.boardData.productCountRate || 0,
        },
        {
          label: this.$t('rulePool.sceneCount'),
          field: 'sceneCount',
          value: this.boardData.sceneCount || 0,
          rateField: 'sceneCountRate',
          rate: this.boardData.sceneCountRate || 0,
        },
      ]
    },
    statusOptions() {
      return [
        { label: this.$t('rulePool.enabled'), value: 1 },
        { label: this.$t('rulePool.disabled'), value: 2 },
      ]
    },
    riskLevelOptions() {
      return [
        { label: this.$t('rulePool.lowRisk'), value: 1 },
        { label: this.$t('rulePool.mediumLowRisk'), value: 2 },
        { label: this.$t('rulePool.mediumRisk'), value: 3 },
        { label: this.$t('rulePool.mediumHighRisk'), value: 4 },
        { label: this.$t('rulePool.highRisk'), value: 5 },
      ]
    },
    hitActionOptions() {
      return [
        { label: this.$t('rulePool.pass'), value: this.$t('rulePool.pass') },
        {
          label: this.$t('rulePool.reject'),
          value: this.$t('rulePool.reject'),
        },
        {
          label: this.$t('rulePool.transferToManual'),
          value: this.$t('rulePool.transferToManual'),
        },
      ]
    },
    modelTypeOptions() {
      return [
        { label: this.$t('rulePool.score'), value: 1 },
        { label: this.$t('rulePool.rule'), value: 5 },
        { label: this.$t('rulePool.classification'), value: 6 },
      ]
    },
    columnConfig() {
      return [
        { label: this.$t('rulePool.strategy'), field: 'strategy', width: 180 },
        {
          label: this.$t('rulePool.ruleGroup'),
          field: 'ruleGroup',
          width: 350,
        },
        { label: this.$t('rulePool.ruleCode'), field: 'ruleCode', width: 180 },
        { label: this.$t('rulePool.ruleDesc'), field: 'ruleDesc', width: 500 },
        {
          label: this.$t('rulePool.riskLevelLabel'),
          field: 'riskLevel',
          width: 180,
        },
        {
          label: this.$t('rulePool.hitActionLabel'),
          field: 'hitAction',
          width: 180,
        },
        { label: this.$t('rulePool.statusLabel'), field: 'status', width: 180 },
        {
          label: this.$t('rulePool.referenceProduct'),
          field: 'referenceProduct',
          width: 180,
        },
        {
          label: this.$t('rulePool.businessSceneLabel'),
          field: 'businessScene',
          width: 180,
        },
        {
          label: this.$t('rulePool.modelTypeLabel'),
          field: 'modelType',
          width: 180,
        },
        {
          label: this.$t('rulePool.createTimeLabel'),
          width: 180,
          field: 'createTime',
          type: 'timeStamp',
        },
      ]
    },
    tableHandle() {
      return {
        fixed: 'right',
        width: '140px',
        label: this.$t('rulePool.operation'),
        align: 'left',
        slot: true,
      }
    },
  },
  watch: {
    timeRange: {
      handler(val) {
        if (val.length) {
          this.queryParams.createTimeStart = val[0]
          this.queryParams.createTimeEnd = val[1]
        } else {
          this.queryParams.createTimeStart = null
          this.queryParams.createTimeEnd = null
        }
      },
    },
  },
  mounted() {
    this.init()
    this.handleSearch()
  },
  methods: {
    getFileNameFromHeaders,
    init() {
      getRulePoolStats().then((res) => {
        if (res.code === 200) {
          // 更新 boardData，computed 的 boardList 会自动更新
          this.boardData = {
            totalRules: res.data.totalRules,
            totalRulesRate: res.data.totalRulesRate,
            activeRules: res.data.activeRules,
            activeRulesRate: res.data.activeRulesRate,
            productCount: res.data.productCount,
            productCountRate: res.data.productCountRate,
            sceneCount: res.data.sceneCount,
            sceneCountRate: res.data.sceneCountRate,
          }
        }
      })
      getRulePoolHotKeywords().then((res) => {
        if (res.code === 200) {
          this.hotList = res.data
        }
      })
      business_search().then((res) => {
        if (res.code === 200) {
          this.businessSceneOptions = res.data.list
        }
      })

      getDicts('decision_standard').then((res) => {
        const arr = res.data.map((item) => {
          return {
            dictLabel: item.dictLabel,
            dictValueList: JSON.parse(item.dictValue),
          }
        })
        let arr2 = []
        arr.forEach((item) => {
          arr2 = arr2.concat(item.dictValueList)
        })
        sessionStorage.setItem('decisionStandard', JSON.stringify(arr2))
      })
    },
    handleSearch() {
      this.loading = true
      getRulePoolGage({ ...this.queryParams, ...this.params }).then((res) => {
        if (res.code === 200) {
          this.dataList = res.data.list
          this.total = res.data.total
          this.loading = false
        }
      })
    },
    handleReset() {
      this.queryParams = this.$options.data().queryParams
      this.params = this.$options.data().params
      this.timeRange = []
      if (this.$refs.gutuPaginationRef) {
        this.$refs.gutuPaginationRef.reset()
      }
    },
    handleExport() {
      let list = Array.from(this.selectRowMap.values())
      let params = null
      if (list.length > 0) {
        params = {
          ids: list.map((item) => item.id),
        }
      } else {
        params = {
          ...this.queryParams,
          ...this.params,
        }
      }
      this.exportLoading = true
      getRulePoolExport(params).then((res) => {
        this.exportLoading = false
        const fileName =
          this.getFileNameFromHeaders(res.headers) ||
          this.$t('rulePool.versionCompare')
        this.handleDownload(res.data, fileName)
      })
    },
    handleDownload(file, fileName) {
      const blob = new Blob([file], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8',
      })
      const url = URL.createObjectURL(blob)

      const a = document.createElement('a')
      a.href = url
      a.download = fileName // 文件名

      // 4. 触发下载
      document.body.appendChild(a)
      a.click()

      // 5. 清理
      setTimeout(() => {
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
      }, 100)
    },
    handleSelection(data) {
      this.dataList.forEach((item) => {
        if (this.selectRowMap.has(item.id) && !data.includes(item)) {
          this.selectRowMap.delete(item.id)
        }
      })
      data.forEach((item) => {
        this.selectRowMap.set(item.id, item)
      })
    },
    handleDetails(data) {
      console.log(data, 'data')

      this.currentRow = { ...data, codeId: data.id }
      this.drawer.visible = true
    },
    handleHotSearch(item) {
      this.queryParams.keyword = item
      this.handleSearch()
    },
    handleOpenSearch() {
      this.openStatus = !this.openStatus
    },
    updateParams(data) {
      this.params = data
      this.handleSearch()
    },
  },
}
</script>
<style lang="less" scoped>
.strategy-rule-pool {
  width: 100%;
  height: calc(var(--bgvh) - 42px);
  padding: 20px;
  font-family: 'PingFang SC-Medium';
  overflow-y: auto;

  .board-container {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    &-item {
      background: #fff;
      padding: 10px;
      border-radius: 12px;
      box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
      &-label {
        font-size: 14px;
        color: #333;
        margin-bottom: 4px;
      }
      &-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        margin-bottom: 12px;
      }
      &-compared {
        font-size: 14px;
        color: #c6acaf;
        display: flex;
        align-items: center;
        gap: 4px;
        .increase {
          color: #1cd400;
        }
        .decrease {
          color: #ff0000;
        }
      }
      .line {
        width: 100%;
        border-radius: 10px;
        margin-bottom: 8px;
        ::v-deep.el-progress-bar {
          height: 8px;
        }
      }
    }
  }

  .search-container {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    background: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
    position: relative;

    .search-input {
      position: relative;
      ::v-deep .el-input {
        .el-input__inner {
          height: 50px !important;
          padding-left: 34px;
          padding-right: 100px;
        }
        .el-input__suffix {
          right: 80px;
        }
      }
    }

    .search-icon {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #c6acaf;
    }
    .search-button {
      position: absolute;
      right: 0;
      top: 0;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 10px;
      .el-button {
        height: 32px;
        font-size: 14px;
        border-radius: 8px;
      }
    }

    .hot-search {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      font-size: 14px;
      margin: 8px 0px;

      .hot-tag {
        background: rgba(#2888e8, 0.1);
        padding: 4px 8px;
        border-radius: 4px;
        margin-right: 8px;
        color: #2888e8;
        cursor: pointer;
        &:hover {
          background: rgba(#2888e8, 0.2);
        }
      }
    }

    .search-options {
      margin-top: 20px;

      &-header {
        display: flex;
        align-items: center;
        .el-button {
          padding: 0 20px;
          height: 40px;
          font-size: 14px;
          border-radius: 8px;
        }
      }

      &-content {
        margin-top: 10px;
        overflow: hidden;

        ::v-deep .el-form {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 10px;

          .el-form-item {
            margin-bottom: 0;
            .el-form-item__label {
              line-height: 40px;
            }
            .el-form-item__content {
              .el-select {
                width: 100%;
              }
              .el-date-editor {
                height: 42px;
                .el-range-separator {
                  line-height: 32px;
                  width: 30px;
                }
              }
            }
          }
        }
      }
    }
  }

  .openBtn {
    position: absolute;
    bottom: -30px;
    left: 50%;
    transform: translate(-50%, 0);
    > svg {
      width: 140px;
      height: 30px;
    }
    div {
      position: absolute;
      width: 100%;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      span {
        font-size: 12px;
        margin-left: 4px;
      }
    }

    .rotate180 {
      transform: rotate(180deg);
    }
  }

  .table-container {
    margin-top: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
    .table-container-header {
      padding: 20px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .table-container-table {
      padding: 0px 20px 10px;
    }

    .pagination {
      padding-bottom: 20px;
    }
  }
}
</style>
