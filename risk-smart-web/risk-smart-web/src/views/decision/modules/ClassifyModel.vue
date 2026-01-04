<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Search } from '@element-plus/icons-vue'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref([
  { id: 1, name: '高风险客户', code: 'HIGH_RISK', description: '信用评分低于500的客户', status: 1 },
  { id: 2, name: '中风险客户', code: 'MID_RISK', description: '信用评分500-700的客户', status: 1 },
  { id: 3, name: '低风险客户', code: 'LOW_RISK', description: '信用评分高于700的客户', status: 1 },
])
</script>

<template>
  <div class="classify-model" v-loading="loading">
    <div class="toolbar">
      <el-input style="width: 240px" :placeholder="t('common.search')" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus">{{ t('decision.addClassify') }}</el-button>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="name" :label="t('decision.classifyName')" min-width="150" />
      <el-table-column prop="code" :label="t('decision.classifyCode')" width="150" />
      <el-table-column prop="description" :label="t('decision.description')" min-width="200" />
      <el-table-column prop="status" :label="t('user.status')" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? t('decision.enabled') : t('decision.disabled') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('user.actions')" width="150" align="center">
        <template #default>
          <el-button link type="primary">{{ t('common.edit') }}</el-button>
          <el-button link type="danger">{{ t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
.classify-model {
  height: 100%;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }
}
</style>
