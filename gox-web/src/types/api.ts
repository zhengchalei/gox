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

// 登录请求类型 - 根据 OpenAPI 定义
export interface LoginRequest {
  username: string
  password: string
  rememberMe: boolean
}

// 登录响应类型 - 根据 OpenAPI 定义
export interface LoginResponse {
  token: string
  username: string
}

// 用户相关类型
export interface UserDetailDTO {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  enabled: boolean
  createdTime: string
  updatedTime: string
  roleIds: number[]
  roles: RoleInfo[]
}

export interface UserListDTO {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  enabled: boolean
  createdTime: string
  updatedTime: string
}

export interface UserCreateDTO {
  username: string
}

export interface UserUpdateDTO {
  id: number
  username: string
  enabled: boolean
  email?: string
  phone?: string
}

export interface UserSpecification {
  id?: number
  username?: string
  nickname?: string
  avatar?: string
  email?: string
  phone?: string
  enabled?: boolean
  createdTime?: string
  updatedTime?: string
}

// 角色相关类型
export interface RoleDetailDTO {
  id: number
  name: string
  code: string
  enabled: boolean
  description?: string
  createdTime: string
  updatedTime: string
  permissionIds: number[]
  permissions: PermissionInfo[]
  routePermissionIds: number[]
  routePermissions: RoutePermissionInfo[]
}

export interface RoleListDTO {
  id: number
  name: string
  code: string
  enabled: boolean
  description?: string
  createdTime: string
  updatedTime: string
  permissionIds: number[]
  routePermissionIds: number[]
}

export interface RoleCreateDTO {
  name: string
  code: string
  description?: string
  permissionIds: number[]
}

export interface RoleUpdateDTO {
  id: number
  name: string
  code: string
  description?: string
  permissionIds: number[]
}

export interface RoleSpecification {
  id?: number
  name?: string
  code?: string
  enabled?: boolean
  description?: string
  createdTime?: string
  updatedTime?: string
  permissionIds?: number[]
  routePermissionIds?: number[]
}

export interface RoleInfo {
  id: number
  name: string
  code: string
  description?: string
  enabled: boolean
  createdTime: string
  updatedTime: string
  permissionIds: number[]
  permissions: PermissionInfo[]
}

// 权限相关类型
export interface PermissionDetailDTO {
  id: number
  name: string
  code: string
  description?: string
  createdTime: string
  updatedTime: string
  roleIds: number[]
  roles: RoleInfo[]
}

export interface PermissionListDTO {
  id: number
  name: string
  code: string
  description?: string
  createdTime: string
  updatedTime: string
  roleIds: number[]
}

export interface PermissionCreateDTO {
  name: string
  code: string
  description?: string
  roleIds: number[]
}

export interface PermissionUpdateDTO {
  id: number
  name: string
  code: string
  description?: string
  roleIds: number[]
}

export interface PermissionSpecification {
  id?: number
  name?: string
  code?: string
  description?: string
  createdTime?: string
  updatedTime?: string
  roleIds?: number[]
}

export interface PermissionInfo {
  id: number
  name: string
  code: string
  description?: string
  createdTime: string
  updatedTime: string
}

// 路由权限相关类型
export interface RoutePermissionInfo {
  id: number
  path: string
  method: string
  description?: string
  createdTime: string
  updatedTime: string
}

// 文件相关类型
export enum StorageType {
  LOCAL = 'LOCAL',
  ALIYUN = 'ALIYUN',
  TENCENT = 'TENCENT',
  MINIO = 'MINIO'
}

export interface FileInfoDetailDTO {
  id: number
  originalName: string
  storageName: string
  path: string
  extension: string
  size: number
  mimeType: string
  storageType: StorageType
  createdTime: string
  updatedTime: string
}

export interface FileInfoListDTO {
  id: number
  originalName: string
  storageName: string
  extension: string
  size: number
  mimeType: string
  storageType: StorageType
  createdTime: string
  updatedTime: string
}

export interface FileInfoSpecification {
  id?: number
  originalName?: string
  storageName?: string
  path?: string
  extension?: string
  size?: number
  mimeType?: string
  storageType?: StorageType
  createdTime?: string
  updatedTime?: string
}

// 通用响应类型别名
export type RLoginResponse = ApiResponse<LoginResponse>
export type RUserDetailDTO = ApiResponse<UserDetailDTO>
export type RBoolean = ApiResponse<boolean>
export type RVoid = ApiResponse<void>

// 用户管理响应类型
export type RPageUserListDTO = ApiResponse<PageResponse<UserListDTO>>

// 角色管理响应类型
export type RRoleDetailDTO = ApiResponse<RoleDetailDTO>
export type RPageRoleListDTO = ApiResponse<PageResponse<RoleListDTO>>

// 权限管理响应类型
export type RPermissionDetailDTO = ApiResponse<PermissionDetailDTO>
export type RPagePermissionListDTO = ApiResponse<PageResponse<PermissionListDTO>>

// 文件管理响应类型
export type RFileInfoDetailDTO = ApiResponse<FileInfoDetailDTO>
export type RListFileInfoDetailDTO = ApiResponse<FileInfoDetailDTO[]>
export type RListFileInfoListDTO = ApiResponse<FileInfoListDTO[]>
export type RPageFileInfoListDTO = ApiResponse<PageResponse<FileInfoListDTO>>
export type RMapStringString = ApiResponse<Record<string, string>>
