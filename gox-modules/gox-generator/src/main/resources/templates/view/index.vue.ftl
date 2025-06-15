<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>${entityComment}管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增${entityComment}
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="searchForm" :inline="true">

        <#list fields as field>
        <el-form-item label="${field.comment}">
          <el-input
            v-model="searchForm.${field.name}"
            placeholder="请输入${field.comment}"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        </#list>
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

        <#list fields as field>
        <el-table-column prop="${field.name}" label="${field.comment}" min-width="120" show-overflow-tooltip />
        </#list>

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
        <#list fields as field>
        <el-form-item label="${field.comment}" prop="${field.name}">
          <el-input v-model="formData.${field.name}" placeholder="请输入${field.comment}" />
        </el-form-item>
        </#list>
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
          <#list fields as field>
          <el-descriptions-item label="${field.comment}">
            {{ viewData.${field.name} }}
          </el-descriptions-item>
          </#list>
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
  ${entityName?uncap_first}Api,
  type ${entityName}CreateDTO,
  type ${entityName}DetailDTO,
  type ${entityName}ListDTO,
  type ${entityName}Specification,
  type ${entityName}UpdateDTO,
} from "../../api/${moduleName}/${entityName?uncap_first}";

// 响应式数据
const loading = ref(false);
const tableData = ref<${entityName}ListDTO[]>([]);

// 搜索表单
const searchForm = reactive<${entityName}Specification>({
  <#list fields as field>
  ${field.name}: <#if field.type=="String"> ''</#if><#if field.type=="Boolean"> true</#if>,
  </#list>
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
const dialogTitle = computed(() => (isEdit.value ? "编辑${entityComment}" : "新增${entityComment}"));

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<${entityName}CreateDTO & Partial<${entityName}UpdateDTO>>({
  <#list fields as field>
  ${field.name}:<#if field.type=="String"> ''</#if><#if field.type=="Boolean"> true</#if>,
  </#list>
});
const formRules = {
  <#list fields as field>
    <#if field.nullable>
  ${field.name}: [{ required: true, message: "请输入${field.comment}", trigger: "blur" }],
  <#else>
  ${field.name}: [{ required: true, message: "请输入${field.comment}", trigger: "blur" }],
  </#if>
  </#list>
};

// 查看详情数据
const viewData = ref<${entityName}DetailDTO | null>(null);

const fetch${entityName} = async () => {
  try {
    loading.value = true;
    const response = await ${entityName?uncap_first}Api.findPage(
      pagination.currentPage,
      pagination.pageSize,
      searchForm
    );

    tableData.value = response.data.content;
    pagination.total = response.data.totalElements;
  } catch (error) {
    console.error("获取${entityName}列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetch${entityName}();
};

const handleReset = () => {
  Object.assign(searchForm, {
    <#list fields as field>
    ${field.name}: "",
    </#list>
  });
  handleSearch();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetch${entityName}();
};

const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  fetch${entityName}();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    <#list fields as field>
    ${field.name}: "",
    </#list>
  });
  dialogVisible.value = true;
};

const handleEdit = (row: ${entityName}ListDTO) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    <#list fields as field>
    ${field.name}: row.${field.name},
    </#list>
  });
  dialogVisible.value = true;
};

const handleView = async (row: ${entityName}ListDTO) => {
  try {
    const response = await ${entityName?uncap_first}Api.findById(row.id);
    viewData.value = response.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error("获取${entityName}详情失败:", error);
  }
};

const handleDelete = async (row: ${entityName}ListDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除${entityComment} "$\{row.name}" 吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await ${entityName?uncap_first}Api.deleteById(row.id);
    ElMessage.success("删除成功");
    fetch${entityName}();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除${entityName}失败:", error);
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
      await ${entityName?uncap_first}Api.update(formData as ${entityName}UpdateDTO);
      ElMessage.success("更新成功");
    } else {
      await ${entityName?uncap_first}Api.create(formData);
      ElMessage.success("创建成功");
    }

    dialogVisible.value = false;
    fetch${entityName}();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetch${entityName}();
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