<template>
  <el-dialog title="分配数据权限" :visible.sync="dialogVisible" width="507px">
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          size="medium"
          v-model="ruleForm.roleName"
          :disabled="true"
        ></el-input>
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          size="medium"
          v-model="ruleForm.roleKey"
          :disabled="true"
        ></el-input>
      </el-form-item>
      <el-form-item label="权限范围" prop="dataScope">
        <el-select v-model="ruleForm.dataScope" @change="dataScopeSelectChange">
          <el-option
            v-for="item in dataScopeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button class="btn" @click="submit">确认修改</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { dataScope } from '@/api/system/roleManagement.js'

export default {
  data() {
    return {
      dialogVisible: false,
      ruleForm: {
        roleName: undefined,
        roleKey: undefined,
        dataScope: undefined,
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
      dataScopeOptions: [
        {
          value: '1',
          label: '全部数据权限',
        },
        {
          value: '2',
          label: '自定数据权限',
        },
        {
          value: '3',
          label: '本部门数据权限',
        },
        {
          value: '4',
          label: '本部门及以下数据权限',
        },
        {
          value: '5',
          label: '仅本人数据权限',
        },
      ],
    }
  },
  methods: {
    dataScopeSelectChange(value) {
      if (value !== '2') {
        this.$refs.dept.setCheckedKeys([])
      }
    },
    submit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          dataScope({
            ...this.ruleForm,
            deptIds: [],
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
.btn {
  color: #fff;
  border: none;
  // background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
  background-color: #409eff;
}
</style>
