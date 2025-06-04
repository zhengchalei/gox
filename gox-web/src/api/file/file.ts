import api, { type ApiResponse, type PageResponse } from '../index'

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
  fileKey: string
  path: string
  extension: string
  size: number
  mimeType: string
  storageType: StorageType
  createdTime: string
  updatedTime: string
  downloadUrl: string
}

export interface FileInfoListDTO {
  id: number
  originalName: string
  fileKey: string
  extension: string
  size: number
  mimeType: string
  storageType: StorageType
  createdTime: string
  updatedTime: string
  downloadUrl: string
}

export interface FileInfoSpecification {
  id?: number
  originalName?: string
  fileKey?: string
  path?: string
  extension?: string
  size?: number
  mimeType?: string
  storageType?: StorageType
  createdTime?: string
  updatedTime?: string
  downloadUrl?: string
}

export const fileApi = {
  // 上传单个文件
  upload: (file: File): Promise<ApiResponse<FileInfoDetailDTO>> => {
    const formData = new FormData()
    formData.append('file', file)

    return api.post('/api/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 批量上传文件
  batchUpload: (files: File[]): Promise<ApiResponse<FileInfoDetailDTO[]>> => {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })

    return api.post('/api/file/batch-upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 根据ID查询文件详情
  findById: (id: number): Promise<ApiResponse<FileInfoDetailDTO>> => {
    return api.get(`/api/file/${id}`)
  },

  // 根据存储名称查询文件详情
  findByfileKey: (fileKey: string): Promise<ApiResponse<FileInfoDetailDTO>> => {
    return api.get(`/api/file/storage/${fileKey}`)
  },

  // 按存储类型查询文件
  findByStorageType: (storageType: StorageType): Promise<ApiResponse<FileInfoListDTO[]>> => {
    return api.get(`/api/file/type/${storageType}`)
  },

  // 按原始名称搜索文件
  search: (name: string): Promise<ApiResponse<FileInfoListDTO[]>> => {
    return api.get(`/api/file/search?name=${encodeURIComponent(name)}`)
  },

  // 分页查询文件
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: FileInfoSpecification): Promise<ApiResponse<PageResponse<FileInfoListDTO>>> => {
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
    return api.get(`/api/file/page?${params}`)
  },

  // 获取文件下载URL
  getDownloadUrl: (id: number): Promise<ApiResponse<Record<string, string>>> => {
    return api.get(`/api/file/url/${id}`)
  },

  // 获取文件临时访问URL
  getTemporaryUrl: (id: number, minutes: number = 5): Promise<ApiResponse<Record<string, string>>> => {
    return api.get(`/api/file/temp-url/${id}?minutes=${minutes}`)
  },

  // 下载文件
  download: (fileKey: string): Promise<Blob> => {
    return api.get(`/api/file/download/${fileKey}`, {
      responseType: 'blob'
    })
  },

  // 删除文件
  deleteById: (id: number): Promise<ApiResponse<void>> => {
    return api.delete(`/api/file/${id}`)
  },

  // 批量删除文件
  batchDelete: (ids: number[]): Promise<ApiResponse<void>> => {
    return api.delete('/api/file/batch', {
      data: ids
    })
  }
} 