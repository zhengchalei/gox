<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input
            v-model="searchForm.nickname"
            placeholder="请输入昵称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
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
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column
          prop="email"
          label="邮箱"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="enabled" label="状态" min-width="100">
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
        <el-table-column prop="updatedTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.updatedTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="360" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" @click="handleAssignRole(row)">
              分配角色
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
      width="500px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
        @submit.prevent
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            :disabled="isEdit && formData.id"
          />
        </el-form-item>
        <el-form-item v-if="isEdit" label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            type="email"
          />
        </el-form-item>
        <el-form-item v-if="isEdit" label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态" prop="enabled">
          <el-switch
            v-model="formData.enabled"
            active-text="启用"
            inactive-text="禁用"
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

    <!-- 分配角色对话框 -->
    <el-dialog
      title="分配角色"
      v-model="assignRoleDialogVisible"
      width="600px"
      :before-close="handleAssignDialogClose"
    >
      <div v-if="assignUserData">
        <p><strong>用户名：</strong>{{ assignUserData.username }}</p>
        <p><strong>昵称：</strong>{{ assignUserData.nickname || "暂无" }}</p>
        <el-divider />
        <p><strong>选择角色：</strong></p>
        <el-checkbox-group v-model="assignRoleIds">
          <el-card
            v-for="role in roleList"
            :key="role.id"
            class="role-card"
            :class="{ 'role-card-selected': assignRoleIds.includes(role.id) }"
          >
            <el-checkbox :label="role.id" :value="role.id">
              <div class="role-info">
                <div class="role-name">{{ role.name }}</div>
                <div class="role-code">{{ role.code }}</div>
                <div class="role-description">
                  {{ role.description || "暂无描述" }}
                </div>
              </div>
            </el-checkbox>
          </el-card>
        </el-checkbox-group>
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
    <el-dialog title="用户详情" v-model="viewDialogVisible" width="600px">
      <div v-if="viewData" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{
            viewData.id
          }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{
            viewData.username
          }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{
            viewData.nickname || "暂无"
          }}</el-descriptions-item>
          <el-descriptions-item label="头像">
            <el-avatar
              v-if="viewData.avatar"
              :src="viewData.avatar"
              size="small"
            />
            <span v-else style="color: #999">暂无头像</span>
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">{{
            viewData.email || "暂无"
          }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{
            viewData.phone || "暂无"
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
          <el-descriptions-item label="角色" span="2">
            <el-tag
              v-for="role in viewData.roles"
              :key="role.id"
              style="margin-right: 8px"
            >
              {{ role.name }}
            </el-tag>
            <span v-if="!viewData.roles.length" style="color: #999"
              >暂无角色</span
            >
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { Plus, Search, Refresh } from "@element-plus/icons-vue";
import {
  userApi,
  type UserCreateDTO,
  type UserDetailDTO,
  type UserListDTO,
  type UserRoleUpdateDTO,
  type UserSpecification,
  type UserUpdateDTO,
} from "../../api/system/user.ts";
import { roleApi, type RoleListDTO } from "../../api/system/role.ts";
import { formatDateTime } from "../../utils/dateUtil.ts";

// 响应式数据
const loading = ref(false);
const tableData = ref<UserListDTO[]>([]);
const selectedUsers = ref<UserListDTO[]>([]);

// 搜索表单
const searchForm = reactive<UserSpecification>({
  username: "",
  nickname: "",
  email: "",
  phone: "",
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
const assignRoleDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const assignLoading = ref(false);
const dialogTitle = computed(() => (isEdit.value ? "编辑用户" : "新增用户"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<UserCreateDTO & Partial<UserUpdateDTO>>({
  username: "",
  enabled: true,
  email: "",
  phone: "",
});
const formRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "用户名长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  email: [{ type: "email", message: "请输入正确的邮箱格式", trigger: "blur" }],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号格式",
      trigger: "blur",
    },
  ],
};

// 查看详情数据
const viewData = ref<UserDetailDTO | null>(null);

// 分配角色数据
const assignUserData = ref<UserListDTO | null>(null);
const assignRoleIds = ref<number[]>([]);
const roleList = ref<RoleListDTO[]>([]);

const fetchUsers = async () => {
  try {
    loading.value = true;
    const response = await userApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.totalElements;
  } catch (error) {
    console.error("获取用户列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const fetchRoles = async () => {
  try {
    const response = await roleApi.findPage(1, 1000, {});
    roleList.value = response.data.content;
  } catch (error) {
    console.error("获取角色列表失败:", error);
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchUsers();
};

const handleReset = () => {
  Object.assign(searchForm, {
    username: "",
    nickname: "",
    email: "",
    phone: "",
    enabled: undefined,
  });
  handleSearch();
};

const handleSelectionChange = (selection: UserListDTO[]) => {
  selectedUsers.value = selection;
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchUsers();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchUsers();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    username: "",
    enabled: true,
    email: "",
    phone: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row: UserListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    enabled: row.enabled,
    email: row.email || "",
    phone: row.phone || "",
  });
  dialogVisible.value = true;
};

const handleView = async (row: UserListDTO) => {
  try {
    const response = await userApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取用户详情失败:", error);
  }
};

const handleAssignRole = async (row: UserListDTO) => {
  try {
    // 获取用户详细信息以获取当前角色
    const response = await userApi.findById(row.id);
    assignUserData.value = row;
    assignRoleIds.value = [...response.data.roleIds];
    assignRoleDialogVisible.value = true;
  } catch (error) {
    console.error("获取用户角色失败:", error);
  }
};

const handleDelete = async (row: UserListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await userApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchUsers();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除用户失败:", error);
    }
  }
};

const handleDialogClose = () => {
  dialogVisible.value = false;
  formRef.value?.clearValidate();
};

const handleAssignDialogClose = () => {
  assignRoleDialogVisible.value = false;
  assignUserData.value = null;
  assignRoleIds.value = [];
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    if (isEdit.value) {
      await userApi.update(formData as UserUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await userApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchUsers();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const handleAssignSubmit = async () => {
  if (!assignUserData.value) return;

  try {
    assignLoading.value = true;

    const updateData: UserRoleUpdateDTO = {
      id: assignUserData.value.id,
      roleIds: assignRoleIds.value,
    };

    await userApi.assignUserRole(updateData);
    ElMessage.success("分配角色成功");

    assignRoleDialogVisible.value = false;
    fetchUsers();
  } catch (error) {
    console.error("分配角色失败:", error);
    ElMessage.error("分配角色失败");
  } finally {
    assignLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchUsers();
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

.role-card {
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.role-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.role-card-selected {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.role-info {
  margin-left: 8px;
}

.role-name {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
  margin-bottom: 4px;
}

.role-code {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.role-description {
  font-size: 14px;
  color: #606266;
}
</style>
