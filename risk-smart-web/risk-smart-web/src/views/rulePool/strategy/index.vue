<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { Download, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import {
  getRulePoolGage,
  getRulePoolStats,
  getRulePoolHotKeywords,
  business_search
} from '@/api/rulePool/strategy'

// --- State ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const hotList = ref<string[]>([])
const businessSceneOptions = ref<any[]>([])

// Dashboard Stats
const boardList = reactive([
  { label: '总规则数', value: 0, rate: 0, field: 'totalRules', rateField: 'totalRulesRate' },
  { label: '启用中规则', value: 0, rate: 0, field: 'activeRules', rateField: 'activeRulesRate' },
  { label: '规则覆盖产品数', value: 0, rate: 0, field: 'productCount', rateField: 'productCountRate' },
  { label: '规则覆盖场景数', value: 0, rate: 0, field: 'sceneCount', rateField: 'sceneCountRate' }
])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  strategy: '',
  status: undefined,
  riskLevel: undefined,
  hitAction: undefined,
  businessScene: undefined,
  modelType: undefined,
  referenceProduct: '',
  createTimeStart: undefined as string | undefined,
  createTimeEnd: undefined as string | undefined
})

const timeRange = ref([])
const showMoreFilters = ref(false)
const drawerVisible = ref(false)
const currentRow = ref<any>(null)

// Options (Static for now, could be from Dicts)
const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 2 }
]
const riskLevelOptions = [
  { label: '低风险', value: 1 },
  { label: '中低风险', value: 2 },
  { label: '中风险', value: 3 },
  { label: '中高风险', value: 4 },
  { label: '高风险', value: 5 }
]
const hitActionOptions = [
  { label: '通过', value: '通过' },
  { label: '拒绝', value: '拒绝' },
  { label: '转人工', value: '转人工' }
]
const modelTypeOptions = [
  { label: '评分', value: 1 },
  { label: '规则', value: 5 },
  { label: '分类', value: 6 }
]

// --- Methods ---
const initData = async () => {
  // Stats
  try {
      const res = await getRulePoolStats()
      if (res.code === 200) {
          boardList.forEach(item => {
              item.value = res.data[item.field] || 0
              item.rate = res.data[item.rateField] || 0
          })
      }
  } catch (e) { console.error(e) }

  // Hot Keywords
  try {
      const res = await getRulePoolHotKeywords()
      if (res.code === 200) {
          hotList.value = res.data || []
      }
  } catch (e) { console.error(e) }

  // Business Scenes
  try {
      const res = await business_search({})
      if (res.code === 200) {
          businessSceneOptions.value = res.data.list || []
      }
  } catch (e) { console.error(e) }

  getList()
}

