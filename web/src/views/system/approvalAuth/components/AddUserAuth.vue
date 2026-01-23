<template>
  <div>
    <el-drawer
      :visible.sync="drawer.visible"
      :title="drawer.title"
      :size="drawer.size"
      :before-close="handleClose"
    >
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
        <el-button type="primary" @click="getDataList">搜索</el-button>
      </div>
      <GutuTable
        :dataList="dataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :collectionList="collectionList"
        :query="params"
        :handle="tableHandle"
        :tableHeight="200"
      >
        <template #bt_handle="{ data }">
          <div class="bt_handle">
            <el-button
              v-if="data.row.permissionLevel == 2"
              type="warning"
              @click="handleAuth(data.row, 3)"
              icon="el-icon-delete"
            >
              取消添加
            </el-button>
            <el-button
              v-else
              type="primary"
              @click="handleAuth(data.row, 2)"
              icon="el-icon-plus"
            >
              添加权限
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
    </el-drawer>
  </div>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import { approvalAuthList, approvalAuthAssign } from '@/api/system/approvalAuth'
export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '新增用户权限',
        size: '40%',
      },
      queryParams: {
        deptName: null,
        account: null,
        userName: null,
        searchType: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      loading: false,
      dataList: [],
      total: 0,
      columnConfig: [
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
      collectionList: {},
      tableHandle: {
        fixed: 'right',
        width: '160px',
        label: '操作',
        align: 'left',
        slot: true,
      },
    }
  },
  watch: {
    queryParams: {
      handler() {
        this.getDataList()
      },
      deep: true,
    },
  },
  mounted() {},
  methods: {
    getDataList() {
      this.loading = true
      approvalAuthList({ ...this.queryParams, ...this.params }).then((res) => {
        if (res.code == 200) {
          this.dataList = res.data.list
          this.total = Number(res.data.total)
        }
        this.loading = false
      })
    },
    handleAuth(data, status) {
      let title = status == 2 ? '添加审批权限' : '取消添加审批权限'
      this.$confirm(`确定给用户 【${data.userName}】 ${title}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let params = { ...data, permissionLevel: status }
          approvalAuthAssign(params).then((res) => {
            if (res.code === 200) {
              this.$message.success('权限分配成功')
              this.resetParams()
              this.getDataList()
              this.$emit('refresh')
            }
          })
        })
        .catch(() => {})
    },
    resetParams() {
      this.dataList = []
      this.total = 0
      this.queryParams = this.$options.data().queryParams
      this.params = this.$options.data().params
      if (this.$refs.gutuPaginationRef) this.$refs.gutuPaginationRef.reset()
    },
    updateParams(data) {
      this.params.pageNum = data.pageNum
    },
    handleOpen() {
      this.resetParams()
      this.getDataList()
      this.drawer.visible = true
    },
    handleClose() {
      this.drawer.visible = false
    },
  },
}
</script>
<style lang="less" scoped></style>
