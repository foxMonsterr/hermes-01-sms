import request from '@/utils/request'
export interface OrderItem {id:number;snackId:number;snackName:string;imageUrl:string;price:number;quantity:number;unit:string;subtotal:number}
export interface Order {id:number;orderNo:string;status:string;totalAmount:number;totalQuantity:number;receiver:string;phone:string;address:string;remark:string;createTime:string;shipTime:string;completeTime:string;cancelTime:string;items:OrderItem[]}
export function createOrder(d:any){return request.post<Order>('/shop/orders',d)}
export function getOrders(params?:any){return request.get<{records:Order[];total:number}>('/shop/orders',{params})}
export function getOrderDetail(id:number){return request.get<Order>('/shop/orders/'+id)}
export function cancelOrder(id:number){return request.patch('/shop/orders/'+id+'/cancel')}
export function confirmReceive(id:number){return request.patch('/shop/orders/'+id+'/confirm')}
