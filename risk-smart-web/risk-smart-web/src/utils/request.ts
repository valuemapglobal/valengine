import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response

    // Handle blob response
    if (response.config.responseType === 'blob') {
      return response
    }

    // Customize based on your API response structure
    if (data.code === 200 || data.code === 0) {
      return data
    }

    // Handle specific error codes
    if (data.code === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      return Promise.reject(new Error('Unauthorized'))
    }

    ElMessage.error(data.msg || 'Request failed')
    return Promise.reject(new Error(data.msg || 'Error'))
  },
  (error) => {
    const { response } = error

    if (response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    } else if (response?.status === 403) {
      ElMessage.error('No permission')
    } else if (response?.status === 404) {
      ElMessage.error('Resource not found')
    } else if (response?.status >= 500) {
      ElMessage.error('Server error')
    } else if (error.message.includes('timeout')) {
      ElMessage.error('Request timeout')
    } else if (error.message.includes('Network Error')) {
      ElMessage.error('Network error')
    }

    return Promise.reject(error)
  }
)

// API Response type
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// Request methods
export function get<T = any>(url: string, params?: Record<string, any>, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.get(url, { params, ...config })
}

export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.post(url, data, config)
}

export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.put(url, data, config)
}

export function del<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.delete(url, config)
}

export default service
