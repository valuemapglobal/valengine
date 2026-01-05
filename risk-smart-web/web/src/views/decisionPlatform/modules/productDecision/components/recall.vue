<template>
  <div class="recall-box">
    <div class="recall-box-title">模型名称：{{ $attrs.moduleName }}</div>
    <div class="search-box">
      <div class="form-box">
        <el-form :model="searchForm">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item>
                <el-input
                  v-model="searchForm.idNumOrCusName"
                  clearable
                  @input="inputChange"
                  placeholder="请输入流水号或客户名称"
                >
                  <i slot="prefix" class="el-input__icon el-icon-search"></i>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item>
                <el-date-picker
                  style="width: 100%"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  v-model="searchForm.time"
                  type="datetimerange"
                  range-separator="-"
                  start-placeholder="创建时间(起)"
                  end-placeholder="创建时间(止)"
                  @change="timeChange"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <div style="display: flex; gap: 10px">
                <el-button
                  type="primary"
                  style="height: 42px; width: 85px"
                  size="medium"
                  class="btn"
                  @click="showUpLoad = !showUpLoad"
                >
                  上传
                </el-button>
                <el-button
                  size="medium"
                  icon="el-icon-upload2"
                  @click="exportAB"
                  style="height: 42px"
                  type="warning"
                  >导出</el-button
                >
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="tab-box">
        <el-tabs ref="tabs" v-model="searchForm.isAll" @tab-click="handleClick">
          <el-tab-pane label="全部" name="0"></el-tab-pane>
          <el-tab-pane label="坏客户" name="1"></el-tab-pane>
        </el-tabs>
      </div>
      <el-table
        :data="tableData"
        border
        v-loading="loading"
        style="width: 100%"
        :header-row-style="tableHeaderColor"
        :header-cell-style="{
          background: '#F7FAFE',
          fontWeight: 600,
          color: '#191C31',
          height: '50px',
          borderColor: '#E9E9E9',
        }"
      >
        <el-table-column prop="taskNumber" label="交易流水号" width="180">
        </el-table-column>
        <el-table-column prop="customFlag" label="实际是否坏客户" width="180">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.customFlag"
              :active-value="0"
              @change="changeSwitch(scope.row)"
              :inactive-value="1"
            >
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="产品名称"> </el-table-column>
        <el-table-column prop="customerName" label="客户名称">
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"> </el-table-column>
      </el-table>
      <div class="pagination-box">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="searchForm.pageNum"
          :page-size="10"
          layout="prev, pager, next, jumper"
          :total="searchForm.total"
        >
        </el-pagination>
        <div class="">
          <el-button
            style="background: #e4e6ef; height: 42px; border: none; width: 82px"
            @click="prepage"
          >
            上一步
          </el-button>
          <el-button
            type="primary"
            style="height: 42px; width: 124px"
            @click="submit"
            :disabled="tableData.length > 0 ? false : true"
          >
            进行模型回溯
          </el-button>
        </div>
      </div>
    </div>
    <div class="tip-box">
      <div class="text-color">提示</div>
      <div>
        1.点击“进行模型回溯”后，系统将基于回溯列表信息重新进行风险评估，通过比较预测结果与实际结果来重新评估各版本模型的表现
      </div>
      <div>2.您可以在回溯记录中点击“详情”查看回溯结果。</div>
      <div>3.若回溯数据量较大，报告生成时间可能较长，请稍后查看报告</div>
    </div>
    <uploadDrawer
      title="正负样例数据上传"
      :isShow="showUpLoad"
      :searchForm="searchForm"
      @change="closeUpLoad"
    />
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import {
  exportAB,
  getTaskListByModelId,
  submitAB,
  updateCustomFlag,
} from '../api/riskModel'
import uploadDrawer from './uploadDrawer.vue'
export default {
  name: 'recall',
  components: {
    uploadDrawer,
  },
  props: ['ruleForm', 'activeId'],
  data() {
    return {
      loading: false,
      showUpLoad: false, // 上传抽屉
      tableData: [],
      fileList: [],
      searchForm: {
        pageNum: 1,
        pageSize: 10,
        customFlag: null,
        idNumOrCusName: null,
        startTime: null,
        endTime: null,
        total: 0,
        isAll: '0',
      },
      uploadHeader: {
        Authorization: getToken(),
      },
    }
  },
  methods: {
    clearForm() {
      this.searchForm = this.$options.data().searchForm
    },
    // 上一步
    prepage() {
      this.clearForm()
      this.$emit('prepage')
    },
    closeUpLoad() {
      this.showUpLoad = false
      this.searchForm = this.$options.data().searchForm
      this.getList()
    },
    inputChange() {
      this.searchForm.pageNum = 1
      if (!this.searchForm.idNumOrCusName) {
        this.searchForm.idNumOrCusName = null
      }
      this.getList()
    },
    // 导出
    exportAB() {
      let data = {
        // ruleCode: this.$attrs.ruleCode,
        ruleCode: this.ruleForm.moduleId,
        idNumOrCusName: this.searchForm.idNumOrCusName,
        startTime: this.searchForm.startTime,
        endTime: this.searchForm.endTime,
      }
      exportAB(data).then((res) => {
        if (res) {
          var elink = document.createElement('a')
          elink.download = `翔e贷系统-客户初筛-企业.xls`
          elink.style.display = 'none'
          let blob = new Blob([res], {
            type: 'application/octet-stream;charset=utf-8',
          })
          elink.href = URL.createObjectURL(blob)
          document.body.appendChild(elink)
          elink.click()
          document.body.removeChild(elink)
        }
      })
    },
    changeSwitch(row) {
      updateCustomFlag({ taskId: row.taskId, customFlag: row.customFlag })
        .then(() => {})
        .catch(() => {})
    },
    submit() {
      submitAB({
        ...this.ruleForm,
        abTestQueryDTO: { ...this.searchForm, ruleCode: this.activeId },
        rdeRule: this.ruleForm.rdeRule.join(';'),
      }).then((res) => {
        if (res.code == 200) {
          this.$message.success('操作成功')
          this.$emit('colseMask')
        }
      })
    },
    timeChange(val) {
      if (val) {
        this.searchForm.startTime = val[0]
        this.searchForm.endTime = val[1]
      } else {
        this.searchForm.startTime = null
        this.searchForm.endTime = null
      }
      this.searchForm.pageNum = 1
      this.getList()
    },
    getList() {
      this.loading = true
      this.$refs.tabs.$el.querySelector('.el-tabs__active-bar').style.width =
        '30px'
      getTaskListByModelId({
        ...this.searchForm,
        ruleCode: this.activeId,
      }).then((res) => {
        if (res.code == 200) {
          this.loading = false
          this.tableData = res.data.list
          this.searchForm.total = res.data.total
        }
      })
    },

    init() {
      //
      this.loading = true
    },
    handleCurrentChange(current) {
      this.searchForm.pageNum = current
      this.getList()
    },
    handleClick() {
      this.searchForm.pageNum = 1
      this.getList()
    },
  },
}
</script>

