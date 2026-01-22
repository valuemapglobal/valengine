<template>
  <div class="accountResources">
    <div class="searchTop">
      <el-input
        v-model="queryParams.name"
        placeholder="请输入资源名称"
        clearable
      >
        <img src="@/assets/images/search_blue.png" slot="prefix" />
      </el-input>
      <el-select
        v-model="queryParams.type"
        placeholder="资源分类"
        clearable
        :popper-append-to-body="false"
      >
        <el-option
          v-for="(item, index) in collectionList.resourceTypeList"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
      <el-button class="resetBtn" type="primary" @click="reset">重置</el-button>
      <el-button class="addBtn" type="primary" @click="openView()"
        >添加资源</el-button
      >
    </div>
    <div class="tableWrapper">
      <div class="tableFilter flex">
        <div class="operation flex" @click="handleDelete">
          <img src="@/assets/images/accountResources/del.png" alt="" />
          <span>批量删除</span>
        </div>
        <div class="operation" @click="handleUpdate(null, 1)">
          <img src="@/assets/images/accountResources/up.png" alt="" />
          <span>批量上架</span>
        </div>
        <div class="operation" @click="handleUpdate(null, 0)">
          <img src="@/assets/images/accountResources/down.png" alt="" />
          <span>批量下架</span>
        </div>
      </div>
      <GutuTable
        :dataList="tableDataList"
        :loading="loading"
        :columnConfig="columnConfig"
        :handle="tableHandle"
        :collectionList="collectionList"
        :tableHeight="290"
        selection
        @handleSelection="handleSelection"
      >
        <template #bt_handle="{ data }">
          <el-button type="text" @click="openView(data.row)"> 详情 </el-button>
          <el-button
            v-if="data.row.status == 0"
            type="text"
            @click="handleUpdate(data.row, 1)"
          >
            上架
          </el-button>
          <el-button
            v-if="data.row.status == 1"
            type="text"
            @click="handleUpdate(data.row, 0)"
          >
            下架
          </el-button>
          <el-button type="text" @click="handleDelete(data.row)">
            删除
          </el-button>
        </template>
      </GutuTable>
      <div class="pagination">
        <GutuPagination
          ref="gutuPagination"
          :total="total"
          @searchData="updateParams"
        />
      </div>
    </div>
    <AddResources
      ref="addResourcesRef"
      :collectionList="collectionList"
      :resourcesInfo="currentRow"
      @success="getDataList"
    />
  </div>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import AddResources from './components/addResources.vue'
