import request from '@/utils/request'
import type { CategoryVO, CategoryFormDTO } from '@/types/category'

export function getCategories() {
  return request.get<CategoryVO[]>('/categories')
}

export function createCategory(data: CategoryFormDTO) {
  return request.post<CategoryVO>('/categories', data)
}

export function updateCategory(id: number, data: CategoryFormDTO) {
  return request.put<CategoryVO>(`/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return request.delete(`/categories/${id}`)
}
