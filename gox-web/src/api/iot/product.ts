import request from "../../utils/request.ts";
import type { ApiResponse, PageResponse } from "../index.ts";

// 权限相关类型
export interface ProductDetailDTO {
  id: number;
  code: String;
  name: String;
  description?: String;
  status: Boolean;
  createdTime: string;
  modifiedTime?: string;
  createdBy?: number;
  modifiedBy?: number;
}

export interface ProductListDTO {
  id: number;
  code: String;
  name: String;
  description?: String;
  status: Boolean;
  createdTime: string;
  modifiedTime?: string;
  createdBy?: number;
  modifiedBy?: number;
}

export interface ProductCreateDTO {
  code?: String;
  name?: String;
  description: String;
  status?: Boolean;
}

export interface ProductUpdateDTO {
  id: number;
  code?: String;
  name?: String;
  description: String;
  status?: Boolean;
}

export interface ProductSpecification {
  id?: number;
  code?: String;
  name?: String;
  description?: String;
  status?: Boolean;
}

export const productApi = {
  // 创建权限
  create: (data: ProductCreateDTO): Promise<ApiResponse<void>> => {
    return request.post("/api/iot/product", data);
  },

  // 更新权限
  update: (data: ProductUpdateDTO): Promise<ApiResponse<void>> => {
    return request.put("/api/iot/product", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<ProductDetailDTO>> => {
    return request.get(`/api/iot/product/${id}`);
  },

  // 分页查询权限
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: ProductSpecification
  ): Promise<ApiResponse<PageResponse<ProductListDTO>>> => {
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
    return request.get(`/api/iot/product/page?${params}`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/api/iot/product/${id}`);
  },
};
