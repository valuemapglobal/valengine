<!--
 * @Date: 2023-10-31 13:45:01
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-01-04 17:53:11
 * @FilePath: \vm-micro-middleground\src\interfacePlatform\modules\interfaceUser.vue
-->
<!--
 * @Date: 2023-10-31 13:45:01
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2023-10-31 14:29:40
 * @FilePath: \vm-micro-middleground\src\interfacePlatform\modules\interfaceUser.vue
-->
<template>
  <div class="tableTemplate">
    <div class="searchTop">
      <div class="search">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
        />
        <!-- <el-button
          type="primary"
          @click="getList"
        >搜 索</el-button> -->
        <el-button type="primary" icon="el-icon-plus" @click="addUser"
          >申 请</el-button
        >
      </div>
      <!-- <div class="operate">

      </div> -->
    </div>
    <div class="tableWrapper">
      <gutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :handle="tableHandle"
        :tableHeight="220"
        :collectionList="collectionList"
      >
        <template #bt_handle="{ data }">
          <el-button type="text" @click="openRole(data.row)">
            接口信息
          </el-button>
        </template>
      </gutuTable>
    </div>
    <div class="pagination">
      <gutuPagination
        ref="gutuPagination"
        :total="total"
        @searchData="getList"
      />
    </div>
    <el-drawer
      :close-on-click-modal="false"
      :title="drawer.title"
      :visible.sync="drawer.visable"
      :size="drawer.size"
      append-to-body
      :before-close="closeDrawer"
    >
      <div v-if="drawer.type == 'add'">
        <el-form
          :model="userForm"
          label-width="80px"
          :rules="userRules"
          ref="userFormRef"
        >
          <div class="generateKey">
            <div>
              <p>
                appkey：<span>{{ userForm.appKey }}</span>
              </p>
              <p>
                sercet：<span>{{ userForm.secret }}</span>
              </p>
            </div>
            <el-form-item prop="appKey">
              <el-button type="primary" @click="getKeyAndSecret"
                >生成appkey和secret</el-button
              >
            </el-form-item>
          </div>
          <el-form-item label="用户名称" prop="userId">
            <el-select
              v-model="userForm.userId"
              placeholder="请选择"
              filterable
            >
              <el-option
                v-for="item in userCodeList"
                :key="item.value"
                :label="item.roleCode"
                :value="item.userId"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bottomBtns">
          <el-button type="primary" @click="submitUser">确 定 </el-button>
          <el-button @click="closeDrawer">取 消</el-button>
        </div>
      </div>
      <div v-if="drawer.type == 'info'">
        <div class="generateKey">
          <div>
            <p>
              用户名称：<span>{{ rowInfo.userName }}</span>
            </p>
            <p>
              部门：<span>{{ rowInfo.dept && rowInfo.dept.deptName }}</span>
            </p>
          </div>
        </div>
        <el-form label-width="80px">
          <el-form-item label="菜单权限">
            <div class="tree">
              <el-tree
                ref="roleTreeRef"
                :data="treeData"
                show-checkbox
                node-key="id"
              >
              </el-tree>
            </div>
          </el-form-item>
        </el-form>
        <div class="bottomBtns">
          <el-button type="primary" @click="submitRole"> 保 存 </el-button>
          <el-button @click="closeDrawer">取 消</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
