<template>
  <div class="WorkflowEngine">
    <div class="content">
      <div class="header">
        <div class="left">
          <el-select
            v-model="queryParams.businessCode"
            :placeholder="$t('platformEngine.selectBusinessScene')"
            style="width: 100%; height: 100%"
            clearable
            @clear="getList"
          >
            <el-option
              v-for="(item, index) in sceneList"
              :key="index"
              :label="item.dictLabel"
              :value="item.dictValue"
            >
            </el-option>
          </el-select>
          <el-select
            v-model="queryParams.useIf"
            :placeholder="$t('platformEngine.selectUseIf')"
            style="width: 100%; height: 100%"
            clearable
            @clear="getList"
          >
            <el-option :label="$t('platformEngine.use')" :value="1" />
            <el-option :label="$t('platformEngine.disabled')" :value="0" />
          </el-select>
          <el-input
            :placeholder="$t('platformEngine.inputProcessStrategy')"
            style="height: 42px"
            clearable
            suffix-icon="el-icon-search"
            v-model="queryParams.processStrategy"
            @clear="getList"
          />
          <el-button style="margin-left: 20px" type="primary" @click="reset"
            >{{ $t('common.reset') }}
          </el-button>

          <el-button
            type="warning"
            style="background: #ff8f1f; margin-left: 10px"
            @click="add"
            >{{ $t('platformEngine.newProcessStrategy') }}
          </el-button>
        </div>
      </div>
      <div class="card-list" v-loading="loading" v-if="list.length > 0">
        <div class="card-item" v-for="(item, index) in list" :key="index">
          <div class="card-title">
            <div class="title-warp">
              <div class="title">{{ item.processStrategy }}</div>
              <div class="status successType" v-if="item.useIf == 1">
                {{ $t('platformEngine.enabled') }}
              </div>
              <div class="status errorType" v-else>
                {{ $t('platformEngine.disabled') }}
              </div>
            </div>
            <div class="btns-list">
              <div class="usage" @click="usageHandle(item)">
                {{ $t('platformEngine.processUsage') }}
              </div>

              <div class="edit" @click="edit(item)" style="cursor: pointer">
                <img
                  src="../../image/edit.png"
                  style="width: 16px; height: 16px"
                />
                <div class="text">{{ $t('common.edit') }}</div>
              </div>

              <template>
                <el-popconfirm
                  :title="$t('platformEngine.deleteConfirm')"
                  @confirm="del(item)"
                >
                  <div class="del" slot="reference">
                    <img
                      src="../../image/delete.png"
                      style="width: 16px; height: 16px"
                    />
                    <div class="text">{{ $t('common.delete') }}</div>
                  </div>
                </el-popconfirm>
              </template>
            </div>
          </div>
          <div class="card-content">
            <div class="info" style="margin-bottom: 20px">
              <div class="key">{{ $t('platformEngine.businessScene') }}</div>
              <div class="value">{{ mapScene(item.businessCode) }}</div>
            </div>
          </div>
          <div class="card-footer-text">
            <div class="dec">
              <div class="key">{{ $t('platformEngine.description') }}</div>
              <div class="value">{{ item.content ? item.content : '-' }}</div>
            </div>

            <!-- v-if="hasButton('platform:Enable Disable:show')" -->
            <el-switch
              v-model="item.useIf"
              @change="changeSwicth(item)"
              :active-value="1"
              :inactive-value="0"
              class="demo"
              :width="50"
              active-color="var(--primary-color)"
              inactive-color="#D6D6D6"
              :active-text="$t('platformEngine.enabled')"
              :inactive-text="$t('platformEngine.disabled')"
            >
            </el-switch>
          </div>
        </div>
      </div>
      <el-empty
        :image-size="200"
        v-if="list.length <= 0"
        style="height: calc(var(--bgvh)- 100px)"
      ></el-empty>

      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="query.pageNum"
          :page-size="query.pageSize"
          layout="prev, pager, next, jumper"
          :total="query.total"
        >
        </el-pagination>
      </div>
    </div>
    <Addflow
      ref="Addflow"
      :sceneList="sceneList"
      @getList="getList"
      :title="title"
    />
    <!--		流程使用-->
    <ProcessUsage ref="processUsageRef" />
  </div>
