<template>
  <el-drawer
    size="800px"
    title="添加用户"
    :visible.sync="drawer"
    :before-close="handleClose"
  >
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="角色名称" prop="roleName">
            <el-input
              size="medium"
              placeholder="请输入角色名称"
              v-model="ruleForm.roleName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="权限字符" prop="roleKey">
            <el-input
              size="medium"
              placeholder="请输入权限字符"
              v-model="ruleForm.roleKey"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="角色顺序" prop="roleSort">
            <el-input-number
              v-model="ruleForm.roleSort"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio v-model="ruleForm.status" label="0">正常</el-radio>
            <el-radio v-model="ruleForm.status" label="1">停用</el-radio>
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
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="菜单权限" prop="menuIds">
            <div>
              <el-checkbox
                v-model="group1"
                @change="handleCheckedTreeExpand($event, 'menu')"
                >展开/折叠</el-checkbox
              >
              <el-checkbox
                v-model="group2"
                @change="handleCheckedTreeNodeAll($event, 'menu')"
                >全选/全不选</el-checkbox
              >
              <el-checkbox
                v-model="group3"
                @change="handleCheckedTreeConnect($event, 'menu')"
                >父子联动</el-checkbox
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
      <el-button class="btn" @click="postUser" v-loading="loading"
        >立即添加</el-button
      >
      <el-button @click="handleClose">取消</el-button>
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
      rules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
        ],
        roleKey: [
          { required: true, message: '请输入权限字符', trigger: 'blur' },
        ],
        roleSort: [
          { required: true, message: '请输入角色顺序', trigger: 'blur' },
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
      group1: false,
      group2: false,
      group3: true,
      treeData: [],
      checkedKeys: [],
      strictly: false,
    }
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
      console.log(this.ruleForm, 'ruleForm')
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
              menuIds: this.getMenuAllCheckedKeys(),
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
