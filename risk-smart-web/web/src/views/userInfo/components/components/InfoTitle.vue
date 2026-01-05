<template>
  <div class="info-title">
    <div class="info-title-left">
      <div class="info-title-left-text">{{ title }}</div>
      <div class="info-title-left-tip" v-if="tips">{{ tips }}</div>
    </div>
    <div class="info-title-right">
      <slot name="right">
        <el-button type="primary"> 按钮 </el-button>
      </slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Title',
  props: {
    title: { type: String, default: '这是标题' },
    tips: { type: String },
  },
  data() {
    return {}
  },
}
</script>

<style lang="less" scoped>
.info-title {
  padding: 20px 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eeeeee;

  &-left {
    font-size: 16px;
    color: #ff8f1f;
    line-height: 22px;

    &-text {
      font-weight: 600;
      color: #191c31;
    }

    &-tip {
      margin-top: 6px;
    }
  }

  &-right {
    .el-button {
      min-width: 84px;
      height: 40px;
      font-size: 14px;
      border-radius: 6px 6px 6px 6px;

      &:hover {
        opacity: 0.8;
      }

      &:active {
        opacity: 1;
      }

      &--primary {
        border-color: var(--primary-color);
        background: var(--primary-color);
      }

      &--info {
        color: #333333;
        border-color: #f0f2f5;
        background: #f0f2f5;
      }
    }
  }
}
</style>
