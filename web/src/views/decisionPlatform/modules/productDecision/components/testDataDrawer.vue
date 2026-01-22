<template>
  <div class="testDataDrawer">
    <el-drawer
      :visible="drawer.visible"
      size="90%"
    >
      <div class="left">
        <span class="title">测试数据</span>
        <div class="search">
          <el-input
            v-model="searchForm.companyName"
            placeholder="请输入企业名称"
          ></el-input>
          <el-select
            v-model="searchForm.testState"
            :popper-append-to-body='false'
            placeholder="请选择测试状态"
          >
            <el-option
              v-for="(item,index) in statusList"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
          <el-button
            type="primary"
            class="searchBtn"
            @click="searchDataList"
          >搜索</el-button>
          <el-button
            class="step"
            :class="{'pass':step>=3}"
          >测试通过：{{step}} / 3</el-button>
        </div>
        <el-table
          :data="dataList"
          border
          height="650px"
          v-adaptive="{bottomOffset: 90}"
          v-loading="tableLoading"
          highlight-current-row
          ref="testTable"
          @row-click='(row)=>{
            getTestData(row)
          }'
        >
          <el-table-column
            v-for="(item,index) in testTableList"
            :key="index"
            :label="item.label"
            :prop="item.value"
            v-bind="item.config"
          >
            <template slot-scope="{row,$index}">
              <div v-if="!item.type">
                {{row[item.value]?row[item.value]:'-'}}
              </div>
              <div v-else-if="item.type =='tag'">
                <el-tag :type="handleTagType(row[item.value])">{{row[item.value]}}</el-tag>
              </div>
              <div v-else-if="item.type =='operate'">
                <el-button
                  type="text"
                  @click.stop
                  @click="startTest(row,$index)"
                >测试</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            @current-change="handleTestCurrent"
            :current-page.sync="searchParams.pageNum"
            :page-size="searchParams.pageSize"
            layout="prev, pager, next, jumper"
            :total="total"
          ></el-pagination>
        </div>
      </div>
      <div class="right">
        <div class="top">
          <span class="title">测试结果</span>
          <img
            @click="closeDrawer"
            src="@/assets/images/close_w.png"
          />
        </div>
        <div
          class="noData"
          v-if="!resultStatus"
        >
          <img src="@/assets/images/dataRisk/noData.png" />
          <span>您还未选择测试文件，点击左侧测试按钮试试吧~</span>
        </div>
        <div v-else>
          <div class="hitCount">
            <span>企业规则命中结果：{{hitTotal}}</span>
          </div>
          <el-table
            :data="testDataList"
            border
            v-loading="resultTableLoading"
            height="590px"
            v-adaptive="{bottomOffset: 150}"
          >
            <el-table-column
              v-for="(item,index) in testResultList"
              :key="index"
              :label="item.label"
              :prop="item.value"
              v-bind="item.config"
            >
              <template slot-scope="{row,$index}">
                <div v-if="item.type =='seq'">
                  {{$index + 1}}
                </div>
                <div v-if="!item.type">
                  {{row[item.value]?row[item.value]:'-'}}
                </div>
                <div v-else-if="item.type =='tag'">
                  <span v-if="row[item.value] == 0">{{'-'}}</span>
                  <el-tag
                    v-else
                    type="danger"
                  >{{row[item.value] == '1'?'命中':''}}</el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              background
              @current-change="handleResultCurrent"
              :current-page.sync="searchHitParams.pageNum"
              :page-size="searchHitParams.pageSize"
              layout="prev, pager, next, jumper"
              :total="resultTotal"
            ></el-pagination>
          </div>
          <div class="resultBtnList">
            <el-button
              @click="handleCheck(0)"
              :disabled='tableLoading || resultTableLoading'
            >验证失败</el-button>
            <el-button
              @click="handleCheck(1)"
              :disabled='tableLoading || resultTableLoading'
            >验证通过</el-button>
            <el-button
              @click="handleCheck(2)"
              :disabled='tableLoading || resultTableLoading'
            >验证通过，并切换下一条</el-button>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  searchFlowRecord,
  submitModelToTest,
  countHitNum,
  updateTestResult
} from "../api/index"
import { mapState } from 'vuex'
export default {
  props: {
    modelList: {
      type: Array,
      default: []
    },
    outStep: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      step: 0,
      searchForm: {
        companyName: '',
        testState: '全部'
      },
      drawer: {
        visible: false
      },
      statusList: [
        { label: '全部', value: '全部' },
        { label: '验证通过', value: '验证通过' },
        { label: '验证失败', value: '验证失败' },
        { label: '未测试', value: '未测试' },
      ],
      dataList: [],
      testDataList: [],
      searchParams: {
        pageNum: 1,
        pageSize: 10
      },
      searchHitParams: {
        pageNum: 1,
        pageSize: 20
      },
      total: 0,
      hitTotal: 0,
      resultTotal: 0,
      atPresentForm: {
        orderNo: null,
        index: 0
      },
      tableLoading: false,
      resultStatus: false,
      resultTableLoading: false,
      testTableList: [
        {
          label: '尽调ID', value: 'orderNo', config: {
            width: '185px',
            align: 'center'
          },
        },
        {
          label: '测试时间', value: 'testTime', config: {
            width: '140px',
            align: 'center'
          }
        },
        {
          label: '企业名称', value: 'companyName', config: {
            align: 'center'
          }
        },
        {
          label: '尽调方式', value: 'accessWay', config: {
            width: '100px',
            align: 'center'
          }
        },

        {
          label: '测试状态', value: 'testState', config: {
            width: '90px',
            align: 'center'
          },
          type: 'tag'
        },
        {
          label: '操作', value: '', config: {
            width: '70px',
            align: 'center',
            fixed: 'right'
          },
          type: 'operate'
        },
      ],
      testResultList: [
        {
          label: '序号', config: {
            width: '70px',
            align: 'center'
          },
          type: 'seq'
        },
        {
          label: '风险类型', value: 'riskType', config: {
            width: '110px',
            align: 'center'
          },
        },
        {
          label: '风险描述', value: 'riskDescription'
        },
        {
          label: '是否命中', value: 'hit', config: {
            width: '100px',
            align: 'center'
          },
          type: 'tag'
        },
      ],
      projectCode: null,
      ruleCode: null
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    'drawer.visible': {
      handler(val) {
        if (val) {
          this.init()
          this.reset()
        }
      }, deep: true
    },
    step: {
      handler(val) {
        this.$emit('update:outStep', val)
      }, deep: true
    },
    outStep: {
      handler(val) {
        this.step = JSON.parse(JSON.stringify(val))
      }, deep: true
    },
    'dataRisk.config': {
      handler(val) {
        this.projectCode = val.projectCode
        this.ruleCode = val.ruleCode
      }, deep: true, immediate: true
    }
  },

  computed: {
    ...mapState(['dataRisk'])
  },
  methods: {
    init() {
      this.searchDataList()
    },
    searchDataList(status) {
      let search = false
      let total = this.total
      let pageNum = this.searchParams.pageNum
      let pageSize = this.searchHitParams.pageSize
      if (status) {
        if (status == 2) {
          if (this.atPresentForm.index < this.dataList.length - 1) {
            this.atPresentForm.index++
            search = true
          } else if (this.atPresentForm.index >= this.dataList.length - 1 && Math.ceil(total / pageSize) > pageNum) {
            this.atPresentForm.index = 0
            this.searchParams.pageNum++
            search = true
          } else {
            this.$message('当前数据是最后一条数据')
          }
        }
      }

      this.tableLoading = true
      searchFlowRecord({
        ...this.searchForm,
        ...this.searchParams,
        projectCode: this.projectCode,
        ruleCode: this.ruleCode
      }).then((res) => {
        if (res.code == 200) {
          this.dataList = res.data.rdeModelSelectListVoList
          this.total = res.data.total
          this.step = res.data.accessTotal
          if (search) {
            let newData = this.dataList[this.atPresentForm.index]
            this.startTest(newData, this.atPresentForm.index)
          }
        }
        this.tableLoading = false
      }).catch((err) => {
        this.tableLoading = false
      });
    },
    handleTagType(status) {
      if (status) {
        return new Map([
          ["未测试", ""],
          ["验证失败", "danger"],
          ["验证通过", "success"]
        ]).get(status)
      }
    },
    startTest(data, index) {
      let type = true
      this.resultStatus = true
      this.resultTableLoading = true
      this.atPresentForm.orderNo = data.orderNo
      this.atPresentForm.index = index
      this.$nextTick(() => {
        this.$refs.testTable.setCurrentRow(data)
      })
      submitModelToTest({
        serialNumber: data.orderNo,
        modelIdList: this.modelList,
        projectCode: this.projectCode,
        ruleCode: this.ruleCode
      }).then((res) => {
        if (res.code == 200) {
          this.getTestData(data, type)
        }
      }).catch((err) => { });
    },
    getTestData(data, type = false) {
      this.resultTableLoading = true
      // this.resultStatus = !type
      countHitNum({
        orderNo: data.orderNo,
        ...this.searchHitParams,
        projectCode: this.projectCode,
        ruleCode: this.ruleCode
      }).then((res) => {
        if (res.code == 200) {
          this.testDataList = []
          this.resultTotal = 0
          this.hitTotal = 0
          this.testDataList = res.data.rdeModelTestResultList
          this.resultTotal = res.data.total
          this.hitTotal = res.data.hitNumber
          this.resultStatus = type
        }
        this.resultTableLoading = false
      }).catch((err) => {
        this.resultTableLoading = false
      });
    },
    handleCheck(status) {
      updateTestResult({
        serialNumber: this.atPresentForm.orderNo,
        state: new Map([
          [0, '验证失败'],
          [1, '验证通过'],
          [2, '验证通过'],
        ]).get(status),
        projectCode: this.projectCode,
        ruleCode: this.ruleCode
      }).then((res) => {
        if (res.code == 200) {
          this.searchDataList(status)
        }
      }).catch((err) => { });
    },
    closeDrawer() {
      this.reset()
      this.drawer.visible = false
    },
    reset() {
      this.searchForm = {
        companyName: '',
        testState: '全部'
      }
      this.searchParams = {
        pageNum: 1,
        pageSize: 10
      }
      this.searchHitParams = {
        pageNum: 1,
        pageSize: 20
      }
      this.dataList = []
      this.testDataList = []
      this.resultStatus = false
    },
    handleTestCurrent(val) {
      this.searchParams.pageNum = val
      this.searchDataList()
    },
    handleResultCurrent(val) {
      this.searchHitParams.pageNum = val
      this.searchDataList()
    },
  }
}
</script>

