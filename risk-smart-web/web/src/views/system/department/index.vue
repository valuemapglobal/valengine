<template>
  <div class="app-container">
    <!-- @search="searchData"  -->
    <div class="statusBar">
      <Search :list="list" @search="getList" />
      <div class="btnss">
        <!-- <el-button type="danger" plain icon="el-icon-delete">删除</el-button> -->
        <el-button icon="el-icon-plus" size="default" @click="handleAdd"
          >新增</el-button
        >
      </div>
    </div>
    <div class="content">
      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="deptList"
        border
        height="calc(var(--bgvh) - 100px)"
        row-key="deptId"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :header-cell-style="{
          background: 'rgba(230,238,252,0.3)',
        }"
        :cell-style="{
          fontSize: '14px',
        }"
      >
        <el-table-column
          prop="deptName"
          label="部门名称"
          width="550"
        ></el-table-column>
        <el-table-column
          prop="orderNum"
          label="排序"
          width=""
        ></el-table-column>
        <el-table-column prop="status" label="状态" width="">
          <template slot-scope="{ row }">
            <!-- <dict-tag
            :options="dict.type.sys_normal_disable"
            :value="scope.row.status"
          /> -->
            <div class="column_status">
              <span v-if="row.status === '0'" class="normal">正常</span>
              <span v-else-if="row.status === '1'" class="deactivate"
                >停用</span
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="创建时间"
          align="left"
          prop="createTime"
          width=""
        >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="left"
          class="table_button"
          width="300"
        >
          <template slot-scope="scope">
            <!-- <span
              class="el-dropdown-link"
              @click="openAccountNumber(scope.row, 1)"
            >
              百融账号
            </span> -->
            <span
              class="el-dropdown-link"
              @click="openAccountNumber(scope.row, 2)"
            >
              估图账号
            </span>
            <span class="el-dropdown-link" @click="handleUpdate(scope.row)">
              修改
            </span>
            <span class="el-dropdown-link" @click="handleAdd(scope.row)">
              新增
            </span>
            <span
              style="color: red"
              class="el-dropdown-link"
              @click="handleDelete(scope.row)"
              v-if="scope.row.parentId != 0"
            >
              删除
            </span>
            <!-- <el-button type="text" @click="handleUpdate(scope.row)"
              >修改</el-button
            >
            <el-button type="text" @click="handleAdd(scope.row)"
              >新增</el-button
            >
            <el-button
              v-if="scope.row.parentId != 0"
              type="text"
              @click="handleDelete(scope.row)"
              >删除</el-button
            > -->
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加或修改部门对话框 -->
    <el-drawer :title="title" :visible.sync="open" size="60%" direction="rtl">
      <div class="drawerContent" v-if="drawerType == 'default'">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-row>
            <el-col :span="24" v-if="form.parentId !== 0">
              <el-form-item label="上级部门" prop="parentId">
                <!-- <el-input
                  v-model="form.deptName"
                  placeholder="请输入部门名称"
                /> -->
                <el-cascader
                  v-model="form.parentId"
                  placeholder="选择上级部门"
                  :options="deptOptions"
                  :props="{ checkStrictly: true, value: 'id' }"
                ></el-cascader>
                <!-- <treeselect
                  v-model="form.parentId"
                  :options="deptOptions"
                  :normalizer="normalizer"
                  placeholder="选择上级部门"
                /> -->
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="部门名称" prop="deptName">
                <el-input
                  v-model="form.deptName"
                  placeholder="请输入部门名称"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属地区" prop="areaId">
                <el-select
                  v-model="form.areaId"
                  placeholder="请选择所属地区"
                  clearable
                >
                  <el-option
                    v-for="(item, index) in dataList"
                    :key="index"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="显示排序" prop="orderNum">
                <el-input-number
                  v-model="form.orderNum"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="负责人" prop="leader">
                <el-input
                  v-model="form.leader"
                  placeholder="请输入负责人"
                  maxlength="20"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input
                  v-model="form.phone"
                  placeholder="请输入联系电话"
                  maxlength="11"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input
                  v-model="form.email"
                  placeholder="请输入邮箱"
                  maxlength="50"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="部门状态">
                <el-radio v-model="form.status" label="0">正常</el-radio>
                <el-radio v-model="form.status" label="1">停用</el-radio>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="授权状态">
                <el-radio v-model="form.accredit" :label="1">已授权</el-radio>
                <el-radio v-model="form.accredit" :label="0">未授权</el-radio>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </div>
      <BairongDrawer
        v-if="drawerType == 'baiRong'"
        :dataInfo="currentRow"
        @close="
          () => {
            getList()
            cancel()
          }
        "
      />
      <ValupMapDrawer
        v-if="drawerType == 'valueMap'"
        :dataInfo="currentRow"
        @close="
          () => {
            getList()
            cancel()
          }
        "
      />
    </el-drawer>
  </div>