</template>

<script>
import Addflow from './Addflow.vue'
import {
  policyList,
  getType,
  getBusinessScenario,
  deletePolicy,
  updateStatus,
  policyUpdate,
  addModelChangeLog,
} from '../../api/platformEngine'

import ProcessUsage from '@/views/platformEngine/components/ProcessUsage'

export default {
  components: {
    Addflow,
    ProcessUsage,
  },
  data() {
    return {
      title: '',
      sceneList: [], // 业务场景字典名称
      query: {
        pageNum: 1,
        pageSize: 10,
        total: 0,
      },
      loading: false,
      options: [],
      queryParams: {
        businessCode: null,
        useIf: null,
        processStrategy: null,
      },
      value: '',
      list: [], // 列表
    }
  },
  mounted() {
    this.getType()
  },
  watch: {
    queryParams: {
      handler() {
        this.getList()
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {},
  methods: {
    usageHandle(item) {
      if (item.useIf != 1) {
        return this.$message.warning(
          this.$t('platformEngine.pleaseEnableProcessFirst')
        )
      }
      Object.assign(this.$refs.processUsageRef.processData, {
        id: item.id,
        processStrategy: item.processStrategy,
      })
      this.$refs.processUsageRef.drawer = true
    },
    // 编辑
    edit(item) {
      this.currentRow = JSON.parse(JSON.stringify(item))
      this.title = this.$t('platformEngine.edit')
      const addflowRef = this.$refs.Addflow
      addflowRef.drawer = true
      addflowRef.getInfo(item)
    },
    // 重置
    reset() {
      this.queryParams = this.$options.data().queryParams
      this.getList()
    },
    changeSwicth(item) {
      this.currentRow = {}
      const action =
        item.useIf == 0
          ? this.$t('platformEngine.disable')
          : this.$t('platformEngine.enable')
      this.$confirm(
        this.$t('platformEngine.enableOrDisableProcessConfirm', { action }),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          this.addModel({
            ...item,
            useing: item.useIf,
            operationType: item.useIf == 0 ? 4 : 3,
            processStrategyId: item.id,
          })
          updateStatus({ id: item.id, useIf: item.useIf }).then((res) => {
            if (res.code == 200) {
              this.getList()
            }
          })
        })
        .catch(() => {
          item.useIf = item.useIf == 0 ? 1 : 0
        })
    },
    // 删除
    del(item) {
      this.currentRow = JSON.parse(JSON.stringify(item))
      this.addModel({ operationType: 1 })
      deletePolicy({ id: item.id })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('platformEngine.deleteSuccess'))
            this.getList()
          }
        })
        .catch(() => {})
    },
    // 映射业务场景字段
    mapScene(code) {
      let arr = this.sceneList.filter((item, index) => item.dictValue == code)
      if (arr.length > 0) {
        return arr[0].dictLabel
      }
    },
    addModel(data) {
      let currentRow = this.currentRow
      if (currentRow) {
        currentRow.useing = currentRow ? currentRow.useIf : undefined
        currentRow.processStrategyId = currentRow ? currentRow.id : undefined
      }

      addModelChangeLog(
        this.filterMetaFields(Object.assign(currentRow || {}, data))
      )
        .then(() => {})
        .catch((err) => {})
    },
    filterMetaFields(obj) {
      let meta = {
        processStrategyId: null,
        processStrategy: null,
        productName: null,
        businessCode: null,
        content: null,
        operationType: null,
        useing: null,
      }
      const allowedKeys = Object.keys(meta)
      return Object.keys(obj)
        .filter((key) => allowedKeys.includes(key))
        .reduce((acc, key) => {
          acc[key] = obj[key]
          return acc
        }, {})
    },
    // 获取业务场景
    getType() {
      getBusinessScenario().then((res) => {
        if (res.code == 200) {
          this.sceneList = res.data.list.map((item) => {
            return {
              dictLabel: item.name,
              dictValue: item.id + '',
            }
          })
        }
      })
    },
    // 获取列表
    getList() {
      this.loading = true
      policyList({ ...this.query, ...this.queryParams }).then((res) => {
        if (res.code == 200) {
          this.loading = false
          this.list = res.data.list
          this.query.total = res.data.total
        }
      })
    },
    handleCurrentChange(data) {
      this.query.pqgeNum = data
      this.getList()
    },
    add() {
      this.title = this.$t('platformEngine.new')
      this.$refs.Addflow.drawer = true
    },
  },
}
</script>

