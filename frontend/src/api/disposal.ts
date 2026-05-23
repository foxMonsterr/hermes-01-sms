import request from '@/utils/request'
import type { PageData } from '@/types/common'
import type { DisposalDTO, DisposalRecord, DisposalStats } from '@/types/disposal'

export function getDisposals(page=1,size=10,startDate?:string,endDate?:string) { return request.get<PageData<DisposalRecord>>('/disposals',{params:{page,size,startDate,endDate}}) }
export function disposeSnack(id:number,data:DisposalDTO) { return request.post<{snackId:number;remainingQuantity:number;disposedQuantity:number;recordId:number}>(`/snacks/${id}/dispose`,data) }
export function getDisposalStats(params?:{startDate?:string;endDate?:string}) { return request.get<DisposalStats>('/disposals/stats',{params}) }
