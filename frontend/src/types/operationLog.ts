export interface OperationLog {
  id: number; username: string; action: string; targetType: string
  targetId: number | null; detail: string; result: string; ip: string; createTime: string
}
export interface LogQuery { page?: number; size?: number; action?: string; targetType?: string; startDate?: string; endDate?: string }
export const ACTION_OPTIONS = [
  { label:'登录成功',value:'LOGIN_SUCCESS'},{ label:'修改密码',value:'PASSWORD_UPDATE'},{ label:'新增零食',value:'SNACK_CREATE'},
  { label:'编辑零食',value:'SNACK_UPDATE'},{ label:'删除零食',value:'SNACK_DELETE'},{ label:'入库',value:'STOCK_IN'},{ label:'出库',value:'STOCK_OUT'},
  { label:'库存调整',value:'STOCK_ADJUST'},{ label:'库存盘点',value:'INVENTORY_CHECK'},{ label:'丢弃处理',value:'DISPOSAL_CREATE'},
  { label:'采购入库',value:'SHOPPING_STOCK_IN'},{ label:'新增供应商',value:'SUPPLIER_CREATE'},{ label:'修改配置',value:'CONFIG_UPDATE'},
  { label:'批量导入',value:'IMPORT_SNACKS'},{ label:'数据导出',value:'EXPORT_DATA'}
]
export const TARGET_TYPE_OPTIONS = [
  { label:'用户认证',value:'AUTH'},{ label:'零食',value:'SNACK'},{ label:'分类',value:'CATEGORY'},{ label:'库存',value:'STOCK'},
  { label:'采购清单',value:'SHOPPING_LIST'},{ label:'供应商',value:'SUPPLIER'},{ label:'系统配置',value:'CONFIG'},
  { label:'丢弃记录',value:'DISPOSAL'},{ label:'盘点记录',value:'INVENTORY_CHECK'}
]
