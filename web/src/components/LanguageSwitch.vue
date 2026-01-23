<template>
  <div class="language-switch">
    <el-dropdown
      trigger="click"
      @command="handleCommand"
      :append-to-body="false"
    >
      <span class="language-btn">
        <i class="el-icon-s-tools"></i>
        <span class="language-text">{{ currentLanguage }}</span>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          :command="'zh'"
          :class="{ active: $i18n.locale === 'zh' }"
        >
          {{ $t('language.zh') }}
        </el-dropdown-item>
        <el-dropdown-item
          :command="'en'"
          :class="{ active: $i18n.locale === 'en' }"
        >
          {{ $t('language.en') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: 'LanguageSwitch',
  computed: {
    currentLanguage() {
      return this.$i18n.locale === 'zh' ? '中文' : 'English'
    },
  },
  methods: {
    handleCommand(command) {
      this.$i18n.locale = command
      localStorage.setItem('locale', command)
      // 刷新 Element UI 的语言包（如果需要）
      // this.$message.success(this.$t('language.switch'))
    },
  },
}
</script>

<style lang="less" scoped>
.language-switch {
  .language-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: auto;
    min-width: 80px;
    height: 30px;
    padding: 0 10px;
    border-radius: 6px;
    cursor: pointer;
    color: var(--text-color);
    font-size: 14px;
    margin-left: 10px;

    &:hover {
      background: var(--header-bg);
    }

    i {
      margin-right: 5px;
    }

    .language-text {
      white-space: nowrap;
    }
  }
}

:deep(.el-dropdown-menu__item) {
  &.active {
    color: var(--primary-color);
    background-color: #e9f3fd;
  }
}
</style>






