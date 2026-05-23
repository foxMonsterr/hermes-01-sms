export interface InventoryCheckDTO { actualQty: number; remark?: string }
export interface InventoryCheckRecord {
  id: number; snackId: number; snackName: string; systemQty: number; actualQty: number
  difference: number; remark: string; checkDate: string; createTime: string
}
export interface InventoryCheckStats { totalCheckCount: number; differenceCount: number; totalPositiveDiff: number; totalNegativeDiff: number }
