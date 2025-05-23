<template>
  <div class="file-upload">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文件上传</span>
          <el-button type="primary" @click="handleClearAll">
            <el-icon><Delete /></el-icon>
            清空列表
          </el-button>
        </div>
      </template>
      
      <!-- 上传区域 -->
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          class="upload-dragger"
          drag
          :multiple="true"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
          accept="*"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持任意格式文件，支持多文件选择和拖拽上传
            </div>
          </template>
        </el-upload>
      </div>

      <!-- 操作按钮 -->
      <div class="upload-actions" v-if="fileList.length > 0">
        <el-button type="primary" :loading="uploading" @click="handleUploadAll">
          <el-icon><Upload /></el-icon>
          上传全部文件 ({{ fileList.length }})
        </el-button>
        <el-button @click="handleRemoveAll">
          <el-icon><Delete /></el-icon>
          移除全部
        </el-button>
      </div>

      <!-- 文件列表 -->
      <div class="file-list" v-if="fileList.length > 0">
        <el-divider content-position="left">文件列表</el-divider>
        <el-table :data="fileList" style="width: 100%">
          <el-table-column prop="name" label="文件名" min-width="200" show-overflow-tooltip />
          <el-table-column prop="size" label="文件大小" width="120">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="上传状态" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'ready'" type="info">待上传</el-tag>
              <el-tag v-else-if="row.status === 'uploading'" type="warning">上传中</el-tag>
              <el-tag v-else-if="row.status === 'success'" type="success">已上传</el-tag>
              <el-tag v-else-if="row.status === 'fail'" type="danger">上传失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="进度" width="150">
            <template #default="{ row }">
              <el-progress 
                v-if="row.status === 'uploading' || row.status === 'success'"
                :percentage="row.percentage || 0"
                :status="row.status === 'success' ? 'success' : undefined"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row, $index }">
              <el-button 
                v-if="row.status === 'ready'"
                type="primary" 
                 
                @click="handleUploadSingle(row, $index)"
              >
                上传
              </el-button>
              <el-button 
                v-if="row.status === 'success'"
                type="success" 
                 
                @click="handleViewFile(row)"
              >
                查看
              </el-button>
              <el-button 
                type="danger" 
                 
                @click="handleRemoveSingle($index)"
                :disabled="row.status === 'uploading'"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 上传历史 -->
      <div class="upload-history" v-if="uploadedFiles.length > 0">
        <el-divider content-position="left">上传历史</el-divider>
        <el-table :data="uploadedFiles" style="width: 100%">
          <el-table-column prop="originalName" label="文件名" min-width="200" show-overflow-tooltip />
          <el-table-column prop="size" label="文件大小" width="120">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column prop="mimeType" label="文件类型" width="150" />
          <el-table-column prop="storageType" label="存储类型" width="100">
            <template #default="{ row }">
              <el-tag >{{ row.storageType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary"  @click="handleDownload(row)">
                下载
              </el-button>
              <el-button type="success"  @click="handleCopyUrl(row)">
                复制链接
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, UploadFile, UploadFiles } from 'element-plus'
import { UploadFilled, Upload, Delete } from '@element-plus/icons-vue'
import { fileApi } from '../../api/file'
import type { FileInfoDetailDTO } from '../../types/api'

// 响应式数据
const uploadRef = ref()
const uploading = ref(false)
const fileList = ref<UploadFile[]>([])
const uploadedFiles = ref<FileInfoDetailDTO[]>([])

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

const handleFileChange = (file: UploadFile, fileList: UploadFiles) => {
  // 设置文件状态
  file.status = 'ready'
  file.percentage = 0
}

const handleFileRemove = (file: UploadFile, fileList: UploadFiles) => {
  // Element Plus 会自动处理文件列表更新
}

const handleRemoveSingle = (index: number) => {
  fileList.value.splice(index, 1)
}

const handleRemoveAll = () => {
  fileList.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const handleClearAll = () => {
  handleRemoveAll()
  uploadedFiles.value = []
}

const handleUploadSingle = async (file: UploadFile, index: number) => {
  if (!file.raw) return
  
  try {
    file.status = 'uploading'
    file.percentage = 0
    
    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (file.percentage! < 90) {
        file.percentage! += Math.random() * 30
      }
    }, 200)
    
    const response = await fileApi.upload(file.raw)
    
    clearInterval(progressInterval)
    file.status = 'success'
    file.percentage = 100
    
    // 添加到上传历史
    uploadedFiles.value.unshift(response.data)
    
    ElMessage.success(`文件 "${file.name}" 上传成功`)
  } catch (error) {
    file.status = 'fail'
    file.percentage = 0
    console.error('文件上传失败:', error)
    ElMessage.error(`文件 "${file.name}" 上传失败`)
  }
}

