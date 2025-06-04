
import api, { type ApiResponse } from "../index.ts";
import type { UserDetailDTO } from "../system/user.ts";

// 登录请求类型 - 根据 OpenAPI 定义
export interface LoginRequest {
    username: string
    password: string
    rememberMe: boolean
}

// 登录响应类型 - 根据 OpenAPI 定义
export interface LoginResponse {
    token: string
    username: string
}

export const loginApi = {
    // 用户名密码登录
    login: (data: LoginRequest): Promise<ApiResponse<UserDetailDTO>> => {
        return api.post('/api/auth/login', data)
    },

    // 退出登录
    logout: (): Promise<ApiResponse<void>> => {
        return api.post('/api/logout')
    },

    // 获取当前用户信息
    getUserInfo: (): Promise<ApiResponse<UserDetailDTO>> => {
        return api.get('/api/user/info')
    }
}