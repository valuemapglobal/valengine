<template>
  <div class="ml">
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      :size="drawer.width"
      :before-close="handleClose"
    >
      <div class="ml-top">
        <div class="ml-top_search">
          <el-input
            v-model="queryParams.customerName"
            type="primary"
            :placeholder="$t('monitor.inputCustomerName')"
            clearable
          />
          <!-- <el-select
            v-model="queryParams.processPolicyId"
            placeholder="预警模型"
            clearable
            filterable
            @clear="queryParams.processPolicyId = null"
          >
            <el-option
              v-for="item in policyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select> -->
        </div>
        <div class="ml-top_operate">
          <el-button type="primary" @click="handleOpenBatchAdd">{{
            $t('monitor.batchAdd')
          }}</el-button>
          <el-button type="warning" @click="handleOpenAddMonitor">{{
            $t('monitor.addList')
          }}</el-button>
        </div>
      </div>
      <GutuTable
        :loading="loading"
        :dataList="dataList"
        :columnConfig="columnConfig"
        :collectionList="collectionList"
        :handle="tableHandle"
        :tableHeight="280"
        selection
        @handleSelection="handleSelection"
      >
        <template #bt_handle="{ data }">
          <div class="operateBtns">
            <el-button type="text" @click="handleCancelMonitor(data.row)">{{
              $t('monitor.cancelMonitor')
            }}</el-button>
          </div>
        </template>
      </GutuTable>
      <GutuPagination :total="total" @searchData="updateParams" />
      <div class="bottomBtns">
        <el-button
          type="primary"
          plain
          :disabled="!selectList.length"
          @click="handleCancelMonitor()"
          >{{ $t('monitor.cancelMonitor') }}</el-button
        >
      </div>
    </el-drawer>
    <BatchAdd
      ref="batchAddRef"
      :subjectType="currentData?.subjectType"
      :taskId="taskId"
      @refresh="getDataList"
    />
    <AddMonitor
      ref="addMonitorRef"
      :subjectType="currentData?.subjectType"
      :isChange="dataList.length == 0"
      @refresh="getDataList"
    />
  </div>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
import BatchAdd from './BatchAdd.vue'
import AddMonitor from './AddMonitor.vue'

import { monitorTargetList, batchCancel } from '../../api'

export default {
  name: 'MonitoringList',
  components: {
    GutuTable,
    GutuPagination,
    BatchAdd,
    AddMonitor,
  },
  props: {
    collectionList: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '',
        width: '960px',
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      taskId: null,
      queryParams: {
        customerName: null,
        processPolicyId: null,
      },
      policyOptions: [],

      total: 10,
      loading: false,
      dataList: [],
      selectList: [],
      currentData: null,
    }
  },
  computed: {
    columnConfig() {
      return [
        {
          label: this.$t('monitor.customerName'),
          field: 'customerName',
          width: '260px',
        },
        {
          label: this.$t('monitor.monitorSubject'),
          field: 'subjectType',
          type: 'tag',
          pairedList: 'ric_monitor_subject',
          width: '180px',
        },
        {
          label: this.$t('monitor.warningModel'),
          field: 'processPolicyName',
          width: '180px',
        },
        {
          label: this.$t('monitor.createTime'),
          field: 'createTime',
          type: 'format',
          width: '180px',
        },
      ]
    },
    tableHandle() {
      return {
        fixed: 'right',
        width: '140',
        label: this.$t('monitor.operation'),
        align: 'center',
        slot: true,
      }
    },
  },
  watch: {
    queryParams: {
      handler() {
        this.getDataList()
      },
      deep: true,
    },
  },
  methods: {
    init() {},
    getDataList() {
      this.loading = true
      this.dataList = []
      monitorTargetList({
        taskId: this.taskId,
        ...this.queryParams,
        ...this.params,
      }).then((res) => {
        if (res.code == 200) {
          this.dataList = res.rows
          this.total = res.total
        }
        this.loading = false
      })
    },
    handleCancelMonitor(data) {
      let list = []
      if (data) {
        list.push(data)
      } else {
        list = [...this.selectList]
      }
      if (!list.length) {
        this.$message.warning(this.$t('monitor.pleaseSelectCustomer'))
        return
      }

      const customerNames = list.map((item) => item.customerName).join('，')
      const confirmMsg = `${this.$t(
        'monitor.confirmCancelMonitor'
      )}<span style="color: var(--primary-color);"> ${customerNames} </span>${this.$t(
        'monitor.confirmCancelMonitorSuffix'
      )}`
      this.$confirm(confirmMsg, this.$t('common.tip'), {
        dangerouslyUseHTMLString: true,
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          batchCancel({ targetIds: list.map((item) => item.targetId) }).then(
            (res) => {
              if (res.code == 200) {
                this.$message.success(this.$t('monitor.cancelMonitorSuccess'))
                this.getDataList()
              }
            }
          )
        })
        .catch(() => {})
    },
    handleSelection(data) {
      this.selectList = data
    },
    handleOpenBatchAdd() {
      this.$refs.batchAddRef.handleOpen()
    },
    handleOpenAddMonitor() {
      this.$refs.addMonitorRef.handleOpen(this.taskId)
    },
    updateParams(data) {
      this.params = { ...data }
      this.getDataList()
    },
    handleOpen(data) {
      this.currentData = { ...data }
      this.taskId = data.taskId
      this.drawer.title = this.$t('monitor.monitoringList')
      this.getDataList()
      this.init()
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
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    position: relative;
    padding: 0px 20px 20px;

    .bottomBtns {
      position: absolute;
      bottom: 0px;
      left: 0px;
      padding: 20px;

      .el-button {
        height: 42px;
        font-size: 14px;
        border-radius: 6px;
      }
    }
  }
}
.ml {
  &-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    &_search,
    &_operate {
      display: flex;
    }

    ::v-deep &_search {
      .el-input {
        width: 320px;
        margin-right: 16px;
        .el-input__inner {
          height: 40px;
          background: rgba(#000, 0.04);
        }
      }
      .el-select {
        width: 230px;
      }
    }
    &_operate {
      .el-button {
        padding: 0px 20px;
        height: 40px;
        border-radius: 6px;
      }
    }
  }
  .operateBtns {
    .el-button {
      font-size: 14px;
    }
  }
  .gutuPagination {
    margin-top: 10px;
  }
}
</style>
