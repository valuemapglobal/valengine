<template>
  <div class="history-task">
    <div class="history-task-search">
      <el-input
        v-model="queryParams.tracebackNo"
        placeholder="请输入回溯任务号"
        clearable
        @clear="queryParams.tracebackNo = null"
      ></el-input>

      <el-select
        v-model="queryParams.status"
        placeholder="请选择任务状态"
        clearable
        filterable
        @clear="queryParams.status = null"
      >
        <el-option
          v-for="item in collectionList.taskStatusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-date-picker
        v-model="queryParams.creationDate"
        placeholder="请选择创建时间"
        clearable
        value-format="yyyy-MM-dd"
        @clear="queryParams.creationDate = null"
      ></el-date-picker>
      <el-button icon="el-icon-refresh" @click="resetParams">重置</el-button>
      <el-button type="primary" icon="el-icon-search" @click="getDataList"
        >搜索</el-button
      >
    </div>
    <main>
      <GutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :collectionList="collectionList"
        :tableHeight="240"
        :params="params"
        :handle="tableHandle"
      >
        <template #slot_processStrategies="{ row }">
          <el-tag v-for="strategy in row.processStrategies" :key="strategy">{{
            strategy
          }}</el-tag>
        </template>
        <template #bt_handle="{ data }">
          <el-button type="text" size="medium" @click="handleDetails(data.row)">
            查看详情
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
    <Details ref="detailsRef" />
  </div>
</template>

<script>
import Details from './details.vue'
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
import { tracebackTasksList } from '../api'
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
        creationDate: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      productOptions: [],
      businessOptions: [],
      modelTypeOptions: [],
      versionOptions: [],
      dataList: [],
      total: 10,
      loading: false,
      columnConfig: [
        {
          label: '序号',
          type: 'serial',
          width: '60',
        },
        {
          label: '回溯任务号',
          field: 'tracebackNo',
          width: '240',
        },
        {
          label: '流程策略集',
          field: 'processStrategies',
          width: null,
          type: 'slot',
        },
        {
          label: '创建时间',
          field: 'createdAt',
          width: '180',
          type: 'timeStamp',
        },
        {
          label: '数据来源',
          field: 'dataSource',
          width: '280',
        },
        {
          label: '任务状态',
          field: 'status',
          width: '240',
          type: 'tag',
          pairedList: 'taskStatusOptions',
        },
      ],
      collectionList: {
        taskStatusOptions: [
          { label: '任务正在创建中', value: 'GENERATING' },
          { label: '(子任务状态) 等待执行', value: 'PENDING' },
          { label: '任务正在执行中', value: 'PROCESSING' },
          { label: '(子任务状态) 执行成功', value: 'SUCCESS' },
          { label: '(子任务状态) 执行失败', value: 'FAILED' },
        ],
      },
      tableHandle: {
        fixed: 'right',
        width: '160px',
        label: '操作',
        align: 'left',
        slot: true,
      },

      selectRowMap: [],
      selectedTotal: 0,
    }
  },
  watch: {
    queryParams: {
      handler(val) {
        this.params = this.$options.data().params
        this.$refs.gutuPaginationRef.reset()
        this.getDataList()
      },
      deep: true,
    },
  },
  mounted() {
    this.getDataList()
  },
  methods: {
    handleSubmit() {
      if (this.selectedTotal === 0) {
        this.$message.warning('请选择任务')
        return
      }
    },
    getDataList() {
      this.loading = true
      this.dataList = []
      this.total = 0
      tracebackTasksList({ ...this.queryParams, ...this.params })
        .then((res) => {
          if (res.code == 200) {
            this.dataList = res.rows
            this.total = res.total
          }
        })
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
        if (this.selectRowMap.has(item.taskId) && !data.includes(item)) {
          this.selectRowMap.delete(item.taskId)
        }
      })
      data.forEach((item) => {
        this.selectRowMap.set(item.taskId, item)
      })
      this.selectedTotal = this.selectRowMap.size
    },
    resetParams() {
      this.queryParams = {}
    },

    handleDetails(data) {
      this.$refs.detailsRef.handleOpen(data)
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
      width: 200px;
      margin-right: 10px;
      .el-input__inner {
        height: 42px;
      }
    }

    .el-select {
      width: 200px;
      margin-right: 10px;
      .el-input__inner {
        height: 42px;
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

    .gutuPagination {
      margin-top: 20px;
      // text-align: right;
    }
  }
}
</style>
