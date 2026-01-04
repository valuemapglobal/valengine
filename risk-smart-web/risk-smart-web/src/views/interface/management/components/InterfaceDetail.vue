<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import {
  findInterfaceFieIdInfo,
  findInterfaceInfoTest,
  interfaceOn
} from '@/api/interface/management'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  itemInfo: any,
  sourceNo: string
}>()

const emit = defineEmits(['refresh'])

// --- State ---
const activeTab = ref('details')
const reqList = ref([])
const resList = ref([])
const testParams = ref<any[]>([])
const testForm = reactive<any>({})
const testResult = ref<any>(null)
const loadingTest = ref(false)

// Data Types mapping (simplified for now, ideally from dict)
const dataTypeMap: Record<number, string> = {
  0: 'String',
  1: 'Number',
  2: 'Boolean',
  3: 'Object',
  4: 'Array'
}

// Stats / Info
const getParamsInfo = async () => {
  if (!props.itemInfo?.interfaceManageNo) return
  const res = await findInterfaceFieIdInfo({ manageNo: props.itemInfo.interfaceManageNo })
  if (res.code === 200) {
    const list = res.data.list || []
    reqList.value = list.filter((i: any) => i.interfaceFieIdType === 0)
    resList.value = list.filter((i: any) => i.interfaceFieIdType === 1)
    
    // Setup Test Form
    testParams.value = list.filter((i: any) => i.interfaceFieIdType === 0)
    testParams.value.forEach((p: any) => {
       testForm[p.interfaceFieIdAlias] = ''
    })
  }
}

watch(() => props.itemInfo, () => {
  activeTab.value = 'details'
  testResult.value = null
  if (props.itemInfo) {
    getParamsInfo()
  }
}, { immediate: true })

// --- Actions ---
const handleToggleStatus = async () => {
  const newStatus = props.itemInfo.interfaceOn === 1 ? 0 : 1
  try {
    await interfaceOn({
       manageNo: props.itemInfo.interfaceManageNo,
       sourceNo: props.itemInfo.interfaceSourceNo,
       interfaceOn: newStatus
    })
    ElMessage.success(newStatus === 1 ? '已开启' : '已关闭')
    emit('refresh')
  } catch (e) {
    console.error(e)
  }
}

const handleTest = async () => {
  loadingTest.value = true
  try {
    // Construct payload
    // Legacy logic: manageNo, sourceNo, interfaceNo, paramData
    const payload = {
       manageNo: props.itemInfo.interfaceManageNo,
       sourceNo: props.sourceNo,
       interfaceNo: props.itemInfo.interfaceNo,
       paramData: { ...testForm }
    }
    
    // Parse JSON fields if necessary (Legacy logic did JSON.parse for Object/Array types)
    // We'll skip complex parsing for now unless we duplicate the exact legacy logic.
    
    const res = await findInterfaceInfoTest(payload)
    if (res.code === 200) {
      testResult.value = res.data
    } else {
      testResult.value = res // Show error result
    }
  } catch (e) {
    testResult.value = e
  } finally {
    loadingTest.value = false
  }
}
</script>

<template>
  <div class="detail-container">
    <!-- Header -->
    <div class="header-section">
      <div class="info-group">
        <h2>{{ itemInfo.interfaceName }}</h2>
        <p><label>描述：</label>{{ itemInfo.interfaceDescription }}</p>
        <p><label>ID：</label>{{ itemInfo.interfaceNo }}</p>
      </div>
      <div class="action-group">
        <el-button 
          :type="itemInfo.interfaceOn === 1 ? 'danger' : 'success'" 
          @click="handleToggleStatus"
        >
          {{ itemInfo.interfaceOn === 1 ? '关闭接口' : '开启接口' }}
        </el-button>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs-section">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="产品详情" name="details">
           <div class="tab-content">
             <h3>产品特点</h3>
             <p>{{ itemInfo.interfaceFeatures || '暂无描述' }}</p>
           </div>
        </el-tab-pane>
        
        <el-tab-pane label="业务参数" name="params">
           <div class="tab-content">
              <h4>请求参数</h4>
              <el-table :data="reqList" border style="margin-bottom: 20px;">
                <el-table-column prop="interfaceFieIdName" label="参数名称" />
                <el-table-column label="数据类型">
                  <template #default="{ row }">{{ dataTypeMap[row.interfaceFieIdDataType] || row.interfaceFieIdDataType }}</template>
                </el-table-column>
                <el-table-column prop="interfaceFieIdDescription" label="说明" />
              </el-table>

              <h4>响应参数</h4>
              <el-table :data="resList" border>
                <el-table-column prop="interfaceFieIdName" label="参数名称" />
                <el-table-column label="数据类型">
                  <template #default="{ row }">{{ dataTypeMap[row.interfaceFieIdDataType] || row.interfaceFieIdDataType }}</template>
                </el-table-column>
                <el-table-column prop="interfaceFieIdDescription" label="说明" />
              </el-table>
           </div>
        </el-tab-pane>
        
        <el-tab-pane label="接口测试" name="test">
           <div class="tab-content">
              <div class="test-form">
                <el-form label-position="top">
                  <el-form-item v-for="param in testParams" :key="param.interfaceFieIdAlias" :label="param.interfaceFieIdDescription">
                    <el-input v-model="testForm[param.interfaceFieIdAlias]" :placeholder="param.interfaceFieIdRemark || '请输入'" />
                  </el-form-item> 
                  <el-form-item>
                    <el-button type="primary" :loading="loadingTest" @click="handleTest">发送请求</el-button>
                  </el-form-item>
                </el-form>
              </div>
              <div class="test-result" v-if="testResult">
                 <h4>响应结果</h4>
                 <pre>{{ JSON.stringify(testResult, null, 2) }}</pre>
              </div>
           </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped lang="scss">
.detail-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}
.info-group h2 {
  margin: 0 0 10px 0;
}
.info-group p {
  margin: 5px 0;
  color: #666;
}
.tabs-section {
  flex: 1;
  overflow-y: auto;
}
.tab-content {
  padding: 10px;
}
.test-result pre {
  background: #f4f4f4;
  padding: 10px;
  border-radius: 4px;
  overflow: auto;
}
</style>
