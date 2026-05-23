import request from '@/utils/request'
import type { StockRecord, StockRecordStats, StockRecordQuery } from '@/types/stockRecord'
import type { PageData } from '@/types/common'

export function getStockRecords(params: StockRecordQuery) {
  return request.get<PageData<StockRecord>>('/stock-records', { params })
}

export function getStockRecordsBySnack(snackId: number, params: StockRecordQuery) {
  return request.get<PageData<StockRecord>>(`/stock-records/snack/${snackId}`, { params })
}

export function getStockRecordStats() {
  return request.get<StockRecordStats>('/stock-records/stats')
}
