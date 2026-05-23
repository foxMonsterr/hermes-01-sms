export type DisposalReason = 'EXPIRED' | 'DAMAGED' | 'MANUAL'
export interface DisposalDTO { quantity: number; reason: DisposalReason; remark?: string }
export interface DisposalRecord {
  id: number; snackId: number; snackName: string; quantity: number; unit: string
  unitPrice: number | null; totalLoss: number | null; reason: string; remark: string; disposeDate: string; createTime: string
}
export interface DisposalStats {
  totalDisposedQty: number; expiredDisposedQty: number; damagedDisposedQty: number; manualDisposedQty: number; estimatedLoss: number | null
}
