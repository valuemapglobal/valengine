<!--
 * @Date: 2022-04-24 19:03:38
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-07-11 14:29:58
 * @name: Icon
 * @FilePath: \gutu-projeckt\smartValue\src\components\Icon.vue
-->
<template>
  <i :class="`iconfont icon-${type}`" :style="`font-size:${size}rem;color:${color};`"></i>
</template>

<script>
export default {
  name: "Icon",
  props: {
    type: {
      type: String,
      default: "",
    },
    size: {
      type: [Number,String],
      default: 1,
    },
    color: {
      type: String,
      default: "",
    },
  },
};
</script>

<style lang="less" scoped>
.iconfont {
  color: #b5b5c3;
  font-size: 1rem;
}
</style>
