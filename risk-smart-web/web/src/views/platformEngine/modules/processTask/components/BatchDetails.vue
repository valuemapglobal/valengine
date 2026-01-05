<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
    :destroy-on-close="true"
    :show-close="true"
  >
    <GutuTable
      :loading="loading"
      :dataList="dataList"
      :columnConfig="columnConfig"
      :collectionList="collectionList"
      :handle="tableHandle"
      :border="false"
      :params="params"
      tableHeight="140"
    >
      <template #slot_processEntry="{ row }">
        <el-button
          v-if="row.processEntry && row.processEntry.length"
          type="text"
          size="medium"
          @click="openDrawer('entryDetail', row.processEntry)"
          >详情</el-button
        >
      </template>
      <template #slot_businessCode="{ row }">
        <el-tag
          type="primary"
          v-if="
            row.businessCode &&
            setShowValue(row.businessCode, businessList, 'businessCode', 'name')
          "
        >
          {{
            setShowValue(row.businessCode, businessList, 'businessCode', 'name')
          }}
        </el-tag>
      </template>
      <template #slot_modelNameList="{ row }">
        <el-tooltip
          v-if="row.modelNameList && row.modelNameList.length"
          class="item"
          effect="dark"
          :content="row.modelNameList.join(',')"
          placement="top-start"
        >
          <el-tag type="primary">
            {{
              row.modelNameList.length > 2
                ? `${row.modelNameList[0]},${row.modelNameList[1]}…`
                : row.modelNameList.join(',')
            }}
          </el-tag>
        </el-tooltip>
      </template>
      <template #slot_responseForm="{ row }">
        {{
          setShowValue(
            row.responseForm,
            responseOptions,
            'dictValue',
            'dictLabel'
          )
        }}
      </template>
      <template #slot_taskStatus="{ row }">
        <el-tag
          v-if="row.taskStatus"
          :type="setShowValue(row.taskStatus, statusOptions, 'value', 'type')"
        >
          {{ setShowValue(row.taskStatus, statusOptions) }}
        </el-tag>
      </template>
      <template #bt_handle="{ data }">
        <el-button
          v-if="data.row.responseForm === 1"
          type="text"
          @click="
            openDrawer('responseResult', data.row.taskNo, data.row.taskStatus)
          "
        >
          详情
        </el-button>
        <el-button
          v-if="data.row.taskStatus === 3 && data.row.responseForm === 2"
          type="text"
          @click="openDrawer('riskReport', data.row.taskNo)"
        >
          查看报告
        </el-button>
      </template>
    </GutuTable>
    <div class="pagination">
      <div class="pagination-total">
        共 <span>{{ total }}</span> 条
      </div>
      <GutuPagination
        ref="gutuPaginationRef"
        :total="total"
        :pageSize="params.pageSize"
        layout="prev, pager, next"
        @searchData="updateParams"
      />
    </div>
  </el-drawer>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'

import { taskRecordList } from '@/views/platformEngine/api/platformEngine'
import { setShowValue } from '@/utils'
export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  props: {
    businessList: {
      type: Array,
      default: () => [],
    },
    responseOptions: {
      type: Array,
      default: () => [],
    },
    statusOptions: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '批量详情',
        width: '60%',
      },
      loading: false,
      dataList: [],
      columnConfig: [
        {
          label: '序号',
          type: 'serial',
          width: '60px',
        },
        {
          label: '任务编号',
          field: 'taskNo',
          width: '280',
        },
        {
          label: '申请用户',
          field: 'applicationUser',
          width: '200',
        },
        {
          label: '流程入参',
          field: 'processEntry',
          width: '150',
          type: 'slot',
        },
        {
          label: '业务场景',
          field: 'businessCode',
          width: '150',
          type: 'slot',
        },
        {
          label: '流程策略',
          field: 'processStrategy',
          width: '180',
        },
        {
          label: '模型名称',
          field: 'modelNameList',
          width: '240',
          type: 'slot',
        },
        {
          label: '申请时间',
          field: 'createTime',
          width: '180',
          type: 'timeStamp',
        },
        {
          label: '响应形式',
          field: 'responseForm',
          width: '180',
          type: 'slot',
        },
        {
          label: '状态',
          field: 'taskStatus',
          width: '120',
          type: 'slot',
        },
      ],
      collectionList: {},
      tableHandle: {
        fixed: 'right',
        width: '120px',
        label: '响应结果',
        align: 'center',
        slot: true,
      },
      total: 0,
      params: {
        pageSize: 10,
        pageNum: 1,
      },
      batchId: null,
    }
  },
  methods: {
    setShowValue,
    getDataList() {
      this.loading = true
      taskRecordList({
        batchId: this.batchId,
        ...this.params,
      }).then((res) => {
        this.loading = false
        this.dataList = res.data.list
        this.total = res.data.total
      })
    },
    resetParams() {
      this.params = this.$options.data().params
      if (this.$refs.gutuPaginationRef) this.refs.gutuPaginationRef.reset()
    },
    updateParams(data) {
      this.params = data
      this.getDataList()
    },
    openDrawer(type, data, riskStatus) {
      this.$emit('openDrawer', type, data, riskStatus)
    },
    handleOpen(data) {
      this.batchId = data.id
      this.resetParams()
      this.getDataList()
      this.drawer.visible = true
    },
    handleClose() {
      this.drawer.visible = false
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-drawer {
  font-family: PingFang SC-Medium;
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 0px 20px 20px;
  }
}
.pagination {
  width: 100%;
  margin: 16px 0 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .pagination-total {
    white-space: nowrap;
    font-size: 14px;
    color: #999;
    span {
      color: #333;
      font-weight: 500;
    }
  }
  ::v-deep .gutuPagination {
    width: auto;
  }
}
</style>
