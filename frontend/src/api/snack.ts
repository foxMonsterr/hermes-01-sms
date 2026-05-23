import request from '@/utils/request'
import type { SnackVO, SnackCreateDTO, SnackUpdateDTO, QuantityUpdateDTO, SnackQuery } from '@/types/snack'
import type { PageData } from '@/types/common'
import type { ImportResult } from '@/types/import'

export function getSnacks(params: SnackQuery) { return request.get<PageData<SnackVO>>('/snacks',{params}) }
export function getSnackDetail(id:number) { return request.get<SnackVO>(`/snacks/${id}`) }
export function createSnack(data:SnackCreateDTO) { return request.post<SnackVO>('/snacks',data) }
export function updateSnack(id:number,data:SnackUpdateDTO) { return request.put<SnackVO>(`/snacks/${id}`,data) }
export function updateQuantity(id:number,data:QuantityUpdateDTO) { return request.patch(`/snacks/${id}/quantity`,data) }
export function adjustQuantity(id:number,data:{quantity:number;remark?:string}) { return request.patch(`/snacks/${id}/quantity/adjust`,data) }
export function deleteSnack(id:number) { return request.delete(`/snacks/${id}`) }
export function batchDeleteSnacks(ids:number[]) { return request.delete('/snacks/batch',{data:{ids}}) }
export function uploadSnackImage(formData:FormData) { return request.upload<{url:string}>('/files/upload',formData) }
export function getImportTemplate() { return request.download('/snacks/import-template') }
export function importSnacks(formData:FormData) { return request.upload<ImportResult>('/snacks/import',formData) }
