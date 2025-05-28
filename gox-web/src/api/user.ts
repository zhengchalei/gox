import api from './index'
import type {
  UserCreateDTO,
  UserUpdateDTO,
  UserSpecification,
  RUserDetailDTO,
  RPageUserListDTO,
  RVoid,
  UserRoleUpdateDTO
} from '../types/api'

export const userApi = {
  // 创建用户
  create: (data: UserCreateDTO): Promise<RVoid> => {
    return api.post('/api/v1/sys/user', data)
  },

  // 更新用户
  update: (data: UserUpdateDTO): Promise<RVoid> => {
    return api.put('/api/v1/sys/user', data)
  },

  // 根据ID查询用户
  findById: (id: number): Promise<RUserDetailDTO> => {
    return api.get(`/api/v1/sys/user/${id}`)
  },

  // 根据用户名查询用户
  findByUsername: (username: string): Promise<RUserDetailDTO> => {
    return api.get(`/api/v1/sys/user/username/${username}`)
  },

  // 修改用户角色
  assignUserRole: (data: UserRoleUpdateDTO): Promise<RVoid> => {
    return api.post('/api/v1/sys/user/assign-role', data)
  },

  // 分页查询用户
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: UserSpecification): Promise<RPageUserListDTO> => {
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
    return api.get(`/api/v1/sys/user/page?${params}`)
  },

  // 删除用户
  deleteById: (id: number): Promise<RVoid> => {
    return api.delete(`/api/v1/sys/user/${id}`)
  }
} 