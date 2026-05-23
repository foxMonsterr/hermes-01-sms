<template>
  <el-container class="layout">
    <el-aside width="220px">
      <div class="logo">🍿 零食管理</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>首页</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Folder /></el-icon><span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/snacks">
          <el-icon><Food /></el-icon><span>零食管理</span>
        </el-menu-item>
        <el-menu-item index="/stock-records">
          <el-icon><Document /></el-icon><span>库存流水</span>
        </el-menu-item>
        <el-menu-item index="/shopping-list">
          <el-icon><ShoppingCart /></el-icon><span>采购清单</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><TrendCharts /></el-icon><span>统计看板</span>
        </el-menu-item>
        <el-menu-item index="/notifications">
          <el-icon><Bell /></el-icon>
          <span>消息提醒</span>
          <el-badge v-if="unreadCount > 0" :value="unreadCount" class="menu-badge" />
        </el-menu-item>
        <el-menu-item index="/suppliers">
          <el-icon><Avatar /></el-icon><span>供应商</span>
        </el-menu-item>
        <el-menu-item index="/logs">
          <el-icon><Clock /></el-icon><span>操作日志</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon><span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="header-title">零食管理系统</span>
        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar :size="28" :src="userStore.avatarUrl" style="margin-right:6px" />
            {{ userStore.nickname || userStore.username }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/settings')">个人设置</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const notifStore = useNotificationStore()

const activeMenu = computed(() => route.path)
const unreadCount = computed(() => notifStore.unread.count)

onMounted(() => {
  notifStore.fetchUnread()  // P1: 接口不存在时 fail silently
})

function handleLogout() {
  userStore.logout()
  notifStore.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.el-aside { background-color: #304156; overflow-x: hidden; }
.logo { height: 60px; line-height: 60px; text-align: center; font-size: 18px; font-weight: bold; color: #fff; border-bottom: 1px solid #4a5064; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e4e7ed; padding: 0 20px; }
.header-title { font-size: 16px; color: #303133; }
.user-info { cursor: pointer; display: flex; align-items: center; gap: 4px; color: #606266; }
.el-main { background: #f5f7fa; padding: 20px; }
.menu-badge { margin-left: 8px; }
</style>
