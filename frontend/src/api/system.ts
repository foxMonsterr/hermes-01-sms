import request from '@/utils/request'
import { downloadBlob } from '@/utils/download'

export async function backupDatabase() {
  const blob = await request.download('/system/backup')
  const now = new Date()
  const ts = now.getFullYear()+String(now.getMonth()+1).padStart(2,'0')+String(now.getDate()).padStart(2,'0')+'_'+String(now.getHours()).padStart(2,'0')+String(now.getMinutes()).padStart(2,'0')+String(now.getSeconds()).padStart(2,'0')
  downloadBlob(blob as any, `snack_backup_${ts}.db`)
}

export function getSystemHealth() { return request.get<{status:string;database:string;time:string}>('/system/health') }
export function getSystemVersion() { return request.get<{name:string;version:string;backend:string;frontend:string}>('/system/version') }
