<template>
  <div class="afterLoan_detail">
    <div class="operation">
      <div class="search">
        <el-input
          v-model="searchFrom.operName"
          :placeholder="$t('decisionManage.inputOperator')"
          suffix-icon="el-icon-search"
          style="width: 300; height: 42px"
          clearable
          @clear="getlogData"
        ></el-input>
        <el-button type="primary" class="search_btn" @click="getlogData">
          {{ $t('common.search') }}
        </el-button>
      </div>
    </div>
    <div class="content">
      <div class="search-warp">
        <div class="search-dropdown">
          <el-select
            :popper-append-to-body="false"
            v-model="searchFrom.deptId"
            :placeholder="$t('decisionManage.selectUserDept')"
            clearable
            @change="getlogData"
          >
            <el-option
              v-for="(item, index) in deptList"
              :key="index"
              :label="item.deptName"
              :value="item.deptId"
            >
            </el-option>
          </el-select>

          <el-select
            :popper-append-to-body="false"
            v-model="searchFrom.businessType"
            :placeholder="$t('decisionManage.selectOperationType')"
            clearable
            @change="getlogData"
          >
            <el-option :value="1" :label="$t('common.add')"></el-option>
            <el-option :value="2" :label="$t('common.modify')"></el-option>
            <el-option :value="3" :label="$t('common.delete')"></el-option>
            <!-- <el-option :value="12" :label="$t('decisionManage.enableDisable')"></el-option> -->
          </el-select>
          <el-select
            :popper-append-to-body="false"
            v-model="searchFrom.businessCode"
            :placeholder="$t('decisionManage.selectBusinessScene')"
            clearable
            @change="getlogData"
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
            :popper-append-to-body="false"
            v-model="searchFrom.ruleCode"
            :placeholder="$t('decisionManage.selectStrategyModel')"
            clearable
            @change="getlogData"
          >
            <el-option :value="1" :label="$t('decisionManage.scoreModel')">
            </el-option>
            <el-option :value="2" :label="$t('decisionManage.ratingModel')">
            </el-option>
            <el-option :value="3" :label="$t('decisionManage.limitModel')">
            </el-option>
            <el-option :value="4" :label="$t('decisionManage.priceModel')">
            </el-option>
            <el-option :value="5" :label="$t('decisionManage.ruleModel')">
            </el-option>
            <el-option :value="6" :label="$t('decisionManage.classifyModel')">
            </el-option>
          </el-select>
          <el-date-picker
            v-model="selectForm.time"
            type="datetimerange"
            @change="changeTime"
            range-separator="-"
            :start-placeholder="$t('decisionManage.runTimeStart')"
            :end-placeholder="$t('decisionManage.runTimeEnd')"
            value-format="yyyy-MM-dd HH:mm:ss"
          >
          </el-date-picker>
        </div>

        <!-- <span style="margin-left:10px;color: rgba(0,0,0,0.4);" @click="reset">{{ $t('common.reset') }}</span> -->
        <el-button
          type="primary"
          style="margin-left: 10px"
          class="search_btn"
          @click="reset"
        >
          {{ $t('common.reset') }}
        </el-button>
      </div>
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        :border="true"
        height="620px"
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
            <span v-if="item.scope === 'ruleCode'">
              <div v-if="row[item.value] == 1">
                {{ $t('decisionManage.scoreModel') }}
              </div>
              <div v-if="row[item.value] == 2">
                {{ $t('decisionManage.ratingModel') }}
              </div>
              <div v-if="row[item.value] == 3">
                {{ $t('decisionManage.limitModel') }}
              </div>
              <div v-if="row[item.value] == 4">
                {{ $t('decisionManage.priceModel') }}
              </div>
              <div v-if="row[item.value] == 5">
                {{ $t('decisionManage.ruleModel') }}
              </div>
              <div v-if="row[item.value] == 6">
                {{ $t('decisionManage.classifyModel') }}
              </div>
            </span>
            <span v-else-if="item.scope === 'businessType'">
              <!-- <span v-if="row[item.value] == '0'">{{
                  $t('common.other')
                }}</span> -->
              <span v-if="row[item.value] == '1'">{{ $t('common.add') }}</span>
              <span v-if="row[item.value] == '2'">{{
                $t('common.modify')
              }}</span>
              <span v-if="row[item.value] == '3'">{{
                $t('common.delete')
              }}</span>
              <span v-if="row[item.value] == '12'">{{
                $t('decisionManage.enableDisable')
              }}</span>
            </span>

            <span v-else-if="!item.scope">
              {{ row[item.value] }}
            </span>
          </template>
        </el-table-column>

        <el-table-column
          :label="$t('common.operation')"
          width="160px"
          fixed="right"
        >
          <template slot-scope="{ row }">
            <div class="handler">
              <!-- <el-link :underline="false" type="" @click="openDrawer(row)">{{ $t('common.approve') }}
                              </el-link> -->
              <el-link
                type="danger"
                style="color: #3662ec"
                :underline="false"
                @click="handleDetail(row)"
                >{{ $t('operlogManage.detail') }}
              </el-link>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          :current-page.sync="searchFrom.pageNum"
          :page-size="searchFrom.pageSize"
          layout="total,sizes,prev, pager, next, jumper"
          :total="totalNum"
        >
        </el-pagination>
      </div>
    </div>

    <approve :info="info" ref="approve" />
    <detail ref="detail" :info="info" :sceneList="sceneList" />
  </div>
