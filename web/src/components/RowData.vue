<template>
  <div class="root-container" :class="`rowData_${sortOrder}`">
    <div
      class="info-container"
      v-for="(infoItem, infoIndex) in configList"
      :key="infoIndex"
      :style="{ width: handleInfoWidth }"
    >
      <div class="info-title" v-if="infoItem.title.length">
        <template v-for="(titleItem, titleIndex) in infoItem.title">
          <div :key="titleIndex">
            <span v-if="titleItem.label">
              {{ titleItem.field ? `${titleItem.label}：` : titleItem.label }}
            </span>
            <div v-if="titleItem.field">
              {{
                fromValueToContents(
                  data[titleItem.field],
                  titleItem.type,
                  titleItem.multiply,
                  titleItem.subfield
                )
              }}
              <span v-if="data[titleItem.field]">{{ titleItem.unit }}</span>
            </div>
          </div>
        </template>
      </div>
      <div class="info-row">
        <template v-for="(rowItem, rowIndex) in handleRow(infoItem)">
          <div
            class="info-cell"
            :style="{ width: rowItem.width }"
            :class="{
              'mt-10': rowIndex + 1 > infoItem.row,
              'items-end': rowItem.breakSpaces,
              'flex-col': dataSortOrder == 'column',
              centerValue: isCenter,
            }"
            :key="rowIndex"
          >
            <span
              v-if="rowItem.label"
              class="label"
              :style="{ fontSize: labelSize + 'px' }"
              :class="{ 'whitespace-break-spaces': rowItem.breakSpaces }"
            >
              {{ rowItem.label }}{{ dataSortOrder == 'row' ? '：' : '' }}
            </span>

            <div
              v-if="rowItem.field || rowItem.type === 'slot'"
              class="info-value"
              :class="{
                ellipsis: !rowItem.unEllipsis,
              }"
            >
              <template v-if="!['slot'].includes(rowItem.type)">
                <span
                  :class="{
                    columnValue: dataSortOrder == 'column',
                  }"
                  :style="{ fontSize: valueSize + 'px' }"
                  >{{
                    fromValueToContents(
                      data[rowItem.field],
                      rowItem.type,
                      rowItem.multiply,
                      rowItem.subfield
                    )
                  }}</span
                >
              </template>
              <template v-else>
                <slot v-if="rowItem?.slot" :name="rowItem.slot"></slot>
                <slot v-else></slot>
              </template>
              <span v-if="data[rowItem.field]">{{ rowItem.unit }}</span>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { handleReplace, parseTime } from '@/utils/rouyi'
export default {
  name: 'InfoCard',
  props: {
    data: {
      type: Object,
      default: () => ({}),
    },
    configList: {
      type: Array,
      default: () => [],
    },
    hasDivision: {
      type: Boolean,
      default: false,
    },
    sortOrder: {
      type: String,
      default: 'row',
    },
    dataSortOrder: {
      type: String,
      default: 'row',
    },
    isCenter: {
      type: Boolean,
      default: false,
    },
    showVip: {
      type: Boolean,
      default: false,
    },
    labelSize: {
      type: Number,
      default: 14,
    },
    valueSize: {
      type: Number,
      default: 16,
    },
  },
  computed: {
    handleInfoWidth() {
      if (this.sortOrder === 'column') return '100%'
      let length = this.configList.length
      if (this.hasDivision) {
        return `calc((100% - ${(length - 1) * 40}px)/${length})`
      } else {
        return `calc(100% / ${length})`
      }
    },
  },
  methods: {
    handleRow(data) {
      let { row, fieldList } = data
      for (let i = 0; i < fieldList.length; i += row) {
        let list = fieldList.slice(i, i + row)
        let hasWidth = list
          .filter((item) => item.hasOwnProperty('width') && item.width != null)
          .map((item) => item.width)
        let noWidth = list.filter((item) => !item.hasOwnProperty('width'))
        let widthCount = hasWidth.length ? ' - ' + hasWidth.join(' - ') : ''
        let width = `calc((100%${widthCount})/${
          hasWidth.length ? noWidth.length : row
        })`
        noWidth.forEach((item) => (item.width = width))
      }
      return fieldList
    },
    setText(type, value) {
      switch (type) {
        case 'rate':
          return value ? String(value).replace('-', '降低') : value
        case 'per':
          return value ? String(value).replace('-', '减少') : value
        default:
          return value
      }
    },
    setWitherPoint(value) {
      if (value && value.toString().indexOf('.') >= 0) {
        return value.toFixed(2)
      }
      return value
    },
    fromValueToContents(value, type, multiply = false, subfield) {
      switch (type) {
        case 'rate':
          return value
            ? this.setWitherPoint(multiply ? value * 100 : value)
            : '-'
        case 'money':
          return value ? this.formatMoney(value) : '-'
        case 'timeStamp':
          return value ? parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}') : '-'
        case 'replace':
          return value ? handleReplace(value, this.data) : '-'
        case 'object':
          return typeof value == 'object' && subfield ? value[subfield] : value
        default:
          return value || '-'
      }
    },
  },
}
</script>

<style scoped>
.root-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  font-weight: 500;
}
.info-container {
  display: flex;
  flex-direction: column;
}
.info-title {
  display: flex;
  font-weight: 500;
  font-size: 16px;
  color: var(--text-color-secondary);
  margin-bottom: 18px;
}
.info-row {
  display: flex;
  flex-wrap: wrap;
  font-size: 14px;
}
.info-cell {
  display: flex;
  padding-right: 4px;
  align-items: center;
}
.label {
  color: var(--text-color-tertiary);
  white-space: nowrap;
}
.info-value {
  color: var(--text-color-secondary);
  display: flex;
  align-items: center;
}
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rowData_row {
  justify-content: space-between;
}
.rowData_column {
  width: 100%;
  flex-direction: column;
}
.rowData_column > div {
  margin-bottom: 20px;
}
.rowData_column > div:last-of-type {
  margin-bottom: 0px;
}
.mt-10 {
  margin-top: 10px;
}
.flex-col {
  flex-direction: column;
}
.items-end {
  align-items: flex-end;
}
.columnValue {
  display: block;
  font-size: 16px;
  color: var(--text-color);
}
.centerValue {
  justify-content: center;
  align-items: center;
}
</style>
