<template>
  <div class="approvalAuth">
    <div class="topSearch">
      <el-input
        v-model="queryParams.deptName"
        placeholder="搜索部门"
        clearable
        @clear="queryParams.deptName = null"
      ></el-input>
      <el-input
        v-model="queryParams.userName"
        placeholder="搜索账号"
        clearable
        @clear="queryParams.userName = null"
      ></el-input>
      <el-input
        v-model="queryParams.nickName"
        placeholder="搜索用户名"
        clearable
        @clear="queryParams.nickName = null"
      ></el-input>
      <el-button
        type="primary"
        size="normal"
        icon="el-icon-search"
        @click="getDataList"
        >搜索</el-button
      >
      <el-button
        type="primary"
        size="normal"
        @click="openAdd"
        icon="el-icon-plus"
        >新增用户权限</el-button
      >
    </div>
    <main>
      <GutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :collectionList="collectionList"
        :query="params"
        :tableHeight="180"
        :handle="tableHandle"
      >
        <template #bt_handle="{ data }">
          <div class="bt_handle">
            <el-button
              type="text"
              @click="handleDeleteAuth(data.row)"
              icon="el-icon-delete"
            >
              删除权限
            </el-button>
          </div>
        </template>
      </GutuTable>
      <GutuPagination
        style="margin-top: 10px"
        ref="gutuPaginationRef"
        :total="total"
        @searchData="updateParams"
      ></GutuPagination>
    </main>
    <AddUserAuth ref="addUserAuthRef" @refresh="getDataList" />
  </div>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import AddUserAuth from './components/AddUserAuth'

import { approvalAuthList, approvalAuthAssign } from '@/api/system/approvalAuth'
export default {
  components: {
    GutuTable,
    GutuPagination,
    AddUserAuth,
  },
  data() {
    return {
      queryParams: {
        deptName: null,
        userName: null,
        nickName: null,
        searchType: 2,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      loading: false,
      dataList: [],
      total: 10,
      columnConfig: [
        {
          label: '序号',
          align: 'center',
          type: 'serial',
          width: '100px',
        },
        {
          label: '部门名称',
          field: 'deptName',
          align: 'center',
          width: null,
        },
        {
          label: '用户账号',
          field: 'userName',
          align: 'center',
          width: null,
        },
        {
          label: '用户名',
          field: 'nickName',
          align: 'center',
          width: null,
        },
      ],
      tableHandle: {
        fixed: 'right',
        width: '300px',
        label: '操作',
        align: 'left',
        slot: true,
      },
      collectionList: {},
    }
  },
  watch: {
    queryParams: {
      handler() {
        // this.getDataList()
        this.params = this.$options.data().params
      },
      deep: true,
    },
  },
  mounted() {
    this.getDataList()
  },
  methods: {
    getDataList() {
      this.loading = true
      approvalAuthList({ ...this.queryParams, ...this.params })
        .then((res) => {
          this.dataList = res.data.list
          this.total = Number(res.data.total)
        })
        .finally(() => {
          this.loading = false
        })
    },
    resetParams() {
      this.dataList = []
      this.total = 0
      this.queryParams = this.$options.data().queryParams
      this.params = this.$options.data().params
      if (this.$refs.gutuPaginationRef) this.$refs.gutuPaginationRef.reset()
    },
    handleDeleteAuth(data) {
      this.$confirm(
        `确定取消用户 【${data.userName}】 的审批权限吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(() => {
          let params = { ...data, permissionLevel: 3 }
          approvalAuthAssign(params).then((res) => {
            if (res.code === 200) {
              this.$message.success('取消审批权限成功')
              this.resetParams()
              this.getDataList()
            }
          })
        })
        .catch(() => {})
    },
    openAdd() {
      this.$refs.addUserAuthRef.handleOpen()
    },
    updateParams(data) {
      this.params.pageNum = data.pageNum
      this.getDataList()
    },
  },
}
</script>
<style lang="less" scoped>
.approvalAuth {
  height: calc(var(--bgvh) - 42px);
  padding: 20px;

  .bt_handle {
    .el-button {
      font-size: 14px;
      color: #ff4d4f;
    }
  }

  :deep(.el-drawer) {
    .el-drawer__header {
      margin-bottom: 0;
      padding: 20px;
    }
    .el-drawer__body {
      padding: 0 20px 20px 20px;
    }
  }
}
.cardList {
  height: calc(100% - 60px);
  overflow-y: auto;
}
:deep(.topSearch) {
  display: flex;
  margin-bottom: 16px;
  .el-input {
    width: 240px;
    .el-input__inner {
      height: 42px;
    }
  }
  .el-input + .el-input {
    margin-left: 10px;
  }
  .el-button {
    margin-left: 10px;
  }
}
</style>
