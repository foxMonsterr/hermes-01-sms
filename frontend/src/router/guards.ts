import type { Router } from 'vue-router'
import { getToken, removeToken } from '@/utils/token'
import { useUserStore } from '@/stores/user'

export function setupGuards(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    const token = getToken()
    const userStore = useUserStore()

    // 去登录页 — 已登录则直接跳首页
    if (to.path === '/login') {
      if (token) {
        next('/dashboard')
        return
      }
      next()
      return
    }

    // 需要认证但无 token → 跳登录
    if (to.meta.requiresAuth && !token) {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      return
    }

    // 有 token 但 store 中无用户信息 → 从后端恢复
    if (token && !userStore.username) {
      try {
        await userStore.fetchProfile()
      } catch {
        removeToken()
        next('/login')
        return
      }
    }

    next()
  })
}
