<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { useDecisionStore } from '@/store/modules/decision'
import { getProductList, addProduct, updateProduct, deleteProduct, type Product } from '@/api/decision'

const { t } = useI18n()
const store = useDecisionStore()

// 状态
const loading = ref(false)
const searchName = ref('')
const dataList = ref<Product[]>([])
const total = ref(0)
const activeIndex = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)

// 弹窗状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref({ id: 0, name: '' })

// 计算属性
const hasMore = computed(() => dataList.value.length < total.value)

// 获取产品列表
async function fetchList(append = false) {
  loading.value = true
  try {
    const res = await getProductList({
      name: searchName.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    })
    if (res.code === 200 && res.data) {
      const list = res.data.list.map((item: Product) => ({
        ...item,
        productName: item.name,
      }))
      if (append) {
        dataList.value = [...dataList.value, ...list]
      } else {
        dataList.value = list
        // 默认选中第一个
        if (list.length > 0 && list[0]) {
          activeIndex.value = 0
          store.setProject(list[0].id)
        }
      }
      total.value = res.data.total
      store.setProductList(dataList.value, total.value)
    }
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pageNum.value = 1
  dataList.value = []
  fetchList()
}

// 点击产品
function handleClick(item: Product, index: number) {
  if (activeIndex.value === index) return
  activeIndex.value = index
  store.setProject(item.id)
}

// 加载更多
function handleScroll(e: Event) {
  const target = e.target as HTMLElement
  if (target.scrollTop + target.clientHeight >= target.scrollHeight - 10) {
    if (hasMore.value && !loading.value) {
      pageNum.value++
      fetchList(true)
    }
  }
}

// 新增
function handleAdd() {
  dialogTitle.value = t('common.add')
  formData.value = { id: 0, name: '' }
  dialogVisible.value = true
}

// 编辑
function handleEdit(item: Product) {
  dialogTitle.value = t('common.edit')
  formData.value = { id: item.id, name: item.name }
  dialogVisible.value = true
}

// 删除
async function handleDelete(item: Product) {
  try {
    await ElMessageBox.confirm(t('decision.deleteProductConfirm'), t('common.warning'), {
      type: 'warning',
    })
    const res = await deleteProduct(item.id)
    if (res.code === 200 && res.data) {
      ElMessage.success(t('common.success'))
      handleSearch()
    }
  } catch {
    // 取消删除
  }
}

// 提交表单
async function handleSubmit() {
  if (!formData.value.name.trim()) {
    ElMessage.warning(t('decision.pleaseEnterProductName'))
    return
  }

  const api = formData.value.id ? updateProduct : addProduct
  const res = await api(formData.value)
  if (res.code === 200) {
    ElMessage.success(t('common.success'))
    dialogVisible.value = false
    handleSearch()
  }
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="product-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchName"
        :placeholder="t('common.search')"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus" @click="handleAdd" />
    </div>

    <!-- 产品列表 -->
    <div class="list-container" v-loading="loading" @scroll="handleScroll">
      <div
        v-for="(item, index) in dataList"
        :key="item.id"
        class="product-item"
        :class="{ active: activeIndex === index }"
        @click="handleClick(item, index)"
      >
        <span class="name">{{ item.name }}</span>
        <div class="actions">
          <el-icon class="action-icon" @click.stop="handleEdit(item)"><Edit /></el-icon>
          <el-icon class="action-icon" @click.stop="handleDelete(item)"><Delete /></el-icon>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore && !loading" class="load-more">
        {{ t('common.loading') }}
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-input
        v-model="formData.name"
        :placeholder="t('decision.pleaseEnterProductName')"
        clearable
      />
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.product-list {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;

  .search-bar {
    padding: 20px;
    display: flex;
    gap: 10px;

    :deep(.el-input) {
      flex: 1;

      .el-input__wrapper {
        height: 40px;
        border-radius: 6px;
        padding-left: 32px;
      }

      .el-input__prefix {
        left: 12px;
      }
    }

    .el-button {
      width: 40px;
      height: 40px;
      padding: 0;
      border-radius: 6px;
      background: #f2f8fe;
      border: none;

      &:hover {
        background: #e6f4ff;
      }
    }
  }

  .list-container {
    flex: 1;
    padding: 0 20px 20px;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 4px;
      background-color: rgba(193, 193, 193, 1);
    }

    &::-webkit-scrollbar-track {
      border-radius: 4px;
      background-color: rgba(241, 241, 241, 1);
    }
  }

  .product-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 14px;
    height: 48px;
    margin-bottom: 10px;
    background: linear-gradient(90deg, #f5f7fa 0%, #fff 100%);
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;

    .name {
      flex: 1;
      width: calc(100% - 56px);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 14px;
      font-weight: 400;
      color: #606266;
    }

    .actions {
      display: flex;
      align-items: center;
      gap: 10px;

      .action-icon {
        font-size: 14px;
        color: #666666;
        opacity: 0.6;
        cursor: pointer;
        opacity: 0;
        transition: opacity 0.2s;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    &:hover {
      border-color: var(--el-color-primary-light-5);

      .actions .action-icon {
        opacity: 1;
      }
    }

    &.active {
      background: linear-gradient(90deg, var(--el-color-primary) 0%, var(--el-color-primary) 100%);
      border-color: var(--el-color-primary);

      .name {
        color: #fff;
      }

      .actions .action-icon {
        opacity: 1;
        color: rgba(255, 255, 255, 0.8);

        &:hover {
          color: #fff;
        }
      }
    }
  }

  .load-more {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 0;
    font-size: 16px;
    color: rgba(85, 161, 237, 1);
    cursor: pointer;
  }
}
</style>
