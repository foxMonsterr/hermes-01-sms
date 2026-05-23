import request from '@/utils/request'
import type { StatisticsOverview, CategoryDistribution } from '@/types/statistics'

export function getStatisticsOverview() {
  return request.get<StatisticsOverview>('/statistics/overview')
}

export function getCategoryDistribution() {
  return request.get<CategoryDistribution[]>('/statistics/category-distribution')
}

export function getStockTrend(period = '7d') {
  return request.get<any>('/statistics/stock-trend', { params: { period } })
}

export function getValueStats() {
  return request.get<any>('/statistics/value')
}
