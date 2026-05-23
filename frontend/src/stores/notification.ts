import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount, generateNotifications } from '@/api/notification'
import type { UnreadCount } from '@/types/notification'

export const useNotificationStore = defineStore('notification', () => {
  const unread = ref<UnreadCount>({ count: 0, expirySoonCount: 0, expiredCount: 0, lowStockCount: 0, stockOutCount: 0 })

  async function fetchUnread() {
    try {
      const res = await getUnreadCount()
      unread.value = res.data
    } catch { /* P1 阶段接口可能不存在，忽略 */ }
  }

  async function generate() {
    try {
      await generateNotifications()
      await fetchUnread()
    } catch { /* ignore */ }
  }

  function clear() {
    unread.value = { count: 0, expirySoonCount: 0, expiredCount: 0, lowStockCount: 0, stockOutCount: 0 }
  }

  return { unread, fetchUnread, generate, clear }
})
