<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import {
  listRole,
  addRole,
  updateRole,
  delRole,
  changeRoleStatus,
  getMenuTree,
  getRoleMenuTree
} from '@/api/system/role'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const roleList = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    roleName: '',
    roleKey: '',
    status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({
    roleId: undefined,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    status: '0',
    menuIds: [] as any[],
    remark: ''
})

const menuOptions = ref<any[]>([])
const menuRef = ref<any>(null)

// --- Methods ---
const getList = async () => {
    loading.value = true
    try {
        const res: any = await listRole(queryParams)
        if (res.code === 200) {
            roleList.value = res.data?.list || res.rows || []
            total.value = res.data?.total || res.total || 0
        }
    } catch(e) {}
    finally { loading.value = false }
}

const handleSearch = () => {
    queryParams.pageNum = 1
    getList()
}

const handleReset = () => {
    queryParams.roleName = ''
    queryParams.roleKey = ''
    queryParams.status = undefined
    handleSearch()
}

const handleAdd = () => {
    resetForm()
    getMenuTreeselect()
    dialogTitle.value = '新增角色'
    dialogVisible.value = true
}

const handleEdit = async (row: any) => {
    resetForm()
    dialogTitle.value = '修改角色'
    const roleMenuRes: any = await getRoleMenuTree(row.roleId)
    
    // Legacy API returns { checkedKeys: [...], menus: [...] } or just checkedKeys?
    // Based on standard, it usually returns ids.
    // Let's assume standardized response from standard API.
    
    // Just refresh menu tree first
    await getMenuTreeselect()
    
    form.roleId = row.roleId
    form.roleName = row.roleName
    form.roleKey = row.roleKey
    form.roleSort = row.roleSort
    form.status = row.status
    form.remark = row.remark
    
    dialogVisible.value = true
    
    nextTick(() => {
        if (roleMenuRes.checkedKeys) {
            roleMenuRes.checkedKeys.forEach((v: any) => {
                nextTick(() => {
                     menuRef.value.setChecked(v, true, false)
                })
            })
        }
    })
}

const getMenuTreeselect = async () => {
    const res = await getMenuTree()
    menuOptions.value = res.data
}

const submitForm = async () => {
     if (!form.roleName) {
        ElMessage.warning('请输入角色名称')
        return
    }
    
    // Get Checked Keys
    const checkedKeys = menuRef.value.getCheckedKeys()
    const halfCheckedKeys = menuRef.value.getHalfCheckedKeys()
    form.menuIds = checkedKeys.concat(halfCheckedKeys)
    
    try {
        if (form.roleId) {
            await updateRole(form)
            ElMessage.success('修改成功')
        } else {
            await addRole(form)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
    } catch(e) {}
}

const handleDelete = (row: any) => {
      ElMessageBox.confirm('是否确认删除名称为"' + row.roleName + '"的数据项?', '警告', {
        type: 'warning'
    }).then(async () => {
        await delRole(row.roleId)
        ElMessage.success('删除成功')
        getList()
    })
}

const handleStatusChange = async (row: any) => {
    try {
        await changeRoleStatus({ roleId: row.roleId, status: row.status })
        ElMessage.success('状态修改成功')
    } catch(e) {
        row.status = row.status === '0' ? '1' : '0'
    }
}

const resetForm = () => {
     Object.assign(form, {
        roleId: undefined,
        roleName: '',
        roleKey: '',
        roleSort: 0,
        status: '0',
        menuIds: [],
        remark: ''
    })
    if (menuRef.value) {
        menuRef.value.setCheckedKeys([])
    }
}

onMounted(() => {
    getList()
})
</script>

<template>
  <div class="role-container">
      <div class="search-bar">
          <el-form :inline="true" :model="queryParams">
               <el-form-item label="角色名称">
                  <el-input v-model="queryParams.roleName" placeholder="请输入" clearable @keyup.enter="handleSearch" />
              </el-form-item>
              <el-form-item label="权限字符">
                  <el-input v-model="queryParams.roleKey" placeholder="请输入" clearable @keyup.enter="handleSearch" />
              </el-form-item>
               <el-form-item label="状态">
                   <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
                       <el-option label="正常" value="0" />
                       <el-option label="停用" value="1" />
                   </el-select>
               </el-form-item>
               <el-form-item>
                  <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
                  <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                  <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
               </el-form-item>
          </el-form>
      </div>
      
       <div class="table-content">
           <el-table v-loading="loading" :data="roleList" border style="width: 100%">
               <el-table-column type="selection" width="55" align="center" />
               <el-table-column label="角色编号" prop="roleId" width="120" />
               <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150" />
               <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="150" />
               <el-table-column label="显示顺序" prop="roleSort" width="100" />
               <el-table-column label="状态" align="center" width="100">
                    <template #default="{ row }">
                        <el-switch
                              v-model="row.status"
                              active-value="0"
                              inactive-value="1"
                              @change="handleStatusChange(row)"
                            />
                    </template>
               </el-table-column>
               <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
               <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                   <template #default="{ row }">
                       <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">修改</el-button>
                        <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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
       
       <!-- Add/Edit Dialog -->
       <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body>
           <el-form ref="roleForm" :model="form" label-width="100px">
               <el-form-item label="角色名称" prop="roleName">
                    <el-input v-model="form.roleName" placeholder="请输入角色名称" />
               </el-form-item>
               <el-form-item label="权限字符" prop="roleKey">
                    <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
               </el-form-item>
               <el-form-item label="角色顺序" prop="roleSort">
                    <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
               </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="form.status">
                        <el-radio label="0">正常</el-radio>
                        <el-radio label="1">停用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="菜单权限">
                     <el-tree
                        class="tree-border"
                        :data="menuOptions"
                        show-checkbox
                        ref="menuRef"
                        node-key="id" 
                        empty-text="加载中，请稍候"
                        :props="{ label: 'label', children: 'children' }"
                     />
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
.role-container {
    padding: 20px;
    background: #f0f2f5;
    min-height: 100vh;
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
}
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #fff none;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}
</style>
