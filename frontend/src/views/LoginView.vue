<template>
  <div class="login-page">
    <!-- 背景层 -->
    <div class="login-bg"></div>

    <!-- 毛玻璃卡片 -->
    <div class="login-card anim-scale-in">
      <div class="login-logo">
        <span class="logo-bear">🐻</span>
        <h1>零食小管家</h1>
        <p>好好管理零食，好好享受生活 ~</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
            ✨ 登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span class="footer-tip">还没有账号？<a class="footer-link" @click="switchToRegister">立即注册</a></span>
      </div>
    </div>

    <!-- 注册卡片 -->
    <div v-if="showRegister" class="login-card anim-scale-in" style="margin-top:0">
      <div class="login-logo">
        <span class="logo-bear">🐻</span>
        <h1>创建账号</h1>
        <p>加入零食小管家，开始管理吧 ~</p>
      </div>

      <el-form ref="regFormRef" :model="regForm" :rules="regRules" @keyup.enter="handleRegister" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="regForm.username" placeholder="请输入用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="regForm.nickname" placeholder="请输入昵称（选填）" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="regForm.password" type="password" show-password placeholder="请输入密码（最少6位）" :prefix-icon="Lock" size="large" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="regForm.confirmPassword" type="password" show-password placeholder="请确认密码" :prefix-icon="Lock" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="regLoading" @click="handleRegister" class="login-btn">
            ✨ 注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span class="footer-tip">已有账号？<a class="footer-link" @click="switchToLogin">返回登录</a></span>
        <span class="footer-tip" style="margin-top:2px">🐰 注册即默认创建 8 个零食分类哦~</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getToken } from '@/utils/token'
import { register } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const regFormRef = ref<FormInstance>()
const loading = ref(false)
const regLoading = ref(false)
const showRegister = ref(false)

// 已登录直接跳转
if (getToken()) { router.replace('/dashboard') }

const form = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', nickname: '', password: '', confirmPassword: '' })

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码最少6位', trigger: 'blur' }]
}
const regRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码最少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (_r: any, v: string, cb: any) => v === regForm.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
  ]
}

async function handleLogin() {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return
  loading.value = true
  try {
    await userStore.login({ username: form.username, password: form.password })
    ElMessage.success('🐻 欢迎回来！')
    router.push('/dashboard')
  } catch {
    // 错误由 request 拦截器统一处理
  } finally {
    loading.value = false
  }
}

function switchToRegister() { showRegister.value = true }
function switchToLogin() { showRegister.value = false }

async function handleRegister() {
  const ok = await regFormRef.value?.validate().catch(() => false)
  if (!ok) return
  regLoading.value = true
  try {
    await register({ username: regForm.username, password: regForm.password, nickname: regForm.nickname || undefined })
    ElMessage.success('🎉 注册成功！请登录')
    switchToLogin()
    form.username = regForm.username
  } catch {
    // 错误由 request 拦截器统一处理
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景 — 纯 CSS 渐变替代图片（图片路径可后续替换） */
.login-bg {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, #FFB6D3 0%, #E8D5F5 25%, #FFD1E8 50%, #D5E5F5 75%, #FFE0EC 100%);
  background-size: 400% 400%;
  animation: bgShift 20s ease infinite;
  filter: brightness(1.05);
}
@keyframes bgShift {
  0%,100% { background-position: 0% 50%; }
  50%     { background-position: 100% 50%; }
}

/* 装饰圆 */
.login-bg::before,
.login-bg::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
}
.login-bg::before {
  width: 400px; height: 400px;
  background: var(--primary);
  top: -100px; right: -100px;
  animation: float 6s ease-in-out infinite;
}
.login-bg::after {
  width: 300px; height: 300px;
  background: var(--info);
  bottom: -80px; left: -80px;
  animation: float 8s ease-in-out infinite reverse;
}

/* 卡片 */
.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 40px 36px 28px;
  background: var(--glass-bg);
  backdrop-filter: blur(20px) saturate(180%);
  border-radius: var(--radius-xl);
  border: 1px solid var(--glass-border);
  box-shadow: var(--shadow-xl);
}

.login-logo { text-align: center; margin-bottom: 28px; }
.logo-bear { font-size: 56px; display: block; margin-bottom: 10px; animation: float 3s ease-in-out infinite; }
.login-logo h1 { font-size: 22px; color: var(--text-primary); margin: 0 0 4px; }
.login-logo p { font-size: 13px; color: var(--text-secondary); margin: 0; }

.login-form { margin-top: 0; }
.login-btn { width: 100%; height: 44px; font-size: 15px; letter-spacing: 2px; }

.login-footer { text-align: center; margin-top: 20px; }
.footer-tip { font-size: 13px; color: var(--text-secondary); }
.footer-link { color: var(--primary); cursor: pointer; text-decoration: underline; font-weight: 500; }
.footer-link:hover { color: var(--primary-dark); }
</style>
