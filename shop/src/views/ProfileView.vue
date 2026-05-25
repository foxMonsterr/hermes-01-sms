<template>
  <div class="page"><h2>👤 个人中心</h2><el-card><p>用户名：{{store.username}}</p><p>昵称：{{store.nickname}}</p><el-button type="danger" style="margin-top:16px" @click="logout">退出登录</el-button></el-card></div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useShopUserStore } from '@/stores/shopUser'
const store=useShopUserStore();const router=useRouter()
function logout(){store.logout();router.push('/home')}
</script>

<style scoped>.page{padding:0}</style>
