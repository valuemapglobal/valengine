<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/store/modules/app'
import type { LocaleType } from '@/locales'

const { locale } = useI18n()
const appStore = useAppStore()

const currentLang = computed(() => locale.value)

const languages = [
  { value: 'zh-CN', label: '简体中文', flag: '🇨🇳' },
  { value: 'en-US', label: 'English', flag: '🇺🇸' },
]

function handleChange(lang: LocaleType) {
  appStore.setLocale(lang)
}
</script>

<template>
  <el-dropdown trigger="click" @command="handleChange">
    <span class="lang-switch">
      <el-icon><svg-icon name="language" /></el-icon>
      <span class="current-lang">
        {{ currentLang === 'zh-CN' ? '中文' : 'EN' }}
      </span>
      <el-icon class="el-icon--right"><arrow-down /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in languages"
          :key="lang.value"
          :command="lang.value"
          :class="{ 'is-active': currentLang === lang.value }"
        >
          <span class="lang-flag">{{ lang.flag }}</span>
          <span>{{ lang.label }}</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style lang="scss" scoped>
.lang-switch {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;

  &:hover {
    background-color: #f5f7fa;
  }

  .current-lang {
    font-size: 14px;
    color: #606266;
  }
}

.lang-flag {
  margin-right: 8px;
}

:deep(.el-dropdown-menu__item) {
  &.is-active {
    color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }
}
</style>
