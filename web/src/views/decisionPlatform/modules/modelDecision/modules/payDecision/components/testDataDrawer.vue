<template>
  <div class="testDataDrawer">
    <el-drawer
      :visible="drawer.visible"
      size="90%"
    >
      <div class="left">
        <span class="title">测试数据</span>
        <div class="search">
          <GTInput
            v-model="searchForm.companyName"
            placeholder="请输入个体名称"
            @keyup.enter.native="searchDataList"
          >
            <i
              slot="suffix"
              class="el-icon-search"
              @click="searchDataList"
            />
          </GTInput>
          <!--					<el-select
                      v-model="searchForm.testState"
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
                    >搜索
                    </el-button>
                    <el-button
                      class="step"
                      :class="{'pass':step>=3}"
                    >测试通过：{{ step }} / 3
                    </el-button>-->
        </div>
        <el-table
          :data="dataList"
          border
          height="650px"
          v-adaptive="{bottomOffset: 90}"
          v-loading="tableLoading"
          highlight-current-row
          ref="testTable"
          :row-class-name="tableRowClassName"
          @row-click="(row) => startTest(row, row.index)"
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
                {{ row[item.value] ? row[item.value] : '-' }}
              </div>
              <div v-else-if="item.type =='tag'">
                <el-tag :type="handleTagType(row[item.value])">{{ row[item.value] }}</el-tag>
              </div>
              <div v-else-if="item.type =='operate'">
                <el-button
                  type="text"
                  @click.stop
                  @click="startTest(row,$index)"
                >测试
                </el-button>
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
          v-if="!resultStatus && !resultTableLoading"
        >
          <img src="@/assets/images/dataRisk/noData.png" />
          <span>您还未选择测试文件，点击左侧测试按钮试试吧~</span>
        </div>
        <div v-else>
          <el-tabs
            v-model="tabActive"
            @tab-click="handleClick"
          >
            <el-tab-pane
              label="测试结果汇总"
              name="1"
              v-loading="resultTableLoading"
            >
              <div class="hitCount">
                <div class="hitCount-text">
                  流水总条数:&nbsp;
                  <span class="blue">{{ resultNum }}</span>
                  <div class="gray">&nbsp;(未命中分类数量：<span class="red">{{ resultNoHitNum }}</span> 条)
                  </div>
                </div>
              </div>
              <el-table
                :data="resultList"
                border
                height="590px"
                v-adaptive="{bottomOffset: 150}"
              >
                <el-table-column
                  v-for="(item,index) in testResultColumn"
                  :key="index"
                  :label="item.label"
                  :prop="item.value"
                  v-bind="item.config"
                >
                  <template slot-scope="{row,$index}">
                    <div v-if="item.type =='seq'">
                      {{ $index + 1 }}
                    </div>
                    <div v-if="!item.type">
                      {{ row[item.value] ? row[item.value] : '-' }}
                    </div>
                    <div v-else-if="item.type =='tag'">
                      <span v-if="row[item.value] == 0">{{ '-' }}</span>
                      <el-tag
                        v-else
                        type="danger"
                      >{{ row[item.value] == '1' ? '命中' : '' }}
                      </el-tag>
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
            </el-tab-pane>
            <el-tab-pane
              label="明细查看"
              name="2"
              v-loading="resultTableLoading"
            >
              <div class="hitCount">
                <div class="hitCount-text">
                  流水总条数:&nbsp;
                  <span class="blue">{{ resultNum }}</span>
                  <div class="gray">&nbsp;(未命中分类数量：<span class="red">{{ resultNoHitNum }}</span> 条)
                  </div>
                </div>
              </div>
              <div id="filterTable">
                <el-table
                  :data="detailList"
                  border
                  height="590px"
                  v-adaptive="{bottomOffset: 150}"
                >
                  <el-table-column
                    v-for="(item,index) in testDetailColumn"
                    :key="index"
                    :label="item.label"
                    :prop="item.value"
                    v-bind="item.config"
                  >
                    <template v-slot:header>
                      <div class="table-header">
                        <span>{{ item.label }}</span>
                        <img
                          :src="handlerImg(item)"
                          @mousedown="openFilter($event,item)"
                        />
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <FliterPop
                  ref="filterPopRef"
                  :show.sync='showFilter'
                  :position='popPosition'
                  :dataList='filterData'
                  :rowHeight='38'
                  @clickSubmit="filterSubmit"
                  @sortFun="filterSort"
                  @delParams="filterDefault"
                  @remarkData="filterInput"
                />
              </div>
              <div class="pagination">
                <el-pagination
                  background
                  @current-change="handleDetailCurrent"
                  :current-page.sync="detailParams.pageNum"
                  :page-size="detailParams.pageSize"
                  layout="prev, pager, next, jumper"
                  :total="detailTotal"
                ></el-pagination>
              </div>
            </el-tab-pane>
          </el-tabs>
          <div class="resultBtnList">
            <el-button
              @click="handleCheck(0)"
              :disabled='tableLoading || resultTableLoading'
            >验证失败
            </el-button>
            <el-button
              @click="handleCheck(1)"
              :disabled='tableLoading || resultTableLoading'
            >验证通过
            </el-button>
            <!--						<el-button
							@click="handleCheck(2)"
							:disabled='tableLoading || resultTableLoading'
						>验证通过，并切换下一条
						</el-button>-->
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import GTInput from "@/components/GTInput";
import FliterPop from "@/components/filterTable/fliterPop";
import {
  searchFlowRecord,
  submitModelToTestPay,
  updateTestResult, testResultCollect, testListPage, testFilterDetail
} from "@/api/dataRisk/productDecision.js"
import { mapState } from 'vuex'

