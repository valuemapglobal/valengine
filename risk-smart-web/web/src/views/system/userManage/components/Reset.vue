<template>
  <el-dialog title="重置密码" :visible.sync="dialogVisible" width="507px">
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="新的密码" prop="password">
        <el-input
          type="password"
          show-password
          size="medium"
          v-model="ruleForm.password"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button class="btn" @click="submit">确认修改</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { resetPwd } from '@/api/system/userManagement.js'
export default {
  data() {
    return {
      dialogVisible: false,
      ruleForm: {
        password: null,
      },
      userId: null,
      rules: {
        password: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          {
            min: 5,
            max: 20,
            message: '用户密码长度必须介于 5 和 20 之间',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  methods: {
    submit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          console.log(this.ruleForm)
          resetPwd({
            password: this.ruleForm.password,
            userId: this.userId,
          })
            .then((res) => {
              this.show()
              this.$emit('success')
              this.$message({
                type: 'success',
                message: '操作成功!',
              })
            })
            .catch(() => {
              this.show()
              this.$emit('success')
            })
        } else {
          return false
        }
      })
    },
    show() {
      this.dialogVisible = !this.dialogVisible
      if (!this.dialogVisible) {
        this.ruleForm.password = null
      }
    },
  },
}
</script>

<style lang="less" scoped>
:deep .el-dialog {
  border-radius: 12px;
}
.box {
  width: 100%;
  display: flex;
  justify-content: center;
  margin: 14px 0;
}
:deep .el-upload-dragger,
:deep .el-upload,
:deep .upload-demo {
  width: 100%;
}
.dialog-footer {
  .el-button {
    height: 42px;
    padding: 0 20px;
    font-size: 14px;
  }
  .btn {
    color: #fff;
    border: none;
    background: #409eff;
  }
}
</style>
