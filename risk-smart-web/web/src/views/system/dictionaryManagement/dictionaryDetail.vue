<template>
  <div class="userManage">
    <div class="statusBar">
      <div class="searchTop">
        <el-input
          v-model="queryParams.dictLabel"
          placeholder="字典标签"
          clearable
        ></el-input>
        <el-button type="primary" @click="reset" size="default">重置</el-button>
      </div>
      <div class="btnss">
        <el-button
          type="primary"
          @click="openAdd"
          size="default"
          icon="el-icon-plus"
          >新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="isDel"
          size="default"
          icon="el-icon-delete"
          @click="handleDelete"
          >删除
        </el-button>

        <el-button type="" size="default" @click="goBack">返回</el-button>
      </div>
    </div>
    <div class="content">
      <div class="filter">
        <GutuSelect
          :options="dictNameList"
          :data.sync="queryParams.dictType"
          flatten
          :config="{ placeholder: '字典名称' }"
          :adaptiveWidth="{ enable: true, minWidth: '90px' }"
        />
        <GutuSelect
          :options="[
            { label: '正常', value: '0' },
            { label: '停用', value: '1' },
          ]"
          :data.sync="queryParams.status"
          flatten
          :config="{ placeholder: '字典状态' }"
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
              <span v-if="row.status === '0'" class="normal">正常</span>
              <span v-else-if="row.status === '1'" class="deactivate"
                >停用</span
              >
            </div>
            <div v-else-if="col.prop === 'operation'">
              <span class="el-dropdown-link" @click="openChaneg(row)">
                修改
              </span>
              <span
                style="color: red"
                class="el-dropdown-link"
                @click="handleDelete(row)"
              >
                删除
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
          label-width="100px"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item label="字典类型">
                <el-input v-model="uploadForm.dictType" :disabled="true" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="数据标签" prop="dictLabel">
                <el-input
                  v-model="uploadForm.dictLabel"
                  placeholder="请输入数据标签"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="数据键值" prop="dictValue">
                <el-input
                  v-model="uploadForm.dictValue"
                  placeholder="请输入数据键值"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="样式属性" prop="cssClass">
                <el-input
                  v-model="uploadForm.cssClass"
                  placeholder="请输入样式属性"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="显示排序" prop="dictSort">
                <el-input-number
                  v-model="uploadForm.dictSort"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="回显样式" prop="listClass">
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
              <el-form-item label="状态" prop="status">
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
              <el-form-item label="备注" prop="remark">
                <el-input
                  v-model="uploadForm.remark"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="bottomBtn">
          <el-button class="btn" type="primary" @click="operation"
            >确定
          </el-button>
          <el-button @click="handleClose">取消</el-button>
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
  data() {
    return {
      tableHeader: [
        {
          prop: 'dictCode',
          id: 1,
          label: '字典编码',
          width: '100px',
          align: 'left',
        },
        {
          prop: 'dictLabel',
          id: 2,
          label: '字典标签',
          align: 'left',
        },
        {
          prop: 'dictValue',
          id: 3,
          label: '字典键值',
          align: 'left',
        },
        {
          prop: 'dictSort',
          id: 4,
          label: '字典顺序',
          align: 'center',
          width: '116px',
        },
        {
          prop: 'status',
          id: 5,
          label: '状态',
          align: 'center',
          width: '116px',
        },
        {
          prop: 'remark',
          id: 6,
          label: '备注',
          align: 'left',
        },
        {
          prop: 'createTime',
          id: 7,
          label: '创建时间',
          align: 'left',
        },
        {
          prop: 'operation',
          id: 8,
          label: '操作',
          width: '180px',
          align: 'left',
        },
      ],
      listClassOptions: [
        {
          value: 'default',
          label: '默认',
        },
        {
          value: 'primary',
          label: '主要',
        },
        {
          value: 'success',
          label: '成功',
        },
        {
          value: 'info',
          label: '信息',
        },
        {
          value: 'warning',
          label: '警告',
        },
        {
          value: 'danger',
          label: '危险',
        },
      ],
      DictStatus: [
        { label: '正常', value: '0' },
        { label: '停用', value: '1' },
      ],

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
      rules: {
        dictLabel: {
          required: true,
          message: '请输入数据标签',
          trigger: 'blur',
        },
        dictValue: {
          required: true,
          message: '请输入数据键值',
          trigger: 'blur',
        },
        dictSort: {
          required: true,
          message: '请输入显示排序',
          trigger: 'blur',
        },
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
                  this.$message.success('操作成功')
                  this.handleClose()
                  this.searchData()
                }
              })
              .catch((err) => {})
          } else if (this.drawer.type === 'change') {
            updateData({ ...this.uploadForm })
              .then((res) => {
                if (res.code == 200) {
                  this.$message.success('操作成功')
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
      this.drawer.title = '添加字典'
      this.drawer.type = 'add'
      this.drawer.visible = true
    },
    handleDelete(data) {
      const dictIds = (data && data.dictCode) || this.ids
      this.$confirm(`是否删除编号为${dictIds}的字典?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then((res) => {
          deleteData(dictIds)
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('操作成功')
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
      this.drawer.title = '修改字典类型'
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
