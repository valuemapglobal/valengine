<!--
 * @Date: 2022-04-21 18:33:23
 * @LastEditors: Do not edit
 * @LastEditTime: 2023-02-22 10:27:57
 * @name: CodeLogin 验证码登录
 * @FilePath: \vmkj\src\login\components\PassWordLogin.vue
-->
<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" label-width="0">
      <span class="text">手机号或账号</span>
      <el-form-item prop="username">
        <el-input
          class="ipt"
          v-model="form.username"
          placeholder="请输入手机号或账号"
        ></el-input>
      </el-form-item>
      <div class="textBox">
        <span class="text">密码</span>
        <a @click="toRegister(4)">忘记密码？</a>
      </div>
      <el-form-item prop="password">
        <el-input
          class="ipt"
          placeholder="请输入密码"
          v-model="form.password"
          show-password
        >
        </el-input>
      </el-form-item>
    </el-form>
    <!-- 用户同意权限 -->
    <Authority v-model="status" />

    <div class="btns">
      <el-button type="primary" @click="submitForm" :loading="loading"
        >登录</el-button
      >
      <!-- <el-button type="primary" plain @click="toRegister(1)"
        >免密码登录</el-button
      > -->
    </div>
  </div>
</template>

<script>
import Authority from './Authority.vue'
import { toRegister } from './toRegister'
import { login } from '@/api/base/index.js'

export default {
  name: 'CodeLogin',
  mixins: [toRegister],

  components: {
    Authority,
  },
  data() {
    return {
      form: {
        username: null,
        password: null,
      },
      status: false, //用户同意状态
      loading: false,
      rules: {
        username: [
          {
            required: true,
            message: '请输入手机号',
            trigger: 'blur',
          },
        ],
        password: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (!this.status) {
            this.$message.warning('请选同意用户协议！')
            return
          }
          this.loading = true
          login(this.form)
            .then((res) => {
              // this.$message.success("登录成功！");
              localStorage.setItem('id_token', res.data.access_token)
              this.$emit('success')
              this.loading = false
            })
            .catch((err) => {
              this.loading = false
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
  },
}
</script>
<style lang="less" scoped>
.textBox {
  display: flex;
  justify-content: space-between;
}

.ipt {
  margin: 10px 0 0 0;

  :deep input {
    background-color: #f3f6f9;
    border-radius: 12px;
    height: 65px !important;
  }
}

a {
  color: #3699ff !important;
}

.btns {
  display: flex;

  margin-top: 30px;

  > button {
    width: 100%;
    height: 54px;
    padding: 16px 34px;
    border-radius: 8px;
  }
}

.textBox {
  display: flex;
  justify-content: space-between;
}
</style>
