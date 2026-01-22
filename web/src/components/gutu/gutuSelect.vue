<template>
  <div class="gutuSelect">
    <el-select
      ref="selectRef"
      v-bind="mergedConfig"
      v-model="value"
      :class="{
        'autowidth-select': adaptive,
        flatten: flatten,
      }"
    >
      <template slot="prefix" v-if="adaptive">
        {{
          (options.find((item) => item.value === value) || {}).label ||
          (options.find((item) => item.dictValue === value) || {}).dictLabel
        }}
      </template>
      <el-option
        v-for="(item, index) in options"
        :key="index"
        :label="item.label || item.dictLabel"
        :value="item.value || item.dictValue"
      />
    </el-select>
  </div>
</template>

<script>
export default {
  props: {
    options: {
      type: Array,
      default: () => [],
    },
    config: {
      type: Object,
      default: () => {},
    },
    //扁平化
    flatten: {
      type: Boolean,
      default: false,
    },
    data: {},
    /**
     * 自适应宽度
     * adaptiveWidth：默认为boolean
     * 也可以接受对象
     * 参数：min-width、max-width、enable：是否启用
     */
    adaptiveWidth: {
      type: [Object, Boolean],
      default: false,
    },
  },
  computed: {
    mergedConfig() {
      // 合并传入的 config 和默认配置
      return { ...this.defaultConfig, ...this.config }
    },
    adaptive() {
      if (typeof this.adaptiveWidth == 'boolean') return this.adaptiveWidth
      else return this.adaptiveWidth.enable
    },
  },
  data() {
    return {
      value: null,
      defaultConfig: {
        clearable: true,
        placeholder: '请选择',
      },
      adaptiveConfig: {
        minWidth: '90px',
        maxWidth: 'auto',
      },
    }
  },
  watch: {
    value: {
      handler(val) {
        this.$emit('update:data', val)
      },
    },
    data: {
      handler(val) {
        this.value = val
      },
    },
  },
  mounted() {
    this.setElSelect()
  },
  methods: {
    setElSelect() {
      if (
        (typeof this.adaptiveWidth == 'boolean' && !this.adaptiveWidth) ||
        (typeof this.adaptiveWidth == 'object' && !this.adaptiveWidth.enable)
      )
        return
      if (this.$refs.selectRef) {
        let object = { ...this.adaptiveConfig, ...this.adaptiveWidth }
        const inputElement = this.$refs.selectRef.$el
        inputElement.style.minWidth = object.minWidth
        inputElement.style.maxWidth = object.maxWidth
      }
    },
  },
}
</script>

<style lang="less" scoped>
.gutuSelect {
  display: flex;
  // ::v-deep.el-select {
  //   .el-input__inner {
  //     height: 42px;
  //   }
  // }
}
.flatten {
  ::v-deep .el-input {
    .el-input__inner {
      padding-left: 0;
      border: none;
      background-color: transparent;
      &::placeholder {
        color: var(--text-color-secondary);
      }
    }
  }
}
.autowidth-select {
  ::v-deep .el-input {
    .el-input__inner {
      height: 100% !important;
    }
    .el-input__prefix {
      position: relative !important;
      left: 0;
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      padding: 0 15px;
      height: 32px;
      // line-height: 32px;
      visibility: hidden;
      display: flex;
      flex-wrap: nowrap;
    }
    input {
      position: absolute;
      padding-left: 15px;
    }
  }
}
</style>
