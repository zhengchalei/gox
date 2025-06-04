import api, { type ApiResponse, type PageResponse } from '../index'
import type { RoleInfo } from "./role.ts";


// 用户相关类型
export interface UserDetailDTO {
    id: number
    username: string
    nickname: string
    avatar: string
    email: string
    phone: string
    enabled: boolean
    createdTime: string
    updatedTime: string
    roleIds: number[]
    roles: RoleInfo[]
}

export interface UserListDTO {
    id: number
    username: string
    nickname: string
    email: string
    phone: string
    enabled: boolean
    createdTime: string
    updatedTime: string
}

export interface UserCreateDTO {
    username: string
}

export interface UserUpdateDTO {
    id: number
    username: string
    enabled: boolean
    email?: string
    phone?: string
}

export interface UserRoleUpdateDTO {
    id: number
    roleIds: number[]
}

export interface UserSpecification {
    id?: number
    username?: string
    nickname?: string
    avatar?: string
    email?: string
    phone?: string
    enabled?: boolean
    createdTime?: string
    updatedTime?: string
}

export const userApi = {
    // 创建用户
    create: (data: UserCreateDTO): Promise<ApiResponse<void>> => {
        return api.post('/api/sys/user', data)
    },

    // 更新用户
    update: (data: UserUpdateDTO): Promise<ApiResponse<void>> => {
        return api.put('/api/sys/user', data)
    },

    // 根据ID查询用户
    findById: (id: number): Promise<ApiResponse<UserDetailDTO>> => {
        return api.get(`/api/sys/user/${id}`)
    },

    // 根据用户名查询用户
    findByUsername: (username: string): Promise<ApiResponse<UserDetailDTO>> => {
        return api.get(`/api/sys/user/username/${username}`)
    },

    // 修改用户角色
    assignUserRole: (data: UserRoleUpdateDTO): Promise<ApiResponse<void>> => {
        return api.post('/api/sys/user/assign-role', data)
    },

    // 分页查询用户
    findPage: (currentPage: number = 1, pageSize: number = 10, specification: UserSpecification): Promise<ApiResponse<PageResponse<UserListDTO>>> => {
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
        return api.get(`/api/sys/user/page?${params}`)
    },

    // 删除用户
    deleteById: (id: number): Promise<ApiResponse<void>> => {
        return api.delete(`/api/sys/user/${id}`)
    }
} 