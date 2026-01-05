<template>
  <div class="text-model">
    <div class="text-model-search">
      <div class="input-box">
        <el-input
          placeholder="请输入测试任务号"
          v-model="params.taskNo"
          clearable
          @input="paramsChange"
        ></el-input>
        <img src="../../../images/search.png" alt="search" />
      </div>
      <el-select
        v-model="params.testStatus"
        clearable
        placeholder="请选择测试状态"
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
        start-placeholder="创建开始时间"
        end-placeholder="创建结束时间"
        format="yyyy-MM-dd"
        value-format="yyyy-MM-dd"
        @change="paramsChange"
      >
      </el-date-picker>
    </div>
    <div class="text-model-wrapper">
      <div class="text-model-wrapper-table">
        <el-table
          v-loading="loadingShow"
          :data="tableData"
          border
          style="width: 100%"
          height="calc(var(--bgvh) - 316px)"
          :header-cell-style="tableHeaderColor"
        >
          <el-table-column
            prop="id"
            label="序号"
            align="center"
            width="80"
          ></el-table-column>
          <el-table-column
            prop="taskNo"
            label="测试任务号"
            align="center"
            width="200"
          ></el-table-column>
          <el-table-column label="模型名称" align="center" width="170">
            <el-tag type="primary" slot-scope="{ row }">{{
              row.modelName
            }}</el-tag>
          </el-table-column>
          <el-table-column
            prop="modelRemark"
            label="模型描述"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="modelVerson"
            label="模型版本号"
            align="center"
            width="200"
          ></el-table-column>
          <el-table-column label="业务场景" align="center" width="96">
            <el-tag type="primary" slot-scope="{ row }">{{
              row.businessName
            }}</el-tag>
          </el-table-column>
          <el-table-column
            prop="ruleName"
            label="策略场景"
            align="center"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            align="center"
            width="150"
          >
            <span slot-scope="{ row }">{{ parseTime(row.createTime) }}</span>
          </el-table-column>
          <el-table-column
            prop="name"
            width="120"
            label="测试drl文件"
            align="center"
          >
            <template slot-scope="{ row }">
              <el-popover
                placement="top"
                width="248"
                trigger="click"
                :popper-options="{ gpuAcceleration: true }"
                :append-to-body="false"
              >
                <div class="drl-content">
                  <div class="drl-content-header">
                    <div class="header-title">测试drl脚本</div>
                    <div class="header-copy" @click="copyText(testDrl)">
                      <img src="../../../images/copy-blue.png" alt="" />
                      <span>复制</span>
                    </div>
                  </div>
                  <div class="drl-content-inner">
                    {{ testDrl }}
                  </div>
                </div>
                <el-button
                  type="primary"
                  slot="reference"
                  @click="drlHandle(row.taskNo)"
                  >drl文件</el-button
                >
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column align="center" label="测试状态">
            <template slot-scope="{ row }">
              <el-tag
                :type="statusOptions[row.testStatus].type"
                v-if="statusOptions[row.testStatus]"
              >
                {{ statusOptions[row.testStatus].label }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="action">
        <el-tooltip
          class="item"
          effect="dark"
          content="点击触发当前展示版本的模型测试"
          placement="left-start"
        >
          <el-button type="success" @click="testHandle">测试</el-button>
        </el-tooltip>
      </div>
      <div class="text-model-wrapper-pagination">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :current-page="params.pageNum"
          :total="total"
          @current-change="currentChange"
        >
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getTestModelList,
  modelTest,
  selectDrl,
} from '@/views/decisionPlatform/modules/productDecision/api'
import { parseTime } from '@/utils/rouyi'

