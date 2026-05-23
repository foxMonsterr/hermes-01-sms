import request from '@/utils/request'
import type { SystemConfig } from '@/types/systemConfig'

export function getSystemConfig() { return request.get<SystemConfig>('/settings/config') }
export function updateSystemConfig(data:SystemConfig) { return request.put<SystemConfig>('/settings/config',data) }
export function resetSystemConfig() { return request.post<SystemConfig>('/settings/config/reset') }
