<template>
  <el-drawer size="1400px" title="添加用户" :visible.sync="drawer">
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户昵称" prop="nickName">
            <el-input
              size="medium"
              placeholder="请输入用户昵称"
              v-model="ruleForm.nickName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="归属部门" prop="deptId">
            <el-cascader
              size="medium"
              v-model="ruleForm.deptId"
              placeholder="请输入归属部门"
              :options="options"
              :props="{ checkStrictly: true, value: 'id' }"
            ></el-cascader>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input
              size="medium"
              placeholder="请输入手机号码"
              v-model="ruleForm.phonenumber"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input
              size="medium"
              placeholder="请输入邮箱"
              v-model="ruleForm.email"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="!edit">
        <el-col :span="12">
          <el-form-item label="用户名称" prop="userName">
            <el-input
              size="medium"
              placeholder="请输入用户名称"
              v-model="ruleForm.userName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="用户密码" prop="password">
            <el-input
              placeholder="请输入用户密码"
              size="medium"
              show-password
              v-model="ruleForm.password"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户性别" prop="sex">
            <!-- <el-input size="medium" v-model="ruleForm.sex"></el-input> -->
            <el-select
              v-model="ruleForm.sex"
              placeholder="请选择性别"
              size="medium"
              clearable
            >
              <el-option
                v-for="val in sexOptions"
                :key="val.value"
                :label="val.label"
                :value="val.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="ruleForm.status" fill="#ff4040">
              <el-radio label="0">正常</el-radio>
              <el-radio label="1">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="岗位" prop="postIds">
            <el-select
              v-model="ruleForm.postIds"
              placeholder="请选择岗位"
              size="medium"
              clearable
              multiple
            >
              <el-option
                v-for="val in postOptions"
                :key="val.postId"
                :label="val.postName"
                :value="val.postId"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="角色" prop="roleIds">
            <el-select
              v-model="ruleForm.roleIds"
              placeholder="请选择角色"
              size="medium"
              clearable
              multiple
            >
              <el-option
                v-for="val in roleOptions"
                :key="val.roleId"
                :label="val.roleName"
                :value="val.roleId"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input
              placeholder="请输入备注"
              type="textarea"
              size="medium"
              v-model="ruleForm.remark"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="demo-drawer__footer">
      <el-button class="btn" @click="postUser" v-loading="loading">{{
        edit ? '立即修改' : '立即添加'
      }}</el-button>
      <el-button
        @click="
          () => {
            this.drawer = false
            this.reset()
          }
        "
        >取消</el-button
      >
    </div>
  </el-drawer>
</template>

<script>
import { getUser, postUser, putUser } from '@/api/system/userManagement.js'
export default {
  props: {
    options: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      edit: false,
      drawer: false,
      ruleForm: {
        deptId: null, //部门
        email: null, //邮箱
        nickName: null, //昵称
        password: '123456', //密码
        phonenumber: null, //手机号
        sex: '0', //性别
        userName: null, //账号
        status: '0', //状态
        remark: null, //备注
        postIds: null, //岗位
        roleIds: null, //角色
      },
      loading: false,
      rules: {
        nickName: [
          { required: true, message: '请输入用户昵称', trigger: 'blur' },
        ],
        userName: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          {
            min: 2,
            max: 20,
            message: '请输入最少2个最多20个的字符',
            trigger: 'blur',
          },
        ],
        password: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
        ],
        roleIds: [
          { required: true, message: '请选择用户角色', trigger: 'change' },
        ],
      },
      sexOptions: [
        {
          label: '男',
          value: '0',
        },
        {
          label: '女',
          value: '1',
        },
        {
          label: '未知',
          value: '2',
        },
      ],
      postOptions: [],
      roleOptions: [],
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      this.getUser()
    },
    reset() {
      this.ruleForm = {
        deptId: null, //部门
        email: null, //邮箱
        nickName: null, //昵称
        password: '123456', //密码
        phonenumber: null, //手机号
        sex: '0', //性别
        userName: null, //账号
        status: '0', //状态
        remark: null, //备注
        postIds: null, //岗位
        roleIds: null, //角色
      }
      this.$nextTick(() => {
        this.$refs.ruleForm.resetFields()
      })
    },
    getUser() {
      getUser().then((res) => {
        this.postOptions = res.posts
        this.roleOptions = res.roles
      })
    },
    postUser() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.loading = true
          if (this.edit) {
            putUser({
              ...this.ruleForm,
              deptId: this.ruleForm.deptId
                ? this.ruleForm.deptId[this.ruleForm.deptId.length - 1]
                : null,
            })
              .then((res) => {
                this.loading = false
                this.drawer = false
                this.reset()
                this.$emit('success')
                this.$message({
                  type: 'success',
                  message: '操作成功!',
                })
              })
              .catch((err) => {
                this.$emit('success')
                this.loading = false
              })
          } else {
            postUser({
              ...this.ruleForm,
              deptId: this.ruleForm.deptId
                ? this.ruleForm.deptId[this.ruleForm.deptId.length - 1]
                : null,
            })
              .then((res) => {
                this.loading = false
                this.drawer = false
                this.$emit('success')
                this.reset()
                this.$message({
                  type: 'success',
                  message: '操作成功!',
                })
              })
              .catch((err) => {
                this.loading = false
                this.$emit('success')
              })
          }
        } else {
          return false
        }
      })
    },
    show() {
      this.drawer = !this.drawer
    },
  },
}
</script>

<style lang="less" scoped>
// :deep .el-drawer__body {
//   padding: 20px;
// }
.demo-drawer__footer {
  display: flex;
  justify-content: flex-end;

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
:deep .el-cascader,
:deep .el-select {
  width: 100%;
}
</style>
