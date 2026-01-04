<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  findSourceInfo,
  saveSourceInfo,
  updateSourceInfo,
  removeSourceInfo,
  type SourceInfoParams
} from '@/api/interface/dataScenario'

// --- State ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive<SourceInfoParams>({
  pageNum: 1,
  pageSize: 10,
  sourceName: ''
})

// Dialog
const dialogVisible = ref(false)
const dialogTitle = ref('新增数据场景')
const formRef = ref()
const formData = reactive({
  id: undefined,
  dataName: '', // Interface Source Name ? Legacy used dataName/sourceName inconsistently?
  // Checking legacy DataDuditing.vue: item.dataName
  // Checking legacy dataList.js: saveSourceInfo(data)
  // Let's assume 'dataName' is the displayed name, and 'interfaceSourceNo' is ID?
  // We'll stick to legacy property names if possible.
  interfaceSourceNo: undefined,
  interfaceDataType: 0 // Default type
})

const rules = {
  dataName: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

const dataTypeOptions = [
  { value: 0, label: '元数据' },
  { value: 1, label: '特征变量' },
  { value: 2, label: '分析指标' },
]

// --- Methods ---

const getList = async () => {
  loading.value = true
  try {
    const res = await findSourceInfo(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const handleReset = () => {
  queryParams.sourceName = ''
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增数据场景'
  formData.id = undefined
  formData.interfaceSourceNo = undefined
  formData.dataName = ''
  formData.interfaceDataType = 0
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑数据场景'
  formData.id = row.id // Assuming there is an ID or we use interfaceSourceNo
  formData.interfaceSourceNo = row.interfaceSourceNo
  formData.dataName = row.dataName
  formData.interfaceDataType = row.interfaceDataType
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('是否确认删除该数据场景?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await removeSourceInfo({ sourceNo: row.interfaceSourceNo })
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  })
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formData.interfaceSourceNo && dialogTitle.value.includes('编辑')) { // Logic adjustment required based on actual API behavior for ID
           // Legacy updateSourceInfo
           // Assuming update needs sourceNo and other fields
           await updateSourceInfo(formData)
           ElMessage.success('修改成功')
        } else {
           await saveSourceInfo(formData)
           ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="app-container">
    <!-- Search -->
    <div class="search-wrapper">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="名称">
          <el-input
            v-model="queryParams.sourceName"
            placeholder="请输入名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增场景</el-button>
      </div>
    </div>

    <!-- Table -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="dataName" label="场景名称" />
      <el-table-column prop="interfaceSourceNo" label="编号" />
      <el-table-column prop="interfaceDataType" label="类型">
        <template #default="{ row }">
            <el-tag>{{ dataTypeOptions.find(o => o.value === row.interfaceDataType)?.label || '未知' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination-container">
      <el-pagination
        v-if="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="dataName">
          <el-input v-model="formData.dataName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="类型" prop="interfaceDataType">
           <el-select v-model="formData.interfaceDataType" placeholder="请选择">
              <el-option v-for="item in dataTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
           </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #fff;
  height: 100%;
}
.search-wrapper {
  display: flex;
  justify-content: space-between;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
