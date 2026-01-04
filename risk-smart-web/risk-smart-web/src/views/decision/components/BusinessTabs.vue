<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import { useDecisionStore } from '@/store/modules/decision'
import { getBusinessList, addBusiness, updateBusiness, deleteBusiness, type Business } from '@/api/decision'
import ModelModules from '../modules/index.vue'

const { t } = useI18n()
const store = useDecisionStore()

// 状态
const tabs = ref<Business[]>([])
const activeTab = ref<number | null>(null)
const addInputVisible = ref(false)
const addInputValue = ref('')
const inputRef = ref<HTMLInputElement>()

// 弹窗状态
const editDialogVisible = ref(false)
const editFormData = ref({ id: 0, name: '' })

// 获取业务场景列表
async function fetchTabs() {
  const res = await getBusinessList()
  if (res.code === 200 && res.data) {
    tabs.value = res.data.list
    store.setBusinessList(tabs.value)
    // 默认选中第一个
    if (tabs.value.length > 0 && !activeTab.value && tabs.value[0]) {
      activeTab.value = tabs.value[0].id
      store.setBusiness(tabs.value[0])
    }
  }
}

// 切换标签
function handleTabClick(item: Business) {
  activeTab.value = item.id
  store.setBusiness(item)
}

// 显示新增输入框
function showAddInput() {
  addInputValue.value = ''
  addInputVisible.value = true
  setTimeout(() => {
    inputRef.value?.focus()
  }, 100)
}

// 新增业务场景
async function handleAdd() {
  if (!addInputValue.value.trim()) {
    ElMessage.warning(t('decision.pleaseEnterBusinessName'))
    addInputVisible.value = false
    return
  }

  const res = await addBusiness({ name: addInputValue.value })
  if (res.code === 200 && res.data) {
    ElMessage.success(t('common.success'))
    addInputVisible.value = false
    fetchTabs()
  }
}

// 编辑业务场景
function handleEdit(item: Business) {
  editFormData.value = { id: item.id, name: item.name }
  editDialogVisible.value = true
}

// 提交编辑
async function handleEditSubmit() {
  if (!editFormData.value.name.trim()) {
    ElMessage.warning(t('decision.pleaseEnterBusinessName'))
    return
  }

  const res = await updateBusiness(editFormData.value)
  if (res.code === 200 && res.data) {
    ElMessage.success(t('common.success'))
    editDialogVisible.value = false
    fetchTabs()
  }
}

// 删除业务场景
async function handleDelete(item: Business) {
  try {
    await ElMessageBox.confirm(t('decision.deleteBusinessConfirm'), t('common.warning'), {
      type: 'warning',
    })
    const res = await deleteBusiness(item.id)
    if (res.code === 200 && res.data) {
      ElMessage.success(t('common.success'))
      fetchTabs()
    }
  } catch {
    // 取消删除
  }
}

onMounted(() => {
  fetchTabs()
})

// 监听产品变化，重新加载业务场景
watch(() => store.currentProjectCode, () => {
  fetchTabs()
})
</script>

<template>
  <div class="business-tabs">
    <!-- 标签栏 -->
    <ul class="tabs-header">
      <li
        v-for="item in tabs"
        :key="item.id"
        class="tab-item"
        :class="{ active: activeTab === item.id }"
        @click="handleTabClick(item)"
      >
        <!-- 编辑按钮 (非系统默认的才显示) -->
        <el-icon
          v-if="item.deptId !== 1"
          class="tab-action edit"
          @click.stop="handleEdit(item)"
        >
          <Edit />
        </el-icon>
        <div v-else class="tab-action-placeholder" />

        <span class="tab-name">{{ item.name }}</span>

        <!-- 删除按钮 -->
        <el-icon
          v-if="item.deptId !== 1"
          class="tab-action delete"
          @click.stop="handleDelete(item)"
        >
          <Delete />
        </el-icon>
        <div v-else class="tab-action-placeholder" />
      </li>

      <!-- 新增按钮 -->
      <li class="tab-item add-tab" :class="{ 'input-mode': addInputVisible }">
        <el-input
          v-if="addInputVisible"
          ref="inputRef"
          v-model="addInputValue"
          :placeholder="t('decision.pleaseEnterBusinessName')"
          @blur="addInputVisible = false"
          @keyup.enter="handleAdd"
        />
        <div v-else class="add-button" @click="showAddInput">
          <div class="add-icon" />
          <span>{{ t('common.add') }}</span>
        </div>
      </li>
    </ul>

    <!-- 内容区域 -->
    <div class="tabs-content">
      <ModelModules />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editDialogVisible" :title="t('common.edit')" width="400px">
      <el-input
        v-model="editFormData.name"
        :placeholder="t('decision.pleaseEnterBusinessName')"
        clearable
      />
      <template #footer>
        <el-button @click="editDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleEditSubmit">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.business-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;

  .tabs-header {
    position: relative;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    margin: 0;
    padding: 0;
    list-style: none;
    overflow-x: auto;

    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 2px;
      background-color: #c1c1c1;
    }

    .tab-item {
      display: flex;
      align-items: center;
      justify-content: space-around;
      padding: 0 8px;
      height: 50px;
      background: rgba(0, 0, 0, 0.04);
      margin-right: 4px;
      border-radius: 5px 5px 0 0;
      font-size: 14px;
      font-weight: normal;
      line-height: 16px;
      color: #606266;
      cursor: pointer;
      transition: all 0.2s;

      .tab-name {
        margin: 0 8px;
        white-space: nowrap;
      }

      .tab-action {
        font-size: 12px;
        opacity: 0;
        z-index: -100;
        transition: opacity 0.3s;
        cursor: pointer;

        &.edit {
          margin-right: 8px;
        }

        &.delete {
          margin-left: 8px;
        }

        &:hover {
          color: var(--el-color-primary);
        }
      }

      .tab-action-placeholder {
        width: 10px;
        height: 10px;
      }

      &:hover {
        .tab-action {
          opacity: 1;
          z-index: 6;
        }
      }

      &.active {
        background: #fff;
        color: var(--el-color-primary);
        font-size: 14px;
        font-weight: 500;
        line-height: 16px;
        border: 1px solid #e4e7ed;
        border-bottom: none;

        .tab-action {
          opacity: 1;
          z-index: 6;
        }
      }

      &.add-tab {
        background: var(--el-color-primary);
        color: #fff;
        padding: 0;
        justify-content: center;

        &.input-mode {
          background: #fff;
          padding: 0;

          :deep(.el-input) {
            .el-input__wrapper {
              height: 50px;
              line-height: 50px;
              border-radius: 6px 6px 0 0;
              border: none;
              box-shadow: none;
            }
          }
        }

        .add-button {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 96px;
          height: 100%;
          gap: 8px;

          .add-icon {
            position: relative;
            width: 10px;
            height: 10px;
            display: flex;
            align-items: center;
            justify-content: center;

            &::before,
            &::after {
              position: absolute;
              content: '';
              display: block;
              width: 10px;
              height: 2px;
              background: #ffffff;
              border-radius: 2px;
            }

            &::after {
              width: 2px;
              height: 10px;
            }
          }
        }
      }
    }
  }

  .tabs-content {
    flex: 1;
    overflow: hidden;
    background-color: #fff;
    border: 1px solid #e4e7ed;
    border-top: none;
    border-radius: 0 0 8px 8px;
  }
}
</style>
