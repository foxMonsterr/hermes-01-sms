import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './token'

const service = axios.create({ baseURL: '/api', timeout: 10000 })
service.interceptors.request.use(c=>{const t=getToken();if(t)c.headers.Authorization=`Bearer ${t}`;return c})

let redirecting=false
service.interceptors.response.use(
  r=>r.data,
  e=>{
    if(e.response?.status===401&&!redirecting){redirecting=true;removeToken();ElMessage.error('请先登录');setTimeout(()=>{redirecting=false},500)}
    else ElMessage.error(e.response?.data?.msg||'请求失败')
    return Promise.reject(e)
  }
)

export default {
  get<T>(url:string,p?:any){return service.get<any,{code:number;msg:string;data:T}>(url,p)},
  post<T>(url:string,d?:any){return service.post<any,{code:number;msg:string;data:T}>(url,d)},
  put<T>(url:string,d?:any){return service.put<any,{code:number;msg:string;data:T}>(url,d)},
  patch<T>(url:string,d?:any){return service.patch<any,{code:number;msg:string;data:T}>(url,d)},
  delete<T>(url:string){return service.delete<any,{code:number;msg:string;data:T}>(url)}
}
