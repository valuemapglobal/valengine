<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getThemeList, getGroupList, getDictsByType, type Theme, type VariableGroup } from '@/api/dataCenter'
import VariableDrawer from './components/VariableDrawer.vue'

const { t } = useI18n()

// 数据类型字典
const dataTypeList = ref<{ dictValue: string; dictLabel: string }[]>([])

// 主题相关状态
const themeLoading = ref(false)
const themeList = ref<Theme[]>([])
const themeTotal = ref(0)
const selectedThemeIndex = ref(0)
const themeQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  packageType: 0,
})

// 对象/变量组相关状态
const groupLoading = ref(false)
const groupList = ref<VariableGroup[]>([])
const groupTotal = ref(0)
const selectedThemeNo = ref('')
const groupQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
})

// 抽屉状态
const themeDrawerVisible = ref(false)
const themeDrawerTitle = ref('')
const themeForm = ref<Partial<Theme>>({})

const groupDrawerVisible = ref(false)
const groupDrawerTitle = ref('')
const groupForm = ref<Partial<VariableGroup>>({})

const variableDrawerVisible = ref(false)
const currentGroup = ref<VariableGroup | null>(null)

// 获取数据类型字典
async function fetchDicts() {
  try {
    const res = await getDictsByType('decision_data_type')
    if (res.code === 200 && res.data) {
      dataTypeList.value = res.data
    }
  } catch {
    // ignore
  }
}

// 获取主题列表
async function fetchThemeList() {
  themeLoading.value = true
  try {
    const res = await getThemeList(themeQueryParams.value)
    if (res.code === 200 && res.data) {
      themeList.value = res.data.rows
      themeTotal.value = res.data.total
      // 默认选中第一个
      if (themeList.value.length > 0 && themeList.value[0]) {
        selectedThemeIndex.value = 0
        selectedThemeNo.value = themeList.value[0].themeNo
        fetchGroupList()
      }
    }
  } finally {
    themeLoading.value = false
  }
}

// 获取对象列表
async function fetchGroupList() {
  if (!selectedThemeNo.value) return
  groupLoading.value = true
  try {
    const res = await getGroupList({
      themeNo: selectedThemeNo.value,
      ...groupQueryParams.value,
    })
    if (res.code === 200 && res.data) {
      groupList.value = res.data.rows
      groupTotal.value = res.data.total
    }
  } finally {
    groupLoading.value = false
  }
}

// 点击主题行
function handleThemeClick(row: Theme, index: number) {
  selectedThemeIndex.value = index
  selectedThemeNo.value = row.themeNo
  groupQueryParams.value.pageNum = 1
  fetchGroupList()
}

// 主题行样式
function themeRowStyle({ rowIndex }: { rowIndex: number }) {
  if (selectedThemeIndex.value === rowIndex) {
    return { backgroundColor: '#e6f7ff' }
  }
  return {}
}

// 查看主题详情
function handleThemeDetail(row: Theme) {
  themeForm.value = { ...row }
  themeDrawerTitle.value = t('dataCenter.themeDetail')
  themeDrawerVisible.value = true
}

// 查看对象详情
function handleGroupDetail(row: VariableGroup) {
  groupForm.value = { ...row }
  groupDrawerTitle.value = t('dataCenter.objectDetail')
  groupDrawerVisible.value = true
}

// 查看属性
function handleViewVariable(row: VariableGroup) {
  currentGroup.value = row
  variableDrawerVisible.value = true
}

// 获取类型标签
function getTypeLabel(type: number) {
  const item = dataTypeList.value.find(d => d.dictValue === String(type))
  return item?.dictLabel || '-'
}

// 主题分页
function handleThemePageChange(page: number) {
  themeQueryParams.value.pageNum = page
  fetchThemeList()
}

function handleThemeSizeChange(size: number) {
  themeQueryParams.value.pageSize = size
  fetchThemeList()
}

// 对象分页
function handleGroupPageChange(page: number) {
  groupQueryParams.value.pageNum = page
  fetchGroupList()
}

function handleGroupSizeChange(size: number) {
  groupQueryParams.value.pageSize = size
  fetchGroupList()
}

onMounted(() => {
  fetchDicts()
  fetchThemeList()
})
</script>