const getList = async () => {
    loading.value = true
    try {
        const res = await getRulePoolGage(queryParams)
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
    queryParams.keyword = ''
    queryParams.strategy = ''
    queryParams.status = undefined
    queryParams.riskLevel = undefined
    queryParams.hitAction = undefined
    queryParams.businessScene = undefined
    queryParams.modelType = undefined
    queryParams.referenceProduct = ''
    queryParams.createTimeStart = undefined
    queryParams.createTimeEnd = undefined
    timeRange.value = []
    handleSearch()
}

const handlePageChange = (val: number) => {
    queryParams.pageNum = val
    getList()
}

const handleSizeChange = (val: number) => {
    queryParams.pageSize = val
    getList()
}

const handleHotSearch = (val: string) => {
    queryParams.keyword = val
    handleSearch()
}

const handleDetails = (row: any) => {
    currentRow.value = row
    drawerVisible.value = true
}

watch(timeRange, (val: any) => {
    if (val && val.length === 2) {
        queryParams.createTimeStart = val[0]
        queryParams.createTimeEnd = val[1]
    } else {
        queryParams.createTimeStart = undefined
        queryParams.createTimeEnd = undefined
    }
})

onMounted(() => {
    initData()
})
</script>

<template>
  <div class="rule-pool-container">
      <!-- Dashboard Cards -->
      <div class="board-container">
          <div v-for="(item, index) in boardList" :key="index" class="board-container-item">
              <div class="board-container-item-label">{{ item.label }}</div>
              <div class="board-container-item-value">{{ item.value }}</div>
              <el-progress 
                class="line" 
                :show-text="false" 
                :percentage="Math.min(Math.abs(item.rate), 100)" 
                :color="item.rate >= 0 ? '#1cd400' : '#ff0000'"
              ></el-progress>
              <div class="board-container-item-compared">
                  <span class="rate-icon" v-if="item.rate > 0" style="color: #1cd400">▲</span>
                  <span class="rate-icon" v-else-if="item.rate < 0" style="color: #ff0000">▼</span>
                  <span :class="{ increase: item.rate > 0, decrease: item.rate < 0 }">
                      {{ Math.abs(item.rate) }}%
                  </span>
                  <span class="rate-text">较上月</span>
              </div>
          </div>
      </div>

      <!-- Search Area -->
      <div class="search-container">
          <div class="search-input">
             <el-input 
                v-model="queryParams.keyword" 
                placeholder="搜索规则名称/ID/策略/产品关键词" 
                clearable 
                @keyup.enter="handleSearch"
                class="main-search-input"
              >
                  <template #suffix>
                      <el-button type="primary" @click="handleSearch">搜索</el-button>
                  </template>
              </el-input>
          </div>
          
          <div class="hot-search" v-if="hotList.length">
              <span>热门搜索：</span>
              <span 
                v-for="tag in hotList" 
                :key="tag" 
                class="hot-tag" 
                @click="handleHotSearch(tag)"
              >
                  {{ tag }}
              </span>
          </div>

          <div class="search-options" v-show="showMoreFilters">
               <div class="search-options-header">
                  <el-button type="primary" @click="handleSearch">应用筛选</el-button>
                  <el-button @click="handleReset">重置筛选</el-button>
                  <el-button :icon="Download">导出数据</el-button>
               </div>
               
               <div class="search-options-content">
                   <el-form :model="queryParams" label-width="100px">
                       <el-form-item label="策略名称：">
                           <el-input v-model="queryParams.strategy" placeholder="请输入" clearable />
                       </el-form-item>
                       <el-form-item label="规则状态：">
                          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
                              <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                          </el-select>
                       </el-form-item>
                       <el-form-item label="风险等级：">
                          <el-select v-model="queryParams.riskLevel" placeholder="请选择" clearable>
                              <el-option v-for="opt in riskLevelOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                          </el-select>
                       </el-form-item>
                       <el-form-item label="命中动作：">
                          <el-select v-model="queryParams.hitAction" placeholder="请选择" clearable>
                              <el-option v-for="opt in hitActionOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                          </el-select>
                       </el-form-item>
                        <el-form-item label="业务场景：">
                          <el-select v-model="queryParams.businessScene" placeholder="请选择" clearable>
                               <el-option v-for="opt in businessSceneOptions" :key="opt.id" :label="opt.name" :value="opt.id" />
                          </el-select>
                       </el-form-item>
                        <el-form-item label="模型类型：">
                          <el-select v-model="queryParams.modelType" placeholder="请选择" clearable>
                              <el-option v-for="opt in modelTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                          </el-select>
                       </el-form-item>
                       <el-form-item label="产品名称：">
                          <el-input v-model="queryParams.referenceProduct" placeholder="请输入" clearable />
                       </el-form-item>
                        <el-form-item label="时间范围：">
                           <el-date-picker
                            v-model="timeRange"
                            type="daterange"
                            range-separator="-"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="YYYY-MM-DD"
                            style="width: 100%"
                          />
                       </el-form-item>
                   </el-form>
               </div>
          </div>
          
          <div class="openBtn" @click="showMoreFilters = !showMoreFilters">
             <div>
                <el-icon :class="{ rotate180: showMoreFilters }"><ArrowDown v-if="!showMoreFilters"/><ArrowUp v-else/></el-icon>
                <span>{{ showMoreFilters ? '收起筛选' : '更多筛选' }}</span>
             </div>
             <!-- SVG shape for button background -->
             <svg width="140" height="35" viewBox="0 0 140 35" class="bg-svg">
               <path
                 d="M 20,35 L 120,35 Q 135,20 140,0 L 0,0 Q 5,20 20,35 Z"
                 fill="#fff"
               />
             </svg>
           </div>
      </div>

      <!-- Table Section -->
      <div class="table-container">
          <div class="table-container-header">
              <span class="table-container-header-title">规则列表</span>
              <div>
                共 <span style="color: #2888e8">{{ total }}</span> 条规则
              </div>
          </div>
          
          <div class="table-container-table">
               <el-table v-loading="loading" :data="tableData" border style="width: 100%">
                 <el-table-column prop="strategy" label="策略" width="150" show-overflow-tooltip />
                 <el-table-column prop="ruleGroup" label="规则组" min-width="200" show-overflow-tooltip />
                 <el-table-column prop="ruleCode" label="规则Code" width="150" />
                 <el-table-column prop="ruleDesc" label="规则描述" min-width="300" show-overflow-tooltip />
                 <el-table-column prop="riskLevel" label="风险等级" width="100" />
                 <el-table-column prop="hitAction" label="命中动作" width="100" />
                 <el-table-column prop="status" label="状态" width="100">
                     <template #default="{ row }">
                         <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
                     </template>
                 </el-table-column>
                 <el-table-column label="操作" width="100" fixed="right" align="center">
                     <template #default="{ row }">
                         <el-button link type="primary" @click="handleDetails(row)">查看详情</el-button>
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
                      @current-change="handlePageChange"
                      @size-change="handleSizeChange"
                   />
               </div>
          </div>
      </div>

      <!-- Detail Drawer -->
      <el-drawer v-model="drawerVisible" title="规则详情" size="50%">
           <div v-if="currentRow" class="detail-content">
              <el-descriptions title="基本信息" :column="1" border>
                  <el-descriptions-item label="策略">{{ currentRow.strategy }}</el-descriptions-item>
                  <el-descriptions-item label="规则组">{{ currentRow.ruleGroup }}</el-descriptions-item>
                  <el-descriptions-item label="规则Code">{{ currentRow.ruleCode }}</el-descriptions-item>
                  <el-descriptions-item label="规则描述">{{ currentRow.ruleDesc }}</el-descriptions-item>
                  <el-descriptions-item label="风险等级">{{ currentRow.riskLevel }}</el-descriptions-item>
                  <el-descriptions-item label="命中动作">{{ currentRow.hitAction }}</el-descriptions-item>
                  <el-descriptions-item label="状态">{{ currentRow.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
                  <el-descriptions-item label="参考产品">{{ currentRow.referenceProduct }}</el-descriptions-item>
                  <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
              </el-descriptions>
          </div>
      </el-drawer>
  </div>
</template>

<style scoped lang="scss">
.rule-pool-container {
    width: 100%;
    min-height: 100vh;
    padding: 20px;
    background-color: #f0f2f5;
    font-family: 'PingFang SC-Medium';
}

.board-container {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;

    &-item {
        background: #fff;
        padding: 15px;
        border-radius: 12px;
        box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.05);

        &-label { font-size: 14px; color: #333; margin-bottom: 8px; }
        &-value { font-size: 24px; font-weight: bold; color: #333; margin-bottom: 12px; }
        .line { width: 100%; margin-bottom: 8px; :deep(.el-progress-bar__inner) { border-radius: 4px; } }
        &-compared {
            font-size: 13px;
            color: #909399;
            display: flex;
            align-items: center;
            gap: 4px;
            .increase { color: #1cd400; }
            .decrease { color: #ff0000; }
            .rate-icon { font-size: 12px; }
        }
    }
}

.search-container {
    margin-top: 20px;
    background: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 0 10px 0 rgba(0,0,0,0.05);
    position: relative;
    padding-bottom: 40px; /* Space for the open button */

    .search-input {
        max-width: 100%;
        margin-bottom: 15px;
        :deep(.main-search-input) {
            .el-input__wrapper {
                 padding-right: 85px; /* Space for button */
                 height: 45px;
                 border-radius: 4px;
            }
            .el-input__suffix {
                position: absolute;
                right: 5px;
                top: 50%;
                transform: translateY(-50%);
            }
        }
    }

    .hot-search {
        font-size: 14px;
        margin-bottom: 15px;
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        span { color: #666; }
        .hot-tag {
            background: rgba(40, 136, 232, 0.1);
            color: #2888e8;
            padding: 4px 10px;
            border-radius: 4px;
            margin: 0 5px;
            cursor: pointer;
            &:hover { background: rgba(40, 136, 232, 0.2); }
        }
    }

    .search-options {
        margin-top: 20px;
        border-top: 1px solid #eee;
        padding-top: 20px;

        &-header {
            display: flex;
            gap: 10px;
            margin-bottom: 15px;
        }

        &-content {
            :deep(.el-form) {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 15px;
                .el-form-item { margin-bottom: 0; width: 100%; }
                .el-select { width: 100%; }
            }
        }
    }

    .openBtn {
        position: absolute;
        bottom: -34px; /* Adjust based on SVG height */
        left: 50%;
        transform: translateX(-50%);
        width: 140px;
        height: 35px;
        cursor: pointer;
        z-index: 10;
        
        .bg-svg {
            position: absolute;
            top: 0;
            left: 0;
            filter: drop-shadow(0 2px 4px rgba(0,0,0,0.05));
        }

        div {
            position: relative;
            z-index: 2;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #606266;
            
            span { font-size: 12px; margin-left: 4px; }
            .rotate180 { transform: rotate(180deg); transition: transform 0.3s; }
        }
    }
}

.table-container {
    margin-top: 50px; /* Space for the open button overlay */
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 0 10px 0 rgba(0,0,0,0.05);
    padding: 20px;

    &-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        &-title { font-size: 16px; font-weight: bold; color: #303133; }
    }
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
