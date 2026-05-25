import request from '@/utils/request'
export interface CartItem {id:number;snackId:number;snackName:string;imageUrl:string;price:number;quantity:number;stock:number;onShelf:boolean;subtotal:number}
export function getCart(){return request.get<CartItem[]>('/shop/cart')}
export function addToCart(snackId:number,quantity:number){return request.post<CartItem>('/shop/cart',{snackId,quantity})}
export function updateCartQty(id:number,quantity:number){return request.put('/shop/cart/'+id,{quantity})}
export function removeFromCart(id:number){return request.delete('/shop/cart/'+id)}
export function clearCart(){return request.delete('/shop/cart')}
