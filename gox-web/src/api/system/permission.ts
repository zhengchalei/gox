import api, { type ApiResponse, type PageResponse } from '../index'
import type { RoleInfo } from './role'


// 权限相关类型
export interface PermissionDetailDTO {
  id: number
  name: string
  code: string
  description?: string
  createdTime: string
  updatedTime: string
  roleIds: number[]
  roles: RoleInfo[]
}

export interface PermissionListDTO {
  id: number
  name: string
  code: string
  description?: string
  createdTime: string
  updatedTime: string
  roleIds: number[]
}

export interface PermissionCreateDTO {
  name: string
  code: string
  description?: string
  roleIds: number[]
}

export interface PermissionUpdateDTO {
  id: number
  name: string
  code: string
  description?: string
  roleIds: number[]
}

export interface PermissionSpecification {
  id?: number
  name?: string
  code?: string
  description?: string
  createdTime?: string
  updatedTime?: string
  roleIds?: number[]
}

export interface PermissionInfo {
  id: number
  name: string
  code: string
  description?: string
  createdTime: string
  updatedTime: string
}

export const permissionApi = {
  // 创建权限
  create: (data: PermissionCreateDTO): Promise<ApiResponse<void>> => {
    return api.post('/api/sys/permission', data)
  },

  // 更新权限
  update: (data: PermissionUpdateDTO): Promise<ApiResponse<void>> => {
    return api.put('/api/sys/permission', data)
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<PermissionDetailDTO>> => {
    return api.get(`/api/sys/permission/${id}`)
  },

  // 分页查询权限
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: PermissionSpecification): Promise<ApiResponse<PageResponse<PermissionListDTO>>> => {
    const params = new URLSearchParams({
      currentPage: currentPage.toString(),
      pageSize: pageSize.toString(),
      ...Object.fromEntries(
        Object.entries(specification)
          .filter(([_, value]) => value !== undefined && value !== '')
          .map(([key, value]) => [
            key,
            Array.isArray(value) ? value.join(',') : value?.toString() || ''
          ])
      )
    })
    return api.get(`/api/sys/permission/page?${params}`)
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return api.delete(`/api/sys/permission/${id}`)
  }
} 