import { getDicts } from '@/api/index'
import { getUser } from '@/api/system/userManagement.js'
import { queryInterface } from '@/api/charging/entryCharge'
import {
  adminList,
  add,
  editsStatus,
  remove,
  getResourceType,
} from '@/api/system/resources'
import { del } from 'vue'
export default {
  components: {
    GutuTable,
    GutuPagination,
    AddResources,
  },
  data() {
    return {
      queryParams: {
        name: null,
        type: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      total: 10,
      loading: false,
      columnConfig: [
        {
          label: '资源名称',
          field: 'name',
          width: '',
          type: 'selection',
          align: 'left',
        },
        {
          label: '资源图片',
          field: 'icon',
          width: '150',
          align: 'left',
          type: 'image',
          imageStyle: {
            width: '30px',
            height: '30px',
          },
          imageList: [],
        },
        {
          label: '资源分类',
          field: 'type',
          width: '',
          align: 'left',
          type: 'tag',
          pairedList: 'resourceTypeList',
        },
        {
          label: '创建时间',
          field: 'createTime',
          width: '250',
          align: 'left',
          type: 'timeStamp',
        },
        {
          label: '资源状态',
          field: 'status',
          width: '200',
          align: 'left',
          type: 'tag',
          pairedList: 'statusList',
          styleList: 'statusStyle',
        },
      ],
      collectionList: {
        resourceTypeList: [],
        reportTypeList: [],
        userTypeList: [
          { label: '企业', value: '1' },
          { label: '个人', value: '2' },
        ],
        roleList: [],
        statusList: [
          { label: '待上架', value: 0 },
          { label: '已上架', value: 1 },
        ],
        statusStyle: new Map([
          [0, { color: '#FF8F1F ', bgColor: true }],
          [1, { color: '#3662EC ', bgColor: true }],
        ]),
      },
      tableHandle: {
        fixed: 'right',
        width: '300px',
        label: '操作',
        align: 'left',
        slot: true,
      },
      tableDataList: [],
      selectData: [],
      currentRow: null,
    }
  },
  watch: {
    queryParams: {
      handler() {
        this.resetParams()
        this.getDataList()
      },
      deep: true,
    },
  },
  mounted() {
    this.getDataList()
    this.init()
  },
  methods: {
    init() {
      getDicts('system_resources_type')
        .then((res) => {
          if (res.code == 200) {
            let data = res.data.map((item) => {
              return {
                label: item.dictLabel,
                value: item.dictValue,
              }
            })
            this.collectionList.resourceTypeList = data
          }
        })
        .catch((err) => {})
      getDicts('system_resources_report_type')
        .then((res) => {
          if (res.code == 200) {
            let data = res.data.map((item) => {
              return {
                label: item.dictLabel,
                value: item.dictValue,
              }
            })
            this.collectionList.reportTypeList = data
          }
        })
        .catch((err) => {})

      getUser().then((res) => {
        this.collectionList.roleList = res.roles
      })
      queryInterface({ pageNum: 1, pageSize: 99 })
        .then((res) => {
          this.collectionList.interFaceList = res.data.list
        })
        .catch((err) => {})
    },
    getDataList() {
      let params = { ...this.queryParams, ...this.params }
      this.loading = true
      adminList(params)
        .then((res) => {
          if (res.code == 200) {
            let data = res.data.list
            let imageList = (this.columnConfig.find(
              (item) => item.field == 'icon'
            ).imageList = [])
            data.forEach((item) => {
              imageList.push(item.icon)
            })

            this.tableDataList = data
            this.total = res.data.total
          }
          this.loading = false
        })
        .catch((err) => {
          this.loading = false
        })
    },
    handleSelection(data) {
      this.selectData = [...data]
    },
    openView(data) {
      this.currentRow = { ...data }
      if (this.$refs.addResourcesRef) {
        this.$refs.addResourcesRef.openDrawer()
      }
    },
    handleUpdate(data, status) {
      if (!data && !this.selectData.length) {
        this.$message.warning(`请先选择需要${status ? '上架' : '下架'}的资源`)
        return
      }
      this.$confirm(`是否${status ? '上架' : '下架'}选中的资源`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let resourceIdList = data
            ? [data.id]
            : this.selectData.map((item) => item.id)
          editsStatus({
            resourceIdList: resourceIdList,
            status: status,
          })
            .then((res) => {
              this.selectData = []
              this.$message.success('操作成功')
              this.getDataList()
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    handleDelete(data) {
      if (!data && !this.selectData.length) {
        this.$message.warning('请先选择需要删除的资源')
        return
      }
      this.$confirm('是否删除选中的资源', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let resourceIdList = data
            ? [data.id]
            : this.selectData.map((item) => item.id)
          remove({
            resourceIdList: resourceIdList,
          })
            .then((res) => {
              if (res.code == 200) {
                this.selectData = []
                this.$message.success('操作成功')
                this.getDataList()
              }
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    resetParams() {
      this.params = this.$options.data().params
      this.$refs.gutuPagination.reset()
    },
    updateParams(data) {
      this.params = { ...data }
      this.getDataList()
    },
    reset() {
      this.resetParams()
      this.queryParams = this.$options.data().queryParams
    },
  },
}
</script>

<style lang="less" scoped>
.accountResources {
  width: 100%;
  height: calc(var(--bgvh) - 42px);
  padding: 20px;
  font-family: PingFang SC-Regular;
  ::v-deep(.searchTop) {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .el-input {
      width: 260px;
      height: 48px;
      margin-right: 10px;
      .el-input__inner {
        height: 48px;
      }
      .el-input__prefix {
        line-height: 48px;
      }
    }
    .el-select {
      > .el-input {
        width: 140px;
        .el-input__inner {
          height: 48px;
        }
      }
    }

    .resetBtn,
    .addBtn {
      height: 48px;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 16px;
      border-radius: 6px;
    }
    .resetBtn {
      width: 72px;
      background-color: var(--primary-color);
      color: #fff;
    }
    .addBtn {
      width: 96px;
      color: #fff;
      background-color: var(--primary-color);
    }
  }
  .tableWrapper {
    border-radius: 12px;
    background: #ffffff;
    overflow-y: auto;
    padding: 20px;

    .tableFilter {
      margin-bottom: 20px;
      cursor: pointer;
      .operation {
        margin-right: 30px;
        > img {
          width: 16px;
          height: 16px;
          margin-right: 6px;
        }
        > span {
          color: rgba(#000, 0.85);
          font-size: 14px;
        }
      }
    }

    ::v-deep(.el-table) {
      .el-table__header > thead {
        font-size: 16px;
        font-family: PingFang SC-Regular;
        font-weight: 500;
      }
      .el-table__header > thead th {
        background-color: rgba(#0256ff, 0.04);
      }
      .cell {
        font-size: 16px;
        font-weight: 400;
      }
      .el-button {
        font-size: 16px;
      }
    }

    .pagination {
      margin-top: 20px;
    }
  }
}
.flex {
  display: flex;
  align-items: center;
}
</style>
