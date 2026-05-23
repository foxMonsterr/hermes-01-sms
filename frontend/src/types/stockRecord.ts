export interface StockRecord {
  id: number
  snackId: number
  snackName: string
  changeType: string
  changeQty: number
  beforeQty: number
  afterQty: number
  remark: string
  createTime: string
}

export interface StockRecordStats {
  totalIn: number
  totalOut: number
  todayIn: number
  todayOut: number
  recentRecords: StockRecord[]
}

export interface StockRecordQuery {
  page?: number
  size?: number
  snackId?: number
  changeType?: string
  startDate?: string
  endDate?: string
}
