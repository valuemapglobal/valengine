<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useDataRiskStore } from '@/store/modules/dataRisk'
import {
  business_search,
  newlyAddBusiness,
  editBusiness,
  deleteBusiness
} from '@/api/decision/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const store = useDataRiskStore()

// --- State ---
const tabs = ref<any[]>([])
const activeTab = ref<string | number>('')
const loading = ref(false)

// Add/Edit State
const isAdd = ref(false)
const addValue = ref('')
const inputRef = ref<HTMLInputElement | null>(null)

// Dialog State
const dialogVisible = ref(false)
const editValue = ref('')
const editId = ref<string | number | undefined>(undefined)

// --- Methods ---
const getTabs = async () => {
    loading.value = true
    try {
        const res = await business_search()
        if (res.code === 200) {
            tabs.value = res.data.list.map((item: any) => ({
                ...item,
                disabled: false
            }))
            
            if (tabs.value.length > 0) {
                // Select first tab or keep active if exists
                if (!activeTab.value || !tabs.value.find(t => t.id === activeTab.value)) {
                    handleTabClick(tabs.value[0])
                }
            } else {
                activeTab.value = ''
            }
        }
    } catch(e) { console.error(e) }
    finally { loading.value = false }
}

const handleTabClick = (item: any) => {
    activeTab.value = item.id
    store.changeBusiness(item)
    store.changeProductDecision({ businessCode: item.id, businessName: item.name })
}

const handleAddClick = () => {
    isAdd.value = true
    addValue.value = ''
    nextTick(() => {
        if (inputRef.value) inputRef.value.focus()
    })
}

const submitAdd = async () => {
    if (!addValue.value) {
        ElMessage.warning('请输入名称')
        return
    }
    try {
        const res = await newlyAddBusiness({ name: addValue.value })
        if (res.code === 200) {
            ElMessage.success('新增成功')
            isAdd.value = false
            getTabs()
        }
    } catch(e) { console.error(e) }
}

const cancelAdd = () => {
    isAdd.value = false
    addValue.value = ''
}

const handleEdit = (item: any) => {
    editId.value = item.id
    editValue.value = item.name
    dialogVisible.value = true
}

const handleDelete = (item: any) => {
    ElMessageBox.confirm('是否确认删除?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const res = await deleteBusiness(item.id)
            if (res.code === 200) {
                ElMessage.success('删除成功')
                getTabs()
            }
        } catch(e) {}
    })
}

const submitEdit = async () => {
    if (!editValue.value) {
        ElMessage.warning('请输入名称')
        return
    }
    try {
        const res = await editBusiness({ id: editId.value, name: editValue.value })
        if (res.code === 200) {
            ElMessage.success('编辑完成')
            dialogVisible.value = false
            getTabs()
        }
    } catch(e) { console.error(e) }
}

onMounted(() => {
    getTabs()
})
</script>

<template>
  <div class="business-tabs-container">
      <div class="tabs-header">
          <div 
            v-for="item in tabs" 
            :key="item.id"
            class="tab-item"
            :class="{ active: activeTab === item.id }"
            @click="handleTabClick(item)"
          >
              <span class="tab-label">{{ item.name }}</span>
              <div class="tab-actions" v-if="item.deptId != 1">
                  <el-icon class="action-icon" @click.stop="handleEdit(item)"><Edit /></el-icon>
                  <el-icon class="action-icon" @click.stop="handleDelete(item)"><Delete /></el-icon>
              </div>
          </div>
          
          <div class="add-tab" v-if="!isAdd" @click="handleAddClick">
              <el-icon><Plus /></el-icon> 新增
          </div>
          <div class="add-input" v-else>
              <el-input 
                v-model="addValue" 
                ref="inputRef"
                placeholder="名称" 
                size="small"
                @keyup.enter="submitAdd"
                @blur="cancelAdd"
              />
          </div>
      </div>
      
      <div class="tabs-content">
          <slot></slot>
      </div>
      
      <el-dialog v-model="dialogVisible" title="编辑" width="30%">
         <el-input v-model="editValue" placeholder="请输入名称" />
         <template #footer>
             <el-button @click="dialogVisible = false">取消</el-button>
             <el-button type="primary" @click="submitEdit">确定</el-button>
         </template>
     </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.business-tabs-container {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.tabs-header {
    display: flex;
    background: #f5f7fa;
    padding: 0 10px;
    border-bottom: 1px solid #e4e7ed;
    overflow-x: auto;
    flex-shrink: 0;
    
    .tab-item {
        padding: 0 15px;
        height: 40px;
        display: flex;
        align-items: center;
        cursor: pointer;
        border-right: 1px solid #e4e7ed;
        position: relative;
        font-size: 14px;
        color: #909399;
        min-width: 100px;
        justify-content: center;
        
        &.active {
            background: #fff;
            color: #409eff;
            font-weight: 500;
        }
        
        .tab-label {
            margin-right: 5px;
        }
        
        .tab-actions {
            display: none;
            margin-left: 5px;
            .action-icon {
                font-size: 12px;
                margin: 0 2px;
                &:hover { color: #f56c6c; }
            }
        }
        
        &:hover .tab-actions {
            display: flex;
        }
    }
    
    .add-tab {
        padding: 0 15px;
        height: 40px;
        display: flex;
        align-items: center;
        background: #ecf5ff;
        color: #409eff;
        cursor: pointer;
        &:hover {
            opacity: 0.8;
        }
    }
    
    .add-input {
        padding: 0 5px;
        height: 40px;
        display: flex;
        align-items: center;
        width: 120px;
    }
}

.tabs-content {
    flex: 1;
    overflow: hidden;
    background: #fff;
    padding: 20px;
}
</style>
