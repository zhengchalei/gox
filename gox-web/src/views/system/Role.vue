<template>
  <el-card>
    <avue-crud
      ref="crud"
      :data="tableData"
      :option="tableOption"
      :page="page"
      :search="searchSpecification"
      @row-save="handleSave"
      @row-update="handleUpdate"
      @row-del="handleDelete"
      @refresh-change="handleRefresh"
      @search-change="handleSearch"
      @search-reset="handleRefresh"
      @row-click="handleRowClick"
    >
      <template #menu="{ _size, row, _index }">
        <el-button
          text
          type="primary"
          :icon="Star"
          @click="openPermissionDialog(row)"
        >
          分配权限
        </el-button>
      </template>
    </avue-crud>
  </el-card>

  <assign-permission-dialog
    v-model:visible="permissionDialogVisible"
    :role-id="currentRoleId"
    @success="handlePermissionAssignSuccess"
  />
</template>

<script setup lang="ts">
import {
  Star
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, ref } from "vue";
import {
  roleApi,
  type RoleCreateDTO,
  type RoleListDTO,
  type RoleSpecification,
  type RoleUpdateDTO,
} from "../../api/system/role";
import AssignPermissionDialog from "./components/AssignPermissionDialog.vue";

const crud = ref(null);
// 响应式数据
const tableData = ref<RoleListDTO[]>([]);
const searchSpecification = ref<RoleSpecification>({});
const page = ref({
  total: 0,
  currentPage: 1,
  pageSize: 20,
});

const tableOption = ref({
  emptyBtn: true,
  viewBtn: true,
  border: true,
  index: true,
  tree: true,
  dialogDrag: true,
  headerAlign: "center",
  align: "center",
  labelWidth: 100,
  menuWidth: 340,
  addBtnText: "新增",
  title: "角色管理",
  column: [
    {
      label: "角色名称",
      prop: "name",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入角色名称",
          trigger: "blur",
        },
      ],
    },
    {
      label: "角色编码",
      prop: "code",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入角色编码",
          trigger: "blur",
        },
      ],
    },
    {
      label: "描述",
      prop: "description",
      type: "textarea",
      span: 24,
    },
  ],
});

const handleSearch = async (
  form: RoleSpecification,
  done: (form: RoleSpecification) => void
) => {
  const { data } = await roleApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await roleApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: RoleCreateDTO,
  done: (form: RoleCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await roleApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: RoleUpdateDTO,
  _index: number,
  done: (row: RoleUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await roleApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: RoleListDTO,
  _index: number,
  done: (row: RoleListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该角色, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await roleApi.deleteById(row.id);
      success ? ElMessage.success("删除成功") : ElMessage.error("删除失败");
      done(row);
    })
    .catch(() => {});
};

// 分配权限相关状态和方法
const permissionDialogVisible = ref(false);
const currentRoleId = ref<number | undefined>(undefined);

// 打开权限分配对话框
const openPermissionDialog = (row: RoleListDTO) => {
  currentRoleId.value = row.id;
  permissionDialogVisible.value = true;
};

// 权限分配成功后的回调
const handlePermissionAssignSuccess = () => {
  handleRefresh();
};

const handleRowClick = (row: RoleListDTO) => {
  // 可以在这里处理行点击事件
};

// 生命周期
onMounted(async () => {
  await handleRefresh();
});
</script>

<style scoped></style>
