<!--
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2024-06-27 09:57:03
 * @LastEditTime: 2024-07-16 15:52:35
 * @LastEditors: Seastar 1507136388@qq.com
 * @Description: 
-->
<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :width="width"
    :append-to-body="true"
    :before-close="dialogBeforeClose"
  >
    <template slot="title">
      <div class="header" v-if="title">
        <el-image :src="require('./image/doubt.png')" />
        <span>{{ title }}</span>
      </div>
    </template>
    <div>{{ tips }}</div>
    <div class="bottomBtn">
      <el-button type="primary" class="confirm" @click="handleClick">{{
        confirmBtn
      }}</el-button>
      <el-button @click="dialogVisible = false">{{ cancelBtn }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: {
    width: {
      type: String,
      default: '30%',
    },
    title: {
      type: String || null,
      default: null,
    },
    tips: {
      type: String,
      default: '',
    },
    confirmBtn: {
      type: String,
      default: '确定',
    },
    cancelBtn: {
      type: String,
      default: '取消',
    },
  },
  data() {
    return {
      dialogVisible: false,
    }
  },
  methods: {
    handleClick() {
      this.$emit('confirm')
    },
    openDialog() {
      this.dialogVisible = true
    },
    dialogBeforeClose() {
      this.dialogVisible = false
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep(.el-dialog) {
  border-radius: 6px;
  .el-dialog__header {
    padding-bottom: 0px;
    font-family: PingFang SC-Medium;
    font-size: 18px;
    color: rgba(#000, 0.85);
  }
  .el-dialog__body {
    padding: 20px;
    color: #666666;
    font-size: 14px;
    font-family: PingFang SC-Regular;
  }
}
.header {
  display: flex;
  align-items: center;
  .el-image {
    width: 22px;
    height: 22px;
    margin-right: 16px;
  }
}
.bottomBtn {
  margin-top: 14px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  .confirm {
    padding: 7px 12px;
    background-color: #3d7fff;
    color: #fff;
    font-size: 14px;
  }
}
</style>