import {
  getUserKey,
  getUser,
  addInterfaceUser,
  getTree,
  getPermissions,
  updatePermissions,
} from '../api/interfaceUser'
export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  data() {
    return {
      dataList: [],
      loading: false,
      columnConfig: [
        { label: '用户名称', field: 'userName', width: '150', align: 'left' },
        {
          label: '部门',
          field: 'dept',
          subfield: 'deptName',
          width: '',
          type: 'object',
          align: 'left',
        },
        { label: 'appkey', field: 'appKey', width: '260', align: 'left' },
        { label: 'secret', field: 'secret', width: '', align: 'left' },
        {
          label: '添加时间',
          field: 'createTime',
          width: '200',
          align: 'left',
          type: 'timeStamp',
        },
      ],
      tableHandle: {
        fixed: 'right',
        width: '',
        label: '操作',
        align: 'left',
        slot: true,
      },
      collectionList: {},
      total: 0,
      queryParams: {
        userName: null,
      },
      drawer: {
        title: null,
        visable: false,
        size: '35%',
      },
      userForm: {
        appKey: null,
        secret: null,
        userId: null,
      },
      userRules: {
        appKey: [
          {
            required: true,
            message: '请先生成appKey和secret',
            trigger: 'blur',
          },
        ],
        userId: [
          { required: true, message: '请选择角色码', trigger: 'change' },
        ],
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      userCodeList: [],
      rowInfo: {},
      treeData: [],
    }
  },
  watch: {
    queryParams: {
      handler(val) {
        this.getList()
      },
      deep: true,
    },
  },
  mounted() {
    this.getList()
    this.init()
  },
  methods: {
    init() {
      getUser({ pageNum: 1, pageSize: 9999 })
        .then((res) => {
          if (res.code == 200) {
            res.rows.map(
              (item) =>
                (item.roleCode = `${item.userName}${
                  item.dept && item.dept.deptName
                    ? '（' + item.dept.deptName + '）'
                    : ''
                }`)
            )
            this.userCodeList = res.rows
          }
        })
        .catch(() => {})
      getTree()
        .then((res) => {
          if (res.code == 200) {
            let data = res.data
            this.treeData = this.handleTree(data)
            console.log(this.treeData)
          }
        })
        .catch(() => {})
    },
    getList(data) {
      let params = { ...this.params, ...data }
      getUser({ ...this.queryParams, ...params })
        .then((res) => {
          if (res.code == 200) {
            this.dataList = res.rows
            this.total = res.total
          }
        })
        .catch(() => {})
    },
    addUser() {
      this.drawer.title = '添加用户'
      this.drawer.type = 'add'
      this.drawer.size = '35%'
      this.drawer.visable = true
    },
    openRole(data) {
      this.rowInfo = JSON.parse(JSON.stringify(data))
      this.getUserPermissions(data.userId)
      this.drawer.title = '接口信息'
      this.drawer.type = 'info'
      this.drawer.size = '35%'
      this.drawer.visable = true
    },
    getKeyAndSecret() {
      getUserKey()
        .then((res) => {
          if (res.code == 200) {
            this.userForm = { ...this.userForm, ...res.data }
            this.$refs.userFormRef.clearValidate('appKey')
          }
        })
        .catch(() => {})
    },
    submitUser() {
      this.$refs.userFormRef.validate((valid) => {
        if (valid) {
          addInterfaceUser({ ...this.userForm })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(res.data)
                this.$refs.userFormRef.resetFields()
                this.closeDrawer()
              }
            })
            .catch(() => {})
        }
      })
    },
    submitRole() {
      let params = {
        userId: this.rowInfo.userId,
        manageNos: this.$refs.roleTreeRef.getCheckedKeys(),
      }
      updatePermissions(params)
        .then((res) => {
          if (res.code == 200) {
            this.$message.success('操作成功')
            this.closeDrawer()
          }
        })
        .catch(() => {})
    },
    getUserPermissions(id) {
      this.$nextTick(() => {
        let treeList = this.treeData
        for (let i = 0; i < treeList.length; i++) {
          if (this.$refs.roleTreeRef)
            this.$refs.roleTreeRef.store.nodesMap[
              treeList[i].id
            ].expanded = false
        }
      })
      getPermissions({ userId: id })
        .then((res) => {
          if (res.code == 200) {
            this.$refs.roleTreeRef.setCheckedKeys([])
            res.data.forEach((item) => {
              this.$refs.roleTreeRef.setChecked(
                item.interfaceManageNo,
                true,
                false
              )
            })
          }
        })
        .catch(() => {})
    },
    closeDrawer() {
      this.drawer.visable = false
      this.userForm = this.$options.data().userForm
      this.rowInfo = this.$options.data().rowInfo
      this.getList()
    },
    handleTree(data) {
      data.forEach((item) => {
        item.id = item.interfaceManageNo || item.interfaceSourceNo
        item.label = item.interfaceName || item.dataName
        if (item.hasOwnProperty('children') && item.children.length > 0) {
          this.handleTree(item.children)
        }
      })
      return data
    },
  },
}
</script>

<style lang="less" scoped>
@import '/src/assets/less/commonCss.less';
.generateKey {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(var(--primary-color), 0.04);
  padding: 16px;
  font-size: 14px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  margin-bottom: 20px;
  p {
    color: var(--text-color-tertiary);
    margin-bottom: 10px;
    span {
      color: var(--text-color-secondary) !important;
    }
  }
  p:last-of-type {
    margin-bottom: 0px;
  }
}
::v-deep.gutuTable {
  .el-table {
    font-size: 16px !important;
    .has-gutter,
    .el-button {
      font-size: 16px !important;
    }
  }
}
.tree {
  width: 100%;
  height: 430px;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  overflow-y: auto;
  border-radius: 10px;
}
</style>
