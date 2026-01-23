<template>
  <div class="warningTask">
    <div class="wt-top">
      <SliderFilter
        v-model="currentTab"
        :options="filterOptions"
        className="tab-change"
      ></SliderFilter>
      <div class="wt-top_search">
        <template v-if="currentTab == 'list'">
          <el-input
            v-model="queryParams.keyword"
            prefix-icon="el-icon-search"
            :placeholder="$t('monitor.inputTaskNo')"
            clearable
            @clear="queryParams.keyword = null"
          ></el-input>
        </template>
        <template v-if="currentTab == 'dynamics'">
          <el-input
            v-model="queryParams.customerName"
            prefix-icon="el-icon-search"
            :placeholder="$t('monitor.inputCustomerName')"
          ></el-input>
          <el-select
            v-model="queryParams.subjectType"
            :placeholder="$t('monitor.selectCustomerType')"
            clearable
          >
            <el-option
              v-for="(item, index) in collectionList.ric_monitor_subject"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
          <el-select
            v-model="queryParams.processPolicyId"
            :placeholder="$t('monitor.warningModel')"
            clearable
            filterable
            @clear="queryParams.processPolicyId = null"
          >
            <el-option
              v-for="item in collectionList.policyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
          <!-- <el-select
            v-model="queryParams.riskType"
            placeholder="请选择风险程度"
          >
            <el-option
              v-for="(item, index) in riskTypeOptions"
              :key="index"
            ></el-option>
          </el-select> -->
        </template>
      </div>
    </div>
    <div class="wt-content">
      <div class="wt-content_Search">
        <div class="filter">
          <GutuSelect
            :data.sync="queryParams.status"
            :config="{
              placeholder: $t('monitor.status'),
            }"
            :options="statusOptions"
            :flatten="true"
            :adaptiveWidth="{
              minWidth: '80px',
              enable: true,
            }"
          />
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="-"
            value-format="yyyy-MM-dd"
            :start-placeholder="$t('monitor.createStartDate')"
            :end-placeholder="$t('monitor.createEndDate')"
          >
          </el-date-picker>
        </div>
        <div class="operate">
          <el-button
            v-if="currentTab == 'list'"
            type="warning"
            @click="handleOpenAddTask()"
            >{{ $t('monitor.addMonitorTask') }}</el-button
          >
        </div>
      </div>
      <GutuTable
        v-if="resetStatus"
        :loading="tableLoading"
        :dataList="dataList"
        :columnConfig="currentTab == 'list' ? columnConfig : recordColumnConfig"
        :collectionList="collectionList"
        :handle="tableHandle"
        :tableHeight="280"
        @handleSwitchChange="handleChangeTaskStatus"
      >
        <template #slotHeader_monitorTime="scope">
          {{ $t('monitor.monitorTime') }}
          <el-tooltip effect="dark" placement="top">
            <div
              slot="content"
              class="tip"
              v-html="$t('monitor.monitorTimeTip')"
            ></div>
            <i class="el-icon-question" />
          </el-tooltip>
        </template>
        <template #slotHeader_monitorStatus="scope">
          {{ $t('monitor.monitorStatus') }}
          <el-tooltip effect="dark" placement="top">
            <div
              slot="content"
              class="tip"
              v-html="$t('monitor.monitorStatusTip')"
            ></div>
            <i class="el-icon-question" />
          </el-tooltip>
        </template>
        <template #bt_handle="{ data }">
          <div class="operateBtns" v-if="currentTab == 'list'">
            <el-button type="text" @click="hadleOpenMonitoringList(data.row)">{{
              $t('monitor.monitoringList')
            }}</el-button>
            <el-button type="text" @click="handleOpenAddTask(data.row)">{{
              $t('monitor.edit')
            }}</el-button>
            <el-button
              class="delete"
              type="text"
              @click="handleDelete(data.row)"
              >{{ $t('monitor.delete') }}</el-button
            >
          </div>
          <div class="operateBtns" v-else>
            <el-button
              type="text"
              @click="handleOpenDynamicsDetail(data.row)"
              >{{ $t('monitor.viewDetail') }}</el-button
            >
          </div>
        </template>
      </GutuTable>

      <GutuPagination :total="total" @searchData="updateParams" />
    </div>
    <AddMonitoringTasks
      ref="addMonitoringTasksRef"
      :collectionList="collectionList"
      @refresh="getDataList"
    />
    <MonitoringList ref="monitoringListRef" :collectionList="collectionList" />
    <DynamicsDetail ref="dynamicsDetailRef" />
  </div>