<style lang="less" scoped>
.recall-box {
  ::v-deep .el-range-separator {
    line-height: 35px;
  }

  &-title {
    font-size: 18px;
    font-family: PingFang SC, PingFang SC;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.85);
    margin-top: 24px;
  }

  .search-box {
    margin: 20px 0px 14px 0px;

    .form-box {
      ::v-deep .el-input--small .el-input__inner,
      .el-range-editor--small.el-input__inner {
        background: rgba(0, 0, 0, 0.04);
        height: 42px;
        border: none;
      }

      ::v-deep .el-range-editor--small .el-range__icon {
        display: none;
      }

      ::v-deep .el-range-editor--small .el-range-input {
        background-color: transparent;
      }
    }
  }

  .tab-box {
    border: 1px solid rgba(0, 0, 0, 0.08);
    padding-left: 14px;
    border-bottom: none;

    ::v-deep .el-tabs__nav-wrap::after {
      display: none;
    }

    ::v-deep .el-tabs__header {
      margin: 0 0 10px;
    }
  }

  .pagination-box {
    margin-top: 20px;
    display: flex;
    // justify-content: center;
    flex-direction: column;
    gap: 20px;
    align-items: center;
  }

  .tip-box {
    font-size: 13px;
    color: rgba(0, 0, 0, 0.45);
    line-height: 23px;

    .text-color {
      color: #ff8f1f;
      font-size: 14px;
    }
  }
}
/deep/.el-range-editor.el-input__inner {
  justify-content: space-between !important;
}
</style>