</template>

<script>
import { deleteById } from '@/api/dataRisk/riskStrategy.js'
import { getStatusType, getType, getlog, getuserdp } from './api'
import approve from './approve'
import detail from './detail'

export default {
  components: {
    //   Search,
    //   myUpload,
    approve,
    detail,
  },
  data() {
    return {
      deptList: [],
      statusData: [], // 所有的状态
      info: {},
      sceneList: [], // 场景列表
      activeBtn: 0,
      tableData: [],
      tableLoading: false,
      searchFrom: {
        pageNum: 1,
        pageSize: 10,
      },
      selectForm: {},
      totalNum: 0,
      drawer: {
        title: '',
        visible: false,
        type: 'addCode',
        width: '50%',
      },
    }
  },
  computed: {
    tableList() {
      return [
        {
          value: 'operId',
          label: this.$t('decisionManage.operationNo'),
          width: '100px',
          align: 'left',
        },
        {
          value: 'operName',
          label: this.$t('decisionManage.operator'),
          width: '190px',
          align: 'left',
        },
        {
          value: 'deptName',
          label: this.$t('decisionManage.userDept'),
          width: '170px',
          align: 'left',
        },
        {
          value: 'businessType',
          label: this.$t('decisionManage.operationType'),
          // width: '130px',
          //   align: "left",
          scope: 'businessType',
        },
        // {
        //   value: "productName",
        //   label: this.$t('decisionManage.financialProduct'),
        //   // width: '130px',
        //   align: "left",
        // },
        {
          value: 'businessCode',
          label: this.$t('decisionManage.businessScene'),
          align: 'left',
          scope: 'businessCode',
        },

        {
          value: 'ruleCode',
          label: this.$t('decisionManage.strategyModel'),
          width: '240px',
          align: 'left',
          scope: 'ruleCode',
        },
        // {
        //   value: "ruleName",
        //   label: this.$t('decisionManage.processStrategy'),
        //   width: "240px",
        //   align: "left",
        //   scope: 'ruleName'
        // },
        {
          value: 'operTime',
          label: this.$t('decisionManage.operationTime'),
          width: '240px',
          align: 'left',
          // scope: 'scope_time'
        },

        //
      ]
    },
  },
  watch: {},
  mounted() {
    this.getType()
    this.getStatusType()
    this.getlogData()
    this.getReqGetuserdp()
  },
  methods: {
    getReqGetuserdp() {
      getuserdp().then((res) => {
        this.deptList = res.data
      })
    },
    // mapStatus(code) {
    //     if (code && this.statusData.length >= 1) {
    //         console.log('code', code, this.statusData)
    //         return this.statusData.filter((item) => item.dictValue == code)[0].dictLabel
    //     } else {
    //         return '/'
    //     }
    // },
    // 获取审批状态
    getStatusType() {
      getStatusType('approval_status').then((res) => {
        this.statusData = res.data
      })
    },

    // 时间筛选
    changeTime(s) {
      if (s) {
        this.searchFrom.startTime = s[0]
        this.searchFrom.endTime = s[1]
      } else {
        this.searchFrom.startTime = ''
        this.searchFrom.endTime = ''
      }

      this.getlogData()
    },
    handleDetail(row) {
      this.info = { ...row }
      this.$refs.detail.getDetail({ ...row })
      // this.$refs.detail.visible = true;
    },
    mapSceneList(code) {
      if (code && this.sceneList.length > 0) {
        let arr = this.sceneList.filter((item) => item.dictValue == code)

        if (arr.length > 0) {
          return arr[0].dictLabel
        } else {
          return '/'
        }
      } else {
        return '/'
      }
    },
    // 获取业务场景
    getType() {
      getType('platform_business_scenario').then((res) => {
        if (res.code == 200) {
          this.sceneList = res.data
        }
      })
    },
    getlogData() {
      this.tableLoading = true
      getlog({ ...this.searchFrom })
        .then((res) => {
          // const promises = res.data.list.map((item) =>
          //   selectOneUserByUser({ userName: item.operName }).then((res) => {
          //     if (res.code === 200) {
          //       item.operName = res.data.nickName;
          //     }
          //   })
          // );

          // Promise.all(promises)
          //   .then(() => {
          //     this.tableData = res.data.list;
          //     this.totalNum = res.data.total;
          //     this.tableLoading = false;
          //   })
          //   .catch((error) => {
          //     // 处理错误
          //   });
          this.tableData = res.data.list
          this.totalNum = res.data.total
          this.tableLoading = false
        })
        .catch((err) => {
          this.tableLoading = false
        })
    },

    handleCurrentChange(val) {
      this.searchFrom.pageNum = val
      this.getlogData()
    },
    handleSizeChange(val) {
      this.searchFrom.pageSize = val
      this.getlogData()
    },
    handleTag(status, item, data) {
      let list = item.list
      let returnData = list.find((itemX) => itemX.value === data)
      if (returnData) {
        return returnData[status]
      }
    },
    handleClose() {
      this.reset()
      this.drawer.visible = false
    },
    openDrawer(row) {
      this.info = { ...row }
      this.$refs.approve.visible = true
    },

    handleDelete(row) {
      this.$confirm(
        this.$t('decisionManage.deleteConfirm'),
        this.$t('common.warning'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          deleteById(row.id)
            .then((res) => {
              this.$message.success(this.$t('common.success'))
              this.getlogData()
            })
            .catch((err) => {})
        })
        .catch(function () {})
    },
    reset() {
      this.selectForm = {}
      this.searchFrom = {}
      this.getlogData()
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep .el-dropdown-link {
  color: rgba(#000, 0.85);
}

.search-warp {
  background: var(--bg-color);
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  margin-bottom: 10px;

  ::v-deep .search-dropdown {
    display: flex;
    align-items: center;
    .el-select {
      width: 240px;
      margin-right: 10px;
    }

    .el-input {
      width: 240px;
      .el-input__inner {
        height: 42px;
        background: var(--bg-color);
        border-radius: 6px;
        &::placeholder {
          color: var(--text-color-secondary);
        }
      }
    }

    .el-date-editor {
      width: 300px;
      height: 42px;
    }
  }

  .search_btn {
    height: 42px;
    font-size: 14px;
    margin-left: 10px;
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
  height: calc(100vh - 42px);

  .operation {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .search {
      display: flex;

      ::v-deep .el-input__inner {
        width: 240px;
        height: 42px;
        background: var(--bg-color);
        border-radius: 6px;
        &::placeholder {
          color: var(--text-color-secondary);
        }
      }

      .search_btn {
        height: 42px;
        font-size: 14px;
        margin-left: 10px;

        .el-icon-plus {
          margin-right: 0;
        }
      }
    }

    .switchBtn {
      padding: 5px;
      background-color: var(--bg-color);
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

  .content {
    margin-top: 20px;
    margin-bottom: 0;
    padding: 20px;
    background: var(--bg-color);
    border-radius: 6px;

    .pagination {
      display: flex;
      justify-content: center;
      background-color: var(--bg-color);
      padding: 20px 20px 0 20px;
    }
  }

  .addCode {
    padding: 0px 30px;
    box-sizing: border-box;

    ::v-deep .el-form {
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
</style>
