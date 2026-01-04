<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  listUser,
  getUser,
  addUser,
  updateUser,
  delUser,
  changeUserStatus,
  resetUserPwd,
  getDeptTree
} from '@/api/system/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Key } from '@element-plus/icons-vue'

const loading = ref(false)
const userList = ref<any[]>([])
const total = ref(0)
const deptOptions = ref<any[]>([])
const deptName = ref('')

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: undefined,
  deptId: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({
    userId: undefined,
    deptId: undefined,
    userName: '',
    nickName: '',
    password: '',
    phonenumber: '',
    email: '',
    sex: '0',
    status: '0',
    remark: '',
    postIds: [],
    roleIds: []
})

// --- Methods ---
const getList = async () => {
    loading.value = true
    try {
        const res: any = await listUser(queryParams)
        if (res.code === 200) {
            userList.value = res.rows || res.data?.list || []
            total.value = res.total || res.data?.total || 0
        }
    } catch(e) {}
    finally { loading.value = false }
}

const getDeptTreeData = async () => {
    try {
        const res = await getDeptTree()
        deptOptions.value = res.data
    } catch(e) {}
}

const handleNodeClick = (data: any) => {
    queryParams.deptId = data.id
    handleSearch()
}

const handleSearch = () => {
    queryParams.pageNum = 1
    getList()
}

const handleReset = () => {
    queryParams.userName = ''
    queryParams.phonenumber = ''
    queryParams.status = undefined
    queryParams.deptId = undefined
    handleSearch()
}

const handleAdd = () => {
    resetForm()
    dialogTitle.value = '新增用户'
    dialogVisible.value = true
}

const handleEdit = async (row: any) => {
    resetForm()
    dialogTitle.value = '修改用户'
    try {
        const res: any = await getUser(row.userId)
        if (res.code === 200) {
             const data = res.data
             Object.assign(form, data)
             form.roleIds = res.roleIds || []
             form.postIds = res.postIds || []
             // If dept is object, extract id
             if (data.dept) form.deptId = data.dept.deptId
             dialogVisible.value = true
        }
    } catch(e) {}
}

const handleDelete = (row: any) => {
     ElMessageBox.confirm('是否确认删除用户编号为"' + row.userId + '"的数据项?', '警告', {
        type: 'warning'
    }).then(async () => {
        await delUser(row.userId)
        ElMessage.success('删除成功')
        getList()
    })
}

const handleStatusChange = async (row: any) => {
     try {
        await changeUserStatus({ userId: row.userId, status: row.status })
        ElMessage.success('状态修改成功')
    } catch(e) {
        row.status = row.status === '0' ? '1' : '0'
    }
}

const handleResetPwd = (row: any) => {
    ElMessageBox.prompt('请输入"' + row.userName + '"的新密码', '重置密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
    }).then(async ({ value }) => {
        await resetUserPwd({ userId: row.userId, password: value })
        ElMessage.success('修改成功，新密码是：' + value)
    })
}

