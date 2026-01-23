<template>
  <div class="gutuPagination">
    <el-pagination
      ref="pagination"
      v-if="total > 0 && bPageShow"
      :total="total"
      background
      :current-page.sync="queryParams.pageNum"
      :layout="layout"
      :page-sizes="pageSizes"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script>
export default {
  name: 'gutuPagination',
  props: {
    total: {
      type: Number,
      default: 0,
    },
    layout: {
      type: String,
      default: 'total,sizes,prev, pager, next, jumper',
    },
    pageNum: {
      type: Number,
      default: 1,
    },
    pageSize: {
      type: Number,
      default: 10,
    },
    pageSizes: {
      type: Array,
      default: () => [10, 20, 30, 50, 100],
    },
  },
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      bPageShow: true, //解决页面刷新后页码没有改变问题
    }
  },
  watch: {},
  mounted() {
    this.queryParams = {
      pageNum: this.pageNum ? this.pageNum : 1,
      pageSize: this.pageSize ? this.pageSize : 10,
    }
  },
  methods: {
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.$emit('searchData', this.queryParams)
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.$emit('searchData', this.queryParams)
    },
    reset() {
      this.queryParams = {
        pageNum: this.pageNum ? this.pageNum : 1,
        pageSize: this.pageSize ? this.pageSize : 10,
      }
      this.bPageShow = false
      this.$nextTick(() => {
        this.bPageShow = true
      })
    },
  },
}
</script>

<style lang="less" scoped>
.gutuPagination {
  width: 100%;
  text-align: center;
  // ::v-deep .el-pagination {
  //   &.is-background .btn-next,
  //   &.is-background .btn-prev,
  //   &.is-background .el-pager li {
  //     background: var(--bg-color);
  //     border-radius: 6px;
  //     font-size: 14px;
  //     color: var(--text-color-tertiary);
  //     font-weight: 400;
  //     padding: 0 12px;
  //     height: 32px;
  //     line-height: 32px;
  //     // border: 1px solid var(--border-color);

  //     &.more {
  //       border: none;
  //       color: var(--text-color-tertiary);
  //       font-weight: 500;
  //       background: transparent;
  //     }
  //   }

  //   &.is-background .el-pager li:not(.disabled).active {
  //     background: var(--primary-color);
  //     color: var(--bg-color);
  //     border: 1px solid var(--primary-color);
  //   }

  //   .el-pagination__jump,
  //   .el-pagination__editor,
  //   .el-input__inner {
  //     height: 32px;
  //   }
  // }
}
</style>
