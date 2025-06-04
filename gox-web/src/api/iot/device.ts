import api, { type ApiResponse, type PageResponse } from "../index";

// 权限相关类型
export interface DeviceDetailDTO {
  id: number
  code: String
  name: String
  description?: String
  online: Boolean
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface DeviceListDTO {
  id: number
  code: String
  name: String
  description?: String
  online: Boolean
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface DeviceCreateDTO {
  code?: String
  name?: String
  description: String
  online?: Boolean
}

export interface DeviceUpdateDTO {
  id: number
  code?: String
  name?: String
  description: String
  online?: Boolean
}

export interface DeviceSpecification {
  id?: number
  code?: String
  name?: String
  description?: String
  online?: Boolean
}

export const deviceApi = {
  // 创建权限
  create: (data: DeviceCreateDTO): Promise<ApiResponse<void>> => {
    return api.post("/api/iot/device", data);
  },

  // 更新权限
  update: (data: DeviceUpdateDTO): Promise<ApiResponse<void>> => {
    return api.put("/api/iot/device", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<DeviceDetailDTO>> => {
    return api.get(`/api/iot/device/${id}`);
  },

  // 分页查询权限
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: DeviceSpecification
  ): Promise<ApiResponse<PageResponse<DeviceListDTO>>> => {
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
    return api.get(`/api/iot/device/page?${params}`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return api.delete(`/api/iot/device/${id}`);
  },
};
