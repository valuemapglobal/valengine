<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
    :destroy-on-close="true"
    :show-close="true"
  >
    <!--<div class="detail_row">
       <div class="detail_row-item" v-for="item in dataList" :key="item.label">
        <span class="label">{{ item.label }}：</span>
        <span class="value">{{ item.value }}</span>
      </div> 
    </div>-->
    <GutuTable
      :dataList="dataList"
      :columnConfig="columnConfig"
      :collectionList="collectionList"
    />
  </el-drawer>
</template>
<script>
import { riskTypeDetail } from '../../api'
import GutuTable from '@/components/gutu/gutuTable.vue'
export default {
  components: { GutuTable },
  data() {
    return {
      drawer: {
        visible: false,
        title: '风险类别',
        width: '40%',
      },
      loading: false,
      dataList: [],
      columnConfig: [],
      collectionList: {},
      id: null,
    }
  },
  methods: {
    getDataList() {
      this.loading = true
      riskTypeDetail(this.id)
        .then((res) => {
          if (res.code === 200) {
            let data = res.data.detailData
            this.dataList = data

            let config = res.data.riskTypeInfo.fieldConfig
            this.columnConfig = []
            if (config) {
              config.forEach((item) => {
                this.columnConfig.push({
                  label: item.displayName,
                  field: item.fieldName,
                  width: this.handleTableWidth(item.displayName),
                })
              })
            }
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    handleTableWidth(name) {
      let charsList = [
        { label: '任务号', width: '240px' },
        { label: '案号', width: '240px' },
        { label: '时间', width: '180px' },
      ]

      if (typeof name !== 'string') return 'auto'

      // 遍历chars数组，检查name是否包含当前label
      for (const char of charsList) {
        if (name.includes(char.label)) {
          return char.width
        }
      }

      // 未找到匹配项时返回默认值
      return 'auto'
    },
    handleOpen(data) {
      this.id = data.alertId
      this.getDataList()
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
    padding: 16px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 0px 20px 20px;
  }
}
.detail_row {
  display: flex;
  flex-wrap: wrap;
  // .detail_row-item {
  //   width: 100%;
  //   display: flex;
  //   // justify-content: space-between;
  //   align-items: center;
  //   span {
  //     font-size: 14px;
  //     color: #333;
  //   }
  // }
  .detail_row-item {
    width: calc(50% - 10px);
    display: flex;
    flex-direction: column;
    margin-bottom: 10px;
    justify-content: space-between;
    align-items: flex-start;
    .label {
      font-size: 14px;
      color: rgba(#333, 0.8);
    }
    .value {
      font-size: 14px;
      color: #333;
    }
  }
}
</style>
