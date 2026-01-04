<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { getVariableList, type Variable, type VariableGroup } from '@/api/dataCenter'

const props = defineProps<{
  modelValue: boolean
  group: VariableGroup | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const { t } = useI18n()

const loading = ref(false)
const variableList = ref<Variable[]>([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
})

// 获取变量列表
async function fetchVariables() {
  if (!props.group?.id) return
  loading.value = true
  try {
    const res = await getVariableList({
      groupId: props.group.id,
      ...queryParams.value,
    })
    if (res.code === 200 && res.data) {
      variableList.value = res.data.rows
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 关闭抽屉
function handleClose() {
  emit('update:modelValue', false)
}

// 分页
function handlePageChange(page: number) {
  queryParams.value.pageNum = page
  fetchVariables()
}

function handleSizeChange(size: number) {
  queryParams.value.pageSize = size
  fetchVariables()
}

// 监听打开
watch(() => props.modelValue, (val) => {
  if (val && props.group) {
    queryParams.value.pageNum = 1
    fetchVariables()
  }
})
</script>

<template>
  <el-drawer
    :model-value="modelValue"
    :title="t('dataCenter.viewProperties')"
    size="50%"
    @close="handleClose"
  >
    <div class="variable-drawer" v-loading="loading">
      <div class="group-info" v-if="group">
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="t('dataCenter.objectName')">
            {{ group.name }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('dataCenter.objectCode')">
            {{ group.keycode }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-table :data="variableList" border stripe style="margin-top: 20px">
        <el-table-column prop="name" :label="t('dataCenter.propertyName')" />
        <el-table-column prop="keycode" :label="t('dataCenter.propertyCode')" />
        <el-table-column prop="type" :label="t('dataCenter.propertyType')" width="100" />
        <el-table-column prop="required" :label="t('dataCenter.required')" width="80">
          <template #default="{ row }">
            <el-tag :type="row.required ? 'danger' : 'info'" size="small">
              {{ row.required ? t('common.yes') : t('common.no') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="t('decision.description')" />
      </el-table>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          :total="total"
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </el-drawer>
</template>

<style lang="scss" scoped>
.variable-drawer {
  padding: 20px;

  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}
</style>
