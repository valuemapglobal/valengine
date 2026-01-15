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
        style="width: 100%"
        :header-cell-style="{
          background: 'rgba(230,238,252,0.3)',
        }"
        @selection-change="handleSelect"
        height="675px"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column
          :show-overflow-tooltip="true"
          v-for="col in tableHeader"
          :key="col.id"
          :label="col.label"
          :width="col.width"
          :align="col.align"
        >
          <template slot-scope="{ row }">
            <div v-if="col.type === 'operation'">
              <span class="el-dropdown-link" @click="openChaneg(row)">
                {{ $t('operlogManage.detail') }}
              </span>
            </div>
            <div v-else-if="col.type === 'select'">
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
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      direction="rtl"
      :before-close="handleClose"
      size="60%"
    >
      <div class="uploadForm">
        <el-form
          :model="uploadForm"
          :rules="rules"
          ref="uploadForm"
          :label-width="isEnglish() ? '160px' : '100px'"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item :label="$t('operlogManage.operationModule') + '：'"
                >{{ uploadForm.title }} /
                {{
                  uploadForm.businessType
                    ? handleDoMain('sys_oper_type', uploadForm.businessType)
                        .dictLabel
                    : ''
                }}</el-form-item
              >
              <el-form-item :label="$t('operlogManage.loginInfo') + '：'"
                >{{ uploadForm.operName }} /
                {{ uploadForm.operIp }}</el-form-item
              >
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('operlogManage.requestUrl') + '：'">{{
                uploadForm.operUrl
              }}</el-form-item>
              <el-form-item :label="$t('operlogManage.requestMethod') + '：'">{{
                uploadForm.requestMethod
              }}</el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                :label="$t('operlogManage.operationMethod') + '：'"
                >{{ uploadForm.method }}</el-form-item
              >
            </el-col>
            <el-col :span="24">
              <el-form-item :label="$t('operlogManage.requestParams') + '：'">{{
                uploadForm.operParam
              }}</el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="$t('operlogManage.returnParams') + '：'">{{
                uploadForm.jsonResult
              }}</el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('operlogManage.operationStatus') + '：'">
                <div v-if="uploadForm.status === 0">
                  {{ $t('operlogManage.normal') }}
                </div>
                <div v-else-if="uploadForm.status === 1">
                  {{ $t('operlogManage.failed') }}
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('operlogManage.operationTime') + '：'">{{
                uploadForm.operTime
              }}</el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                :label="$t('operlogManage.exceptionInfo') + '：'"
                v-if="uploadForm.status === 1"
                >{{ uploadForm.errorMsg }}</el-form-item
              >
            </el-col>
          </el-row>
        </el-form>
        <div class="bottomBtn">
          <el-button @click="handleClose">{{ $t('common.close') }}</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import {
  selectAll,
  delOperlog,
  cleanOperlog,
  exportData,
} from '@/api/system/operlog.js'
import { getDomain } from '@/api/financing/index.js'
export default {
  components: {
    Search,
  },
  computed: {
    list() {
      return [
        {
          type: 'input',
          placeholder: this.$t('operlogManage.inputSystemModule'),
          prop: {
            key: 'title',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: this.$t('operlogManage.inputOperator'),
          prop: {
            key: 'operName',
            value: null,
          },
        },
        {
          type: 'select',
          placeholder: this.$t('operlogManage.selectType'),
          options: this.listTypeOptions?.sys_oper_type || [],
          prop: {
            key: 'businessType',
            value: null,
          },
        },
        {
          type: 'select',
          placeholder: this.$t('operlogManage.selectStatus'),
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
          prop: 'operId',
          id: 1,
          label: this.$t('operlogManage.logNo'),
          width: '100px',
          align: 'left',
        },
        {
          prop: 'title',
          id: 2,
          label: this.$t('operlogManage.systemModule'),
          align: 'left',
        },
        {
          type: 'select',
          prop: 'businessType',
          list: 'sys_oper_type',
          id: 3,
          label: this.$t('operlogManage.operationType'),
          align: 'center',
          width: this.isEnglish() ? '120px' : '80px',
        },
        {
          prop: 'requestMethod',
          id: 4,
          label: this.$t('operlogManage.requestMethod'),
          align: 'center',
          width: this.isEnglish() ? '120px' : '100px',
        },
        {
          prop: 'operName',
          id: 5,
          label: this.$t('operlogManage.operator'),
          align: 'left',
        },
        {
          prop: 'operIp',
          id: 6,
          label: this.$t('operlogManage.host'),
          align: 'left',
        },
        {
          type: 'select',
          prop: 'status',
          id: 7,
          label: this.$t('operlogManage.operationStatus'),
          width: '80px',
          align: 'center',
          list: 'sys_common_status',
        },
        {
          prop: 'operTime',
          id: 8,
          label: this.$t('operlogManage.operationDate'),
          width: '180px',
          align: 'left',
        },
        {
          type: 'operation',
          prop: 'operation',
          id: 9,
          label: this.$t('common.operation'),
          width: '180px',
          align: 'left',
        },
      ]
    },
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
        },
        total: 0,
        sizes: [10, 20, 30, 40, 50, 100],
      },
      params: {},

      uploadForm: {},
      rules: {},
      ids: [],
    }
  },
  mounted() {
    this.init()
    this.searchData()
  },
  watch: {},
  methods: {
    init() {
      getDomain('sys_oper_type').then((res) => {
        if (res.code == 200) {
          res.data.forEach((item) => {
            item.label = item.dictLabel
            item.value = item.dictValue
          })
          this.$set(this.listTypeOptions, 'sys_oper_type', res.data)
          this.$set(this.listTypeInfo, 'sys_oper_type', res.data)
        }
      })
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
    operation() {
      if (this.drawer.type === 'add') {
        addDictionary({ ...this.uploadForm })
          .then((res) => {
            if (res.code == 200) {
              this.$message.success(this.$t('common.success'))
              this.handleClose()
              this.searchData()
            }
          })
          .catch((err) => {})
      } else if (this.drawer.type === 'change') {
        updateType({ ...this.uploadForm })
          .then((res) => {
            if (res.code == 200) {
              this.$message.success(this.$t('common.success'))
              this.handleClose()
              this.searchData()
            }
          })
          .catch((err) => {})
      }
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
      console.log(data, 'data???')
      const dictIds = data.operId || this.ids
      this.$confirm(
        this.$t('operlogManage.deleteConfirm', { id: dictIds }),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then((res) => {
          delOperlog(dictIds)
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
        .confirm(this.$t('operlogManage.clearConfirm'))
        .then(function () {
          return cleanOperlog()
        })
        .then(() => {
          this.$modal.msgSuccess(this.$t('operlogManage.clearSuccess'))
          this.searchData()
        })
        .catch(() => {})
    },

    openChaneg(data) {
      this.uploadForm = {}
      this.uploadForm = data
      this.drawer.title = this.$t('operlogManage.operlogDetail')
      this.drawer.type = 'change'
      this.drawer.visible = true
    },
    handleSelect(val) {
      this.selectData = val
      this.ids = val.map((item) => item.operId)
      this.isDel = !val.length
      this.isUpload = val.length != 1
    },
    handleClose() {
      this.$refs.uploadForm.resetFields()
      this.drawer.visible = false
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
    height: calc(100% - 60px);
    margin-top: 20px;
    padding: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    background: var(--bg-color);
    border-radius: 6px;
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
  box-sizing: border-box;

  ::v-deep .el-form {
    .el-form-item__label {
      font-size: 14px;
      color: var(--text-color-secondary);
    }
    .el-form-item__content {
      word-break: break-all;
      color: var(--text-color-tertiary);
      border: 1px solid var(--border-color);
      padding: 0px 10px;
      border-radius: 4px;
    }
  }

  .bottomBtn {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;

    .el-button {
      padding: 0px 24px;
      height: 42px;
      font-size: 14px;
      border-radius: 8px;
    }
  }
}
</style>