let timer
export default {
  name: 'textModel',
  props: {
    paramsData: { type: Object, default: () => {} },
  },
  data() {
    return {
      loadingShow: false,
      params: {
        pageNum: 1,
        pageSize: 10,
        taskNo: undefined, //任务号筛选框(支持模糊搜索)
        testStatus: undefined, //测试状态下拉框(0初始化 1运行中 2成功 3失败)
        startEndDate: undefined,
      },
      statusOptions: [
        { label: '初始化', value: 0, type: 'default' },
        { label: '运行中', value: 1, type: 'primary' },
        { label: '成功', value: 2, type: 'success' },
        { label: '失败', value: 3, type: 'danger' },
      ],
      tableData: [],
      total: 0,
      testDrl: '',
    }
  },
  watch: {
    paramsData: {
      handler(cur) {
        this.getList()
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    parseTime,
    paramsChange() {
      this.params.pageNum = 1
      this.getList()
    },
    /**
     * 获取列表数据
     * @param selfRefresh 是否为自刷新调用
     */
    getList(selfRefresh) {
      if (!selfRefresh) {
        this.loadingShow = true
      }
      const { startEndDate, ...newParams } = this.params
      // startTime: undefined,//时间区间筛选框-开始时间
      // 	endTime: undefined//时间区间筛选框-结束时间
      getTestModelList({
        ...newParams,
        ...this.paramsData,
        startTime: startEndDate ? `${startEndDate[0]} 00:00:00` : undefined,
        endTime: startEndDate ? `${startEndDate[1]} 23:59:59` : undefined,
      })
        .then((res) => {
          this.loadingShow = false
          if (res.code === 200) {
            const { list, total } = res.data
            this.tableData = list
            this.total = total
            if (list && list.length) {
              const resultStatus = list.map((item) => {
                return item.testStatus
              })
              if (
                resultStatus.indexOf(0) !== -1 ||
                resultStatus.indexOf(1) !== -1
              ) {
                if (timer) clearTimeout(timer)
                timer = setTimeout(() => {
                  this.getList(true)
                }, 5000)
              } else {
                clearTimeout(timer)
              }
            }
          }
        })
        .catch(() => {
          this.loadingShow = false
        })
    },
    clearTimer() {
      clearTimeout(timer)
    },
    drlHandle(taskNo) {
      selectDrl(taskNo).then((res) => {
        if (res.code === 200) {
          this.testDrl = res.data.testDrl
        }
      })
    },
    /**
     * drl文件复制
     * @param textToCopy
     */
    copyText(textToCopy) {
      const dom = document.createElement('input')
      dom.value = textToCopy
      document.body.appendChild(dom)
      dom.select()
      document.execCommand('copy')
      document.body.removeChild(dom)
      this.$message.success('文本已复制到剪贴板')
    },
    currentChange(num) {
      this.params.pageNum = num
      this.getList()
    },
    //测试按点击
    testHandle() {
      this.loadingShow = true
      Object.assign(this.params, {
        pageNum: 1,
        pageSize: 10,
        taskNo: undefined, //任务号筛选框(支持模糊搜索)
        testStatus: undefined, //测试状态下拉框(0初始化 1运行中 2成功 3失败)
        startEndDate: undefined,
      })
      modelTest(this.paramsData).then((res) => {
        if (res.code === 200) {
          this.getList()
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
.text-model {
  padding: 0 30px 30px;

  &-search {
    display: flex;
    align-items: center;

    ::v-deep .el-input {
      &__inner {
        width: 300px;
        height: 42px;
        line-height: 42px;
        border-radius: 4px;
        background: rgba(0, 0, 0, 0.04);
        border: none;
      }

      .el-select__caret {
        color: #1e2135;
        font-weight: 600;
        font-size: 15px;
      }
    }

    ::v-deep .el-date-editor {
      .el-icon-date {
        display: none;
      }

      .el-range-input {
        width: 50%;
        background: transparent;
      }

      .el-range-separator {
        line-height: 34px;
        color: rgba(0, 0, 0, 0.3);
      }
    }

    .input-box {
      display: flex;
      align-items: center;
      border: none;
      background: rgba(0, 0, 0, 0.04);

      ::v-deep .el-input {
        &__inner {
          width: 266px;
          background: transparent;
        }
      }

      img {
        width: 15px;
        height: 15px;
        margin-right: 14px;
      }
    }

    ::v-deep .el-select {
      margin: 0 10px;
    }
  }

  &-wrapper {
    margin-top: 30px;

    &-table {
      .drl-content {
        &-header {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .header-title {
            font-weight: 400;
            font-size: 12px;
            color: rgba(0, 0, 0, 0.85);
            line-height: 16px;
          }

          .header-copy {
            cursor: pointer;
            display: flex;
            align-items: center;
            font-size: 12px;
            color: var(--primary-color);
            line-height: 16px;

            img {
              width: 10px;
              height: 10px;
              margin-right: 4px;
            }
          }
        }

        &-inner {
          margin-top: 6px;
          font-size: 10px;
          color: rgba(0, 0, 0, 0.6);
          line-height: 16px;
        }
      }

      ::v-deep .el-button {
        &--primary {
          padding: 2px 10px;
          font-size: 12px;
          line-height: 16px;
          border-color: var(--primary-color);
          background: var(--primary-color);
        }
      }

      ::v-deep .el-tag {
        padding: 2px 6px;
        border: none;
        border-radius: 2px;
        font-size: 12px;
        line-height: 20px;

        &--primary {
          color: var(--primary-color);
          background: var(--primary-color-lighter);
        }

        &--success {
          color: #00b578;
          background: rgba(#00b578, 0.1);
        }

        &--danger {
          color: #fa5151;
          background: rgba(#fa5151, 0.1);
        }
      }
    }

    .action {
      text-align: right;
      margin: 30px 0;

      ::v-deep .el-button {
        &--success {
          width: 88px;
          height: 40px;
          font-weight: 500;
          font-size: 14px;
          background: #00b578;
          border-radius: 6px;
          border: none;
        }
      }
    }

    &-pagination {
      text-align: center;
      margin-top: 20px;
    }
  }
}
</style>
