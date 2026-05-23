<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="title">零食管理系统</h1>
      <el-tabs v-model="activeTab" class="tabs">
        <el-tab-pane label="登录" name="login">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码"
                prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loginLoading" @click="handleLogin" style="width:100%">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="0">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="密码"
                prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password"
                placeholder="确认密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input v-model="registerForm.nickname" placeholder="昵称（选填）" prefix-icon="Avatar" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" :loading="registerLoading" @click="handleRegister" style="width:100%">
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { register as registerApi } from '@/api/auth'
import type { LoginDTO, RegisterDTO } from '@/types/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('login')
const loginLoading = ref(false)
const registerLoading = ref(false)
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

const loginForm = reactive<LoginDTO>({ username: '', password: '' })
const registerForm = reactive<RegisterDTO & { confirmPassword: string }>({
  username: '', password: '', confirmPassword: '', nickname: ''
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度 2-50', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度 6-50', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  loginLoading.value = true
  try {
    await userStore.login({ ...loginForm })
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch {
    // request 拦截器已弹窗
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return
  registerLoading.value = true
  try {
    await registerApi({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname || undefined
    })
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
    registerForm.username = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    registerForm.nickname = ''
  } catch {
    // request 拦截器已弹窗
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}
.title {
  text-align: center;
  font-size: 24px;
  color: #303133;
  margin-bottom: 24px;
}
.tabs {
  margin-bottom: -8px;
}
</style>
