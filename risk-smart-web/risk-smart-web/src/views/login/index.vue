<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/store/modules/app'
import PasswordLogin from './components/PasswordLogin.vue'
import CodeLogin from './components/CodeLogin.vue'
import loginImage from './images/login.png'

const { t } = useI18n()
const route = useRoute()
const appStore = useAppStore()

// Login type: 0 = password, 1 = code
const loginType = ref(0)

// Watch route query for login type
watch(
  () => route.query.type,
  (type) => {
    if (type !== undefined) {
      loginType.value = Number(type)
    }
  },
  { immediate: true }
)

function switchLoginType(type: number) {
  loginType.value = type
}

function toggleLang() {
  const newLang = appStore.locale === 'zh-CN' ? 'en-US' : 'zh-CN'
  appStore.setLocale(newLang)
}
</script>

<template>
  <div class="login-page">
    <!-- Language Toggle -->
    <div class="lang-toggle" @click="toggleLang">
      {{ appStore.locale === 'zh-CN' ? 'EN' : '中文' }}
    </div>

    <!-- Left Side - Background -->
    <div class="login-left">
      <img :src="loginImage" alt="login" class="login-image" />
      <div class="login-info">
        <h1 class="title">{{ t('login.platformTitle') }}</h1>
        <p class="subtitle">{{ t('login.platformSubtitle') }}</p>
      </div>
    </div>

    <!-- Right Side - Login Form -->
    <div class="login-right">
      <div class="login-form-wrapper">
        <!-- Login Type Tabs -->
        <div class="login-tabs">
          <div
            class="tab-item"
            :class="{ active: loginType === 1 }"
            @click="switchLoginType(1)"
          >
            {{ t('login.codeLogin') }}
          </div>
          <div
            class="tab-item"
            :class="{ active: loginType === 0 }"
            @click="switchLoginType(0)"
          >
            {{ t('login.passwordLogin') }}
          </div>
        </div>

        <!-- Password Login Form -->
        <PasswordLogin v-if="loginType === 0" @switch="switchLoginType" />

        <!-- Code Login Form -->
        <CodeLogin v-if="loginType === 1" @switch="switchLoginType" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  min-width: 1200px;
  background-image: url('./images/bg.png');
  background-size: cover;
  background-position: center;
}

.lang-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.1);
  color: #333;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  z-index: 100;
  transition: background 0.2s;

  &:hover {
    background: rgba(0, 0, 0, 0.2);
  }
}

.login-left {
  position: relative;
  width: 55%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-image {
    width: 80%;
    max-width: 600px;
  }

  .login-info {
    position: absolute;
    top: 60px;
    left: 100px;

    .title {
      font-size: 48px;
      font-weight: 600;
      color: #1a1a2e;
      margin: 0 0 20px;
      font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
    }

    .subtitle {
      font-size: 16px;
      line-height: 28px;
      color: #4a4a4a;
      max-width: 500px;
      margin: 0;
    }
  }
}

.login-right {
  width: 45%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
}

.login-form-wrapper {
  width: 420px;
  padding: 40px;
}

.login-tabs {
  display: flex;
  margin-bottom: 32px;
  border-bottom: 1px solid #e8e8e8;

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 12px 0;
    font-size: 18px;
    color: #999;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    transition: all 0.3s;

    &:hover {
      color: #409eff;
    }

    &.active {
      color: #409eff;
      font-weight: 600;
      border-bottom-color: #409eff;
    }
  }
}

@media screen and (max-width: 1400px) {
  .login-left .login-info {
    left: 60px;

    .title {
      font-size: 36px;
    }
  }
}
</style>
