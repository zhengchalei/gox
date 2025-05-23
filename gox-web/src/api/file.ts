import api from './index'
import type {
  FileInfoSpecification,
  RFileInfoDetailDTO,
  RListFileInfoDetailDTO,
  RListFileInfoListDTO,
  RPageFileInfoListDTO,
  RMapStringString,
  RVoid,
  StorageType
} from '../types/api'

export const fileApi = {
  // 上传单个文件
  upload: (file: File): Promise<RFileInfoDetailDTO> => {
    const formData = new FormData()
    formData.append('file', file)
    
    return api.post('/api/v1/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 批量上传文件
  batchUpload: (files: File[]): Promise<RListFileInfoDetailDTO> => {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    
    return api.post('/api/v1/file/batch-upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 根据ID查询文件详情
  findById: (id: number): Promise<RFileInfoDetailDTO> => {
    return api.get(`/api/v1/file/${id}`)
  },

  // 根据存储名称查询文件详情
  findByStorageName: (storageName: string): Promise<RFileInfoDetailDTO> => {
    return api.get(`/api/v1/file/storage/${storageName}`)
  },

  // 按存储类型查询文件
  findByStorageType: (storageType: StorageType): Promise<RListFileInfoListDTO> => {
    return api.get(`/api/v1/file/type/${storageType}`)
  },

  // 按原始名称搜索文件
  search: (name: string): Promise<RListFileInfoListDTO> => {
    return api.get(`/api/v1/file/search?name=${encodeURIComponent(name)}`)
  },

  // 分页查询文件
  findPage: (currentPage: number = 1, pageSize: number = 10, specification: FileInfoSpecification): Promise<RPageFileInfoListDTO> => {
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
    return api.get(`/api/v1/file/page?${params}`)
  },

  // 获取文件下载URL
  getDownloadUrl: (id: number): Promise<RMapStringString> => {
    return api.get(`/api/v1/file/url/${id}`)
  },

  // 获取文件临时访问URL
  getTemporaryUrl: (id: number, minutes: number = 5): Promise<RMapStringString> => {
    return api.get(`/api/v1/file/temp-url/${id}?minutes=${minutes}`)
  },

  // 下载文件
  download: (storageName: string): Promise<Blob> => {
    return api.get(`/api/v1/file/download/${storageName}`, {
      responseType: 'blob'
    })
  },

  // 删除文件
  deleteById: (id: number): Promise<RVoid> => {
    return api.delete(`/api/v1/file/${id}`)
  },

  // 批量删除文件
  batchDelete: (ids: number[]): Promise<RVoid> => {
    return api.delete('/api/v1/file/batch', {
      data: ids
    })
  }
} 