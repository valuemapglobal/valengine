<!--
 * @Date: 2022-10-12 15:20:40
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-12-14 15:55:07
 * @name:
 * @FilePath: /vmkj/src/components/pageSearch.vue
-->
<template>
  <div class="box">
    <ul class="search">
      <li v-for="(item, index) in list" :key="index" :class="{ dark: dark }">
        <el-input
          v-if="item.type === 'input'"
          v-model="formData[item.prop.key]"
          :placeholder="item.placeholder"
          :clearable="item.clearable != null ? item.clearable : true"
          @change="
            ($event) => {
              if (item.change) {
                handleChange($event, item)
              }
            }
          "
        ></el-input>
        <el-select
          :class="item.placeholder.indexOf('时间') !== -1 ? 'date-select' : ''"
          v-else-if="item.type === 'select'"
          :popperAppendToBody="false"
          v-model="formData[item.prop.key]"
          :placeholder="item.placeholder"
          :clearable="item.clearable != null ? item.clearable : true"
          @change="
            ($event) => {
              if (item.change) {
                handleChange($event, item)
              }
            }
          "
        >
          <el-option
            v-for="val in item.options"
            :key="val.value || val"
            :label="val.label || val"
            :value="val.value || val"
          >
          </el-option>
        </el-select>
        <el-date-picker
          :clearable="false"
          v-else-if="item.type === 'time'"
          v-model="formData[item.prop.key]"
          :placeholder="item.placeholder"
          type="daterange"
          range-separator="-"
          :value-format="'yyyy-MM-dd'"
          :start-placeholder="$t('common.createTimeStart')"
          :end-placeholder="$t('common.createTimeEnd')"
          @change="
            ($event) => {
              if (item.change) {
                handleChange($event, item)
              }
            }
          "
        >
        </el-date-picker>
        <el-date-picker
          class="weekTimePicker"
          v-else-if="item.type === 'weekTime'"
          v-model="weekRange"
          type="week"
          :format="selectWeekTime"
          :placeholder="$t('common.selectWeek')"
          :picker-options="pickerOptions"
          @change="
            ($event) => {
              onWeekChange($event, item)
            }
          "
        />
      </li>
    </ul>
    <el-button type="primary" @click="search" v-if="onlySearch">{{
      $t('common.search')
    }}</el-button>
    <el-button type="primary" v-else @click="reset">{{
      $t('common.reset')
    }}</el-button>
  </div>
</template>

<script>
export default {
  name: 'search',
  props: {
    list: {
      type: Array,
      default: () => [],
    },
    listInit: {
      type: Boolean,
      default: true,
    },
    dark: {
      type: Boolean,
      default: false,
    },
    //是否只展示搜索按钮
    onlySearch: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      formData: {},
      formDataCopy: {},
      weekRange: null,
      selectWeekTime: '',
      pickerOptions: {
        firstDayOfWeek: 1,
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
      },
    }
  },
  computed: {
    setKey: {
      get() {
        return (val) => {
          return
        }
      },
    },
  },
  watch: {
    list: {
      handler(val) {
        if (!this.onlySearch) return
        val.forEach((item) => {
          if (item.prop.value) {
            this.$set(this.formData, item.prop.key, item.prop.value)
          } else {
            this.$set(this.formData, item.prop.key, null)
          }
        })
        // this.search();
      },
      // immediate: true,
      deep: true,
    },
    formData: {
      handler(val) {
        if (this.onlySearch) return
        if (!Object.keys(this.formDataCopy).length) {
          this.formDataCopy = JSON.parse(JSON.stringify(this.formData))
        }

        this.search()
      },
      // immediate: true,
      deep: true,
    },
  },
  mounted() {
    if (this.onlySearch || !this.listInit) return
    this.setList()
  },
  methods: {
    reset() {
      // this.$set(this,"formData",{...JSON.parse(JSON.stringify(this.formDataCopy))})
      this.formData = JSON.parse(JSON.stringify(this.formDataCopy))
    },
    setList() {
      this.list.forEach((item) => {
        if (item.prop.value) {
          this.$set(this.formData, item.prop.key, item.prop.value)
        } else {
          this.$set(this.formData, item.prop.key, null)
        }
      })
    },
    search() {
      for (let key in this.formData) {
        if (!this.formData[key]) this.formData[key] = null
      }
      this.$emit('search', this.formData)
    },
    handleChange(event, val) {
      val.prop.value = event
      this.$emit('change', val)
    },
    getWeekStartAndEnd(date) {
      const currentDate = new Date(date)
      const day = currentDate.getDay() || 7
      const startDate = new Date(currentDate)
      const endDate = new Date(currentDate)
      const nowDate = new Date()

      // 获取周的起始日期（星期一）
      startDate.setDate(currentDate.getDate() - day + 1)
      startDate.setHours(0, 0, 0, 0) // 设置为当天的00:00:00

      // 获取周的结束日期（星期日）
      endDate.setDate(currentDate.getDate() + (7 - day))
      endDate.setHours(23, 59, 59, 999)
      nowDate.setHours(23, 59, 59, 999)

      // 返回结果格式化为 "YYYY-MM-DD"
      return {
        startDate: this.formatDate(startDate),
        endDate: this.formatDate(nowDate < endDate ? nowDate : endDate),
      }
    },

    // 格式化日期为 "YYYY-MM-DD"
    formatDate(date) {
      const year = date.getFullYear()
      const month = ('0' + (date.getMonth() + 1)).slice(-2) // 月份从0开始，所以加1
      const day = ('0' + date.getDate()).slice(-2)
      return `${year}-${month}-${day}`
    },

    // 当选择的周改变时触发
    onWeekChange(value, item) {
      // 获取起止日期
      const { startDate, endDate } = this.getWeekStartAndEnd(value)
      this.formData[item.prop.key] = [startDate, endDate]
      this.selectWeekTime = `${startDate}~${endDate}`
    },
  },
}
</script>

<style lang="less" scoped>
.box {
  display: flex;

  .el-button {
    padding: 0 20px;
    height: 42px;
    font-size: 14px;
    background: var(--primary-color);
    border-radius: 6px;
  }
}

.search {
  display: flex;

  > li {
    height: 42px;
    border-radius: 6px;
    opacity: 1;
    margin-right: 10px;

    :deep .el-select {
      width: 100%;
    }

    .el-input__inner {
      height: 42px;
      background: var(--bg-color);
      border-radius: 6px;

      &::placeholder {
        color: var(--text-color-tertiary);
      }
    }
  }

  > li:has(.date-select) {
    width: 240px;
  }
  > li:has(.el-date-editor) {
    width: 300px !important;
  }

  .dark {
    :deep .el-input__inner {
      background-color: rgba(#000, 0.04);
    }
  }
  ::v-deep .weekTimePicker {
    width: 240px;
    .el-input__inner {
      padding-left: 20px;
    }
    .el-input__prefix {
      display: none;
    }
  }
  :deep .el-date-editor {
    width: 300px !important;
    .el-range-separator {
      display: flex;
      align-items: center;
    }
  }
}
</style>
