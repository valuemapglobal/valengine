<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  listMenu,
  addMenu,
  updateMenu,
  delMenu,
  getMenu
} from '@/api/system/menu'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'

// --- State ---
const loading = ref(false)
const menuList = ref<any[]>([])
const queryParams = reactive({
  menuName: '',
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({
  menuId: undefined,
  parentId: 0,
  menuType: 'M',
  icon: '',
  menuName: '',
  orderNum: 0,
  path: '',
  component: '',
  perms: '',
  visible: '0',
  status: '0' 
})

const menuOptions = ref<any[]>([])

// --- Methods ---
const getList = async () => {
    loading.value = true
    try {
        const res = await listMenu(queryParams)
        if (res.code === 200) {
            menuList.value = handleTree(res.data, 'menuId')
        }
    } catch(e) { console.error(e) }
    finally { loading.value = false }
}

const handleTree = (data: any[], id: string, parentId = 'parentId', children = 'children') => {
    // Simplified tree builder if backend returns flat list
    // Assuming backend returns flat list based on legacy code analysis
    let config = {
      id: id || 'id',
      parentId: parentId || 'parentId',
      childrenList: children || 'children'
    }
    
    var childrenListMap: any = {}
    var nodeIds: any = {}
    var tree: any[] = []

    for (let d of data) {
      let parentId = d[config.parentId]
      if (childrenListMap[parentId] == null) {
        childrenListMap[parentId] = []
      }
      nodeIds[d[config.id]] = d
      childrenListMap[parentId].push(d)
    }

    for (let d of data) {
      let parentId = d[config.parentId]
      if (nodeIds[parentId] == null) {
        tree.push(d)
      }
    }

    for (let t of tree) {
      adaptToChildrenList(t)
    }

    function adaptToChildrenList(o: any) {
      if (childrenListMap[o[config.id]] !== null) {
        o[config.childrenList] = childrenListMap[o[config.id]]
      }
      if (o[config.childrenList]) {
        for (let c of o[config.childrenList]) {
          adaptToChildrenList(c)
        }
      }
    }
    return tree
}

const handleSearch = () => {
    getList()
}

const handleReset = () => {
    queryParams.menuName = ''
    queryParams.status = undefined
    handleSearch()
}

const handleAdd = (row?: any) => {
    resetForm()
    if (row && row.menuId) {
        form.parentId = row.menuId
    }
    dialogTitle.value = '新增菜单'
    dialogVisible.value = true
    // Reload menu options for tree select
    menuOptions.value = [{ menuId: 0, menuName: '主类目', children: menuList.value }]
}

const handleEdit = async (row: any) => {
    resetForm()
    dialogTitle.value = '修改菜单'
    try {
        const res = await getMenu(row.menuId)
        if (res.code === 200) {
             Object.assign(form, res.data)
             dialogVisible.value = true
             menuOptions.value = [{ menuId: 0, menuName: '主类目', children: menuList.value }]
        }
    } catch(e) {}
}

const handleDelete = (row: any) => {
    ElMessageBox.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?', '警告', {
        type: 'warning'
    }).then(async () => {
        await delMenu(row.menuId)
        ElMessage.success('删除成功')
        getList()
    })
}

const submitForm = async () => {
    if (!form.menuName) {
        ElMessage.warning('请输入菜单名称')
        return
    }
    try {
        if (form.menuId) {
            await updateMenu(form)
            ElMessage.success('修改成功')
        } else {
            await addMenu(form)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
    } catch(e) {}
}

const resetForm = () => {
    Object.assign(form, {
        menuId: undefined,
        parentId: 0,
        menuType: 'M',
        icon: '',
        menuName: '',
        orderNum: 0,
        path: '',
        component: '',
        perms: '',
        visible: '0',
        status: '0' 
    })
}

onMounted(() => {
    getList()
})
</script>

<template>
  <div class="menu-container">
      <div class="search-bar">
          <el-form :inline="true" :model="queryParams">
              <el-form-item label="菜单名称">
                  <el-input v-model="queryParams.menuName" placeholder="请输入" clearable @keyup.enter="handleSearch" />
              </el-form-item>
              <el-form-item label="状态">
                  <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
                      <el-option label="正常" value="0" />
                      <el-option label="停用" value="1" />
                  </el-select>
              </el-form-item>
              <el-form-item>
                  <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
                  <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                  <el-button type="primary" plain :icon="Plus" @click="handleAdd()">新增</el-button>
              </el-form-item>
          </el-form>
      </div>
      
      <div class="table-content">
          <el-table
            v-loading="loading"
            :data="menuList"
            row-key="menuId"
            :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
          >
              <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160" />
              <el-table-column prop="icon" label="图标" align="center" width="100" />
              <el-table-column prop="orderNum" label="排序" width="60" />
              <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true" />
              <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" />
              <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                      <el-tag v-if="row.status === '0'" type="success">正常</el-tag>
                      <el-tag v-else type="danger">停用</el-tag>
                  </template>
              </el-table-column>
              <el-table-column label="创建时间" align="center" prop="createTime" />
              <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                  <template #default="{ row }">
                      <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">修改</el-button>
                      <el-button link type="primary" :icon="Plus" @click="handleAdd(row)">新增</el-button>
                      <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
                  </template>
              </el-table-column>
          </el-table>
      </div>

      <!-- Add/Edit Dialog -->
      <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
          <el-form ref="menuForm" :model="form" label-width="100px">
               <el-form-item label="上级菜单">
                   <el-tree-select
                      v-model="form.parentId"
                      :data="menuOptions"
                      :props="{ label: 'menuName', children: 'children' }"
                      value-key="menuId"
                      placeholder="选择上级菜单"
                      check-strictly
                   />
               </el-form-item>
               <el-form-item label="菜单类型" prop="menuType">
                    <el-radio-group v-model="form.menuType">
                        <el-radio label="M">目录</el-radio>
                        <el-radio label="C">菜单</el-radio>
                        <el-radio label="F">按钮</el-radio>
                    </el-radio-group>
               </el-form-item>
               <el-form-item label="菜单名称" prop="menuName">
                   <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
               </el-form-item>
               <el-form-item label="路由地址" prop="path" v-if="form.menuType != 'F'">
                   <el-input v-model="form.path" placeholder="请输入路由地址" />
               </el-form-item>
               <el-form-item label="组件路径" prop="component" v-if="form.menuType == 'C'">
                   <el-input v-model="form.component" placeholder="请输入组件路径" />
               </el-form-item>
               <el-form-item label="权限字符" prop="perms" v-if="form.menuType != 'M'">
                   <el-input v-model="form.perms" placeholder="请输入权限标识" />
               </el-form-item>
                <el-form-item label="显示排序" prop="orderNum">
                   <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
               </el-form-item>
               <el-form-item label="菜单状态" prop="status">
                   <el-radio-group v-model="form.status">
                        <el-radio label="0">正常</el-radio>
                        <el-radio label="1">停用</el-radio>
                    </el-radio-group>
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
.menu-container {
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
</style>
