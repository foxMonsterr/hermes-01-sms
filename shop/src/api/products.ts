import request from '@/utils/request'
export interface ShopProduct {id:number;name:string;categoryId:number;categoryName:string;quantity:number;unit:string;price:number;imageUrl:string;description:string;expiryStatus:string;inStock:boolean}
export function getProducts(params?:any){return request.get<{records:ShopProduct[];total:number}>('/shop/products',{params})}
export function getProductDetail(id:number){return request.get<ShopProduct>('/shop/products/'+id)}
export function getCategories(){return request.get<{id:number;name:string;icon:string;sortOrder:number}[]>('/shop/categories')}
