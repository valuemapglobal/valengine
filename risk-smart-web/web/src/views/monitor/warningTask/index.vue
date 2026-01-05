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
            placeholder="请输入任务编号/流程策略"
            clearable
            @clear="queryParams.keyword = null"
          ></el-input>
        </template>
        <template v-if="currentTab == 'dynamics'">
          <el-input
            v-model="queryParams.customerName"
            prefix-icon="el-icon-search"
            placeholder="请输入监控客户名称"
          ></el-input>
          <el-select
            v-model="queryParams.subjectType"
            placeholder="请选择监控客户类型"
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
            placeholder="预警模型"
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
              placeholder: '状态',
            }"
            :options="collectionList.statusOptions"
            :flatten="true"
            :adaptiveWidth="{
              minWidth: '60px',
              enable: true,
            }"
          />
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="-"
            value-format="yyyy-MM-dd"
            start-placeholder="创建开始日期"
            end-placeholder="创建结束日期"
          >
          </el-date-picker>
        </div>
        <div class="operate">
          <el-button
            v-if="currentTab == 'list'"
            type="warning"
            @click="handleOpenAddTask()"
            >新增监测任务</el-button
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
          监测时间
          <el-tooltip effect="dark" placement="top">
            <div slot="content" class="tip">
              监测时间目前为1天,7天,30天<br />备注：目前页面上一旦选择监测时间不可更改
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </template>
        <template #slotHeader_monitorStatus="scope">
          监测状态
          <el-tooltip effect="dark" placement="top">
            <div slot="content" class="tip">
              点击按钮时弹出弹窗。<br />内容为：请确认是否将此任务进行关闭/开启。
              备注：新增时默认为关闭,监测名单无数据时强制关闭
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </template>
        <template #bt_handle="{ data }">
          <div class="operateBtns" v-if="currentTab == 'list'">
            <el-button type="text" @click="hadleOpenMonitoringList(data.row)"
              >监测名单列表</el-button
            >
            <el-button type="text" @click="handleOpenAddTask(data.row)"
              >编辑</el-button
            >
            <el-button
              class="delete"
              type="text"
              @click="handleDelete(data.row)"
              >删除</el-button
            >
          </div>
          <div class="operateBtns" v-else>
            <el-button type="text" @click="handleOpenDynamicsDetail(data.row)"
              >查看详情</el-button
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
      filterOptions: [
        { label: '监测列表', value: 'list' },
        { label: '监测动态', value: 'dynamics' },
      ],
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
      columnConfig: [
        {
          type: 'serial',
          label: '序号',
          width: '80',
        },
        {
          label: '任务编号',
          field: 'taskNo',
          width: '280',
        },
        {
          label: '申请用户',
          field: 'applyUser',
        },
        {
          label: '预警模型',
          field: 'processPolicyName',
        },
        {
          label: '监控主体',
          field: 'subjectType',
          type: 'tag',
          pairedList: 'ric_monitor_subject',
          styleList: 'subjectStyle',
        },
        {
          label: '监测时间',
          field: 'monitorDays',
          slotHeader: 'monitorTime',
          type: 'tag',
          pairedList: 'ric_monitor_time',
        },
        {
          label: '创建时间',
          field: 'createTime',
          type: 'timeStamp',
        },
        {
          label: '监测状态',
          field: 'status',
          slotHeader: 'monitorStatus',
          type: 'switch',
          activeValue: 1,
          inactiveValue: 0,
          activeText: '开启',
          inactiveText: '关闭',
        },
      ],
      recordColumnConfig: [
        {
          label: '客户名称',
          field: 'customerName',
          width: '',
        },
        {
          label: '风险程度',
          field: 'riskLevel',
          width: '',
          type: 'tag',
          pairedList: 'ric_monitor_riskLevel',
        },
        {
          label: '风险类别',
          field: 'riskType',
          width: '',
        },
        {
          label: '预警内容',
          field: 'warningContent',
          width: '',
        },
        {
          label: '预警时间',
          field: 'warningTime',
          width: '',
        },
      ],
      tableHandle: {
        fixed: 'right',
        width: '220',
        label: '操作',
        align: 'center',
        slot: true,
      },
      collectionList: {
        policyOptions: [],
        statusOptions: [
          { label: '开启', value: '1' },
          { label: '关闭', value: '0' },
        ],
        subjectStyle: new Map([
          [0, { color: 'var(--primary-color)', bgColor: true }],
          [1, { color: '#00B578', bgColor: true }],
        ]),
      },
    }
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
      this.$confirm(`确定删除该监测任务吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          deleteTask(data.taskId).then((res) => {
            if (res.code == 200) {
              this.$message.success('删除成功')
              this.getDataList()
            }
          })
        })
        .catch((err) => {})
    },
    handleChangeTaskStatus(data) {
      let str = data.status == 1 ? '开启' : '关闭'
      this.$confirm(`确定${str}该监测任务吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          changeTaskStatus(data).then((res) => {
            if (res.code == 200) {
              this.$message.success('修改成功')
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
          border: none;
          .el-icon-date {
            display: none;
          }
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
