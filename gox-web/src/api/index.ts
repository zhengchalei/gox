// OpenAPI 标准响应类型
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  code: number
  timestamp: number
}

// 分页对象类型
export interface PageableObject {
  offset: number
  sort: SortObject
  paged: boolean
  pageSize: number
  pageNumber: number
  unpaged: boolean
}

export interface SortObject {
  empty: boolean
  sorted: boolean
  unsorted: boolean
}

export interface PageResponse<T> {
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  size: number
  content: T[]
  number: number
  sort: SortObject
  numberOfElements: number
  pageable: PageableObject
  empty: boolean
}
