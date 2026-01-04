<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  policyList,
  updatePolicyStatus,
  deletePolicy,
  getBusinessScenario
} from '@/api/platformEngine'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'

// --- State ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const businessOptions = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  policyName: '',
  businessCode: undefined,
  status: undefined
})

 
// Used in template/logic


// --- Methods ---
const initData = async () => {
    try {
        const res = await getBusinessScenario()
        if (res.code === 200) {
            businessOptions.value = res.data.list || []
        }
    } catch(e) {}
    getList()
}

const getList = async () => {
    loading.value = true
    try {
        const res = await policyList(queryParams)
        if (res.code === 200) {
            tableData.value = res.data.list
            total.value = res.data.total
        }
    } catch(e) { console.error(e) }
    finally { loading.value = false }
}

const handleSearch = () => {
    queryParams.pageNum = 1
    getList()
}

const handleReset = () => {
    queryParams.policyName = ''
    queryParams.businessCode = undefined
    queryParams.status = undefined
    handleSearch()
}

const handleAdd = () => {
    ElMessage.info('流程设计器功能将在后续版本迁移')
}

const handleEdit = (row: any) => {
    console.log(row)
    ElMessage.info('流程设计器功能将在后续版本迁移')
}

const handleStatusChange = async (row: any) => {
    try {
        await updatePolicyStatus({ id: row.id, status: row.status })
        ElMessage.success('状态更新成功')
    } catch(e) {
        row.status = row.status === 1 ? 0 : 1 // Revert on fail
    }
}

const handleDelete = (row: any) => {
    ElMessageBox.confirm('确认删除该流程策略?', '警告', {
        type: 'warning'
    }).then(async () => {
        try {
            await deletePolicy({ ids: [row.id] })
            ElMessage.success('删除成功')
            getList()
        } catch(e) {}
    })
}

onMounted(() => {
    initData()
})
</script>

<template>
  <div class="workflow-engine-container">
      <div class="search-bar">
          <el-form :inline="true" :model="queryParams">
              <el-form-item label="策略名称">
                  <el-input v-model="queryParams.policyName" placeholder="请输入" clearable />
              </el-form-item>
              <el-form-item label="业务场景">
                   <el-select v-model="queryParams.businessCode" placeholder="请选择" clearable>
                       <el-option v-for="opt in businessOptions" :key="opt.id" :label="opt.name" :value="opt.id" />
                   </el-select>
              </el-form-item>
              <el-form-item>
                  <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
                  <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                  <el-button type="success" :icon="Plus" @click="handleAdd">新增流程</el-button>
              </el-form-item>
          </el-form>
      </div>
      
      <div class="table-content">
          <el-table v-loading="loading" :data="tableData" border style="width: 100%">
              <el-table-column prop="policyName" label="流程名称" />
              <el-table-column prop="businessName" label="业务场景" />
              <el-table-column prop="version" label="版本号" width="100" />
              <el-table-column prop="createTime" label="创建时间" />
              <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                      <el-switch 
                        v-model="row.status" 
                        :active-value="1" 
                        :inactive-value="0"
                        @change="handleStatusChange(row)"
                      />
                  </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center">
                  <template #default="{ row }">
                      <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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
  </div>
</template>

<style scoped lang="scss">
.workflow-engine-container {
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
</style>
