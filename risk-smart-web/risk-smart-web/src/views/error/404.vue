<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>{{ t('error.notFound') }}</p>
      <el-button type="primary" @click="goHome">{{ t('error.backToHome') }}</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;

  .content {
    text-align: center;

    h1 {
      font-size: 120px;
      color: #409eff;
      margin: 0;
      line-height: 1;
    }

    p {
      font-size: 24px;
      color: #666;
      margin: 20px 0 40px;
    }
  }
}
</style>