const handleUploadAll = async () => {
  const pendingFiles = fileList.value.filter(file => file.status === 'ready')
  if (pendingFiles.length === 0) {
    ElMessage.warning('没有待上传的文件')
    return
  }
  
  uploading.value = true
  
  try {
    // 检查是否可以批量上传
    if (pendingFiles.length > 1) {
      // 批量上传
      const files = pendingFiles.map(file => file.raw!).filter(Boolean)
      
      // 设置所有文件为上传中状态
      pendingFiles.forEach(file => {
        file.status = 'uploading'
        file.percentage = 0
      })
      
      // 模拟批量上传进度
      const progressInterval = setInterval(() => {
        pendingFiles.forEach(file => {
          if (file.percentage! < 90) {
            file.percentage! += Math.random() * 20
          }
        })
      }, 300)
      
      const response = await fileApi.batchUpload(files)
      
      clearInterval(progressInterval)
      
      // 更新文件状态
      pendingFiles.forEach((file, index) => {
        file.status = 'success'
        file.percentage = 100
      })
      
      // 添加到上传历史
      uploadedFiles.value.unshift(...response.data)
      
      ElMessage.success(`成功上传 ${files.length} 个文件`)
    } else {
      // 单文件上传
      await handleUploadSingle(pendingFiles[0], 0)
    }
  } catch (error) {
    // 设置失败状态
    pendingFiles.forEach(file => {
      file.status = 'fail'
      file.percentage = 0
    })
    console.error('批量上传失败:', error)
    ElMessage.error('批量上传失败')
  } finally {
    uploading.value = false
  }
}

const handleViewFile = async (file: UploadFile) => {
  try {
    // 如果有文件ID，获取详细信息
    const fileData = uploadedFiles.value.find(f => f.originalName === file.name)
    if (fileData) {
      const response = await fileApi.findById(fileData.id)
      
      await ElMessageBox.alert(
        `
        <p><strong>文件名：</strong>${response.data.originalName}</p>
        <p><strong>存储名：</strong>${response.data.storageName}</p>
        <p><strong>文件大小：</strong>${formatFileSize(response.data.size)}</p>
        <p><strong>MIME类型：</strong>${response.data.mimeType}</p>
        <p><strong>存储类型：</strong>${response.data.storageType}</p>
        <p><strong>上传时间：</strong>${formatDateTime(response.data.createdTime)}</p>
        `,
        '文件详情',
        {
          dangerouslyUseHTMLString: true
        }
      )
    }
  } catch (error) {
    console.error('获取文件详情失败:', error)
  }
}

const handleDownload = async (file: FileInfoDetailDTO) => {
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

const handleCopyUrl = async (file: FileInfoDetailDTO) => {
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

// 生命周期
onMounted(() => {
  // 可以在这里加载最近上传的文件历史
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-area {
  margin-bottom: 20px;
}

.upload-dragger {
  width: 100%;
}

.upload-actions {
  margin: 20px 0;
  text-align: center;
}

.upload-actions .el-button {
  margin: 0 10px;
}

.file-list,
.upload-history {
  margin-top: 20px;
}

.el-upload__tip {
  color: #606266;
  font-size: 12px;
  margin-top: 7px;
}
</style> 