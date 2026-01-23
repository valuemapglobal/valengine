<template>
  <div class="history-task">
    <div class="history-task-search">
      <el-input
        v-model="queryParams.taskNo"
        placeholder="请输入任务编号"
        clearable
        @clear="queryParams.taskNo = null"
      ></el-input>

      <el-select
        v-model="queryParams.productId"
        placeholder="请选择产品"
        clearable
        filterable
        @clear="queryParams.productId = null"
      >
        <el-option
          v-for="item in productOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-select
        v-model="queryParams.businessScene"
        placeholder="请选择业务场景"
        clearable
        filterable
        @clear="queryParams.businessScene = null"
      >
        <el-option
          v-for="item in collectionList.businessCodeOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <!-- <el-select
        v-model="queryParams.modelName"
        placeholder="请选择模型类型"
        clearable
        filterable
        @clear="queryParams.modelName = null"
      >
        <el-option
          v-for="item in modelTypeOptions"
          :key="item.value"
          :label="item.label"
          :value="item.label"
        >
        </el-option>
      </el-select> -->
      <el-input
        v-model="queryParams.modelName"
        placeholder="请输入模型名称"
        size="normal"
        clearable
        @clear="queryParams.modelName = null"
      ></el-input>

      <!-- <el-select
        v-model="queryParams.versionId"
        placeholder="请选择版本号"
        clearable
        filterable
        @clear="queryParams.versionId = null"
      >
        <el-option
          v-for="item in versionOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select> -->
      <el-button icon="el-icon-refresh" @click="resetParams">重置</el-button>
      <el-button type="primary" icon="el-icon-search" @click="getDataList"
        >搜索</el-button
      >
      <el-button type="primary" @click="handleSubmit(false)"
        >回溯任务提交</el-button
      >
    </div>
    <main>
      <GutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :tableHeight="240"
        :params="params"
        selection
        :collectionList="collectionList"
        @handleSelection="handleSelection"
      >
        <template slot="slot_expectedResult" slot-scope="scope">
          <el-input
            v-model="scope.row.expectedResult"
            clearable
            placeholder="350-950"
            @input="validateExpectedResult(scope.row)"
            @blur="validateExpectedResult(scope.row)"
            :class="{ 'is-error': scope.row.expectedResultError }"
          >
          </el-input>
          <div v-if="scope.row.expectedResultError" class="error-message">
            {{ scope.row.expectedResultError }}
          </div>
        </template>
        <template #slot_modelName="{ row }">
          <el-tooltip
            class="item"
            effect="dark"
            :content="row.modelName"
            placement="top-start"
          >
            <div class="model-name-tooltip">
              <el-tag type="primary">
                {{ row.modelName }}
              </el-tag>
            </div>
          </el-tooltip>
        </template>
        <template #slot_processEntry="{ row }">
          <el-button type="text" size="medium" @click="openDetail(row)">
            详情
          </el-button>
        </template>
      </GutuTable>
      <GutuPagination
        ref="gutuPaginationRef"
        :total="total"
        :pageSize="params.pageSize"
        @searchData="updateParams"
      />
    </main>
    <el-drawer
      :visible.sync="drawer.visible"
      :title="drawer.title"
      :size="drawer.size"
    >
      <GutuTable
        :dataList="selectedDataList"
        :loading="loading"
        :columnConfig="selectedColumnConfig"
        :collectionList="collectionList"
        :tableHeight="150"
        :params="params"
      >
        <template slot="slot_expectedResult" slot-scope="scope">
          <el-input
            v-model="scope.row.expectedResult"
            clearable
            placeholder="350-950"
            @change="validateExpectedResult(scope.row, true)"
            :class="{ 'is-error': scope.row.expectedResultError }"
          >
          </el-input>
          <div v-if="scope.row.expectedResultError" class="error-message">
            {{ scope.row.expectedResultError }}
          </div>
        </template>
        <template #slot_modelName="{ row }">
          <el-tooltip
            class="item"
            effect="dark"
            :content="row.modelName"
            placement="top-start"
          >
            <div class="model-name-tooltip">
              <el-tag type="primary">
                {{ row.modelName }}
              </el-tag>
            </div>
          </el-tooltip>
        </template>
        <template #slot_processEntry="{ row }">
          <el-button type="text" size="medium" @click="openDetail(row)">
            详情
          </el-button>
        </template>
      </GutuTable>
      <div class="bottomBtns">
        <el-button @click="drawer.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit(true)"
          >回溯任务提交</el-button
        >
      </div>
    </el-drawer>
    <Details ref="detailsRef" />
  </div>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
