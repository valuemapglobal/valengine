<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Search } from '@element-plus/icons-vue'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref([
  { id: 1, name: 'AAA级', code: 'AAA', scoreMin: 850, scoreMax: 900, description: '最优质客户', status: 1 },
  { id: 2, name: 'AA级', code: 'AA', scoreMin: 750, scoreMax: 849, description: '优质客户', status: 1 },
  { id: 3, name: 'A级', code: 'A', scoreMin: 650, scoreMax: 749, description: '良好客户', status: 1 },
  { id: 4, name: 'B级', code: 'B', scoreMin: 550, scoreMax: 649, description: '一般客户', status: 1 },
  { id: 5, name: 'C级', code: 'C', scoreMin: 0, scoreMax: 549, description: '风险客户', status: 1 },
])
</script>

<template>
  <div class="rate-model" v-loading="loading">
    <div class="toolbar">
      <el-input style="width: 240px" :placeholder="t('common.search')" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus">{{ t('decision.addRate') }}</el-button>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="name" :label="t('decision.rateName')" min-width="120" />
      <el-table-column prop="code" :label="t('decision.rateCode')" width="100" />
      <el-table-column :label="t('decision.scoreRange')" width="150" align="center">
        <template #default="{ row }">
          {{ row.scoreMin }} - {{ row.scoreMax }}
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
.rate-model {
  height: 100%;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }
}
</style>
