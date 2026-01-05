<template>
  <div class="userManage">
    <div class="statusBar">
      <Search :list="list" @search="searchData" />
      <div class="btnss">
        <el-button type="primary" class="add" @click="openAdd">新增</el-button>
      </div>
    </div>
    <div class="content">
      <div class="centerBox">
        <div>
          <el-table
            :data="tableData"
            border
            style="width: 100%"
            @selection-change="handleSelectionChange"
            height="675px"
          >
            <el-table-column align="center" type="selection" width="55" />
            <el-table-column
              :show-overflow-tooltip="true"
              align="center"
              v-for="col in tableHeader"
              :key="col.id"
              :label="col.label"
              :width="col.width"
            >
              <template slot-scope="{ row }">
                <div v-if="!['operation', 'dept', 'status'].includes(col.prop)">
                  {{ row[col.prop] }}
                </div>
                <div v-if="['dept'].includes(col.prop)">
                  {{ row[col.prop] ? row[col.prop].deptName : '' }}
                </div>
                <div v-if="['status'].includes(col.prop)">
                  <el-switch
                    active-color="#409eff"
                    v-model="row[col.prop]"
                    @change="
                      (bl) => {
                        switchChange(bl, row)
                      }
                    "
                  />
                </div>
                <div
                  v-if="
                    ['operation'].includes(col.prop) && row.roleKey != 'admin'
                  "
                >
                  <span class="el-dropdown-link" @click="edit(row)">
                    修改
                  </span>
                  <span
                    style="color: red"
                    class="el-dropdown-link"
                    @click="remove(row)"
                  >
                    删除
                  </span>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page.sync="searchFrom.pageNum"
              :page-size="searchFrom.pageSize"
              layout="prev, pager, next, jumper"
              :total="totalNum"
            />
          </div>
        </div>
      </div>
    </div>
    <AddUser ref="AddUser" @success="getLists()" />
    <Upload ref="Upload" />
    <DataAccess ref="DataAccess" @success="getLists()" />
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import AddUser from './components/AddUser.vue'
import Upload from './components/Upload.vue'
import DataAccess from './components/DataAccess.vue'
import {
  getList,
  changeStatus,
  getUserInfo,
  deletetUser,
  roleMenuTreeselect,
  exportData,
} from '@/api/system/roleManagement.js'

