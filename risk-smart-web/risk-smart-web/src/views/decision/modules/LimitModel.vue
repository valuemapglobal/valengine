<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Search } from '@element-plus/icons-vue'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref([
  { id: 1, name: '标准额度策略', code: 'STANDARD_LIMIT', minAmount: 1000, maxAmount: 50000, description: '适用于普通客户', status: 1 },
  { id: 2, name: '优质客户额度', code: 'VIP_LIMIT', minAmount: 10000, maxAmount: 200000, description: '适用于VIP客户', status: 1 },
])
</script>

<template>
  <div class="limit-model" v-loading="loading">
    <div class="toolbar">
      <el-input style="width: 240px" :placeholder="t('common.search')" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus">{{ t('decision.addLimit') }}</el-button>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="name" :label="t('decision.limitName')" min-width="150" />
      <el-table-column prop="code" :label="t('decision.limitCode')" width="150" />
      <el-table-column :label="t('decision.amountRange')" width="180" align="center">
        <template #default="{ row }">
          {{ row.minAmount.toLocaleString() }} - {{ row.maxAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="description" :label="t('decision.description')" min-width="200" />
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
.limit-model {
  height: 100%;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }
}
</style>
