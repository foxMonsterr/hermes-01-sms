<template>
  <el-container class="layout">
    <!-- 侧栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span class="logo-icon">🐻</span>
        <span class="logo-text">零食小管家</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="transparent"
        text-color="var(--text-sidebar)"
        active-text-color="#fff"
      >
        <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon><span>首页</span></el-menu-item>
        <el-menu-item index="/categories"><el-icon><Folder /></el-icon><span>分类管理</span></el-menu-item>
        <el-menu-item index="/snacks"><el-icon><Food /></el-icon><span>零食管理</span></el-menu-item>
        <el-menu-item index="/stock-records"><el-icon><Document /></el-icon><span>库存流水</span></el-menu-item>
        <el-menu-item index="/shopping-list"><el-icon><ShoppingCart /></el-icon><span>采购清单</span></el-menu-item>
        <el-menu-item index="/statistics"><el-icon><TrendCharts /></el-icon><span>统计看板</span></el-menu-item>
        <el-menu-item index="/notifications"><el-icon><Bell /></el-icon>
          <span>消息提醒</span>
          <el-badge v-if="unreadCount>0" :value="unreadCount" class="menu-badge" />
        </el-menu-item>
        <el-menu-item index="/suppliers"><el-icon><Avatar /></el-icon><span>供应商</span></el-menu-item>
        <el-menu-item index="/logs"><el-icon><Clock /></el-icon><span>操作日志</span></el-menu-item>
        <el-menu-item index="/shop-orders"><el-icon><Goods /></el-icon><span>店铺订单</span></el-menu-item>
        <el-menu-item index="/settings"><el-icon><Setting /></el-icon><span>系统设置</span></el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="sidebar-decor">🐰</div>
        <div class="sidebar-tip">零食要好好管理哦~</div>
      </div>
    </el-aside>

    <!-- 右侧 -->
    <el-container>
      <!-- 顶栏 -->
      <el-header class="header">
        <span class="header-greeting">{{ greeting }}</span>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="unreadCount===0" class="header-bell">
            <el-icon :size="20" style="cursor:pointer" @click="router.push('/notifications')"><Bell /></el-icon>
          </el-badge>
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.avatarUrl" class="user-avatar">
                <span style="font-size:18px">🐻</span>
              </el-avatar>
              <span class="user-name">{{ userStore.nickname || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/settings')">
                  <el-icon><Setting /></el-icon> 系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 小熊店长 -->
    <BearAssistant />
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import BearAssistant from '@/components/BearAssistant.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const notifStore = useNotificationStore()

const activeMenu = computed(() => route.path)
const unreadCount = computed(() => notifStore.unread.count)

const greeting = computed(() => {
  const h = new Date().getHours()
  const g = h<12 ? ['☀️ 早上好','元气满满的早晨~','新的一天开始啦！']
    : h<18 ? ['🌤️ 下午好','午后的阳光真好~','继续加油哦！']
    : ['🌙 晚上好','辛苦了一天~','该休息啦！']
  return g[Math.floor(Math.random()*g.length)]
})

onMounted(() => { notifStore.fetchUnread().catch(()=>{}) })

function handleLogout() { userStore.logout(); notifStore.clear(); router.push('/login') }
</script>

<style scoped>
.layout { height: 100vh; }

/* ======== 侧栏 ======== */
.sidebar {
  background: var(--bg-sidebar) !important;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 60px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo-icon { font-size: 28px; }
.logo-text { font-size: 17px; font-weight: bold; color: #fff; }
.sidebar .el-menu { flex: 1; border-right: none; }
.menu-badge { margin-left: auto; }

.sidebar-footer {
  padding: 16px 12px;
  text-align: center;
  border-top: 1px solid rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.5);
  font-size: 12px;
}
.sidebar-decor { font-size: 28px; margin-bottom: 4px; }
.sidebar-tip { font-size: 11px; }

/* ======== 顶栏 ======== */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  backdrop-filter: blur(12px) saturate(180%);
  background: rgba(255,255,255,0.88);
  border-bottom: 1px solid var(--glass-border);
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 10;
}
.header-greeting { font-size: 15px; font-weight: 500; color: var(--text-primary); }
.header-right { display: flex; align-items: center; gap: 20px; }
.header-bell { cursor: pointer; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; color: var(--text-primary); }
.user-avatar { border: 2px solid var(--primary-light); transition: all 0.2s ease; }
.user-avatar:hover { border-color: var(--primary); }
.user-name { font-size: 14px; font-weight: 500; }

/* ======== 主内容 ======== */
.main { background: var(--bg-page); min-height: calc(100vh - 56px); padding: 20px; }

/* ======== 页面过渡 ======== */
.page-enter-active,
.page-leave-active { transition: all 0.25s ease; }
.page-enter-from { opacity: 0; transform: translateY(10px); }
.page-leave-to { opacity: 0; transform: translateY(-10px); }
</style>
