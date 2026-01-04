<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Search } from '@element-plus/icons-vue'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref([
  { id: 1, name: '信用评分卡', code: 'CREDIT_SCORE', baseScore: 600, minScore: 300, maxScore: 900, status: 1 },
  { id: 2, name: '反欺诈评分', code: 'FRAUD_SCORE', baseScore: 500, minScore: 0, maxScore: 1000, status: 1 },
])
</script>

<template>
  <div class="score-model" v-loading="loading">
    <div class="toolbar">
      <el-input style="width: 240px" :placeholder="t('common.search')" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus">{{ t('decision.addScoreCard') }}</el-button>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="name" :label="t('decision.scoreName')" min-width="150" />
      <el-table-column prop="code" :label="t('decision.scoreCode')" width="150" />
      <el-table-column prop="baseScore" :label="t('decision.baseScore')" width="120" align="center" />
      <el-table-column prop="minScore" :label="t('decision.minScore')" width="120" align="center" />
      <el-table-column prop="maxScore" :label="t('decision.maxScore')" width="120" align="center" />
      <el-table-column prop="status" :label="t('user.status')" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? t('decision.enabled') : t('decision.disabled') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('user.actions')" width="180" align="center">
        <template #default>
          <el-button link type="primary">{{ t('decision.configure') }}</el-button>
          <el-button link type="primary">{{ t('common.edit') }}</el-button>
          <el-button link type="danger">{{ t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
.score-model {
  height: 100%;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }
}
</style>
