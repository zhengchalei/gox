<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>MQTT访问控制管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增MQTT访问控制
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">

        <el-form-item label="IP地址">
          <el-input
            v-model="searchForm.ipaddress"
            placeholder="请输入IP地址"
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
        <el-form-item label="客户端ID">
          <el-input
            v-model="searchForm.clientId"
            placeholder="请输入客户端ID"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作">
          <el-input
            v-model="searchForm.action"
            placeholder="请输入操作"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="权限">
          <el-input
            v-model="searchForm.permission"
            placeholder="请输入权限"
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

        <el-table-column prop="ipaddress" label="IP地址" min-width="120" show-overflow-tooltip />
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="clientId" label="客户端ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="action" label="操作" min-width="120" show-overflow-tooltip />
        <el-table-column prop="permission" label="权限" min-width="120" show-overflow-tooltip />

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
        <el-form-item label="IP地址" prop="ipaddress">
          <el-input v-model="formData.ipaddress" placeholder="请输入IP地址" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="客户端ID" prop="clientId">
          <el-input v-model="formData.clientId" placeholder="请输入客户端ID" />
        </el-form-item>
        <el-form-item label="操作" prop="action">
          <el-input v-model="formData.action" placeholder="请输入操作" />
        </el-form-item>
        <el-form-item label="权限" prop="permission">
          <el-input v-model="formData.permission" placeholder="请输入权限" />
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
          <el-descriptions-item label="IP地址">
            {{ viewData.ipaddress }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ viewData.username }}
          </el-descriptions-item>
          <el-descriptions-item label="客户端ID">
            {{ viewData.clientId }}
          </el-descriptions-item>
          <el-descriptions-item label="操作">
            {{ viewData.action }}
          </el-descriptions-item>
          <el-descriptions-item label="权限">
            {{ viewData.permission }}
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
import { formatDateTime } from "../../utils/dateUtil";
import {
  mqttAclApi,
  type MqttAclCreateDTO,
  type MqttAclDetailDTO,
  type MqttAclListDTO,
  type MqttAclSpecification,
  type MqttAclUpdateDTO,
} from "../../api/iot/mqttAcl";

// 响应式数据
const loading = ref(false);
const tableData = ref<MqttAclListDTO[]>([]);

// 搜索表单
const searchForm = reactive<MqttAclSpecification>({
  ipaddress:  '',
  username:  '',
  clientId:  '',
  action:  '',
  permission:  '',
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
const dialogTitle = computed(() => (isEdit.value ? "编辑MQTT访问控制" : "新增MQTT访问控制"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<MqttAclCreateDTO & Partial<MqttAclUpdateDTO>>({
  ipaddress: '',
  username: '',
  clientId: '',
  action: '',
  permission: '',
});
const formRules = {
  ipaddress: [{ required: true, message: "请输入IP地址", trigger: "blur" }],
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  clientId: [{ required: true, message: "请输入客户端ID", trigger: "blur" }],
  action: [{ required: true, message: "请输入操作", trigger: "blur" }],
  permission: [{ required: true, message: "请输入权限", trigger: "blur" }],
};

// 查看详情数据
const viewData = ref<MqttAclDetailDTO | null>(null);

const fetchMqttAcl = async () => {
  try {
    loading.value = true;
    const response = await mqttAclApi.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.totalElements;
  } catch (error) {
    console.error("获取MqttAcl列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchMqttAcl();
};

const handleReset = () => {
  Object.assign(searchForm, {
    ipaddress: "",
    username: "",
    clientId: "",
    action: "",
    permission: "",
  });
  handleSearch();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchMqttAcl();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetchMqttAcl();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    ipaddress: "",
    username: "",
    clientId: "",
    action: "",
    permission: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row: MqttAclListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    ipaddress: row.ipaddress,
    username: row.username,
    clientId: row.clientId,
    action: row.action,
    permission: row.permission,
  });
  dialogVisible.value = true;
};

const handleView = async (row: MqttAclListDTO) => {
  try {
    const response = await mqttAclApi.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取MqttAcl详情失败:", error);
  }
};

const handleDelete = async (row: MqttAclListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除MQTT访问控制 "$\{row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await mqttAclApi.deleteById(row.id);
    ElMessage.success("删除成功");
    fetchMqttAcl();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除MqttAcl失败:", error);
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
      await mqttAclApi.update(formData as MqttAclUpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await mqttAclApi.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetchMqttAcl();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchMqttAcl();
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