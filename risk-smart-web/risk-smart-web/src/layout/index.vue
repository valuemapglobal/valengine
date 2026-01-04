<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/store/modules/user'
import LangSwitch from '@/components/LangSwitch.vue'
import {

  Setting,
  User,
  SwitchButton,
  DataAnalysis,
  TrendCharts,
  Connection,
  List,
  Cpu,
} from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => {
  // 返回一级菜单路径
  const path = route.path
  const firstLevel = '/' + path.split('/')[1]
  return firstLevel
})

const menuList = computed(() => [
  {
    path: '/interface',
    title: t('menu.interfacePlatform'),
    icon: Connection,
    children: [
      { path: '/interface/scene', title: t('menu.dataSceneManagement') },
      { path: '/interface/management', title: t('menu.interfaceManagement') },
    ],
  },
  {
    path: '/data-center',
    title: t('menu.dataCenter'),
    icon: DataAnalysis,
    children: [
      { path: '/data-center/metadata', title: t('menu.metadata') },
      { path: '/data-center/variable', title: t('menu.featureVariable') },
    ],
  },
  {
    path: '/rule-pool',
    title: t('menu.rulePool'),
    icon: List,
    children: [
      { path: '/rule-pool/strategy', title: t('menu.ruleStrategyList') },
    ],
  },
  {
    path: '/decision',
    title: t('menu.decisionPlatform'),
    icon: TrendCharts,
    children: [
      { path: '/decision/product', title: t('menu.productModel') },
    ],
  },
  {
    path: '/platform-engine',
    title: t('menu.platformEngine'),
    icon: Cpu,
    children: [
      { path: '/platform-engine/flow', title: t('menu.workflowEngine') },
      { path: '/platform-engine/approval', title: t('menu.taskApproval') },
      { path: '/platform-engine/record', title: t('menu.taskRecords') },
    ],
  },
  {
    path: '/system',
    title: t('menu.system'),
    icon: Setting,
    children: [
      { path: '/system/user', title: t('menu.userManagement') },
      { path: '/system/role', title: t('menu.roleManagement') },
      { path: '/system/menu', title: t('menu.menuManagement') },
    ],
  },
])

function handleMenuSelect(path: string) {
  router.push(path)
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="layout-container">
    <!-- Top Header with Navigation -->
    <el-header class="layout-header">
      <!-- Logo -->
      <div class="header-logo">
        <img src="/gutu.ico" alt="logo" class="logo-img" />
        <span class="logo-text">ValEngine</span>
      </div>

      <!-- Top Navigation Menu -->
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :ellipsis="false"
        class="top-menu"
        @select="handleMenuSelect"
      >
        <template v-for="menu in menuList" :key="menu.path">
          <el-sub-menu v-if="menu.children" :index="menu.path">
            <template #title>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.path"
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path">
            <span>{{ menu.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>

      <!-- Right Actions -->
      <div class="header-right">
        <LangSwitch />
        <el-dropdown>
          <span class="user-dropdown">
            <span class="username">{{ userStore.userInfo?.username || t('layout.account') }}</span>
            <el-icon><User /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>{{ t('layout.profile') }}</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                {{ t('layout.logout') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- Main Content -->
    <el-main class="layout-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
  height: 60px;
  z-index: 100;

  .header-logo {
    display: flex;
    align-items: center;
    margin-right: 40px;

    .logo-img {
      width: 36px;
      height: 36px;
      margin-right: 15px;
    }

    .logo-text {
      font-size: 24px;
      font-weight: bold;
      background: linear-gradient(
        to right,
        #2888e8 0%,
        #10b981 70%,
        #2888e8 100%
      );
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
      color: transparent;
      letter-spacing: 1px;
    }
  }

  .top-menu {
    flex: 1;
    border-bottom: none;
    height: 100%;

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 60px;
      line-height: 60px;
      font-size: 14px;
      color: #303133;
      border-bottom: none !important;
      padding: 0 20px;

      &:hover {
        background-color: transparent;
        color: var(--el-color-primary);
      }
    }

    :deep(.el-menu-item.is-active),
    :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
      color: var(--el-color-primary);
      border-bottom: none !important;
    }

    :deep(.el-sub-menu__icon-arrow) {
      margin-left: 5px;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .user-dropdown {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 8px 12px;
      border-radius: 4px;

      &:hover {
        background-color: #f5f7fa;
      }

      .username {
        color: #333;
        font-size: 14px;
      }
    }
  }
}

.layout-main {
  flex: 1;
  background: #f5f7fa;
  padding: 0;
  overflow: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
