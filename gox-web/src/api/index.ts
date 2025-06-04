import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '../router'

// OpenAPI 标准响应类型
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  code: number
  timestamp: number
}

// 分页对象类型
export interface PageableObject {
  offset: number
  sort: SortObject
  paged: boolean
  pageSize: number
  pageNumber: number
  unpaged: boolean
}

export interface SortObject {
  empty: boolean
  sorted: boolean
  unsorted: boolean
}

export interface PageResponse<T> {
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  size: number
  content: T[]
  number: number
  sort: SortObject
  numberOfElements: number
  pageable: PageableObject
  empty: boolean
}


// 创建axios实例
const api = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
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

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    const data = response.data as ApiResponse
    
    // 检查业务状态码
    if (data.success === false) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    
    return response.data
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器开小差了')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    
    return Promise.reject(error)
  }
)

export default api 