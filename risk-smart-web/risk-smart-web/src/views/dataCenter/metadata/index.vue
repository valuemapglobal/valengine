<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getThemeList, getGroupList } from '@/api/dataCenter/metadata'
import VariableEdit from '../components/VariableEdit.vue'

// --- State ---
const loadingTheme = ref(false)
const themeList = ref<any[]>([])
const totalTheme = ref(0)
const currentTheme = ref<any>(null)

const loadingGroup = ref(false)
const groupList = ref<any[]>([])
const totalGroup = ref(0)
const currentGroup = ref<any>(null)

const showVariableDrawer = ref(false)

const themeQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  packageType: 0 // Default from legacy
})

const groupQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  themeNo: undefined as string | undefined
})

const dataTypeMap: Record<number, string> = {
    0: '数值', 1: '字符串', 2: '日期', 3: '对象', 4: '数组', 5: '文件'
}

// --- Methods ---
const getThemes = async () => {
    loadingTheme.value = true
    try {
        const res = await getThemeList(themeQuery)
        if (res.code === 200) {
            themeList.value = res.data.rows
            totalTheme.value = res.data.total
            // Select first one if available
            if (themeList.value.length > 0 && !currentTheme.value) {
                handleThemeSelect(themeList.value[0])
            }
        }
    } catch(e) { console.error(e) } 
    finally { loadingTheme.value = false }
}

const getGroups = async () => {
    if (!groupQuery.themeNo) return
    loadingGroup.value = true
    try {
        const res = await getGroupList(groupQuery)
        if (res.code === 200) {
            groupList.value = res.data.rows
            totalGroup.value = res.data.total
        }
    } catch(e) { console.error(e) }
    finally { loadingGroup.value = false }
}

const handleThemeSelect = (row: any) => {
    currentTheme.value = row
    groupQuery.themeNo = row.themeNo // Adjust ID field if needed
    groupQuery.pageNum = 1
    getGroups()
}

const handleThemePageChange = (val: number) => {
    themeQuery.pageNum = val
    getThemes()
}

const handleGroupPageChange = (val: number) => {
    groupQuery.pageNum = val
    getGroups()
}

const handleViewProperties = (row: any) => {
    currentGroup.value = row
    showVariableDrawer.value = true
}

onMounted(() => {
    getThemes()
})
</script>

<template>
  <div class="data-center-container">
     <!-- Left: Theme List -->
     <div class="left-panel">
        <div class="panel-header">主题</div>
        <el-table 
            v-loading="loadingTheme" 
            :data="themeList" 
            border 
            highlight-current-row
            @row-click="handleThemeSelect"
            style="flex: 1"
        >
            <el-table-column prop="name" label="主题名称" />
            <el-table-column prop="keycode" label="包名称" />
        </el-table>
        <el-pagination 
            small
            layout="prev, pager, next" 
            :total="totalTheme"
            v-model:current-page="themeQuery.pageNum"
            :page-size="themeQuery.pageSize"
            @current-change="handleThemePageChange"
            class="mini-pagination"
        />
     </div>

     <!-- Right: Group List -->
     <div class="right-panel">
        <div class="panel-header">对象</div>
        <el-table v-loading="loadingGroup" :data="groupList" border style="flex: 1">
             <el-table-column prop="keycode" label="Code" />
             <el-table-column prop="name" label="名称" />
             <el-table-column prop="interfaceVersion" label="版本号" width="100" />
             <el-table-column prop="type" label="类型" width="100">
                 <template #default="{ row }">{{ dataTypeMap[row.type] || row.type }}</template>
             </el-table-column>
             <el-table-column label="操作" width="150" align="center">
                 <template #default="{ row }">
                     <el-button link type="primary" @click="handleViewProperties(row)">查看属性</el-button>
                     <!-- Other actions like Edit/Details can be added here -->
                 </template>
             </el-table-column>
        </el-table>
        <el-pagination 
            layout="total, prev, pager, next"
            :total="totalGroup"
            v-model:current-page="groupQuery.pageNum"
            :page-size="groupQuery.pageSize"
            @current-change="handleGroupPageChange"
            class="pagination"
        />
     </div>

     <!-- Properties Drawer -->
     <el-drawer v-model="showVariableDrawer" title="查看属性" size="50%">
         <VariableEdit :variable="currentGroup" v-if="showVariableDrawer" />
     </el-drawer>
  </div>
</template>

<style scoped lang="scss">
.data-center-container {
    display: flex;
    height: calc(100vh - 84px);
    padding: 20px;
    gap: 20px;
    background-color: #f0f2f5;
}

.left-panel, .right-panel {
    background: #fff;
    display: flex;
    flex-direction: column;
    border-radius: 4px;
    padding: 10px;
}

.left-panel { flex: 0 0 400px; }
.right-panel { flex: 1; }

.panel-header {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
}

.mini-pagination {
    margin-top: 10px;
    justify-content: center;
}

.pagination {
    margin-top: 10px;
    justify-content: flex-end;
}
</style>
