<template>
  <div>
    <el-table
      :data="tableData"
      :border="border"
      :header-row-style="{
        backgroundColor: 'rgba(230,238,252,0.3)',
      }"
      :height="
        !fixedHeight
          ? tableHeight
            ? `calc(var(--bgvh) - ${tableHeight}px)`
            : null
          : tableHeight
      "
      v-loading="loading"
    >
      <el-table-column
        v-for="(i, index) in tableColumn"
        :key="index"
        :label="i.label"
        :prop="i.prop"
        :width="i.width || 'auto'"
        :min-width="i.minWidth || 'auto'"
        :fixed="i.fixed || false"
        :align="i.align || 'left'"
      >
        <template slot-scope="{ row }">
          <div v-if="i?.showOverflowTooltip" class="show-overflow-tooltip">
            <el-popover
              trigger="hover"
              placement="bottom"
              :title="i.label"
              :disabled="
                (row[i.prop]?.length ?? 0) < (i?.textOverflowLength ?? 0)
              "
              :popper-options="{
                gpuAcceleration: true,
              }"
              :append-to-body="false"
              width="380"
            >
              <div>
                {{ row[i.prop] }}
              </div>
              <div
                slot="reference"
                style="
                  display: -webkit-box;
                  -webkit-line-clamp: 3;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  -webkit-box-orient: vertical;
                "
              >
                {{ row[i.prop] }}
              </div>
            </el-popover>
          </div>
          <div v-else-if="i.type == 'money'">
            {{ formatMoney(row[i.prop]) }}
          </div>
          <div v-else>{{ row[i.prop] }}</div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    tableData: {
      typeof: Array,
      default: () => {
        return []
      },
    },
    metaModel: {
      typeof: [String, null],
      default: null,
    },
    loading: {
      typeof: Boolean,
      default: false,
    },
    border: {
      typeof: Boolean,
      default: false,
    },
    fixedHeight: {
      type: Boolean,
      default: false,
    },
    //表格适配高度
    tableHeight: {
      type: Number || Object,
      default: null,
    },
  },
  data() {
    return {
      tableColumn: [],
    }
  },
  computed: {
    ...mapGetters(['AERTTL']),
  },
  watch: {
    metaModel: {
      handler(val) {
        if (val) {
          this.initTableColumns()
        }
      },
      immediate: true,
    },
  },
  methods: {
    initTableColumns() {
      const filterTabelCols = this.AERTTL.filter(
        ({ metaModel }) => metaModel == this.metaModel
      ).sort((a, b) => a.sort - b.sort)

      this.tableColumn = filterTabelCols.map(
        ({ info: { title: label, fieldName: prop, type }, style }) => {
          const result = {
            label,
            prop,
            type,
          }

          const patternsToCheck = ['金额', '数额']
          const regex = new RegExp(patternsToCheck.join('|'))
          if (regex.test(label)) {
            result.type = 'money'
          }

          if (
            style &&
            Object.prototype.toString.call(style) == '[object Object]'
          ) {
            for (const key in style) {
              result[key] = style[key]
            }
          }
          return result
        }
      )
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep .el-table {
  .el-table__header-wrapper {
    thead,
    tr,
    th,
    .cell {
      background: rgb(248, 250, 255);
      font-size: 16px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
    }
  }

  tbody {
    td {
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
    }

    .el-popover {
      width: 380px;

      .popover-content {
        position: relative;
        max-width: unset;
        border: none;
      }
    }
  }
}
</style>
