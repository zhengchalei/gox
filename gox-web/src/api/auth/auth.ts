import type { UserDetailDTO } from "../system/user.ts";
import request from "../../utils/request.ts";
import type { ApiResponse } from "../index.ts";

// 登录请求类型 - 根据 OpenAPI 定义
export interface LoginRequest {
  username: string;
  password: string;
  rememberMe: boolean;
}

// 登录响应类型 - 根据 OpenAPI 定义
export interface LoginResponse {
  token: string;
  username: string;
}

export const loginApi = {
  // 用户名密码登录
  login: (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    return request.post("/api/auth/login", data);
  },

  // 退出登录
  logout: (): Promise<ApiResponse<void>> => {
    return request.post("/api/logout");
  },

  // 获取当前用户信息
  getUserInfo: (): Promise<ApiResponse<UserDetailDTO>> => {
    return request.get("/api/user/info");
  },
};
