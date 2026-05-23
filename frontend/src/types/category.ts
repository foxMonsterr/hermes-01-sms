/** 分类信息 */
export interface CategoryVO {
  id: number
  name: string
  icon: string
  sortOrder: number
  snackCount: number
  createTime: string
}

/** 分类创建/更新请求 */
export interface CategoryFormDTO {
  name: string
  icon?: string
  sortOrder?: number
}
