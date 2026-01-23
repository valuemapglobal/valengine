<template>
  <div class="theme-switch-wrapper" @click="toggleTheme">
    <div class="theme-switch" :class="{ 'is-dark': isDark }">
      <!-- Sun Icon -->
      <div class="icon sun-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="4" fill="currentColor"/>
          <path d="M12 5V3M12 21v-2M5 12H3M21 12h-2M7.05 7.05L5.636 5.636M18.364 18.364l-1.414-1.414M7.05 16.95l-1.414 1.414M18.364 5.636l-1.414 1.414"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"/>
        </svg>
      </div>
      <!-- Moon Icon -->
      <div class="icon moon-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"
                fill="currentColor"/>
        </svg>
      </div>
      <!-- Toggle Ball -->
      <div class="toggle-ball"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ThemeSwitch',
  data() {
    return {
      isDark: localStorage.getItem('theme') === 'dark'
    }
  },
  mounted() {
    // Sync with store
    this.isDark = this.$store.state.theme === 'dark'
  },
  methods: {
    toggleTheme() {
      this.isDark = !this.isDark
      const theme = this.isDark ? 'dark' : 'light'
      this.$store.dispatch('setTheme', theme)
    }
  },
  watch: {
    '$store.state.theme': {
      handler(newTheme) {
        this.isDark = newTheme === 'dark'
      },
      immediate: true
    }
  }
}
</script>

<style lang="less" scoped>
.theme-switch-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: var(--bg-color-lighter);
  }
}

.theme-switch {
  position: relative;
  width: 56px;
  height: 28px;
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  border-radius: 14px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);

  &.is-dark {
    background: linear-gradient(135deg, #2d3436 0%, #636e72 100%);

    .sun-icon {
      opacity: 0;
      transform: translateY(20px) rotate(-90deg);
    }

    .moon-icon {
      opacity: 1;
      transform: translateY(0) rotate(0deg);
    }

    .toggle-ball {
      transform: translateX(28px);
      background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
      box-shadow:
        0 0 8px rgba(253, 203, 110, 0.6),
        inset -2px -2px 4px rgba(0, 0, 0, 0.1);
    }
  }

  .icon {
    position: absolute;
    top: 50%;
    width: 16px;
    height: 16px;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

    svg {
      width: 100%;
      height: 100%;
    }
  }

  .sun-icon {
    left: 7px;
    transform: translateY(-50%);
    color: #ffeaa7;
    opacity: 1;
  }

  .moon-icon {
    right: 7px;
    transform: translateY(-50%) translateY(-20px) rotate(90deg);
    color: #ffeaa7;
    opacity: 0;
  }

  .toggle-ball {
    position: absolute;
    top: 3px;
    left: 3px;
    width: 22px;
    height: 22px;
    background: linear-gradient(135deg, #fff 0%, #f5f5f5 100%);
    border-radius: 50%;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow:
      0 2px 8px rgba(0, 0, 0, 0.2),
      inset -2px -2px 4px rgba(0, 0, 0, 0.05);

    &::before {
      content: '';
      position: absolute;
      top: 3px;
      left: 3px;
      width: 6px;
      height: 6px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 50%;
      opacity: 0.6;
    }
  }
}

// Hover animation
.theme-switch-wrapper:hover .theme-switch {
  box-shadow:
    inset 0 2px 4px rgba(0, 0, 0, 0.1),
    0 0 12px rgba(9, 132, 227, 0.3);

  &.is-dark {
    box-shadow:
      inset 0 2px 4px rgba(0, 0, 0, 0.1),
      0 0 12px rgba(253, 203, 110, 0.3);
  }
}

// Active press effect
.theme-switch-wrapper:active .toggle-ball {
  width: 26px;
}

.theme-switch.is-dark .theme-switch-wrapper:active .toggle-ball {
  transform: translateX(24px);
}
</style>
