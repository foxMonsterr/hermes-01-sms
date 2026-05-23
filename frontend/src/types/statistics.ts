export interface StatisticsOverview {
  totalSnackCount: number
  totalQuantity: number
  expiredCount: number
  soonExpiredCount: number
  lowStockCount: number
  categoryCount: number
  totalValue: number | null
  todayInQty: number
  todayOutQty: number
}

export interface CategoryDistribution {
  categoryId: number
  categoryName: string
  snackCount: number
  totalQuantity: number
}
