<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const stats = computed(() => [
  { title: t('dashboard.totalUsers'), value: 1234, icon: 'User', color: '#409EFF' },
  { title: t('dashboard.riskAlerts'), value: 56, icon: 'Warning', color: '#E6A23C' },
  { title: t('dashboard.decisionsToday'), value: 892, icon: 'Document', color: '#67C23A' },
  { title: t('dashboard.successRate'), value: '98.5%', icon: 'TrendCharts', color: '#909399' },
])
</script>

<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col v-for="stat in stats" :key="stat.title" :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: stat.color + '20', color: stat.color }">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>{{ t('dashboard.riskTrend') }}</span>
          </template>
          <div class="chart-placeholder">
            Chart will be rendered here
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>{{ t('dashboard.recentAlerts') }}</span>
          </template>
          <el-empty :description="t('dashboard.noAlerts')" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.dashboard {
  .stat-card {
    .stat-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #333;
      }

      .stat-title {
        font-size: 14px;
        color: #999;
        margin-top: 4px;
      }
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .chart-placeholder {
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    background: #f5f7fa;
    border-radius: 4px;
  }
}
</style>
