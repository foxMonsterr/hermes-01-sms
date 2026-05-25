<template>
  <div class="auth-page"><div class="auth-card"><h2>✨ 注册</h2>
    <el-form><el-form-item><el-input v-model="username" placeholder="用户名" size="large"/></el-form-item><el-form-item><el-input v-model="nickname" placeholder="昵称（选填）" size="large"/></el-form-item><el-form-item><el-input v-model="password" type="password" show-password placeholder="密码（至少6位）" size="large"/></el-form-item><el-form-item><el-button type="primary" size="large" :loading="loading" @click="submit" style="width:100%">注 册</el-button></el-form-item></el-form>
    <p class="switch">已有账号？<router-link to="/login">返回登录</router-link></p>
  </div></div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'
const router=useRouter();const username=ref('');const nickname=ref('');const password=ref('');const loading=ref(false)
async function submit(){if(!username.value||!password.value||password.value.length<6){ElMessage.warning('请填写完整信息');return};loading.value=true;try{await register(username.value,password.value,nickname.value||username.value);ElMessage.success('注册成功，请登录');router.push('/login')}finally{loading.value=false}}
</script>

<style scoped>
.auth-page{display:flex;justify-content:center;padding-top:60px}.auth-card{width:380px;background:#fff;border-radius:16px;padding:40px 36px;box-shadow:0 4px 20px rgba(0,0,0,0.06)}h2{text-align:center;margin-bottom:24px}.switch{text-align:center;margin-top:16px;font-size:13px;color:#999}a{color:#E8659A}
</style>
