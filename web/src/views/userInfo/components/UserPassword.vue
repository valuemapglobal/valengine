<template>
  <div style="height: 100%">
    <!-- 头部信息 -->
    <div class="title">
      <span>个人信息</span>
      <div>
        <el-button type="primary" @click="submit">提交修改</el-button>
        <el-button type="info" plain>取消</el-button>
      </div>
    </div>
    <!-- 内容 -->
    <div class="formBox">
      <el-form
        :model="form"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="form"
      >
        <el-form-item label="旧密码:" prop="avatar">
          <el-input
            v-model="form.oldPassword"
            placeholder="请输入旧密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码:" prop="avatar">
          <el-input
            v-model="form.password"
            placeholder="请输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认新密码:" prop="avatar">
          <el-input
            v-model="form.confirmPassword"
            placeholder="请输入确认新密码"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { changePwd } from '@/api/base/index.js'
export default {
  name: 'UserPassword',
  components: {},
  data() {
    return {
      form: {
        oldPassword: undefined,
        password: undefined,
        confirmPassword: undefined,
      },
      // 表单校验
      rules: {},
    }
  },
  created() {},
  mounted() {},
  methods: {
    submit() {
      this.change()
    },
    close() {
      this.$router.push({ path: '/index' })
    },
    // 个人信息查看
    change() {
      this.$confirm('此操作会修改您的密码, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          changePwd(this.form).then((res) => {
            if (res.code === 200) {
              //  this.userId = res.user.userId
              this.$message({
                message: '修改成功',
                type: 'success',
              })
              this.$emit('back')
            } else {
              this.$message.error('修改失败')
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消修改',
          })
        })
    },
  },
}
</script>

<style scoped lang="less">
.title {
  height: 6.25rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1.875rem;
  border-bottom: 0.0625rem solid #eeeeee;
  box-sizing: border-box;
  > span {
    font-size: 1rem;
    font-weight: 500;
    color: #191c31;
    line-height: 1.375rem;
  }
}
.form {
  width: 50%;
}
.formBox {
  padding: 1.25rem;
  overflow-y: auto;
  height: calc(100% - 8.75rem);
}
</style>