const submitForm = async () => {
     if (!form.userName) {
        ElMessage.warning('请输入用户名称')
        return
    }
    try {
        if (form.userId) {
            await updateUser(form)
            ElMessage.success('修改成功')
        } else {
            await addUser(form)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
    } catch(e) {}
}

const resetForm = () => {
     Object.assign(form, {
        userId: undefined,
        deptId: undefined,
        userName: '',
        nickName: '',
        password: '',
        phonenumber: '',
        email: '',
        sex: '0',
        status: '0',
        remark: '',
        postIds: [],
        roleIds: []
    })
}

onMounted(() => {
    getList()
    getDeptTreeData()
})
</script>

<template>
  <div class="user-container">
      <div class="left-panel">
           <el-input
              v-model="deptName"
              placeholder="请输入部门名称"
              prefix-icon="Search"
              style="margin-bottom: 20px"
            />
           <el-tree
            :data="deptOptions"
            :props="{ children: 'children', label: 'label' }"
            :expand-on-click-node="false"
            :filter-node-method="(value: string, data: any) => data.label.includes(value)"
            ref="tree"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          />
      </div>
      
      <div class="right-panel">
           <div class="search-bar">
               <el-form :inline="true" :model="queryParams">
                   <el-form-item label="用户名称">
                       <el-input v-model="queryParams.userName" placeholder="请输入" clearable @keyup.enter="handleSearch" />
                   </el-form-item>
                   <el-form-item label="手机号码">
                       <el-input v-model="queryParams.phonenumber" placeholder="请输入" clearable @keyup.enter="handleSearch" />
                   </el-form-item>
                   <el-form-item>
                       <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
                   </el-form-item>
               </el-form>
           </div>
           
           <div class="table-content">
               <el-table v-loading="loading" :data="userList" border style="width: 100%">
                   <el-table-column type="selection" width="50" align="center" />
                    <el-table-column label="用户编号" align="center" prop="userId" />
                    <el-table-column label="用户名称" align="center" prop="userName" />
                    <el-table-column label="用户昵称" align="center" prop="nickName" />
                    <el-table-column label="部门" align="center" prop="dept.deptName" />
                    <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
                    <el-table-column label="状态" align="center">
                         <template #default="{ row }">
                            <el-switch
                              v-model="row.status"
                              active-value="0"
                              inactive-value="1"
                              @change="handleStatusChange(row)"
                            />
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
                        <template #default="{ row }">
                            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">修改</el-button>
                            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
                            <el-button link type="warning" :icon="Key" @click="handleResetPwd(row)">重置密码</el-button>
                        </template>
                    </el-table-column>
               </el-table>
               
                <el-pagination
                class="pagination"
                v-if="total > 0"
                v-model:current-page="queryParams.pageNum"
                v-model:page-size="queryParams.pageSize"
                :total="total"
                layout="total, prev, pager, next"
                @current-change="getList"
             />
           </div>
      </div>
      
      <!-- Add/Edit Dialog -->
      <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
          <el-form ref="userForm" :model="form" label-width="80px">
              <el-row>
                  <el-col :span="12">
                      <el-form-item label="用户昵称" prop="nickName">
                          <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
                      </el-form-item>
                  </el-col>
                  <el-col :span="12">
                      <el-form-item label="归属部门" prop="deptId">
                          <el-tree-select
                            v-model="form.deptId"
                            :data="deptOptions"
                            :props="{ label: 'label', children: 'children' }"
                            value-key="id"
                            placeholder="请选择"
                            check-strictly
                          />
                      </el-form-item>
                  </el-col>
              </el-row>
              <el-row>
                  <el-col :span="12">
                      <el-form-item label="手机号码" prop="phonenumber">
                          <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
                      </el-form-item>
                  </el-col>
                  <el-col :span="12">
                      <el-form-item label="邮箱" prop="email">
                          <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
                      </el-form-item>
                  </el-col>
              </el-row>
               <el-row>
                  <el-col :span="12">
                      <el-form-item label="用户名称" prop="userName">
                          <el-input v-model="form.userName" placeholder="请输入用户名称" />
                      </el-form-item>
                  </el-col>
                  <el-col :span="12">
                      <el-form-item label="用户密码" prop="password">
                          <el-input v-model="form.password" placeholder="请输入用户密码" type="password" />
                      </el-form-item>
                  </el-col>
              </el-row>
              <el-form-item label="状态">
                   <el-radio-group v-model="form.status">
                        <el-radio label="0">正常</el-radio>
                        <el-radio label="1">停用</el-radio>
                    </el-radio-group>
              </el-form-item>
              <el-form-item label="备注">
                  <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
              </el-form-item>
          </el-form>
           <template #footer>
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitForm">确定</el-button>
          </template>
      </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.user-container {
    padding: 20px;
    background: #f0f2f5;
    min-height: 100vh;
    display: flex;
    gap: 20px;
}
.left-panel {
    width: 260px;
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    height: calc(100vh - 40px);
    overflow-y: auto;
}
.right-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
}
.search-bar {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
}
.table-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    flex: 1;
}
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
