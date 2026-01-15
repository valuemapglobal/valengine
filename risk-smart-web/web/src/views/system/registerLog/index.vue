<template>
  <div class="userManage">
    <div class="statusBar">
      <Search :list="list" @search="search" />
      <div class="btnss">
        <el-button
          type="danger"
          plain
          :disabled="isDel"
          icon="el-icon-delete"
          @click="handleDelete"
          >{{ $t('common.delete') }}</el-button
        >
        <el-button
          type="danger"
          plain
          :disabled="isDel"
          icon="el-icon-delete"
          @click="handleClean"
          >{{ $t('operlogManage.clear') }}</el-button
        >
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          @click="handleExport"
          >{{ $t('common.export') }}</el-button
        >
      </div>
    </div>
    <div class="content">
      <el-table
        :data="tableData"
        border
        height="100px"
        style="width: 100%"
        :header-cell-style="{
          background: 'rgba(230,238,252,0.3)',
        }"
        @selection-change="handleSelect"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column
          :show-overflow-tooltip="true"
          v-for="col in tableHeader"
          :key="col.id"
          :label="col.label"
          :width="col.width"
          :align="col.align"
          :sortable="col.type === 'sort' ? 'custom' : false"
          :sort-orders="col.type === 'sort' ? ['descending', 'ascending'] : []"
          :prop="col.prop"
        >
          <template slot-scope="{ row }">
            <div v-if="col.type === 'select'">
              <el-tag :type="handleDoMain(col.list, row[col.prop]).listClass">{{
                handleDoMain(col.list, row[col.prop]).dictLabel
              }}</el-tag>
            </div>
            <div v-else>{{ row[col.prop] }}</div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          :current-page.sync="queryParams.params.pageNum"
          :page-sizes="queryParams.pageSizes"
          :page-size="queryParams.params.sizes"
          layout="total,sizes,prev,pager,next,jumper"
          :total="queryParams.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
