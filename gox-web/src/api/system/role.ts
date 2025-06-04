import request from "../../utils/request.ts";
import type { ApiResponse, PageResponse } from "../index.ts";
import type { PermissionInfo } from "./permission.ts";

// 角色相关类型
export interface RoleDetailDTO {
  id: number;
  name: string;
  code: string;
  enabled: boolean;
  description?: string;
  createdTime: string;
  updatedTime: string;
  permissionIds: number[];
  permissions: PermissionInfo[];
}

export interface RoleListDTO {
  id: number;
  name: string;
  code: string;
  enabled: boolean;
  description?: string;
  createdTime: string;
  updatedTime: string;
  permissionIds: number[];
}

export interface RoleCreateDTO {
  name: string;
  code: string;
  description?: string;
}

export interface RoleUpdateDTO {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export interface RolePermissionUpdateDTO {
  id: number;
  permissionIds: number[];
}

export interface RoleSpecification {
  id?: number;
  name?: string;
  code?: string;
  enabled?: boolean;
  description?: string;
  createdTime?: string;
  updatedTime?: string;
  permissionIds?: number[];
}

export interface RoleInfo {
  id: number;
  name: string;
  code: string;
  description?: string;
  enabled: boolean;
  createdTime: string;
  updatedTime: string;
  permissionIds: number[];
  permissions: PermissionInfo[];
}

export const roleApi = {
  // 创建角色
  create: function (data: RoleCreateDTO): Promise<ApiResponse<void>> {
    return request.post("/api/sys/role", data);
  },

  // 更新角色
  update: (data: RoleUpdateDTO): Promise<ApiResponse<void>> => {
    return request.put("/api/sys/role", data);
  },

  // 分配角色权限
  assignRolePermission: (
    data: RolePermissionUpdateDTO
  ): Promise<ApiResponse<void>> => {
    return request.post("/api/sys/role/assign-permission", data);
  },

  // 根据ID查询角色
  findById: (id: number): Promise<ApiResponse<RoleDetailDTO>> => {
    return request.get(`/api/sys/role/${id}`);
  },

  // 分页查询角色
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: RoleSpecification
  ): Promise<ApiResponse<PageResponse<RoleListDTO>>> => {
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
    return request.get(`/api/sys/role/page?${params}`);
  },

  // 删除角色
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/api/sys/role/${id}`);
  },
};
