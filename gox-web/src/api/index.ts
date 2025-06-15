// OpenAPI 标准响应类型
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data: T;
  code: number;
  timestamp: number;
}

// 分页对象类型
export interface PageableObject {
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

export interface SortObject {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface PageResponse<T> {
  content: T[];
  pageable: PageableObject;
}
