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
const countdown = ref(0)
const isSending = ref(false)

const form = reactive({
  phone: '',
  code: '',
})

const rules = computed<FormRules>(() => ({
  phone: [
    { required: true, message: t('login.pleaseEnterPhone'), trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: t('login.invalidPhoneFormat'),
      trigger: 'blur',
    },
  ],
  code: [
    { required: true, message: t('login.pleaseEnterCode'), trigger: 'blur' },
  ],
}))

async function sendCode() {
  // Validate phone first
  if (!form.phone || !/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning(t('login.pleaseEnterValidPhone'))
    return
  }

  if (countdown.value > 0) return

  isSending.value = true
  try {
    // TODO: Call actual send SMS API
    // import { sendSms } from '@/api/auth'
    // await sendSms({ phone: form.phone })

    ElMessage.success(t('login.codeSent'))

    // Start countdown
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch {
    ElMessage.error(t('login.sendCodeFailed'))
  } finally {
    isSending.value = false
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // TODO: Call actual verify login API
    // import { verifyLogin } from '@/api/auth'
    // const res = await verifyLogin(form)
    // localStorage.setItem('token', res.data.access_token)

    // Mock login for development
    const mockToken = 'mock-token-' + Date.now()
    const mockUserInfo = {
      id: '1',
      username: form.phone,
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
</script>

<template>
  <div class="code-login">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      @keyup.enter="handleSubmit"
    >
      <el-form-item prop="phone">
        <el-input
          v-model="form.phone"
          :placeholder="t('login.pleaseEnterPhone')"
          size="large"
          class="login-input"
        />
      </el-form-item>

      <el-form-item prop="code">
        <el-input
          v-model="form.code"
          :placeholder="t('login.pleaseEnterCode')"
          size="large"
          class="code-input"
        >
          <template #append>
            <div
              class="send-btn"
              :class="{ disabled: countdown > 0 || isSending }"
              @click="sendCode"
            >
              <template v-if="countdown > 0">
                {{ countdown }}s
              </template>
              <template v-else>
                {{ t('login.sendCode') }}
              </template>
            </div>
          </template>
        </el-input>
      </el-form-item>

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

      <!-- Agreement Text -->
      <div class="agreement-text">
        {{ t('login.loginAgreement') }}
        <a href="javascript:;">{{ t('login.userAgreement') }}</a>
        {{ t('login.and') }}
        <a href="javascript:;">{{ t('login.privacyPolicy') }}</a>
      </div>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
.code-login {
  .login-input {
    :deep(.el-input__wrapper) {
      background-color: #fff;
      border-radius: 6px;
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      padding: 4px 16px;

      .el-input__inner {
        height: 48px;
      }
    }
  }

  .code-input {
    :deep(.el-input__wrapper) {
      background-color: #fff;
      border-radius: 6px 0 0 6px;
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      border-right: none;
      padding: 4px 16px;

      .el-input__inner {
        height: 48px;
      }
    }

    :deep(.el-input-group__append) {
      background-color: #fff;
      border-radius: 0 6px 6px 0;
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      border-left: none;
      padding: 0;

      .send-btn {
        padding: 0 20px;
        height: 56px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #409eff;
        font-size: 14px;
        cursor: pointer;
        white-space: nowrap;

        &:hover:not(.disabled) {
          color: #66b1ff;
        }

        &.disabled {
          color: #c0c4cc;
          cursor: not-allowed;
        }
      }
    }
  }

  .submit-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    border-radius: 8px;
    margin-top: 20px;
  }

  .agreement-text {
    margin-top: 20px;
    font-size: 14px;
    color: #666;
    text-align: center;

    a {
      color: #409eff;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
