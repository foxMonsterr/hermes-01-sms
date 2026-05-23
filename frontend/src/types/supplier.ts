export interface Supplier {
  id: number; name: string; contact: string; phone: string
  address: string; notes: string; createTime: string
}
export interface SupplierCreateDTO { name: string; contact?: string; phone?: string; address?: string; notes?: string }
export interface SupplierQuery { page?: number; size?: number; keyword?: string }
export interface SupplierStats { supplierId: number; supplierName: string; purchaseCount: number; totalQuantity: number; estimatedAmount: number | null }
