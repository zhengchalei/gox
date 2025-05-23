import api from './index'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '../types/api'

// API测试工具
export const testApi = {
  // 测试后端连接
  testConnection: async (): Promise<boolean> => {
    try {
      const response = await api.get('/')
      ElMessage.success('后端连接正常')
      return true
    } catch (error) {
      console.error('连接测试失败:', error)
      ElMessage.error('后端连接失败，请检查服务是否启动')
      return false
    }
  },

  // 测试登录接口
  testLogin: async (): Promise<void> => {
    try {
      const testData = {
        username: 'admin',
        password: 'password',
        rememberMe: false
      }
      
      const response = await api.post('/api/auth/login', testData) as ApiResponse
      console.log('登录测试响应:', response)
      
      if (response.success) {
        ElMessage.success('登录接口测试成功')
      } else {
        ElMessage.warning(`登录测试失败: ${response.message}`)
      }
    } catch (error: any) {
      console.error('登录接口测试失败:', error)
      if (error.response?.status === 401) {
        ElMessage.warning('测试账号认证失败（这是正常的）')
      } else {
        ElMessage.error('登录接口测试失败')
      }
    }
  },

  // 测试用户信息接口
  testUserInfo: async (): Promise<void> => {
    try {
      const response = await api.get('/api/user/info') as ApiResponse
      console.log('用户信息测试响应:', response)
      
      if (response.success) {
        ElMessage.success('用户信息接口测试成功')
      } else {
        ElMessage.warning(`用户信息测试失败: ${response.message}`)
      }
    } catch (error: any) {
      console.error('用户信息接口测试失败:', error)
      if (error.response?.status === 401) {
        ElMessage.warning('需要先登录才能获取用户信息')
      } else {
        ElMessage.error('用户信息接口测试失败')
      }
    }
  }
} 