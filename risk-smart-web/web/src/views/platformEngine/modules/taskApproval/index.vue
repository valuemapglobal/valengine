<template>
  <div class="afterLoan_detail">
    <div class="operation">
      <div class="search">
        <el-input
          v-model="searchFrom.approvalUserName"
          placeholder="请输入申请用户"
          suffix-icon="el-icon-search"
          style="width: 300; height: 42px"
          clearable
          @clear="searchData"
        ></el-input>
        <el-button type="primary" class="search_btn" @click="searchData">
          搜索
        </el-button>
      </div>
    </div>
    <div class="table">
      <div class="search-warp">
        <div class="search-dropdown">
          <!-- <el-dropdown>
                        <span class="el-dropdown-link">
                            业务场景<i class="el-icon-arrow-down el-icon--right"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item v-for="(item,index) in sceneList" :key="index">{{ item.dictLabel }}</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown> -->
          <el-select
            v-model="searchFrom.businessCode"
            placeholder="请选择业务场景"
            clearable
            @change="searchData"
          >
            <el-option
              v-for="(item, index) in sceneList"
              :key="index"
              :label="item.dictLabel"
              :value="item.dictValue"
            >
            </el-option>
          </el-select>
        </div>
        <div>
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
        </div>
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
            <span v-else-if="item.scope === 'approvalStatus'">
              <!-- {{ row[item.value]}} -->
              <el-tag v-if="row[item.value] == '-1'" type="warning">{{
                '无需审核'
              }}</el-tag>
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
              <el-link :underline="false" type="" @click="openDrawer(row)"
                >审批
              </el-link>
              <el-link
                type="danger"
                :underline="false"
                @click="handleDetail(row)"
                >详情
              </el-link>
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
        :current-page.sync="searchFrom.pageNum"
        :page-size="searchFrom.pageSize"
        layout="total,sizes,prev, pager, next, jumper"
        :total="totalNum"
      >
      </el-pagination>
    </div>
    <approve :info="info" @success="searchData" ref="approve" />
    <detail ref="detail" :info="info" :sceneList="sceneList" />
  </div>
</template>

<script>
import { deleteById } from '@/api/dataRisk/riskStrategy.js'
import myUpload from '@/components/gutu/gutuUpload'
import Search from '@/components/pageSearch.vue'
import { getStatusType, getTask, getType } from '../../api/platformEngine'
import approve from './approve.vue'
import detail from './detail.vue'

export default {
  components: {
    Search,
    myUpload,
    approve,
    detail,
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
          width: '100px',
          align: 'left',
        },
        {
          value: 'applyName',
          label: '申请用户',
          width: '190px',
          align: 'left',
        },
        {
          value: 'approvalUserName',
          label: '审批人员',
          width: '170px',
          align: 'left',
        },
        {
          value: 'businessCode',
          label: '业务场景',
          align: 'left',
          scope: 'businessCode',
        },
        {
          value: 'processStrategy',
          label: '流程策略',
          // width: '130px',
          align: 'left',
        },
        {
          value: 'nodeId',
          label: '审批节点',
          width: '240px',
          align: 'left',
          // scope: 'scope_time'
        },
        {
          value: 'ruleName',
          label: '模型名称',
          width: '240px',
          align: 'left',
          // scope: 'scope_time'
        },
        {
          value: 'approvalStatus',
          label: '审批状态',
          width: '240px',
          align: 'left',
          scope: 'approvalStatus',
        },
        {
          value: 'createTime',
          label: '申请时间',
          width: '240px',
          align: 'left',
          // scope: 'scope_time'
        },

        //
      ],
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
  computed: {},
  watch: {},
  mounted() {
    this.searchData()
    this.getType()
    this.getStatusType()
  },
  methods: {
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

      this.searchData()
    },
    handleDetail(row) {
      this.info = row
      this.$refs.detail.getDetail(row.processTask)
      this.$refs.detail.visible = true
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
      getTask({ ...this.searchFrom })
        .then((res) => {
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
      this.searchData()
    },
    handleSizeChange(val) {
      this.searchFrom.pageSize = val
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
      this.reset()
      this.drawer.visible = false
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
      this.selectForm = {}
    },
  },
}
</script>

<style lang="less" scoped>
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

      ::v-deep .el-input__inner {
        width: 300px;
        height: 42px;
        background: #ffffff;
        border-radius: 4px 4px 4px 4px;
        border: none;
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
      }

      .el-table__fixed-header-wrapper {
        padding: 0px;
      }

      .el-table__fixed-body-wrapper {
        padding: 0px;
      }

      .cell {
        font-size: 14px;
        font-family: PingFang SC;
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
</style>