<template>
  <div class="data-center">
    <!-- 主题列表 -->
    <div class="panel theme-panel">
      <div class="panel-header">
        <span class="panel-title">{{ t('dataCenter.theme') }}</span>
      </div>
      <el-table
        v-loading="themeLoading"
        :data="themeList"
        :row-style="themeRowStyle"
        border
        height="calc(100% - 100px)"
        @row-click="handleThemeClick"
      >
        <el-table-column prop="name" :label="t('dataCenter.themeName')" />
        <el-table-column prop="keycode" :label="t('dataCenter.packageName')" />
        <el-table-column prop="packageType" :label="t('dataCenter.themeType')">
          <template #default="{ row }">
            {{ row.packageType === '0' ? t('dataCenter.analysisObject') : t('dataCenter.derivedObject') }}
          </template>
        </el-table-column>
        <el-table-column :label="t('user.actions')" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="handleThemeDetail(row)">
              {{ t('common.detail') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-if="themeTotal > 0"
          :total="themeTotal"
          :current-page="themeQueryParams.pageNum"
          :page-size="themeQueryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleThemePageChange"
          @size-change="handleThemeSizeChange"
        />
      </div>
    </div>

    <!-- 对象列表 -->
    <div class="panel object-panel">
      <div class="panel-header">
        <span class="panel-title">{{ t('dataCenter.object') }}</span>
      </div>
      <el-table
        v-loading="groupLoading"
        :data="groupList"
        border
        height="calc(100% - 100px)"
      >
        <el-table-column prop="keycode" label="code" />
        <el-table-column prop="name" :label="t('dataCenter.objectName')" />
        <el-table-column prop="interfaceVersion" :label="t('dataCenter.version')" width="100" />
        <el-table-column prop="type" :label="t('dataCenter.objectType')" width="100">
          <template #default="{ row }">
            {{ getTypeLabel(row.type) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('user.actions')" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewVariable(row)">
              {{ t('dataCenter.viewProperties') }}
            </el-button>
            <el-button link type="primary" @click="handleGroupDetail(row)">
              {{ t('common.detail') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-if="groupTotal > 0"
          :total="groupTotal"
          :current-page="groupQueryParams.pageNum"
          :page-size="groupQueryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleGroupPageChange"
          @size-change="handleGroupSizeChange"
        />
      </div>
    </div>

    <!-- 主题详情抽屉 -->
    <el-drawer v-model="themeDrawerVisible" :title="themeDrawerTitle" size="50%">
      <el-form :model="themeForm" label-width="120px" class="detail-form">
        <el-form-item :label="t('dataCenter.themeName')">
          <el-input v-model="themeForm.name" disabled />
        </el-form-item>
        <el-form-item :label="t('dataCenter.packageName')">
          <el-input v-model="themeForm.keycode" disabled />
        </el-form-item>
        <el-form-item :label="t('dataCenter.themeType')">
          <span>{{ themeForm.packageType === '0' ? t('dataCenter.analysisObject') : t('dataCenter.derivedObject') }}</span>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 对象详情抽屉 -->
    <el-drawer v-model="groupDrawerVisible" :title="groupDrawerTitle" size="50%">
      <el-form :model="groupForm" label-width="120px" class="detail-form">
        <el-form-item :label="t('dataCenter.objectName')">
          <el-input v-model="groupForm.name" disabled />
        </el-form-item>
        <el-form-item :label="t('dataCenter.objectCode')">
          <el-input v-model="groupForm.keycode" disabled />
        </el-form-item>
        <el-form-item :label="t('dataCenter.objectType')">
          <span>{{ getTypeLabel(groupForm.type || 0) }}</span>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 属性抽屉 -->
    <VariableDrawer
      v-model="variableDrawerVisible"
      :group="currentGroup"
    />
  </div>
</template>

<style lang="scss" scoped>
.data-center {
  display: flex;
  gap: 20px;
  padding: 20px;
  height: calc(100vh - 100px);

  .panel {
    flex: 1;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    flex-direction: column;

    .panel-header {
      margin-bottom: 16px;

      .panel-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .pagination {
      display: flex;
      justify-content: center;
      padding-top: 16px;
    }
  }

  .detail-form {
    padding: 20px;
  }
}
</style>
