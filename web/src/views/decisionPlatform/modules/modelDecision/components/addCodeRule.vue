<template>
  <div class="select_code">
    <div class="selectTable">
      <div class="table">
        <el-table
          v-loading="tableConfig.loading"
          :data="tableConfig.dataList"
          :header-row-style="tableHeaderColor"
          height="500px"
          v-adaptive="{ bottomOffset: tableConfig.tableBottom }"
          border
          :row-class-name="tableRowClassName"
          @selection-change="
            (rows) => {
              handleChange(rows, 'leftSelect')
            }
          "
        >
          <el-table-column type="selection" width="50" />
          <el-table-column
            v-for="(item, index) in tableConfig.list"
            :key="index"
            v-bind="item"
          />
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            @current-change="handleCurrentChange"
            @size-change="handleCurrenSizeChange"
            :current-page.sync="tableConfig.searchForm.pageNum"
            :page-size="tableConfig.searchForm.pageSize"
            layout="total,sizes,prev, pager, next, jumper"
            :total="tableConfig.totalNum"
          />
        </div>
      </div>

      <div class="btnList">
        <i
          class="el-icon-right"
          :class="{ highlight: leftSelect.length }"
          @click="toRightList"
        />
        <i
          class="el-icon-back"
          :class="{ highlight: rightSelect.length }"
          @click="removeRightList"
        />
      </div>
      <div class="table">
        <el-table
          v-loading="selectTableConfig.loading"
          :data="selectTableConfig.dataList"
          :header-row-style="tableHeaderColor"
          height="400px"
          v-adaptive="{ bottomOffset: selectTableConfig.tableBottom }"
          border
          :row-class-name="tableRowClassName"
          @selection-change="
            (rows) => {
              handleChange(rows, 'rightSelect')
            }
          "
        >
          <el-table-column type="selection" width="50" />
          <el-table-column
            v-for="(item, index) in selectTableConfig.list"
            :key="index"
            v-bind="item"
          />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'

export default {
  components: {
    Search,
  },
  props: {
    tableConfig: {
      type: Object,
      default: () => {},
    },
    selectTableConfig: {
      type: Object,
      default: () => {},
    },
  },
  mounted() {},
  data() {
    return {
      active: this,
      searchList: [
        {
          type: 'input',
          placeholder: '请输入code',
          prop: {
            key: 'code',
            value: null,
          },
        },
        {
          type: 'input',
          placeholder: '请输入决策说明',
          prop: {
            key: 'content',
            value: null,
          },
        },
      ],

      totalNum: 0,
      leftSelect: [],
      rightSelect: [],
    }
  },

  methods: {
    handleChange(rows, type) {
      this.active[type] = rows
    },
    toRightList() {
      let left = JSON.parse(JSON.stringify(this.leftSelect))
      let list = this.selectTableConfig.dataList
      if (left.length > 0) {
        list.forEach((item) => {
          let index = left.findIndex(
            (innerItem) =>
              innerItem.id === item.codeId || innerItem.id === item.id
          )
          if (index != -1) left.splice(index, 1)
        })
        if (left.length > 0) {
          this.selectTableConfig.dataList = list.concat(left)
        }
      }
    },
    removeRightList() {
      let right = this.rightSelect
      let list = this.selectTableConfig.dataList
      right.forEach((item) => {
        list.splice(
          list.findIndex(
            (innerItem) =>
              innerItem.id === item.codeId || innerItem.id === item.id
          ),
          1
        )
      })
    },
    handleCurrentChange(val) {
      this.$emit('turnPage', val)
    },
    handleCurrenSizeChange(val) {
      this.$emit('turnSize', val)
    },
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'tableColor'
      }
    },
  },
}
</script>

<style lang="less" scoped>
.select_code {
  box-sizing: border-box;

  //.operation {
  //  display: flex;
  //  justify-content: space-between;
  //  align-items: center;
  //  margin-bottom: 20px;
  //
  //  .search {
  //    display: flex;
  //    align-items: center;
  //
  //    .search_btn {
  //      height: 42px;
  //      color: #fff;
  //      border: none;
  //      background: var(--primary-color);
  //      margin-left: 10px;
  //    }
  //  }
  //}

  .selectTable {
    display: flex;
    justify-content: space-between;

    .table {
      //width: 48%;

      /deep/ .el-table {
        width: 100%;
        border-radius: 15px;
        margin-bottom: 0px;

        .el-table__header,
        .el-table__body {
          padding: 0px;
        }

        .el-table__cell {
          background-color: transparent;
        }

        .el-table__fixed-header-wrapper {
          padding: 0px;
        }

        .el-table__fixed-body-wrapper {
          padding: 0px;
        }

        .cell {
          font-size: 14px;
          font-family: PingFang SC;
          color: rgba(#000, 0.85);
        }

        .tableColor {
          background: linear-gradient(135deg, #f1f5ff 0%, #ecf0ff 100%);
        }

        .handle {
          .el-link {
            margin-right: 20px;
          }
        }
      }

      .pagination {
        display: flex;
        justify-content: flex-end;
        background-color: #fff;
        padding: 20px;
        border: none;

        /deep/ .el-pagination {
          display: flex;
          justify-content: center;

          .el-pager {
            padding: 0px;
          }

          .el-pagination__jump,
          .el-pagination__sizes,
          .el-pagination__total {
            padding: 0px;
          }
        }
      }
    }

    .btnList {
      margin: auto 20px;
      display: flex;
      flex-direction: column;

      > i {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #91c2ff;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        margin: 15px 0px;
        cursor: pointer;
      }

      .highlight {
        background: var(--primary-color);
      }
    }
  }
}
</style>
