<template>
  <div class="auth-fail">
    <div class="el-icon-error" />
    <div class="error-tip">
      打款金额错误！请联系客服后重新开通企业认证功能！客服热线：400-1818-747
    </div>
  </div>
</template>

<script>
export default {
  name: 'AuthFail',
  data() {
    return {
      dialogVisible: false,
    }
  },
}
</script>

<style lang="less" scoped>
.auth-fail {
  text-align: center;
}

.el-icon-error {
  color: #fa5151;
  font-size: 73px;
}

.error-tip {
  margin-top: 28px;
  text-align: center;
  font-size: 18px;
  font-weight: 500;
  color: #333333;
  line-height: 27px;
}
</style>
