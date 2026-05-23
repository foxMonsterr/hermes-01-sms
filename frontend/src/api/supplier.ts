import request from '@/utils/request'
import type { PageData } from '@/types/common'
import type { Supplier, SupplierCreateDTO, SupplierStats, SupplierQuery } from '@/types/supplier'

export function getSuppliers(params: SupplierQuery) { return request.get<PageData<Supplier>>('/suppliers',{params}) }
export function getSupplierOptions() { return request.get<{id:number;name:string}[]>('/suppliers/options') }
export function getSupplierDetail(id:number) { return request.get<Supplier>(`/suppliers/${id}`) }
export function getSupplierStats(id:number, params?:{startDate?:string;endDate?:string}) { return request.get<SupplierStats>(`/suppliers/${id}/stats`,{params}) }
export function createSupplier(data:SupplierCreateDTO) { return request.post<Supplier>('/suppliers',data) }
export function updateSupplier(id:number,data:SupplierCreateDTO) { return request.put<Supplier>(`/suppliers/${id}`,data) }
export function deleteSupplier(id:number) { return request.delete(`/suppliers/${id}`) }
