<template>
  <div class="permission-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>MQTT用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增MQTT用户
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">

        <el-form-item label="是否超级用户">
          <el-input
            v-model="searchForm.isSuperuser"
            placeholder="请输入是否超级用户"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="searchForm.passwordHash"
            placeholder="请输入密码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="盐">
          <el-input
            v-model="searchForm.salt"
            placeholder="请输入盐"
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

        <el-table-column prop="isSuperuser" label="是否超级用户" min-width="120" show-overflow-tooltip />
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="passwordHash" label="密码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="salt" label="盐" min-width="120" show-overflow-tooltip />

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
        <el-form-item label="是否超级用户" prop="isSuperuser">
          <el-input v-model="formData.isSuperuser" placeholder="请输入是否超级用户" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="passwordHash">
          <el-input v-model="formData.passwordHash" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="盐" prop="salt">
          <el-input v-model="formData.salt" placeholder="请输入盐" />
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
          <el-descriptions-item label="是否超级用户">
            {{ viewData.isSuperuser }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ viewData.username }}
          </el-descriptions-item>
          <el-descriptions-item label="密码">
            {{ viewData.passwordHash }}
          </el-descriptions-item>
          <el-descriptions-item label="盐">
            {{ viewData.salt }}
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
  mqttuserApi,
  type MqttUserCreateDTO,
  type MqttUserDetailDTO,
  type MqttUserListDTO,
  type MqttUserSpecification,
  type MqttUserUpdateDTO,
} from "../../api/iot/mqttuser";

// 响应式数据
const loading = ref(false);
const tableData = ref<MqttUserListDTO[]>([]);

// 搜索表单
const searchForm = reactive<MqttUserSpecification>({
  isSuperuser:  true,
  username:  '',
  passwordHash:  '',
  salt:  '',
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
const dialogTitle = computed(() => (isEdit.value ? "编辑MQTT用户" : "新增MQTT用户"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<MqttUserCreateDTO & Partial<MqttUserUpdateDTO>>({
  isSuperuser: true,
  username: '',
  passwordHash: '',
  salt: '',
});
const formRules = {
  isSuperuser: [{ required: true, message: "请输入是否超级用户", trigger: "blur" }],
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  passwordHash: [{ required: true, message: "请输入密码", trigger: "blur" }],
  salt: [{ required: true, message: "请输入盐", trigger: "blur" }],
};

// 查看详情数据
const viewData = ref<MqttUserDetailDTO | null>(null);

const fetchMqttUser = async () => {
  try {
    loading.value = true;
    const response = await mqttuserApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.totalElements;
  } catch (error) {
    console.error("获取MqttUser列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchMqttUser();
};

const handleReset = () => {
  Object.assign(searchForm, {
    isSuperuser: "",
    username: "",
    passwordHash: "",
    salt: "",
  });
  handleSearch();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchMqttUser();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchMqttUser();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    isSuperuser: "",
    username: "",
    passwordHash: "",
    salt: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row: MqttUserListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    isSuperuser: row.isSuperuser,
    username: row.username,
    passwordHash: row.passwordHash,
    salt: row.salt,
  });
  dialogVisible.value = true;
};

const handleView = async (row: MqttUserListDTO) => {
  try {
    const response = await mqttuserApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取MqttUser详情失败:", error);
  }
};

const handleDelete = async (row: MqttUserListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除MQTT用户 "$\{row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await mqttuserApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchMqttUser();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除MqttUser失败:", error);
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
      await mqttuserApi.update(formData as MqttUserUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await mqttuserApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchMqttUser();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchMqttUser();
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