export default {
  components: {
    Search,
    AddUser,
    Upload,
    DataAccess,
  },
  data() {
    return {
      list: [
        {
          type: 'input',
          placeholder: '请输入角色名称',
          prop: {
            key: 'roleName',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: '请输入权限字符',
          prop: {
            key: 'roleKey',
            value: null,
          },
        },
        {
          type: 'select',
          placeholder: '请输入选择角色状态',
          options: [
            {
              value: '0',
              label: '正常',
            },
            {
              value: '1',
              label: '停用',
            },
          ],
          prop: {
            key: 'status',
            value: null,
          },
        },
        {
          type: 'time',
          placeholder: '请选择时间',
          prop: {
            key: 'params',
            value: null,
          },
        },
      ],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      tableHeader: [
        {
          prop: 'roleId',
          id: 1,
          label: '角色编号',
        },
        {
          prop: 'roleName',
          id: 2,
          label: '角色名称',
        },
        {
          prop: 'roleKey',
          id: 3,
          label: '权限字符',
        },
        {
          prop: 'roleSort',
          id: 4,
          label: '显示顺序',
        },
        {
          prop: 'status',
          id: 5,
          label: '状态',
        },
        {
          prop: 'createTime',
          id: 6,
          label: '创建时间',
        },
        {
          prop: 'operation',
          id: 7,
          width: '220px',
          label: '操作',
        },
      ],
      tableData: [],
      searchFrom: {
        pageNum: 1,
        pageSize: 15,
      },
      totalNum: 0,
      deptId: {},
      selectData: [],
    }
  },
  watch: {},
  created() {
    this.init()
  },
  methods: {
    init() {
      this.getLists()
    },
    handleSelectionChange(data) {
      this.selectData = data
    },
    /*    dataPermission(row) {
     getUserInfo(row.roleId).then((res) => {
     let formData = {}
     formData = { ...res.data }
     this.$refs.DataAccess.ruleForm = formData
     this.$refs.DataAccess.show()
     })
     },*/
    /*    assignUsers(row) {
     this.$router.push({
     name: 'Distribution',
     query: {
     roleId: row.roleId,
     },
     })
     },*/
    openAdd() {
      this.$refs.AddUser.show()
      this.$nextTick(() => {
        this.$refs.AddUser.$refs.tree.setCheckedKeys([])
      })
      // this.$refs.AddUser.$refs.tree.setCurrentKey(null);
      // this.$refs.ruleForm.menuIds = []
    },
    /*removes() {
     let data = this.selectData.reduce((arr, item) => {
     arr.push(item.roleId)
     return arr
     }, [])
     this.$confirm(
     `是否确认删除用户编号为"${data.join(',')}"的数据项？`,
     '系统提示',
     {
     confirmButtonText: '确定',
     cancelButtonText: '取消',
     type: 'warning',
     }
     )
     .then(() => {
     deletetUser(data.join(',')).then((res) => {
     this.getLists(this.deptId)
     this.$message({
     type: 'success',
     message: '操作成功!',
     })
     })
     })
     .catch(() => {
     this.$message({
     type: 'info',
     message: '已取消',
     })
     })
     },*/
    remove(data) {
      this.$confirm(
        `是否确认删除用户编号为"${data.roleId}"的数据项？`,
        '系统提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(() => {
          deletetUser(data.roleId).then((res) => {
            this.getLists(this.deptId)
            this.$message({
              type: 'success',
              message: '操作成功!',
            })
          })
        })
        .catch(() => {})
    },
    getLists(deptId = {}, data = {}) {
      getList({
        ...this.searchFrom,
        // ...deptId,
        ...data,
      }).then((res) => {
        this.tableData = res.rows.reduce((arr, item) => {
          arr.push({
            ...item,
            status: !Number(item.status),
          })
          return arr
        }, [])
        this.totalNum = res.total
      })
    },
    switchChange(bl, data) {
      console.log(bl, data)
      let st = !bl
        ? `确认要"停用""${data.roleName}"用户吗？`
        : `确认要"启用""${data.roleName}"用户吗？`
      this.$confirm(st, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          changeStatus({ status: bl ? '0' : '1', roleId: data.roleId }).then(
            (res) => {
              this.$message({
                type: 'success',
                message: '操作成功!',
              })
            }
          )
        })
        .catch(() => {
          data.status = !bl
          this.$message({
            type: 'info',
            message: '已取消',
          })
        })
    },
    checkChange(data) {
      this.deptId = { deptId: data.id }
      this.getLists({ deptId: data.id })
    },
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    searchData(val) {
      let data = JSON.parse(JSON.stringify(val))
      if (data.deptName != undefined) {
        this.$refs.tree.filter(data.deptName)
      }
      if (data.params) {
        let time = JSON.parse(JSON.stringify(data.params))
        delete data.params
        this.getLists(this.deptId, this.addDateRange(data, time))
        return
      }
      this.getLists(this.deptId, data)
      console.log(data)
    },
    handleCurrentChange(val) {
      // console.log("此时的页数是", val);
      this.searchFrom.pageNum = val
      this.getLists()
    },
    addDateRange(params, dateRange, propName) {
      let search = params
      search.params =
        typeof search.params === 'object' &&
        search.params !== null &&
        !Array.isArray(search.params)
          ? search.params
          : {}
      dateRange = Array.isArray(dateRange) ? dateRange : []
      if (typeof propName === 'undefined') {
        search.params['beginTime'] = dateRange[0]
        search.params['endTime'] = dateRange[1]
      } else {
        search.params['begin' + propName] = dateRange[0]
        search.params['end' + propName] = dateRange[1]
      }
      return search
    },
    edit(row) {
      getUserInfo(row.roleId).then((res) => {
        let formData = {}
        formData = { ...res.data }
        this.$refs.AddUser.ruleForm = formData
        this.$refs.AddUser.edit = true
        this.$refs.AddUser.show()
        this.$nextTick(() => {
          roleMenuTreeselect(row.roleId).then((res2) => {
            // formData['menuIds'] = res2.checkedKeys
            res2.checkedKeys.forEach((v) => {
              this.$nextTick(() => {
                this.$refs.AddUser.$refs.tree.setChecked(v, true, false)
              })
            })
          })
        })
      })
    },
    checkedCascaderHandle(optionList, checkedIds = []) {
      // optionList Cascader 渲染的列表
      // checkedIds 返回的 选中的ids数组
      let arr = []

      optionList.forEach((it) => {
        let ids = []
        ids.push(it.id)
        checkedIds.forEach((cit) => {
          cit = Number(cit)
          if (it.id == cit) {
            arr.push(ids)
          }
          this.getCheckedParentId(it, cit, it, arr, ids)
        })
      })

      return arr
    },

    getCheckedParentId(option, current, rootItem, arr, ids = []) {
      option.children &&
        option.children.forEach((item) => {
          item.ids = JSON.parse(JSON.stringify(ids))
          item.ids && item.ids.push(item.id)
          if (item.id == current) {
            arr.push(item.ids)
          }
          this.getCheckedParentId(item, current, rootItem, arr, item.ids)
        })
    },
    /*    handleExport() {
     this.download(
     exportData(),
     {
     ...this.searchFrom,
     },
     `role_${new Date().getTime()}.xlsx`
     )
     },*/
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
        border-radius: 6px;
        font-size: 16px;
      }

      // > button:nth-of-type(1) {
      //   color: #00b578 !important;
      //   background-color: rgba(0, 181, 120, 0.1);
      // }
      > button:nth-of-type(2) {
        color: #fa5151 !important;
        background-color: rgba(250, 81, 81, 0.1);
      }

      > button:nth-of-type(3) {
        color: #ff8f1f !important;
        background-color: rgba(255, 143, 31, 0.1);
      }

      > .add {
        background-color: var(--primary-color);
        font-size: 14px;
      }
    }
  }

  .content {
    margin: 20px 0;
    height: calc(100% - 60px);
    display: flex;

    .centerBox {
      width: 100%;
      height: 100%;
      background: var(--bg-color);
      border-radius: 6px 6px 6px 6px;
      opacity: 1;
      padding: 20px;
      overflow: hidden;

      > div {
        height: 100%;
        overflow: auto;
      }

      .pagination {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px 20px 0 20px;
      }
    }
  }
}

.el-dropdown-link {
  font-size: 12px;
  font-weight: normal;
  color: var(--primary-color);
  cursor: pointer;
  line-height: 22px;
  margin: 0 5px;
}

:deep.el-pagination {
  display: flex;
  justify-content: center;

  .el-pager {
    .active {
      // background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
      background-color: #409eff;
    }
  }
}
</style>
