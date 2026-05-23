import request from '@/utils/request'
import type { PageData } from '@/types/common'
import type { InventoryCheckDTO, InventoryCheckRecord, InventoryCheckStats } from '@/types/inventoryCheck'

export function getInventoryChecks(page=1,size=10,startDate?:string,endDate?:string) { return request.get<PageData<InventoryCheckRecord>>('/inventory-checks',{params:{page,size,startDate,endDate}}) }
export function checkSnack(id:number,data:InventoryCheckDTO) { return request.post<{snackId:number;systemQty:number;actualQty:number;difference:number;recordId:number}>(`/snacks/${id}/inventory-check`,data) }
export function getInventoryCheckStats(params?:{startDate?:string;endDate?:string}) { return request.get<InventoryCheckStats>('/inventory-checks/stats',{params}) }
