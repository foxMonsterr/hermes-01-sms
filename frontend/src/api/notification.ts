import request from '@/utils/request'
import type { Notification, UnreadCount } from '@/types/notification'
import type { PageData } from '@/types/common'

export function getNotifications(page = 1, size = 10, type?: string, isRead?: number) {
  return request.get<PageData<Notification>>('/notifications', { params: { page, size, type, isRead } })
}

export function getUnreadCount() {
  return request.get<UnreadCount>('/notifications/unread-count')
}

export function generateNotifications() {
  return request.post<{ createdCount: number }>('/notifications/generate')
}

export function markRead(id: number) {
  return request.patch(`/notifications/${id}/read`)
}

export function markAllRead() {
  return request.patch('/notifications/read-all')
}

export function deleteNotification(id: number) {
  return request.delete(`/notifications/${id}`)
}
