import api from './index'
import type {
  RoleCreateDTO,
  RoleUpdateDTO,
  RoleSpecification,
  RRoleDetailDTO,
  RPageRoleListDTO,
  RVoid
} from '../types/api'

export const roleApi = {
  // 创建角色
  create: (data: RoleCreateDTO): Promise<RVoid> => {
    return api.post('/api/v1/sys/role', data)
  },

  // 更新角色
  update: (data: RoleUpdateDTO): Promise<RVoid> => {
    return api.put('/api/v1/sys/role', data)
  },

  // 根据ID查询角色
  findById: (id: number): Promise<RRoleDetailDTO> => {
    return api.get(`/api/v1/sys/role/${id}`)
  },

  // 分页查询角色
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: RoleSpecification): Promise<RPageRoleListDTO> => {
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
    return api.get(`/api/v1/sys/role/page?${params}`)
  },

  // 删除角色
  deleteById: (id: number): Promise<RVoid> => {
    return api.delete(`/api/v1/sys/role/${id}`)
  }
} 