</template>
<script>
import SliderFilter from '@/components/SliderFilter'
import GutuSelect from '@/components/gutu/gutuSelect.vue'
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
import AddMonitoringTasks from './components/AddMonitoringTasks.vue'
import MonitoringList from './components/MonitoringList.vue'
import DynamicsDetail from './components/DynamicsDetail.vue'

import {
  taskList,
  recordList,
  deleteTask,
  changeTaskStatus,
  processPolicyEnableList,
} from '../api'
import { getDicts } from '@/api'

export default {
  components: {
    SliderFilter,
    GutuSelect,
    GutuTable,
    GutuPagination,
    AddMonitoringTasks,
    MonitoringList,
    DynamicsDetail,
  },
  data() {
    return {
      currentTab: 'list',
      resetStatus: true,
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      queryParams: {
        keyword: null,
        customerName: null,
        riskType: null,
        startTime: null,
        endTime: null,
        subjectType: null,
        processPolicyId: null,
      },
      timeRange: [],
      tableLoading: false,
      dataList: [],
      total: 0,
      collectionList: {
        policyOptions: [],
        subjectStyle: new Map([
          [0, { color: 'var(--primary-color)', bgColor: true }],
          [1, { color: '#00B578', bgColor: true }],
        ]),
      },
    }
  },
  computed: {
    filterOptions() {
      return [
        { label: this.$t('monitor.monitorListTab'), value: 'list' },
        { label: this.$t('monitor.monitorDynamics'), value: 'dynamics' },
      ]
    },
    columnConfig() {
      return [
        {
          type: 'serial',
          label: this.$t('monitor.serialNumber'),
          width: '80',
        },
        {
          label: this.$t('monitor.taskNo'),
          field: 'taskNo',
          width: '280',
        },
        {
          label: this.$t('monitor.applyUser'),
          field: 'applyUser',
        },
        {
          label: this.$t('monitor.warningModel'),
          field: 'processPolicyName',
        },
        {
          label: this.$t('monitor.monitorSubject'),
          field: 'subjectType',
          type: 'tag',
          pairedList: 'ric_monitor_subject',
          styleList: 'subjectStyle',
        },
        {
          label: this.$t('monitor.monitorTime'),
          field: 'monitorDays',
          slotHeader: 'monitorTime',
          type: 'tag',
          pairedList: 'ric_monitor_time',
        },
        {
          label: this.$t('monitor.createTime'),
          field: 'createTime',
          type: 'timeStamp',
        },
        {
          label: this.$t('monitor.monitorStatus'),
          field: 'status',
          slotHeader: 'monitorStatus',
          type: 'switch',
          activeValue: 1,
          inactiveValue: 0,
          activeText: this.$t('monitor.open'),
          inactiveText: this.$t('monitor.close'),
        },
      ]
    },
    recordColumnConfig() {
      return [
        {
          label: this.$t('monitor.customerName'),
          field: 'customerName',
          width: '',
        },
        {
          label: this.$t('monitor.riskLevel'),
          field: 'riskLevel',
          width: '',
          type: 'tag',
          pairedList: 'ric_monitor_riskLevel',
        },
        {
          label: this.$t('monitor.riskType'),
          field: 'riskType',
          width: '',
        },
        {
          label: this.$t('monitor.warningContent'),
          field: 'warningContent',
          width: '',
        },
        {
          label: this.$t('monitor.warningTime'),
          field: 'warningTime',
          width: '',
        },
      ]
    },
    tableHandle() {
      return {
        fixed: 'right',
        width: '220',
        label: this.$t('monitor.operation'),
        align: 'center',
        slot: true,
      }
    },
    statusOptions() {
      return [
        { label: this.$t('monitor.open'), value: '1' },
        { label: this.$t('monitor.close'), value: '0' },
      ]
    },
  },
  watch: {
    currentTab: {
      handler() {
        this.queryParams = this.$options.data().queryParams
        this.params = this.$options.data().params
      },
    },
    timeRange: {
      handler(val) {
        if (val && val.length) {
          this.queryParams.startTime = val[0]
          this.queryParams.endTime = val[1]
        } else {
          this.queryParams.startTime = null
          this.queryParams.endTime = null
        }
      },
    },
    queryParams: {
      handler() {
        this.getDataList()
      },
      deep: true,
    },
  },
  mounted() {
    this.init()
    this.getDataList()
  },
  methods: {
    init() {
      let list = [
        'ric_monitor_time',
        'ric_monitor_subject',
        'ric_monitor_riskLevel',
      ]
      list.forEach((item) => {
        getDicts(item)
          .then((res) => {
            this.$set(
              this.collectionList,
              item,
              res.data.map((itemX) => {
                return {
                  label: itemX.dictLabel,
                  value: itemX.dictValue,
                }
              })
            )
          })
          .catch((err) => {})
      })

      processPolicyEnableList({}).then((res) => {
        if (res.code == 200) {
          this.collectionList.policyOptions = res.data.map((item) => {
            return {
              label: item.name,
              value: item.id,
            }
          })
        }
      })
    },
    getDataList() {
      this.resetStatus = false
      setTimeout(() => {
        this.resetStatus = true
      }, 100)
      this.dataList = []
      let URL = this.currentTab == 'list' ? taskList : recordList
      this.tableLoading = true
      URL({ ...this.queryParams, ...this.params })
        .then((res) => {
          if (res.code == 200) {
            this.dataList = res.data.list
            this.total = res.data.total
          }
          this.tableLoading = false
        })
        .catch((err) => {})
    },
    handleDelete(data) {
      this.$confirm(
        this.$t('monitor.confirmDeleteTask'),
        this.$t('common.tip'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          deleteTask(data.taskId).then((res) => {
            if (res.code == 200) {
              this.$message.success(this.$t('monitor.deleteSuccess'))
              this.getDataList()
            }
          })
        })
        .catch((err) => {})
    },
    handleChangeTaskStatus(data) {
      const confirmMsg =
        data.status == 1
          ? this.$t('monitor.confirmOpenTask')
          : this.$t('monitor.confirmCloseTask')
      this.$confirm(confirmMsg, this.$t('common.tip'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          changeTaskStatus(data).then((res) => {
            if (res.code == 200) {
              this.$message.success(this.$t('monitor.modifySuccess'))
              this.getDataList()
            }
          })
        })
        .catch(() => {
          data.status = data.status == 1 ? 0 : 1
        })
    },
    handleOpenDynamicsDetail(data) {
      this.$refs.dynamicsDetailRef.handleOpen(data)
    },
    handleOpenAddTask(data) {
      this.$refs.addMonitoringTasksRef.handleOpen(data)
    },
    hadleOpenMonitoringList(data) {
      this.$refs.monitoringListRef.handleOpen(data)
    },
    updateParams(data) {
      this.params = { ...data }
      this.getDataList()
    },
  },
}
</script>
<style lang="less" scoped>
.warningTask {
  padding: 20px;

  .wt-top {
    display: flex;
    margin-bottom: 14px;

    &_search {
      display: flex;
      align-items: center;

      ::v-deep .el-input {
        width: 260px;
        margin-left: 10px;
        .el-input__inner {
          border-radius: 6px;
          height: 48px;
        }
      }
      ::v-deep .el-select {
        .el-input {
          width: 200px;
        }

        .el-select__inner {
          border-radius: 6px;
          height: 48px;
        }
      }
    }
  }

  .wt-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 6px;

    .wt-content_Search {
      height: 40px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      .filter {
        display: flex;
        align-items: center;

        ::v-deep .el-date-editor {
          .el-range-input {
            background: rgba(#000, 0.05);
          }
        }
      }
      .operate {
        display: flex;
        align-items: center;

        .el-button {
          border-radius: 6px;
          height: 40px;
        }
      }
    }

    .operateBtns {
      .el-button {
        font-size: 14px;
      }
      .delete {
        color: #fa5151;
      }
    }

    .gutuPagination {
      margin-top: 10px;
    }
  }
}

.tip {
  width: 160px !important;
  text-align: center;
  font-size: 12px;
  line-height: 22px;
}
</style>
