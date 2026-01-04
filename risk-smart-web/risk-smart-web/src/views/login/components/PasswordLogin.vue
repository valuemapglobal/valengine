<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import type { FormInstance, FormRules } from 'element-plus'

const emit = defineEmits<{
  switch: [type: number]
}>()

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const agreePolicy = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = computed<FormRules>(() => ({
  username: [
    { required: true, message: t('login.pleaseEnterUsername'), trigger: 'blur' },
  ],
  password: [
    { required: true, message: t('login.pleaseEnterPassword'), trigger: 'blur' },
  ],
}))

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  if (!agreePolicy.value) {
    ElMessage.warning(t('login.pleaseAgreePolicy'))
    return
  }

  loading.value = true
  try {
    // TODO: Call actual login API
    // import { login } from '@/api/auth'
    // const res = await login(form)
    // localStorage.setItem('token', res.data.access_token)

    // Mock login for development
    const mockToken = 'mock-token-' + Date.now()
    const mockUserInfo = {
      id: '1',
      username: form.username,
      roles: ['admin'],
      permissions: ['*'],
    }

    userStore.setToken(mockToken)
    userStore.setUserInfo(mockUserInfo)

    ElMessage.success(t('login.loginSuccess'))

    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch {
    ElMessage.error(t('login.loginFailed'))
  } finally {
    loading.value = false
  }
}

function handleForgotPassword() {
  // TODO: Implement forgot password
  ElMessage.info('Forgot password feature coming soon')
}
</script>

<template>
  <div class="password-login">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      @keyup.enter="handleSubmit"
    >
      <div class="form-label">{{ t('login.usernameOrPhone') }}</div>
      <el-form-item prop="username">
        <el-input
          v-model="form.username"
          :placeholder="t('login.pleaseEnterUsernameOrPhone')"
          size="large"
          class="login-input"
        />
      </el-form-item>

      <div class="form-label-row">
        <span class="form-label">{{ t('login.password') }}</span>
        <a class="forgot-link" @click="handleForgotPassword">
          {{ t('login.forgotPassword') }}
        </a>
      </div>
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          :placeholder="t('login.pleaseEnterPassword')"
          size="large"
          show-password
          class="login-input"
        />
      </el-form-item>

      <!-- Agreement -->
      <div class="agreement">
        <el-checkbox v-model="agreePolicy" />
        <span class="agreement-text">
          {{ t('login.readAndAgree') }}
          <a href="javascript:;">{{ t('login.userAgreement') }}</a>
          {{ t('login.and') }}
          <a href="javascript:;">{{ t('login.privacyPolicy') }}</a>
        </span>
      </div>

      <!-- Submit Button -->
      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="loading"
        @click="handleSubmit"
      >
        {{ t('login.login') }}
      </el-button>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
.password-login {
  .form-label {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
  }

  .form-label-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;

    .form-label {
      margin-bottom: 0;
    }

    .forgot-link {
      font-size: 14px;
      color: #409eff;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .login-input {
    :deep(.el-input__wrapper) {
      background-color: #f5f7fa;
      border-radius: 8px;
      box-shadow: none;
      padding: 4px 16px;

      .el-input__inner {
        height: 48px;
      }
    }
  }

  .agreement {
    display: flex;
    align-items: flex-start;
    margin: 20px 0;
    gap: 8px;

    .agreement-text {
      font-size: 14px;
      color: #666;
      line-height: 22px;

      a {
        color: #409eff;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }

  .submit-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    border-radius: 8px;
  }
}
</style>
