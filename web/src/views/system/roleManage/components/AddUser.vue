<template>
  <el-drawer
    size="800px"
    :title="drawerTitle"
    :visible.sync="drawer"
    :before-close="handleClose"
  >
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      :label-width="isEnglish() ? '140px' : '100px'"
      class="demo-ruleForm"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('roleManage.roleName')" prop="roleName">
            <el-input
              size="medium"
              :placeholder="$t('roleManage.inputRoleName')"
              v-model="ruleForm.roleName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('roleManage.roleKey')" prop="roleKey">
            <el-input
              size="medium"
              :placeholder="$t('roleManage.inputRoleKey')"
              v-model="ruleForm.roleKey"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('roleManage.roleSort')" prop="roleSort">
            <el-input-number
              v-model="ruleForm.roleSort"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('common.status')" prop="status">
            <el-radio v-model="ruleForm.status" label="0">{{
              $t('common.normal')
            }}</el-radio>
            <el-radio v-model="ruleForm.status" label="1">{{
              $t('common.disabled')
            }}</el-radio>
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
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="$t('roleManage.menuPermission')" prop="menuIds">
            <div>
              <el-checkbox
                v-model="group1"
                @change="handleCheckedTreeExpand($event, 'menu')"
                >{{ $t('roleManage.expandCollapse') }}</el-checkbox
              >
              <el-checkbox
                v-model="group2"
                @change="handleCheckedTreeNodeAll($event, 'menu')"
                >{{ $t('roleManage.selectAll') }}</el-checkbox
              >
              <el-checkbox
                v-model="group3"
                @change="handleCheckedTreeConnect($event, 'menu')"
                >{{ $t('roleManage.parentChildLink') }}</el-checkbox
              >
            </div>
            <div class="box">
              <el-tree
                ref="tree"
                :props="props"
                :data="treeData"
                node-key="id"
                :check-strictly="strictly"
                show-checkbox
              >
              </el-tree>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="demo-drawer__footer">
      <el-button class="btn" @click="postUser" v-loading="loading">{{
        edit
          ? $t('userManage.immediatelyModify')
          : $t('roleManage.immediatelyAdd')
      }}</el-button>
      <el-button @click="handleClose">{{ $t('common.cancel') }}</el-button>
    </div>
  </el-drawer>
</template>

<script>
import {
  postUser,
  putUser,
  getTreeselect,
} from '@/api/system/roleManagement.js'
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
        roleName: null, //角色名称
        roleKey: null, //权限字符
        roleSort: null, //角色顺序
        status: '0', //状态
        remark: null, //备注
        menuIds: [],
      },
      loading: false,
      props: {
        // label: "name",
        // children: "zones",
      },
      count: 1,
      postOptions: [],
      roleOptions: [],
      group1: false,
      group2: false,
      group3: true,
      treeData: [],
      checkedKeys: [],
      strictly: false,
    }
  },
  computed: {
    drawerTitle() {
      return this.edit
        ? this.$t('roleManage.editRole')
        : this.$t('roleManage.addRole')
    },
    rules() {
      return {
        roleName: [
          {
            required: true,
            message: this.$t('roleManage.inputRoleNameMsg'),
            trigger: 'blur',
          },
        ],
        roleKey: [
          {
            required: true,
            message: this.$t('roleManage.inputRoleKeyMsg'),
            trigger: 'blur',
          },
        ],
        roleSort: [
          {
            required: true,
            message: this.$t('roleManage.inputRoleSortMsg'),
            trigger: 'blur',
          },
        ],
      }
    },
  },
  watch: {},
  created() {
    this.init()
  },
  methods: {
    init() {
      this.getTreeselect()
    },
    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value, type) {
      let treeList = this.treeData
      for (let i = 0; i < treeList.length; i++) {
        this.$refs.tree.store.nodesMap[treeList[i].id].expanded = value
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      this.$refs.tree.setCheckedNodes(value ? this.treeData : [])
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value, type) {
      // this.strictly = this.group3;
      this.$set(this, 'strictly', !this.group3)
    },
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.tree.getCheckedKeys()
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    getTreeselect() {
      getTreeselect().then((res) => {
        this.treeData = res.data
      })
    },
    handleClose() {
      this.drawer = false
      this.reset()
    },
    reset() {
      this.ruleForm = {
        roleName: null, //角色名称
        roleKey: null, //权限字符
        roleSort: null, //角色顺序
        status: '0', //状态
        remark: null, //备注
        menuIds: [],
      }
    },
    postUser() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.loading = true
          if (this.edit) {
            putUser({
              ...this.ruleForm,
              menuIds: this.getMenuAllCheckedKeys(),
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
              menuIds: this.getMenuAllCheckedKeys(),
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
.demo-drawer__footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;

  .el-button {
    height: 42px;
    padding: 0 20px;
    font-size: 14px;
  }
}
:deep .el-cascader,
:deep .el-select,
:deep.el-input-number {
  width: 100%;
}
.box {
  width: 548px;
  height: 311px;
  background: var(--bg-color);
  border-radius: 6px 6px 6px 6px;
  opacity: 1;
  border: 1px solid rgba(0, 0, 0, 0.16);
  overflow-y: auto;
}
.btn {
  color: #fff;
  border: none;
  // background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
  background-color: #409eff;
}
</style>
