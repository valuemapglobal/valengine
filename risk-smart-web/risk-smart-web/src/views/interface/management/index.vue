<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  findInterfaceInfo,
} from '@/api/interface/management' // Note: findSourceInfo was moved to dataScenario, but InterfaceManagement needs it. I also exported Logic in management? No, I put it in dataScenario.ts.
import { findSourceInfo as getSourceList } from '@/api/interface/dataScenario'
import InterfaceDetail from './components/InterfaceDetail.vue'

// Re-map imports
const getInterfaceList = findInterfaceInfo

// --- State ---
const sourceList = ref<any[]>([])
const interfaceList = ref<any[]>([]) // This will hold the children of the selected source
const currentSource = ref<any>(null)
const currentInterface = ref<any>(null)

const sourceQuery = reactive({
  pageNum: 1,
  pageSize: 20, // Load more sources
  sourceName: ''
})

// --- Methods ---

const initData = async () => {
  // Fetch Sources (Left Side)
  const res = await getSourceList(sourceQuery)
  if (res.code === 200) {
    sourceList.value = res.data.list
    if (sourceList.value.length > 0) {
      selectSource(sourceList.value[0])
    }
  }
}

const selectSource = async (source: any) => {
  currentSource.value = source
  currentInterface.value = null // Reset selection
  // Fetch Interfaces for this Source
  const res = await getInterfaceList({ sourceNo: source.interfaceSourceNo })
  if (res.code === 200) {
     // The legacy code used nested structure, but here we might just get a list?
     // findInterfaceInfo in management.ts calls Api.FindInterface.
     // In legacy InterfaceManagement.vue users findInterfaceInfo to populate children.
     interfaceList.value = res.data.list || []
     if (interfaceList.value.length > 0) {
        selectInterface(interfaceList.value[0])
     }
  }
}

const selectInterface = (item: any) => {
  currentInterface.value = item
}

onMounted(() => {
  initData()
})

</script>

<template>
  <div class="interface-manage-container">
    <!-- Left: Source List -->
    <div class="left-panel">
      <div class="panel-header">数据场景</div>
      <div class="source-list">
        <div 
          v-for="source in sourceList" 
          :key="source.interfaceSourceNo"
          class="source-item"
          :class="{ active: currentSource?.interfaceSourceNo === source.interfaceSourceNo }"
          @click="selectSource(source)"
        >
          <span class="source-name">{{ source.dataName }}</span>
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>
    </div>

    <!-- Middle: Interface List (Optional, similar to legacy?) -->
    <!-- The legacy code had complex nested lists. I'll simplify: 
         Left Panel = Sources.
         Middle Panel = Interfaces in Source.
         Right Panel = Detail. 
         OR
         Left = Tree/List of Source -> Interface.
         Right = Detail.
    -->
    
    <!-- Using 3-column layout to match legacy feel (Source -> Interface -> Detail) -->
    
    <div class="middle-panel">
       <div class="panel-header">接口列表</div>
       <div class="interface-list" v-if="interfaceList.length > 0">
          <div 
            v-for="item in interfaceList" 
            :key="item.interfaceNo"
            class="interface-item"
             :class="{ active: currentInterface?.interfaceNo === item.interfaceNo }"
            @click="selectInterface(item)"
          >
             {{ item.interfaceName }}
          </div>
       </div>
       <div v-else class="empty-text">暂无接口</div>
    </div>

    <!-- Right: Detail -->
    <div class="right-panel">
      <InterfaceDetail 
        v-if="currentInterface" 
        :item-info="currentInterface"
        :source-no="currentSource?.interfaceSourceNo"
        @refresh="selectSource(currentSource)" 
      />
      <div v-else class="empty-detail">请选择接口查看详情</div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.interface-manage-container {
  display: flex;
  height: calc(100vh - 84px); // Adjust based on layout header
  background-color: #f0f2f5;
  padding: 10px;
  gap: 10px;
}

.left-panel, .middle-panel {
  width: 250px;
  background-color: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}

.right-panel {
  flex: 1;
  background-color: #fff;
  border-radius: 4px;
  overflow: hidden;
}

.panel-header {
  padding: 15px;
  font-weight: bold;
  border-bottom: 1px solid #eee;
}

.source-list, .interface-list {
  flex: 1;
  overflow-y: auto;
}

.source-item, .interface-item {
  padding: 12px 15px;
  cursor: pointer;
  border-left: 3px solid transparent;
  &:hover {
    background-color: #f5f7fa;
  }
  &.active {
    background-color: #e6f7ff;
    border-left-color: #1890ff;
    color: #1890ff;
  }
}

.empty-text, .empty-detail {
  padding: 50px;
  text-align: center;
  color: #999;
}
</style>
