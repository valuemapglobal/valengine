<template>
  <div class="risk-report" v-loading="loading">
    <div class="rr-header">
      <div class="rr-header-title">
        <img class="logo" src="/ico/xt.ico" alt="" />
        评估引擎详细分析
      </div>
      <div class="rr-header-operation">
        <!-- <el-button type="primary" icon="el-icon-download">下载报告</el-button> -->
        <el-button icon="el-icon-refresh" @click="getDataInfo">刷新</el-button>
      </div>
    </div>
    <div class="rr-content">
      <div class="rr-content-card" v-if="baseInfo">
        <div class="rr-content-card-title">任务基本信息</div>
        <RowData
          :data="baseInfo"
          :configList="baseInfoConfig"
          sortOrder="column"
          dataSortOrder="column"
        />
      </div>
      <div class="rr-content-card evaluationProcess" v-if="decisionInfo">
        <div class="ep-header">
          <div class="flex-col">
            <span class="ep-header-title">用户风险评估流程</span>
            <span class="ep-header-time">
              评估周期：{{ parseTime(decisionInfo.startTime) }} 至
              {{ parseTime(decisionInfo.endTime) }}
            </span>
          </div>
          <div class="flex ep-header-data">
            <RowData
              :data="decisionInfo"
              :configList="decisionInfoConfig"
              sortOrder="column"
              dataSortOrder="column"
              isCenter
            />
          </div>
        </div>
        <div class="flex justify-between">
          <div
            style="width: calc(70% - 10px)"
            class="rr-content-card innerCard flex-col align-center"
          >
            <div class="innerCard-title flex justify-between">
              <div>风险等级分布</div>
              <div>基于{{ decisionInfo.modelCount }}个模型</div>
            </div>
            <div class="ep-distribution">
              <div class="ep-distribution-chart">
                <div
                  class="ep-distribution-chart_mask"
                  :style="{ width: `${decisionInfo.percentage}%` }"
                />
              </div>
              <div class="ep-distribution-info">
                <span>低风险</span>
                <span>中风险</span>
                <span>高风险</span>
              </div>
            </div>
          </div>
          <div
            style="width: calc(30% - 10px)"
            class="rr-content-card innerCard"
          >
            <div class="innerCard-title flex justify-between">
              <div>模型执行结果</div>
              <div>基于{{ decisionInfo.modelCount }}个模型</div>
            </div>
            <div class="result-list flex">
              <div
                class="result-item flex-col align-center"
                v-for="(item, index) in resultList"
                :style="{
                  width: `calc((100% - 40px) / ${resultList.length})`,
                  background: item.style.background,
                }"
              >
                <span :style="{ color: item.style.color }">{{
                  item.data
                }}</span>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="rr-content-card innerCard">
          <div class="innerCard-title flex justify-between">
            <div>独立风险池评分详情</div>
            <div>预警</div>
          </div>
          <div class="ep-fraction">
            <div
              class="ep-fraction-item"
              v-for="(item, index) in fractionList"
              :key="index"
            >
              <span>{{ item.label }}</span>
              <span class="ep-fraction-item_data">{{ item.data }}</span>
            </div>
          </div>
          <div class="ep-deduct">
            <div
              class="ep-deduct-item"
              v-for="(item, index) in deductList"
              :key="index"
            >
              <span>{{ item.label }}</span>
              <span class="ep-deduct-item_data"
                >{{ item.data }}{{ item.unit }}</span
              >
            </div>
          </div>
        </div>
        <div class="flex ep-decision">
          <div class="flex ep-decision-item">
            <i class="el-icon-success" />
            <div class="flex-col">
              <span class="ep-decision-label">开始时间</span>
              <span class="ep-decision-value">{{
                parseTime(decisionInfo?.startTime)
              }}</span>
            </div>
          </div>
          <div class="flex ep-decision-item">
            <i class="el-icon-success" />
            <div class="flex-col">
              <span class="ep-decision-label">结束时间</span>
              <span class="ep-decision-value">{{
                parseTime(decisionInfo?.endTime)
              }}</span>
            </div>
          </div>
          <div class="flex ep-decision-item">
            <i class="el-icon-info" style="color: var(--primary-color)" />
            <div class="flex-col">
              <span class="ep-decision-label">最终决策</span>
              <span class="ep-decision-value">
                {{ decisionInfo?.finalStatus }} -
                {{ decisionInfo?.reviewStatus }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="rr-strategy">
        <div
          style="width: calc(50% - 10px)"
          class="rr-content-card"
          v-if="processStrategyInfo"
        >
          <div class="innerCard-title">流程策略详情</div>
          <div class="flex-col justify-between">
            <div
              class="flex justify-between strategy-item"
              v-for="(item, index) in strategyDetails"
              :key="index"
            >
              <span>{{ item.label }}</span>
              <span v-if="item.type == 'timeStamp'" class="strategy-item_value">
                {{
                  processStrategyInfo[item.field]
                    ? parseTime(processStrategyInfo[item.field])
                    : '-'
                }}
              </span>
              <span v-else class="strategy-item_value">{{
                processStrategyInfo[item.field] || '-'
              }}</span>
            </div>
          </div>
          <div class="strategy-description flex-col">
            <span class="strategy-description_label">策略描述</span>
            <span>{{ processStrategyInfo.description || '-' }}</span>
          </div>
        </div>
        <div
          style="width: calc(50% - 10px)"
          class="rr-content-card"
          v-if="productInfo"
        >
          <div class="innerCard-title">产品信息</div>
          <div class="flex-col justify-between">
            <div
              class="flex justify-between strategy-item"
              v-for="(item, index) in productInfoDetails"
              :key="index"
            >
              <span>{{ item.label }}</span>
              <span v-if="item.type == 'timeStamp'" class="strategy-item_value">
                {{
                  productInfo[item.field]
                    ? parseTime(productInfo[item.field])
                    : '-'
                }}
              </span>
              <span v-else class="strategy-item_value">{{
                productInfo[item.field] || '-'
              }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="rr-model" v-if="moduleList.length > 0">
        <div class="rr-model-title">模型执行详情</div>
        <div class="rr-module-list">
          <div
            class="rr-module-item"
            v-for="(item, index) in moduleList"
            :key="index"
          >
            <div class="step">{{ index + 1 }}</div>
            <div class="rr-content-card">
              <div class="line" v-if="index !== moduleList.length - 1" />
              <div class="rr-module-item-title">
                <span>{{ item.moduleTitle }}</span>
              </div>
              <div class="flex justify-between">
                <div class="model-type">{{ item.modelType }}</div>
                <div class="model-status">
                  <span :style="statusStyle[item.status]">{{
                    item.status
                  }}</span>
                  <span>{{ (Number(item.duration) / 1000).toFixed(1) }}秒</span>
                </div>
              </div>
              <div class="model-info">
                <RowData
                  :data="item"
                  :configList="handleModuleConfig(item)"
                  sortOrder="column"
                  dataSortOrder="column"
                  :valueSize="14"
                />
              </div>
              <div class="innerCard">
                <div class="innerCard-title">执行结果</div>
                <GutuTable
                  v-if="item.modelType != '分类模型'"
                  :dataList="item.details"
                  :columnConfig="handleColumnConfig(item)"
                />
                <div v-else>
                  <GutuTable
                    class="classify-table"
                    v-for="(itemX, indexX) in item.details"
                    :key="indexX"
                    :dataList="itemX.dynamicData"
                    :columnConfig="handleClassifyColumnConfig(itemX)"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="rr-content-card rr-decision">
        <div class="rr-content-card-title">决策详情</div>
        <div class="rr-decision-content">
          <div style="width: calc(50% - 10px)">
            <div class="title">是否转人工</div>
            <div class="innerCard">
              {{ decisionInfo?.needManualReview ? '是' : '否' }}
            </div>
          </div>
          <div style="width: calc(50% - 10px)">
            <div class="title">决策条件</div>
            <div class="innerCard flex-col">
              <div
                class="flex-col decision-item"
                v-for="(item, index) in decisionConditions"
                :key="index"
              >
                <span>{{ item.label }}：</span>
                <span v-for="(item2, index2) in item.value" :key="index2">
                  · {{ item2 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="rr-content-card rr-analysis" v-if="analysisRecommendation">
        <div class="rr-content-card-title">风控综合分析</div>
        <div class="innerCard rr-analysis-content">
          <div class="flex-col">
            <div class="analysis-title flex">
              <i class="el-icon-info" />分析建议
            </div>
            <div v-for="(item, index) in analysisList" :key="index">
              <div>
                <span>{{ index + 1 }}、{{ item.label }}：</span>
                <span v-if="item.type == 'list'"
                  >{{
                    analysisRecommendation[item.field].join('、') || '-'
                  }}。</span
                >
                <span v-else>{{
                  analysisRecommendation[item.field] || '-'
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import RowData from '@/components/RowData.vue'
import GutuTable from '@/components/gutu/gutuTable.vue'
import { parseTime } from '@/utils/rouyi'
import { reportResponse } from '@/views/platformEngine/api/platformEngine'

export default {
  components: {
    RowData,
    GutuTable,
  },
  data() {
    return {
      taskNo: null,
      baseInfo: null, // 任务基本信息
      decisionInfo: null, // 用户风险评估流程
      processStrategyInfo: null, // 流程策略详情
      productInfo: null, // 产品信息
      moduleList: [], // 模型执行详情
      decisionConditions: [], // 决策详情
      analysisRecommendation: null, // 风控综合分析

      loading: false,

      baseInfoConfig: [
        {
          title: [],
          row: 4,
          fieldList: [
            { label: '任务编号', field: 'taskNo' },
            { label: '申请用户', field: 'applicationUser' },
            { label: '所属部门', field: 'deptName' },
            { label: '业务场景', field: 'businessScenario' },
            { label: '产品名称', field: 'productName' },
            { label: '流程策略', field: 'processStrategy' },
            { label: '任务状态', field: 'taskStatusDesc' },
            { label: '响应形式', field: 'responseFormDesc' },
          ],
        },
      ],

      decisionInfoConfig: [
        {
          title: [],
          row: 3,
          fieldList: [
            { label: '总耗时', field: 'totalDuration' },
            { label: '模型数量', field: 'modelCount' },
            { label: '风险评分', field: 'finalScore', unit: '分' },
          ],
        },
      ],

      resultList: [
        {
          label: '通过',
          data: 0,
          style: { color: '#155724', background: '#D4EDDA' },
        },
        {
          label: '预警',
          data: 0,
          style: { color: '#856404', background: '#FFF3CD' },
        },
        {
          label: '拒绝',
          data: 0,
          style: { color: '#A71D2A', background: '#F8D7DA' },
        },
      ],

      fractionList: [
        { label: '初始分数', data: '-', field: 'initialScore' },
        { label: '最终得分', data: '-', field: 'finalScore' },
        { label: '总扣分', data: '-', field: 'totalDeduction' },
        { label: '评分等级', data: '-', field: 'scoreLevel' },
      ],
      deductList: [
        {
          label: '评分模型扣分',
          data: '-',
          field: 'scoreModelDeduction',
          unit: '分',
        },
        {
          label: '规则模型扣分',
          data: '-',
          field: 'ruleModelDeduction',
          unit: '分',
        },
        {
          label: '分类模型扣分',
          data: '-',
          field: 'categoryModelDeduction',
          unit: '分',
        },
      ],
      strategyDetails: [
        { label: '流程策略', field: 'strategyName' },
        { label: '业务场景', field: 'businessScenario' },
        { label: '产品名称', field: 'productName' },
        { label: '启用状态', field: 'status' },
        { label: '创建时间', field: 'createTime', type: 'timeStamp' },
        { label: '最后更新', field: 'updateTime', type: 'timeStamp' },
      ],
      productInfoDetails: [
        { label: '产品名称', field: 'productName' },
        { label: '产品ID', field: 'productId' },
        { label: '所属部门', field: 'deptName' },
        { label: '产品类型', field: 'productType' },
        { label: '创建时间', field: 'createTime', type: 'timeStamp' },
        { label: '最后更新', field: 'updateTime', type: 'timeStamp' },
      ],

      statusStyle: {
        拒绝: { color: '#A71D2A', background: '#F8D7DA' },
        预警: { color: '#856404', background: '#FFF3CD' },
        通过: { color: '#155724', background: '#D4EDDA' },
      },

      ruleColumnConfig: [
        { label: '序号', type: 'serial', width: 60 },
        { label: '风险等级', field: 'riskLevel' },
        { label: '规则代码', field: 'ruleCode' },
        { label: '命中规则内容', field: 'hitAction' },
      ],
      ratingColumnConfig: [
        { label: '序号', type: 'serial', width: 60 },
        { label: '规则代码', field: 'ruleCode' },
        { label: '维度', field: 'dimension' },
        { label: '命中规则内容', field: 'hitAction' },
      ],

      analysisList: [
        { label: '整体评估', field: 'overallAssessment' },
        { label: '风险警告', field: 'riskWarning' },
        { label: '处理建议', field: 'suggestion' },
        { label: '后续行动', field: 'followUpActions' },
        { label: '关键风险因素', field: 'keyRiskFactors', type: 'list' },
        { label: '具体建议', field: 'recommendations', type: 'list' },
      ],
    }
  },
  mounted() {
    let taskNo = this.$route.query.taskNo
    this.taskNo = taskNo
    this.getDataInfo()
  },
  methods: {
    parseTime,
    getDataInfo() {
      this.handleReset()
      this.loading = true
      reportResponse({
        taskNo: this.taskNo,
      }).then((res) => {
        if (res.code == 200) {
          let data = res.data
          this.baseInfo = data
          this.decisionInfo = {
            ...data?.decisionInfo,
            ...data?.totalScoreInfo,
            percentage: (Number(data?.totalScoreInfo?.finalScore) / 950) * 100,
            modelCount: data?.modules?.length,
          }
          if (this.decisionInfo?.decisionConditions) {
            let obj = this.decisionInfo?.decisionConditions
            for (let key in obj) {
              this.decisionConditions.push({
                label: key,
                value: obj[key],
              })
            }
          }
          this.fractionList.forEach((item) => {
            if (!['', null, undefined].includes(this.decisionInfo[item.field]))
              item.data = this.decisionInfo[item.field]
          })
          this.deductList.forEach((item) => {
            if (!['', null, undefined].includes(this.decisionInfo[item.field]))
              item.data = this.decisionInfo[item.field]
          })

          this.processStrategyInfo = data?.processStrategyInfo
          this.productInfo = data?.productInfo
          this.moduleList = data?.modules
          if (data?.modules) {
            let result = {
              通过: 0,
              预警: 1,
              拒绝: 2,
            }
            this.modelList = data?.modules
            data?.modules.forEach((item) => {
              this.resultList[result[item.status]].data++
            })
          }

          this.analysisRecommendation = data?.analysisRecommendation
        }
        this.loading = false
      })
    },
    handleReset() {
      this.baseInfo = null
      this.decisionInfo = null
      this.processStrategyInfo = null
      this.productInfo = null
      this.moduleList = []
      this.decisionConditions = []
      this.analysisRecommendation = null
    },
    handleModuleConfig(item) {
      let list = [
        {
          title: [],
          row: 5,
          fieldList: [
            { label: '开始', field: 'startTime', type: 'timeStamp' },
            { label: '结束', field: 'endTime', type: 'timeStamp' },
            { label: '模型ID', field: 'modelId' },
            { label: '模型类型', field: 'modelType' },
          ],
        },
      ]
      if (item.modelType === '评分模型') {
        list[0].fieldList.push({
          label: '评分',
          field: 'finalScore',
          unit: '分',
        })
      }
      return list
    },
    handleColumnConfig(item) {
      switch (item.modelType) {
        case '评分模型':
          return this.ratingColumnConfig
        case '规则模型':
          return this.ruleColumnConfig
      }
    },
    handleClassifyColumnConfig(item) {
      let dynamicHeaders = item?.dynamicHeaders || []
      let list = []
      dynamicHeaders.forEach((item) => {
        list.push({
          label: item.fieldName,
          field: item.field,
        })
      })
      return list
    },
  },
}
</script>
<style lang="less" scoped>
.risk-report {
  height: calc(var(--bgvh) - 42px);
  width: 100%;
  margin: 0px auto;

  .rr-header {
    height: 42px;
    background-color: #fff;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .rr-header-title {
      font-size: 16px;
      font-weight: 600;

      .logo {
        width: 36px;
        height: 36px;
        margin-right: 15px;
        cursor: pointer;
      }
    }

    .rr-header-operation {
      display: flex;
      align-items: center;

      .el-button {
        font-size: 14px;
        border-radius: 8px;
      }
    }
  }

  .rr-content {
    height: calc(var(--bgvh) - 42px);
    width: 100%;
    padding: 20px;

    &-card {
      width: 100%;
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 16px;

      &-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
      }
    }
  }
}

.evaluationProcess {
  .ep-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    &-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 8px;
    }

    &-time {
      font-size: 14px;
      color: rgba(#000, 0.6);
    }

    &-data {
      width: 240px;
    }
  }

  .ep-distribution {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    width: 70%;
    height: 50px;

    &-chart {
      position: relative;
      width: 100%;
      height: 10px;
      background: linear-gradient(
        to right,
        #00ff00,
        #ffff00 50%,
        #ffff00 50%,
        #ff0000
      );
      margin-bottom: 10px;
      border-radius: 20px;

      &_mask {
        position: absolute;
        top: 0;
        right: 0;
        height: 100%;
        background-color: #d7d7d7;
        border-radius: 20px;

        &::before {
          content: '';
          position: absolute;
          top: 50%;
          transform: translateY(-50%);
          left: -10px;
          width: 20px;
          height: 20px;
          border-radius: 50%;
          border: 4px solid var(--primary-color);
          background: #fff;
        }
      }
    }

    &-info {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;

      > span {
        font-size: 14px;
        color: rgba(#000, 0.6);
      }
    }
  }

  .result-list {
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .result-item {
      display: flex;
      align-items: center;
      font-size: 14px;
      padding: 4px 0px;
      border-radius: 8px;
    }
  }

  .ep-fraction,
  .ep-deduct {
    width: 100%;
    display: flex;
    justify-content: space-around;
    align-items: center;

    &-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      color: rgba(#000, 0.6);
      font-size: 14px;

      &_data {
        font-size: 16px;
        color: #000;
      }
    }
  }

  .ep-fraction {
    margin-bottom: 20px;
    border-bottom: 1px solid rgba(#000, 0.1);
    padding-bottom: 20px;
  }

  .ep-decision {
    &-item {
      margin-right: 20px;
    }

    &-label {
      font-size: 16px;
      color: #000;
    }

    &-value {
      font-size: 14px;
      color: rgba(#000, 0.6);
    }

    i {
      font-size: 30px;
      color: #10b981;
      margin-right: 8px;
    }
  }
}

.rr-strategy {
  display: flex;
  justify-content: space-between;
  align-items: stretch;

  .strategy-description {
    margin-top: 20px;
    border-top: 1px solid rgba(#000, 0.1);
    padding-top: 10px;
    font-size: 14px;
    color: rgba(#000, 0.6);

    &_label {
      font-size: 16px;
      color: #000;
      margin-bottom: 10px;
    }
  }

  .strategy-item {
    font-size: 14px;
    color: rgba(#000, 0.6);
    margin-bottom: 10px;

    &_value {
      font-size: 16px;
      color: #000;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.rr-model {
  width: 100%;

  &-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
  }

  .rr-module-list {
    width: 100%;
    display: flex;
    flex-direction: column;

    .rr-module-item {
      width: 100%;
      display: flex;
      align-items: self-start;
      position: relative;

      &-title {
        font-size: 16px;
        color: #000;
        margin-bottom: 10px;
      }

      .line {
        position: absolute;
        top: 0;
        left: 10px;
        width: 2px;
        height: 100%;
        background-color: rgba(#000, 0.1);
        z-index: 1;
      }

      .step {
        width: 24px;
        height: 24px;
        background-color: var(--primary-color);
        border-radius: 50%;
        margin-right: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #fff;
        z-index: 2;
      }

      .model-type {
        font-size: 14px;
        color: rgba(#000, 0.6);
        background: var(--primary-color-lighter);
        padding: 4px 8px;
        border-radius: 4px;
        color: var(--primary-color);
      }

      .model-status {
        font-size: 14px;
        color: rgba(#000, 0.6);

        span {
          padding: 2px 8px;
          border-radius: 4px;
        }
      }

      .model-info {
        margin: 10px 0;
      }
    }
  }
}

.classify-table {
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
}

.rr-decision {
  &-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    .title {
      margin-bottom: 10px;
      font-size: 14px;
      color: #000;
    }

    .decision-item {
      font-size: 14px;
      color: rgba(#000, 0.6);
      margin-bottom: 10px;
      span:first-child {
        color: #000;
      }
    }
  }
}

.rr-analysis {
  &-content {
    width: 100%;
    display: flex;
    align-items: flex-start;
    font-size: 14px;
    color: rgba(#000, 0.6);

    .analysis-title {
      color: var(--primary-color);
      margin-bottom: 10px;
      i {
        font-size: 20px;
        margin-right: 10px;
      }
    }
  }
}

.innerCard {
  padding: 16px !important;
  background: rgba(var(--primary-color), 0.03) !important;

  &-title {
    width: 100%;
    margin-bottom: 20px;
    font-size: 14px;
    color: #000;

    > div:last-child {
      color: rgba(#000, 0.6);
    }
  }
}

.flex {
  display: flex;
  align-items: center;
}

.flex-col {
  display: flex;
  flex-direction: column;
}

.justify-between {
  justify-content: space-between;
}

.justify-around {
  justify-content: space-around;
}

.align-center {
  align-items: center;
}
</style>
