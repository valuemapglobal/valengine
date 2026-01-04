<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDecisionStore, ModelType, ModelTypeNames } from '@/store/modules/decision'
import RuleModel from './RuleModel.vue'
import ClassifyModel from './ClassifyModel.vue'
import ScoreModel from './ScoreModel.vue'
import RateModel from './RateModel.vue'
import LimitModel from './LimitModel.vue'
import PriceModel from './PriceModel.vue'

const { t } = useI18n()
const store = useDecisionStore()

// 模型按钮列表
const modelButtons = [
  { label: 'decision.ruleModel', type: ModelType.RULE, component: 'rule' },
  { label: 'decision.classifyModel', type: ModelType.CLASSIFY, component: 'classify' },
  { label: 'decision.scoreModel', type: ModelType.SCORE, component: 'score' },
  { label: 'decision.rateModel', type: ModelType.RATE, component: 'rate' },
  { label: 'decision.limitModel', type: ModelType.LIMIT, component: 'limit' },
  { label: 'decision.priceModel', type: ModelType.PRICE, component: 'price' },
]

// 当前激活的模型
const activeModel = ref<string>('rule')

// 切换模型
function handleModelClick(item: typeof modelButtons[0]) {
  activeModel.value = item.component
  store.setModelType(item.type, ModelTypeNames[item.type])
}

// 发布策略
function handleRelease() {
  // TODO: 实现发布逻辑
}

onMounted(() => {
  store.setModelType(ModelType.RULE)
})
</script>

<template>
  <div class="model-modules">
    <!-- 模型切换按钮 -->
    <div class="model-header">
      <div class="model-buttons">
        <el-button
          v-for="item in modelButtons"
          :key="item.component"
          :type="activeModel === item.component ? 'primary' : 'default'"
          @click="handleModelClick(item)"
        >
          {{ t(item.label) }}
        </el-button>
      </div>

      <div class="model-actions">
        <el-button type="primary" @click="handleRelease">
          {{ t('decision.confirmRelease') }}
        </el-button>
      </div>
    </div>

    <!-- 模型内容区域 -->
    <div class="model-content">
      <RuleModel v-if="activeModel === 'rule'" />
      <ClassifyModel v-else-if="activeModel === 'classify'" />
      <ScoreModel v-else-if="activeModel === 'score'" />
      <RateModel v-else-if="activeModel === 'rate'" />
      <LimitModel v-else-if="activeModel === 'limit'" />
      <PriceModel v-else-if="activeModel === 'price'" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.model-modules {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;

  .model-header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 17px;

    .model-buttons {
      display: flex;
      gap: 16px;

      .el-button {
        background: rgba(0, 0, 0, 0.04);
        font-size: 14px;
        font-weight: normal;
        color: #606266;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        border: none;

        &:hover {
          background: rgba(0, 0, 0, 0.08);
        }

        &:focus {
          box-shadow: none;
        }

        &.el-button--primary {
          background-color: var(--el-color-primary);
          color: #fff;
        }
      }
    }

    .model-actions {
      display: flex;
      align-items: center;
      gap: 10px;

      .el-button {
        border-radius: 12px;
      }
    }
  }

  .model-content {
    flex: 1;
    height: calc(100% - 55px);
    overflow: hidden;
  }
}
</style>
