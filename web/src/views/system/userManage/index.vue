<template>
  <div class="userManage">
    <div class="statusBar">
      <Search :list="list" ref="search" @search="searchData" />

      <div class="btnss">
        <el-button
          class="add"
          @click="
            () => {
              $refs.AddUser.edit = false
              $refs.AddUser.reset()
              $refs.AddUser.show()
            }
          "
          >{{ $t('common.add') }}
        </el-button>
        <el-button plain @click="handleExport"
          >{{ $t('common.export') }}
        </el-button>
      </div>
    </div>
    <div class="content">
      <div class="leftBox">
        <div>
          <el-tree
            ref="tree"
            :data="data"
            default-expand-all
            :props="defaultProps"
            @node-click="checkChange"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
          ></el-tree>
        </div>
      </div>
      <div class="reightBox">
        <div>
          <el-table
            :data="tableData"
            border
            style="width: 100%"
            :header-cell-style="{
              background: 'rgba(230,238,252,0.3)',
            }"
            @selection-change="handleSelectionChange"
            height="675px"
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
                  <el-switch
                    v-model="row[col.prop]"
                    active-color="#409EFF"
                    @change="
                      (bl) => {
                        switchChange(bl, row)
                      }
                    "
                  >
                  </el-switch>
                </div>
                <div v-if="['operation'].includes(col.prop)">
                  <span class="el-dropdown-link" @click="edit(row)">
                    {{ $t('common.modify') }}
                  </span>
                  <span class="el-dropdown-link" @click="openResetPwd(row)">{{
                    $t('userManage.resetPassword')
                  }}</span>
                  <span class="el-dropdown-link" @click="assignRoles(row)">{{
                    $t('userManage.assignRoles')
                  }}</span>
                  <span
                    style="color: red"
                    class="el-dropdown-link"
                    @click="remove(row)"
                  >
                    {{ $t('common.delete') }}
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
    </div>
    <AddUser
      ref="AddUser"
      @success="searchData($refs.search.formData)"
      :options="data"
    />
    <Upload ref="Upload" />
    <Reset ref="Reset" @success="searchData($refs.search.formData)" />
    <AssignRoles ref="AssignRoles" />
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import AddUser from './components/AddUser.vue'
import Upload from './components/Upload.vue'
import Reset from './components/Reset.vue'
import AssignRoles from './components/AssignRoles.vue'
import {
  getTreeselect,
  getList,
  changeStatus,
  getUserInfo,
  deletetUser,
  exportData,
} from '@/api/system/userManagement.js'

