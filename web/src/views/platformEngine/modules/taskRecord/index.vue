<template>
  <div class="afterLoan_detail">
    <div class="operation">
      <div class="search">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入任务编号"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-input
          v-model="queryParams.approvalUserName"
          placeholder="请输入申请用户"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-input
          v-model="queryParams.idNumber"
          placeholder="请输入证件号"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-input
          v-model="queryParams.processStrategy"
          placeholder="请输入流程策略"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入模型名称"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-input
          v-model="queryParams.evaluationResult"
          placeholder="请输入评审结果"
          suffix-icon="el-icon-search"
          clearable
          @clear="searchData"
        ></el-input>
        <el-button type="primary" class="search_btn" @click="reset">
          重置
        </el-button>
        <el-button
          type="primary"
          class="search_btn"
          icon="el-icon-plus"
          @click="openAddTask"
          v-if="hasButton('newly:added:show')"
        >
          新增
        </el-button>
      </div>
    </div>
    <div class="table">
      <div class="search-warp">
        <GutuSelectVue
          :data.sync="queryParams.businessCode"
          :options="sceneList"
          :config="{
            placeholder: '业务场景',
          }"
          flatten
          :adaptiveWidth="{
            minWidth: '100px',
            enable: true,
          }"
        />
        <GutuSelectVue
          :data.sync="queryParams.status"
          :options="[
            { label: '成功', value: '0' },
            { label: '失败', value: '1' },
            { label: '执行中', value: '2' },
          ]"
          :config="{
            placeholder: '状态',
          }"
          flatten
          :adaptiveWidth="{
            minWidth: '80px',
            enable: true,
          }"
        />
        <div class="filterTime">
          <!-- <span>申请时间</span> -->
          <el-date-picker
            v-model="createTime"
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="-"
            start-placeholder="申请开始日期"
            end-placeholder="申请结束日期"
          >
          </el-date-picker>
        </div>
        <!-- <div>
          <el-date-picker
            v-model="selectForm.time"
            type="datetimerange"
            @change="changeTime"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          >
          </el-date-picker>
        </div> -->
      </div>
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        border
        height="calc(var(--bgvh) - 272px)"
        :header-row-style="tableHeaderColor"
      >
        <el-table-column
          v-for="(item, index) in tableList"
          :key="index"
          :prop="item.value"
          :label="item.label"
          :fixed="item.fixed"
          :align="item.align || 'center'"
          :width="item.width"
          :min-width="item.minWidth || '100px'"
        >
          <template slot-scope="{ row }">
            <span v-if="item.scope === 'businessCode'">
              {{ mapSceneList(row[item.value]) }}
            </span>
            <!-- row[item.value] == 0
                    ? 'success'
                    : row[item.value] == 1
                    ? 'warning'
                    : '' -->
            <span v-if="item.scope === 'incorrect'">
              <el-tag :type="checkTagType(row)">{{ checkTag(row) }}</el-tag>
            </span>
            <span v-else-if="item.scope == 'dict'">
              {{ handleDict(item.pairedList, row[item.value]) }}
            </span>
            <span v-else-if="item.scope === 'approvalStatus'">
              <!-- {{ row[item.value]}} -->
              <el-tag v-if="row[item.value] == '-1'">{{ '无需审核' }}</el-tag>
              <el-tag v-if="row[item.value] == '0'" type="warning">{{
                '待审核'
              }}</el-tag>
              <el-tag v-if="row[item.value] == '1'" type="success">{{
                '已通过'
              }}</el-tag>
              <el-tag v-if="row[item.value] == '2'" type="danger">{{
                '已拒绝'
              }}</el-tag>
            </span>
            <span v-else-if="!item.scope">
              {{ row[item.value] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160px" fixed="right">
          <template slot-scope="{ row }">
            <div class="handler">
              <!-- <el-link :underline="false" type="" @click="openDrawer(row)">审批
                            </el-link> -->
              <el-link
                v-if="
                  row.hasOwnProperty('incorrect') &&
                  row.incorrect != 2 &&
                  row.creditStatus != 3
                "
                type="danger"
                :underline="false"
                @click="handleDetail(row)"
                >详情
              </el-link>
              <el-popover
                class="fail-popover"
                placement="bottom-end"
                width="240"
                trigger="hover"
                :popper-options="{ gpuAcceleration: true }"
                :append-to-body="false"
                :content="row.errorMsg"
                v-if="row.creditStatus == 3"
              >
                <el-button type="text" class="fail-button" slot="reference">
                  失败原因
                </el-button>
              </el-popover>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination">
      <el-pagination
        background
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
        :current-page.sync="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        layout="total,sizes,prev, pager, next, jumper"
        :total="totalNum"
      >
      </el-pagination>
    </div>
    <approve :info="info" ref="approve" />
    <detail ref="detail" :info="info" :sceneList="sceneList" />
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      :size="drawer.width"
      :before-close="handleClose"
    >
      <RiskReport
        v-if="drawer.type == 'riskReport'"
        ref="kycReportRef"
        :flag="flag"
        :pdfUrl="txdPdf"
        :judicialUrl="judicialLink"
        :data="orderNo"
      ></RiskReport>

      <!--  -->
      <AddRiskEval
        v-if="drawer.type == 'addRiskEval'"
        :workflowList="workflowList"
        @success="
          () => {
            handleClose()
            searchData()
          }
        "
        @close="handleClose"
      />
    </el-drawer>
    <!-- <kycReport
      ref="kycReportRef"
      :data='orderNo'
    ></kycReport> -->
  </div>
</template>

<script>
import { deleteById } from '@/api/dataRisk/riskStrategy.js'
import { getDicts } from '@/api/index'
import GutuSelectVue from '@/components/gutu/gutuSelect.vue'
import myUpload from '@/components/gutu/gutuUpload'
import Search from '@/components/pageSearch.vue'
import {
  getStatusType,
  getTaskList,
  getType,
  policyList,
} from '../../api/platformEngine'
import AddRiskEval from '../../components/addRiskEval.vue'
import approve from './approve.vue'
import detail from './detail.vue'
import KycReport from './kycReport'
import RiskReport from './riskReport'

export default {
  components: {
    Search,
    myUpload,
    approve,
    detail,
    KycReport,
    RiskReport,
    GutuSelectVue,
    AddRiskEval,
  },
  data() {
    return {
      statusData: [], // 所有的状态
      info: {},
      sceneList: [], // 场景列表
      activeBtn: 0,
      tableData: [],
      tableLoading: false,
      tableList: [
        {
          value: 'taskNumber',
          label: '任务编号',
          width: '260',
          align: 'left',
        },
        {
          value: 'applyName',
          label: '申请用户',
          width: '190px',
          align: 'left',
        },
        {
          value: 'customerName',
          label: '客户名称',
          width: '170px',
          align: 'left',
        },
        {
          value: 'idNumber',
          label: '证件号',
          width: '185px',
          align: 'left',
        },
        {
          value: 'businessCode',
          label: '业务场景',
          align: 'left',
          width: '140',
          scope: 'businessCode',
        },
        {
          value: 'processStrategy',
          label: '流程策略',
          // width: '130px',
          align: 'left',
        },
        // {
        //   value: 'nodeId',
        //   label: '审批节点',
        //   width: '240px',
        //   align: 'left',
        //   // scope: 'scope_time'
        // },
        {
          value: 'ruleName',
          label: '模型名称',
          width: '',
          align: 'left',
          // scope: 'scope_time'
        },
        // {
        //   value: 'evaluationResult',
        //   label: '评审结果',
        //   width: '100px',
        //   align: 'left'
        // },
        {
          value: 'incorrect',
          label: '状态',
          width: '80px',
          align: 'left',
          scope: 'incorrect',
        },
        {
          value: 'evaluationResult',
          label: '评级结果',
          width: '100px',
          align: 'left',
          scope: 'dict',
          pairedList: 'creditStatusList',
        },
        {
          value: 'approvalStatus',
          value: 'createTime',
          label: '申请时间',
          width: '200px',
          align: 'left',
          // scope: 'scope_time'
        },

        //
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        approvalUserName: null,
        businessCode: null,
        startTime: null,
        endTime: null,
        orderNo: null,
        status: null,
        customerName: null,
        processStrategy: null,
        ruleName: null,
        evaluationResult: null,
      },
      selectForm: {},
      totalNum: 0,

      orderNo: null,
      createTime: [],
      workflowList: [],
      creditStatusList: [],
      drawer: {
        title: null,
        visible: false,
        type: null,
        width: '740px',
      },
      flag: null,
      txdPdf: null,
      judicialLink: null,
    }
  },
  computed: {},
  watch: {
    createTime: {
      handler(val) {
        this.queryParams.startTime = val && val.length ? val[0] : null
        this.queryParams.endTime = val && val.length ? val[1] : null
      },
    },
    queryParams: {
      handler() {
        this.searchData()
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {
    // this.searchData()
    this.getType()
    this.getStatusType()
    this.getPolicy()
    getDicts('creditStatus')
      .then((res) => {
        if (res.code == 200) {
          this.creditStatusList = res.data
        }
      })
      .catch((err) => {})
  },
  methods: {
    // 获取审批状态
    getStatusType() {
      getStatusType('approval_status').then((res) => {
        this.statusData = res.data
      })
    },

    // 时间筛选
    changeTime(s) {
      if (s) {
        this.queryParams.startTime = s[0]
        this.queryParams.endTime = s[1]
      } else {
        this.queryParams.startTime = ''
        this.queryParams.endTime = ''
      }
      this.searchData()
    },
    handleDetail(row) {
      this.info = row
      this.orderNo = row.taskNumber
      if (row.hasOwnProperty('flag')) this.flag = row.flag
      else this.flag = null
      if (row.hasOwnProperty('txdPdf')) this.txdPdf = row.txdPdf
      else this.txdPdf = null
      if (row.hasOwnProperty('judicialLink'))
        this.judicialLink = row.judicialLink
      else this.judicialLink = null
      this.drawer.title = '风控评估报告'
      this.drawer.type = 'riskReport'
      this.drawer.width = '740px'
      this.drawer.visible = true
    },
    mapSceneList(code) {
      return this.sceneList.filter((item) => item.dictValue == code)[0]
        .dictLabel
    },
    // 获取业务场景
    getType() {
      getType('platform_business_scenario').then((res) => {
        if (res.code == 200) {
          this.sceneList = res.data
        }
      })
    },
    searchData() {
      this.tableLoading = true
      this.cancel('取消请求')
      getTaskList({ ...this.queryParams }, this)
        .then((res) => {
          this.tableData = res.data.list
          this.totalNum = res.data.total
          this.tableLoading = false
        })
        .catch((err) => {
          this.tableLoading = false
        })
    },
    cancel() {},
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.searchData()
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.searchData()
    },
    handleTag(status, item, data) {
      let list = item.list
      let returnData = list.find((itemX) => itemX.value === data)
      if (returnData) {
        return returnData[status]
      }
    },

    handleClose() {
      this.drawer.visible = false
      this.orderNo = this.$options.data().orderNo
    },
    openDrawer(row) {
      this.info = row
      this.$refs.approve.visible = true
    },

    handleDelete(row) {
      this.$confirm('是否确认删除这条记录?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          deleteById(row.id)
            .then((res) => {
              this.$message.success('操作成功')
              this.searchData()
            })
            .catch((err) => {})
        })
        .catch(function () {})
    },
    reset() {
      this.queryParams = this.$options.data().queryParams
      this.createTime = []
    },
    openAddTask() {
      this.drawer.width = '600px'
      this.drawer.type = 'addRiskEval'
      this.drawer.title = '新增风险评估'
      this.drawer.visible = true
    },
    getPolicy() {
      policyList({ pageNum: 1, pageSize: 999 }).then((res) => {
        if (res.code == 200) {
          this.workflowList = res.data.list
        }
      })
    },
    handleDict(list, field) {
      let pairedList = this[list]
      let data = pairedList.find((item) => item.dictValue == field + '')
      if (data) return data.dictLabel
      else return field
    },
    checkTagType(data) {
      if (data.creditStatus == 3) return 'danger'
      else {
        if (data.incorrect == 0) return 'success'
        else if (data.incorrect == 1) return 'warning'
        else return ''
      }
    },
    checkTag(data) {
      if (data.creditStatus == 3) return '拒绝'
      else {
        if (data.incorrect == 0) return '成功'
        else if (data.incorrect == 1) return '失败'
        else return '执行中'
      }
    },
  },
}
</script>

<style lang="less" scoped>
@import '/src/assets/less/commonCss.less';
::v-deep .el-dropdown-link {
  color: rgba(#000, 0.85);
}

.search-warp {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  padding-left: 20px;

  .search-dropdown {
    width: 160px;
  }
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.afterLoan_detail {
  padding: 20px;
  height: calc(var(--bgvh) - 42px);

  .operation {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .search {
      display: flex;

      ::v-deep .el-input {
        margin-right: 10px;
        .el-input__inner {
          width: 190px;
          height: 42px;
          background: #ffffff;
          border-radius: 4px 4px 4px 4px;
          border: none;
        }
      }

      .search_btn {
        height: 42px;
        font-size: 14px;

        .el-icon-plus {
          margin-right: 0;
        }
      }
    }

    .switchBtn {
      padding: 5px;
      background-color: #e5e9ec;
      display: flex;

      .btn {
        font-size: 14px;
      }

      .activeBtn {
        color: var(--primary-color);
        background-color: #fff;
      }
    }
  }

  .table {
    margin-top: 15px;
    margin-bottom: 0;

    > :not(:first-child) {
      border: none !important;
    }

    /deep/ .el-table {
      .el-table__header,
      .el-table__body {
        padding: 0px;
      }

      .el-table__cell {
        background-color: transparent;

        .fail-popover {
          .el-popover {
            position: fixed !important;
            font-size: 14px;
            font-weight: 400;
            color: #fa5151;
            line-height: 22px;
            white-space: pre-line;
          }
        }

        .fail-button {
          font-size: 14px;
          color: #fa5151;
        }
      }

      .el-table__fixed-header-wrapper {
        padding: 0px;
      }

      .el-table__fixed-body-wrapper {
        padding: 0px;
      }

      .cell {
        font-size: 16px;
        // font-family: PingFang SC;
        color: rgba(#000, 0.85);
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: center;
    background-color: #fff;
    padding: 20px;
  }

  .addCode {
    padding: 0px 30px;
    box-sizing: border-box;

    /deep/ .el-form {
      .el-select {
        width: 100%;
      }
    }

    .btnBotton {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      margin-top: 40px;

      > button {
        padding: 10px 20px;
        border-radius: 6px 6px 6px 6px;
      }

      > button:nth-of-type(1) {
        color: #fff;
        background: var(--primary-color);
      }

      > button:nth-of-type(2) {
        color: rgba(#000, 0.85);
        background-color: #f0f2f5;
        margin-left: 20px;
      }
    }
  }
}

.innerTable {
  padding: 15px 160px 15px 50px;
  box-sizing: border-box;
}

.handler {
  .el-link {
    margin-right: 20px;
  }
}

.filterTime {
  height: 100%;
  display: flex;
  align-items: center;
  font-size: 13px;

  /deep/.el-date-editor {
    width: 260px;
    border: none;
    .el-range__icon {
      display: none;
    }
    .el-range-separator {
      width: 20px;
    }
    .el-range-input {
      width: 50%;
      text-align: left;
      padding-left: 10px;
      background-color: rgba(#000, 0.05);
    }
  }
}
</style>
