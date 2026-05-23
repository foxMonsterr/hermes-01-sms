export interface Notification {
  id: number
  type: string
  title: string
  content: string
  relatedId: number
  snackName: string
  notifyDate: string
  isRead: number
  createTime: string
}

export interface UnreadCount {
  count: number
  expirySoonCount: number
  expiredCount: number
  lowStockCount: number
  stockOutCount: number
}