export default {
  components: {
    Search,
    AddUser,
    Upload,
    Reset,
    AssignRoles,
  },
  computed: {
    list() {
      return [
        {
          type: 'input',
          placeholder: this.$t('userManage.inputDeptName'),
          prop: {
            key: 'deptName',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: this.$t('userManage.inputUserName'),
          prop: {
            key: 'userName',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: this.$t('userManage.inputPhoneNumber'),
          prop: {
            key: 'phonenumber',
            value: null,
          },
        },
        {
          type: 'select',
          placeholder: this.$t('userManage.selectUserStatus'),
          options: [
            {
              value: '0',
              label: this.$t('common.normal'),
            },
            {
              value: '1',
              label: this.$t('common.disabled'),
            },
          ],
          prop: {
            key: 'status',
            value: null,
          },
        },
        {
          type: 'time',
          placeholder: this.$t('userManage.selectTime'),
          prop: {
            key: 'params',
            value: null,
          },
        },
      ]
    },
    tableHeader() {
      return [
        {
          prop: 'userId',
          id: 1,
          label: this.$t('userManage.userId'),
        },
        {
          prop: 'userName',
          id: 2,
          label: this.$t('userManage.userName'),
        },
        {
          prop: 'nickName',
          id: 3,
          label: this.$t('userManage.nickName'),
        },
        {
          prop: 'enterpriseName',
          id: 4,
          label: this.$t('userManage.enterpriseName'),
        },
        {
          prop: 'dept',
          id: 5,
          label: this.$t('userManage.dept'),
        },
        {
          prop: 'status',
          id: 6,
          label: this.$t('common.status'),
        },
        {
          prop: 'operation',
          id: 7,
          width: this.isEnglish() ? '320px' : '220px',
          label: this.$t('common.operation'),
        },
      ]
    },
  },
  data() {
    return {
      data: [],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
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
  watch: {
    data: {
      handler(val) {},
    },
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      this.getTreeselect()
      this.getLists()
    },
    handleSelectionChange(data) {
      this.selectData = data
    },
    openResetPwd(row) {
      this.$refs.Reset.userId = row.userId
      this.$refs.Reset.show()
    },
    assignRoles(row) {
      this.$refs.AssignRoles.authRole(row.userId)
      this.$refs.AssignRoles.show()
    },

    // dropdown(val, row) {
    //   console.log(val, '????');
    //   if (val === 'resetPassword') {
    //     this.$refs.Reset.userId = row.userId
    //     this.$refs.Reset.show()
    //   } else {
    //     this.$refs.AssignRoles.authRole(row.userId)
    //     this.$refs.AssignRoles.show()
    //   }
    // },

    removes() {
      let data = this.selectData.reduce((arr, item) => {
        arr.push(item.userId)
        return arr
      }, [])
      this.$confirm(
        this.$t('userManage.deleteConfirmBatch', { ids: data.join(',') }),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          deletetUser(data.join(',')).then((res) => {
            this.getLists(this.deptId)
            this.$message({
              type: 'success',
              message: this.$t('common.success'),
            })
          })
        })
        .catch(() => {})
    },
    remove(data) {
      this.$confirm(
        this.$t('userManage.deleteConfirm', { id: data.userId }),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          deletetUser(data.userId).then((res) => {
            this.searchData(this.$refs.search.formData)
            this.$message({
              type: 'success',
              message: this.$t('common.success'),
            })
          })
        })
        .catch(() => {})
    },
    getTreeselect() {
      getTreeselect().then((res) => {
        this.data = res.data
      })
    },
    getLists(deptId = {}, data = {}) {
      getList({
        ...this.searchFrom,
        ...deptId,
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
      let st = !bl
        ? this.$t('userManage.disableConfirm', { name: data.userName })
        : this.$t('userManage.enableConfirm', { name: data.userName })
      this.$confirm(st, this.$t('userManage.tip'), {
        confirmButtonText: this.$t('common.sure'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          changeStatus({ status: bl ? '0' : '1', userId: data.userId }).then(
            (res) => {
              this.$message({
                type: 'success',
                message: this.$t('common.success'),
              })
            }
          )
        })
        .catch(() => {
          data.status = !bl
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
    /*
     * isPagination: 是否为点击分页
     * */
    searchData(val, isPagination = false) {
      let data = JSON.parse(JSON.stringify(val))
      if (!isPagination) this.deptId = {}
      if (data.deptName != undefined) {
        this.$refs.tree.filter(data.deptName)
      } else {
        this.$refs.tree.filter()
      }
      if (data.params) {
        let time = JSON.parse(JSON.stringify(data.params))
        delete data.params
        this.getLists(this.deptId, this.addDateRange(data, time))
        return
      }
      this.getLists(this.deptId, data)
    },
    handleCurrentChange(val) {
      this.searchFrom.pageNum = val
      this.searchData(this.$refs.search.formData, true)
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
      getUserInfo(row.userId).then((res) => {
        let formData = {}
        formData = { ...res.data }
        formData['deptId'] = res.data.dept
          ? this.checkedCascaderHandle(this.data, [res.data.dept.deptId])[0]
          : []
        formData['postIds'] = res.postIds
        formData['roleIds'] = res.roleIds

        this.$refs.AddUser.ruleForm = formData
        this.$refs.AddUser.edit = true
        this.$refs.AddUser.show()
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
    handleExport() {
      this.download(
        exportData(),
        {
          ...this.searchFrom,
        },
        `role_${new Date().getTime()}.xlsx`
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
    // justify-content: space-between;
    height: 42px;

    > .btnss {
      margin-left: 10px;
      > button {
        border-radius: 6px;
        border: none;
        height: 42px;
        padding: 0 20px;
        font-size: 14px;
      }

      > button:nth-of-type(2) {
        color: #fa5151 !important;
        background-color: var(--bg-color-lighter);
      }

      > button:nth-of-type(3) {
        color: #ff8f1f !important;
        background-color: rgba(255, 143, 31, 0.1);
      }

      > .add {
        // color: #3662ec !important;
        // background-color: rgba(54, 98, 236, 0.1);
        color: #fff;
        border: none;
        background: #409eff;
      }
    }
  }

  .content {
    margin: 20px 0;
    height: calc(100% - 60px);
    // height: 100%;
    display: flex;

    .leftBox {
      width: 315px;
      height: 100%;
      background: var(--bg-color);
      border-radius: 6px 6px 6px 6px;
      opacity: 1;
      padding: 20px;

      > div {
        height: 100%;
        overflow: auto;
      }

      :deep .el-tree-node__content {
        height: 42px;
      }
    }

    .reightBox {
      width: calc(100% - 315px);
      height: 100%;
      margin-left: 20px;
      background: var(--bg-color);
      border-radius: 6px 6px 6px 6px;
      opacity: 1;
      padding: 20px;
      overflow: hidden;

      > div {
        overflow: auto;
        height: 100%;
      }

      .pagination {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px 20px 0 20px;

        :deep .el-pager {
          .active {
            background: #409eff;
          }
        }
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
</style>
