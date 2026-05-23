export interface ShoppingListItem {
  id: number; snackId: number | null; snackName: string; categoryId: number | null; categoryName: string
  plannedQty: number; actualQty: number | null; price: number | null
  status: 'PENDING' | 'BOUGHT' | 'CANCELLED'; source: 'MANUAL' | 'LOW_STOCK'
  supplierId: number | null; supplierName: string; boughtTime: string | null; remark: string; createTime: string
}
export interface ShoppingListCreateDTO {
  snackName: string; snackId?: number; categoryId?: number; plannedQty: number; price?: number; supplierId?: number; remark?: string
}
export interface ShoppingListStockInDTO { actualQty: number; price?: number; categoryId?: number; remark?: string }
