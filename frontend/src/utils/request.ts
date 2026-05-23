import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/token'
import type { Result } from '@/types/common'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

service.interceptors.request.use(config => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let isRedirecting = false
service.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      if (!isRedirecting) {
        isRedirecting = true
        removeToken()
        ElMessage.error('登录已过期，请重新登录')
        import('@/router').then(({ default: router }) => {
          router.push('/login').finally(() => { setTimeout(() => { isRedirecting = false }, 500) })
        })
      }
    } else {
      ElMessage.error(error.response?.data?.msg || '请求失败')
    }
    return Promise.reject(error)
  }
)

const request = {
  get<T>(url: string, config?: any)    { return service.get<any, Result<T>>(url, config) },
  post<T>(url: string, data?: any)     { return service.post<any, Result<T>>(url, data) },
  put<T>(url: string, data?: any)      { return service.put<any, Result<T>>(url, data) },
  patch<T>(url: string, data?: any)    { return service.patch<any, Result<T>>(url, data) },
  delete<T>(url: string, config?: any) { return service.delete<any, Result<T>>(url, config) },
  upload<T>(url: string, formData: FormData) {
    return service.post<any, Result<T>>(url, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  download(url: string, params?: any) {
    return service.get(url, { params, responseType: 'blob' })
  }
}
export default request
