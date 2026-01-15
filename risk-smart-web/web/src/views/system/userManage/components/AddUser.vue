<template>
  <el-drawer size="1400px" :title="drawerTitle" :visible.sync="drawer">
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      :label-width="isEnglish() ? '140px' : '100px'"
      class="demo-ruleForm"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('userManage.nickName')" prop="nickName">
            <el-input
              size="medium"
              :placeholder="$t('userManage.inputNickName')"
              v-model="ruleForm.nickName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('userManage.dept')" prop="deptId">
            <el-cascader
              size="medium"
              v-model="ruleForm.deptId"
              :placeholder="$t('userManage.inputDept')"
              :options="options"
              :props="{ checkStrictly: true, value: 'id' }"
            ></el-cascader>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item
            :label="$t('userManage.phonenumber')"
            prop="phonenumber"
          >
            <el-input
              size="medium"
              :placeholder="$t('userManage.inputPhonenumber')"
              v-model="ruleForm.phonenumber"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('userManage.email')" prop="email">
            <el-input
              size="medium"
              :placeholder="$t('userManage.inputEmail')"
              v-model="ruleForm.email"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="!edit">
        <el-col :span="12">
          <el-form-item :label="$t('userManage.userName')" prop="userName">
            <el-input
              size="medium"
              :placeholder="$t('userManage.inputUserName')"
              v-model="ruleForm.userName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('userManage.password')" prop="password">
            <el-input
              :placeholder="$t('userManage.inputPassword')"
              size="medium"
              show-password
              v-model="ruleForm.password"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('userManage.sex')" prop="sex">
            <!-- <el-input size="medium" v-model="ruleForm.sex"></el-input> -->
            <el-select
              v-model="ruleForm.sex"
              :placeholder="$t('userManage.selectSex')"
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
          <el-form-item :label="$t('common.status')" prop="status">
            <el-radio-group v-model="ruleForm.status" fill="#ff4040">
              <el-radio label="0">{{ $t('common.normal') }}</el-radio>
              <el-radio label="1">{{ $t('common.disabled') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('userManage.post')" prop="postIds">
            <el-select
              v-model="ruleForm.postIds"
              :placeholder="$t('userManage.selectPost')"
              size="medium"
              clearable
              multiple
              collapse-tags
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
          <el-form-item :label="$t('userManage.role')" prop="roleIds">
            <el-select
              v-model="ruleForm.roleIds"
              :placeholder="$t('userManage.selectRole')"
              size="medium"
              clearable
              multiple
              collapse-tags
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
          <el-form-item :label="$t('common.remark')" prop="remark">
            <el-input
              :placeholder="$t('userManage.inputRemark')"
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
        edit
          ? $t('userManage.immediatelyModify')
          : $t('userManage.immediatelyAdd')
      }}</el-button>
      <el-button
        @click="
          () => {
            this.drawer = false
            this.reset()
          }
        "
        >{{ $t('common.cancel') }}</el-button
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
      postOptions: [],
      roleOptions: [],
    }
  },
  computed: {
    drawerTitle() {
      return this.edit
        ? this.$t('userManage.editUser')
        : this.$t('userManage.addUser')
    },
    rules() {
      return {
        nickName: [
          {
            required: true,
            message: this.$t('userManage.inputNickNameMsg'),
            trigger: 'blur',
          },
        ],
        userName: [
          {
            required: true,
            message: this.$t('userManage.inputUserNameMsg'),
            trigger: 'blur',
          },
          {
            min: 2,
            max: 20,
            message: this.$t('userManage.userNameLengthMsg'),
            trigger: 'blur',
          },
        ],
        password: [
          {
            required: true,
            message: this.$t('userManage.inputPasswordMsg'),
            trigger: 'blur',
          },
        ],
        roleIds: [
          {
            required: true,
            message: this.$t('userManage.selectRoleMsg'),
            trigger: 'change',
          },
        ],
      }
    },
    sexOptions() {
      return [
        {
          label: this.$t('userManage.male'),
          value: '0',
        },
        {
          label: this.$t('userManage.female'),
          value: '1',
        },
        {
          label: this.$t('userManage.unknown'),
          value: '2',
        },
      ]
    },
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
                  message: this.$t('common.success'),
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
                  message: this.$t('common.success'),
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
