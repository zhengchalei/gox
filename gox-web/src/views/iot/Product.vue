<template>
  <div class="permission-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>产品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增产品
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">

        <el-form-item label="产品编号">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入产品编号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入产品名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="产品描述">
          <el-input
            v-model="searchForm.description"
            placeholder="请输入产品描述"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="产品状态">
          <el-input
            v-model="searchForm.status"
            placeholder="请输入产品状态"
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
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column prop="code" label="产品编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="name" label="产品名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="description" label="产品描述" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="产品状态" min-width="120" show-overflow-tooltip />

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
        <el-form-item label="产品编号" prop="code">
          <el-input v-model="formData.code" placeholder="请输入产品编号" />
        </el-form-item>
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="产品描述" prop="description">
          <el-input v-model="formData.description" placeholder="请输入产品描述" />
        </el-form-item>
        <el-form-item label="产品状态" prop="status">
          <el-input v-model="formData.status" placeholder="请输入产品状态" />
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
          <el-descriptions-item label="产品编号">
            {{ viewData.code }}
          </el-descriptions-item>
          <el-descriptions-item label="产品名称">
            {{ viewData.name }}
          </el-descriptions-item>
          <el-descriptions-item label="产品描述">
            {{ viewData.description }}
          </el-descriptions-item>
          <el-descriptions-item label="产品状态">
            {{ viewData.status }}
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
import { ElMessage, ElMessageBox, FormInstance } from "element-plus";
import {
  productApi,
  type ProductCreateDTO,
  type ProductDetailDTO,
  type ProductListDTO,
  type ProductSpecification,
  type ProductUpdateDTO,
} from "../../api/iot/product";

// 响应式数据
const loading = ref(false);
const tableData = ref<ProductListDTO[]>([]);

// 搜索表单
const searchForm = reactive<ProductSpecification>({
  code:  '',
  name:  '',
  description:  '',
  status:  true,
});

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
const dialogTitle = computed(() => (isEdit.value ? "编辑产品" : "新增产品"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<ProductCreateDTO & Partial<ProductUpdateDTO>>({
  code: '',
  name: '',
  description: '',
  status: true,
});
const formRules = {
  code: [{ required: true, message: "请输入产品编号", trigger: "blur" }],
  name: [{ required: true, message: "请输入产品名称", trigger: "blur" }],
  description: [{ required: true, message: "请输入产品描述", trigger: "blur" }],
  status: [{ required: true, message: "请输入产品状态", trigger: "blur" }],
};

// 查看详情数据
const viewData = ref<ProductDetailDTO | null>(null);

// 方法
const formatDateTime = (dateTime: string) => {
  return new Date(dateTime).toLocaleString("zh-CN");
};

const fetchProduct = async () => {
  try {
    loading.value = true;
    const response = await productApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.totalElements;
  } catch (error) {
    console.error("获取Product列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchProduct();
};

const handleReset = () => {
  Object.assign(searchForm, {
    code: "",
    name: "",
    description: "",
    status: "",
  });
  handleSearch();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchProduct();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchProduct();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    code: "",
    name: "",
    description: "",
    status: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row: ProductListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    code: row.code,
    name: row.name,
    description: row.description,
    status: row.status,
  });
  dialogVisible.value = true;
};

const handleView = async (row: ProductListDTO) => {
  try {
    const response = await productApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取Product详情失败:", error);
  }
};

const handleDelete = async (row: ProductListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除产品 "$\{row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await productApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchProduct();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除Product失败:", error);
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
      await productApi.update(formData as ProductUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await productApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchProduct();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchProduct();
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
  justify-content: center;
}

.detail-content {
  padding: 20px 0;
}
</style> 