<template>
  <div class="userManage">
    <div class="statusBar">
      <div class="searchTop">
        <el-input
          v-model="queryParams.dictName"
          :placeholder="$t('dictionaryManagement.dictName')"
          clearable
        ></el-input>
        <el-input
          v-model="queryParams.dictType"
          :placeholder="$t('dictionaryManagement.dictType')"
          clearable
        ></el-input>
        <el-button type="primary" @click="reset" size="default">{{
          $t('common.reset')
        }}</el-button>
      </div>
      <div class="btnss">
        <el-button
          type="primary"
          @click="openAdd"
          size="default"
          icon="el-icon-plus"
          >{{ $t('common.add') }}</el-button
        >
        <el-button
          type="danger"
          plain
          :disabled="isDel"
          size="default"
          icon="el-icon-delete"
          @click="handleDelete"
          >{{ $t('common.delete') }}</el-button
        >
      </div>
    </div>
    <div class="content">
      <div class="filter">
        <GutuSelect
          :options="statusOptions"
          :data.sync="queryParams.status"
          flatten
          :config="{ placeholder: $t('dictionaryManagement.dictStatus') }"
          :adaptiveWidth="{ enable: true, minWidth: '90px' }"
        />
        <div class="filterTime">
          <el-date-picker
            v-model="createTime"
            :append-to-body="false"
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="-"
            :start-placeholder="$t('dictionaryManagement.createTimeStart')"
            :end-placeholder="$t('dictionaryManagement.createTimeEnd')"
          >
          </el-date-picker>
        </div>
      </div>
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        height="calc(var(--bgvh) - 280px)"
        @selection-change="handleSelect"
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
            <div v-if="col.prop === 'dictType'">
              <router-link
                :to="
                  '/System/DictionaryDetail?id=' +
                  row.dictId +
                  '&type=' +
                  row.dictType
                "
                class="link-type"
              >
                <span class="column_link">{{ row.dictType }}</span>
              </router-link>
            </div>
            <div class="column_status" v-else-if="col.prop === 'status'">
              <span v-if="row.status === '0'" class="normal">{{
                $t('common.normal')
              }}</span>
              <span v-else-if="row.status === '1'" class="deactivate">{{
                $t('common.disabled')
              }}</span>
            </div>
            <div v-else-if="col.prop === 'operation'">
              <span class="el-dropdown-link" @click="openChaneg(row)">
                {{ $t('common.modify') }}
              </span>
              <span
                style="color: red"
                class="el-dropdown-link"
                @click="handleDelete(row)"
              >
                {{ $t('common.delete') }}
              </span>
            </div>
            <div v-else>{{ row[col.prop] }}</div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          :current-page.sync="params.pageNum"
          :page-size="params.sizes"
          layout="total,sizes,prev,pager,next,jumper"
          :total="total"
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
          :label-width="isEnglish() ? '140px' : '100px'"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.dictName')"
                prop="dictName"
              >
                <el-input
                  size="medium"
                  v-model="uploadForm.dictName"
                  :placeholder="$t('menuManagement.inputPlaceholder')"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.dictType')"
                prop="dictType"
              >
                <el-input
                  size="medium"
                  v-model="uploadForm.dictType"
                  :placeholder="$t('menuManagement.inputPlaceholder')"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="$t('common.status')" prop="status">
                <el-radio v-model="uploadForm.status" label="0">{{
                  $t('common.normal')
                }}</el-radio>
                <el-radio v-model="uploadForm.status" label="1">{{
                  $t('common.disabled')
                }}</el-radio>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="$t('common.remark')" prop="remark">
                <el-input
                  v-model="uploadForm.remark"
                  :placeholder="$t('menuManagement.inputPlaceholder')"
                  type="textarea"
                  :rows="3"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="bottomBtn">
          <el-button class="btn" type="primary" @click="operation">{{
            $t('common.sure')
          }}</el-button>
          <el-button @click="handleClose">{{ $t('common.cancel') }}</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import GutuSelect from '@/components/gutu/gutuSelect.vue'
