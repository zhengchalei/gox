<template>
  <div class="file-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文件管理</span>
          <div class="header-actions">
            <!-- 视图模式切换 -->
            <el-radio-group
              v-model="viewMode"
              size="small"
              style="margin-right: 16px"
              @change="handleViewModeChange"
            >
              <el-radio-button value="table">
                <el-icon><List /></el-icon>
                表格视图
              </el-radio-button>
              <el-radio-button value="grid">
                <el-icon><Grid /></el-icon>
                网格视图
              </el-radio-button>
            </el-radio-group>

            <el-button
              type="danger"
              :disabled="selectedFiles.length === 0"
              @click="handleBatchDelete"
            >
              <el-icon><Delete /></el-icon>
              批量删除 ({{ selectedFiles.length }})
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" :inline="true" @submit.prevent>
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
            <el-select
              v-model="searchForm.storageType"
              placeholder="请选择存储类型"
              clearable
              style="width: 120px"
            >
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

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" class="table-view">
        <el-divider content-position="left">文件列表</el-divider>
        <div v-loading="loading">
          <el-table
            :data="tableData"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            row-key="id"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column
              prop="originalName"
              label="文件名"
              min-width="200"
              show-overflow-tooltip
            >
              <template #default="{ row }">
                <div class="file-name-cell">
                  <el-icon class="file-icon" :size="20">
                    <Picture v-if="isImage(row.mimeType)" />
                    <Document v-else-if="isDocument(row.mimeType)" />
                    <VideoPlay v-else-if="isVideo(row.mimeType)" />
                    <Headset v-else-if="isAudio(row.mimeType)" />
                    <Files v-else />
                  </el-icon>
                  <span>{{ row.originalName }}</span>
                </div>
              </template>
            </el-table-column>
            <!-- fileKey -->
            <el-table-column
              prop="fileKey"
              label="文件Key"
              min-width="120"
              show-overflow-tooltip
            >
              <template #default="{ row }">
                {{ row.fileKey }}
              </template>
            </el-table-column>
            <el-table-column prop="size" label="文件大小" width="120" sortable>
              <template #default="{ row }">
                {{ formatFileSize(row.size) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="mimeType"
              label="文件类型"
              width="180"
              show-overflow-tooltip
            />
            <el-table-column prop="storageType" label="存储类型" width="100">
              <template #default="{ row }">
                <el-tag
                  size="small"
                  :type="getStorageTypeColor(row.storageType)"
                >
                  {{ getStorageTypeName(row.storageType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="createdTime"
              label="上传时间"
              width="180"
              sortable
            >
              <template #default="{ row }">
                {{ formatDateTime(row.createdTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="300" fixed="right">
              <template #default="{ row }">
                <el-button-group size="small">
                  <el-button @click.stop="handleCopyDownloadUrl(row)"
                    >下载地址</el-button
                  >
                  <el-button @click="handlePreview(row)">预览</el-button>
                  <el-button @click="handleDownload(row)">下载</el-button>
                  <el-button @click="handleDelete(row)" type="danger"
                    >删除</el-button
                  >
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 空状态 -->
        <el-empty
          v-if="!loading && tableData.length === 0"
          description="暂无文件"
        />
      </div>

      <!-- 网格视图 -->
      <div v-else class="grid-view">
        <el-divider content-position="left">文件预览</el-divider>

        <div v-loading="loading" class="file-grid">
          <div
            v-for="file in tableData"
            :key="file.id"
            class="file-card"
            @click="handleFileSelect(file)"
            :class="{ selected: selectedFiles.some((f) => f.id === file.id) }"
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
              <div class="file-name" :title="file.originalName">
                {{ file.originalName }}
              </div>
              <div class="file-meta">
                <span>{{ formatFileSize(file.size) }}</span>
                <el-tag
                  size="small"
                  :type="getStorageTypeColor(file.storageType)"
                >
                  {{ getStorageTypeName(file.storageType) }}
                </el-tag>
              </div>
              <div class="file-actions">
                <el-button-group size="small">
                  <el-button @click.stop="handlePreview(file)">预览</el-button>
                  <el-button @click.stop="handleDownload(file)">下载</el-button>
                  <el-button @click.stop="handleDelete(file)" type="danger"
                    >删除</el-button
                  >
                </el-button-group>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty
          v-if="!loading && tableData.length === 0"
          description="暂无文件"
        />
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="tableData.length > 0">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="
            viewMode === 'table' ? [10, 20, 50, 100] : [12, 24, 48, 96]
          "
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 上传区域 -->
      <div class="upload-section">
        <el-divider content-position="left">文件上传</el-divider>
        <el-upload
          ref="uploadRef"
          class="upload-dragger"
          drag
          :multiple="true"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="uploadFileList"
          accept="*"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击选择文件</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持任意格式文件，支持多文件选择和拖拽上传，添加文件后自动上传
            </div>
          </template>
        </el-upload>

        <!-- 上传文件列表 -->
        <div class="upload-file-list" v-if="uploadFileList.length > 0">
          <el-table
            :data="uploadFileList"
            style="width: 100%; margin-top: 15px"
          >
            <el-table-column
              prop="name"
              label="文件名"
              min-width="200"
              show-overflow-tooltip
            />
            <el-table-column prop="size" label="文件大小" width="120">
              <template #default="{ row }">
                {{ formatFileSize(row.size) }}
              </template>
            </el-table-column>
            <el-table-column label="上传状态" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'ready'" type="info"
                  >待上传</el-tag
                >
                <el-tag v-else-if="row.status === 'uploading'" type="warning"
                  >上传中</el-tag
                >
                <el-tag v-else-if="row.status === 'success'" type="success"
                  >已上传</el-tag
                >
                <el-tag v-else-if="row.status === 'fail'" type="danger"
                  >上传失败</el-tag
                >
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
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row, $index }">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleRemoveUploadFile($index)"
                  :disabled="row.status === 'uploading'"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
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
          <img
            :src="getPreviewUrl(previewFile)"
            :alt="previewFile.originalName"
          />
        </div>

        <!-- 视频预览 -->
        <div v-else-if="isVideo(previewFile.mimeType)" class="video-preview">
          <video
            controls
            :src="getPreviewUrl(previewFile)"
            style="max-width: 100%; max-height: 500px"
          >
            您的浏览器不支持视频播放。
          </video>
        </div>

        <!-- 音频预览 -->
        <div v-else-if="isAudio(previewFile.mimeType)" class="audio-preview">
          <audio controls :src="getPreviewUrl(previewFile)" style="width: 100%">
            您的浏览器不支持音频播放。
          </audio>
        </div>

        <!-- 文本文件预览 -->
        <div v-else-if="isText(previewFile.mimeType)" class="text-preview">
          <el-input
            type="textarea"
            :model-value="textContent"
            readonly
            :rows="20"
            style="width: 100%"
          />
        </div>

        <!-- 其他文件类型 -->
        <div v-else class="other-preview">
          <el-empty description="此文件类型不支持预览">
            <el-button
              type="primary"
              @click="previewFile && handleDownload(previewFile)"
            >
              下载查看
            </el-button>
          </el-empty>
        </div>

        <!-- 文件信息 -->
        <el-divider />
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">{{
            previewFile.originalName
          }}</el-descriptions-item>
          <el-descriptions-item label="文件Key">{{
            previewFile.fileKey
          }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{
            formatFileSize(previewFile.size)
          }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{
            previewFile.mimeType
          }}</el-descriptions-item>
          <el-descriptions-item label="存储类型">{{
            getStorageTypeName(previewFile.storageType)
          }}</el-descriptions-item>
          <el-descriptions-item label="上传时间" span="2">{{
            formatDateTime(previewFile.createdTime)
          }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type UploadFile,
  type UploadFiles,
} from "element-plus";
import {
  Delete,
  Document,
  Files,
  Grid,
  Headset,
  List,
  Picture,
  Refresh,
  Search,
  UploadFilled,
  VideoPlay,
} from "@element-plus/icons-vue";
import { fileApi, type FileInfoListDTO, type FileInfoSpecification } from "../../api/file/file";

// 响应式数据
const loading = ref(false);
const tableData = ref<FileInfoListDTO[]>([]);
const selectedFiles = ref<FileInfoListDTO[]>([]);
const previewVisible = ref(false);
const previewFile = ref<FileInfoListDTO | null>(null);
const textContent = ref("");

// 上传相关
const uploadRef = ref();
const uploadFileList = ref<UploadFile[]>([]);

// 搜索表单
const searchForm = reactive<FileInfoSpecification>({
  originalName: "",
  mimeType: "",
  storageType: undefined,
});

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20, // 默认为表格模式的页面大小
  total: 0,
});

// 视图模式
const viewMode = ref<"table" | "grid">("table");

// 当视图模式改变时，调整页面大小
const handleViewModeChange = () => {
  if (viewMode.value === "table") {
    pagination.pageSize = 20;
  } else {
    pagination.pageSize = 24;
  }
  pagination.currentPage = 1;
  fetchFiles();
};

// 方法
const formatFileSize = (size: number): string => {
  if (size === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(size) / Math.log(k));
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

const formatDateTime = (dateTime: string) => {
  return new Date(dateTime).toLocaleString("zh-CN");
};

const isImage = (mimeType: string) => mimeType.startsWith("image/");
const isVideo = (mimeType: string) => mimeType.startsWith("video/");
const isAudio = (mimeType: string) => mimeType.startsWith("audio/");
const isDocument = (mimeType: string) =>
  mimeType.includes("pdf") ||
  mimeType.includes("document") ||
  mimeType.includes("text") ||
  mimeType.includes("sheet") ||
  mimeType.includes("presentation");
const isText = (mimeType: string) => mimeType.startsWith("text/");

const getStorageTypeColor = (storageType: string) => {
  const colorMap: Record<string, string> = {
    LOCAL: "info",
    ALIYUN: "primary",
    TENCENT: "success",
    MINIO: "warning",
  };
  return colorMap[storageType] || "info";
};

const getStorageTypeName = (storageType: string) => {
  const nameMap: Record<string, string> = {
    LOCAL: "本地",
    ALIYUN: "阿里云",
    TENCENT: "腾讯云",
    MINIO: "MinIO",
  };
  return nameMap[storageType] || storageType;
};

const getPreviewUrl = (file: FileInfoListDTO) => {
  console.log("file", JSON.stringify(file));
  // 这里需要根据实际的API来构建预览URL
  return `/api/file/preview/${file.fileKey}`;
};

// 表格视图的选择处理
const handleSelectionChange = (selection: FileInfoListDTO[]) => {
  selectedFiles.value = selection;
};

const fetchFiles = async () => {
  try {
    loading.value = true;
    console.log("正在获取文件列表...", {
      currentPage: pagination.currentPage,
      pageSize: pagination.pageSize,
      searchForm: searchForm,
    });

    const response = await fileApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );
    console.log("文件列表响应:", response);

    tableData.value = response.data.content;
    pagination.total = response.data.totalElements;

    console.log("文件数据:", tableData.value);
  } catch (error) {
    console.error("获取文件列表失败:", error);
    ElMessage.error("获取文件列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchFiles();
};

const handleReset = () => {
  Object.assign(searchForm, {
    originalName: "",
    mimeType: "",
    storageType: undefined,
  });
  handleSearch();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchFiles();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchFiles();
};

const handleFileSelect = (file: FileInfoListDTO) => {
  const index = selectedFiles.value.findIndex((f) => f.id === file.id);
  if (index > -1) {
    selectedFiles.value.splice(index, 1);
  } else {
    selectedFiles.value.push(file);
  }
};

const handlePreview = async (file: FileInfoListDTO) => {
  previewFile.value = file;

  // 如果是文本文件，加载内容
  if (isText(file.mimeType)) {
    try {
      const blob = await fileApi.download(file.fileKey);
      const text = await blob.text();
      textContent.value = text;
    } catch (error) {
      console.error("加载文本内容失败:", error);
      textContent.value = "加载失败";
    }
  }

  previewVisible.value = true;
};

const handlePreviewClose = () => {
  previewVisible.value = false;
  previewFile.value = null;
  textContent.value = "";
};

const handleDownload = async (file: FileInfoListDTO) => {
  try {
    const blob = await fileApi.download(file.fileKey);

    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = file.originalName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    ElMessage.success("文件下载成功");
  } catch (error) {
    console.error("文件下载失败:", error);
    ElMessage.error("文件下载失败");
  }
};

const handleCopyDownloadUrl = (file: FileInfoListDTO) => {
  navigator.clipboard.writeText(file.downloadUrl);
  ElMessage.success("下载地址已复制到剪贴板");
};

const handleDelete = async (file: FileInfoListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.originalName}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await fileApi.deleteById(file.id);
    ElMessage.success("删除成功");
    fetchFiles();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除文件失败:", error);
      ElMessage.error("删除文件失败");
    }
  }
};

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedFiles.value.length} 个文件吗？`,
      "批量删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const ids = selectedFiles.value.map((f) => f.id);
    await fileApi.batchDelete(ids);

    ElMessage.success(`成功删除 ${ids.length} 个文件`);
    selectedFiles.value = [];
    fetchFiles();
  } catch (error) {
    if (error !== "cancel") {
      console.error("批量删除失败:", error);
      ElMessage.error("批量删除失败");
    }
  }
};

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.style.display = "none";
  // 显示默认图标
  const parent = img.parentElement;
  if (parent) {
    parent.innerHTML =
      '<el-icon class="file-type-icon" size="60"><Picture /></el-icon>';
  }
};

// 上传相关方法
const handleFileChange = (file: UploadFile, fileList: UploadFiles) => {
  file.status = "ready";
  file.percentage = 0;

  // 自动上传
  nextTick(() => {
    handleUploadSingle(file);
  });
};

const handleFileRemove = (file: UploadFile, fileList: UploadFiles) => {
  // Element Plus 会自动处理文件列表更新
};

const handleRemoveUploadFile = (index: number) => {
  uploadFileList.value.splice(index, 1);
};

const handleUploadSingle = async (file: UploadFile) => {
  if (!file.raw) return;

  try {
    file.status = "uploading";
    file.percentage = 0;

    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (file.percentage! < 90) {
        file.percentage! += Math.random() * 30;
      }
    }, 200);

    const response = await fileApi.upload(file.raw);

    clearInterval(progressInterval);
    file.status = "success";
    file.percentage = 100;

    ElMessage.success(`文件 "${file.name}" 上传成功`);

    // 上传成功后刷新文件列表
    setTimeout(() => {
      fetchFiles();
      // 移除成功的文件
      const index = uploadFileList.value.findIndex((f) => f.uid === file.uid);
      if (index > -1) {
        uploadFileList.value.splice(index, 1);
      }
    }, 1000);
  } catch (error) {
    file.status = "fail";
    file.percentage = 0;
    console.error("文件上传失败:", error);
    ElMessage.error(`文件 "${file.name}" 上传失败`);
  }
};

// 生命周期
onMounted(() => {
  console.log("文件管理页面已挂载，开始获取数据");
  fetchFiles();
});

// 监听视图模式变化
watch(viewMode, handleViewModeChange);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions .el-button {
  margin-left: 10px;
}

.header-actions .el-radio-group {
  margin-right: 16px;
}

.search-bar {
  margin-bottom: 20px;
}

.table-view {
  margin: 20px 0;
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  color: #606266;
  flex-shrink: 0;
}

.grid-view {
  margin: 20px 0;
}

.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.file-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
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
  background-color: #f0f8ff;
}

.file-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
  margin-bottom: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.file-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.file-type-icon {
  color: #909399;
}

.file-info {
  text-align: center;
}

.file-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.file-actions {
  display: flex;
  justify-content: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.upload-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.upload-dragger {
  width: 100%;
}

.upload-file-list {
  margin-top: 15px;
}

.preview-content {
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 500px;
  border-radius: 4px;
}

.video-preview,
.audio-preview {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.text-preview {
  text-align: left;
  margin: 20px 0;
}

.other-preview {
  margin: 40px 0;
}

.el-upload__tip {
  color: #606266;
  font-size: 12px;
  margin-top: 7px;
}

.el-divider {
  margin: 20px 0;
}
</style> 