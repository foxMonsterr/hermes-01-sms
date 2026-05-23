export interface ImportErrorItem { row: number; reason: string }
export interface ImportResult { successCount: number; failCount: number; errors: ImportErrorItem[] }
