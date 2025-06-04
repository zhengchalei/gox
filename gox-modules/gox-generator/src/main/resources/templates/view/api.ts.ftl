import api, { type ApiResponse, type PageResponse } from "../index";

// 权限相关类型
export interface ${entityName}DetailDTO {
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
}

export interface ${entityName}ListDTO {
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
}

export interface ${entityName}CreateDTO {
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
}

export interface ${entityName}UpdateDTO {
  id: number
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
}

export interface ${entityName}Specification {
  id?: number
  <#list fields as field>
  ${field.name}?: ${field.type}
  </#list>
}

export interface ${entityName}Info {
  id: number
  <#list fields as field>
  ${field.name}<#if field.nullable>?</#if>: ${field.type}
  </#list>
}

export const ${entityName?lower_case}Api = {
  // 创建权限
  create: (data: ${entityName}CreateDTO): Promise<ApiResponse<void>> => {
    return api.post("/api/${moduleName}/${entityName?lower_case}", data);
  },

  // 更新权限
  update: (data: ${entityName}UpdateDTO): Promise<ApiResponse<void>> => {
    return api.put("/api/${moduleName}/${entityName?lower_case}", data);
  },

  // 根据ID查询权限
  findById: (id: number): Promise<ApiResponse<${entityName}DetailDTO>> => {
    return api.get(`/api/${moduleName}/${entityName?lower_case}/${id}`);
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
    return api.get(`/api/${moduleName}/${entityName?lower_case}/page?${params}`);
  },

  // 删除权限
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return api.delete(`/api/${moduleName}/${entityName?lower_case}/${id}`);
  },
};
