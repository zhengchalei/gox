<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增设备
          </el-button>
        </div>
      </template>
      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="设备编号">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入设备编号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入设备名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="设备描述">
          <el-input
            v-model="searchForm.description"
            placeholder="请输入设备描述"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="设备状态">
          <el-input
            v-model="searchForm.online"
            placeholder="请输入设备状态"
            clearable
            @keyup.enter="handleSearch"
          />
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

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column
          prop="code"
          label="设备编号"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="name"
          label="设备名称"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="description"
          label="设备描述"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="online"
          label="设备状态"
          min-width="120"
          show-overflow-tooltip
        />

        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.updatedTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
        @submit.prevent
      >
        <el-form-item label="设备编号" prop="code">
          <el-input v-model="formData.code" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备描述" prop="description">
          <el-input
            v-model="formData.description"
            placeholder="请输入设备描述"
          />
        </el-form-item>
        <el-form-item label="设备状态" prop="online">
          <el-input v-model="formData.online" placeholder="请输入设备状态" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="权限详情" v-model="viewDialogVisible" width="600px">
      <div v-if="viewData" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备编号">
            {{ viewData.code }}
          </el-descriptions-item>
          <el-descriptions-item label="设备名称">
            {{ viewData.name }}
          </el-descriptions-item>
          <el-descriptions-item label="设备描述">
            {{ viewData.description }}
          </el-descriptions-item>
          <el-descriptions-item label="设备状态">
            {{ viewData.online }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{
            formatDateTime(viewData.createdTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{
            formatDateTime(viewData.modifiedTime)
          }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import {
  deviceApi,
  type DeviceCreateDTO,
  type DeviceDetailDTO,
  type DeviceListDTO,
  type DeviceSpecification,
  type DeviceUpdateDTO,
} from "../../api/iot/device";
import { formatDateTime } from "../../utils/dateUtil";

// 响应式数据
const loading = ref(false);
const tableData = ref<DeviceListDTO[]>([]);

// 搜索表单
const searchForm = reactive<DeviceSpecification>({});

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

// 对话框
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const dialogTitle = computed(() => (isEdit.value ? "编辑设备" : "新增设备"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<DeviceCreateDTO & Partial<DeviceUpdateDTO>>({
  code: "",
  name: "",
  description: "",
  online: true,
});
const formRules = {
  code: [{ required: true, message: "请输入设备编号", trigger: "blur" }],
  name: [{ required: true, message: "请输入设备名称", trigger: "blur" }],
  description: [{ required: true, message: "请输入设备描述", trigger: "blur" }],
  online: [{ required: true, message: "请输入设备状态", trigger: "blur" }],
};

// 查看详情数据
const viewData = ref<DeviceDetailDTO | null>(null);

const fetchDevice = async () => {
  try {
    loading.value = true;
    const response = await deviceApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.page.totalElements;
  } catch (error) {
    console.error("获取Device列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchDevice();
};

const handleReset = () => {
  Object.assign(searchForm, {
    code: "",
    name: "",
    description: "",
    online: "",
  });
  handleSearch();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchDevice();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchDevice();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    code: "",
    name: "",
    description: "",
    online: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row: DeviceListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    code: row.code,
    name: row.name,
    description: row.description,
    online: row.online,
  });
  dialogVisible.value = true;
};

const handleView = async (row: DeviceListDTO) => {
  try {
    const response = await deviceApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取Device详情失败:", error);
  }
};

const handleDelete = async (row: DeviceListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除设备 "$\{row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deviceApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchDevice();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除Device失败:", error);
    }
  }
};

const handleDialogClose = () => {
  dialogVisible.value = false;
  formRef.value?.clearValidate();
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    if (isEdit.value) {
      await deviceApi.update(formData as DeviceUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await deviceApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchDevice();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchDevice();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: right;
}

.detail-content {
  padding: 20px 0;
}
</style>
