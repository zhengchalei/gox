import request from "../../utils/request.ts";
import type { ApiResponse, PageResponse } from "../index.ts";

// 权限相关类型
export interface MqttAclDetailDTO {
  id: number
  ipaddress: String
  username: String
  clientId: String
  action: String
  permission: String
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface MqttAclListDTO {
  id: number
  ipaddress: String
  username: String
  clientId: String
  action: String
  permission: String
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface MqttAclCreateDTO {
  ipaddress?: String
  username?: String
  clientId?: String
  action?: String
  permission?: String
}

export interface MqttAclUpdateDTO {
  id: number
  ipaddress?: String
  username?: String
  clientId?: String
  action?: String
  permission?: String
}

export interface MqttAclSpecification {
  id?: number
  ipaddress?: String
  username?: String
  clientId?: String
  action?: String
  permission?: String
}

export const mqttaclApi = {
  // 创建权限
  create: (data: MqttAclCreateDTO): Promise<ApiResponse<void>> => {
    return request.post("/api/iot/mqttacl", data);
  },

  // 更新权限
  update: (data: MqttAclUpdateDTO): Promise<ApiResponse<void>> => {
    return request.put("/api/iot/mqttacl", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<MqttAclDetailDTO>> => {
    return request.get(`/api/iot/mqttacl/${id}`);
  },

  // 分页查询权限
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: MqttAclSpecification
  ): Promise<ApiResponse<PageResponse<MqttAclListDTO>>> => {
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
    return request.get(`/api/iot/mqttacl/page?${params}`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/api/iot/mqttacl/${id}`);
  },
};
