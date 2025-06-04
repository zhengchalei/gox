import api from './index'
import type {
  ${entityName}CreateDTO,
  ${entityName}UpdateDTO,
  ${entityName}Specification,
  R${entityName}DetailDTO,
  RPage${entityName}ListDTO,
  RVoid,
} from '../types/api'

export const ${entityName}Api = {
  // 创建${entityComment}
  create: (data: ${entityName}CreateDTO): Promise<RVoid> => {
    return api.post(`/api/sys/${entityName?lower_case}`, data)
  },

  // 更新${entityComment}
  update: (data: ${entityName}UpdateDTO): Promise<RVoid> => {
    return api.put(`/api/sys/${entityName?lower_case}`, data)
  },

  // 根据ID查询${entityComment}
  findById: (id: number): Promise<R${entityName}DetailDTO> => {
    return api.get(`/api/sys/${entityName?lower_case}/$\{id}`)
  },

  // 分页查询${entityComment}
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: ${entityName}Specification): Promise<RPage${entityName}ListDTO> => {
    const params = new URLSearchParams({
      currentPage: currentPage.toString(),
      pageSize: pageSize.toString(),
      ...Object.fromEntries(
        Object.entries(specification)
          .filter(([_, value]) => value !== undefined && value !== '')
          .map(([key, value]) => [
            key,
            Array.isArray(value) ? value.join(',') : value?.toString() || ''
          ])
      )
    })
    return api.get(`/api/sys/${entityName?lower_case}/page?$\{params}`)
  },

  // 删除${entityComment}
  deleteById: (id: number): Promise<RVoid> => {
    return api.delete(`/api/sys/${entityName?lower_case}/$\{id}`)
  }
}