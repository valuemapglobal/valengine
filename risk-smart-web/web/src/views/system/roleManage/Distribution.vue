<template>
  <div class="userManage">
    <div class="statusBar">
      <Search :list="list" @search="searchData" />
      <div class="btnss">
        <el-button
          class="add"
          @click="
            () => {
              $refs.AssignRoles.show()
            }
          "
          >新增</el-button
        >
        <el-button
          type="info"
          :disabled="!selectData.length"
          plain
          @click="cancelAll"
          >取消授权</el-button
        >

        <el-button
          @click="
            () => {
              $router.go(-1)
            }
          "
          >退出</el-button
        >
      </div>
    </div>
    <div class="content">
      <div class="centerBox">
        <el-table
          :data="tableData"
          border
          style="width: 100%"
          :header-cell-style="{
            background: 'rgba(230,238,252,0.3)',
          }"
          @selection-change="handleSelectionChange"
        >
          <el-table-column align="center" type="selection" width="55">
          </el-table-column>
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
                <el-tag v-if="row[col.prop] === '0'" type="success"
                  >正常</el-tag
                >
                <el-tag v-else type="warning">停用</el-tag>
              </div>
              <div
                v-if="
                  ['operation'].includes(col.prop) && row.roleKey != 'admin'
                "
              >
                <span
                  style="color: red"
                  class="el-dropdown-link"
                  @click="cancel(row)"
                >
                  取消授权
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
          ></el-pagination>
        </div>
      </div>
    </div>
    <AddUser ref="AddUser" @success="getLists()" />
    <Upload ref="Upload" />
    <AssignRoles ref="AssignRoles" @ok="getLists()" />
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import AddUser from './components/AddUser.vue'
import Upload from './components/Upload.vue'
import AssignRoles from './components/AssignRoles.vue'
import {
  getAllocatedList,
  deletetUser,
  putCancel,
  putCancelAll,
} from '@/api/system/roleManagement.js'

export default {
  components: {
    Search,
    AddUser,
    Upload,
    AssignRoles,
  },
  data() {
    return {
      list: [
        {
          type: 'input',
          placeholder: '请输入用户名称',
          prop: {
            key: 'userName',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: '请输入手机号码',
          prop: {
            key: 'phonenumber',
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
          prop: 'userName',
          id: 1,
          label: '用户名称',
        },
        {
          prop: 'nickName',
          id: 2,
          label: '用户昵称',
        },
        {
          prop: 'email',
          id: 3,
          label: '邮箱',
        },
        {
          prop: 'phonenumber',
          id: 4,
          label: '手机',
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
          label: '操作',
        },
      ],
      tableData: [],
      searchFrom: {
        pageNum: 1,
        pageSize: 15,
      },
      totalNum: 0,
      roleId: this.$route.query.roleId,
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
    cancel(row) {
      this.$confirm(`确认要取消该用户"${row.userName}"角色吗？`, '系统提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          putCancel({ roleId: this.roleId, userId: row.userId }).then((res) => {
            this.getLists()
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
    },
    cancelAll() {
      this.$confirm(`是否取消选中用户授权数据项？`, '系统提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let data = this.selectData.reduce((arr, item) => {
            arr.push(item.userId)
            return arr
          }, [])
          putCancelAll({ roleId: this.roleId, userId: data.join(',') }).then(
            (res) => {
              this.getLists()
              this.$message({
                type: 'success',
                message: '操作成功!',
              })
            }
          )
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消',
          })
        })
    },
    getLists(data = {}) {
      getAllocatedList({
        ...this.searchFrom,
        roleId: this.roleId,
        ...data,
      }).then((res) => {
        this.tableData = res.rows
        this.totalNum = res.total
      })
    },
    searchData(val) {
      let data = JSON.parse(JSON.stringify(val))
      if (data.params) {
        let time = JSON.parse(JSON.stringify(data.params))
        delete data.params
        this.getLists(this.addDateRange(data, time))
        return
      }
      this.getLists(data)
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
  },
}
</script>

<style lang="less" scoped>
.userManage {
  padding: 20px;
  height: calc(100vh - 42px);
  .statusBar {
    display: flex;
    justify-content: space-between;
    height: 42px;
    > .btnss {
      > button {
        border: none;
        height: 42px;
        padding: 0 20px;
      }
      //   > button:nth-of-type(1) {
      //     color: #00b578 !important;
      //     background-color: rgba(0, 181, 120, 0.1);
      //   }
      > button:nth-of-type(2) {
        color: #fa5151 !important;
        background-color: rgba(250, 81, 81, 0.1);
      }
      > button:nth-of-type(3) {
        color: #ff8f1f !important;
        background-color: rgba(255, 143, 31, 0.1);
      }
      > .add {
        color: #ff4040 !important;
        background-color: rgba(#ff4040, 0.1);
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
      background: #ffffff;
      border-radius: 6px 6px 6px 6px;
      opacity: 1;
      padding: 20px;
      .pagination {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
      }
    }
  }
}
.el-dropdown-link {
  font-size: 14px;
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
      background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
    }
  }
}
</style>
