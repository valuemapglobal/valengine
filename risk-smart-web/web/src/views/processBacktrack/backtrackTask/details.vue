<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
    :destroy-on-close="true"
    :show-close="true"
  >
    <div class="bt-details">
      <div class="card">
        <div class="title">任务基本信息</div>
        <div class="card-content" v-if="baseInfo">
          <div
            class="card-item"
            v-for="(item, index) in sourceDataList"
            :key="index"
          >
            <div class="card-item-label">{{ item.label }}</div>
            <div v-if="item.type == 'time'" class="card-item-value">
              {{
                baseInfo[item.field]
                  ? formatTime(baseInfo[item.field], 'yyyy-MM-dd HH:mm:ss')
                  : '-'
              }}
            </div>
            <div
              v-else-if="item.field == 'processStrategies'"
              class="card-item-value"
            >
              <el-tag
                v-for="strategy in baseInfo[item.field]"
                :key="strategy"
                >{{ strategy }}</el-tag
              >
              <!-- {{ baseInfo[item.field] || '-' }} -->
            </div>
            <div v-else-if="item.field == 'status'" class="card-item-value">
              {{ handleStatus(baseInfo[item.field]) }}
            </div>
            <div v-else class="card-item-value">
              {{ baseInfo[item.field] || '-' }}
            </div>
          </div>
        </div>
      </div>
      <div class="title">模型对比结果</div>
      <GutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :tableHeight="380"
        :params="params"
      >
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
      </GutuTable>
      <GutuPagination
        :total="total"
        :pageSize="params.pageSize"
        @searchData="updateParams"
      />
    </div>
  </el-drawer>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
import { formatTime } from '@/utils'
import { tracebackTasksDetail } from '../api'
export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '回溯任务详情',
        width: '60%',
      },
      dataList: [],
      loading: false,
      columnConfig: [
        {
          label: '序号',
          type: 'serial',
          width: '60',
        },
        {
          label: '流程任务编号',
          field: 'historicalTaskNo',
          width: '240',
        },
        {
          label: '模型名称',
          field: 'modelNameList',
          width: null,
          type: 'slot',
        },
        {
          label: '历史结果（评分）',
          field: 'historicalTotalScore',
          width: '160',
        },
        {
          label: '预期结果',
          field: 'expectedTotalScore',
          width: '160',
        },
        {
          label: '实际结果',
          field: 'actualTotalScore',
          width: '160',
        },
      ],
      sourceDataList: [
        {
          label: '回溯任务号',
          field: 'tracebackNo',
        },
        {
          label: '创建时间',
          field: 'createdAt',
          type: 'time',
        },
        {
          label: '流程策略集',
          field: 'processStrategies',
          type: 'slot',
        },
        {
          label: '任务状态',
          field: 'status',
          type: 'tag',
        },
      ],
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      total: 0,
      tracebackNo: null,
      baseInfo: null,

      taskStatusOptions: [
        { label: '任务正在创建中', value: 'GENERATING' },
        { label: '(子任务状态) 等待执行', value: 'PENDING' },
        { label: '任务正在执行中', value: 'PROCESSING' },
        { label: '(子任务状态) 执行成功', value: 'SUCCESS' },
        { label: '(子任务状态) 执行失败', value: 'FAILED' },
      ],
    }
  },
  methods: {
    formatTime,
    getData() {
      this.loading = true
      tracebackTasksDetail(this.tracebackNo)
        .then((res) => {
          if (res.code == 200) {
            this.baseInfo = res.data
            this.dataList = res.data.taskDetails || []
            this.total = Number(res.data.totalCount)
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    updateParams(data) {
      this.params.pageNum = data.pageNum
      this.getData()
    },
    handleStatus(status) {
      return (
        this.taskStatusOptions.find((item) => item.value == status)?.label ||
        '-'
      )
    },
    handleOpen(data) {
      this.tracebackNo = data.tracebackNo
      this.baseInfo = null
      this.dataList = []
      this.total = 0
      this.drawer.visible = true
      this.getData()
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
.bt-details {
  .card {
    padding: 16px;
    background-color: var(--bg-color-lighter);
    margin-bottom: 20px;
    .card-content {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
      .card-item {
        display: flex;
        flex-direction: column;
        &-label {
          font-size: 14px;
          color: var(--text-color-tertiary);
        }
        &-value {
          font-size: 14px;
          color: var(--text-color);
        }
      }
    }
  }
  .gutuPagination {
    margin-top: 20px;
  }

  .title {
    font-size: 16px;
    color: var(--text-color);
    margin-bottom: 16px;
    position: relative;

    // &::before {
    //   content: '';
    //   display: block;
    //   left: 0;
    //   top: 50%;
    //   transform: translateY(-50%);
    //   position: absolute;
    //   width: 2px;
    //   height: 16px;
    //   background-color: var(--primary-color);
    // }
  }
}
</style>
