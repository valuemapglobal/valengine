<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getCategoryList, objectList } from '@/api/dataCenter/indicator'
import VariableEdit from '../components/VariableEdit.vue'

// --- State ---
const loadingCategory = ref(false)
const categoryList = ref<any[]>([])
const totalCategory = ref(0)
const currentCategory = ref<any>(null)

const loadingObject = ref(false)
const objectListRef = ref<any[]>([]) // Renamed to avoid confusion with API 'objectList'
const totalObject = ref(0)
const currentObject = ref<any>(null)

const showVariableDrawer = ref(false)

const categoryQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  packageType: 0
})

const objectQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  analysisIndicatorsNo: undefined as string | undefined
})

// --- Methods ---
const getCategories = async () => {
    loadingCategory.value = true
    try {
        const res = await getCategoryList(categoryQuery)
        if (res.code === 200) {
            categoryList.value = res.data.rows
            totalCategory.value = res.data.total
            if (categoryList.value.length > 0 && !currentCategory.value) {
                handleCategorySelect(categoryList.value[0])
            }
        }
    } catch(e) { console.error(e) }
    finally { loadingCategory.value = false }
}

const getObjects = async () => {
    if (!objectQuery.analysisIndicatorsNo) return
    loadingObject.value = true
    try {
        const res = await objectList(objectQuery)
        if (res.code === 200) {
            objectListRef.value = res.data.rows
            totalObject.value = res.data.total
        }
    } catch(e) { console.error(e) }
    finally { loadingObject.value = false }
}

const handleCategorySelect = (row: any) => {
    currentCategory.value = row
    objectQuery.analysisIndicatorsNo = row.analysisIndicatorsNo
    objectQuery.pageNum = 1
    getObjects()
}

const handleViewProperties = (row: any) => {
    currentObject.value = row
    showVariableDrawer.value = true
}

onMounted(() => {
    getCategories()
})
</script>

<template>
  <div class="data-center-container">
     <!-- Left: Category List -->
     <div class="left-panel">
        <div class="panel-header">指标类型</div>
        <el-table 
            v-loading="loadingCategory" 
            :data="categoryList" 
            border 
            highlight-current-row
            @row-click="handleCategorySelect"
            style="flex: 1"
        >
            <el-table-column prop="categoryName" label="指标类型名称" />
            <el-table-column prop="categoryCode" label="Code" />
        </el-table>
        <el-pagination 
            small
            layout="prev, pager, next" 
            :total="totalCategory"
            v-model:current-page="categoryQuery.pageNum"
            v-model:page-size="categoryQuery.pageSize"
            @current-change="getCategories"
            class="mini-pagination"
        />
     </div>

     <!-- Right: Object List -->
     <div class="right-panel">
        <div class="panel-header">指标对象</div>
        <el-table v-loading="loadingObject" :data="objectListRef" border style="flex: 1">
             <el-table-column prop="objectCode" label="Code" />
             <el-table-column prop="objectName" label="名称" />
             <el-table-column label="操作" width="150" align="center">
                 <template #default="{ row }">
                     <el-button link type="primary" @click="handleViewProperties(row)">查看属性</el-button>
                 </template>
             </el-table-column>
        </el-table>
        <el-pagination 
            layout="total, prev, pager, next"
            :total="totalObject"
            v-model:current-page="objectQuery.pageNum"
            v-model:page-size="objectQuery.pageSize"
            @current-change="getObjects"
            class="pagination"
        />
     </div>

     <el-drawer v-model="showVariableDrawer" title="设置指标规则" size="50%">
         <VariableEdit :variable="currentObject" v-if="showVariableDrawer" />
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
.mini-pagination { margin-top: 10px; justify-content: center; }
.pagination { margin-top: 10px; justify-content: flex-end; }
</style>
