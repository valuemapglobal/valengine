<template>
  <!--大标题-->
  <div class="head-line">
    <div class="head-line-text">
      <slot>标题</slot>
    </div>
    <div class="head-line-cancel" @click="$emit('click')">
      <img src="../../public/images/Financing/cancel.png" alt="" />
    </div>
  </div>
</template>

<script>
export default {
  name: '',
}
</script>

<style lang="less" scoped>
.head-line {
  display: flex;
  align-items: center;
  justify-content: space-between;

  &-text {
    font-size: 18px;
    font-weight: 500;
    color: #3f4254;
    line-height: 26px;
  }

  &-cancel {
    img {
      width: 26px;
      height: 26px;
      cursor: pointer;
    }
  }
}
</style>
