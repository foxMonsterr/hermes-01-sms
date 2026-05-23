/** 统一响应 */
export interface Result<T = unknown> {
  code: number
  msg: string
  data: T
}

/** 分页数据 */
export interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
}

/** 分页参数 */
export interface PageParams {
  page?: number
  size?: number
}
