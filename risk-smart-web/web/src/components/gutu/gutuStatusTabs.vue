<template>
  <div class="status-tab">
    <div
      class="status-tab-item"
      :class="{ 'status-tab-active': index === active }"
      v-for="(item, index) in list"
      :key="index"
    >
      <div class="status-tab-item-label" @click="handlerClick(item, index)">
        {{ item.label ? item.label : item.dictLabel }}
      </div>
      <div class="gray" v-if="index < list.length - 1">}</div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      active: 0,
    }
  },
  methods: {
    handlerClick(data, index) {
      this.active = index
      this.$emit('change', { ...data, index: index })
    },
    reset() {
      this.active = 0
    },
  },
}
</script>

<style lang="less" scoped>
.status-tab {
  display: flex;
  align-items: center;
  border-radius: 4px;
  background: #ffffff;

  &-item {
    display: flex;
    align-items: center;
    font-size: 14px;
    font-weight: 400;
    color: rgba(0, 0, 0, 0.85);
    line-height: 20px;

    &-label {
      cursor: pointer;
      padding: 11px 0;
      margin: 0 15px;
    }

    .gray {
      color: rgba(0, 0, 0, 0.16);
    }
  }

  &-active {
    font-weight: 500;
    color: var(--primary-color);

    .status-tab-item-label {
      position: relative;

      &::after {
        bottom: 0;
        left: 0;
        position: absolute;
        content: '';
        width: 100%;
        height: 2px;
        background: var(--primary-color);
      }
    }
  }
}
</style>