import {
  selectAll,
  addDictionary,
  updateType,
  deleteType,
} from '@/api/system/dictionary'
export default {
  components: {
    Search,
    GutuSelect,
  },
  computed: {
    statusOptions() {
      return [
        { label: this.$t('common.normal'), value: '0' },
        { label: this.$t('common.disabled'), value: '1' },
      ]
    },
    tableHeader() {
      return [
        {
          prop: 'dictId',
          id: 1,
          label: this.$t('dictionaryManagement.dictId'),
          width: '60px',
          align: 'left',
        },
        {
          prop: 'dictName',
          id: 2,
          label: this.$t('dictionaryManagement.dictName'),
          align: 'left',
        },
        {
          prop: 'dictType',
          id: 3,
          label: this.$t('dictionaryManagement.dictType'),
          align: 'left',
        },
        {
          prop: 'status',
          id: 4,
          label: this.$t('common.status'),
          align: 'center',
          width: '116px',
        },
        {
          prop: 'remark',
          id: 5,
          label: this.$t('common.remark'),
          align: 'left',
        },
        {
          prop: 'createTime',
          id: 6,
          label: this.$t('common.createTime'),
          align: 'left',
        },
        {
          prop: 'operation',
          id: 7,
          label: this.$t('common.operation'),
          width: '180px',
          align: 'left',
        },
      ]
    },
    rules() {
      return {
        dictName: [
          {
            required: true,
            message: this.$t('dictionaryManagement.inputDictName'),
            trigger: 'blur',
          },
        ],
        dictType: [
          {
            required: true,
            message: this.$t('dictionaryManagement.inputDictType'),
            trigger: 'blur',
          },
        ],
      }
    },
  },
  data() {
    return {
      drawer: {
        title: '',
        visible: false,
        type: '',
      },

      tableData: [],
      selectData: [],

      isDel: true,
      isUpload: true,

      params: {
        pageNum: 1,
        pageSize: 10,
      },
      queryParams: {
        dictName: null,
        dictType: null,
        status: null,
        'params[beginTime]': null,
        'params[endTime]': null,
      },
      createTime: [],
      total: 0,

      uploadForm: {
        dictName: null,
        dictType: null,
        status: '0',
        remark: null,
        dictId: null,
      },
      ids: [],
    }
  },
  mounted() {
    this.searchData()
  },
  watch: {
    queryParams: {
      handler() {
        this.params = this.$options.data().params
        this.searchData()
      },
      deep: true,
    },
    createTime: {
      handler(val) {
        if (!val) val = []
        this.queryParams['params[beginTime]'] = val.length ? val[0] : null
        this.queryParams['params[endTime]'] = val.length ? val[1] : null
      },
    },
  },
  methods: {
    searchData() {
      // if (data && data.time) {
      //   data['params[beginTime]'] = data.time[0]
      //   data['params[endTime]'] = data.time[1]
      // } else {
      //   data['params[beginTime]'] = null
      //   data['params[endTime]'] = null
      // }
      selectAll({ ...this.params, ...this.queryParams })
        .then((res) => {
          this.tableData = res.rows
          this.total = res.total
        })
        .catch((eerr) => {})
    },
    operation() {
      this.$refs.uploadForm.validate((valid) => {
        if (valid) {
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
        }
      })
    },
    openAdd() {
      this.drawer.title = this.$t('dictionaryManagement.addDictionary')
      this.drawer.type = 'add'
      this.uploadForm = {
        dictName: null,
        dictType: null,
        status: '0',
        remark: null,
        dictId: null,
      }
      this.drawer.visible = true
    },
    handleDelete(data) {
      const dictIds = data.dictId || this.ids
      this.$confirm(
        this.$t('dictionaryManagement.deleteConfirm', { id: dictIds }),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then((res) => {
          deleteType(dictIds)
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
    openChaneg(data) {
      if (data) {
        for (let key in this.uploadForm) {
          if (data[key]) {
            this.uploadForm[key] = data[key]
          }
        }
      } else if (this.selectData.length == 1) {
        for (let key in this.uploadForm) {
          if (this.selectData[0][key]) {
            this.uploadForm[key] = this.selectData[0][key]
          }
        }
      } else {
        return
      }
      this.drawer.title = this.$t('dictionaryManagement.editDictionary')
      this.drawer.type = 'change'

      this.drawer.visible = true
    },
    handleSelect(val) {
      this.selectData = val
      this.ids = val.map((item) => item.dictId)
      this.isDel = !val.length
      this.isUpload = val.length != 1
    },
    handleClose() {
      this.$refs.uploadForm.resetFields()
      this.drawer.visible = false
    },
    handleSizeChange(val) {
      this.params.pageSize = val
      this.searchData()
    },
    handleCurrentChange(val) {
      this.params.pageNum = val
      this.searchData()
    },
    reset() {
      this.queryParams = this.$options.data().queryParams
      this.createTime = []
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
    .searchTop {
      display: flex;

      /deep/.el-input,
      .el-select {
        // width: 100%;
        margin-right: 10px;
        .el-input__inner {
          height: 42px;
          background: var(--bg-color);
          border-radius: 6px;
          &::placeholder {
            color: var(--text-color-secondary);
          }
        }
      }
      :deep .el-button {
        font-size: 14px;
        height: 42px;
        padding: 10px 28px;
      }
    }
    > .btnss {
      margin-left: 10px;
      > button {
        border: none;
        height: 42px;
        padding: 0 20px;
      }
      > button:nth-of-type(2) {
        color: #00b578 !important;
        background-color: rgba(0, 181, 120, 0.1);
        // border: 1px rgba(0, 181, 120, 0.2) solid;
        // border: hidden;
      }
    }
  }
  .content {
    margin: 20px 0;
    height: calc(var(--bgvh) - 144px);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    background-color: var(--bg-color);
    .filter {
      display: flex;
      align-items: center;
      padding: 10px 20px;

      .filterTime {
        height: 100%;
        display: flex;
        align-items: center;
        font-size: 12px;
        margin-right: 10px;

        /deep/.el-date-editor {
          width: 340px;
          padding: 0px 5px !important;
          .el-range__icon {
            display: none;
          }
          .el-range-separator {
            width: 20px;
            line-height: 30px !important;
          }
          .el-range-input {
            width: 50%;
            text-align: left;
            padding-left: 10px;
            background-color: rgba(#000, 0.05);
          }
        }
        > span {
          margin-right: 5px;
        }
      }
    }
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
      margin: 10px auto;
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

  .bottomBtn {
    display: flex;
    justify-content: flex-end;

    .el-button {
      padding: 0px 24px;
      height: 42px;
      font-size: 14px;
      border-radius: 8px;
    }
  }
}
:deep.el-pagination {
  display: flex;
  justify-content: center;
  // .el-pager {
  //   .active {
  //     background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
  //   }
  // }
}
:deep(.el-table) {
  font-size: 16px !important;
  .has-gutter,
  .el-button {
    font-size: 16px !important;
  }
}
</style>
