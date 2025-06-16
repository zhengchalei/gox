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
    ></avue-crud>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, ref } from "vue";
import { roleApi, type RoleListDTO } from "../../api/system/role";
import {
  userApi,
  type UserCreateDTO,
  type UserListDTO,
  type UserSpecification,
  type UserUpdateDTO,
} from "../../api/system/user";

const crud = ref(null);
// 响应式数据
const tableData = ref<UserListDTO[]>([]);
const searchSpecification = ref<UserSpecification>({});
const page = ref({
  total: 0,
  currentPage: 1,
  pageSize: 20,
});

// 角色相关
const roleOptions = ref<RoleListDTO[]>([]);

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
  addBtnText: "新增",
  title: "用户管理",
  column: [
    {
      label: "用户名",
      prop: "username",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入用户名",
          trigger: "blur",
        },
      ],
    },
    {
      label: "昵称",
      prop: "nickname",
      search: true,
    },
    {
      label: "邮箱",
      prop: "email",
      search: true,
      rules: [
        {
          type: "email",
          message: "请输入正确的邮箱格式",
          trigger: "blur",
        }
      ]
    },
    {
      label: "手机号",
      prop: "mobile",
      search: true,
    },
    {
      label: "状态",
      prop: "enabled",
      type: "radio",
      search: true,
      dicData: [
        {
          label: "启用",
          value: true,
        },
        {
          label: "禁用",
          value: false,
        },
      ],
      rules: [
        {
          required: true,
          message: "请选择状态",
          trigger: "blur",
        }
      ]
    },
  ],
});

const handleSearch = async (
  form: UserSpecification,
  done: (form: UserSpecification) => void
) => {
  const { data } = await userApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await userApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: UserCreateDTO,
  done: (form: UserCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await userApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: UserUpdateDTO,
  _index: number,
  done: (row: UserUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await userApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: UserListDTO,
  _index: number,
  done: (row: UserListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该用户, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await userApi.deleteById(row.id);
      success ? ElMessage.success("删除成功") : ElMessage.error("删除失败");
      done(row);
    })
    .catch(() => {});
};

const handleRowClick = (row: UserListDTO) => {
  // 可以在这里处理行点击事件
};

// 角色相关方法
const fetchRoles = async () => {
  try {
    const { data } = await roleApi.findPage(1, 100, {});
    roleOptions.value = data.content;
  } catch (error) {
    console.error("获取角色列表失败:", error);
  }
};

// 生命周期
onMounted(async () => {
  await handleRefresh();
  await fetchRoles();
});
</script>

<style scoped></style>
