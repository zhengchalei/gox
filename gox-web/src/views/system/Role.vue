<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入角色名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入角色编码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.enabled"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
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

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" min-width="120" />
        <el-table-column prop="code" label="角色编码" min-width="120" />
        <el-table-column
          prop="description"
          label="描述"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" @click="handleAssignPermission(row)">
              分配权限
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
          :current-page="pagination.currentPage"
          :page-size="pagination.pageSize"
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
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入角色编码"
            :disabled="isEdit && formData.id"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入角色描述"
            :rows="3"
          />
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

    <!-- 分配权限对话框 -->
    <el-dialog
      title="分配权限"
      v-model="assignPermissionDialogVisible"
      width="600px"
      :before-close="handleAssignDialogClose"
    >
      <div v-if="assignRoleData">
        <p><strong>角色名称：</strong>{{ assignRoleData.name }}</p>
        <p><strong>角色编码：</strong>{{ assignRoleData.code }}</p>
        <el-divider />
        <p><strong>选择权限：</strong></p>
        <el-tree
          ref="assignPermissionTreeRef"
          :data="permissionTreeData"
          show-checkbox
          node-key="id"
          :props="{ children: 'children', label: 'name' }"
          :default-checked-keys="assignRoleData.permissionIds"
          @check="handleAssignPermissionCheck"
        />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleAssignDialogClose">取消</el-button>
          <el-button
            type="primary"
            :loading="assignLoading"
            @click="handleAssignSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="角色详情" v-model="viewDialogVisible" width="600px">
      <div v-if="viewData" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="角色ID">{{
            viewData.id
          }}</el-descriptions-item>
          <el-descriptions-item label="角色名称">{{
            viewData.name
          }}</el-descriptions-item>
          <el-descriptions-item label="角色编码">{{
            viewData.code
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="viewData.enabled ? 'success' : 'danger'">
              {{ viewData.enabled ? "启用" : "禁用" }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{
            formatDateTime(viewData.createdTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{
            formatDateTime(viewData.updatedTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="描述" span="2">
            {{ viewData.description || "暂无描述" }}
          </el-descriptions-item>
          <el-descriptions-item label="权限" span="2">
            <el-tag
              v-for="permission in viewData.permissions"
              :key="permission.id"
              style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ permission.name }}
            </el-tag>
            <span v-if="!viewData.permissions.length" style="color: #999"
              >暂无权限</span
            >
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  ElTree,
} from "element-plus";
import { Plus, Search, Refresh } from "@element-plus/icons-vue";
import {
  roleApi,
  type RoleCreateDTO,
  type RoleDetailDTO,
  type RoleListDTO,
  type RolePermissionUpdateDTO,
  type RoleSpecification,
  type RoleUpdateDTO,
} from "../../api/system/role.ts";
import {
  permissionApi,
  type PermissionListDTO,
} from "../../api/system/permission.ts";
import { formatDateTime } from "../../utils/dateUtil.ts";

// 响应式数据
const loading = ref(false);
const tableData = ref<RoleListDTO[]>([]);
const selectedRoles = ref<RoleListDTO[]>([]);

// 搜索表单
const searchForm = reactive<RoleSpecification>({
  name: "",
  code: "",
  enabled: undefined,
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
const assignPermissionDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const assignLoading = ref(false);
const dialogTitle = computed(() => (isEdit.value ? "编辑角色" : "新增角色"));

// 表单
const formRef = ref<FormInstance>();
const permissionTreeRef = ref<InstanceType<typeof ElTree>>();
const assignPermissionTreeRef = ref<InstanceType<typeof ElTree>>();
const formData = reactive<RoleCreateDTO & Partial<RoleUpdateDTO>>({
  name: "",
  code: "",
  description: "",
});
const formRules = {
  name: [
    { required: true, message: "请输入角色名称", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "角色名称长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "请输入角色编码", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "角色编码长度在 2 到 50 个字符",
      trigger: "blur",
    },
    {
      pattern: /^[A-Z_]+$/,
      message: "角色编码只能包含大写字母和下划线",
      trigger: "blur",
    },
  ],
};

// 查看详情数据
const viewData = ref<RoleDetailDTO | null>(null);

// 分配权限数据
const assignRoleData = ref<RoleListDTO | null>(null);
const assignPermissionIds = ref<number[]>([]);

// 权限树数据
const permissionTreeData = ref<PermissionListDTO[]>([]);

const fetchRoles = async () => {
  try {
    loading.value = true;
    const response = await roleApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.pageable.totalElements;
  } catch (error) {
    console.error("获取角色列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const fetchPermissions = async () => {
  try {
    const response = await permissionApi.findPage(1, 1000, {});
    permissionTreeData.value = response.data.content;
  } catch (error) {
    console.error("获取权限列表失败:", error);
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchRoles();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: "",
    code: "",
    enabled: undefined,
  });
  handleSearch();
};

const handleSelectionChange = (selection: RoleListDTO[]) => {
  selectedRoles.value = selection;
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchRoles();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchRoles();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    name: "",
    code: "",
    description: "",
  });
  dialogVisible.value = true;
  // 清空权限树选择
  setTimeout(() => {
    permissionTreeRef.value?.setCheckedKeys([]);
  }, 100);
};

const handleEdit = (row: RoleListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    description: row.description,
    permissionIds: [...row.permissionIds],
  });
  dialogVisible.value = true;
  // 设置权限树选中状态
  setTimeout(() => {
    permissionTreeRef.value?.setCheckedKeys(row.permissionIds);
  }, 100);
};

const handleView = async (row: RoleListDTO) => {
  try {
    const response = await roleApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取角色详情失败:", error);
  }
};

const handleAssignPermission = (row: RoleListDTO) => {
  assignRoleData.value = row;
  assignPermissionIds.value = [...row.permissionIds];
  assignPermissionDialogVisible.value = true;
  // 设置权限树选中状态
  setTimeout(() => {
    assignPermissionTreeRef.value?.setCheckedKeys(row.permissionIds);
  }, 100);
};

const handleDelete = async (row: RoleListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 "${row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await roleApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchRoles();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除角色失败:", error);
    }
  }
};

const handleAssignPermissionCheck = () => {
  if (assignPermissionTreeRef.value) {
    const checkedKeys =
      assignPermissionTreeRef.value.getCheckedKeys() as number[];
    const halfCheckedKeys =
      assignPermissionTreeRef.value.getHalfCheckedKeys() as number[];
    assignPermissionIds.value = [...checkedKeys, ...halfCheckedKeys];
  }
};

const handleDialogClose = () => {
  dialogVisible.value = false;
  formRef.value?.clearValidate();
};

const handleAssignDialogClose = () => {
  assignPermissionDialogVisible.value = false;
  assignRoleData.value = null;
  assignPermissionIds.value = [];
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    if (isEdit.value) {
      await roleApi.update(formData as RoleUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await roleApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchRoles();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const handleAssignSubmit = async () => {
  if (!assignRoleData.value) return;

  try {
    // 获取最新的权限选择
    handleAssignPermissionCheck();

    assignLoading.value = true;

    const updateData: RolePermissionUpdateDTO = {
      id: assignRoleData.value.id,
      permissionIds: assignPermissionIds.value,
    };

    await roleApi.assignRolePermission(updateData);
    ElMessage.success("分配权限成功");

    assignPermissionDialogVisible.value = false;
    fetchRoles();
  } catch (error) {
    console.error("分配权限失败:", error);
    ElMessage.error("分配权限失败");
  } finally {
    assignLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchRoles();
  fetchPermissions();
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