import Details from './details.vue'
import { historicalList, tracebackTasks } from '../api'
import { getBusinessScenario } from '@/views/platformEngine/api/platformEngine'
import { product_search } from '@/views/decisionPlatform/modules/productDecision/api/index'
export default {
  components: {
    GutuTable,
    GutuPagination,
    Details,
  },
  data() {
    return {
      queryParams: {
        productId: null,
        businessId: null,
        modelType: null,
        versionId: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      productOptions: [],
      modelTypeOptions: [
        { label: '评分模型', value: '1' },
        { label: '规则模型', value: '5' },
        { label: '分类模型', value: '6' },
      ],
      versionOptions: [],
      dataList: [],
      total: 0,
      loading: false,
      columnConfig: [
        {
          label: '序号',
          type: 'serial',
          width: '60',
        },
        {
          label: '任务编号',
          field: 'taskNo',
          width: '240',
        },
        {
          label: '申请用户',
          field: 'applicationUser',
          width: '120',
        },
        {
          label: '流程入参',
          field: 'processEntry',
          width: '160',
          type: 'slot',
        },
        {
          label: '业务场景',
          field: 'businessCode',
          width: '120',
          type: 'tag',
          pairedList: 'businessCodeOptions',
        },
        {
          label: '流程策略',
          field: 'processStrategy',
          width: '180',
        },
        {
          label: '模型名称及版本',
          field: 'modelName',
          width: '',
          type: 'slot',
        },
        {
          label: '数据来源',
          field: 'dataSource',
          width: '120',
        },
        {
          label: '申请时间',
          field: 'createTime',
          width: '180',
          type: 'timeStamp',
        },
        {
          label: '预期分数',
          field: 'expectedResult',
          width: '180',
          type: 'slot',
          fixed: 'right',
        },
        {
          label: '流程报告评分',
          field: 'totalScore',
          width: '120',
          fixed: 'right',
        },
      ],
      collectionList: {
        businessCodeOptions: [],
      },
      selectedDataList: [],
      selectedColumnConfig: [
        {
          label: '任务编号',
          field: 'taskNo',
          width: '260',
        },
        {
          label: '申请用户',
          field: 'applicationUser',
          width: '120',
        },
        {
          label: '流程入参',
          field: 'processEntry',
          width: '120',
          type: 'slot',
        },
        {
          label: '业务场景',
          field: 'businessCode',
          width: '120',
          type: 'tag',
          pairedList: 'businessCodeOptions',
        },
        {
          label: '流程策略',
          field: 'processStrategy',
          width: '180',
        },
        {
          label: '模型名称及版本',
          field: 'modelName',
          width: '280',
          type: 'slot',
        },
        {
          label: '数据来源',
          field: 'dataSource',
          width: '120',
        },
        {
          label: '申请时间',
          field: 'createTime',
          width: '180',
          type: 'timeStamp',
        },
        {
          label: '预期分数',
          field: 'expectedResult',
          width: '180',
          type: 'slot',
          fixed: 'right',
        },
        {
          label: '流程报告评分',
          field: 'totalScore',
          width: '120',
          fixed: 'right',
        },
      ],
      // tableHandle: {
      //   fixed: 'right',
      //   width: '120',
      //   label: '操作',
      //   align: 'left',
      //   slot: true,
      // },

      selectRowMap: new Map(),
      selectedTotal: 0,

      drawer: {
        visible: false,
        title: '回溯任务提交',
        size: '80%',
      },
    }
  },
  watch: {
    dataList: {
      handler(val) {
        this.handleSelectData(val)
      },
      deep: true,
    },
    queryParams: {
      handler() {
        this.params = this.$options.data().params
        this.$refs.gutuPaginationRef.reset()
        this.getDataList()
      },
      deep: true,
    },
  },
  mounted() {
    this.getDataList()
    this.init()
  },
  methods: {
    init() {
      getBusinessScenario().then((res) => {
        if (res.code == 200) {
          this.collectionList.businessCodeOptions = res.data.list.map(
            (item) => {
              return {
                label: item.name,
                value: item.id + '',
              }
            }
          )
        }
      })
      this.handleProductDataList()
    },
    handleProductDataList() {
      product_search({
        pageNum: 1,
        pageSize: 999,
      }).then((res) => {
        if (res.code == 200) {
          this.productOptions = res.data.list.map((item) => {
            return {
              label: item.name,
              value: item.id,
            }
          })
        }
      })
    },
    handleSubmit(strongValid = false) {
      let total = strongValid
        ? this.selectedDataList.length
        : this.selectedTotal
      if (total === 0) {
        this.$message.warning('请选择任务')
        return
      }
      let list = !strongValid
        ? Array.from(this.selectRowMap.values())
        : this.selectedDataList
      let filterList = list.filter((item) => !item.expectedResult)

      if (filterList.length > 0) {
        this.selectedDataList = list.map((item) => {
          return {
            ...item,
            expectedResultError: item.expectedResult ? '' : '请填写预期分数',
          }
        })
        this.drawer.visible = true
        return
      }
      let params = {
        items: list.map((item) => {
          return {
            historicalTaskNo: item.taskNo,
            expectedTotalScore: item.expectedResult,
          }
        }),
        remark: null,
      }
      tracebackTasks(params)
        .then((res) => {
          if (res.code == 200) {
            this.$message.success('回溯任务提交成功')
            this.handleCloseDrawer()
          }
        })
        .catch((err) => {})
    },
    getDataList() {
      this.loading = true
      this.dataList = []
      this.total = 0
      historicalList({ ...this.queryParams, ...this.params })
        .then((res) => {
          if (res.code == 200) {
            this.dataList = res.rows.map((item) => {
              return {
                ...item,
                expectedResultError: '',
              }
            })
            this.total = res.total
          }
        })
        .catch((err) => {})
        .finally(() => {
          this.loading = false
        })
    },
    updateParams(data) {
      this.params.pageNum = data.pageNum
      this.getDataList()
    },
    handleSelection(data) {
      this.dataList.forEach((item) => {
        if (this.selectRowMap.has(item.taskNo) && !data.includes(item)) {
          this.selectRowMap.delete(item.taskNo)
        }
      })
      data.forEach((item) => {
        this.selectRowMap.set(item.taskNo, item)
      })
      this.selectedTotal = this.selectRowMap.size
    },
    handleSelectData(list) {
      let selectList = []
      list.forEach((item) => {
        if (this.selectRowMap.has(item.userId)) {
          selectList.push(item)
        }
      })
      this.$nextTick(() => {
        if (selectList.length) {
          this.$refs.gutuTableRef.toggleSelection(selectList)
        }
      })
    },
    resetParams() {
      this.queryParams = {}
    },
    validateExpectedResult(row, check = false) {
      const value = row.expectedResult
      if (!value && check) {
        row.expectedResultError = '请填写预期分数'
        return true
      }
      if (value) {
        // 检查是否为数字
        const numValue = Number(value)
        if (isNaN(numValue)) {
          row.expectedResultError = '请输入有效的数字'
          return false
        }

        // 检查范围
        if (numValue < 350 || numValue > 950) {
          row.expectedResultError = '请输入350-950之间的数字'
          return false
        }

        // 检查小数位数（最多2位小数）
        if (
          value.toString().includes('.') &&
          value.toString().split('.')[1].length > 2
        ) {
          row.expectedResultError = '最多保留2位小数'
          return false
        }
      }

      row.expectedResultError = ''
      return true
    },
    openDetail(data) {
      this.$refs.detailsRef.handleOpen(data)
    },
    handleCloseDrawer() {
      this.drawer.visible = false
      this.selectedDataList = []
      this.selectedTotal = 0
      this.selectRowMap.clear()
      this.getDataList()
    },
  },
}
</script>
<style lang="less" scoped>
.history-task {
  width: 100%;
  height: calc(var(--bgvh) - 42px);
  padding: 20px;

  ::v-deep &-search {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .el-input {
      width: 240px;
      margin-right: 10px;
    }

    .el-select {
      margin-right: 10px;
      width: 200px;
      .el-input {
        width: 200px;
        .el-input__inner {
          height: 42px;
        }
      }
    }

    .el-button {
      padding: 0 20px;
      height: 42px;
      font-size: 14px;
    }
  }
  &-operation {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: var(--bg-color);
    padding: 10px;
    .el-button {
      height: 42px;
      padding: 0 20px;
      font-size: 14px;
    }
  }

  main {
    border-radius: 8px;
    padding: 20px;
    background: var(--bg-color);

    .model-name-tooltip {
      ::v-deep .el-tag {
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .gutuPagination {
      margin-top: 20px;
      // text-align: right;
    }
  }

  // 输入框错误状态样式
  ::v-deep .is-error {
    .el-input__inner {
      border-color: #f56c6c !important;
    }
  }

  .error-message {
    color: #f56c6c;
    font-size: 12px;
    line-height: 1;
    padding-top: 4px;
    text-align: left;
  }

  .tip {
    color: #f56c6c;
    font-size: 14px;
    line-height: 1;
    margin-bottom: 10px;
  }

  :deep(.el-drawer) {
    .el-drawer__header {
      margin-bottom: 0;
      padding: 20px;
    }
    .el-drawer__body {
      padding: 0 20px 20px 20px;
    }
  }
  .bottomBtns {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    .el-button {
      height: 40px;
      padding: 0 20px;
      font-size: 14px;
    }
  }
}
</style>
