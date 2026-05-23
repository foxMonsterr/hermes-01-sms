export interface SnackVO {
  id: number
  name: string
  categoryId: number
  categoryName: string
  quantity: number
  unit: string
  price: number | null
  totalPrice: number | null
  imageUrl: string
  purchaseDate: string
  expiryDate: string
  expiryStatus: string
  daysUntilExpiry: number | null
  notes: string
  createTime: string
}

export interface SnackCreateDTO {
  name: string
  categoryId: number
  quantity: number
  unit?: string
  price?: number
  purchaseDate?: string
  expiryDate?: string
  notes?: string
}

export interface SnackUpdateDTO {
  name: string
  categoryId: number
  unit?: string
  price?: number
  purchaseDate?: string
  expiryDate?: string
  notes?: string
}

export interface SnackQuery {
  page: number
  size: number
  keyword?: string
  categoryId?: number
  expiryStatus?: string
}

export interface QuantityUpdateDTO {
  delta: number
}

export interface QuantityAdjustDTO {
  quantity: number
  remark?: string
}
