<template>
  <div id="filterTable">
    <el-table
      :data="tableData"
      v-bind="tableConfig"
      v-adaptive="tableConfig.adaptive"
    >
      <el-table-column
        v-for="(item, index) in tableInfo.tableColumn"
        :key="index"
        :label="item.label"
        :prop="item.prop"
        v-bind="item.config"
      >
        <template v-slot:header>
          <div class="tableHeader">
            <span>{{item.label}}</span>
            <img
              v-if="tableConfig.hasFilter && item.filter"
              @mousedown="(event)=>{openFilter(event,item)}"
              :src="handlerImg(item)"
            >
          </div>
        </template>
      </el-table-column>
    </el-table>
    <fliterPop
      ref="fliterPop"
      :show.sync='showFilter'
      :position='popPosition'
      :dataList='filterData'
      :rowHeight='38'
      @clickSubmit='clickSubmit'
      @sortFun='handleSort'
      @delParams='handlerDelParams'
      @remarkData='handlerRemark'
    ></fliterPop>
  </div>
</template>

<script>
import FliterPop from '@/components/filterTable/fliterPop'
export default {
  name: 'filterTable',
  components: { FliterPop },
  props: {
    tableInfo: {
      type: Object,
      defalut: () => { }
    },
    tableData: {
      type: Array,
      defalut: () => []
    },
    filterData: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      tableConfig: {
        border: true,
        filter: true
      },
      showFilter: false,
      popPosition: null,
      currentRow: {},
      selectRow: []
    }
  },
  watch: {
    tableInfo: {
      handler(val) {
        if (val.tableConfig) {
          this.tableConfig = val.tableConfig
        }
      }, deep: true, immediate: true
    },
  },
  mounted() { },
  methods: {
    openFilter(event, data) {
      let selectedRow = this.$refs.fliterPop.selectForm
      let status = this.$refs.fliterPop.selectMap
      this.currentRow = JSON.parse(JSON.stringify(data))
      this.popPosition = { event: event, data: data }
      this.showFilter = true
      this.$emit('filterOpenClick', { currentRow: data, selectedRow: selectedRow, status: status })
    },
    handleSort(data) {
      this.currentRow.sort = data.currentRow.sort
      this.$emit('filterOpenClick', data)
    },
    handlerRemark(data) {
      this.$emit('filterOpenClick', data)
    },
    handlerDelParams(data) {
      this.handleSelectRow(data)
      this.$emit('filterClickSubmit', data)
    },

    clickSubmit(data) {
      this.handleSelectRow(data)
      this.$emit('filterClickSubmit', data)
    },

    handleSelectRow(data) {
      let params = data.selectedRow
      this.selectRow = []
      for (let item in params) {
        if (params[item].length > 0) {
          this.selectRow.push(item)
        }
      }
    },

    handlerImg(data) {
      if (data.hasOwnProperty('sort') && data.sort != null) {
        if (data.sort == 0) {
          return require('@/assets/images/components/asc.png')
        } else {
          return require('@/assets/images/components/desc.png')
        }
      } else if (this.selectRow.includes(data.prop)) {
        return require('@/assets/images/components/filter.png')
      } else {
        return require('@/assets/images/filter.png')
      }
    }
  }
}
</script>

<style lang="less" scoped>
#filterTable {
  .tableHeader {
    display: flex;
    align-items: center;
    > img {
      width: 10px;
      height: 10px;
      cursor: pointer;
      margin-left: 5px;
    }
  }
  .filterPop {
    width: 320px;
    height: 390px;
    background-color: red;
    position: absolute;
    top: 0;
    left: 0;
  }
}
</style>
