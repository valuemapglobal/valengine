<!--
 * @Author: detectiveBoy
 * @Date: 2022-11-09 16:50:04
 * @LastEditTime: 2025-06-24 11:07:53
 * @email: 2913379173@qq.com
-->
<template>
  <div
    class="custom-slider-filter"
    :class="[
      smooth ? 'custom-slider-filter-smooth' : '',
      className ? className : '',
      disabled ? 'custom-slider-filter-disabled' : '',
      type,
    ]"
  >
    <ul>
      <template v-for="(i, index) in options">
        <li
          v-if="i"
          :key="index"
          @click="handleClick(i.value)"
          :ref="`custom-slider-filter-item-${index}`"
          :class="[i.value === value ? 'active' : '']"
        >
          {{ i.label }}
        </li>
      </template>
      <div
        class="slider"
        :style="{ width: sliderWidth + 'px', left: left + 'px' }"
      ></div>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'SliderFilter',
  props: {
    value: {
      type: [String, Number],
    },
    options: {
      type: Array,
      required: true,
      default: () => [{ label: '全部', value: 0 }],
    },
    smooth: {
      type: Boolean,
      default: false,
    },
    className: {
      type: String,
      default: '',
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    type: {
      type: String,
      default: 'square', //circular
    },
  },
  model: {
    prop: 'value',
    event: 'change',
  },
  data() {
    return {
      sliderWidth: 0,
      left: 5,
    }
  },
  watch: {
    value: {
      handler(val) {
        const index = this.options.findIndex((i) => i.value === val)
        if (index === -1) return
        this.$nextTick(() => {
          if (window.innerWidth < 1600) {
            this.sliderWidth = this.getWidth(index) + 15
          } else {
            this.sliderWidth = this.getWidth(index)
          }
          let left = 5
          if (index > 0) {
            for (let i = index - 1; i >= 0; i--) {
              if (window.innerWidth < 1600) {
                left += this.getWidth(i) + 16
              } else {
                left += this.getWidth(i)
              }
            }
          }
          this.left = left
        })
      },
      immediate: true,
    },
  },
  methods: {
    getWidth(index) {
      const liEl = this.$refs[`custom-slider-filter-item-${index}`][0]
      return liEl?.getBoundingClientRect()?.width ?? 0
    },
    handleClick(value) {
      if (value === this.value || this.disabled) return
      this.$emit('change', value)
    },
  },
}
</script>

<style lang="less" scoped>
.circular {
  height: 48px;

  > ul {
    border-radius: 6px !important;
    height: 48px !important;
    display: flex;
    align-items: center;

    > div {
      border-radius: 6px !important;
    }
  }
}

.custom-slider-filter {
  display: inline-block;

  ul {
    display: flex;
    position: relative;
    padding: 5px;
    background: var(--bg-color-lighter);
    border-radius: 6px;
    box-sizing: border-box;
    li {
      padding: 6px 12px;
      text-align: center;
      font-size: 14px;
      font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      color: var(--text-color-secondary);
      cursor: pointer;
      z-index: 2;
      transition: color 0.2s linear;
    }

    .active {
      color: #fff;
    }

    .slider {
      z-index: 1;
      position: absolute;
      top: 5px;
      height: calc(100% - 10px);
      background: var(--primary-color);
      box-shadow: 0px 4px 10px 0px rgba(40, 136, 232, 0.1);
      border-radius: 6px;
      transition: all 0.2s linear;
    }
  }
}

.custom-slider-filter-smooth {
  ul {
    border-radius: 25.5px;

    li {
      border-radius: 20.5px;
    }

    .slider {
      border-radius: 20.5px;
    }
  }
}

.custom-slider-filter-disabled {
  ul {
    li {
      cursor: not-allowed;
    }
  }
}
</style>
