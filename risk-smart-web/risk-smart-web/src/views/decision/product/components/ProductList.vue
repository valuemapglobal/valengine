<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useDataRiskStore } from '@/store/modules/dataRisk'
import {
  product_search,
  newlyAddProduct,
  editProduct,
  deleteProduct
} from '@/api/decision/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, RefreshRight } from '@element-plus/icons-vue'

const store = useDataRiskStore()

// --- State ---
const queryParams = reactive({
  name: '',
  pageNum: 1,
  pageSize: 20
})
const loading = ref(false)
const total = ref(0)
const dataList = ref<any[]>([])
const activeIndex = ref(0)
const showLoadMore = ref(false)

// Dialog State
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formName = ref('')
const editId = ref<string | number | undefined>(undefined)

// --- Computed ---
const isLoadMore = computed(() => {
    return queryParams.pageNum * queryParams.pageSize < total.value
})

// --- Methods ---
const getList = async (append = false) => {
    if (!append) {
        loading.value = true
        dataList.value = [] // Clear list on new search
    }
    
    try {
        const res = await product_search(queryParams)
        if (res.code === 200) {
            const newList = res.data.list.map((item: any) => ({
                ...item,
                productName: item.name
            }))
            
            if (append) {
                dataList.value = dataList.value.concat(newList)
            } else {
                dataList.value = newList
                // Auto-select first item
                if (dataList.value.length > 0) {
                    handleSelect(dataList.value[0], 0)
                }
            }
            
            total.value = res.data.total
            store.setProductList(dataList.value)
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
        showLoadMore.value = false
    }
}

const handleSearch = () => {
    queryParams.pageNum = 1
    getList()
}

const handleSelect = (item: any, index: number) => {
    activeIndex.value = index
    store.changeProductDecision({ projectCode: item.id })
    // If we had lock check logic, it would go here
}

const handleLoadMore = () => {
    if (isLoadMore.value) {
        showLoadMore.value = true
        queryParams.pageNum++
        getList(true)
    }
}

// Check scroll for load more
const handleScroll = (e: Event) => {
    const target = e.target as HTMLElement
    if (target.scrollTop + target.clientHeight >= target.scrollHeight - 10) {
        if (isLoadMore.value && !showLoadMore.value) {
            handleLoadMore()
        }
    }
}

// Add/Edit/Delete
const handleAdd = () => {
    dialogTitle.value = '新增产品'
    formName.value = ''
    editId.value = undefined
    dialogVisible.value = true
}

const handleEdit = (item: any) => {
    dialogTitle.value = '编辑产品'
    formName.value = item.productName
    editId.value = item.id
    dialogVisible.value = true
}

const handleDelete = (item: any) => {
    ElMessageBox.confirm('是否确认删除?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const res = await deleteProduct(item.id)
            if (res.code === 200) {
                ElMessage.success('删除成功')
                handleSearch()
            } else {
                ElMessage.warning(res.msg || '删除失败')
            }
        } catch(e) {}
    })
}

const submitForm = async () => {
    if (!formName.value) {
        ElMessage.warning('请输入产品名称')
        return
    }
    
    try {
        let res
        if (dialogTitle.value === '新增产品') {
            res = await newlyAddProduct({ name: formName.value })
        } else {
            res = await editProduct({ name: formName.value, id: editId.value })
        }
        
        if (res.code === 200) {
            ElMessage.success(`${dialogTitle.value}成功`)
            dialogVisible.value = false
            handleSearch()
        }
    } catch (e) { console.error(e) }
}

onMounted(() => {
    getList()
})
</script>

<template>
  <div class="product-list-container">
     <div class="search-bar">
         <div class="input-wrapper">
             <el-input 
                v-model="queryParams.name" 
                placeholder="请输入" 
                :prefix-icon="Search"
                @keyup.enter="handleSearch"
                clearable
                @clear="handleSearch"
             />
         </div>
         <div class="add-btn" @click="handleAdd">
             <el-icon><Plus /></el-icon>
         </div>
     </div>
     
     <div class="list-content" v-loading="loading" @scroll="handleScroll">
         <div 
            v-for="(item, index) in dataList" 
            :key="item.id"
            class="product-item"
            :class="{ active: activeIndex === index }"
            @click="handleSelect(item, index)"
         >
             <div class="product-name">{{ item.productName }}</div>
             <div class="actions">
                 <el-icon class="action-icon" @click.stop="handleEdit(item)"><Edit /></el-icon>
                 <el-icon class="action-icon" @click.stop="handleDelete(item)"><Delete /></el-icon>
             </div>
         </div>
         
         <div class="load-more" v-if="showLoadMore">
             <el-icon class="is-loading"><RefreshRight /></el-icon> 加载更多...
         </div>
         <div class="no-more" v-if="!isLoadMore && dataList.length > 0">
             没有更多了
         </div>
     </div>
     
     <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
         <el-input v-model="formName" placeholder="请输入产品名称" />
         <template #footer>
             <el-button @click="dialogVisible = false">取消</el-button>
             <el-button type="primary" @click="submitForm">确定</el-button>
         </template>
     </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.product-list-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: #fff;
    border-right: 1px solid #eee;
}

.search-bar {
    padding: 15px;
    display: flex;
    gap: 10px;
    background: #f5f7fa;
    border-bottom: 1px solid #eee;
    
    .input-wrapper {
        flex: 1;
    }
    
    .add-btn {
        width: 32px;
        height: 32px;
        background: #ecf5ff;
        color: #409eff;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        cursor: pointer;
        &:hover {
            background: #409eff;
            color: #fff;
        }
    }
}

.list-content {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    
    .product-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 15px;
        margin-bottom: 8px;
        border-radius: 6px;
        cursor: pointer;
        background: #f9f9f9;
        transition: all 0.2s;
        border: 1px solid transparent;
        
        &.active {
            background: #ecf5ff;
            color: #409eff;
            border-color: #b3d8ff;
            .actions { opacity: 1; }
        }
        
        &:hover {
            background: #ecf5ff;
            .actions { opacity: 1; }
        }
        
        .product-name {
            flex: 1;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 14px;
        }
        
        .actions {
            opacity: 0;
            display: flex;
            gap: 8px;
            transition: opacity 0.2s;
            
            .action-icon {
                padding: 4px;
                border-radius: 4px;
                &:hover {
                   background: rgba(0,0,0,0.1); 
                }
            }
        }
    }
}

.load-more, .no-more {
    text-align: center;
    padding: 10px;
    color: #909399;
    font-size: 12px;
}
</style>