<style lang="less" scoped>
.pagination {
  position: fixed;
  bottom: 0px;
  left: 0px;
  height: 60px;
  width: calc(100% - 50px);
  display: flex;
  margin-left: 20px;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
}

::v-deep .el-input__inner {
  height: 42px;
}

.WorkflowEngine {
  padding: 20px;
  height: calc(var(--bgvh) - 50px);
  box-sizing: border-box;

  .content {
    padding-bottom: 60px;

    .header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20px;

      .left {
        display: flex;

        .el-select {
          width: 240px;
          height: 42px;
          background: var(--bg-color);
          margin-right: 10px;
        }
      }

      .right {
        display: flex;

        .input-warp {
          width: 300px;
          height: 42px;
          background: var(--bg-color);
        }
      }
    }

    .card-list {
      height: calc(var(--bgvh) - 200px);
      overflow-y: auto;

      .card-item {
        width: 100%;
        height: 148px;
        background: var(--bg-color);
        border-radius: 6px 6px 6px 6px;
        padding: 20px;
        box-sizing: border-box;
        margin-bottom: 20px;

        .card-title {
          display: flex;
          justify-content: space-between;

          .title-warp {
            display: flex;

            .title {
              font-size: 20px;
              // // font-family: PingFang SC-Medium, PingFang SC;
              font-weight: 500;
              color: var(--text-color-secondary);
              margin-right: 20px;
            }

            .status {
              padding: 3px 8px;
              // height: 28px;
              text-align: center;
              line-height: 28px;
              font-size: 14px;
              // // font-family: PingFang SC-Regular, PingFang SC;
              font-weight: 400;
            }

            .successType,
            .errorType {
              border-radius: 5px;
            }

            .successType {
              background-color: #00b578;
              color: #ffffff;
            }

            .errorType {
              color: var(--text-color-secondary);
              background-color: var(--bg-color-lighter);
            }

            .unuse {
              background: var(--bg-color-lighter);
              color: var(--text-color-secondary);
            }
          }

          .btns-list {
            display: flex;

            .edit,
            .usage,
            .del {
              cursor: pointer;
              // width: 68px;
              padding: 0px 10px;
              height: 32px;
              display: flex;
              justify-content: center;
              align-items: center;
              border-radius: 4px;
            }

            .usage {
              color: #fff;
              font-size: 14px;
              font-weight: 400;
              background: #ff8f1f;
            }

            .del {
              background: var(--bg-color-lighter);
              margin-left: 10px;

              .text {
                font-size: 14px;
                font-weight: 400;
                color: var(--text-color-secondary);
                margin-left: 3px;
              }
            }
            .edit {
              background: var(--primary-color);
              margin-left: 10px;

              .text {
                font-size: 14px;
                font-weight: 400;
                color: #fff;
                margin-left: 3px;
              }
            }
          }
        }

        .card-content {
          display: flex;

          .info {
            display: flex;
            margin-right: 60px;

            .key {
              font-size: 16px;
              font-weight: 400;
              color: var(--text-color-tertiary);
            }

            .value {
              font-size: 16px;
              font-weight: 400;
              color: var(--text-color-secondary);
            }
          }

          .line {
            width: 1px;
            height: 22px;
            background: rgba(0, 0, 0, 0.08);
          }
        }

        .card-footer-text {
          display: flex;
          justify-content: space-between;

          .dec {
            display: flex;

            .key {
              font-size: 16px;
              font-weight: 400;
              color: var(--text-color-tertiary);
            }

            .value {
              font-size: 16px;
              font-weight: 400;
              color: var(--text-color-secondary);
            }
          }
        }
      }
    }
  }
}
</style>
