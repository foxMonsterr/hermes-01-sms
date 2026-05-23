import request from '@/utils/request'
import type { ShoppingListItem, ShoppingListCreateDTO, ShoppingListStockInDTO } from '@/types/shoppingList'
import type { PageData } from '@/types/common'

export function getShoppingList(page = 1, size = 10, status?: string, keyword?: string) {
  return request.get<PageData<ShoppingListItem>>('/shopping-list', { params: { page, size, status, keyword } })
}

export function createShoppingItem(data: ShoppingListCreateDTO) {
  return request.post<ShoppingListItem>('/shopping-list', data)
}

export function updateShoppingItem(id: number, data: ShoppingListCreateDTO) {
  return request.put<ShoppingListItem>(`/shopping-list/${id}`, data)
}

export function updateShoppingItemStatus(id: number, status: string) {
  return request.patch(`/shopping-list/${id}/status`, { status })
}

export function deleteShoppingItem(id: number) {
  return request.delete(`/shopping-list/${id}`)
}

export function fromLowStock() {
  return request.post<{ createdCount: number; skippedCount: number }>('/shopping-list/from-low-stock')
}

export function stockIn(id: number, data: ShoppingListStockInDTO) {
  return request.post<{ shoppingListId: number; snackId: number; status: string; quantity: number }>(`/shopping-list/${id}/stock-in`, data)
}
