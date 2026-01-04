<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'

const { t } = useI18n()

// 规则组数据
interface RuleGroup {
  id: number
  name: string
  code: string
  description?: string
  status: number
  rules: Rule[]
}

interface Rule {
  id: number
  name: string
  code: string
  expression?: string
  status: number
  priority: number
}

const loading = ref(false)
const ruleGroups = ref<RuleGroup[]>([])
const expandedGroups = ref<number[]>([])

// 模拟数据
function fetchData() {
  loading.value = true
  setTimeout(() => {
    ruleGroups.value = [
      {
        id: 1,
        name: '反欺诈规则组',
        code: 'ANTI_FRAUD',
        description: '用于识别欺诈行为的规则集合',
        status: 1,
        rules: [
          { id: 101, name: '身份证黑名单', code: 'ID_BLACKLIST', status: 1, priority: 1 },
          { id: 102, name: '手机号黑名单', code: 'PHONE_BLACKLIST', status: 1, priority: 2 },
          { id: 103, name: '设备指纹检测', code: 'DEVICE_CHECK', status: 1, priority: 3 },
        ],
      },
      {
        id: 2,
        name: '信用评估规则组',
        code: 'CREDIT_EVAL',
        description: '用于信用评估的规则集合',
        status: 1,
        rules: [
          { id: 201, name: '收入验证', code: 'INCOME_VERIFY', status: 1, priority: 1 },
          { id: 202, name: '负债检查', code: 'DEBT_CHECK', status: 1, priority: 2 },
        ],
      },
    ]
    expandedGroups.value = ruleGroups.value.map(g => g.id)
    loading.value = false
  }, 500)
}

// 切换展开状态
function toggleExpand(groupId: number) {
  const index = expandedGroups.value.indexOf(groupId)
  if (index > -1) {
    expandedGroups.value.splice(index, 1)
  } else {
    expandedGroups.value.push(groupId)
  }
}

// 判断是否展开
function isExpanded(groupId: number) {
  return expandedGroups.value.includes(groupId)
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="rule-model" v-loading="loading">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-input
        style="width: 240px"
        :placeholder="t('common.search')"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus">
        {{ t('decision.addRuleGroup') }}
      </el-button>
    </div>

    <!-- 规则组列表 -->
    <div class="rule-groups">
      <div
        v-for="group in ruleGroups"
        :key="group.id"
        class="rule-group"
      >
        <!-- 规则组头部 -->
        <div class="group-header" @click="toggleExpand(group.id)">
          <div class="group-info">
            <el-icon class="expand-icon" :class="{ expanded: isExpanded(group.id) }">
              <i class="el-icon-arrow-right" />
            </el-icon>
            <span class="group-name">{{ group.name }}</span>
            <el-tag size="small" type="info">{{ group.code }}</el-tag>
          </div>
          <div class="group-actions" @click.stop>
            <el-button link type="primary" :icon="Plus">{{ t('decision.addRule') }}</el-button>
            <el-button link type="primary" :icon="Edit">{{ t('common.edit') }}</el-button>
            <el-button link type="danger" :icon="Delete">{{ t('common.delete') }}</el-button>
          </div>
        </div>

        <!-- 规则列表 -->
        <el-collapse-transition>
          <div v-show="isExpanded(group.id)" class="rules-list">
            <el-table :data="group.rules" border stripe>
              <el-table-column prop="name" :label="t('decision.ruleName')" min-width="150" />
              <el-table-column prop="code" :label="t('decision.ruleCode')" width="180" />
              <el-table-column prop="priority" :label="t('decision.priority')" width="100" align="center" />
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
        </el-collapse-transition>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.rule-model {
  height: 100%;
  display: flex;
  flex-direction: column;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  .rule-groups {
    flex: 1;
    overflow-y: auto;

    .rule-group {
      margin-bottom: 16px;
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      overflow: hidden;

      .group-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        background: #f5f7fa;
        cursor: pointer;

        &:hover {
          background: #ebeef5;
        }

        .group-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .expand-icon {
            transition: transform 0.2s;

            &.expanded {
              transform: rotate(90deg);
            }
          }

          .group-name {
            font-size: 15px;
            font-weight: 500;
            color: #303133;
          }
        }

        .group-actions {
          display: flex;
          gap: 8px;
        }
      }

      .rules-list {
        padding: 16px;
        background: #fff;
      }
    }
  }
}
</style>
