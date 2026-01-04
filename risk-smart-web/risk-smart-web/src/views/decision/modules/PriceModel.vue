<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Search } from '@element-plus/icons-vue'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref([
  { id: 1, name: '标准定价策略', code: 'STANDARD_PRICE', baseRate: 8.5, minRate: 6.0, maxRate: 15.0, description: '标准利率定价', status: 1 },
  { id: 2, name: '风险定价策略', code: 'RISK_PRICE', baseRate: 12.0, minRate: 8.0, maxRate: 24.0, description: '根据风险等级定价', status: 1 },
])
</script>

<template>
  <div class="price-model" v-loading="loading">
    <div class="toolbar">
      <el-input style="width: 240px" :placeholder="t('common.search')" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus">{{ t('decision.addPrice') }}</el-button>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="name" :label="t('decision.priceName')" min-width="150" />
      <el-table-column prop="code" :label="t('decision.priceCode')" width="150" />
      <el-table-column prop="baseRate" :label="t('decision.baseRate')" width="120" align="center">
        <template #default="{ row }">
          {{ row.baseRate }}%
        </template>
      </el-table-column>
      <el-table-column :label="t('decision.rateRange')" width="150" align="center">
        <template #default="{ row }">
          {{ row.minRate }}% - {{ row.maxRate }}%
        </template>
      </el-table-column>
      <el-table-column prop="description" :label="t('decision.description')" min-width="150" />
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
.price-model {
  height: 100%;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }
}
</style>
