import request from "../../utils/request.ts";
import type { ApiResponse, PageResponse } from "../index.ts";

// 权限相关类型
export interface ${entityName}DetailDTO {
  id: number
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface ${entityName}ListDTO {
  id: number
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
  createdTime: string
  modifiedTime?: string
  createdBy?: number
  modifiedBy?: number
}

export interface ${entityName}CreateDTO {
  <#list fields as field>
  ${field.name}<#if field.nullable==false>?</#if>: ${field.type}
  </#list>
}

export interface ${entityName}UpdateDTO {
  id: number
  <#list fields as field>
  ${field.name}<#if field.nullable==false>?</#if>: ${field.type}
  </#list>
}

export interface ${entityName}Specification {
  id?: number
  <#list fields as field>
  ${field.name}?: ${field.type}
  </#list>
}

export const ${entityName?lower_case}Api = {
  // 创建权限
  create: (data: ${entityName}CreateDTO): Promise<ApiResponse<void>> => {
    return request.post("/api/${moduleName}/${entityName?lower_case}", data);
  },

  // 更新权限
  update: (data: ${entityName}UpdateDTO): Promise<ApiResponse<void>> => {
    return request.put("/api/${moduleName}/${entityName?lower_case}", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<${entityName}DetailDTO>> => {
    return request.get(`/api/${moduleName}/${entityName?lower_case}/<#noparse>${id}</#noparse>`);
  },

  // 分页查询权限
  findPage: (
    currentPage: number = 1,
    pageSize: number = 10,
    specification: ${entityName}Specification
  ): Promise<ApiResponse<PageResponse<${entityName}ListDTO>>> => {
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
    return request.get(`/api/${moduleName}/${entityName?lower_case}/page?<#noparse>${params}</#noparse>`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/api/${moduleName}/${entityName?lower_case}/<#noparse>${id}</#noparse>`);
  },
};