// import {
//   selectAll,
//   addDictionary,
//   updateType,
//   deleteType,
// } from '@/api/financing/dictionary'
import {
  selectAll,
  delLogininfor,
  cleanLogininfor,
  exportData,
} from '@/api/system/registerLog.js'
import { getDomain } from '@/api/financing/index.js'
export default {
  components: {
    Search,
  },
  data() {
    return {
      active: this,
      listTypeInfo: {},
      listTypeOptions: {},
      drawer: {
        title: '',
        visible: false,
        type: '',
      },

      tableData: [],
      selectData: [],

      isDel: true,
      isUpload: true,

      queryParams: {
        params: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: null,
          isAsc: null,
        },
        total: 0,
        sizes: [10, 20, 30, 40, 50, 100],
      },
      params: {},
      defaultSort: { prop: 'loginTime', order: 'descending' },

      uploadForm: {},
      rules: {},
      ids: [],
    }
  },
  computed: {
    list() {
      return [
        {
          type: 'input',
          placeholder: this.$t('registerLog.inputLoginAddress'),
          prop: {
            key: 'ipaddr',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: this.$t('registerLog.inputUserName'),
          prop: {
            key: 'userName',
            value: null,
          },
        },
        {
          type: 'select',
          placeholder: this.$t('registerLog.selectLoginStatus'),
          options: this.listTypeOptions?.sys_common_status || [],
          prop: {
            key: 'status',
            value: null,
          },
        },
        {
          type: 'time',
          placeholder: this.$t('operlogManage.selectTime'),
          prop: {
            key: 'time',
            value: null,
          },
        },
      ]
    },
    tableHeader() {
      return [
        {
          prop: 'infoId',
          id: 1,
          label: this.$t('registerLog.accessNo'),
          width: '100px',
          align: 'left',
        },
        {
          prop: 'userName',
          id: 2,
          label: this.$t('registerLog.userName'),
          align: 'left',
          type: 'sort',
        },
        {
          prop: 'ipaddr',
          id: 3,
          label: this.$t('registerLog.address'),
          align: 'left',
        },
        {
          type: 'select',
          prop: 'status',
          id: 4,
          label: this.$t('registerLog.loginStatus'),
          width: '80px',
          align: 'center',
          list: 'sys_common_status',
        },
        {
          prop: 'msg',
          id: 5,
          label: this.$t('registerLog.description'),
          align: 'center',
          width: '100px',
        },
        {
          prop: 'accessTime',
          id: 6,
          label: this.$t('registerLog.accessTime'),
          align: 'left',
          type: 'sort',
        },
      ]
    },
  },
  mounted() {
    this.init()
    this.searchData()
  },
  watch: {},
  methods: {
    init() {
      getDomain('sys_common_status').then((res) => {
        if (res.code == 200) {
          res.data.forEach((item) => {
            item.label = item.dictLabel
            item.value = item.dictValue
          })
          this.$set(this.listTypeOptions, 'sys_common_status', res.data)
          this.$set(this.listTypeInfo, 'sys_common_status', res.data)
        }
      })
    },
    search(data) {
      this.params = data
      this.searchData()
    },
    searchData() {
      let data = this.params
      if (data && data.time) {
        data['params[beginTime]'] = data.time[0]
        data['params[endTime]'] = data.time[1]
      } else {
        data['params[beginTime]'] = null
        data['params[endTime]'] = null
      }
      selectAll({ ...this.queryParams.params, ...data })
        .then((res) => {
          this.tableData = res.rows
          this.queryParams.total = res.total
        })
        .catch((eerr) => {})
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.params.orderByColumn = column.prop
      this.queryParams.params.isAsc = column.order
      this.searchData()
    },
    handleDoMain(list, data) {
      data = data + ''
      let myData = null
      if (this.listTypeInfo[list]) {
        myData = this.listTypeInfo[list].find((item) => item.dictValue === data)
      }
      if (myData) {
        return myData
      }
    },
    handleDelete(data) {
      const dictIds = data.infoId || this.ids
      this.$confirm(
        this.$t('registerLog.deleteConfirm', { id: dictIds }),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then((res) => {
          delLogininfor(dictIds)
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(this.$t('common.success'))
                this.searchData()
              }
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    handleClean() {
      this.$modal
        .confirm(this.$t('registerLog.clearConfirm'))
        .then(function () {
          return cleanLogininfor()
        })
        .then(() => {
          this.$modal.msgSuccess(this.$t('operlogManage.clearSuccess'))
          this.searchData()
        })
        .catch(() => {})
    },

    handleSelect(val) {
      this.selectData = val
      this.ids = val.map((item) => item.infoId)
      this.isDel = !val.length
      this.isUpload = val.length != 1
    },
    handleSizeChange(val) {
      this.queryParams.params.pageSize = val
      this.searchData()
    },
    handleCurrentChange(val) {
      this.queryParams.params.pageNum = val
      this.searchData()
    },
    handleExport() {
      this.download(
        exportData(),
        {
          ...this.queryParams,
        },
        `operlog_${new Date().getTime()}.xlsx`
      )
    },
  },
}
</script>

<style lang="less" scoped>
.userManage {
  padding: 20px;
  height: calc(100vh - 42px);
  .statusBar {
    display: flex;
    height: 42px;
    > .btnss {
      margin-left: 10px;
      > button {
        border: none;
        height: 42px;
        padding: 0 20px;
      }
      > button:nth-of-type(1) {
        color: #00b578 !important;
        background-color: rgba(0, 181, 120, 0.1);
        // border: 1px rgba(0, 181, 120, 0.2) solid;
        // border: hidden;
      }
      > button:nth-of-type(2) {
        color: #fa5151 !important;
        background-color: rgba(250, 81, 81, 0.1);
      }
      > button:nth-of-type(3) {
        color: #ff8f1f !important;
        background-color: rgba(255, 143, 31, 0.1);
      }
      > button:nth-of-type(4) {
        color: #3662ec !important;
        background-color: rgba(54, 98, 236, 0.1);
      }
    }
  }
  .content {
    margin-top: 20px;
    padding: 20px;
    height: calc(100% - 60px);
    background: var(--bg-color);
    border-radius: 6px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    .column_status {
      > span {
        padding: 6px 10px;
        border-radius: 2px;
        font-size: 14px;
      }
      .normal {
        background-color: rgba(#3662ec, 0.1);
        color: #3662ec;
      }
      .deactivate {
        background-color: #ffeded;
        color: #ff4949;
      }
    }
    .column_link {
      color: #3662ec;
      cursor: pointer;
    }
    .column_link:hover {
      color: #445da7;
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}
.el-dropdown-link {
  font-size: 14px;
  font-weight: normal;
  color: var(--primary-color);
  line-height: 22px;
  margin-right: 20px;
  cursor: pointer;
}
.uploadForm {
  padding: 20px;
  box-sizing: border-box;

  /deep/.el-form {
    .el-form-item__content {
      word-break: break-all;
    }
  }

  .bottomBtn {
    display: flex;
    justify-content: flex-end;
  }
}
// :deep.el-pagination {
//   display: flex;
//   justify-content: center;
//   .el-pager {
//     .active {
//       background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
//     }
//   }
// }
</style>
