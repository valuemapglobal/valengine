<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  getTaskRecords,
  getBatchRecords,
  getBusinessScenario
} from '@/api/platformEngine'
import { Search, Refresh } from '@element-plus/icons-vue'

// --- State ---
const activeTab = ref('single') // single, batch
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const businessOptions = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  taskNo: '',
  batchNo: '',
  businessCode: undefined,
  startEndDate: [] as string[]
})

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
        let res
        const params = {
            ...queryParams,
            startTime: queryParams.startEndDate?.[0],
            endTime: queryParams.startEndDate?.[1]
        }
        
        if (activeTab.value === 'single') {
            res = await getTaskRecords(params)
        } else {
            res = await getBatchRecords(params)
        }
        
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
    queryParams.taskNo = ''
    queryParams.batchNo = ''
    queryParams.businessCode = undefined
    queryParams.startEndDate = []
    handleSearch()
}

const handleTabChange = () => {
    handleReset()
}

onMounted(() => {
    initData()
})
</script>

<template>
  <div class="task-records-container">
      <div class="tabs-header">
           <el-radio-group v-model="activeTab" @change="handleTabChange">
              <el-radio-button label="single">单次调用记录</el-radio-button>
              <el-radio-button label="batch">批量调用记录</el-radio-button>
            </el-radio-group>
      </div>
      
      <div class="search-bar">
          <el-form :inline="true" :model="queryParams">
              <el-form-item :label="activeTab === 'single' ? '任务编号' : '批次号'">
                  <el-input 
                    v-model="activeTab === 'single' ? queryParams.taskNo : queryParams.batchNo" 
                    placeholder="请输入" 
                    clearable 
                  />
              </el-form-item>
              <el-form-item label="业务场景">
                   <el-select v-model="queryParams.businessCode" placeholder="请选择" clearable>
                       <el-option v-for="opt in businessOptions" :key="opt.id" :label="opt.name" :value="opt.id" />
                   </el-select>
              </el-form-item>
              <el-form-item label="时间范围">
                  <el-date-picker
                    v-model="queryParams.startEndDate"
                    type="daterange"
                    value-format="YYYY-MM-DD"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                  />
              </el-form-item>
              <el-form-item>
                  <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
                  <el-button :icon="Refresh" @click="handleReset">重置</el-button>
              </el-form-item>
          </el-form>
      </div>
      
      <div class="table-content">
          <el-table v-loading="loading" :data="tableData" border style="width: 100%">
               <el-table-column v-if="activeTab === 'single'" prop="taskNo" label="任务编号" width="200" />
               <el-table-column v-else prop="batchNo" label="批次号" width="200" />
               
               <el-table-column prop="businessName" label="业务场景" width="150" />
               <el-table-column prop="policyName" label="流程策略" width="150" />
               
               <el-table-column v-if="activeTab === 'single'" prop="taskStatus" label="状态" width="100">
                   <template #default="{ row }">
                       <el-tag :type="row.taskStatus === 3 ? 'success' : 'warning'">
                           {{ row.taskStatus === 3 ? '成功' : '处理中/失败' }}
                       </el-tag>
                   </template>
               </el-table-column>
               
               <el-table-column prop="createTime" label="创建时间" width="180" />
               
               <el-table-column label="详情" align="center" fixed="right">
                   <template #default>
                       <el-button link type="primary">查看详情</el-button>
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
.task-records-container {
    padding: 20px;
    background: #f0f2f5;
    min-height: 100vh;
}
.tabs-header {
    margin-bottom: 20px;
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