export default {
  components: { GTInput, FliterPop },
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
      testActive: {},//当前测试数据
      dataList: [],
      resultList: [],//测试结果汇总--表格数据
      detailList: [],//明细查看--表格数据
      searchParams: {
        pageNum: 1,
        pageSize: 10
      },
      tabActive: '1',
      searchHitParams: {
        pageNum: 1,
        pageSize: 20
      },
      total: 0,
      resultNoHitNum: 0,//测试结果-未命中数量
      resultNum: 0,//测试结果-总数量
      resultTotal: 0,//总条数
      detailParams: {
        pageNum: 1,
        pageSize: 20,
      },
      showFilter: false,
      popPosition: null,
      currentRow: {},
      selectRow: [],
      filterData: [],//过滤选项

      detailNoHitTotal: 0,//明细查看-未命中条数
      detailTotal: 0,//明细查看-总条数
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
          label: '个体名称', value: 'companyName', config: {
            align: 'center'
          }
        },
        {
          label: '职业', value: 'profession', config: {
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
      testResultColumn: [
        {
          label: '序号', config: {
            width: '70px',
            align: 'center'
          },
          type: 'seq'
        },
        {
          label: '规则名称', value: 'ruleCode', config: {
            width: '110px',
            align: 'center'
          },
        },
        {
          label: '规则描述', value: 'ruleContent'
        },
        {
          label: '命中数量', value: 'ruleNum', config: {
            width: '100px',
            align: 'center'
          },
        },
      ],
      //明细查看表头
      testDetailColumn: [
        {
          label: '交易时间',
          value: 'tradeDate',
          prop: "tradeDateList",
          type: 1,
          filter: true,
          hasSort: true,
          sort: null,
          config: {
            width: '110px',
            align: 'center',
          },
        },
        {
          label: '交易订单号', value: 'tradeOrder', prop: "tradeOrderList", type: 3, filter: true,
        },
        {
          label: '交易对方', value: 'dealAccountName', prop: "dealAccountNameList", type: 4, filter: true, config: {
            width: '100px',
            align: 'center'
          },
        },
        {
          label: '商品说明', value: 'remark', prop: "remarkList", type: 9, filter: true, config: {
            width: '100px',
            align: 'center'
          },
        },
        {
          label: '收入',
          value: 'borrow',
          type: 6,
          prop: "borrowList",
          filter: true,
          hasSort: true,
          sort: null,
          config: {
            width: '100px',
            align: 'center'
          },
        },
        {
          label: '支出', value: 'lend', type: 7, prop: "lendList", filter: true, hasSort: true, sort: null, config: {
            width: '100px',
            align: 'center'
          },
        },
        {
          label: '分类', value: 'purposeCategory', prop: "purposeCategoryList", type: 8, filter: true, config: {
            width: '100px',
            align: 'center'
          },
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
    //设置测试数据表格每一行的下标
    tableRowClassName({ row, rowIndex }) {
      row.index = rowIndex;
    },
    startTest(data, index) {
      this.testActive = data
      this.resultStatus = true
      this.resultTableLoading = true
      this.atPresentForm.orderNo = data.orderNo
      this.atPresentForm.index = index
      this.$nextTick(() => {
        this.$refs.testTable.setCurrentRow(data)
      })
      this.tabActive = '1';//初始化右侧结果显示tab
      submitModelToTestPay({
        profession: data.profession,
        companyName: data.companyName,
        serialNumber: data.orderNo,
        projectCode: this.projectCode,
        ruleCode: this.ruleCode
      }).then((res) => {
        if (res.code === 200) {
          this.handleClick()
        }
      }).catch((err) => { });
    },
    /**
     * 获取测试结果汇总表格数据
     * @param data
     */
    getTestData(data) {
      this.resultTableLoading = true
      testResultCollect({
        projectCode: this.projectCode,
        ruleCode: this.ruleCode,
        serialNumber: data.orderNo,
        profession: data.profession,
        ...this.searchHitParams,
      }).then((res) => {
        if (res.code === 200) {
          this.resultList = res.data.testResultVOList || []
          this.resultNum = res.data.totalNum || 0
          this.resultNoHitNum = res.data.nonum || 0
          this.resultTotal = res.data.collectNum || 0
          this.resultStatus = !!(res.data.testResultVOList && res.data.testResultVOList.length);
        }
        this.resultTableLoading = false
      }).catch((err) => {
        this.resultTableLoading = false
      });
    },
    /**
     * 获取明细查看表格数据
     */
    getDetailData() {
      this.resultTableLoading = true
      testListPage({
        orderNumber: this.testActive.orderNo,
        ...this.detailParams
      }).then(res => {
        if (res.code === 200) {
          let { list, total } = res.data
          this.detailList = list || [];
          this.detailTotal = total || 0;
        }
        this.resultTableLoading = false
      }).catch((err) => {
        this.resultTableLoading = false
      });
    },
    /**
     * tab选项卡切换
     * @param tab
     */
    handleClick(tab) {
      if (this.tabActive === '1') {
        this.searchHitParams.pageNum = 1
        this.getTestData(this.testActive)
      } else {
        this.detailParams = {
          pageNum: 1,
          pageSize: 20
        }
        this.selectRow = []
        this.getDetailData(this.testActive)
      }
    },
    /**
     * 获取筛选选项数据
     * @param data
     */
    getFilterData(data) {
      testFilterDetail({
        orderNumber: this.atPresentForm.orderNo,
        type: data.type,
        remark: data.remark ? data.remark : null
      }).then(res => {
        if (res.code === 200) {
          this.filterData = res.data || []
        }
      })
    },
    /**
     * 打开筛选选择框
     * @param event
     * @param data
     */
    openFilter(event, data) {
      this.currentRow = JSON.parse(JSON.stringify(data))
      this.popPosition = { event, data }
      this.showFilter = true;
      this.getFilterData(data)
    },
    /**
     * 筛选框图片展示处理
     * @param data
     * @returns {*}
     */
    handlerImg(data) {
      if (data.hasOwnProperty('sort') && data.sort != null) {
        if (data.sort == 0) {
          return require('@/assets/images/components/asc.png')
        } else {
          return require('@/assets/images/components/desc.png')
        }
      } else if (this.selectRow.includes(data.prop)) {
        return require('@/assets/images/components/filter.png')
      } else {
        return require('@/assets/images/filter.png')
      }
    },
    /**
     * 筛选按钮确定按钮点击
     * @param data
     * @param isDefault 是否点击了清空条件
     */
    filterSubmit(data, isDefault = false) {
      let { selectedRow } = data;
      if (!isDefault) {
        this.selectRow = Object.keys(selectedRow)
      }
      this.detailParams.pageNum = 1;
      Object.assign(this.detailParams, selectedRow)
      this.getDetailData(this.testActive);
    },
    /**
     * 筛选弹窗 升序&降序点击
     * @param data
     */
    filterSort(data) {
      let { currentRow } = data;
      this.detailParams.pageNum = 1;
      Object.assign(this.detailParams, { balanceType: currentRow.sort, type: currentRow.type })
      this.getDetailData(this.testActive);
      this.showFilter = false;
    },
    /**
     * 筛选弹窗清空条件
     * @param data
     */
    filterDefault(data) {
      let params = data.selectedRow
      this.selectRow = []
      for (let item in params) {
        if (params[item].length > 0) {
          this.selectRow.push(item)
        }
      }
      this.filterSubmit(data, true)
    },
    /**
     * 筛选弹窗输入框-输入
     * @param data
     */
    filterInput(data) {
      let { remark, currentRow } = data;
      this.getFilterData({ remark, type: currentRow.type })
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
      this.resultList = []
      this.detailList = []
      this.resultStatus = false
      this.tabActive = '1'
      this.resultNum = 0
      this.resultNoHitNum = 0
    },
    handleTestCurrent(val) {
      this.searchParams.pageNum = val
      this.searchDataList()
    },
    handleResultCurrent(val) {
      this.searchHitParams.pageNum = val
      this.getTestData(this.testActive)
    },
    handleDetailCurrent(val) {
      this.detailParams.pageNum = val
      this.getDetailData(this.testActive)
    },
  }
}
</script>

<style lang="less" scoped>
.testDataDrawer {
  /deep/ .el-drawer {
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

          .el-icon-search {
            cursor: pointer;
          }

          /*						.el-input {
                          width: 200px;
                          margin-right: 10px;
                          margin-bottom: 10px;
                        }

                        .el-input__inner {
                          background-color: rgba(#000, 0.04);
                          height: 42px;
                          // color: rgba(#000, 0.3);
                          width: 200px;
                        }*/

          .searchBtn {
            height: 40px;
            width: 90px;
          }

          .step {
            width: 144px;
            height: 40px;
            font-size: 14px;
            font-family: PingFang SC-Regular;
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
            font-family: PingFang SC-Medium, PingFang SC;
            font-weight: 500;
            color: #9e9e9e;
          }
        }

        .hitCount {
          width: 100%;
          height: 45px;
          font-size: 14px;
          font-family: PingFang SC-Regular, PingFang SC;
          font-weight: 400;
          color: rgba(0, 0, 0, 0.85);
          margin-top: 20px;
          padding: 0px 20px;
          background-color: rgba(#2b8df1, 0.08);
          border-top-left-radius: 6px;
          border-top-right-radius: 6px;
          display: flex;
          align-items: center;

          &-text {
            display: flex;

            .blue {
              color: #2b8df1;
            }

            .gray {
              color: rgba(0, 0, 0, 0.6);
            }

            .red {
              color: #fa5151;
            }
          }
        }

        .resultBtnList {
          width: 100%;
          display: flex;
          align-items: center;
          justify-content: flex-end;

          .el-button {
            height: 42px;
            font-size: 14px;
            font-family: PingFang SC-Regular, PingFang SC;
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
            font-family: PingFang SC-Regular, PingFang SC;
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

//	筛选图片样式
#filterTable {
  .table-header {
    display: flex;
    align-items: center;

    > img {
      width: 10px;
      height: 10px;
      cursor: pointer;
      margin-left: 5px;
    }
  }

  .filterPop {
    width: 320px;
    height: 390px;
    background-color: red;
    position: absolute;
    top: 0;
    left: 0;
  }
}
</style>
