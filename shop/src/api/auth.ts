import request from '@/utils/request'
export function login(username:string,password:string){return request.post<{id:number;token:string;username:string;nickname:string}>('/shop/auth/login',{username,password})}
export function register(u:string,p:string,n:string){return request.post('/shop/auth/register',{username:u,password:p,nickname:n})}
export function getProfile(){return request.get<{id:number;username:string;nickname:string}>('/shop/auth/profile')}
