<template>
  <div class="auth-page"><div class="auth-card"><h2>🔑 登录</h2>
    <el-form @keyup.enter="submit"><el-form-item><el-input v-model="username" placeholder="用户名" size="large" prefix-icon="User"/></el-form-item><el-form-item><el-input v-model="password" type="password" show-password placeholder="密码" size="large" prefix-icon="Lock"/></el-form-item><el-form-item><el-button type="primary" size="large" :loading="loading" @click="submit" style="width:100%">登 录</el-button></el-form-item></el-form>
    <p class="switch">还没有账号？<router-link to="/register">立即注册</router-link></p>
  </div></div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useShopUserStore } from '@/stores/shopUser'
const router=useRouter();const store=useShopUserStore();const username=ref('');const password=ref('');const loading=ref(false)
async function submit(){loading.value=true;try{await store.login(username.value,password.value);ElMessage.success('登录成功');router.push('/home')}finally{loading.value=false}}
</script>

<style scoped>
.auth-page{display:flex;justify-content:center;padding-top:60px}.auth-card{width:380px;background:#fff;border-radius:16px;padding:40px 36px;box-shadow:0 4px 20px rgba(0,0,0,0.06)}h2{text-align:center;margin-bottom:24px}.switch{text-align:center;margin-top:16px;font-size:13px;color:#999}a{color:#E8659A}
</style>
