<template>
  <div class="userManage">
    <div class="statusBar">
      <div class="searchTop">
        <el-input
          v-model="queryParams.dictLabel"
          :placeholder="$t('dictionaryManagement.dictLabel')"
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
          >{{ $t('common.add') }}
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="isDel"
          size="default"
          icon="el-icon-delete"
          @click="handleDelete"
          >{{ $t('common.delete') }}
        </el-button>

        <el-button type="" size="default" @click="goBack">{{
          $t('common.back')
        }}</el-button>
      </div>
    </div>
    <div class="content">
      <div class="filter">
        <GutuSelect
          :options="dictNameList"
          :data.sync="queryParams.dictType"
          flatten
          :config="{ placeholder: $t('dictionaryManagement.dictName') }"
          :adaptiveWidth="{ enable: true, minWidth: '90px' }"
        />
        <GutuSelect
          :options="statusOptions"
          :data.sync="queryParams.status"
          flatten
          :config="{ placeholder: $t('dictionaryManagement.dictStatus') }"
          :adaptiveWidth="{ enable: true, minWidth: '90px' }"
        />
      </div>
      <el-table
        :data="tableData"
        border
        height="100px"
        style="width: 100%"
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
                :to="'/DictionaryManagement/DictionaryDetail/' + row.dictId"
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
      append-to-body
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
              <el-form-item :label="$t('dictionaryManagement.dictType')">
                <el-input v-model="uploadForm.dictType" :disabled="true" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.dataLabel')"
                prop="dictLabel"
              >
                <el-input
                  v-model="uploadForm.dictLabel"
                  :placeholder="$t('dictionaryManagement.inputDataLabel')"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.dataValue')"
                prop="dictValue"
              >
                <el-input
                  v-model="uploadForm.dictValue"
                  :placeholder="$t('dictionaryManagement.inputDataValue')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.cssClass')"
                prop="cssClass"
              >
                <el-input
                  v-model="uploadForm.cssClass"
                  :placeholder="$t('dictionaryManagement.inputCssClass')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.displaySort')"
                prop="dictSort"
              >
                <el-input-number
                  v-model="uploadForm.dictSort"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('dictionaryManagement.echoStyle')"
                prop="listClass"
              >
                <el-select v-model="uploadForm.listClass">
                  <el-option
                    v-for="item in listClassOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item :label="$t('common.status')" prop="status">
                <el-radio-group v-model="uploadForm.status">
                  <el-radio
                    v-for="dict in DictStatus"
                    :key="dict.value"
                    :label="dict.value"
                    >{{ dict.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item :label="$t('common.remark')" prop="remark">
                <el-input
                  v-model="uploadForm.remark"
                  type="textarea"
                  :placeholder="$t('dictionaryManagement.inputContent')"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="bottomBtn">
          <el-button class="btn" type="primary" @click="operation"
            >{{ $t('common.sure') }}
          </el-button>
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
  getData,
  selectAll,
  insertData,
  updateData,
  deleteData,
  selectList,
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
          prop: 'dictCode',
          id: 1,
          label: this.$t('dictionaryManagement.dictCode'),
          width: '100px',
          align: 'left',
        },
        {
          prop: 'dictLabel',
          id: 2,
          label: this.$t('dictionaryManagement.dictLabel'),
          align: 'left',
        },
        {
          prop: 'dictValue',
          id: 3,
          label: this.$t('dictionaryManagement.dictValue'),
          align: 'left',
        },
        {
          prop: 'dictSort',
          id: 4,
          label: this.$t('dictionaryManagement.dictSort'),
          align: 'center',
          width: '116px',
        },
        {
          prop: 'status',
          id: 5,
          label: this.$t('common.status'),
          align: 'center',
          width: '116px',
        },
        {
          prop: 'remark',
          id: 6,
          label: this.$t('common.remark'),
          align: 'left',
        },
        {
          prop: 'createTime',
          id: 7,
          label: this.$t('common.createTime'),
          align: 'left',
        },
        {
          prop: 'operation',
          id: 8,
          label: this.$t('common.operation'),
          width: '180px',
          align: 'left',
        },
      ]
    },
    listClassOptions() {
      return [
        {
          value: 'default',
          label: this.$t('dictionaryManagement.default'),
        },
        {
          value: 'primary',
          label: this.$t('dictionaryManagement.primary'),
        },
        {
          value: 'success',
          label: this.$t('dictionaryManagement.success'),
        },
        {
          value: 'info',
          label: this.$t('dictionaryManagement.info'),
        },
        {
          value: 'warning',
          label: this.$t('dictionaryManagement.warning'),
        },
        {
          value: 'danger',
          label: this.$t('dictionaryManagement.danger'),
        },
      ]
    },
    DictStatus() {
      return [
        { label: this.$t('common.normal'), value: '0' },
        { label: this.$t('common.disabled'), value: '1' },
      ]
    },
    rules() {
      return {
        dictLabel: {
          required: true,
          message: this.$t('dictionaryManagement.inputDataLabel'),
          trigger: 'blur',
        },
        dictValue: {
          required: true,
          message: this.$t('dictionaryManagement.inputDataValue'),
          trigger: 'blur',
        },
        dictSort: {
          required: true,
          message: this.$t('dictionaryManagement.inputDisplaySort'),
          trigger: 'blur',
        },
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
      detail: {},

      isDel: true,
      isUpload: true,

      queryParams: {
        dictLabel: null,
        dictType: null,
        status: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      total: 0,
      dictNameList: [],

      uploadForm: {
        dictName: null,
        dictType: '2',
        status: '0',
        remark: null,
        dictId: null,
      },
      ids: [],
    }
  },
  mounted() {
    this.init()
    if (this.$route.name == 'DictionaryDetailC') {
      this.dictId = 166
    } else {
      this.dictId = this.$route.query.id
    }
    this.queryParams.dictType = this.dictId
  },
  watch: {
    queryParams: {
      handler(val) {
        this.params = this.$options.data().params
        this.searchData()
      },
      deep: true,
    },
  },
  methods: {
    init() {
      selectAll()
        .then((res) => {
          res.rows.map((item) => {
            item.label = item.dictName
            item.value = item.dictId + ''
            return item
          })
          this.dictNameList = res.rows
          this.uploadForm.dictType = this.handleDomain(this.dictId)
          this.searchData()
        })
        .catch((err) => {})
    },
    searchData() {
      let upload = JSON.parse(JSON.stringify(this.queryParams))
      if (upload && upload.dictType) {
        if (this.handleDomain(upload.dictType)) {
          upload.dictType = this.handleDomain(upload.dictType)
        }
      }
      if (this.$route.name == 'DictionaryDetailC') {
        upload.dictType = 'sys_version'
      }
      selectList({ ...this.params, ...upload })
        .then((res) => {
          this.tableData = res.rows
          this.total = res.total
        })
        .catch((err) => {})
    },
    handleDomain(data) {
      let myData = this.dictNameList.find((item) => item.dictId + '' === data)
      if (myData) {
        return myData.dictType
      } else {
        return false
      }
    },
    handleChange(data) {
      if (data.prop.value) {
        this.dictId = data.prop.value
      }
    },
    operation() {
      this.$refs.uploadForm.validate((valid) => {
        if (valid) {
          if (this.drawer.type === 'add') {
            insertData({ ...this.uploadForm })
              .then((res) => {
                if (res.code == 200) {
                  this.$message.success(this.$t('common.success'))
                  this.handleClose()
                  this.searchData()
                }
              })
              .catch((err) => {})
          } else if (this.drawer.type === 'change') {
            updateData({ ...this.uploadForm })
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
      this.drawer.visible = true
    },
    handleDelete(data) {
      const dictIds = (data && data.dictCode) || this.ids
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
          deleteData(dictIds)
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
      const dictCode = (data && data.dictCode) || this.ids
      getData(dictCode)
        .then((res) => {
          this.uploadForm = res.data
        })
        .catch((err) => {})
      this.drawer.title = this.$t('dictionaryManagement.editDictionaryType')
      this.drawer.type = 'change'
      this.drawer.visible = true
    },
    handleSelect(val) {
      this.selectData = val
      this.ids = val.map((item) => item.dictCode)
      this.isDel = !val.length
      this.isUpload = val.length != 1
    },
    handleClose() {
      if (this.$refs.uploadForm) this.$refs.uploadForm.resetFields()
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
      this.queryParams.dictType = this.$route.query.id
    },
    goBack() {
      this.$router.go(-1)
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
        color: #fa5151 !important;
        background-color: rgba(250, 81, 81, 0.1);
      }

      > button:nth-of-type(3) {
        color: #ff8f1f !important;
        background-color: rgba(255, 143, 31, 0.1);
      }

      > button:nth-of-type(4) {
        color: #ff4040 !important;
        background-color: rgba(#ff4040, 0.1);
      }
    }
  }

  .content {
    margin: 20px 0;
    height: calc(100% - 70px);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    background-color: var(--bg-color);

    .filter {
      display: flex;
      align-items: center;
      padding: 10px 20px;
      > .gutuSelect {
        margin-right: 10px;
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

  .el-form-item {
    padding: 0px 20px;
    box-sizing: border-box;

    .el-form-item__content {
      display: flex;
    }

    .el-select,
    .el-input-number {
      width: 100%;
    }
  }

  .popularTags {
    .el-select {
      width: 300px;
    }
  }
}

:deep.el-pagination {
  display: flex;
  justify-content: center;
}

:deep(.el-table) {
  font-size: 16px !important;
  .has-gutter,
  .el-button {
    font-size: 16px !important;
  }
}
</style>
