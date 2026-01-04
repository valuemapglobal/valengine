<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { findVariablesTopic, findVariablesGroup } from '@/api/dataCenter/variable'
import VariableEdit from '../components/VariableEdit.vue'

// --- State ---
const loadingTopic = ref(false)
const topicList = ref<any[]>([])
const totalTopic = ref(0)
const currentTopic = ref<any>(null)

const loadingGroup = ref(false)
const groupList = ref<any[]>([])
const totalGroup = ref(0)
const currentGroup = ref<any>(null) // Selected Group for Properties

const showVariableDrawer = ref(false)

const topicQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  packageType: 0
})

const groupQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  no: undefined as string | undefined // Topic No
})

// --- Methods ---
const getTopics = async () => {
    loadingTopic.value = true
    try {
        const res = await findVariablesTopic(topicQuery)
        if (res.code === 200) {
            topicList.value = res.data.list
            totalTopic.value = res.data.total
             if (topicList.value.length > 0 && !currentTopic.value) {
                handleTopicSelect(topicList.value[0])
            }
        }
    } catch(e) { console.error(e) }
    finally { loadingTopic.value = false }
}

const getGroups = async () => {
    if (!groupQuery.no) return
    loadingGroup.value = true
    try {
        const res = await findVariablesGroup(groupQuery)
        if (res.code === 200) {
            groupList.value = res.data.list
            totalGroup.value = res.data.total
        }
    } catch(e) { console.error(e) }
    finally { loadingGroup.value = false }
}

const handleTopicSelect = (row: any) => {
    currentTopic.value = row
    groupQuery.no = row.no
    groupQuery.pageNum = 1
    getGroups()
}

const handleViewProperties = (row: any) => {
    currentGroup.value = row
    showVariableDrawer.value = true
}

onMounted(() => {
    getTopics()
})
</script>

<template>
  <div class="data-center-container">
     <!-- Left: Topic List -->
     <div class="left-panel">
        <div class="panel-header">主题</div>
        <el-table 
            v-loading="loadingTopic" 
            :data="topicList" 
            border 
            highlight-current-row
            @row-click="handleTopicSelect"
            style="flex: 1"
        >
            <el-table-column prop="topicName" label="主题名称" />
            <el-table-column prop="topicPackage" label="包名称" />
        </el-table>
        <el-pagination 
            small
            layout="prev, pager, next" 
            :total="totalTopic"
            v-model:current-page="topicQuery.pageNum"
            v-model:page-size="topicQuery.pageSize"
            @current-change="getTopics"
            class="mini-pagination"
        />
     </div>

     <!-- Right: Group List -->
     <div class="right-panel">
        <div class="panel-header">分组</div>
        <el-table v-loading="loadingGroup" :data="groupList" border style="flex: 1">
             <el-table-column prop="groupCode" label="Code" />
             <el-table-column prop="groupName" label="名称" />
             <el-table-column prop="associatedInterfaces" label="接口关联" />
             <el-table-column prop="interfaceVersion" label="版本号" width="100" />
             <el-table-column label="操作" width="150" align="center">
                 <template #default="{ row }">
                     <el-button link type="primary" @click="handleViewProperties(row)">查看属性</el-button>
                 </template>
             </el-table-column>
        </el-table>
        <el-pagination 
            layout="total, prev, pager, next"
            :total="totalGroup"
            v-model:current-page="groupQuery.pageNum"
            v-model:page-size="groupQuery.pageSize"
            @current-change="getGroups"
            class="pagination"
        />
     </div>

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
.mini-pagination { margin-top: 10px; justify-content: center; }
.pagination { margin-top: 10px; justify-content: flex-end; }
</style>
