<template>
  <div class="process-task">
    <div class="process-task-header">
      <SliderFilter
        style="margin-right: 10px"
        :value="isBatch"
        @change="(value) => (isBatch = value)"
        :options="batchOptions"
      />
      <el-input
        v-if="!isBatch"
        placeholder="请输入任务编号"
        v-model="params.taskNo"
        clearable
        @input="paramsChange"
      >
        <img slot="prefix" src="../../image/search.png" alt="" />
      </el-input>
      <el-input
        v-else
        placeholder="请输入任务编号"
        v-model="params.batchNo"
        clearable
        @input="paramsChange"
      >
        <img slot="prefix" src="../../image/search.png" alt="" />
      </el-input>
      <el-input
        placeholder="请输入流程策略"
        v-model="params.processStrategy"
        clearable
        @input="paramsChange"
      >
        <img slot="prefix" src="../../image/search.png" alt="" />
      </el-input>
      <el-select
        v-model="params.responseForm"
        clearable
        placeholder="请选择响应形式"
        @change="paramsChange"
      >
        <el-option
          v-for="item in responseOptions"
          :key="item.dictValue"
          :label="item.dictLabel"
          :value="item.dictValue"
        >
        </el-option>
      </el-select>
      <el-select
        v-if="!isBatch"
        v-model="params.taskStatus"
        clearable
        placeholder="请选择状态"
        @change="paramsChange"
      >
        <el-option
          v-for="item in statusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-select
        v-else
        v-model="params.batchStatus"
        clearable
        placeholder="请选择状态"
        @change="paramsChange"
      >
        <el-option
          v-for="item in statusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-date-picker
        v-model="params.startEndDate"
        type="daterange"
        range-separator="-"
        start-placeholder="申请开始时间"
        end-placeholder="申请结束时间"
        format="yyyy-MM-dd"
        value-format="yyyy-MM-dd"
        @change="paramsChange"
      >
      </el-date-picker>
      <el-button type="primary" @click="onReset">重置</el-button>
    </div>
    <div class="process-task-wrapper">
      <el-table
        v-if="!isBatch"
        v-loading="loadingShow"
        :data="tableData"
        :border="false"
        style="width: 100%"
        height="calc(var(--bgvh) - 243px)"
      >
        <el-table-column
          label="序号"
          type="index"
          width="50"
          align="center"
          :index="indexAdd"
        ></el-table-column>
        <el-table-column
          prop="taskNo"
          label="任务编号"
          width="220"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="applicationUser"
          label="申请用户"
          width="200"
          align="center"
        ></el-table-column>
        <el-table-column label="流程入参" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.processEntry && row.processEntry.length"
              type="text"
              size="medium"
              @click="openDrawer('entryDetail', row.processEntry)"
              >详情</el-button
            >
          </template>
        </el-table-column>
        <el-table-column label="业务场景" width="180" align="center">
          <template slot-scope="{ row }">
            <el-tag
              type="primary"
              v-if="
                row.businessCode &&
                setShowValue(
                  row.businessCode,
                  businessList,
                  'businessCode',
                  'name'
                )
              "
            >
              {{
                setShowValue(
                  row.businessCode,
                  businessList,
                  'businessCode',
                  'name'
                )
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="processStrategy"
          label="流程策略"
          width="180"
          align="center"
        ></el-table-column>
        <el-table-column label="模型名称" width="240" align="center">
          <template
            slot-scope="{ row }"
            v-if="row.modelNameList && row.modelNameList.length"
          >
            <el-tooltip
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
        </el-table-column>
        <el-table-column label="申请时间" width="180" align="center">
          <span slot-scope="{ row }">
            {{ formatTime(row.createTime, 'yyyy-MM-dd HH:mm:ss') }}
          </span>
        </el-table-column>
        <el-table-column label="响应形式" width="160" align="center">
          <span slot-scope="{ row }">
            {{
              setShowValue(
                row.responseForm,
                responseOptions,
                'dictValue',
                'dictLabel'
              )
            }}
          </span>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template slot-scope="{ row }">
            <el-tag
              v-if="row.taskStatus"
              :type="
                setShowValue(row.taskStatus, statusOptions, 'value', 'type')
              "
            >
              {{ setShowValue(row.taskStatus, statusOptions) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="响应结果" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.responseForm === 1 && row.taskStatus === 3"
              type="text"
              @click="openDrawer('responseResult', row.taskNo, row.taskStatus)"
            >
              详情
            </el-button>
            <el-tooltip
              class="item"
              effect="dark"
              :content="row.failureMessage"
              placement="top-end"
            >
              <el-button
                type="text"
                v-if="row.responseForm === 1 && row.taskStatus == 4"
                >查看原因</el-button
              >
            </el-tooltip>
            <el-button
              v-if="row.taskStatus === 3 && row.responseForm === 2"
              type="text"
              @click="openDrawer('riskReport', row.taskNo)"
            >
              查看报告
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-table
        v-if="isBatch"
        v-loading="loadingShow"
        :data="tableData"
        :border="false"
        style="width: 100%"
        height="calc(var(--bgvh) - 243px)"
      >
        <el-table-column
          label="序号"
          type="index"
          width="50"
          align="center"
          :index="indexAdd"
        ></el-table-column>
        <el-table-column
          v-if="isBatch"
          prop="batchNo"
          label="批次号"
          width="220"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="applicationUser"
          label="申请用户"
          width="200"
          align="center"
        ></el-table-column>
        <el-table-column label="批次入参" align="center">
          <template slot-scope="{ row }">
            <el-button
              type="text"
              size="medium"
              @click="handleOpenBatchParams(row)"
              >详情</el-button
            >
          </template>
        </el-table-column>
        <el-table-column label="业务场景" width="180" align="center">
          <template slot-scope="{ row }">
            <el-tag
              type="primary"
              v-if="
                row.businessCode &&
                setShowValue(
                  row.businessCode,
                  businessList,
                  'businessCode',
                  'name'
                )
              "
            >
              {{
                setShowValue(
                  row.businessCode,
                  businessList,
                  'businessCode',
                  'name'
                )
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="processStrategy"
          label="流程策略"
          width="180"
          align="center"
        ></el-table-column>
        <el-table-column label="模型名称" width="240" align="center">
          <template
            slot-scope="{ row }"
            v-if="row.modelNameList && row.modelNameList.length"
          >
            <el-tooltip
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
        </el-table-column>
        <el-table-column label="申请时间" width="180" align="center">
          <span slot-scope="{ row }">
            {{ formatTime(row.createTime, 'yyyy-MM-dd HH:mm:ss') }}
          </span>
        </el-table-column>
        <el-table-column label="响应形式" width="160" align="center">
          <span slot-scope="{ row }">
            {{
              setShowValue(
                row.responseForm,
                responseOptions,
                'dictValue',
                'dictLabel'
              )
            }}
          </span>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template slot-scope="{ row }">
            <el-tag
              v-if="row.batchStatus && isBatch"
              :type="
                setShowValue(
                  row.batchStatus,
                  batchStatusOptions,
                  'value',
                  'type'
                )
              "
            >
              {{ setShowValue(row.batchStatus, batchStatusOptions) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="响应结果" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.batchStatus === 3 && row.responseForm === 1"
              type="text"
              @click="openBatchDetails(row)"
            >
              详情
            </el-button>
            <el-button
              v-if="row.batchStatus === 3 && row.responseForm === 2"
              type="text"
              @click="openBatchDetails(row)"
            >
              查看报告
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="total, prev, pager, next, jumper"
        :current-page="params.pageNum"
        @current-change="currentChange"
        :total="total"
      >
      </el-pagination>
    </div>
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      :modal="false"
      :wrapperClosable="false"
      :before-close="closeDrawer"
      :size="drawer.size"
    >
      <div
        class="drawer-wrapper"
        v-loading="drawer.type === 'responseResult' && responseLoading"
      >
        <!--				入参详情-->
        <div class="entry-detail" v-if="drawer.type === 'entryDetail'">
          <el-table
            :data="responseTableData"
            style="width: 100%"
            :header-cell-style="tableHeaderColor"
          >
            <el-table-column prop="name" label="参数名称"></el-table-column>
            <el-table-column prop="nameZh" label="参数说明"></el-table-column>
            <el-table-column label="参数类型" align="center">
              <el-tag type="primary" slot-scope="{ row }">{{
                row.typeName
              }}</el-tag>
            </el-table-column>
            <el-table-column label="是否必填" align="center">
              <span slot-scope="{ row }">{{
                row.isRequired ? '是' : '否'
              }}</span>
            </el-table-column>
            <el-table-column
              prop="value"
              label="参数值"
              align="center"
            ></el-table-column>
          </el-table>
        </div>
        <div class="response-result" v-if="drawer.type === 'responseResult'">
          <json-viewer
            v-if="Object.keys(currentResponse).length"
            :value="JSON.parse(JSON.stringify(currentResponse))"
            :expand-depth="5"
            expanded
            copyable
          />
        </div>
      </div>
    </el-drawer>
    <BatchDetails
      ref="batchDetailsRef"
      @openDrawer="openDrawer"
      :businessList="businessList"
      :responseOptions="responseOptions"
      :statusOptions="statusOptions"
    />
  </div>
</template>

<script>
import SliderFilter from '@/components/SliderFilter.vue'
import BatchDetails from './components/BatchDetails.vue'
import {
  dataResponseForm,
  dataResponseFailure,
  getType,
  taskRecordList,
  batchList,
} from '@/views/platformEngine/api/platformEngine'
import { business_search } from '@/views/decisionPlatform/modules/productDecision/api'
import { formatTime, setShowValue } from '@/utils'

export default {
  components: {
    SliderFilter,
    BatchDetails,
  },
  data() {
    return {
      params: {
        pageNum: 1,
        pageSize: 10,
        taskNo: null,
        processStrategy: null,
        responseForm: null,
        taskStatus: null,
        startEndDate: null,
      },
      //响应形式--选项
      responseOptions: [],
      //业务场景
      businessList: [],
      //状态选项
      statusOptions: [
        { label: '初始化', value: 1, type: 'default' },
        { label: '生成中', value: 2, type: 'primary' },
        { label: '生成成功', value: 3, type: 'success' },
        { label: '生成失败', value: 4, type: 'danger' },
      ],
      batchStatusOptions: [
        { label: '待处理', value: 1, type: 'default' },
        { label: '处理中', value: 2, type: 'primary' },
        { label: '全部成功', value: 3, type: 'success' },
        { label: '部分失败', value: 4, type: 'danger' },
        { label: '全部失败', value: 5, type: 'danger' },
        { label: '校验失败', value: 6, type: 'danger' },
      ],
      loadingShow: false,
      tableData: [],
      total: 0,
      drawer: {
        type: undefined,
        title: undefined,
        visible: false,
        size: '33%',
      },
      //入参表格数据
      responseTableData: [],
      responseLoading: false,
      //当前返回结果
      currentResponse: {},

      isBatch: 0,
      batchOptions: [
        {
          label: '单次',
          value: 0,
        },

        {
          label: '批量',
          value: 1,
        },
      ],
    }
  },
  watch: {
    isBatch: {
      handler() {
        this.onReset()
      },
    },
  },
  mounted() {
    this.init()
    this.getList()
  },
  methods: {
    formatTime,
    setShowValue,
    onReset() {
      this.params = this.$options.data().params

      this.getList()
    },
    openDrawer(type, data, riskStatus) {
      switch (type) {
        case 'entryDetail':
          Object.assign(this.drawer, {
            type,
            title: '入参详情',
            size: '60%',
          })
          this.responseTableData = eval(data)
          break
        case 'responseResult':
          Object.assign(this.drawer, {
            type,
            title: '数据结果',
            size: '33%',
          })
          this.responseLoading = true
          dataResponseForm({ taskNo: data })
            .then((res) => {
              this.currentResponse = res
              this.responseLoading = false
            })
            .catch(() => {
              this.responseLoading = false
            })
          break
        case 'riskReport':
          window.open(
            `/saas/#/PlatformEngine/RiskReport?taskNo=${data}`,
            '_blank'
          )
          return
          break
      }
      this.drawer.size = '33%'
      this.drawer.visible = true
    },
    openBatchDetails(row) {
      this.$refs.batchDetailsRef.handleOpen(row)
    },
    closeDrawer() {
      this.drawer.visible = false
      this.currentResponse = {}
    },
    init() {
      //获取响应形式字典
      getType('model_response_form').then((res) => {
        this.responseOptions = res.data
      })
      business_search().then((res) => {
        this.businessList = res.data.list.map((item) => {
          return {
            ...item,
            businessCode: item.id,
          }
        })
      })
    },
    paramsChange() {
      this.params.pageNum = 1
      this.getList()
    },
    /**
     * 获取任务列表
     */
    getList() {
      this.loadingShow = true
      const { startEndDate, ...newParams } = this.params
      let URL = this.isBatch ? batchList : taskRecordList
      URL({
        ...newParams,
        startTime: startEndDate ? `${startEndDate[0]} 00:00:00` : undefined,
        endTime: startEndDate ? `${startEndDate[1]} 23:59:59` : undefined,
      })
        .then((res) => {
          this.loadingShow = false
          if (res.code === 200) {
            const { list, total } = res.data
            this.tableData = list
            this.total = total
          }
        })
        .catch(() => {
          this.loadingShow = false
        })
    },
    // 下载
    handleOpenBatchParams(row) {
      window.open(row.fileUrl, '_blank')
    },
    /**
     * 自定义索引
     * @param index
     * @returns {*}
     */
    indexAdd(index) {
      return index + 1 + (this.params.pageNum - 1) * this.params.pageSize
    },
    setModelNameList(modelName) {
      if (typeof modelName === 'string') {
        return modelName.split(',')
      } else {
        return modelName || []
      }
    },
    /**
     * currentPage 改变时会触发
     * @param num
     */
    currentChange(num) {
      this.params.pageNum = num
      this.getList()
    },
  },
}
</script>

<style lang="less" scoped>
.process-task {
  height: calc(var(--bgvh) - 42px);
  padding: 20px;

  &-header {
    display: flex;

    ::v-deep .el-input {
      width: 200px;

      & + .el-input,
      & + .el-select,
      & + .el-date-editor {
        margin-left: 10px;
      }

      .el-input__prefix {
        left: 10px;
        display: flex;
        align-items: center;

        img {
          width: 20px;
          height: 20px;
        }
      }

      .el-input__suffix {
        top: 2px;
      }

      &__inner {
        height: 48px;
        line-height: 48px;
        border-radius: 6px;
        font-weight: 400;
        font-size: 14px;

        &::placeholder {
          color: var(--text-color-tertiary);
        }
      }

      &.el-input--prefix .el-input__inner {
        padding-left: 36px;
      }
    }

    ::v-deep .el-select {
      & + .el-select,
      & + .el-date-editor {
        margin-left: 10px;
      }

      .el-input {
        .el-select__caret {
          font-size: 14px;
          font-weight: 600;
          color: var(--text-color-tertiary);
        }
      }
    }

    ::v-deep .el-date-editor {
      width: 280px;
      height: 42px;

      .el-range-input {
        font-size: 14px;

        &::placeholder {
          color: var(--text-color-tertiary);
        }
      }

      .el-range-separator {
        line-height: 34px;
        color: var(--text-color-tertiary);
      }
    }

    ::v-deep .el-button {
      margin-left: 10px;
      font-size: 14px;
      padding: 13px 20px;
      height: 42px;
      border-radius: 6px;
      border: none;

      &--primary {
        background: var(--primary-color);
      }
    }
  }

  ::v-deep .el-table {
    .el-tag {
      height: 22px;
      line-height: 22px;
      padding: 0 6px;
      font-weight: 400;
      font-size: 12px;
      border-radius: 2px;
      border: none;

      & + .el-tag {
        margin-top: 10px;
      }

      &--success {
        color: #00b578;
        background: rgba(#00b578, 0.1);
      }

      &--danger {
        color: #fa5853;
        background: rgba(#fa5853, 0.1);
      }

      &--primary {
        color: var(--primary-color);
        background: var(--primary-color-lighter);
      }
    }
  }

  &-wrapper {
    margin-top: 21px;
    padding: 20px;
    background: var(--bg-color);
    border-radius: 6px;

    ::v-deep .el-pagination {
      text-align: center;
      margin-top: 20px;
    }
  }

  // .drawer-header {
  //   padding: 30px;
  //   display: flex;
  //   align-items: center;
  //   justify-content: space-between;

  //   &-title {
  //     font-weight: 500;
  //     font-size: 14px;
  //     color: #3f4254;
  //     line-height: 26px;
  //   }

  //   &-icon {
  //     cursor: pointer;

  //     img {
  //       width: 26px;
  //       height: 26px;
  //     }
  //   }
  // }

  .drawer-wrapper {
    height: calc(100% - 87px);
    overflow-y: auto;

    ::v-deep .jv-container {
      background: var(--bg-color);
      .js-code,
      .js-node,
      .jv-push,
      .jv-key,
      .jv-value {
        color: var(--text-color) !important;
      }
    }

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 4px;
      background-color: rgba(193, 193, 193, 1);
    }

    &::-webkit-scrollbar-track {
      border-radius: 4px;
      background-color: rgba(241, 241, 241, 1);
    }
  }
}
</style>
