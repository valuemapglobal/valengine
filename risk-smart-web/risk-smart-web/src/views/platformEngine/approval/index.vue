<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  getPendingTasks,
  submitApproval
} from '@/api/platformEngine'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- State ---
const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
// const activeTab = ref('pending') 

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  taskNo: ''
})

// --- Methods ---
const getList = async () => {
    loading.value = true
    try {
        const res = await getPendingTasks(queryParams)
        if (res.code === 200) {
            // Adjust based on actual API response structure (list vs page)
            if (Array.isArray(res.data)) {
                tableData.value = res.data
                total.value = res.data.length
            } else {
                 tableData.value = res.data.list
                 total.value = res.data.total
            }
        }
    } catch(e) { console.error(e) }
    finally { loading.value = false }
}

const handleApprove = (row: any) => {
     ElMessageBox.prompt('请输入审批意见', '审批通过', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
    }).then(async ({ value }) => {
        try {
            await submitApproval({ 
                taskId: row.taskId, 
                result: 1, // Pass
                comment: value 
            })
            ElMessage.success('审批成功')
            getList()
        } catch(e) {}
    })
}

const handleReject = (row: any) => {
    ElMessageBox.prompt('请输入驳回原因', '审批驳回', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea'
    }).then(async ({ value }) => {
         try {
            await submitApproval({ 
                taskId: row.taskId, 
                result: 2, // Reject
                comment: value 
            })
            ElMessage.success('驳回成功')
            getList()
        } catch(e) {}
    })
}

onMounted(() => {
    getList()
})
</script>

<template>
  <div class="task-approval-container">
      <div class="header">
          <h3>待办任务</h3>
          <el-button type="primary" @click="getList">刷新</el-button>
      </div>
      
      <div class="table-card">
          <el-table v-loading="loading" :data="tableData" style="width: 100%">
              <el-table-column prop="taskNo" label="任务编号" />
              <el-table-column prop="policyName" label="流程名称" />
              <el-table-column prop="nodeName" label="当前节点" />
              <el-table-column prop="applicant" label="申请人" />
              <el-table-column prop="createTime" label="申请时间" />
              <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                      <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
                      <el-button type="danger" size="small" @click="handleReject(row)">驳回</el-button>
                  </template>
              </el-table-column>
          </el-table>
      </div>
  </div>
</template>

<style scoped lang="scss">
.task-approval-container {
    padding: 20px;
    background: #f0f2f5;
    min-height: 100vh;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.table-card {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
}
</style>
