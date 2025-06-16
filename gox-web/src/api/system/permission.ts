import request from "../../utils/request.ts";
import type { ApiResponse, PageResponse } from "../index.ts";
import type { RoleInfo } from "./role";

// 权限相关类型
export interface PermissionDetailDTO {
  id: number;
  name: string;
  code: string;
  description?: string;
  createdTime: string;
  updatedTime: string;
  roleIds: number[];
  roles: RoleInfo[];
}

export interface PermissionListDTO {
  id: number;
  name: string;
  code: string;
  description?: string;
  createdTime: string;
  updatedTime: string;
  roleIds: number[];
}

export interface PermissionCreateDTO {
  name: string;
  code: string;
  description?: string;
  roleIds: number[];
}

export interface PermissionUpdateDTO {
  id: number;
  name: string;
  code: string;
  description?: string;
  roleIds: number[];
}

export interface PermissionSpecification {
  id?: number;
  name?: string;
  code?: string;
  description?: string;
  createdTime?: string;
  updatedTime?: string;
  roleIds?: number[];
}

export const permissionApi = {
  // 创建权限
  create: (data: PermissionCreateDTO): Promise<ApiResponse<void>> => {
    return request.post("/api/sys/permission", data);
  },

  // 更新权限
  update: (data: PermissionUpdateDTO): Promise<ApiResponse<void>> => {
    return request.put("/api/sys/permission", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<PermissionDetailDTO>> => {
    return request.get(`/api/sys/permission/${id}`);
  },

  // 查询权限树
  findTree: (): Promise<ApiResponse<PageResponse<PermissionListDTO>>> => {
    return request.get(`/api/sys/permission/page`);
  },

  // 分页查询权限
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: PermissionSpecification
  ): Promise<ApiResponse<PageResponse<PermissionListDTO>>> => {
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
    return request.get(`/api/sys/permission/page?${params}`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/api/sys/permission/${id}`);
  },
};
