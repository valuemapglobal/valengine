<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { findAllLog } from '@/api/interface/audit'
import { findSourceInfo } from '@/api/interface/dataScenario'

// --- State ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const sourceList = ref<any[]>([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  interfaceNameZh: '', // Interface Name
  sourceNo: '', // Scenario ID
  state: undefined // Status
})

// --- Methods ---
const initData = async () => {
    // Fetch Source List for Filter
    const res = await findSourceInfo({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
        sourceList.value = res.data.list
        // Optionally select first one default? Or allow 'All'?
        // Legacy DataDuditing.vue forced selection of first source.
        if (sourceList.value.length > 0) {
            queryParams.sourceNo = sourceList.value[0].interfaceSourceNo
            getList()
        }
    }
}

const getList = async () => {
  if (!queryParams.sourceNo) return
  loading.value = true
  try {
    const res = await findAllLog(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

// Map SourceNo change to refresh
const handleSourceChange = () => {
    handleQuery()
}

onMounted(() => {
  initData()
})
</script>

<template>
  <div class="app-container">
    <div class="search-bar">
       <el-form :inline="true" :model="queryParams">
         <el-form-item label="数据场景">
            <el-select v-model="queryParams.sourceNo" placeholder="请选择场景" @change="handleSourceChange" style="width: 200px">
               <el-option v-for="s in sourceList" :key="s.interfaceSourceNo" :label="s.dataName" :value="s.interfaceSourceNo" />
            </el-select>
         </el-form-item>
         <el-form-item label="接口名称">
           <el-input v-model="queryParams.interfaceNameZh" placeholder="请输入" clearable />
         </el-form-item>
         <el-form-item>
           <el-button type="primary" @click="handleQuery">查询</el-button>
         </el-form-item>
       </el-form>
    </div>

    <el-table v-loading="loading" :data="tableData" border style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="interfaceNameZh" label="接口名称" />
      <el-table-column prop="createBy" label="用户" width="120" />
      <el-table-column prop="accessTime" label="调用时间" width="180" />
      <el-table-column prop="resultTime" label="响应时间" width="120" />
      <el-table-column prop="ip" label="IP地址" width="140" />
      <el-table-column prop="code" label="状态" width="100" align="center">
         <template #default="{ row }">
            <el-tag :type="row.code == 200 ? 'success' : 'danger'">{{ row.code == 200 ? '成功' : '失败' }}</el-tag>
         </template>
      </el-table-column>
    </el-table>

    <div class="pagination-footer">
      <el-pagination
        v-if="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.app-container {
  padding: 20px;
  background: #fff;
  height: 100%;
}
.pagination-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
