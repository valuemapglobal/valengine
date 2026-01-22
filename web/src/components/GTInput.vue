<template>
  <el-input v-bind="$attrs" v-on="$listeners" :value="value" class="gt-input">
    <template slot="suffix">
      <slot name="suffix"></slot>
    </template>
    <template slot="prefix">
      <slot name="prefix"></slot>
    </template>
  </el-input>
</template>

<script>
export default {
  name: 'GTInput',
  props: {
    value: '',
  },
  data() {
    return {}
  },
}
</script>

<style lang="less" scoped>
.gt-input {
  width: 300px;

  :deep(.el-input__inner) {
    width: 300px;
    height: 42px;
    border: none;
    font-size: 14px;
    border-radius: 4px;
    background-color: rgba(#000, 0.04);
  }

  :deep(.el-input__suffix) {
    right: 16px;
    color: #b3b3b3;
    font-size: 17px;
    display: flex;
    align-items: center;
  }
}
</style>
