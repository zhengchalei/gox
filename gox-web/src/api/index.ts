import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import type { 
  LoginRequest,
  RLoginResponse, 
  RUserDetailDTO, 
  RBoolean,
  RVoid,
  ApiResponse 
} from '../types/api'

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

// 登录API - 根据OpenAPI规范
export const loginApi = {
  // 用户名密码登录
  login: (data: LoginRequest): Promise<RLoginResponse> => {
    return api.post('/api/auth/login', data)
  },
  
  // 退出登录
  logout: (): Promise<RBoolean> => {
    return api.post('/api/logout')
  },
  
  // 获取当前用户信息
  getUserInfo: (): Promise<RUserDetailDTO> => {
    return api.get('/api/user/info')
  }
}

// 导出其他API模块
export { userApi } from './user'
export { roleApi } from './role'
export { permissionApi } from './permission'
export { fileApi } from './file'

export default api 