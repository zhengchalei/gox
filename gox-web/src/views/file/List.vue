<template>
  <div class="file-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文件列表</span>
          <div class="header-actions">
            <el-button type="danger" :disabled="selectedFiles.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              批量删除 ({{ selectedFiles.length }})
            </el-button>
            <el-button type="primary" @click="$router.push('/file/upload')">
              <el-icon><Upload /></el-icon>
              上传文件
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" :inline="true" >
          <el-form-item label="文件名">
            <el-input 
              v-model="searchForm.originalName" 
              placeholder="请输入文件名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="文件类型">
            <el-input 
              v-model="searchForm.mimeType" 
              placeholder="请输入文件类型"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="存储类型">
            <el-select v-model="searchForm.storageType" placeholder="请选择存储类型" clearable>
              <el-option label="本地存储" value="LOCAL" />
              <el-option label="阿里云OSS" value="ALIYUN" />
              <el-option label="腾讯云COS" value="TENCENT" />
              <el-option label="MinIO" value="MINIO" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 视图切换 -->
      <div class="view-switcher">
        <el-radio-group v-model="viewMode" >
          <el-radio-button value="table">表格视图</el-radio-button>
          <el-radio-button value="grid">网格视图</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'">
        <el-table 
          v-loading="loading" 
          :data="tableData" 
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="originalName" label="文件名" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="file-name-cell">
                <el-icon class="file-icon">
                  <Document v-if="isDocument(row.mimeType)" />
                  <Picture v-else-if="isImage(row.mimeType)" />
                  <VideoPlay v-else-if="isVideo(row.mimeType)" />
                  <Headset v-else-if="isAudio(row.mimeType)" />
                  <Files v-else />
                </el-icon>
                <span>{{ row.originalName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="文件大小" width="120">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column prop="mimeType" label="文件类型" width="150" />
          <el-table-column prop="storageType" label="存储类型" width="100">
            <template #default="{ row }">
              <el-tag  :type="getStorageTypeColor(row.storageType)">
                {{ getStorageTypeName(row.storageType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300" fixed="right">
            <template #default="{ row }">
              <el-button type="primary"  @click="handlePreview(row)">
                预览
              </el-button>
              <el-button type="success"  @click="handleDownload(row)">
                下载
              </el-button>
              <el-button type="warning"  @click="handleCopyUrl(row)">
                链接
              </el-button>
              <el-button type="danger"  @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 网格视图 -->
      <div v-else class="grid-view">
        <div class="file-grid">
          <div 
            v-for="file in tableData" 
            :key="file.id" 
            class="file-card"
            @click="handleFileSelect(file)"
            :class="{ selected: selectedFiles.some(f => f.id === file.id) }"
          >
            <div class="file-preview">
              <img 
                v-if="isImage(file.mimeType)" 
                :src="getPreviewUrl(file)" 
                :alt="file.originalName"
                @error="handleImageError"
              />
              <el-icon v-else class="file-type-icon" :size="60">
                <Document v-if="isDocument(file.mimeType)" />
                <VideoPlay v-else-if="isVideo(file.mimeType)" />
                <Headset v-else-if="isAudio(file.mimeType)" />
                <Files v-else />
              </el-icon>
            </div>
            <div class="file-info">
              <div class="file-name" :title="file.originalName">{{ file.originalName }}</div>
              <div class="file-meta">
                <span>{{ formatFileSize(file.size) }}</span>
                <el-tag  :type="getStorageTypeColor(file.storageType)">
                  {{ getStorageTypeName(file.storageType) }}
                </el-tag>
              </div>
              <div class="file-actions">
                <el-button-group >
                  <el-button @click.stop="handlePreview(file)">预览</el-button>
                  <el-button @click.stop="handleDownload(file)">下载</el-button>
                  <el-button @click.stop="handleDelete(file)" type="danger">删除</el-button>
                </el-button-group>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[12, 24, 48, 96]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog 
      title="文件预览" 
      v-model="previewVisible" 
      width="80%" 
      :before-close="handlePreviewClose"
    >
      <div v-if="previewFile" class="preview-content">
        <!-- 图片预览 -->
        <div v-if="isImage(previewFile.mimeType)" class="image-preview">
          <img :src="getPreviewUrl(previewFile)" :alt="previewFile.originalName" />
        </div>
        
        <!-- 视频预览 -->
        <div v-else-if="isVideo(previewFile.mimeType)" class="video-preview">
          <video controls :src="getPreviewUrl(previewFile)">
            您的浏览器不支持视频播放
          </video>
        </div>
        
        <!-- 音频预览 -->
        <div v-else-if="isAudio(previewFile.mimeType)" class="audio-preview">
          <audio controls :src="getPreviewUrl(previewFile)">
            您的浏览器不支持音频播放
          </audio>
        </div>
        
        <!-- 其他文件类型 -->
        <div v-else class="file-info-preview">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="文件名">{{ previewFile.originalName }}</el-descriptions-item>
            <el-descriptions-item label="文件大小">{{ formatFileSize(previewFile.size) }}</el-descriptions-item>
            <el-descriptions-item label="文件类型">{{ previewFile.mimeType }}</el-descriptions-item>
            <el-descriptions-item label="存储类型">{{ getStorageTypeName(previewFile.storageType) }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ formatDateTime(previewFile.createdTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDateTime(previewFile.updatedTime) }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 20px; text-align: center;">
            <el-button type="primary" @click="handleDownload(previewFile)">
              下载文件
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Upload, 
  Delete, 
  Document, 
  Picture, 
  VideoPlay, 
  Headset, 
  Files 
} from '@element-plus/icons-vue'
import { fileApi } from '../../api/file'
import type { FileInfoListDTO, FileInfoSpecification, StorageType } from '../../types/api'

// 响应式数据
const loading = ref(false)
const tableData = ref<FileInfoListDTO[]>([])
const selectedFiles = ref<FileInfoListDTO[]>([])
const viewMode = ref<'table' | 'grid'>('table')

// 搜索表单
const searchForm = reactive<FileInfoSpecification>({
  originalName: '',
  mimeType: '',
  storageType: undefined
})

// 分页
const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

// 预览
const previewVisible = ref(false)
const previewFile = ref<FileInfoListDTO | null>(null)

// 方法
const formatFileSize = (size: number): string => {
  if (size === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDateTime = (dateTime: string) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

const isImage = (mimeType: string) => {
  return mimeType.startsWith('image/')
}

const isVideo = (mimeType: string) => {
  return mimeType.startsWith('video/')
}

const isAudio = (mimeType: string) => {
  return mimeType.startsWith('audio/')
}

const isDocument = (mimeType: string) => {
  const docTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'text/plain',
    'text/html',
    'text/css',
    'text/javascript'
  ]
  return docTypes.includes(mimeType) || mimeType.startsWith('text/')
}

const getStorageTypeColor = (storageType: StorageType) => {
  const colors = {
    LOCAL: 'info',
    ALIYUN: 'warning',
    TENCENT: 'success',
    MINIO: 'primary'
  }
  return colors[storageType] || 'info'
}

const getStorageTypeName = (storageType: StorageType) => {
  const names = {
    LOCAL: '本地',
    ALIYUN: '阿里云',
    TENCENT: '腾讯云',
    MINIO: 'MinIO'
  }
  return names[storageType] || storageType
}

const getPreviewUrl = (file: FileInfoListDTO) => {
  // 这里应该根据实际情况返回文件的预览URL
  // 可能需要调用API获取临时访问链接
  return `/api/v1/file/download/${file.storageName}`
}

const fetchFiles = async () => {
  try {
    loading.value = true
    const response = await fileApi.findPage(pagination.page, pagination.size, searchForm)
    
    tableData.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    console.error('获取文件列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchFiles()
}

const handleReset = () => {
  Object.assign(searchForm, {
    originalName: '',
    mimeType: '',
    storageType: undefined
  })
  handleSearch()
}

const handleSelectionChange = (selection: FileInfoListDTO[]) => {
  selectedFiles.value = selection
}

const handleFileSelect = (file: FileInfoListDTO) => {
  const index = selectedFiles.value.findIndex(f => f.id === file.id)
  if (index > -1) {
    selectedFiles.value.splice(index, 1)
  } else {
    selectedFiles.value.push(file)
  }
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchFiles()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchFiles()
}

const handlePreview = (file: FileInfoListDTO) => {
  previewFile.value = file
  previewVisible.value = true
}

const handlePreviewClose = () => {
  previewVisible.value = false
  previewFile.value = null
}

const handleDownload = async (file: FileInfoListDTO) => {
  try {
    const blob = await fileApi.download(file.storageName)
    
    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = file.originalName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('文件下载成功')
  } catch (error) {
    console.error('文件下载失败:', error)
    ElMessage.error('文件下载失败')
  }
}

const handleCopyUrl = async (file: FileInfoListDTO) => {
  try {
    const response = await fileApi.getDownloadUrl(file.id)
    const url = response.data.url || response.data.downloadUrl
    
    if (url) {
      await navigator.clipboard.writeText(url)
      ElMessage.success('下载链接已复制到剪贴板')
    } else {
      ElMessage.error('获取下载链接失败')
    }
  } catch (error) {
    console.error('获取下载链接失败:', error)
    ElMessage.error('获取下载链接失败')
  }
}

const handleDelete = async (file: FileInfoListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.originalName}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await fileApi.deleteById(file.id)
    ElMessage.success('删除成功')
    fetchFiles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文件失败:', error)
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要删除的文件')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedFiles.value.length} 个文件吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const fileIds = selectedFiles.value.map(file => file.id)
    await fileApi.batchDelete(fileIds)
    
    ElMessage.success('批量删除成功')
    selectedFiles.value = []
    fetchFiles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
  
  // 可以显示一个默认的图标或者错误提示
  const parent = img.parentElement
  if (parent) {
    parent.innerHTML = '<el-icon><Picture /></el-icon>'
  }
}

// 生命周期
onMounted(() => {
  fetchFiles()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
}

.view-switcher {
  margin-bottom: 20px;
  text-align: right;
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  color: #409eff;
}

.grid-view {
  min-height: 400px;
}

.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.file-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.file-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.file-card.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.file-preview {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  overflow: hidden;
}

.file-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.file-type-icon {
  color: #909399;
}

.file-info {
  padding: 12px;
}

.file-name {
  font-weight: 500;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #909399;
}

.file-actions {
  text-align: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.preview-content {
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 60vh;
  object-fit: contain;
}

.video-preview video {
  max-width: 100%;
  max-height: 60vh;
}

.audio-preview {
  padding: 40px 0;
}

.file-info-preview {
  padding: 20px;
}
</style> 