<template>
  <div>
    <el-dialog
      :visible.sync="visible"
      width="507px"
      append-to-body
      :before-close="cancel"
    >
      <div slot="title" class="title-box">
        <img
          :src="require('@/assets/images/question-icon.png')"
          class="question-icon"
        />
        <span>提示</span>
      </div>
      <slot></slot>
      <span slot="footer" class="dialog-footer">
        <el-button @click="update" type="primary" :disabled="disabled">{{
          updateText
        }}</el-button>
        <el-button
          @click="reserved"
          class="reserved-button"
          :disabled="disabled"
          >{{ reservedText }}</el-button
        >
        <el-button @click="cancel" v-if="showCancelBtn">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    updateText: {
      type: String,
      default: '更新版本',
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    reservedText: {
      type: String,
      default: '保留原版本',
    },
    showCancelBtn: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      visible: false,
    }
  },
  methods: {
    update() {
      this.$emit('update')
    },
    reserved() {
      this.$emit('reserved')
    },
    cancel() {
      this.visible = false
      this.$emit('cancel')
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep .el-dialog {
  border-radius: 12px;
}

::v-deep .el-dialog__body {
  padding: 0px 16px;

  > div {
    div + div {
      margin-top: 4px;
    }
  }
}

.title-box {
  .question-icon {
    width: 22px;
    height: 22px;
    margin-right: 14px;
  }
}

.reserved-button {
  background: #e9f3fc;
  color: var(--primary-color);
  border: none;
}
</style>
