<template>
  <div class="shop-layout">
    <header class="shop-header">
      <div class="header-left"><span class="logo" @click="$router.push('/home')">🛒 零食小卖铺</span></div>
      <div class="header-center"><el-input v-model="searchKw" placeholder="搜索零食..." clearable size="large" @keyup.enter="goSearch" style="width:400px"><template #prefix><el-icon><Search/></el-icon></template></el-input></div>
      <div class="header-right">
        <el-badge :value="cartCount" :hidden="!cartCount" :max="99">
          <el-button circle size="large" @click="$router.push('/cart')" title="购物车">
            <el-icon :size="22"><ShoppingCart/></el-icon>
          </el-button>
        </el-badge>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown><span class="user-name">👤 {{userStore.nickname||userStore.username}}</span>
            <template #dropdown><el-dropdown-menu><el-dropdown-item @click="$router.push('/orders')">📋 我的订单</el-dropdown-item><el-dropdown-item @click="$router.push('/profile')">⚙️ 个人中心</el-dropdown-item><el-dropdown-item divided @click="handleLogout">退出</el-dropdown-item></el-dropdown-menu></template>
          </el-dropdown>
        </template>
        <template v-else><el-button @click="$router.push('/login')">登录</el-button><el-button type="primary" @click="$router.push('/register')">注册</el-button></template>
      </div>
    </header>
    <main class="shop-main"><router-view/></main>
    <footer class="shop-footer">© 2026 零食小卖铺 · 好好吃零食，好好生活~</footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useShopUserStore } from '@/stores/shopUser'
import { getCart } from '@/api/cart'

const router = useRouter()
const userStore = useShopUserStore()
const searchKw = ref('')
const cartCount = ref(0)
function goSearch(){ if(searchKw.value)router.push({path:'/products',query:{keyword:searchKw.value}}) }
async function handleLogout(){ userStore.logout(); router.push('/home') }
onMounted(async()=>{ if(userStore.isLoggedIn)try{const r=await getCart();cartCount.value=r.data.length}catch{}})
</script>

<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:'PingFang SC','Microsoft YaHei',sans-serif;background:#FFF5F5;color:#333}
.shop-layout{min-height:100vh;display:flex;flex-direction:column}
.shop-header{display:flex;align-items:center;justify-content:space-between;padding:0 24px;height:60px;background:#fff;box-shadow:0 2px 8px rgba(0,0,0,0.06);position:sticky;top:0;z-index:100}
.header-left .logo{font-size:20px;font-weight:bold;color:#E8659A;cursor:pointer}
.header-right{display:flex;align-items:center;gap:12px}
.user-name{cursor:pointer;color:#E8659A}
.shop-main{flex:1;max-width:1200px;margin:0 auto;padding:20px;width:100%}
.shop-footer{text-align:center;padding:16px;font-size:13px;color:#999;border-top:1px solid #f0f0f0}
</style>
