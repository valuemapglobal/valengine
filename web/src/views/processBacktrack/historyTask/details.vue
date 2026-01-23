<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
    :destroy-on-close="true"
    :show-close="true"
  >
    <GutuTable
      :loading="loading"
      :dataList="dataList"
      :columnConfig="columnConfig"
      :collectionList="collectionList"
      :border="false"
    >
    </GutuTable>
  </el-drawer>
</template>
<script>
import GutuTable from '@/components/gutu/gutuTable.vue'
import GutuPagination from '@/components/gutu/gutuPagination.vue'
export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '流程入参详情',
        width: '50%',
      },
      loading: false,
      dataList: [],
      columnConfig: [
        {
          label: '参数名称',
          field: 'name',
          width: '160',
        },
        {
          label: '参数说明',
          field: 'nameZh',
          width: '160',
        },
        {
          label: '参数类型',
          field: 'typeName',
          width: '160',
        },
        {
          label: '是否必填',
          field: 'isRequired',
          width: '160',
          type: 'tag',
          pairedList: 'requiredOptions',
        },
        {
          label: '参数值',
          field: 'value',
          width: '',
        },
      ],
      collectionList: {
        requiredOptions: [
          {
            label: '是',
            value: true,
          },
          {
            label: '否',
            value: false,
          },
        ],
      },
      tableHandle: {},
      total: 0,
      params: {
        pageSize: 10,
        pageNum: 1,
      },
    }
  },
  methods: {
    handleOpen(data) {
      this.dataList = []
      if (data.processEntry && data.processEntry.length > 0) {
        this.dataList = data.processEntry
      }
      this.drawer.visible = true
    },
    handleClose() {
      this.drawer.visible = false
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-drawer {
  font-family: PingFang SC-Medium;
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 0px 20px 20px;
  }
}
.pagination {
  width: 100%;
  margin: 16px 0 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .pagination-total {
    white-space: nowrap;
    font-size: 14px;
    color: #999;
    span {
      color: #333;
      font-weight: 500;
    }
  }
  ::v-deep .gutuPagination {
    width: auto;
  }
}
</style>