<style lang="less" scoped>
.testDataDrawer {
  /deep/.el-drawer {
    .el-drawer__header {
      display: none;
    }
    .el-drawer__body {
      padding: 30px 0px;
      display: flex;

      .left,
      .right {
        width: 50%;
        min-width: 600px;
        height: 100%;
        box-sizing: border-box;
        padding: 0px 30px;
      }
      .left {
        border-right: 1px rgba(#000, 0.08) solid;

        .title {
          display: block;
          font-size: 18px;
          color: #3f4254;
          margin-bottom: 30px;
          font-weight: 500;
        }
        .search {
          display: flex;
          margin-bottom: 20px;
          flex-wrap: wrap;
          .el-input {
            width: 200px;
            margin-right: 10px;
            margin-bottom: 10px;
          }
          .el-input__inner {
            background-color: rgba(#000, 0.04);
            height: 42px;
            // color: rgba(#000, 0.3);
            width: 200px;
          }

          .searchBtn {
            height: 40px;
            width: 90px;
          }

          .step {
            width: 144px;
            height: 40px;
            font-size: 14px;
            // font-family: PingFang SC-Regular;
            color: #ff8f1f;
            background-color: rgba(#ff8f1f, 0.1);
            border: none;
            display: flex;
            justify-content: center;
            align-items: center;
          }
          .pass {
            background-color: rgba(#00b578, 0.1);
            color: #00b578;
          }
        }
      }

      .right {
        .top {
          width: 100%;
          display: flex;
          justify-content: space-between;
          > img {
            width: 26px;
            height: 26px;
            cursor: pointer;
          }
        }
        .noData {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          > img {
            width: 110px;
          }
          > span {
            font-size: 18px;
            // font-family: PingFang SC-Medium, PingFang SC;
            font-weight: 500;
            color: #9e9e9e;
          }
        }
        .hitCount {
          width: 100%;
          height: 45px;
          font-size: 14px;
          // font-family: PingFang SC-Regular, PingFang SC;
          font-weight: 400;
          color: rgba(0, 0, 0, 0.85);
          margin-top: 20px;
          padding: 0px 20px;
          background-color: rgba(#2b8df1, 0.08);
          border-top-left-radius: 6px;
          border-top-right-radius: 6px;
          display: flex;
          align-items: center;
        }
        .resultBtnList {
          width: 100%;
          display: flex;
          align-items: center;
          justify-content: flex-end;
          .el-button {
            height: 42px;
            font-size: 14px;
            // font-family: PingFang SC-Regular, PingFang SC;
            font-weight: 400;
            border: none;
            background-color: #00b578;
            color: #fff;
          }
          .el-button:nth-child(1) {
            background-color: rgba(#fa5151, 0.1);
            color: #fa5151;
          }
        }
      }

      .el-table {
        .el-table__header {
          .el-table__cell {
            background-color: rgba(#0256ff, 0.04);
            font-size: 14px;
            color: rgba(#000, 0.85);
            // font-family: PingFang SC-Regular, PingFang SC;
            font-weight: 400;
          }
        }
      }
      .pagination {
        display: flex;
        justify-content: flex-end;
        background-color: #fff;
        padding: 20px;
      }
    }
  }
}
</style>