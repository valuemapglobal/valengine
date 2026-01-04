<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { countLog } from '@/api/interface/reports'
import * as echarts from 'echarts'

const stats = ref<any>({})
const chartRef = ref()

const fetchStats = async () => {
  const res = await countLog()
  if (res.code === 200) {
    stats.value = res.data
    initChart()
  }
}

const initChart = () => {
    if (!chartRef.value) return
    const myChart = echarts.init(chartRef.value)
    
    // Simple Pie Chart based on legacy
    const option = {
        title: {
            text: '接口调用分布',
            subtext: `总计: ${stats.value.total || 0}`,
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                radius: '50%',
                data: [
                    { value: stats.value.callsSuccess7days || 0, name: '成功' },
                    { value: stats.value.callsFailure7days || 0, name: '失败' },
                ],
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
    myChart.setOption(option)
}

onMounted(() => {
   fetchStats()
   window.addEventListener('resize', () => {
      const chart = echarts.getInstanceByDom(chartRef.value)
      chart?.resize()
   })
})
</script>

<template>
  <div class="app-container">
    <el-row :gutter="20">
       <el-col :span="6">
          <el-card shadow="hover">
             <template #header>数据总量</template>
             <div class="stat-value">{{ stats.interfaceTotal || 0 }}</div>
          </el-card>
       </el-col>
       <el-col :span="6">
          <el-card shadow="hover">
             <template #header>累计调用量</template>
             <div class="stat-value">{{ stats.total || 0 }}</div>
          </el-card>
       </el-col>
       <el-col :span="6">
          <el-card shadow="hover">
             <template #header>昨日调用</template>
             <div class="stat-value">{{ stats.callsYesterday || 0 }}</div>
          </el-card>
       </el-col>
       <el-col :span="6">
          <el-card shadow="hover">
             <template #header>7日成功/失败</template>
             <div class="stat-value">
                 <span class="success">{{ stats.callsSuccess7days || 0 }}</span> / 
                 <span class="danger">{{ stats.callsFailure7days || 0 }}</span>
             </div>
          </el-card>
       </el-col>
    </el-row>

    <el-row style="margin-top: 20px">
       <el-col :span="24">
          <el-card>
             <div ref="chartRef" style="height: 400px; width: 100%"></div>
          </el-card>
       </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.app-container {
  padding: 20px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}
.success { color: #67c23a; }
.danger { color: #f56c6c; }
</style>
