<script setup lang="ts">
import { ref } from 'vue'
import { Search, Plus, Download, Edit, Delete } from '@element-plus/icons-vue'

// --- Mock Data for UI Matching ---
const modelTypes = [
  { label: '规则模型', value: 'rule' },
  { label: '分类模型', value: 'classify' },
  { label: '评分模型', value: 'score' },
  { label: '评级模型', value: 'grade' },
  { label: '额度模型', value: 'quota' },
  { label: '定价模型', value: 'pricing' }
]
const activeModelType = ref('rule')

const strategies = ref([
  { id: '11270001', name: '11270001', desc: '11270001', version: '11.2701-ZR-c-V1.0.0', status: true },
  { id: '11270002', name: '示例策略2', desc: '测试策略描述', version: 'V1.0.1', status: false }
])

const ruleGroups = ref([
  { id: '2001', name: '11270001', desc: '11270001规则组', status: true }
])

const rules = ref([
  { id: '3001', name: '112100012', desc: '112100012', status: true },
  { id: '3002', name: 'CESHI-001', desc: 'CESHI-001', status: true }
])

// --- Methods ---

</script>

<template>
  <div class="rule-strategy-container">
      <!-- Model Type Tabs -->
      <div class="model-types">
           <el-radio-group v-model="activeModelType" size="large">
              <el-radio-button v-for="type in modelTypes" :key="type.value" :label="type.value">{{ type.label }}</el-radio-button>
           </el-radio-group>
      </div>
      
      <!-- 3-Column Layout -->
      <div class="strategy-content">
          <!-- Column 1: Strategies -->
          <div class="column-card">
              <div class="column-header">
                  <span class="title">策略</span>
                  <el-input class="search-input" placeholder="Q" :suffix-icon="Search" />
                  <div class="actions">
                     <el-button type="primary" plain size="small" :icon="Download">导入策略</el-button>
                     <el-button type="success" plain size="small" :icon="Plus">复用</el-button>
                     <el-button type="warning" plain size="small" :icon="Plus">新增</el-button>
                  </div>
              </div>
              
              <div class="card-list">
                  <div class="item-card" v-for="item in strategies" :key="item.id">
                      <div class="card-top">
                          <span class="card-title">{{ item.name }}</span>
                          <el-tag size="small" type="info">自建</el-tag>
                          <div class="card-btns">
                              <el-button link type="primary" :icon="Edit">编辑</el-button>
                              <el-button link type="danger" :icon="Delete">删除</el-button>
                          </div>
                      </div>
                      <div class="card-desc">描述：{{ item.desc }}</div>
                      <div class="card-version" v-if="item.version">当前版本：{{ item.version }}</div>
                      <div class="card-footer">
                          <el-button size="small" plain round>导出</el-button>
                          <el-button size="small" plain round>测试</el-button>
                          <el-switch v-model="item.status" active-text="使用" inline-prompt />
                      </div>
                  </div>
                   <div class="no-more">没有更多了</div>
              </div>
          </div>

          <!-- Column 2: Rule Groups -->
          <div class="column-card">
              <div class="column-header">
                  <span class="title">规则组</span>
                  <el-input class="search-input" placeholder="Q" :suffix-icon="Search" />
                  <div class="actions">
                     <el-button type="success" plain size="small" :icon="Plus">复用</el-button>
                     <el-button type="warning" plain size="small" :icon="Plus">新增</el-button>
                  </div>
              </div>
               <div class="card-list">
                   <div class="item-card" v-for="item in ruleGroups" :key="item.id">
                       <div class="card-top">
                           <span class="card-title">{{ item.name }}</span>
                           <el-tag size="small" type="info">自建</el-tag>
                           <div class="card-btns">
                              <el-button link type="primary" :icon="Edit">编辑</el-button>
                              <el-button link type="danger" :icon="Delete">删除</el-button>
                           </div>
                       </div>
                       <div class="card-desc">描述：{{ item.desc }}</div>
                       <div class="card-footer footer-simple">
                           <el-switch v-model="item.status" active-text="使用" inline-prompt />
                       </div>
                   </div>
                   <div class="no-more">没有更多了</div>
               </div>
          </div>

          <!-- Column 3: Rules -->
          <div class="column-card">
              <div class="column-header">
                  <span class="title">规则</span>
                  <el-input class="search-input" placeholder="Q" :suffix-icon="Search" />
                   <div class="actions">
                     <el-button type="success" plain size="small" :icon="Plus">复用</el-button>
                     <el-button type="warning" plain size="small" :icon="Plus">新增</el-button>
                  </div>
              </div>
               <div class="card-list">
                   <div class="item-card" v-for="item in rules" :key="item.id">
                       <div class="card-top">
                           <span class="card-title">{{ item.name }}</span>
                           <el-tag size="small" type="info">自建</el-tag>
                           <div class="card-btns">
                              <el-button link type="primary" :icon="Edit">编辑</el-button>
                              <el-button link type="danger" :icon="Delete">删除</el-button>
                           </div>
                       </div>
                       <div class="card-desc">描述：{{ item.desc }}</div>
                       <div class="card-footer footer-simple">
                           <el-switch v-model="item.status" active-text="使用" inline-prompt />
                       </div>
                   </div>
                   <div class="no-more">没有更多了</div>
               </div>
          </div>
      </div>
  </div>
</template>

<style scoped lang="scss">
.rule-strategy-container {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.model-types {
    margin-bottom: 20px;
    :deep(.el-radio-button__inner) {
        border: none;
        background: #f5f7fa;
        margin-right: 10px;
        border-radius: 4px;
        color: #606266;
        padding: 8px 15px;
        box-shadow: none !important;
    }
    :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
        background-color: var(--el-color-primary);
        color: #fff;
        box-shadow: none;
    }
}

.strategy-content {
    flex: 1;
    display: flex;
    gap: 20px;
    overflow: hidden;
}

.column-card {
    flex: 1;
    background: #fff;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    height: 100%;
}

.column-header {
    padding: 15px;
    display: flex;
    align-items: center;
    gap: 10px;
    border-bottom: 1px solid #ebeef5;
    
    .title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
    }
    .search-input {
        width: 60px;
        transition: width 0.3s;
        &:focus-within {
            width: 150px;
        }
        :deep(.el-input__wrapper) {
            border-radius: 15px;
            background-color: #f5f7fa;
            box-shadow: none;
        }
    }
    .actions {
        flex: 1;
        display: flex;
        justify-content: flex-end;
        gap: 5px;
    }
}

.card-list {
    flex: 1;
    padding: 15px;
    overflow-y: auto;
    background-color: #f9f9f9;
}

.item-card {
    background: #fff;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.05);
    transition: transform 0.2s;
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
}

.card-top {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    
    .card-title {
        font-weight: bold;
        font-size: 14px;
        margin-right: 10px;
        color: #303133;
    }
    .card-btns {
        margin-left: auto;
    }
}

.card-desc, .card-version {
    font-size: 12px;
    color: #909399;
    margin-bottom: 8px;
    line-height: 1.4;
}

.card-footer {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
    
    &.footer-simple {
        justify-content: flex-end;
    }
    
    :deep(.el-switch) {
        margin-left: auto;
    }
}

.no-more {
    text-align: center;
    font-size: 12px;
    color: #c0c4cc;
    padding: 10px 0;
}
</style>
