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
import {
  permissionApi,
  type PermissionCreateDTO,
  type PermissionListDTO,
  type PermissionSpecification,
  type PermissionUpdateDTO,
} from "../../api/system/permission";

const crud = ref(null);
// 响应式数据
const tableData = ref<PermissionListDTO[]>([]);
const searchSpecification = ref<PermissionSpecification>({});
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
  addBtnText: "新增",
  title: "权限管理",
  column: [
    {
      label: "权限名称",
      prop: "name",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入权限名称",
          trigger: "blur",
        },
      ],
    },
    {
      label: "权限编码",
      prop: "code",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入权限编码",
          trigger: "blur",
        },
      ],
    },
    {
      label: "权限类型",
      prop: "type",
      type: "select",
      search: true,
      dicData: [
        { label: "菜单", value: "MENU" },
        { label: "按钮", value: "BUTTON" },
        { label: "API", value: "API" },
      ],
      rules: [
        {
          required: true,
          message: "请选择权限类型",
          trigger: "change",
        },
      ],
    },
    {
      label: "父级权限",
      prop: "parentId",
      type: "tree",
      props: {
        label: "name",
        value: "id",
      },
      dicData: [],
      dicMethod: "get",
      dicUrl: "/api/system/permission/tree",
    },
    {
      label: "路由路径",
      prop: "path",
      hide: true,
      formslot: true,
      rules: [
        {
          required: false,
          message: "请输入路由路径",
          trigger: "blur",
        },
      ],
    },
    {
      label: "组件路径",
      prop: "component",
      hide: true,
      formslot: true,
    },
    {
      label: "图标",
      prop: "icon",
      hide: true,
      formslot: true,
    },
    {
      label: "排序",
      prop: "sort",
      type: "number",
      hide: true,
      rules: [
        {
          required: true,
          message: "请输入排序值",
          trigger: "blur",
        },
      ],
    },
    {
      label: "描述",
      prop: "description",
      type: "textarea",
    },
    {
      label: "创建时间",
      prop: "createdTime",
      type: "datetime",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      addDisplay: false,
      editDisplay: false,
    },
    {
      label: "更新时间",
      prop: "updatedTime",
      type: "datetime",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      addDisplay: false,
      editDisplay: false,
    },
  ],
});

const handleSearch = async (
  form: PermissionSpecification,
  done: (form: PermissionSpecification) => void
) => {
  const { data } = await permissionApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await permissionApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: PermissionCreateDTO,
  done: (form: PermissionCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await permissionApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: PermissionUpdateDTO,
  _index: number,
  done: (row: PermissionUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await permissionApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: PermissionListDTO,
  _index: number,
  done: (row: PermissionListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该权限, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await permissionApi.deleteById(row.id);
      success ? ElMessage.success("删除成功") : ElMessage.error("删除失败");
      done(row);
    })
    .catch(() => {});
};

const handleRowClick = (row: PermissionListDTO) => {
  // 可以在这里处理行点击事件
};

// 生命周期
onMounted(async () => {
  await handleRefresh();
});
</script>

<style scoped></style>