</template>

<script>
import {
  listDept,
  listData,
  getDept,
  delDept,
  addDept,
  updateDept,
} from '@/api/system/department.js'
import { getTreeselect } from '@/api/system/userManagement.js'
import Search from '@/components/pageSearch.vue'
import BairongDrawer from './components/bairongDrawer.vue'
import ValupMapDrawer from './components/valupMapDrawer.vue'

export default {
  name: 'Dept',
  components: {
    Search,
    BairongDrawer,
    ValupMapDrawer,
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 显示搜索条件
      showSearch: true,
      // 表格树数据
      deptList: [],
      // 部门树选项
      deptOptions: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        deptName: undefined,
        status: undefined,
      },
      // 表单参数
      form: {},
      dataList: [],
      // 表单校验
      rules: {
        parentId: [
          { required: true, message: '上级部门不能为空', trigger: 'blur' },
        ],
        deptName: [
          { required: true, message: '部门名称不能为空', trigger: 'blur' },
        ],
        orderNum: [
          { required: true, message: '显示排序不能为空', trigger: 'blur' },
        ],
        email: [
          {
            type: 'email',
            message: "'请输入正确的邮箱地址",
            trigger: ['blur', 'change'],
          },
        ],
        areaId: [
          { required: true, message: '显示排序不能为空', trigger: 'blur' },
        ],
        phone: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur',
          },
        ],
      },

      list: [
        {
          type: 'input',
          placeholder: '请输入部门名称',
          prop: {
            key: 'deptName',
            value: null,
          },
        },
        {
          type: 'select',
          placeholder: '请选择状态',
          prop: {
            key: 'status',
            value: null,
          },
          options: [
            { label: '正常', value: '0' },
            { label: '停用', value: '1' },
          ],
        },
      ],
      drawerType: 'default',
      currentRow: null,
    }
  },
  created() {
    // this.getList()
  },
  methods: {
    /** 查询部门列表 */
    getList(data) {
      this.loading = true
      listDept({ ...this.queryParams, ...data }).then((response) => {
        this.deptList = this.handleTree(response.data, 'deptId')
        this.loading = false
      })
      listData({
        pageNum: 1,
        pageSize: 100,
        dictType: 'nyr_service_Area',
        status: 0,
      }).then((response) => {
        let data = response.rows || response.data || []
        data.forEach((item) => {
          item.dictValue = parseInt(item.dictValue)
        })
        this.dataList = data
      })

      this.getDataList()
    },
    getDataList() {
      getTreeselect().then((response) => {
        this.deptOptions = response.data
      })
    },
    openAccountNumber(data, type) {
      this.currentRow = JSON.parse(JSON.stringify(data))
      this.drawerType = type == 1 ? 'baiRong' : 'valueMap'
      this.title = type == 1 ? '添加部门-百融' : '添加部门-估图'
      this.open = true
    },
    /** 转换部门数据结构 */
    normalizer(node) {
      // if (node.children && !node.children.length) {
      //   delete node.children;
      // }
      // return {
      //   id: node.deptId,
      //   label: node.deptName,
      //   children: node.children,
      // };
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        deptId: undefined,
        parentId: undefined,
        deptName: undefined,
        orderNum: undefined,
        leader: undefined,
        phone: undefined,
        email: undefined,
        status: '0',
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset()
      if (row != undefined) {
        this.form.parentId = row.deptId
      }
      this.drawerType = 'default'
      this.open = true
      this.title = '添加部门'
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false
      this.isExpandAll = !this.isExpandAll
      this.$nextTick(() => {
        this.refreshTable = true
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      if (row != undefined) {
        this.form.parentId = row.deptId
      }
      this.drawerType = 'default'
      getDept(row.deptId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改部门'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          let length = this.form.parentId.length
          if (length > 0) {
            this.form.parentId = this.form.parentId[length - 1]
          }
          if (this.form.deptId != undefined) {
            updateDept(this.form).then((response) => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addDept(this.form).then((response) => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm(
        '是否确认删除名称为"' + row.deptName + '"的数据项？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(function () {
          return delDept(row.deptId)
        })
        .then(() => {
          this.getList()
          this.$message.success('删除成功')
        })
        .catch(() => {})
    },

    handleTree(data, id, parentId, children) {
      let config = {
        id: id || 'id',
        parentId: parentId || 'parentId',
        childrenList: children || 'children',
      }

      var childrenListMap = {}
      var nodeIds = {}
      var tree = []

      for (let d of data) {
        let parentId = d[config.parentId]
        if (childrenListMap[parentId] == null) {
          childrenListMap[parentId] = []
        }
        nodeIds[d[config.id]] = d
        childrenListMap[parentId].push(d)
      }

      for (let d of data) {
        let parentId = d[config.parentId]
        if (nodeIds[parentId] == null) {
          tree.push(d)
        }
      }

      for (let t of tree) {
        adaptToChildrenList(t)
      }

      function adaptToChildrenList(o) {
        if (childrenListMap[o[config.id]] !== null) {
          o[config.childrenList] = childrenListMap[o[config.id]]
        }
        if (o[config.childrenList]) {
          for (let c of o[config.childrenList]) {
            adaptToChildrenList(c)
          }
        }
      }
      return tree
    },
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields()
      }
    },
    parseTime(time, pattern) {
      if (arguments.length === 0 || !time) {
        return null
      }
      const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
      let date
      if (typeof time === 'object') {
        date = time
      } else {
        if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
          time = parseInt(time)
        } else if (typeof time === 'string') {
          time = time
            .replace(new RegExp(/-/gm), '/')
            .replace('T', ' ')
            .replace(new RegExp(/\.[\d]{3}/gm), '')
        }
        if (typeof time === 'number' && time.toString().length === 10) {
          time = time * 1000
        }
        date = new Date(time)
      }
      const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay(),
      }
      const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
        let value = formatObj[key]
        // Note: getDay() returns 0 on Sunday
        if (key === 'a') {
          return ['日', '一', '二', '三', '四', '五', '六'][value]
        }
        if (result.length > 0 && value < 10) {
          value = '0' + value
        }
        return value || 0
      })
      return time_str
    },
  },
}
</script>
<style lang="less" scoped>
.app-container {
  padding: 20px;
  height: calc(var(--bgvh) - 42px);
  .statusBar {
    display: flex;
    // justify-content: space-between;
    height: 42px;
    > .btnss {
      margin-left: 10px;
      > button {
        border: none;
        border-radius: 6px;
        height: 42px;
        padding: 0 20px;
      }
      // > button:nth-of-type(1) {
      //   color: #fa5151 !important;
      //   background-color: rgba(#fa5151, 0.1);
      // }
      > button:nth-of-type(1) {
        color: #fff !important;
        background-color: var(--primary-color);
      }
    }
  }
  .content {
    margin: 20px 0;
    height: calc(100% - 70px);
    overflow: hidden;
    display: flex;
    flex-direction: column;

    // :deep.el-tree {
    //   overflow: auto;

    // }
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

    .table_button {
      :deep .el-button {
        margin-left: 30px !important;
      }
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
  margin-right: 10px;
  cursor: pointer;
}
.drawerContent {
  padding: 20px;

  .el-form-item {
    box-sizing: border-box;
    .el-form-item__content {
      display: flex;
    }

    .el-select,
    .el-input-number,
    .el-cascader {
      width: 100%;
    }
  }

  // :deep.el-form {
  //   .el-form-item {
  //     .el-form-item__content {
  //       .el-input-number {
  //         width: 100%;
  //       }
  //     }
  //     // width: 100%;
  //   }
  // }
}
.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  .el-button {
    padding: 0px 24px;
    height: 42px;
    font-size: 14px;
    border-radius: 8px;
  }
}
/deep/.el-table {
  font-size: 16px !important;
  .has-gutter,
  .el-button {
    font-size: 16px !important;
  }
}
/deep/.el-drawer {
  .el-drawer__header {
    margin-bottom: 20px;
  }
}
</style>
