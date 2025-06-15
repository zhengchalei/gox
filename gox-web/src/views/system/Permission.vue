<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增权限
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="权限名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入权限名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入权限编码"
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
        <el-table-column prop="name" label="权限名称" min-width="120" />
        <el-table-column prop="code" label="权限编码" min-width="120" />
        <el-table-column
          prop="description"
          label="描述"
          min-width="150"
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
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入权限编码"
            :disabled="isEdit && formData.id"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入权限描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="关联角色" prop="roleIds">
          <el-select
            v-model="formData.roleIds"
            multiple
            placeholder="请选择关联角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
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
          <el-descriptions-item label="权限ID">{{
            viewData.id
          }}</el-descriptions-item>
          <el-descriptions-item label="权限名称">{{
            viewData.name
          }}</el-descriptions-item>
          <el-descriptions-item label="权限编码">{{
            viewData.code
          }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{
            formatDateTime(viewData.createdTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{
            formatDateTime(viewData.updatedTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="描述" span="2">
            {{ viewData.description || "暂无描述" }}
          </el-descriptions-item>
          <el-descriptions-item label="关联角色" span="2">
            <el-tag
              v-for="role in viewData.roles"
              :key="role.id"
              style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ role.name }}
            </el-tag>
            <span v-if="!viewData.roles.length" style="color: #999"
              >暂无关联角色</span
            >
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import { permissionApi } from "../../api/system/permission";
import type {
  PermissionCreateDTO,
  PermissionDetailDTO,
  PermissionListDTO,
  PermissionSpecification,
  PermissionUpdateDTO,
} from "../../api/system/permission";
import { roleApi, type RoleListDTO } from "../../api/system/role";
import { formatDateTime } from "../../utils/dateUtil";

// 响应式数据
const loading = ref(false);
const tableData = ref<PermissionListDTO[]>([]);
const selectedPermissions = ref<PermissionListDTO[]>([]);

// 搜索表单
const searchForm = reactive<PermissionSpecification>({
  name: "",
  code: "",
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
const dialogTitle = computed(() => (isEdit.value ? "编辑权限" : "新增权限"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<PermissionCreateDTO & Partial<PermissionUpdateDTO>>({
  name: "",
  code: "",
  description: "",
  roleIds: [],
});
const formRules = {
  name: [
    { required: true, message: "请输入权限名称", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "权限名称长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "请输入权限编码", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "权限编码长度在 2 到 50 个字符",
      trigger: "blur",
    },
    {
      pattern: /^[A-Z_:]+$/,
      message: "权限编码只能包含大写字母、下划线和冒号",
      trigger: "blur",
    },
  ],
  roleIds: [{ required: true, message: "请选择关联角色", trigger: "blur" }],
};

// 查看详情数据
const viewData = ref<PermissionDetailDTO | null>(null);

// 角色选项
const roleOptions = ref<RoleListDTO[]>([]);

const fetchPermissions = async () => {
  try {
    loading.value = true;
    const response = await permissionApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.pageable.totalElements;
  } catch (error) {
    console.error("获取权限列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const fetchRoles = async () => {
  try {
    const response = await roleApi.findPage(1, 1000, {});
    roleOptions.value = response.data.content;
  } catch (error) {
    console.error("获取角色列表失败:", error);
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchPermissions();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: "",
    code: "",
  });
  handleSearch();
};

const handleSelectionChange = (selection: PermissionListDTO[]) => {
  selectedPermissions.value = selection;
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchPermissions();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchPermissions();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    name: "",
    code: "",
    description: "",
    roleIds: [],
  });
  dialogVisible.value = true;
};

const handleEdit = (row: PermissionListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    description: row.description,
    roleIds: [...row.roleIds],
  });
  dialogVisible.value = true;
};

const handleView = async (row: PermissionListDTO) => {
  try {
    const response = await permissionApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取权限详情失败:", error);
  }
};

const handleDelete = async (row: PermissionListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限 "${row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await permissionApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchPermissions();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除权限失败:", error);
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
      await permissionApi.update(formData as PermissionUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await permissionApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchPermissions();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchPermissions();
  fetchRoles();
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
