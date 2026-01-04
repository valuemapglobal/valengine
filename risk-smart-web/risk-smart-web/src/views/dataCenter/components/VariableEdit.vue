<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { getRecordList } from '@/api/dataCenter/metadata'

const props = defineProps<{
  variable: any
}>()

// --- State ---
const loading = ref(false)
const variableList = ref<any[]>([])
const total = ref(0)
const drawerVisible = ref(false)
const title = ref('属性详情')
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  groupId: undefined as string | undefined,
  themeId: undefined
})

const form = reactive<any>({})

const dataTypeMap: Record<number, string> = {
  0: '对象',
  1: '集合',
  2: '字符',
  3: '布尔',
  4: '整数',
  5: '浮点数'
}

// --- Methods ---
const getList = async () => {
    if (!queryParams.groupId) return
    loading.value = true
    try {
        const res = await getRecordList(queryParams)
        if (res.code === 200) {
            variableList.value = res.data.rows || res.data.list
            total.value = res.data.total
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleUpdate = (row: any) => {
    Object.assign(form, row)
    drawerVisible.value = true
}

const handleSizeChange = (val: number) => {
    queryParams.pageSize = val
    getList()
}

const handleCurrentChange = (val: number) => {
    queryParams.pageNum = val
    getList()
}

watch(() => props.variable, (newVal) => {
    if (newVal) {
        // Need to check which field holds the ID.
        // Metadata (Group): id ? no, legacy used `groupNo` or `id`?
        // Legacy variable.vue: `this.queryParams.groupId = this.variable.groupNo;`
        // Also checks `this.$route.params.id`.
        // Let's assume `groupNo` or `id` or `no`.
        queryParams.groupId = newVal.groupNo || newVal.no || newVal.id || newVal.analysisIndicatorsObjectNo // adapting to various objects
        queryParams.pageNum = 1
        getList()
    }
}, { immediate: true, deep: true })

</script>

<template>
  <div class="variable-container">
     <el-table v-loading="loading" :data="variableList" border>
        <el-table-column prop="code" label="Code" />
        <el-table-column prop="name" label="变量名称" />
        <el-table-column prop="type" label="变量类型">
            <template #default="{ row }">
                {{ dataTypeMap[row.type] || row.type }}
            </template>
        </el-table-column>
        <el-table-column prop="parentName" label="父对象" />
        <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
                <el-button link type="primary" @click="handleUpdate(row)">详情</el-button>
            </template>
        </el-table-column>
     </el-table>
     
     <div class="pagination">
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

     <el-drawer v-model="drawerVisible" :title="title" size="40%">
        <el-form :model="form" label-width="100px" disabled>
            <el-form-item label="Code">
                <el-input v-model="form.code" />
            </el-form-item>
            <el-form-item label="变量名称">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="变量类型">
                <el-input :model-value="dataTypeMap[form.type] || form.type" />
            </el-form-item>
            <el-form-item label="父对象">
                <el-input v-model="form.parentName" />
            </el-form-item>
        </el-form>
     </el-drawer>
  </div>
</template>

<style scoped>
.variable-container {
    padding: 10px;
}
.pagination {
    margin-top: 15px;
    display: flex;
    justify-content: flex-end;
}
</style>
