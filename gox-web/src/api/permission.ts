import api from './index'
import type {
  PermissionCreateDTO,
  PermissionUpdateDTO,
  PermissionSpecification,
  RPermissionDetailDTO,
  RPagePermissionListDTO,
  RVoid
} from '../types/api'

export const permissionApi = {
  // 创建权限
  create: (data: PermissionCreateDTO): Promise<RVoid> => {
    return api.post('/api/sys/permission', data)
  },

  // 更新权限
  update: (data: PermissionUpdateDTO): Promise<RVoid> => {
    return api.put('/api/sys/permission', data)
  },

  // 根据ID查询权限
  findById: (id: number): Promise<RPermissionDetailDTO> => {
    return api.get(`/api/sys/permission/${id}`)
  },

  // 分页查询权限
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: PermissionSpecification): Promise<RPagePermissionListDTO> => {
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
  deleteById: (id: number): Promise<RVoid> => {
    return api.delete(`/api/sys/permission/${id}`)
  }
} 