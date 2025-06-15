import request from "../../utils/request.ts";
import type { ApiResponse, PageResponse } from "../index.ts";

// 权限相关类型
export interface MqttUserDetailDTO {
  id: number
  isSuperuser: Boolean
  username: String
  passwordHash: String
  salt: String
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface MqttUserListDTO {
  id: number
  isSuperuser: Boolean
  username: String
  passwordHash: String
  salt: String
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface MqttUserCreateDTO {
  isSuperuser?: Boolean
  username?: String
  passwordHash?: String
  salt?: String
}

export interface MqttUserUpdateDTO {
  id: number
  isSuperuser?: Boolean
  username?: String
  passwordHash?: String
  salt?: String
}

export interface MqttUserSpecification {
  id?: number
  isSuperuser?: Boolean
  username?: String
  passwordHash?: String
  salt?: String
}

export const mqttUserApi = {
  // 创建权限
  create: (data: MqttUserCreateDTO): Promise<ApiResponse<void>> => {
    return request.post("/api/iot/mqtt-user", data);
  },

  // 更新权限
  update: (data: MqttUserUpdateDTO): Promise<ApiResponse<void>> => {
    return request.put("/api/iot/mqtt-user", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<MqttUserDetailDTO>> => {
    return request.get(`/api/iot/mqtt-user/${id}`);
  },

  // 分页查询权限
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: MqttUserSpecification
  ): Promise<ApiResponse<PageResponse<MqttUserListDTO>>> => {
    const params = new URLSearchParams({
      currentPage: currentPage.toString(),
      pageSize: pageSize.toString(),
      ...Object.fromEntries(
        Object.entries(specification)
          .filter(([_, value]) => value !== undefined && value !== "")
          .map(([key, value]) => [
            key,
            Array.isArray(value) ? value.join(",") : value?.toString() || "",
          ])
      ),
    });
    return request.get(`/api/iot/mqtt-user/page?${params}`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/api/iot/mqtt-user/${id}`);
  },
};
