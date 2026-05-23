import request from '@/utils/request'
import type { PageData } from '@/types/common'
import type { OperationLog, LogQuery } from '@/types/operationLog'

export function getLogs(params: LogQuery) { return request.get<PageData<OperationLog>>('/logs',{params}) }
