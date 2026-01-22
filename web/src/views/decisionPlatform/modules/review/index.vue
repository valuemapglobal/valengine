<template>
  <div class="review">
    <div class="topSearch">
      <el-input
        v-model="queryParams.keyword"
        placeholder="请输入申请人或审批人"
        clearable
        @keyup.enter.native="getDataList"
        @clear="queryParams.keyword = null"
      ></el-input>
      <el-select
        v-model="queryParams.status"
        placeholder="审批状态"
        clearable
        filterable
        @clear="queryParams.status = null"
      >
        <el-option
          v-for="item in collectionList.statusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>

      <el-button
        type="primary"
        size="normal"
        icon="el-icon-search"
        @click="getDataList"
        >搜索</el-button
      >
      <el-button
        type="primary"
        size="normal"
        icon="el-icon-refresh"
        @click="reset"
        >重置</el-button
      >
    </div>
    <main>
      <GutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :collectionList="collectionList"
        :query="params"
        :tableHeight="180"
        :handle="tableHandle"
      >
        <template #bt_handle="{ data }">
          <div class="bt_handle">
            <el-button type="text" @click="handleDetails(data.row, false)">
              查看
            </el-button>
            <el-button
              v-if="data.row.approvalStatus === 'PENDING_APPROVAL'"
              type="text"
              @click="handleDetails(data.row, true)"
            >
              审批
            </el-button>
          </div>
        </template>
      </GutuTable>
      <GutuPagination
        style="margin-top: 10px"
        ref="gutuPaginationRef"
        :total="total"
        @searchData="updateParams"
      ></GutuPagination>
    </main>
    <Details ref="detailsRef" :expand="collectionList" @refresh="getDataList" />
  </div>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import Details from './components/Details'

import { business_search } from '@/views/decisionPlatform/modules/productDecision/api'
import { getApprovalsList } from './api'
import { getDicts } from '@/api'

export default {
  components: {
    GutuTable,
    GutuPagination,
    Details,
  },
  data() {
    return {
      queryParams: {
        keyword: null,
        status: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      loading: false,
      dataList: [
        // {
        //   taskId: 110,
        //   strategyName: '批次标题',
        //   submitter: '估图账号',
        //   submittedAt: '2025-10-16 09:53:16',
        //   approverName: '估图账号',
        //   approverId: 12428,
        //   approvalStatus: 'PENDING_APPROVAL',
        //   flowType: '策略发布审批',
        // },
      ],
      total: 0,
      columnConfig: [
        {
          label: '序号',
          align: 'center',
          type: 'serial',
          width: '80px',
        },
        {
          label: '审批项',
          field: 'strategyName',
          align: 'center',
          width: null,
        },
        {
          label: '申请人',
          field: 'submitter',
          align: 'center',
          width: null,
        },
        {
          label: '提交时间',
          field: 'submittedAt',
          align: 'center',
          width: null,
        },
        {
          label: '当前审批人',
          field: 'approverName',
          align: 'center',
          width: null,
        },
        {
          label: '流程类型',
          field: 'flowType',
          align: 'center',
          width: null,
        },
        {
          label: '审批状态',
          field: 'approvalStatus',
          align: 'center',
          width: null,
          type: 'tag',
          pairedList: 'statusOptions',
        },
      ],
      tableHandle: {
        fixed: 'right',
        width: '160px',
        label: '操作',
        align: 'left',
        slot: true,
      },
      collectionList: {
        statusOptions: [],
        businessOptions: [],
      },
    }
  },
  // watch: {
  //   queryParams: {
  //     handler() {
  //       this.getDataList()
  //     },
  //     deep: true,
  //   },
  // },

  mounted() {
    this.init()
    this.getDataList()
  },
  methods: {
    init() {
      getDicts('system_review_status').then((res) => {
        this.collectionList.statusOptions = res.data.map((item) => {
          return {
            label: item.dictLabel,
            value: item.dictValue,
          }
        })
      })
      business_search({}).then((res) => {
        this.collectionList.businessOptions = res.data.list.map((item) => {
          return {
            label: item.name,
            value: item.id,
          }
        })
      })
    },
    getDataList() {
      this.loading = true
      getApprovalsList({ ...this.queryParams, ...this.params })
        .then((res) => {
          if (res.code == 200) {
            this.dataList = res.data.list || []
            this.total = res.data.total || 0
          }
        })
        .catch(() => {})
        .finally(() => {
          this.loading = false
        })
    },
    reset() {
      this.queryParams = this.$options.data().queryParams
      this.params = this.$options.data().params
      this.$refs.gutuPaginationRef.reset()

      this.getDataList()
    },
    handleDetails(data, isEdit) {
      this.$refs.detailsRef.handleOpen(data, isEdit)
    },
    openAdd() {
      this.$refs.addUserAuthRef.handleOpen()
    },
    updateParams(data) {
      this.params = data
      this.getDataList()
    },
  },
}
</script>
<style lang="less" scoped>
.review {
  height: calc(var(--bgvh) - 42px);
  padding: 20px;

  :deep(.el-drawer) {
    .el-drawer__header {
      margin-bottom: 0;
      padding: 20px;
    }
    .el-drawer__body {
      padding: 0 20px 20px 20px;
    }
  }
}
.cardList {
  height: calc(100% - 60px);
  overflow-y: auto;
}
:deep(.topSearch) {
  display: flex;
  margin-bottom: 16px;
  .el-input {
    width: 240px;
    .el-input__inner {
      height: 42px;
    }
  }
  .el-input + .el-input {
    margin-left: 10px;
  }
  .el-select {
    margin-left: 10px;
  }
  .el-button {
    margin-left: 10px;
  }
}

.bt_handle {
  .el-button {
    font-size: 14px;
  }
}
</style>
