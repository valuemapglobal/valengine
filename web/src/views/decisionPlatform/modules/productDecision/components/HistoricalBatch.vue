<template>
  <div class="submitReview">
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      :size="drawer.size"
      :before-close="handleClose"
    >
      <div class="sr">
        <div class="sr-content">
          <div class="history-card">
            <div v-for="(item, index) in historyList" :key="index">
              <div
                class="card-item"
                :style="{
                  borderColor: `${handleStyle(item.status).color}`,
                }"
              >
                <div class="flex-between top">
                  <span class="batch-title">{{ item.batchTitle }}</span>
                  <span class="batch-time">{{ item.submittedAt }}</span>
                </div>
                <div class="content">
                  <p class="content-label">批次编号</p>
                  <p class="content-value">{{ item.batchCode }}</p>
                  <p class="content-label">变更描述</p>
                  <p class="content-value">{{ item.changeDescription }}</p>
                  <p class="content-label">审核状态</p>
                  <p :style="{ color: handleStyle(item.status).color }">
                    {{ statusMap.get(item.status) }}
                    {{ item.approvedAt ? `  -  ${item.approvedAt}` : '' }}
                  </p>
                  <div
                    class="content-approval"
                    v-if="
                      ['REJECTED', 'APPROVED'].includes(item.status) &&
                      item.rejectReason
                    "
                    :style="{
                      borderColor: `${handleStyle(item.status).color}`,
                      color: `${handleStyle(item.status).color}`,
                    }"
                  >
                    <p style="margin-bottom: 8px">审批意见</p>
                    <p>{{ item.rejectReason }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="historyList.length === 0" class="noData">
            <img src="@/assets/images/comment/noData.png" alt="" />
            <p>暂无历史策略批次数据</p>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import { mapState } from 'vuex'

import { approvalHistory, notificationsMarkAsRead } from '../api'

export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  data() {
    return {
      drawer: {
        title: '历史策略批次',
        visible: false,
        size: '30%',
      },

      queryParams: {
        tabIndex: null,
      },

      historyList: [],

      tabList: [
        {
          label: '全部',
          value: null,
          data: 999,
          type: 'primary',
        },
        {
          label: '审核中',
          value: 1,
          data: 1,
          type: 'warning',
        },
        {
          label: '已发布',
          value: 2,
          data: 2,
          type: 'success',
        },
        {
          label: '审核失败',
          value: 3,
          data: 3,
          type: 'danger',
        },
      ],
      activeTab: 'historicalBatch',

      statusMap: new Map([
        ['PENDING_APPROVAL', '审核中'],
        ['APPROVED', '已发布'],
        ['REJECTED', '审核失败'],
      ]),
      colorMap: new Map([
        [
          null,
          {
            bgColor: '#fff',
            tagColor: '#E5E7EB',
            color: '#000',
          },
        ],
        [
          'PENDING_APPROVAL',
          {
            bgColor: '#FFF7ED',
            tagColor: '#FED7AA',
            color: '#EDBE2C',
          },
        ],
        [
          'APPROVED',
          {
            bgColor: '#F0FDF4',
            tagColor: '#DCFCE7',
            color: '#23C55E',
          },
        ],
        [
          'REJECTED',
          {
            bgColor: '#FEF2F2',
            tagColor: '#FEE2E2',
            color: '#EF4444',
          },
        ],
      ]),
    }
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  methods: {
    getHistoryList() {
      let params = {
        projectCode: this.dataRisk.decision.projectCode,
        businessCode: this.dataRisk.decision.businessCode,
        ruleCode: this.dataRisk.decision.ruleCode,
      }
      notificationsMarkAsRead(params).then((res) => {
        if (res.code == 200) {
          this.$emit('isRead')
        }
      })
      approvalHistory(params).then((res) => {
        if (res.code == 200) {
          this.historyList = res.data.list
        }
      })
    },
    handleTab(item) {
      this.queryParams.tabIndex = item.value
    },
    handleStyle(item) {
      let style = this.colorMap.get(item)

      if (style) {
        return style
      } else return {}
    },
    handleOpen() {
      this.getHistoryList()
      this.drawer.visible = true
    },
    handleClose() {
      this.drawer.visible = false
    },
  },
}
</script>
<style lang="less" scoped>
.submitReview {
  :deep(.el-drawer) {
    .el-drawer__header {
      color: #000;
      margin-bottom: 0;
      padding: 20px;
    }
    .el-drawer__body {
      padding: 0px 20px 20px;
    }
  }

  :deep(.el-form) {
    .el-form-item__label {
      font-weight: 500;
      font-size: 14px;
      color: #000;
      padding-bottom: 0;
    }

    .el-input {
      .el-input__inner {
        height: 40px;
      }
    }
  }

  .tip {
    font-size: 13px;
    color: rgba(0, 0, 0, 0.6);
  }

  .sr {
    display: flex;
    flex-direction: column;
    height: 100%;
    &-search {
      margin-bottom: 10px;
      display: flex;
      flex-direction: column;

      :deep(.el-input) {
        margin-top: 16px;
        .el-input__inner {
          height: 40px;
        }
      }

      .tabList {
        display: flex;
        align-items: center;
        .tabItem {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 8px 12px;
          border-radius: 4px;
          font-size: 14px;
          cursor: pointer;
          border: solid 1px #e5e5e5;
          margin-right: 16px;
          border-radius: 8px;
          &_data {
            border-radius: 50px;
            padding: 1px 6px;
            background-color: #f5f7fa;
            color: #999;
            margin-left: 16px;
            font-size: 12px;
          }
          &.active {
            background-color: #409eff;
            color: #fff !important;
            border-color: #409eff;
          }
        }
      }
    }

    &-content {
      flex: 1;
      overflow-y: auto;
      .history-card {
        margin-bottom: 16px;
        .card-item {
          padding: 4px 16px;
          margin-bottom: 16px;
          border-left: solid 4px;

          p {
            margin-bottom: 0px;
            font-size: 14px;
          }

          .top {
            margin-bottom: 16px;
            .batch-title {
              color: var(--text-color-secondary);
              margin-right: 16px;
            }
            .batch-tag {
              padding: 4px 12px;
              border-radius: 20px;
              font-size: 12px;
              color: #fff;
            }
            .batch-time {
              font-size: 14px;
              color: var(--text-color-tertiary);
            }
          }

          .content {
            .content-label {
              font-size: 14px;
              color: var(--text-color-secondary);
            }
            .content-value {
              font-size: 14px;
              color: var(--text-color-secondary);
              margin-bottom: 8px;
            }
            .content-approval {
              padding: 8px;
              border: solid 1px;
              margin-top: 8px;
              border-radius: 4px;
            }
          }
        }
      }
    }
  }
}
.flex {
  display: flex;
  align-items: center;
}
.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.flex-column {
  display: flex;
  flex-direction: column;
}
.noData {
  height: calc(100% - 100px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  img {
    width: 100px;
  }
}
</style>
